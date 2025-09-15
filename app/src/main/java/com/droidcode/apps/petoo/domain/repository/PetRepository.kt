package com.droidcode.apps.petoo.domain.repository

import com.droidcode.apps.petoo.domain.models.MainScreenViewStateData
import com.droidcode.apps.petoo.domain.models.OwnersViewStateData
import com.droidcode.apps.petoo.domain.models.VaccinationsData
import com.droidcode.apps.petoo.domain.models.VetPetsViewState
import com.droidcode.apps.petoo.domain.models.VetsViewStateData
import com.droidcode.apps.petoo.ui.theme.VisitData
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.FirebaseDatabase

class PetRepository {
    private val database = FirebaseDatabase.getInstance().getReference("Users")
    private val archiveRef = FirebaseDatabase.getInstance().getReference("Archive")
    private val visitsRef = FirebaseDatabase.getInstance().getReference("Visits")
    private val vetRef = FirebaseDatabase.getInstance().getReference("Vets")

    fun addUserToDatabase(userID: String, name: String, email: String?) {
        database.child(userID).child("Name").setValue(name)
        database.child(userID).child("Email").setValue(email)
    }

    fun addPet(
        userID: String,
        petID: String,
        petName: String,
        petDateOfBirth: String,
        selectedImage: String,
        petBreed: String,
        petGender: String,
        petSpecies: String
    ) {
        database.child(userID).child("Pets").child(petID).child("PetName").setValue(petName)
        database.child(userID).child("Pets").child(petID).child("PetDateOfBirth")
            .setValue(petDateOfBirth)
        database.child(userID).child("Pets").child(petID).child("Image").setValue(selectedImage)
        database.child(userID).child("Pets").child(petID).child("Breed").setValue(petBreed)
        database.child(userID).child("Pets").child(petID).child("Gender").setValue(petGender)
        database.child(userID).child("Pets").child(petID).child("Species").setValue(petSpecies)
    }

