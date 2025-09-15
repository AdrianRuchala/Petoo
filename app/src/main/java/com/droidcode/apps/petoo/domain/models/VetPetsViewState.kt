package com.droidcode.apps.petoo.domain.models

data class VetPetsViewState(
    var petId: String,
    var petName: String,
    var petDateOfBirth: String,
    var petImage: String,
    var petGender: String,
    var petSpecies: String,
    var petBreed: String,
    var petOwner: String
)