package com.droidcode.apps.petoo

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.droidcode.apps.petoo.ui.theme.VisitData
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Composable
fun VisitsScreen(
    modifier: Modifier,
    viewModel: MainScreenViewModel,
    role: String,
    navigateToAddVisitScreen: () -> Unit,
    navigateToVisitDetails: (String) -> Unit
) {
    var isUserVet by remember { mutableStateOf(false) }
    val tabItems = listOf(
        TabItem(stringResource(R.string.visits_history)),
        TabItem(stringResource(R.string.upcoming_visits)),
        TabItem(stringResource(R.string.future_visits))
    )

    viewModel.checkUserRole(role, viewModel) { checkedIsUserVet ->
        isUserVet = checkedIsUserVet
    }

    viewModel.readVisitData(isUserVet)

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val pagerState = rememberPagerState {
        tabItems.size
    }
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }

    Scaffold(
        floatingActionButton = { AddVisit { navigateToAddVisitScreen() } }
    ) { padding ->
        modifier.padding(padding)

        Column(modifier.fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTabIndex) {
                tabItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = {
                            selectedTabIndex = index
                        },
                        text = {
                            Text(item.title)
                        }
                    )
                }
            }
            HorizontalPager(
                pagerState,
                modifier
                    .fillMaxSize()
                    .weight(1f)
            ) { index ->
                LazyColumn(
                    modifier
                        .fillMaxSize()
                        .padding(all = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (viewModel.visitViewState.value.isEmpty()) {
                        item { Text(text = tabItems[index].title) }
                    } else {
                        items(viewModel.visitViewState.value) { visitData ->
                            val visitDateTime = "${visitData.visitDate} ${visitData.visitTime}"
                            val visitDateAsLocalDate = LocalDateTime.parse(
                                visitDateTime,
                                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                            )
                            val closestVisitDate = LocalDateTime.now().plusMonths(1)

                            if (visitDateAsLocalDate < LocalDateTime.now() && selectedTabIndex == 0) {
                                VisitPlate(
                                    modifier,
                                    visitData,
                                    viewModel,
                                    isUserVet
                                ) { navigateToVisitDetails(visitData.visitId) }
                            } else if (visitDateAsLocalDate >= LocalDateTime.now() && selectedTabIndex == 1 && visitDateAsLocalDate <= closestVisitDate) {
                                VisitPlate(
                                    modifier,
                                    visitData,
                                    viewModel,
                                    isUserVet
                                ) { navigateToVisitDetails(visitData.visitId) }
                            } else if (visitDateAsLocalDate > LocalDateTime.now() && selectedTabIndex == 2 && visitDateAsLocalDate > closestVisitDate) {
                                VisitPlate(
                                    modifier,
                                    visitData,
                                    viewModel,
                                    isUserVet
                                ) { navigateToVisitDetails(visitData.visitId) }
                            }
                        }
                    }
                }
            }
        }
    }
}

data class TabItem(
    val title: String,
)

@Composable
fun AddVisit(navigateToAddPetScreen: () -> Unit) {
    FloatingActionButton(onClick = { navigateToAddPetScreen() }) {
        Icon(imageVector = Icons.Default.Add, contentDescription = null)
    }
}

@Composable
fun VisitPlate(
    modifier: Modifier,
    viewState: VisitData,
    viewModel: MainScreenViewModel,
    isUserVet: Boolean,
    navigateToVisitDetails: (String) -> Unit
) {
    viewModel.readRecommendations(viewState.visitId)
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
        Column(
            modifier.padding(8.dp)
        ) {
            Text(viewState.petName, style = MaterialTheme.typography.titleLarge)

            Spacer(modifier = modifier.padding(4.dp))

            Row {
                if (isUserVet) {
                    Text(
                        stringResource(R.string.owner),
                        modifier.padding(end = 4.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(viewState.ownerName)
                } else {
                    Text(
                        stringResource(R.string.vet),
                        modifier.padding(end = 4.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(viewState.vetName)
                }
            }


            Spacer(modifier = modifier.padding(4.dp))

            Row {
                Text(
                    stringResource(R.string.vet_address),
                    modifier.padding(end = 4.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(viewState.vetAddress)
            }


            Spacer(modifier = modifier.padding(4.dp))

            Row {
                Text(
                    stringResource(R.string.visit_date),
                    modifier.padding(end = 4.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text("${viewState.visitDate}, ${viewState.visitTime}")
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
            contentDescription = null,
            modifier = modifier
                .clickable {
                    viewModel.readRecommendations(viewState.visitId)
                    navigateToVisitDetails(viewState.visitId)
                }
        )

    }
}
