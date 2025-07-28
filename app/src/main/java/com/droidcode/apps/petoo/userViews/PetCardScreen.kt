package com.droidcode.apps.petoo.userViews

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.droidcode.apps.petoo.MainScreenViewModel
import com.droidcode.apps.petoo.MainScreenViewStateData
import com.droidcode.apps.petoo.R
import com.droidcode.apps.petoo.SortType
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun PetCardScreen(
    modifier: Modifier,
    viewModel: MainScreenViewModel,
    navigateToAddPetScreen: () -> Unit
) {
    var readArchive by remember { mutableStateOf(false) }
    val sortDialog = remember { mutableStateOf(false) }
    var sortType by remember { mutableStateOf(SortType.Alphabetical) }
    var searchValue by remember { mutableStateOf("") }
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                Scaffold(
                    floatingActionButton = { AddPet { navigateToAddPetScreen() } }
                ) { padding ->
                    modifier.padding(padding)

                    if (readArchive) {
                        viewModel.readSortedArchiveData(sortType)

                    } else {
                        viewModel.readSortedData(sortType)
                    }

                    if (sortDialog.value) {
                        SortList(modifier, sortDialog) { type ->
                            sortType = type
                        }
                    }

                    LazyColumn(
                        modifier
                            .fillMaxSize()
                            .padding(all = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        item {
                            Row(
                                modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            )
                            {
                                Button(
                                    onClick = { readArchive = !readArchive },
                                ) {
                                    Text(
                                        if (readArchive) stringResource(R.string.pet_list) else stringResource(
                                            R.string.archives
                                        )
                                    )
                                }

                                Button(
                                    onClick = { sortDialog.value = true },
                                ) {
                                    Text(stringResource(R.string.sort))
                                }
                            }
                        }

                        item {
                            TextField(
                                value = searchValue,
                                onValueChange = { searchValue = it },
                                modifier
                                    .fillMaxWidth(),
                                label = { Text(stringResource(R.string.searchPet)) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Search,
                                        contentDescription = null,
                                        modifier.clickable {
                                            if (searchValue.isNotEmpty()) {
                                                viewModel.searchPet(searchValue, readArchive)
                                            } else {
                                                if (readArchive) {
                                                    viewModel.readSortedArchiveData(sortType)

                                                } else {
                                                    viewModel.readSortedData(sortType)
                                                }
                                            }
                                        })
                                }
                            )
                        }

                        if (!readArchive && viewModel.mainScreenViewState.value.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.empty_list),
                                    modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (readArchive && viewModel.mainScreenArchiveViewState.value.isEmpty()) {
                            item {
                                Text(
                                    text = stringResource(R.string.empty_list),
                                    modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (!readArchive && viewModel.mainScreenViewState.value.isNotEmpty()) {
                            items(viewModel.mainScreenViewState.value) { petData ->
                                PetPlate(
                                    modifier,
                                    petData,
                                    readArchive,
                                    viewModel,
                                ) {
                                    navigator.navigateTo(
                                        ListDetailPaneScaffoldRole.Detail,
                                        petData.petId
                                    )
                                }
                            }
                        } else if (readArchive && viewModel.mainScreenArchiveViewState.value.isNotEmpty()) {
                            items(viewModel.mainScreenArchiveViewState.value) { petData ->
                                PetPlate(
                                    modifier,
                                    petData,
                                    readArchive,
                                    viewModel,
                                ) {
                                    navigator.navigateTo(
                                        ListDetailPaneScaffoldRole.Detail,
                                        petData.petId
                                    )
                                }
                            }
                        }
                    }
                }
            }
        },
        detailPane = {
            AnimatedPane {
                val content = navigator.currentDestination?.content?.toString()
                    ?: stringResource(R.string.select_pet)
                navigator.currentDestination?.content?.let {
                    PetDetailsScreen(modifier, content, viewModel) { navigator.navigateBack() }
                }
            }
        }
    )
}


@Composable
fun AddPet(navigateToAddPetScreen: () -> Unit) {
    FloatingActionButton(onClick = { navigateToAddPetScreen() }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
    }
}