    fun readPetData(userId: String, onSuccess: (List<MainScreenViewStateData>) -> Unit) {
        val petList = mutableListOf<MainScreenViewStateData>()
        database.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                getPetData(it, petList)
                onSuccess(petList)
            }
        }
    }

    fun readVaccinationList(
        userId: String,
        petId: String,
        onSuccess: (List<VaccinationsData>) -> Unit
    ) {
        val vaccinationsList = mutableListOf<VaccinationsData>()
        database.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                getVaccinationsList(it, petId, vaccinationsList)
                onSuccess(vaccinationsList)
            }
        }
    }

    fun updatePetData(
        userID: String,
        petID: String,
        selectedImage: String,
        petBreed: String,
        petGender: String,
        petSpecies: String
    ) {
        database.child(userID).child("Pets").child(petID).child("Image").setValue(selectedImage)
        database.child(userID).child("Pets").child(petID).child("Breed").setValue(petBreed)
        database.child(userID).child("Pets").child(petID).child("Gender").setValue(petGender)
        database.child(userID).child("Pets").child(petID).child("Species").setValue(petSpecies)
    }

    fun removePet(
        userID: String,
        petID: String,
        onSuccess: () -> Unit
    ) {
        database.child(userID).child("Pets").child(petID).removeValue()
            .addOnSuccessListener { onSuccess() }
    }

    fun addVaccineToDatabase(
        userID: String,
        petID: String,
        illnessName: String,
        vaccineName: String,
        vaccinationDate: String,
    ) {
        database.child(userID).child("Pets").child(petID)
            .child("Vaccinations").child(illnessName).child("VaccinationDate")
            .setValue(vaccinationDate)
        database.child(userID).child("Pets").child(petID)
            .child("Vaccinations").child(illnessName).child("Vaccine")
            .setValue(vaccineName)
    }

    fun removeVaccination(
        userID: String,
        petID: String,
        illnessName: String,
        onSuccess: () -> Unit
    ) {
        database.child(userID).child("Pets").child(petID).child("Vaccinations")
            .child(illnessName).removeValue().addOnSuccessListener { onSuccess() }
    }

    private fun getVaccinationsList(
        database: DataSnapshot,
        petId: String,
        vaccinationsList: MutableList<VaccinationsData>
    ) {
        database.child("Pets").child(petId)
            .child("Vaccinations").children.forEach { vaccinationChild ->
                val illnessName = vaccinationChild.key.toString()
                val vaccinationName =
                    vaccinationChild.child("Vaccine")
                        .getValue(String::class.java)
                        .toString()
                val vaccinationDate =
                    vaccinationChild.child("VaccinationDate")
                        .getValue(String::class.java)
                        .toString()

                val vaccinationData =
                    VaccinationsData(illnessName, vaccinationName, vaccinationDate)
                vaccinationsList.add(vaccinationData)
            }
    }

    private fun getPetData(database: DataSnapshot, petList: MutableList<MainScreenViewStateData>) {
        database.child("Pets").children.forEach { child ->
            val petId = child.key.toString()
            val petName =
                database.child("Pets").child(petId).child("PetName")
                    .getValue(String::class.java)
                    .toString()
            val petDateOfBirth = database.child("Pets").child(petId).child("PetDateOfBirth")
                .getValue(String::class.java).toString()
            val petImage =
                database.child("Pets").child(petId).child("Image")
                    .getValue(String::class.java)
                    .toString()
            val petGender =
                database.child("Pets").child(petId).child("Gender")
                    .getValue(String::class.java) ?: ""
            val petSpecies =
                database.child("Pets").child(petId).child("Species")
                    .getValue(String::class.java) ?: ""
            val petBreed =
                database.child("Pets").child(petId).child("Breed")
                    .getValue(String::class.java) ?: ""

            val vaccinationsList = mutableListOf<VaccinationsData>()
            getVaccinationsList(database, petId, vaccinationsList)

            val petData = MainScreenViewStateData(
                petId,
                petName,
                petDateOfBirth,
                petImage,
                petGender,
                petSpecies,
                petBreed
            )
            petList.add(petData)
        }
    }

    fun readArchivePetData(userId: String, onSuccess: (List<MainScreenViewStateData>) -> Unit) {
        val petList = mutableListOf<MainScreenViewStateData>()
        archiveRef.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                getPetData(it, petList)
                onSuccess(petList)
            }
        }
    }

    fun readArchiveVaccinationList(
        userId: String,
        petId: String,
        onSuccess: (List<VaccinationsData>) -> Unit
    ) {
        val vaccinationsList = mutableListOf<VaccinationsData>()
        archiveRef.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                getVaccinationsList(it, petId, vaccinationsList)
                onSuccess(vaccinationsList)
            }
        }
    }

    fun addPetToArchive(
        userId: String,
        petId: String,
        petName: String,
        petDateOfBirth: String,
        petBreed: String,
        petImage: String,
        petGender: String,
        petSpecies: String,
        vaccinations: List<VaccinationsData>,
        onSuccess: () -> Unit
    ) {

        archiveRef.child(userId).child("Pets").child(petId)
            .child("PetName").setValue(petName)
        archiveRef.child(userId).child("Pets").child(petId)
            .child("PetDateOfBirth").setValue(petDateOfBirth)
        archiveRef.child(userId).child("Pets").child(petId)
            .child("Image").setValue(petImage)
        archiveRef.child(userId).child("Pets").child(petId)
            .child("Breed").setValue(petBreed)
        archiveRef.child(userId).child("Pets").child(petId)
            .child("Gender").setValue(petGender)
        archiveRef.child(userId).child("Pets").child(petId)
            .child("Species").setValue(petSpecies)

        vaccinations.forEach() { vaccination ->
            archiveRef.child(userId).child("Pets").child(petId)
                .child("Vaccinations").child(vaccination.illnessName).child("VaccinationDate")
                .setValue(vaccination.vaccinationDate)
            archiveRef.child(userId).child("Pets").child(petId)
                .child("Vaccinations").child(vaccination.illnessName).child("Vaccine")
                .setValue(vaccination.vaccinationName)
        }

        database.child(userId).child("Pets").child(petId).removeValue()
            .addOnSuccessListener { onSuccess() }
    }

    fun removePetFromArchive(userId: String, petId: String, onSuccess: () -> Unit) {
        archiveRef.child(userId).child("Pets").child(petId).removeValue()
            .addOnSuccessListener { onSuccess() }
    }

    fun addPetToPetList(
        userId: String,
        petId: String,
        petName: String,
        petDateOfBirth: String,
        petBreed: String,
        petImage: String,
        petGender: String,
        petSpecies: String,
        vaccinations: List<VaccinationsData>,
        onSuccess: () -> Unit
    ) {
        database.child(userId).child("Pets").child(petId)
            .child("PetName").setValue(petName)
        database.child(userId).child("Pets").child(petId)
            .child("PetDateOfBirth").setValue(petDateOfBirth)
        database.child(userId).child("Pets").child(petId)
            .child("Image").setValue(petImage)
        database.child(userId).child("Pets").child(petId)
            .child("Breed").setValue(petBreed)
        database.child(userId).child("Pets").child(petId)
            .child("Gender").setValue(petGender)
        database.child(userId).child("Pets").child(petId)
            .child("Species").setValue(petSpecies)

        vaccinations.forEach() { vaccination ->
            database.child(userId).child("Pets").child(petId)
                .child("Vaccinations").child(vaccination.illnessName).child("VaccinationDate")
                .setValue(vaccination.vaccinationDate)
            database.child(userId).child("Pets").child(petId)
                .child("Vaccinations").child(vaccination.illnessName).child("Vaccine")
                .setValue(vaccination.vaccinationName)
        }

        archiveRef.child(userId).child("Pets").child(petId).removeValue()
            .addOnSuccessListener { onSuccess() }
    }

    fun searchPet(
        userId: String,
        readArchive: Boolean,
        onSuccess: (List<MainScreenViewStateData>) -> Unit
    ) {
        val petList = mutableListOf<MainScreenViewStateData>()
        if (readArchive) {
            archiveRef.child(userId).get().addOnSuccessListener {
                if (it.exists()) {
                    getPetData(it, petList)
                    onSuccess(petList)
                }
            }
        } else {
            database.child(userId).get().addOnSuccessListener {
                if (it.exists()) {
                    getPetData(it, petList)
                    onSuccess(petList)
                }
            }
        }
    }

    fun addVisit(
        userId: String,
        userName: String,
        visitId: String,
        petId: String,
        petName: String,
        petImage: String,
        vetId: String,
        vetName: String,
        vetAddress: String,
        visitDate: String,
        visitTime: String
    ) {
        visitsRef.child(visitId).child("OwnerID").setValue(userId)
        visitsRef.child(visitId).child("OwnerName").setValue(userName)
        visitsRef.child(visitId).child("VetID").setValue(vetId)
        visitsRef.child(visitId).child("PetID").setValue(petId)
        visitsRef.child(visitId).child("PetName").setValue(petName)
        visitsRef.child(visitId).child("PetImage").setValue(petImage)
        visitsRef.child(visitId).child("VetName").setValue(vetName)
        visitsRef.child(visitId).child("vetAddress").setValue(vetAddress)
        visitsRef.child(visitId).child("visitDate").setValue(visitDate)
        visitsRef.child(visitId).child("visitTime").setValue(visitTime)
    }

    fun readVisitData(userId: String, isUserVet: Boolean, onSuccess: (List<VisitData>) -> Unit) {
        val visitList = mutableListOf<VisitData>()
        visitsRef.get().addOnSuccessListener {
            if (it.exists()) {
                getVisitData(it, visitList, userId, isUserVet)
                onSuccess(visitList)
            }
        }
    }

    private fun getVisitData(
        database: DataSnapshot,
        visitList: MutableList<VisitData>,
        userId: String,
        isUserVet: Boolean
    ) {
        database.children.forEach { child ->
            val visitId = child.key.toString()
            val petName =
                database.child(visitId).child("PetName").getValue(String::class.java).toString()
            val vetAddress =
                database.child(visitId).child("vetAddress").getValue(String::class.java)
                    .toString()
            val visitDate =
                database.child(visitId).child("visitDate").getValue(String::class.java)
                    .toString()
            val visitTime =
                database.child(visitId).child("visitTime").getValue(String::class.java)
                    .toString()
            val ownerId =
                database.child(visitId).child("OwnerID").getValue(String::class.java)
                    .toString()
            val vetId =
                database.child(visitId).child("VetID").getValue(String::class.java)
                    .toString()
            val petId =
                database.child(visitId).child("PetID").getValue(String::class.java)
                    .toString()
            val petImage =
                database.child(visitId).child("PetImage").getValue(String::class.java)
                    .toString()
            val vetName =
                database.child(visitId).child("VetName").getValue(String::class.java)
                    .toString()
            val ownerName =
                database.child(visitId).child("OwnerName").getValue(String::class.java)
                    .toString()

            val visitData = VisitData(
                visitId, ownerName, ownerId, petId, petName, petImage, vetId,
                vetName, vetAddress, visitDate, visitTime
            )

            if (isUserVet) {
                if (vetId == userId) {
                    visitList.add(visitData)
                }
            } else if (!isUserVet) {
                if (ownerId == userId) {
                    visitList.add(visitData)
                }
            }
        }
    }

    fun removeVisit(visitId: String, onSuccess: () -> Unit) {
        visitsRef.child(visitId).removeValue().addOnSuccessListener { onSuccess() }
    }

    fun addTokenToDatabase(userId: String, token: String, role: String) {
        if (role == "Weterynarz") {
            vetRef.child(userId).child("TokenFCM").setValue(token)
        } else if (role == "Użytkownik") {
            database.child(userId).child("TokenFCM").setValue(token)
        }
    }

    fun readIsUserVet(userId: String, onSuccess: (Boolean) -> Unit) {
        vetRef.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                onSuccess(true)
            } else {
                onSuccess(false)
            }
        }
    }

    fun vetReadPetData(
        userId: String,
        readDate: String,
        onSuccess: (List<VetPetsViewState>) -> Unit
    ) {
        val petList = mutableListOf<VetPetsViewState>()
        vetRef.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                vetGetPetData(it, readDate, petList)
                onSuccess(petList)
            }
        }
    }

    private fun vetGetPetData(
        database: DataSnapshot,
        readDate: String,
        petList: MutableList<VetPetsViewState>
    ) {
        database.child("Pets").child(readDate).children.forEach { child ->
            val petId = child.key.toString()
            val petName =
                database.child("Pets").child(readDate).child(petId).child("PetName")
                    .getValue(String::class.java)
                    .toString()
            val petDateOfBirth =
                database.child("Pets").child(readDate).child(petId).child("PetDateOfBirth")
                    .getValue(String::class.java).toString()
            val petImage =
                database.child("Pets").child(readDate).child(petId).child("Image")
                    .getValue(String::class.java)
                    .toString()
            val petGender =
                database.child("Pets").child(readDate).child(petId).child("Gender")
                    .getValue(String::class.java)
                    .toString()
            val petSpecies =
                database.child("Pets").child(readDate).child(petId).child("Species")
                    .getValue(String::class.java)
                    .toString()
            val petBreed =
                database.child("Pets").child(readDate).child(petId).child("Breed")
                    .getValue(String::class.java) ?: ""
            val petOwner =
                database.child("Pets").child(readDate).child(petId).child("OwnerName")
                    .getValue(String::class.java) ?: ""

            val vaccinationsList = mutableListOf<VaccinationsData>()
            getVaccinationsList(database, petId, vaccinationsList)

            val petData = VetPetsViewState(
                petId,
                petName,
                petDateOfBirth,
                petImage,
                petGender,
                petSpecies,
                petBreed,
                petOwner
            )
            petList.add(petData)
        }
    }

    fun addVetToDatabase(userID: String, name: String?, email: String?) {
        vetRef.child(userID).child("Name").setValue(name)
        vetRef.child(userID).child("Email").setValue(email)
    }

    fun readVetData(onSuccess: (List<VetsViewStateData>) -> Unit) {
        val vetList = mutableListOf<VetsViewStateData>()
        vetRef.get().addOnSuccessListener {
            if (it.exists()) {
                getVetData(it, vetList)
                onSuccess(vetList)
            }
        }
    }

    private fun getVetData(database: DataSnapshot, vetList: MutableList<VetsViewStateData>) {
        database.children.forEach { child ->
            val vetId = child.key.toString()
            val vetName =
                database.child(vetId).child("Name").getValue(String::class.java).toString()
            val vetEmail =
                database.child(vetId).child("Email").getValue(String::class.java).toString()

            val vetData = VetsViewStateData(
                vetId,
                vetEmail,
                vetName,
            )
            vetList.add(vetData)
        }
    }

    fun addPetForVet(
        vetId: String,
        petId: String,
        petName: String,
        petDateOfBirth: String,
        petBreed: String,
        petImage: String,
        petGender: String,
        petSpecies: String,
        vaccinations: List<VaccinationsData>,
        date: String,
        ownerName: String
    ) {
        vetRef.child(vetId).child("Pets").child(date).child(petId)
            .child("PetName").setValue(petName)
        vetRef.child(vetId).child("Pets").child(date).child(petId)
            .child("PetDateOfBirth").setValue(petDateOfBirth)
        vetRef.child(vetId).child("Pets").child(date).child(petId)
            .child("Image").setValue(petImage)
        vetRef.child(vetId).child("Pets").child(date).child(petId)
            .child("Breed").setValue(petBreed)
        vetRef.child(vetId).child("Pets").child(date).child(petId)
            .child("Gender").setValue(petGender)
        vetRef.child(vetId).child("Pets").child(date).child(petId)
            .child("Species").setValue(petSpecies)
        vetRef.child(vetId).child("Pets").child(date).child(petId)
            .child("OwnerName").setValue(ownerName)

        vaccinations.forEach() { vaccination ->
            vetRef.child(vetId).child("Pets").child(date).child(petId)
                .child("Vaccinations").child(vaccination.illnessName).child("VaccinationDate")
                .setValue(vaccination.vaccinationDate)
            vetRef.child(vetId).child("Pets").child(date).child(petId)
                .child("Vaccinations").child(vaccination.illnessName).child("Vaccine")
                .setValue(vaccination.vaccinationName)
        }
    }

    fun vetReadVaccinationList(
        userId: String,
        petId: String,
        date: String,
        onSuccess: (List<VaccinationsData>) -> Unit
    ) {
        val vaccinationsList = mutableListOf<VaccinationsData>()
        vetRef.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                vetGetVaccinationsList(it, petId, vaccinationsList, date)
                onSuccess(vaccinationsList)
            }
        }
    }

    private fun vetGetVaccinationsList(
        database: DataSnapshot,
        petId: String,
        vaccinationsList: MutableList<VaccinationsData>,
        date: String
    ) {
        database.child("Pets").child(date).child(petId)
            .child("Vaccinations").children.forEach { vaccinationChild ->
                val illnessName = vaccinationChild.key.toString()
                val vaccinationName =
                    vaccinationChild.child("Vaccine")
                        .getValue(String::class.java)
                        .toString()
                val vaccinationDate =
                    vaccinationChild.child("VaccinationDate")
                        .getValue(String::class.java)
                        .toString()

                val vaccinationData =
                    VaccinationsData(illnessName, vaccinationName, vaccinationDate)
                vaccinationsList.add(vaccinationData)
            }
    }

    fun removePetFromVet(
        vetID: String,
        petID: String,
        date: String,
    ) {
        vetRef.child(vetID).child("Pets").child(date).child(petID).removeValue()
            .addOnSuccessListener { }
    }

    fun vetSearchPet(
        userId: String,
        date: String,
        onSuccess: (List<VetPetsViewState>) -> Unit
    ) {
        val petList = mutableListOf<VetPetsViewState>()
        vetRef.child(userId).get().addOnSuccessListener {
            if (it.exists()) {
                vetGetPetData(it, date, petList)
                onSuccess(petList)
            }
        }
    }

    fun saveNotes(
        vetID: String,
        petID: String,
        date: String,
        notes: String
    ) {
        vetRef.child(vetID).child("Pets").child(date).child(petID).child("Notes").setValue(notes)
    }

    fun readNotes(
        vetID: String,
        petID: String,
        date: String,
        onSuccess: (String) -> Unit
    ) {
        vetRef.child(vetID).child("Pets").child(date).child(petID).child("Notes").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    onSuccess(it.value.toString())
                } else {
                    onSuccess("")
                }
            }
    }

    fun readOwnersData(onSuccess: (List<OwnersViewStateData>) -> Unit) {
        val ownerList = mutableListOf<OwnersViewStateData>()
        database.get().addOnSuccessListener {
            if (it.exists()) {
                getOwnerData(it, ownerList)
                onSuccess(ownerList)
            }
        }
    }

    private fun getOwnerData(database: DataSnapshot, ownerList: MutableList<OwnersViewStateData>) {
        database.children.forEach { child ->
            val ownerId = child.key.toString()
            val ownerName =
                database.child(ownerId).child("Name").getValue(String::class.java).toString()
            val ownerEmail =
                database.child(ownerId).child("Email").getValue(String::class.java).toString()

            val ownerData = OwnersViewStateData(
                ownerId,
                ownerEmail,
                ownerName,
            )
            ownerList.add(ownerData)
        }
    }

    fun checkIfVisitExists(
        petId: String,
        vetId: String,
        visitDate: String,
        visitTime: String,
        onSuccess: (Boolean) -> Unit
    ) {
        visitsRef.get().addOnSuccessListener {
            if (it.exists()) {
                getVisitDataExists(it, petId, vetId, visitDate, visitTime) { isExisting ->
                    onSuccess(isExisting)
                }
            }
        }
    }

    private fun getVisitDataExists(
        database: DataSnapshot,
        petId: String,
        vetId: String,
        visitDate: String,
        visitTime: String,
        isExisting: (Boolean) -> Unit
    ) {
        var visitExists = false
        database.children.forEach { child ->
            val visitId = child.key.toString()
            val checkedVisitDate =
                database.child(visitId).child("visitDate").getValue(String::class.java)
                    .toString()
            val checkedVisitTime =
                database.child(visitId).child("visitTime").getValue(String::class.java)
                    .toString()
            val checkedVetId =
                database.child(visitId).child("VetID").getValue(String::class.java)
                    .toString()
            val checkedPetId =
                database.child(visitId).child("PetID").getValue(String::class.java)
                    .toString()

            if (petId == checkedPetId && vetId == checkedVetId && visitTime == checkedVisitTime && visitDate == checkedVisitDate) {
                visitExists = true
                return@forEach
            }
        }
        isExisting(visitExists)
    }

    fun saveRecommendations(
        visitId: String,
        recommendations: String
    ) {
        visitsRef.child(visitId).child("Recommendations").setValue(recommendations)
    }

    fun readRecommendations(
        visitId: String,
        onSuccess: (String) -> Unit
    ) {
        visitsRef.child(visitId).child("Recommendations").get()
            .addOnSuccessListener {
                if (it.exists()) {
                    onSuccess(it.value.toString())
                } else {
                    onSuccess("")
                }
            }
    }
}