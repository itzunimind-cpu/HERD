package com.motisoft.herd.ui.screens.breeding

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.local.entity.BreedingInfoEntity
import com.motisoft.herd.data.local.entity.CalvingHistoryEntity
import com.motisoft.herd.data.repository.BreedingRepository
import com.motisoft.herd.ui.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import javax.inject.Inject

data class BreedingDraftState(
    val lastHeatDate: String = "",
    val inseminationDate: String = "",
    val pregnancyTestDate: String = "",
    val expectedCalvingDate: String = "",
    val semenBreed: String = "",
    val isSaving: Boolean = false,
    val savedMessage: String? = null,
)

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

    private val _draft = MutableStateFlow(BreedingDraftState())
    val draft: StateFlow<BreedingDraftState> = _draft.asStateFlow()

    private var draftSeeded = false

    init {
        viewModelScope.launch {
            info.collect { current ->
                // Seed the editable draft once from whatever's already saved, so the
                // farmer's in-progress edits aren't clobbered by re-emissions from Room.
                if (!draftSeeded && current != null) {
                    draftSeeded = true
                    _draft.update {
                        it.copy(
                            lastHeatDate = current.lastHeatDate?.toString().orEmpty(),
                            inseminationDate = current.inseminationDate?.toString().orEmpty(),
                            pregnancyTestDate = current.pregnancyTestDate?.toString().orEmpty(),
                            expectedCalvingDate = current.expectedCalvingDate?.toString().orEmpty(),
                            semenBreed = current.semenBreed.orEmpty(),
                        )
                    }
                }
            }
        }
    }

    fun onLastHeatDateChange(value: String) = _draft.update { it.copy(lastHeatDate = value, savedMessage = null) }
    fun onInseminationDateChange(value: String) = _draft.update { it.copy(inseminationDate = value, savedMessage = null) }
    fun onPregnancyTestDateChange(value: String) = _draft.update { it.copy(pregnancyTestDate = value, savedMessage = null) }
    fun onExpectedCalvingDateChange(value: String) = _draft.update { it.copy(expectedCalvingDate = value, savedMessage = null) }
    fun onSemenBreedChange(value: String) = _draft.update { it.copy(semenBreed = value, savedMessage = null) }

    fun savePregnancyStatus(status: String) {
        viewModelScope.launch {
            val current = info.value
            breedingRepository.saveInfo(
                current?.copy(pregnancyStatus = status) ?: BreedingInfoEntity(
                    cowTag = cowTag,
                    pregnancyStatus = status,
                    lastHeatDate = null,
                    inseminationDate = null,
                    pregnancyTestDate = null,
                    expectedCalvingDate = null,
                    semenBreed = null,
                    remoteId = null,
                    updatedAt = Clock.System.now(),
                    dirty = true,
                ),
            )
        }
    }

    fun saveDetails() {
        val state = _draft.value
        _draft.update { it.copy(isSaving = true, savedMessage = null) }
        viewModelScope.launch {
            val current = info.value
            val updated = (current ?: BreedingInfoEntity(
                cowTag = cowTag,
                pregnancyStatus = "अनिश्चित",
                lastHeatDate = null,
                inseminationDate = null,
                pregnancyTestDate = null,
                expectedCalvingDate = null,
                semenBreed = null,
                remoteId = null,
                updatedAt = Clock.System.now(),
                dirty = true,
            )).copy(
                lastHeatDate = state.lastHeatDate.trim().toLocalDateOrNull(),
                inseminationDate = state.inseminationDate.trim().toLocalDateOrNull(),
                pregnancyTestDate = state.pregnancyTestDate.trim().toLocalDateOrNull(),
                expectedCalvingDate = state.expectedCalvingDate.trim().toLocalDateOrNull(),
                semenBreed = state.semenBreed.trim().takeIf { it.isNotBlank() },
            )
            breedingRepository.saveInfo(updated)
            _draft.update { it.copy(isSaving = false, savedMessage = "जतन झाले") }
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

    private fun String.toLocalDateOrNull(): LocalDate? =
        takeIf { it.isNotBlank() }?.let { runCatching { LocalDate.parse(it) }.getOrNull() }
}
