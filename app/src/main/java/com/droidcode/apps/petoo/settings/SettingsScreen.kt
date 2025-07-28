package com.droidcode.apps.petoo.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.droidcode.apps.petoo.R
import com.skydoves.landscapist.glide.GlideImage

@Composable
fun ProfileScreen(
    modifier: Modifier,
    settingsInfo: SettingsInfo,
    onLogout: () -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (!settingsInfo.profile.name.isNullOrEmpty() && !settingsInfo.profile.email.isNullOrEmpty()) {
                GlideImage(
                    imageModel = { settingsInfo.profile.profilePictureUrl },
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape),
                )
                Column(
                    modifier = Modifier.padding(start = 8.dp),
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        text = settingsInfo.profile.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Text(
                        text = settingsInfo.profile.email,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
        HorizontalDivider(modifier = Modifier.padding(all = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(R.string.app_version))
            Text(
                text = settingsInfo.appVersion, textAlign = TextAlign.End
            )
        }
        HorizontalDivider(modifier = Modifier.padding(all = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(stringResource(id = R.string.dev_options))

            Icon(
                modifier = Modifier.clickable { },
                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
            )
        }
        HorizontalDivider(modifier = Modifier.padding(all = 8.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(all = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = { onLogout() },
                shape = MaterialTheme.shapes.small,
            ) {
                Text(text = stringResource(id = R.string.logout_text))
            }
        }
    }
}
