package presentation.petDetails

import android.net.Uri
import android.text.TextUtils.isEmpty
import androidx.activity.compose.ManagedActivityResultLauncher
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import presentation.viewmodels.MainScreenViewModel
import com.droidcode.apps.petoo.domain.models.MainScreenViewStateData
import com.droidcode.apps.petoo.R
import com.droidcode.apps.petoo.domain.models.VaccinationsData
import presentation.addPet.PetGenderAlertDialog
import com.maxkeppeker.sheets.core.models.base.UseCaseState
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PetDetailsScreen(
    modifier: Modifier,
    petId: String,
    viewModel: MainScreenViewModel,
    onNavigateUp: () -> Unit
) {
    var petName by remember { mutableStateOf("") }
    var petDateOfBirth by remember { mutableStateOf("") }
    var petImage by remember { mutableStateOf("") }
    var petSpecies by remember { mutableStateOf("") }
    var petBreed by remember { mutableStateOf("") }
    var petVaccinations by remember { mutableStateOf<List<VaccinationsData>>(emptyList()) }
    val openIllnessDialog = remember { mutableStateOf(false) }
    val openVaccineDialog = remember { mutableStateOf(false) }
    val openRemoveVaccineDialog = remember { mutableStateOf(false) }
    var illnessName by remember { mutableStateOf("") }
    var vaccines by remember { mutableStateOf(emptyArray<String>()) }
    val showPetGenderAlertDialog = remember { mutableStateOf(false) }

    viewModel.readVaccinationList(petId)
    petVaccinations = viewModel.vaccinationViewState.value
    val petData = viewModel.mainScreenViewState.value.find { it.petId == petId }
    petData?.let {
        petName = petData.petName
        petDateOfBirth = petData.petDateOfBirth
        petImage = petData.petImage
        petSpecies = petData.petSpecies
        petBreed = petData.petBreed
    }

    var petGender by remember { mutableStateOf(petData?.petGender ?: "") }

    var vaccinationDate by remember { mutableStateOf("") }
    val calendarState = rememberUseCaseState()
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            vaccinationDate = date.toString()
            openIllnessDialog.value = true
        })

    if (openIllnessDialog.value) {
        SelectIllness(modifier, openIllnessDialog) { selectedIllness, selectedVaccines ->
            illnessName = selectedIllness
            vaccines = selectedVaccines
            openVaccineDialog.value = illnessName.isNotEmpty()
            openIllnessDialog.value = false
        }
    }

    if (openVaccineDialog.value) {
        SelectVaccine(
            modifier,
            openVaccineDialog,
            petId,
            illnessName,
            vaccines,
            vaccinationDate,
            viewModel
        )
    }

    if (openRemoveVaccineDialog.value) {
        RemoveVaccine(openRemoveVaccineDialog, viewModel, petId, illnessName)
    }

    if (showPetGenderAlertDialog.value) {
        PetGenderAlertDialog(modifier, showPetGenderAlertDialog) { selectedGender ->
            petGender = selectedGender
        }
    }

    var selectedImage by remember { mutableStateOf<Uri?>(null) }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = { uri -> selectedImage = uri }
    )

    Column(
        modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {

        TopBarAndImage(
            modifier.align(Alignment.CenterHorizontally),
            petName,
            petId,
            selectedImage,
            petData,
            viewModel,
            photoPickerLauncher,
            petVaccinations
        ) { onNavigateUp() }
        LazyColumn(modifier.padding(all = 8.dp)) {
            item {
                TextField(
                    value = petName,
                    onValueChange = { petName = it },
                    modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.name)) },
                    isError = petName.isEmpty(),
                    enabled = false
                )
            }

            item {
                Spacer(modifier.padding(all = 8.dp))
            }

            item {
                TextField(
                    value = petDateOfBirth,
                    onValueChange = { petDateOfBirth = it },
                    modifier
                        .fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.date_of_birth)) },
                    isError = petDateOfBirth.isEmpty(),
                    enabled = false
                )
            }

            item {
                Spacer(modifier.padding(all = 8.dp))
            }

            item {
                TextField(
                    value = petGender,
                    onValueChange = { petGender = it },
                    modifier
                        .fillMaxWidth()
                        .clickable { showPetGenderAlertDialog.value = true },
                    label = { Text(stringResource(id = R.string.gender)) },
                    isError = isEmpty(petGender),
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        disabledLabelColor = TextFieldDefaults.colors().unfocusedLabelColor,
                        disabledTextColor = TextFieldDefaults.colors().unfocusedTextColor,
                        disabledIndicatorColor = TextFieldDefaults.colors().unfocusedIndicatorColor,
                    )
                )
            }

            item {
                Spacer(modifier.padding(all = 8.dp))
            }

            item {
                TextField(
                    value = petSpecies,
                    onValueChange = { petSpecies = it },
                    modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.pet_species)) },
                    isError = isEmpty(petSpecies)
                )
            }

            item {
                Spacer(modifier.padding(all = 8.dp))
            }

            item {
                TextField(
                    value = petBreed,
                    onValueChange = { petBreed = it },
                    modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.breed)) },
                    isError = petBreed.isEmpty(),
                )
            }

            items(petVaccinations) { vaccination ->
                Spacer(modifier.padding(all = 8.dp))
                TextField(
                    value = vaccination.vaccinationDate + " | " + vaccination.vaccinationName,
                    onValueChange = { vaccination.vaccinationName = it },
                    modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { },
                            onLongClick = {
                                illnessName = vaccination.illnessName
                                openRemoveVaccineDialog.value = true
                            }
                        ),
                    label = { Text(stringResource(R.string.vaccination) + " " + vaccination.illnessName) },
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        disabledLabelColor = TextFieldDefaults.colors().unfocusedLabelColor,
                        disabledTextColor = TextFieldDefaults.colors().unfocusedTextColor,
                        disabledIndicatorColor = TextFieldDefaults.colors().unfocusedIndicatorColor,
                    )
                )
            }

            item {
                Buttons(
                    modifier,
                    calendarState,
                    petName,
                    petDateOfBirth,
                    petBreed,
                    petGender,
                    petSpecies,
                    selectedImage,
                    petImage,
                    petId,
                    viewModel
                ) { onNavigateUp() }
            }
        }
    }
}

