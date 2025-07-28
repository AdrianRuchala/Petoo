package com.droidcode.apps.petoo.userViews

import android.net.Uri
import android.text.TextUtils.isEmpty
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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
import com.droidcode.apps.petoo.MainScreenViewModel
import com.droidcode.apps.petoo.R
import com.droidcode.apps.petoo.auth.presentation.UserData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin

private lateinit var database: DatabaseReference

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPetScreen(modifier: Modifier, viewModel: MainScreenViewModel, onNavigateUp: () -> Unit) {
    var petName by remember { mutableStateOf("") }
    var petDateOfBirth by remember { mutableStateOf("") }
    var petGender by remember { mutableStateOf("") }
    var petSpecies by remember { mutableStateOf("") }
    var petBreed by remember { mutableStateOf("") }
    val showAlertDialog = remember { mutableStateOf(false) }

    if (showAlertDialog.value) {
        PetGenderAlertDialog(modifier, showAlertDialog) { selectedGender ->
            petGender = selectedGender
        }
    }

    val calendarState = rememberUseCaseState()
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { date ->
            petDateOfBirth = date.toString()
        })

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
                stringResource(id = R.string.add_pet),
                modifier = Modifier
                    .padding(all = 8.dp),
                style = MaterialTheme.typography.titleMedium,
            )
        }
        HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))

        GlideImage(
            imageModel = { selectedImage },
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .align(Alignment.CenterHorizontally)
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

        Column(modifier.padding(all = 8.dp)) {
            TextField(
                value = petName,
                onValueChange = { petName = it },
                modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.name)) },
                isError = isEmpty(petName)
            )
            Spacer(modifier.padding(all = 8.dp))

            TextField(
                value = petDateOfBirth,
                onValueChange = { petDateOfBirth = it },
                modifier
                    .fillMaxWidth()
                    .clickable { calendarState.show() },
                label = { Text(stringResource(id = R.string.date_of_birth)) },
                isError = isEmpty(petDateOfBirth),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledLabelColor = if (petDateOfBirth.isEmpty()) TextFieldDefaults.colors().errorLabelColor else TextFieldDefaults.colors().unfocusedLabelColor,
                    disabledTextColor = if (petDateOfBirth.isEmpty()) TextFieldDefaults.colors().errorTextColor else TextFieldDefaults.colors().unfocusedTextColor,
                    disabledIndicatorColor = if (petDateOfBirth.isEmpty()) TextFieldDefaults.colors().errorIndicatorColor else TextFieldDefaults.colors().unfocusedIndicatorColor,
                )
            )
            Spacer(modifier.padding(all = 8.dp))

            TextField(
                value = petGender,
                onValueChange = { petGender = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { showAlertDialog.value = true },
                label = { Text(stringResource(id = R.string.gender)) },
                isError = petGender.isEmpty(),
                enabled = false,
                colors = TextFieldDefaults.colors(
                    disabledLabelColor = if (petGender.isEmpty()) TextFieldDefaults.colors().errorLabelColor else TextFieldDefaults.colors().unfocusedLabelColor,
                    disabledTextColor = if (petGender.isEmpty()) TextFieldDefaults.colors().errorTextColor else TextFieldDefaults.colors().unfocusedTextColor,
                    disabledIndicatorColor = if (petGender.isEmpty()) TextFieldDefaults.colors().errorIndicatorColor else TextFieldDefaults.colors().unfocusedIndicatorColor,
                )
            )


            Spacer(modifier.padding(all = 8.dp))

            TextField(
                value = petSpecies,
                onValueChange = { petSpecies = it },
                modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.pet_species)) },
                isError = isEmpty(petSpecies)
            )

            Spacer(modifier.padding(all = 8.dp))

            TextField(
                value = petBreed,
                onValueChange = { petBreed = it },
                modifier.fillMaxWidth(),
                label = { Text(stringResource(id = R.string.breed)) },
                isError = isEmpty(petBreed)
            )
        }

        Button(
            onClick = {
                if (!isEmpty(petName) && !isEmpty(petDateOfBirth) && !isEmpty(petBreed) && !isEmpty(
                        petSpecies
                    ) && !isEmpty(petGender)
                ) {
                    database = Firebase.database.reference
                    val userData = UserData(
                        email = Firebase.auth.currentUser?.email,
                        username = Firebase.auth.currentUser?.displayName,
                        profilePictureUrl = Firebase.auth.currentUser?.photoUrl.toString()
                    )

                    val email = userData.email.toString()
                    val name = userData.username.toString()

                    viewModel.addUserToDatabase(name, email)

                    val petId = database.push().key!!
                    val selectedImageString = selectedImage.toString()

                    viewModel.addPet(
                        petId,
                        petName,
                        petDateOfBirth,
                        selectedImageString,
                        petBreed,
                        petGender,
                        petSpecies
                    )
                    onNavigateUp()
                }
            },
            modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 16.dp)
        ) {
            Text(stringResource(id = R.string.save))
        }
    }
}

@Composable
fun PetGenderAlertDialog(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    onDismiss: (String) -> Unit
) {
    var petGender by remember { mutableStateOf("") }
    val genders = arrayOf("Samiec", "Samica")

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(text = stringResource(R.string.select_pet_gender)) },
        text = {
            Column {
                genders.forEach { gender ->
                    Text(
                        text = gender,
                        modifier = modifier
                            .fillMaxWidth()
                            .clickable {
                                when (gender) {
                                    genders[0] -> {
                                        petGender = gender
                                        onDismiss(gender)
                                        showDialog.value = false
                                    }

                                    genders[1] -> {
                                        petGender = gender
                                        onDismiss(gender)
                                        showDialog.value = false
                                    }
                                }
                            }
                            .padding(all = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { showDialog.value = false }) {
                Text(stringResource(R.string.dialog_negative))
            }
        }
    )
}
