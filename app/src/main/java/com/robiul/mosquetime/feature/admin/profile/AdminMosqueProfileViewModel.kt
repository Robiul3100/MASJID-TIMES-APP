package com.robiul.mosquetime.feature.admin.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.robiul.mosquetime.core.auth.AdminUser
import com.robiul.mosquetime.data.firebase.MosqueAdminRepository
import com.robiul.mosquetime.data.model.CommitteeCategory
import com.robiul.mosquetime.data.model.CommitteeMember
import com.robiul.mosquetime.data.model.FacilityItem
import com.robiul.mosquetime.data.model.MosqueDetails
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed class ProfileUiEvent {
    data class ShowMessage(val message: String, val isError: Boolean = false) : ProfileUiEvent()
}

@HiltViewModel
class AdminMosqueProfileViewModel @Inject constructor(
    private val repository: MosqueAdminRepository
) : ViewModel() {

    val currentDetails: StateFlow<MosqueDetails> = repository.mosqueDetails
    val committeeMembers: StateFlow<List<CommitteeMember>> = repository.committeeList
    val isLoading: StateFlow<Boolean> = repository.isLoading

    private val _eventFlow = MutableSharedFlow<ProfileUiEvent>()
    val eventFlow: SharedFlow<ProfileUiEvent> = _eventFlow.asSharedFlow()

    // Editable form state
    val nameBn = MutableStateFlow("")
    val nameEn = MutableStateFlow("")
    val establishedYear = MutableStateFlow("")
    val address = MutableStateFlow("")
    val district = MutableStateFlow("")
    val capacity = MutableStateFlow("")
    val floors = MutableStateFlow("")
    val description = MutableStateFlow("")
    val history = MutableStateFlow("")

    val imamName = MutableStateFlow("")
    val imamTitle = MutableStateFlow("")
    val imamEducation = MutableStateFlow("")
    val imamPhone = MutableStateFlow("")

    val muazzinName = MutableStateFlow("")
    val muazzinPhone = MutableStateFlow("")
    val khademName = MutableStateFlow("")
    val officePhone = MutableStateFlow("")
    val officeEmail = MutableStateFlow("")
    val website = MutableStateFlow("")

    val facilities = MutableStateFlow<List<FacilityItem>>(emptyList())
    val committee = MutableStateFlow<List<CommitteeMember>>(emptyList())

    init {
        populateForm(currentDetails.value, committeeMembers.value)
    }

    fun populateForm(details: MosqueDetails, members: List<CommitteeMember>) {
        nameBn.value = details.nameBn
        nameEn.value = details.nameEn
        establishedYear.value = details.establishedYear
        address.value = details.address
        district.value = details.district
        capacity.value = details.capacity
        floors.value = details.floors
        description.value = details.description
        history.value = details.history

        imamName.value = details.imamName
        imamTitle.value = details.imamTitle
        imamEducation.value = details.imamEducation
        imamPhone.value = details.imamPhone

        muazzinName.value = details.muazzinName
        muazzinPhone.value = details.muazzinPhone
        khademName.value = details.khademName
        officePhone.value = details.officePhone
        officeEmail.value = details.officeEmail
        website.value = details.website

        facilities.value = details.facilities
        committee.value = members
    }

    fun addFacility(title: String, description: String, iconType: String) {
        val updated = facilities.value.toMutableList()
        updated.add(FacilityItem(title, description, iconType))
        facilities.value = updated
    }

    fun removeFacility(index: Int) {
        if (index in facilities.value.indices) {
            val updated = facilities.value.toMutableList()
            updated.removeAt(index)
            facilities.value = updated
        }
    }

    fun addCommitteeMember(
        name: String,
        designation: String,
        category: CommitteeCategory,
        phone: String,
        profession: String,
        term: String
    ) {
        val newId = "cm_${System.currentTimeMillis()}"
        val updated = committee.value.toMutableList()
        updated.add(CommitteeMember(newId, name, designation, category, phone, profession, term))
        committee.value = updated
    }

    fun removeCommitteeMember(id: String) {
        val updated = committee.value.filter { it.id != id }
        committee.value = updated
    }

    fun saveAll(adminUser: AdminUser?) {
        if (nameBn.value.isBlank()) {
            viewModelScope.launch {
                _eventFlow.emit(ProfileUiEvent.ShowMessage("মসজিদের নাম আবশ্যক!", isError = true))
            }
            return
        }

        viewModelScope.launch {
            val updatedDetails = MosqueDetails(
                nameBn = nameBn.value.trim(),
                nameEn = nameEn.value.trim(),
                establishedYear = establishedYear.value.trim(),
                address = address.value.trim(),
                district = district.value.trim(),
                capacity = capacity.value.trim(),
                floors = floors.value.trim(),
                history = history.value.trim(),
                description = description.value.trim(),
                imamName = imamName.value.trim(),
                imamTitle = imamTitle.value.trim(),
                imamEducation = imamEducation.value.trim(),
                imamPhone = imamPhone.value.trim(),
                muazzinName = muazzinName.value.trim(),
                muazzinPhone = muazzinPhone.value.trim(),
                khademName = khademName.value.trim(),
                officePhone = officePhone.value.trim(),
                officeEmail = officeEmail.value.trim(),
                website = website.value.trim(),
                facilities = facilities.value
            )

            repository.saveMosqueProfile(updatedDetails, adminUser)
            repository.saveCommitteeMembers(committee.value, adminUser)

            _eventFlow.emit(ProfileUiEvent.ShowMessage("মসজিদের প্রোফাইল তথ্য সফলভাবে সংরক্ষিত হয়েছে!"))
        }
    }
}