@Composable
fun PetPlate(
    modifier: Modifier,
    viewState: MainScreenViewStateData,
    readArchive: Boolean,
    viewModel: MainScreenViewModel,
    navigateToPetDetails: (String) -> Unit,
) {
    val openMovePetToPetListDialog = remember { mutableStateOf(false) }
    val openRemovePetDialog = remember { mutableStateOf(false) }

    if (openMovePetToPetListDialog.value) {
        MovePetToPetList(openMovePetToPetListDialog, viewModel, viewState)
    }

    if (openRemovePetDialog.value) {
        RemovePetFromArchive(openRemovePetDialog, viewModel, viewState.petId)
    }

    Row(
        modifier
            .fillMaxWidth()
            .border(2.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.small)
            .background(
                color = MaterialTheme.colorScheme.secondaryContainer,
                shape = MaterialTheme.shapes.small
            )
            .padding(all = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        GlideImage(
            imageModel = { viewState.petImage },
            modifier
                .size(80.dp)
                .clip(CircleShape),

            component = rememberImageComponent {
                +PlaceholderPlugin.Failure(
                    if (isSystemInDarkTheme()) {
                        painterResource(R.drawable.no_photography_white)
                    } else {
                        painterResource(id = R.drawable.no_photography)
                    }
                )
            }
        )

        Column(
            modifier.padding(8.dp)
        ) {
            Text(viewState.petName, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = modifier.padding(4.dp))

            Text(viewState.petDateOfBirth)
        }

        Spacer(modifier = Modifier.weight(1f))

        if (readArchive) {
            Image(
                if (isSystemInDarkTheme()) {
                    painterResource(R.drawable.unarchive_white)
                } else {
                    painterResource(id = R.drawable.unarchive)
                },
                contentDescription = null,
                modifier = modifier
                    .padding(horizontal = 8.dp)
                    .clickable { openMovePetToPetListDialog.value = true }
            )
            Icon(
                imageVector = Icons.Default.Delete,
                contentDescription = null,
                modifier = modifier
                    .clickable { openRemovePetDialog.value = true }
            )
        } else {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                modifier = modifier
                    .clickable { navigateToPetDetails(viewState.petId) }
            )
        }
    }
}

@Composable
fun MovePetToPetList(
    showDialog: MutableState<Boolean>,
    viewModel: MainScreenViewModel,
    viewState: MainScreenViewStateData,
) {
    viewModel.readArchiveVaccinationList(viewState.petId)
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.dialog_unarchive_title)) },
            text = { Text(stringResource(R.string.dialog_unarchive_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.addPetToPetList(
                            viewState.petId,
                            viewState.petName,
                            viewState.petDateOfBirth,
                            viewState.petBreed,
                            viewState.petImage,
                            viewState.petGender,
                            viewState.petSpecies,
                            viewModel.vaccinationViewState.value
                        )
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
fun RemovePetFromArchive(
    showDialog: MutableState<Boolean>,
    viewModel: MainScreenViewModel,
    petId: String
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.dialog_title)) },
            text = { Text(stringResource(R.string.dialog_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removePetFromArchive(petId)
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
fun SortList(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    onDismiss: (SortType) -> Unit
) {
    val sortTypes = arrayOf(
        "Alfabetycznie",
        "Od najmłodszego",
        "Od najstarszego",
    )
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(stringResource(R.string.sort)) },
        text = {
            Column {
                sortTypes.forEach { sortType ->
                    Text(
                        text = sortType,
                        modifier = modifier
                            .fillMaxWidth()
                            .clickable {
                                when (sortType) {
                                    sortTypes[0] -> {
                                        onDismiss(SortType.Alphabetical)
                                    }

                                    sortTypes[1] -> {
                                        onDismiss(SortType.Youngest)
                                    }

                                    sortTypes[2] -> {
                                        onDismiss(SortType.Oldest)
                                    }
                                }
                                showDialog.value = false
                            }
                            .padding(vertical = 8.dp),
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { showDialog.value = false }
            ) {
                Text(stringResource(R.string.dialog_negative))
            }
        }
    )
}
