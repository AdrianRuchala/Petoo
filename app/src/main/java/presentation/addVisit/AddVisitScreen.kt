package presentation.addVisit

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.TextUtils.isEmpty
import android.widget.Toast
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TimeInput
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import java.util.Calendar
import android.provider.Settings
import presentation.viewmodels.MainScreenViewModel
import com.droidcode.apps.petoo.R
import com.droidcode.apps.petoo.domain.models.VaccinationsData
import com.droidcode.apps.petoo.notification.Notification
import com.droidcode.apps.petoo.notification.notificationId
import com.google.firebase.auth.ktx.auth
import java.text.SimpleDateFormat
import java.util.Locale

private lateinit var database: DatabaseReference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddVisitScreen(
    modifier: Modifier,
    viewModel: MainScreenViewModel,
    onNavigateUp: () -> Unit
) {
    var petName by remember { mutableStateOf("") }
    var petDateOfBirth by remember { mutableStateOf("") }
    var petImage by remember { mutableStateOf("") }
    var petGender by remember { mutableStateOf("") }
    var petSpecies by remember { mutableStateOf("") }
    var petBreed by remember { mutableStateOf("") }
    var petVaccinations by remember { mutableStateOf<List<VaccinationsData>>(emptyList()) }
    var petId by remember { mutableStateOf("") }
    var vetAddress by remember { mutableStateOf("") }
    var vetName by remember { mutableStateOf("") }
    var visitDate by remember { mutableStateOf("") }
    val openSelectPetDialog = remember { mutableStateOf(false) }
    val openSelectVetDialog = remember { mutableStateOf(false) }
    val openClockDialog = remember { mutableStateOf(false) }
    var visitTime by remember { mutableStateOf("") }
    var visitDateTime by remember { mutableStateOf("") }
    var vetId by remember { mutableStateOf("") }
    val context = LocalContext.current
    val ownerName = Firebase.auth.currentUser?.displayName.toString()

    viewModel.readVaccinationList(petId)
    petVaccinations = viewModel.vaccinationViewState.value

    val calendarState = rememberUseCaseState()
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            visitDate = date.toString()
            openClockDialog.value = true
        })

    if (openSelectPetDialog.value) {
        SelectPet(
            modifier,
            openSelectPetDialog,
            viewModel
        ) { selectedPet, selectedPetId, selectedPetImage ->
            petName = selectedPet
            petId = selectedPetId
            petImage = selectedPetImage
        }
    }

    if (openSelectVetDialog.value) {
        viewModel.readVets()
        SelectVet(modifier, openSelectVetDialog, viewModel) { selectedVet, selectedVetId ->
            vetName = selectedVet
            vetId = selectedVetId
        }
    }

    if (openClockDialog.value) {
        ClockDialog(modifier, openClockDialog) { pickedTime ->
            visitTime = pickedTime
            visitDateTime = "$visitDate, $visitTime"
        }
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        Row(
            modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(100.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onNavigateUp() }
            )

            Text(
                stringResource(R.string.add_visit),
                modifier.padding(8.dp),
                style = MaterialTheme.typography.titleMedium
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))

        Column(modifier.padding(all = 8.dp)) {
            TextField(
                value = petName,
                onValueChange = { petName = it },
                modifier
                    .fillMaxWidth()
                    .clickable { openSelectPetDialog.value = true },
                label = { Text(stringResource(id = R.string.select_pet)) },
                isError = isEmpty(petName),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledLabelColor = if (petName.isEmpty()) TextFieldDefaults.colors().errorLabelColor else TextFieldDefaults.colors().unfocusedLabelColor,
                    disabledTextColor = if (petName.isEmpty()) TextFieldDefaults.colors().errorTextColor else TextFieldDefaults.colors().unfocusedTextColor,
                    disabledIndicatorColor = if (petName.isEmpty()) TextFieldDefaults.colors().errorIndicatorColor else TextFieldDefaults.colors().unfocusedIndicatorColor,
                )
            )
            Spacer(modifier.padding(all = 8.dp))

            TextField(
                value = vetName,
                onValueChange = { vetName = it },
                modifier
                    .fillMaxWidth()
                    .clickable { openSelectVetDialog.value = true },
                label = { Text(stringResource(id = R.string.select_vet)) },
                isError = isEmpty(vetName),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledLabelColor = if (vetName.isEmpty()) TextFieldDefaults.colors().errorLabelColor else TextFieldDefaults.colors().unfocusedLabelColor,
                    disabledTextColor = if (vetName.isEmpty()) TextFieldDefaults.colors().errorTextColor else TextFieldDefaults.colors().unfocusedTextColor,
                    disabledIndicatorColor = if (vetName.isEmpty()) TextFieldDefaults.colors().errorIndicatorColor else TextFieldDefaults.colors().unfocusedIndicatorColor,
                )
            )
            Spacer(modifier.padding(all = 8.dp))

            TextField(
                value = vetAddress,
                onValueChange = { vetAddress = it },
                modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.type_vet_address)) },
                isError = isEmpty(vetAddress),
            )
            Spacer(modifier.padding(all = 8.dp))

            TextField(
                value = visitDateTime,
                onValueChange = { visitDateTime = it },
                modifier
                    .fillMaxWidth()
                    .clickable { calendarState.show() },
                label = { Text(stringResource(id = R.string.select_visit_date)) },
                isError = isEmpty(visitDateTime),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledLabelColor = if (visitDateTime.isEmpty()) TextFieldDefaults.colors().errorLabelColor else TextFieldDefaults.colors().unfocusedLabelColor,
                    disabledTextColor = if (visitDateTime.isEmpty()) TextFieldDefaults.colors().errorTextColor else TextFieldDefaults.colors().unfocusedTextColor,
                    disabledIndicatorColor = if (visitDateTime.isEmpty()) TextFieldDefaults.colors().errorIndicatorColor else TextFieldDefaults.colors().unfocusedIndicatorColor,
                )
            )
            Spacer(modifier.padding(all = 8.dp))
        }

        Button(
            onClick = {
                if (!isEmpty(petName) && !isEmpty(vetAddress) && !isEmpty(visitDateTime)) {
                    database = Firebase.database.reference
                    val visitId = database.push().key!!
                    viewModel.checkIfVisitExists(petId, vetId, visitDate, visitTime) { isExisting ->
                        if (isExisting) {
                            onNavigateUp()
                        } else {
                            viewModel.addVisit(
                                visitId,
                                petId,
                                petName,
                                petImage,
                                vetId,
                                vetName,
                                vetAddress,
                                visitDate,
                                visitTime
                            )

                            val petData =
                                viewModel.mainScreenViewState.value.find { it.petId == petId }
                            petData?.let {
                                petName = petData.petName
                                petDateOfBirth = petData.petDateOfBirth
                                petImage = petData.petImage
                                petGender = petData.petGender
                                petSpecies = petData.petSpecies
                                petBreed = petData.petBreed
                            }

                            if (vetId.isNotEmpty()) {
                                viewModel.addPetForVet(
                                    vetId,
                                    petId,
                                    petName,
                                    petDateOfBirth,
                                    petBreed,
                                    petImage,
                                    petGender,
                                    petSpecies,
                                    petVaccinations,
                                    visitDate,
                                    ownerName
                                )
                            }
                        }
                        checkAlarmPermission(context, visitDate, visitTime)
                        onNavigateUp()
                    }
                }
            },
            modifier.align(Alignment.CenterHorizontally)
        )
        {
            Text(stringResource(R.string.save))
        }
    }
}

