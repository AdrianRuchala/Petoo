package com.droidcode.apps.petoo.settings

data class Profile(
    val name: String?,
    val email: String?,
    val profilePictureUrl: String?
)

data class SettingsInfo(
    val profile: Profile,
    val appVersion: String
)