@Composable
fun SelectIllness(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    onDismiss: (String, Array<String>) -> Unit
) {
    val illnesses = arrayOf(
        stringResource(R.string.distemper),
        stringResource(R.string.parvovirus),
        stringResource(R.string.rubarthsDisease),
        stringResource(R.string.rabies),
        stringResource(R.string.kennelCough),
        stringResource(R.string.parainfluenza),
        stringResource(R.string.leptospirosis),
        stringResource(R.string.lyme)
    )
    var vaccines by remember { mutableStateOf(emptyArray<String>()) }
    var illnessText by remember { mutableStateOf("") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.illness)) },
            text = {
                Column {
                    illnesses.forEach { illness ->
                        Text(
                            text = illness,
                            modifier = modifier
                                .fillMaxWidth()
                                .clickable {
                                    when (illness) {
                                        illnesses[0], illnesses[1], illnesses[5] -> {
                                            vaccines = arrayOf(
                                                "Nobivac DHPPi",
                                                "Duramune DAPPI",
                                                "Vanguard Plus 5"
                                            )
                                        }

                                        illnesses[2] -> {
                                            vaccines = arrayOf("Nobivac DHP", "Vanguard Plus 5/CV")
                                        }

                                        illnesses[3] -> {
                                            vaccines =
                                                arrayOf("Nobivac Rabies", "Rabisin", "Defensor 3")
                                        }

                                        illnesses[4] -> {
                                            vaccines = arrayOf(
                                                "Nobivac KC",
                                                "Bronchicine CAe",
                                                "Intra-Trac 3"
                                            )
                                        }

                                        illnesses[6] -> {
                                            vaccines =
                                                arrayOf("Nobivac L4", "Duramune L4", "Vanguard L4")
                                        }

                                        illnesses[7] -> {
                                            vaccines = arrayOf(
                                                "Nobivac Lyme",
                                                "Duramune Lyme",
                                                "Vanguard crLyme"
                                            )
                                        }
                                    }
                                    onDismiss(illness, vaccines)
                                    showDialog.value = false
                                }
                                .padding(all = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }
                    TextField(
                        value = illnessText,
                        onValueChange = { illnessText = it },
                        modifier
                            .fillMaxWidth(),
                        label = { Text(stringResource(R.string.enter_illness)) },
                        placeholder = { Text(stringResource(R.string.enter_illness)) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (illnessText.isNotEmpty()) {
                            onDismiss(illnessText, vaccines)
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

@Composable
fun SelectVaccine(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    petID: String,
    illnessName: String,
    vaccines: Array<String>,
    vaccinationDate: String,
    viewModel: MainScreenViewModel
) {
    var vaccineText by remember { mutableStateOf("") }

    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.vaccine)) },
            text = {
                Column {
                    vaccines.forEach { vaccine ->
                        Text(
                            text = vaccine,
                            modifier = modifier
                                .fillMaxWidth()
                                .clickable {
                                    vaccineText = vaccine
                                    viewModel.addVaccineToDatabase(
                                        petID,
                                        illnessName,
                                        vaccineText,
                                        vaccinationDate
                                    )
                                    showDialog.value = false
                                }
                                .padding(all = 8.dp),
                            style = MaterialTheme.typography.titleMedium
                        )
                    }

                    Spacer(modifier = Modifier.padding(8.dp))

                    TextField(
                        value = vaccineText,
                        onValueChange = { vaccineText = it },
                        modifier
                            .fillMaxWidth(),
                        label = { Text(stringResource(R.string.enter_vaccine)) },
                        placeholder = { Text(stringResource(R.string.enter_vaccine)) }
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (vaccineText.isNotEmpty()) {
                            viewModel.addVaccineToDatabase(
                                petID,
                                illnessName,
                                vaccineText,
                                vaccinationDate
                            )
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

@Composable
fun RemoveVaccine(
    showDialog: MutableState<Boolean>,
    viewModel: MainScreenViewModel,
    petId: String,
    illnessName: String
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.dialog_title)) },
            text = { Text(stringResource(R.string.dialog_delete_vaccination_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeVaccination(petId, illnessName)
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.dialog_positive))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text(stringResource(R.string.dialog_negative))
                }
            }
        )
    }
}

@Composable
fun TopBarAndImage(
    modifier: Modifier,
    petName: String,
    petId: String,
    selectedImage: Uri?,
    petData: MainScreenViewStateData?,
    viewModel: MainScreenViewModel,
    photoPickerLauncher: ManagedActivityResultLauncher<PickVisualMediaRequest, Uri?>,
    vaccinations: List<VaccinationsData>,
    onNavigateUp: () -> Unit
) {
    val openAddPetToArchiveDialog = remember { mutableStateOf(false) }
    val openRemovePetDialog = remember { mutableStateOf(false) }

    if (openAddPetToArchiveDialog.value) {
        AddPetToArchive(
            openAddPetToArchiveDialog,
            viewModel,
            petId,
            petName,
            petData,
            vaccinations
        ) { onNavigateUp() }
    }

    if (openRemovePetDialog.value) {
        RemovePet(openRemovePetDialog, viewModel, petId) { onNavigateUp() }
    }

    Row(
        modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                contentDescription = null,
                modifier = Modifier
                    .size(36.dp)
                    .clickable { onNavigateUp() }
            )

            Spacer(modifier.size(36.dp))
        }


        Text(
            text = petName,
            modifier = Modifier
                .padding(all = 8.dp),
            style = MaterialTheme.typography.titleMedium,
        )

        Row {
            Image(
                if (isSystemInDarkTheme()) {
                    painterResource(R.drawable.archive_white)
                } else {
                    painterResource(id = R.drawable.archive)
                },
                contentDescription = null,
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { openAddPetToArchiveDialog.value = true }
            )

            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = Modifier.clickable { openRemovePetDialog.value = true }
            )
        }
    }
    HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))

    GlideImage(
        imageModel = { selectedImage ?: petData?.petImage },
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape)
            .clickable {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly
                    )
                )
            },
        component = rememberImageComponent {
            +PlaceholderPlugin.Failure(
                if (isSystemInDarkTheme()) {
                    painterResource(R.drawable.image_search_white)
                } else {
                    painterResource(id = R.drawable.image_search)
                }
            )
        }
    )
    HorizontalDivider(modifier = Modifier.padding(all = 8.dp))
}