@Composable
private fun SelectPet(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    viewModel: MainScreenViewModel,
    onDismiss: (String, String, String) -> Unit
) {
    var petName by remember { mutableStateOf("") }
    val pets = viewModel.mainScreenViewState.value

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.select_pet)) },
            text = {
                Column {
                    pets.forEach { pet ->
                        Text(
                            text = pet.petName,
                            modifier = modifier
                                .fillMaxWidth()
                                .clickable {
                                    petName = pet.petName
                                    onDismiss(petName, pet.petId, pet.petImage)
                                    showDialog.value = false
                                }
                                .padding(all = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.dialog_negative))
                }
            }
        )
    }
}

@Composable
fun SelectVet(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    viewModel: MainScreenViewModel,
    onDismiss: (String, String) -> Unit
) {
    var vetName by remember { mutableStateOf("") }
    var vetId by remember { mutableStateOf("") }
    val vets = viewModel.vetsViewState.value

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.select_vet)) },
            text = {
                Column {
                    vets.forEach { vet ->
                        Column(
                            modifier
                                .fillMaxWidth()
                                .clickable {
                                    vetName = vet.name
                                    vetId = vet.vetId
                                    onDismiss(vetName, vetId)
                                    showDialog.value = false
                                }
                                .padding(all = 8.dp)
                        ) {
                            Text(
                                text = vet.name,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = vet.email
                            )
                        }
                    }

                    TextField(
                        value = vetName,
                        onValueChange = { vetName = it },
                        modifier
                            .fillMaxWidth(),
                        label = { Text(stringResource(R.string.enter_vet)) },
                        placeholder = { Text(stringResource(R.string.enter_vet)) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (vetName.isNotEmpty()) {
                            onDismiss(vetName, vetId)
                            showDialog.value = false
                        }
                    }
                ) {
                    Text(stringResource(R.string.dialog_positive))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.dialog_negative))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ClockDialog(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    onDismiss: (String) -> Unit
) {
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(stringResource(R.string.visit_time)) },
        text = {
            Column(modifier.fillMaxWidth()) {
                TimeInput(
                    state = timePickerState,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val selectedTime = "${
                        timePickerState.hour.toString().padStart(2, '0')
                    }:${timePickerState.minute.toString().padStart(2, '0')}"
                    onDismiss(selectedTime)
                    showDialog.value = false
                }
            ) {
                Text(stringResource(R.string.dialog_positive))
            }
        },
        dismissButton = {
            Button(onClick = { showDialog.value = false }) {
                Text(stringResource(R.string.dialog_negative))
            }
        }
    )
}

@SuppressLint("ScheduleExactAlarm")
private fun scheduleNotification(context: Context, visitDate: String, visitTime: String) {
    val intent = Intent(context, Notification::class.java)

    val pendingIntent = PendingIntent.getBroadcast(
        context,
        notificationId,
        intent,
        PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
    )

    val visitDateTime = "$visitDate $visitTime"
    val calendar = Calendar.getInstance()
    val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    try {
        calendar.time = dateFormat.parse(visitDateTime) ?: throw IllegalArgumentException()
    } catch (e: Exception) {
        e.printStackTrace()
        return
    }

    calendar.add(Calendar.HOUR, -48)

    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    alarmManager.setExactAndAllowWhileIdle(
        AlarmManager.RTC_WAKEUP,
        calendar.timeInMillis,
        pendingIntent
    )
}

private fun checkAlarmPermission(context: Context, visitDate: String, visitTime: String) {
    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (!alarmManager.canScheduleExactAlarms()) {
            Toast.makeText(
                context,
                context.getString(R.string.ask_for_permission),
                Toast.LENGTH_LONG
            )
                .show()
            val intent = Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)
            context.startActivity(intent)
        } else {
            scheduleNotification(context, visitDate, visitTime)
        }
    }
}
