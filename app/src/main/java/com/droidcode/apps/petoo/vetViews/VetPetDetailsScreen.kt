package com.droidcode.apps.petoo.vetViews

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import com.droidcode.apps.petoo.VaccinationsData
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin


@Composable
fun VetPetDetailsScreen(
    modifier: Modifier,
    petId: String,
    viewModel: MainScreenViewModel,
    date: String,
    onNavigateUp: () -> Unit,
    navigateToAddNotesScreen: () -> Unit
) {
    var petName by remember { mutableStateOf("") }
    var petDateOfBirth by remember { mutableStateOf("") }
    var petImage by remember { mutableStateOf("") }
    var petBreed by remember { mutableStateOf("") }
    var petGender by remember { mutableStateOf("") }
    var petSpecies by remember { mutableStateOf("") }
    var petOwner by remember { mutableStateOf("") }
    var petVaccinations by remember { mutableStateOf<List<VaccinationsData>>(emptyList()) }

    viewModel.vetReadVaccinationList(petId, date)
    viewModel.readNotes(petId, date)
    petVaccinations = viewModel.vaccinationViewState.value
    val petData = viewModel.vetMainScreenViewState.value.find { it.petId == petId }
    petData?.let {
        petName = petData.petName
        petDateOfBirth = petData.petDateOfBirth
        petImage = petData.petImage
        petGender = petData.petGender
        petSpecies = petData.petSpecies
        petBreed = petData.petBreed
        petOwner = petData.petOwner
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {

        TopBarAndImage(
            modifier.align(Alignment.CenterHorizontally),
            petName,
            petData,
            { onNavigateUp() },
            { navigateToAddNotesScreen() }
        )
        LazyColumn(modifier.padding(all = 8.dp)) {
            item {
                TextField(
                    value = petName,
                    onValueChange = { petName = it },
                    modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.name)) },
                    isError = petName.isEmpty(),
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
                    value = petDateOfBirth,
                    onValueChange = { petDateOfBirth = it },
                    modifier
                        .fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.date_of_birth)) },
                    isError = petDateOfBirth.isEmpty(),
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
                    value = petGender,
                    onValueChange = { petGender = it },
                    modifier
                        .fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.gender)) },
                    isError = petGender.isEmpty(),
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
                    isError = petSpecies.isEmpty(),
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
                    value = petBreed,
                    onValueChange = { petBreed = it },
                    modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.breed)) },
                    isError = petBreed.isEmpty(),
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
                    value = petOwner,
                    onValueChange = { petOwner = it },
                    modifier.fillMaxWidth(),
                    label = { Text(stringResource(id = R.string.owner_label)) },
                    isError = petOwner.isEmpty(),
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        disabledLabelColor = TextFieldDefaults.colors().unfocusedLabelColor,
                        disabledTextColor = TextFieldDefaults.colors().unfocusedTextColor,
                        disabledIndicatorColor = TextFieldDefaults.colors().unfocusedIndicatorColor,
                    )

                )
            }

            items(petVaccinations) { vaccination ->
                Spacer(modifier.padding(all = 8.dp))
                TextField(
                    value = vaccination.vaccinationDate + " | " + vaccination.vaccinationName,
                    onValueChange = { vaccination.vaccinationName = it },
                    modifier
                        .fillMaxWidth(),
                    label = { Text(stringResource(R.string.vaccination) + " " + vaccination.illnessName) },
                    enabled = false,
                    colors = TextFieldDefaults.colors(
                        disabledLabelColor = TextFieldDefaults.colors().unfocusedLabelColor,
                        disabledTextColor = TextFieldDefaults.colors().unfocusedTextColor,
                        disabledIndicatorColor = TextFieldDefaults.colors().unfocusedIndicatorColor,
                    )
                )
            }
        }
    }
}

@Composable
fun TopBarAndImage(
    modifier: Modifier,
    petName: String,
    petData: VetPetsViewState?,
    onNavigateUp: () -> Unit,
    navigateToAddNotesScreen: () -> Unit
) {
    Row(
        modifier
            .fillMaxWidth()
            .padding(end = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
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
            text = petName,
            modifier = Modifier
                .padding(all = 8.dp),
            style = MaterialTheme.typography.titleMedium,
        )

        Image(
            if (isSystemInDarkTheme()) {
                painterResource(R.drawable.add_notes_white)
            } else {
                painterResource(id = R.drawable.add_notes)
            },
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable { navigateToAddNotesScreen() }
        )

    }
    HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))

    GlideImage(
        imageModel = { petData?.petImage },
        modifier = modifier
            .size(120.dp)
            .clip(CircleShape),
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