@Composable
fun Buttons(
    modifier: Modifier,
    calendarState: UseCaseState,
    petName: String,
    petDateOfBirth: String,
    petBreed: String,
    petGender: String,
    petSpecies: String,
    selectedImage: Uri?,
    petImage: String,
    petId: String,
    viewModel: MainScreenViewModel,
    onNavigateUp: () -> Unit
) {
    Row(
        modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Button(
            onClick = { calendarState.show() },
            modifier
                .padding(top = 16.dp)
        ) {
            Text(stringResource(id = R.string.addVaccination))
        }

        Button(
            onClick = {
                if (petName.isNotEmpty() && petDateOfBirth.isNotEmpty() && petBreed.isNotEmpty()
                    && petGender.isNotEmpty() && petSpecies.isNotEmpty()) {
                    val selectedImageString = selectedImage?.toString() ?: petImage

                    viewModel.updatePetData(
                        petId,
                        selectedImageString,
                        petBreed,
                        petGender,
                        petSpecies
                    )
                    onNavigateUp()
                }
            },
            modifier
                .padding(top = 16.dp)
        ) {
            Text(stringResource(id = R.string.save))
        }
    }
}

@Composable
fun AddPetToArchive(
    showDialog: MutableState<Boolean>,
    viewModel: MainScreenViewModel,
    petId: String,
    petName: String,
    petData: MainScreenViewStateData?,
    vaccinations: List<VaccinationsData>,
    onNavigateUp: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.dialog_archive_title)) },
            text = { Text(stringResource(R.string.dialog_archive_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addPetToArchive(
                            petId,
                            petName,
                            petData!!.petDateOfBirth,
                            petData.petBreed,
                            petData.petImage,
                            petData.petGender,
                            petData.petSpecies,
                            vaccinations
                        ) { onNavigateUp() }
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.dialog_positive))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text(stringResource(R.string.dialog_negative))
                }
            }
        )
    }
}

@Composable
fun RemovePet(
    showDialog: MutableState<Boolean>,
    viewModel: MainScreenViewModel,
    petId: String,
    onNavigateUp: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.dialog_title)) },
            text = { Text(stringResource(R.string.dialog_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removePet(petId) { onNavigateUp() }
                        showDialog.value = false
                    }
                ) {
                    Text(stringResource(R.string.dialog_positive))
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDialog.value = false }
                ) {
                    Text(stringResource(R.string.dialog_negative))
                }
            }
        )
    }
}
