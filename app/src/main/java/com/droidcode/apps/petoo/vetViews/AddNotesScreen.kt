package com.droidcode.apps.petoo.vetViews

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.droidcode.apps.petoo.MainScreenViewModel
import com.droidcode.apps.petoo.R

@Composable
fun AddNotesScreen(
    modifier: Modifier,
    petId: String,
    date: String,
    viewModel: MainScreenViewModel,
    onNavigateUp: () -> Unit
) {
    viewModel.readNotes(petId, date)
    var notesText by remember { mutableStateOf(viewModel.notes.value) }

    Column(
        modifier
            .fillMaxSize()
            .padding(top = 8.dp)
    ) {
        TopBar(modifier, petId, date, notesText, viewModel) { onNavigateUp() }
        TextField(
            value = notesText,
            onValueChange = { notesText = it },
            modifier
                .fillMaxSize()
                .padding(all = 8.dp)
        )
    }
}

@Composable
fun TopBar(
    modifier: Modifier,
    petId: String,
    date: String,
    notes: String,
    viewModel: MainScreenViewModel,
    onNavigateUp: () -> Unit
) {
    val notesOldValue by remember { mutableStateOf(viewModel.notes.value) }
    val openExitDialog = remember { mutableStateOf(false) }

    if (openExitDialog.value) {
        ExitAlertDialog(openExitDialog) { onNavigateUp() }
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
                    if (notesOldValue != notes) {
                        openExitDialog.value = true
                    } else {
                        onNavigateUp()
                    }
                }
        )

        Text(
            text = stringResource(R.string.add_note),
            modifier = Modifier
                .padding(all = 8.dp),
            style = MaterialTheme.typography.titleMedium,
        )

        Image(
            if (isSystemInDarkTheme()) {
                painterResource(R.drawable.baseline_check_24_white)
            } else {
                painterResource(id = R.drawable.baseline_check_24)
            },
            contentDescription = null,
            modifier = Modifier
                .padding(end = 8.dp)
                .clickable {
                    viewModel.saveNotes(petId, date, notes)
                    onNavigateUp()
                }
        )

    }
    HorizontalDivider(modifier = Modifier.padding(top = 8.dp, bottom = 8.dp))
}

@Composable
fun ExitAlertDialog(
    showDialog: MutableState<Boolean>,
    onNavigateUp: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(stringResource(R.string.exit_title)) },
        text = { Text(stringResource(R.string.exit_text)) },
        confirmButton = {
            Button(
                onClick = {
                    showDialog.value = false
                    onNavigateUp()
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
