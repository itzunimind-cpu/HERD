package com.motisoft.herd.ui.screens.health

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.local.entity.HealthStatusEntity
import com.motisoft.herd.data.local.entity.IllnessLogEntity
import com.motisoft.herd.data.local.entity.VaccinationEntity
import com.motisoft.herd.data.repository.HealthRepository
import com.motisoft.herd.ui.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import javax.inject.Inject

@HiltViewModel
class HealthUpdateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val healthRepository: HealthRepository,
) : ViewModel() {

    val cowTag: String = checkNotNull(savedStateHandle[NavRoutes.ARG_TAG])

    val status: StateFlow<HealthStatusEntity?> = healthRepository.observeStatus(cowTag)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val vaccinations: StateFlow<List<VaccinationEntity>> = healthRepository.observeVaccinations(cowTag)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val illnessLog: StateFlow<List<IllnessLogEntity>> = healthRepository.observeIllnessLog(cowTag)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setStatus(newStatus: String) {
        viewModelScope.launch {
            healthRepository.saveStatus(
                HealthStatusEntity(
                    cowTag = cowTag,
                    currentStatus = newStatus,
                    remoteId = status.value?.remoteId,
                    updatedAt = Clock.System.now(),
                    dirty = true,
                ),
            )
        }
    }

    fun addVaccination(name: String, date: LocalDate) {
        viewModelScope.launch {
            healthRepository.addVaccination(
                VaccinationEntity(
                    cowTag = cowTag,
                    vaccineName = name,
                    date = date,
                    remoteId = null,
                    updatedAt = Clock.System.now(),
                    dirty = true,
                ),
            )
        }
    }

    fun addIllnessEntry(description: String, date: LocalDate, treatment: String) {
        viewModelScope.launch {
            healthRepository.addIllnessEntry(
                IllnessLogEntity(
                    cowTag = cowTag,
                    description = description,
                    date = date,
                    treatment = treatment,
                    remoteId = null,
                    updatedAt = Clock.System.now(),
                    dirty = true,
                ),
            )
        }
    }
}
