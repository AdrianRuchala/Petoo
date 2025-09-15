package presentation.petCard

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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
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
import presentation.viewmodels.MainScreenViewModel
import com.droidcode.apps.petoo.R
import com.droidcode.apps.petoo.domain.models.SortType
import com.droidcode.apps.petoo.domain.models.VetPetsViewState
import presentation.addNotes.AddNotesScreen
import com.maxkeppeker.sheets.core.models.base.rememberUseCaseState
import com.maxkeppeler.sheets.calendar.CalendarDialog
import com.maxkeppeler.sheets.calendar.models.CalendarConfig
import com.maxkeppeler.sheets.calendar.models.CalendarSelection
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin
import presentation.petDetails.VetPetDetailsScreen
import java.time.LocalDate

@OptIn(ExperimentalMaterial3AdaptiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun VetPetCardScreen(
    modifier: Modifier,
    viewModel: MainScreenViewModel,
) {
    val sortDialog = remember { mutableStateOf(false) }
    var sortType by remember { mutableStateOf(SortType.Alphabetical) }
    var searchValue by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    val navigator = rememberListDetailPaneScaffoldNavigator<Any>()
    var currentScreenType by remember { mutableStateOf("VetPetDetailsScreen") }

    BackHandler(navigator.canNavigateBack()) {
        navigator.navigateBack()
    }

    val calendarState = rememberUseCaseState()
    CalendarDialog(
        state = calendarState,
        config = CalendarConfig(
            monthSelection = true,
            yearSelection = true
        ),
        selection = CalendarSelection.Date { selectedDate ->
            date = selectedDate.toString()
        })

    ListDetailPaneScaffold(
        directive = navigator.scaffoldDirective,
        value = navigator.scaffoldValue,
        listPane = {
            AnimatedPane {
                Scaffold(
                ) { padding ->
                    modifier.padding(padding)

                    viewModel.vetReadSortedData(sortType, date)

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
                                    onClick = { calendarState.show() },
                                ) {
                                    Text(date)
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
                                                viewModel.vetSearchPet(searchValue, date)
                                            } else {
                                                viewModel.vetReadSortedData(sortType, date)
                                            }
                                        })
                                }
                            )
                        }

                        if (viewModel.vetMainScreenViewState.value.isEmpty()) {
                            item {
                                Text(
                                    text =
                                    if(date != LocalDate.now().toString()){
                                        stringResource(R.string.no_pets_this_day)
                                    } else {
                                        stringResource(R.string.no_pets_today)
                                    },
                                    modifier.fillMaxWidth(),
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else if (viewModel.vetMainScreenViewState.value.isNotEmpty()) {
                            items(viewModel.vetMainScreenViewState.value) { petData ->
                                VetPetPlate(
                                    modifier,
                                    petData,
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
                    if (currentScreenType == "AddNotesScreen") {
                        AddNotesScreen(
                            modifier,
                            content,
                            date,
                            viewModel
                        ) { currentScreenType = "VetPetDetailsScreen" }
                    } else {
                        VetPetDetailsScreen(
                            modifier,
                            content,
                            viewModel,
                            date,
                            { navigator.navigateBack() },
                            { currentScreenType = "AddNotesScreen" }
                        )
                    }
                }
            }
        }

    )
}

@Composable
fun VetPetPlate(
    modifier: Modifier,
    viewState: VetPetsViewState,
    navigateToPetDetails: (String) -> Unit,
) {
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

            Spacer(modifier = modifier.padding(4.dp))

            Text(viewState.petBreed)

            Spacer(modifier = modifier.padding(4.dp))

            Row {
                Text(
                    stringResource(R.string.owner),
                    modifier.padding(end = 4.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(viewState.petOwner)
            }
        }

        Spacer(modifier = Modifier.weight(1f))


        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = modifier
                .clickable { navigateToPetDetails(viewState.petId) }
        )

    }
}
