package com.droidcode.apps.petoo.auth.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.droidcode.apps.petoo.MainScreenViewModel
import com.droidcode.apps.petoo.R
import com.droidcode.apps.petoo.ui.theme.PetooTheme
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

@Composable
fun LoginScreen(modifier: Modifier, onLogin: (String) -> Unit, showError: Boolean) {
    val showAlertDialog = remember { mutableStateOf(false) }

    if (showAlertDialog.value) {
        SelectRole(modifier, showAlertDialog, MainScreenViewModel(), onLogin)
    }

    Column(
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            modifier = modifier
                .size(120.dp),
            painter = painterResource(R.drawable.logo),
            contentDescription = null
        )

        Text(
            modifier = Modifier
                .padding(top = 40.dp),
            text = stringResource(id = R.string.hello_text),
            style = MaterialTheme.typography.headlineLarge
        )

        Text(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp),
            text = stringResource(id = R.string.welcome_text),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            modifier = Modifier
                .padding(start = 20.dp, end = 20.dp, top = 80.dp)
                .border(2.dp, MaterialTheme.colorScheme.secondary, MaterialTheme.shapes.small)
                .padding(all = 16.dp)
                .clickable { showAlertDialog.value = true },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = modifier
                    .size(32.dp),
                painter = painterResource(R.drawable.google),
                contentDescription = null
            )
            Text(
                modifier = Modifier
                    .padding(start = 16.dp),
                text = stringResource(id = R.string.login_text),
                style = MaterialTheme.typography.titleMedium
            )
        }
        if (showError == true) {
            LoginError()
        }
    }
}

@Composable
private fun LoginError() {
    Row(
        modifier = Modifier
            .padding(start = 20.dp, end = 20.dp, top = 10.dp)
            .border(2.dp, MaterialTheme.colorScheme.error, MaterialTheme.shapes.small)
            .background(
                color = MaterialTheme.colorScheme.errorContainer,
                shape = MaterialTheme.shapes.small
            )
            .padding(all = 16.dp)
    ) {
        Text(
            modifier = Modifier
                .padding(start = 16.dp, end = 16.dp),
            text = stringResource(id = R.string.login_error),
            style = MaterialTheme.typography.titleMedium
        )
    }
}


@Preview(showBackground = true)
@Composable
fun LoginErrorScreen() {
    PetooTheme {
        LoginError()
    }
}

@Composable
fun SelectRole(
    modifier: Modifier,
    showDialog: MutableState<Boolean>,
    viewModel: MainScreenViewModel,
    onLogin: (String) -> Unit
) {
    val roles = arrayOf(
        stringResource(R.string.user_role),
        stringResource(R.string.vet_role),
    )

    AlertDialog(
        onDismissRequest = { showDialog.value = false },
        title = { Text(stringResource(R.string.select_role)) },
        text = {
            Column {
                roles.forEach { role ->
                    Text(
                        text = role,
                        modifier = modifier
                            .fillMaxWidth()
                            .clickable {
                                when (role) {
                                    roles[0] -> {
                                        showDialog.value = false
                                        onLogin(role)
                                    }

                                    roles[1] -> {
                                        viewModel.addVetToDatabase(
                                            Firebase.auth.currentUser?.displayName,
                                            Firebase.auth.currentUser?.email
                                        )
                                        showDialog.value = false
                                        onLogin(role)
                                    }
                                }
                            }
                            .padding(vertical = 8.dp),
                        style = MaterialTheme.typography.titleMedium
                    )
                }
                Text(stringResource(id = R.string.login_info), modifier. padding(top = 16.dp))
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
