package com.droidcode.apps.petoo

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import com.droidcode.apps.petoo.vetViews.ExitAlertDialog
import com.skydoves.landscapist.components.rememberImageComponent
import com.skydoves.landscapist.glide.GlideImage
import com.skydoves.landscapist.placeholder.placeholder.PlaceholderPlugin

@Composable
fun VisitDetailsScreen(
    modifier: Modifier,
    visitId: String,
    viewModel: MainScreenViewModel,
    onNavigateUp: () -> Unit
) {
    var vetId by remember { mutableStateOf("") }
    var vetName by remember { mutableStateOf("") }
    var ownerId by remember { mutableStateOf("") }
    var ownerName by remember { mutableStateOf("") }
    var visitDate by remember { mutableStateOf("") }
    var visitTime by remember { mutableStateOf("") }
    var petId by remember { mutableStateOf("") }
    var petName by remember { mutableStateOf("") }
    var petImage by remember { mutableStateOf("") }
    var recommendations by remember { mutableStateOf(viewModel.recommendations.value) }
    var recommendationsOldValue by remember { mutableStateOf(viewModel.recommendations.value) }

    val visitData = viewModel.visitViewState.value.find { it.visitId == visitId }
    visitData?.let {
        vetId = visitData.vetId
        vetName = visitData.vetName
        ownerId = visitData.ownerId
        ownerName = visitData.ownerName
        petId = visitData.petId
        visitDate = visitData.visitDate
        visitTime = visitData.visitTime
        petName = visitData.petName
        petImage = visitData.petImage
    }

    Column(
        modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        TopBar(
            modifier,
            viewModel,
            visitId,
            vetId,
            petId,
            visitDate,
            recommendations,
            recommendationsOldValue
        ) { onNavigateUp() }

        Row(
            modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            GlideImage(
                imageModel = { visitData?.petImage },
                modifier = modifier
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

            Column(modifier.padding(8.dp)) {
                Text(petName, style = MaterialTheme.typography.titleMedium)

                Row {
                    Text(
                        stringResource(R.string.pet_keeper),
                        modifier.padding(end = 4.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(ownerName)
                }
            }

        }

        Column(modifier.padding(8.dp)) {
            Row {
                Text(
                    stringResource(R.string.visit_date),
                    modifier.padding(end = 4.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text("$visitDate, $visitTime")
            }

            Row {
                Text(
                    stringResource(R.string.admitting_vet),
                    modifier.padding(end = 4.dp),
                    style = MaterialTheme.typography.titleMedium
                )
                Text(vetName)
            }
        }

        Column(
            modifier
                .fillMaxSize()
                .padding(8.dp),
        ) {
            Text(
                stringResource(R.string.recommendations),
                style = MaterialTheme.typography.titleMedium
            )

            if (viewModel.isUserVet.value) {
                Column(
                    modifier
                        .fillMaxSize()
                        .padding(bottom = 9.dp)
                ) {
                    TextField(
                        value = recommendations,
                        onValueChange = { recommendations = it },
                        modifier.fillMaxWidth()
                    )
                    Spacer(modifier.padding(8.dp))

                    Button(
                        onClick = {
                            viewModel.saveRecommendations(visitId, recommendations)
                            recommendationsOldValue = recommendations
                        },
                        modifier.align(Alignment.End),
                        enabled = isSaveButtonEnabled(recommendations, recommendationsOldValue)
                    ) {
                        Text(stringResource(R.string.save))
                    }
                }
            } else {
                Text(recommendations)
            }

        }
    }
}

@Composable
fun TopBar(
    modifier: Modifier,
    viewModel: MainScreenViewModel,
    visitId: String,
    vetId: String,
    petId: String,
    visitDate: String,
    recommendations: String,
    recommendationsOldValue: String,
    onNavigateUp: () -> Unit
) {
    val openExitDialog = remember { mutableStateOf(false) }
    val openAlertDialog = remember { mutableStateOf(false) }
    viewModel.isUserVet()

    if (openExitDialog.value) {
        ExitAlertDialog(openExitDialog) { onNavigateUp() }
    }

    if (openAlertDialog.value) {
        RemoveVisit(
            openAlertDialog,
            visitId,
            vetId,
            petId,
            visitDate,
            viewModel,
            viewModel.isUserVet.value
        ) { onNavigateUp() }
    }

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
                .clickable {
                    if (isSaveButtonEnabled(recommendations, recommendationsOldValue)) {
                        openExitDialog.value = true
                    } else {
                        onNavigateUp()
                    }
                }
        )

        Text(
            stringResource(R.string.visit_details),
            modifier.padding(end = 4.dp),
            style = MaterialTheme.typography.titleMedium
        )

        Icon(
            imageVector = Icons.Default.Delete,
            contentDescription = null,
            modifier = modifier
                .clickable { openAlertDialog.value = true }
                .padding(end = 4.dp)
        )
    }
    HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
}

@Composable
fun RemoveVisit(
    showDialog: MutableState<Boolean>,
    visitId: String,
    vetId: String,
    petId: String,
    date: String,
    viewModel: MainScreenViewModel,
    isUserVet: Boolean,
    onNavigateUp: () -> Unit
) {
    if (showDialog.value) {
        AlertDialog(
            onDismissRequest = { showDialog.value = false },
            title = { Text(stringResource(R.string.visit_dialog)) },
            text = { Text(stringResource(R.string.dialog_visit_message)) },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.removeVisit(visitId, isUserVet) { onNavigateUp() }
                        viewModel.removePetFromVet(vetId, petId, date)
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

fun isSaveButtonEnabled(recommendations: String, recommendationsOldValue: String): Boolean {
    return recommendations != recommendationsOldValue
}
