package presentation.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.droidcode.apps.petoo.domain.models.MainScreenViewStateData
import com.droidcode.apps.petoo.domain.repository.PetRepository
import com.droidcode.apps.petoo.domain.models.SortType
import com.droidcode.apps.petoo.domain.models.VaccinationsData
import com.droidcode.apps.petoo.ui.theme.VisitData
import com.droidcode.apps.petoo.domain.models.OwnersViewStateData
import com.droidcode.apps.petoo.domain.models.VetPetsViewState
import com.droidcode.apps.petoo.domain.models.VetsViewStateData
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainScreenViewModel : ViewModel() {
    private val petRepository = PetRepository()

    private val userId = Firebase.auth.currentUser?.uid.toString()
    private val userName = Firebase.auth.currentUser?.displayName.toString()

    var mainScreenViewState = mutableStateOf(emptyList<MainScreenViewStateData>())
    var mainScreenArchiveViewState = mutableStateOf(emptyList<MainScreenViewStateData>())
    var vetMainScreenViewState = mutableStateOf(emptyList<VetPetsViewState>())
    var vaccinationViewState = mutableStateOf(emptyList<VaccinationsData>())
    private var sortedPetList = emptyList<MainScreenViewStateData>()
    private var sortedVetPetList = emptyList<VetPetsViewState>()
    var visitViewState = mutableStateOf(emptyList<VisitData>())
    var isUserVet = mutableStateOf(false)
    var vetsViewState = mutableStateOf(emptyList<VetsViewStateData>())
    var ownersViewState = mutableStateOf(emptyList<OwnersViewStateData>())
    var notes = mutableStateOf("")
    var recommendations = mutableStateOf("")

    fun checkUserRole(role: String, viewModel: MainScreenViewModel, isUserVet: (Boolean) -> Unit) {
        if (role == "Weterynarz" && !viewModel.isUserVet.value) {
            isUserVet(true)
        } else if (role == "Weterynarz" && viewModel.isUserVet.value) {
            isUserVet(true)
        } else if (role == "Użytkownik" && viewModel.isUserVet.value) {
            isUserVet(false)
        } else if (role == "Użytkownik" && !viewModel.isUserVet.value) {
            isUserVet(false)
        } else if (viewModel.isUserVet.value) {
            isUserVet(true)
        } else if (!viewModel.isUserVet.value) {
            isUserVet(false)
        }
    }

    fun addUserToDatabase(name: String, email: String?) {
        petRepository.addUserToDatabase(userId, name, email)
    }

    fun addPet(
        petID: String,
        petName: String,
        petDateOfBirth: String,
        selectedImage: String,
        petBreed: String,
        petGender: String,
        petSpecies: String
    ) {
        petRepository.addPet(userId, petID, petName, petDateOfBirth, selectedImage, petBreed, petGender, petSpecies)
    }

    fun readData() {
        petRepository.readPetData(userId) { petList ->
            sortedPetList = petList.sortedBy { it.petName }
            mainScreenViewState.value = sortedPetList
        }
    }

    fun readVaccinationList(petId: String) {
        petRepository.readVaccinationList(userId, petId) { vaccinationsList ->
            vaccinationViewState.value = vaccinationsList
        }
    }

    fun updatePetData(
        petID: String,
        selectedImage: String,
        petBreed: String,
        petGender: String,
        petSpecies: String
    ) {
        petRepository.updatePetData(userId, petID, selectedImage, petBreed, petGender, petSpecies)
    }


    fun removePet(
        petID: String,
        onNavigateUp: () -> Unit
    ) {
        petRepository.removePet(userId, petID) {
            onNavigateUp()
        }
    }

    fun addVaccineToDatabase(
        petID: String,
        illnessName: String,
        vaccineName: String,
        vaccinationDate: String
    ) {
        petRepository.addVaccineToDatabase(userId, petID, illnessName, vaccineName, vaccinationDate)
        readVaccinationList(petID)
    }

    fun removeVaccination(
        petID: String,
        illnessName: String,
    ) {
        petRepository.removeVaccination(userId, petID, illnessName) {
            readVaccinationList(petID)
        }
    }

    fun readArchiveData() {
        petRepository.readArchivePetData(userId) { petList ->
            sortedPetList = petList.sortedBy { it.petName }
            mainScreenArchiveViewState.value = sortedPetList
        }
    }

    fun readArchiveVaccinationList(petId: String) {
        petRepository.readArchiveVaccinationList(userId, petId) { vaccinationsList ->
            vaccinationViewState.value = vaccinationsList
        }
    }

    fun addPetToArchive(
        petId: String,
        petName: String,
        petDateOfBirth: String,
        petBreed: String,
        petImage: String,
        petGender: String,
        petSpecies: String,
        vaccinations: List<VaccinationsData>,
        onNavigateUp: () -> Unit
    ) {
        petRepository.addPetToArchive(
            userId,
            petId,
            petName,
            petDateOfBirth,
            petBreed,
            petImage,
            petGender,
            petSpecies,
            vaccinations
        ) {
            onNavigateUp()
        }
    }

    fun removePetFromArchive(petId: String) {
        petRepository.removePetFromArchive(userId, petId) {
            readArchiveData()
        }
    }

    fun addPetToPetList(
        petId: String,
        petName: String,
        petDateOfBirth: String,
        petBreed: String,
        petImage: String,
        petGender: String,
        petSpecies: String,
        vaccinations: List<VaccinationsData>
    ) {
        petRepository.addPetToPetList(
            userId,
            petId,
            petName,
            petDateOfBirth,
            petBreed,
            petImage,
            petGender,
            petSpecies,
            vaccinations
        ) {
            readArchiveData()
        }
    }

    fun readSortedData(sortType: SortType) {
        petRepository.readPetData(userId) { petList ->
            when (sortType) {
                SortType.Youngest -> {
                    sortedPetList = petList.sortedBy { it.petDateOfBirth }.reversed()
                    mainScreenViewState.value = sortedPetList
                }

                SortType.Oldest -> {
                    sortedPetList = petList.sortedBy { it.petDateOfBirth }
                    mainScreenViewState.value = sortedPetList
                }

                SortType.Alphabetical -> {
                    sortedPetList = petList.sortedBy { it.petName }
                    mainScreenViewState.value = sortedPetList
                }
            }
        }
    }

    fun readSortedArchiveData(sortType: SortType) {
        petRepository.readArchivePetData(userId) { petList ->
            when (sortType) {
                SortType.Youngest -> {
                    sortedPetList = petList.sortedBy { it.petDateOfBirth }.reversed()
                    mainScreenArchiveViewState.value = sortedPetList
                }

                SortType.Oldest -> {
                    sortedPetList = petList.sortedBy { it.petDateOfBirth }
                    mainScreenArchiveViewState.value = sortedPetList
                }

                SortType.Alphabetical -> {
                    sortedPetList = petList.sortedBy { it.petName }
                    mainScreenArchiveViewState.value = sortedPetList
                }
            }
        }
    }

    fun searchPet(searchPetName: String, readArchive: Boolean) {
        petRepository.searchPet(userId, readArchive) { petList ->
            sortedPetList = petList.filter { it.petName == searchPetName }
            mainScreenViewState.value = sortedPetList
        }
    }

    fun addVisit(
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
        petRepository.addVisit(
            userId,
            userName,
            visitId,
            petId,
            petName,
            petImage,
            vetId,
            vetName,
            vetAddress,
            visitDate,
            visitTime
        )
    }

    fun readVisitData(isUserVet: Boolean) {
        petRepository.readVisitData(userId, isUserVet) { visitData ->
            visitViewState.value = visitData.sortedBy { it.visitDate }
        }
    }

    fun removeVisit(visitId: String, isUserVet: Boolean, onNavigateUp: () -> Unit) {
        petRepository.removeVisit(visitId) {
            onNavigateUp()
            readVisitData(isUserVet)
        }
    }

    fun addVetToDatabase(name: String?, email: String?) {
        petRepository.addVetToDatabase(userId, name, email)
    }

    fun addTokenToDatabase(token: String, role: String) {
        isUserVet()
        petRepository.addTokenToDatabase(userId, token, role)
    }

    fun isUserVet() {
        petRepository.readIsUserVet(userId) { isVet ->
            isUserVet.value = isVet
        }
    }

    fun vetReadSortedData(sortType: SortType, readDate: String) {
        petRepository.vetReadPetData(userId, readDate) { petList ->
            when (sortType) {
                SortType.Youngest -> {
                    sortedVetPetList = petList.sortedBy { it.petDateOfBirth }.reversed()
                    vetMainScreenViewState.value = sortedVetPetList
                }

                SortType.Oldest -> {
                    sortedVetPetList = petList.sortedBy { it.petDateOfBirth }
                    vetMainScreenViewState.value = sortedVetPetList
                }

                SortType.Alphabetical -> {
                    sortedVetPetList = petList.sortedBy { it.petName }
                    vetMainScreenViewState.value = sortedVetPetList
                }
            }
        }
    }

    fun readVets() {
        petRepository.readVetData() { vetList ->
            vetsViewState.value = vetList
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
        ownerName: String,
    ) {
        petRepository.addPetForVet(
            vetId,
            petId,
            petName,
            petDateOfBirth,
            petBreed,
            petImage,
            petGender,
            petSpecies,
            vaccinations,
            date,
            ownerName
        )
    }

    fun vetReadVaccinationList(petId: String, date: String) {
        petRepository.vetReadVaccinationList(userId, petId, date) { vaccinationsList ->
            vaccinationViewState.value = vaccinationsList
        }
    }

    fun vetSearchPet(searchPetName: String, date: String) {
        petRepository.vetSearchPet(userId, date) { petList ->
            sortedVetPetList = petList.filter { it.petName == searchPetName }
            vetMainScreenViewState.value = sortedVetPetList
        }
    }

    fun removePetFromVet(
        vetID: String,
        petID: String,
        date: String,
    ) {
        petRepository.removePetFromVet(vetID, petID, date)
    }

    fun saveNotes(
        petID: String,
        date: String,
        notes: String
    ) {
        petRepository.saveNotes(userId, petID, date, notes)
    }

    fun readNotes(
        petID: String,
        date: String,
    ) {
        petRepository.readNotes(userId, petID, date) { fetchedNotes ->
            notes.value = fetchedNotes
        }
    }

    fun vetAddVisit(
        visitId: String,
        petId: String,
        petName: String,
        petImage: String,
        ownerId: String,
        ownerName: String,
        vetAddress: String,
        visitDate: String,
        visitTime: String
    ) {
        petRepository.addVisit(
            ownerId,
            ownerName,
            visitId,
            petId,
            petName,
            petImage,
            userId,
            userName,
            vetAddress,
            visitDate,
            visitTime
        )
    }

    fun readOwners() {
        petRepository.readOwnersData() { ownerList ->
            ownersViewState.value = ownerList
        }
    }

    fun readPets(ownerId: String) {
        petRepository.readPetData(ownerId) { petList ->
            sortedPetList = petList.sortedBy { it.petName }
            mainScreenViewState.value = sortedPetList
        }
    }

    fun vetAddVaccinationList(ownerId: String, petId: String) {
        petRepository.readVaccinationList(ownerId, petId) { vaccinationsList ->
            vaccinationViewState.value = vaccinationsList
        }
    }

    fun checkIfVisitExists(
        petId: String,
        vetId: String,
        visitDate: String,
        visitTime: String,
        onSuccess: (Boolean) -> Unit
    ) {
        petRepository.checkIfVisitExists(petId, vetId, visitDate, visitTime) { isExisting ->
            onSuccess(isExisting)
        }
    }

    fun saveRecommendations(
        visitId: String,
        recommendations: String
    ) {
        petRepository.saveRecommendations(visitId, recommendations)
    }

    fun readRecommendations(
        visitId: String,
    ) {
        petRepository.readRecommendations(visitId) { fetchedRecommendations ->
            recommendations.value = fetchedRecommendations
        }
    }
}