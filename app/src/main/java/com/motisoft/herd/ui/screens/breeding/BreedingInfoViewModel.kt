package com.motisoft.herd.ui.screens.breeding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.local.entity.BreedingInfoEntity
import com.motisoft.herd.data.local.entity.CalvingHistoryEntity
import com.motisoft.herd.data.repository.BreedingRepository
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
class BreedingInfoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val breedingRepository: BreedingRepository,
) : ViewModel() {

    val cowTag: String = checkNotNull(savedStateHandle[NavRoutes.ARG_TAG])

    val info: StateFlow<BreedingInfoEntity?> = breedingRepository.observeInfo(cowTag)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val calvingHistory: StateFlow<List<CalvingHistoryEntity>> = breedingRepository.observeCalvingHistory(cowTag)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun savePregnancyStatus(status: String) {
        viewModelScope.launch {
            val current = info.value
            breedingRepository.saveInfo(
                current?.copy(pregnancyStatus = status) ?: BreedingInfoEntity(
                    cowTag = cowTag,
                    pregnancyStatus = status,
                    lastHeatDate = null,
                    inseminationDate = null,
                    expectedCalvingDate = null,
                    remoteId = null,
                    updatedAt = Clock.System.now(),
                    dirty = true,
                ),
            )
        }
    }

    fun addCalvingEntry(calfSex: String, calvingDate: LocalDate) {
        viewModelScope.launch {
            breedingRepository.addCalvingEntry(
                CalvingHistoryEntity(
                    cowTag = cowTag,
                    calfSex = calfSex,
                    calvingDate = calvingDate,
                    remoteId = null,
                    updatedAt = Clock.System.now(),
                    dirty = true,
                ),
            )
        }
    }
}
