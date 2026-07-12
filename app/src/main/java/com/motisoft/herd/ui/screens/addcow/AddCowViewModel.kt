package com.motisoft.herd.ui.screens.addcow

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.local.entity.CowEntity
import com.motisoft.herd.data.repository.CowRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.LocalDate
import javax.inject.Inject

data class AddCowUiState(
    val tag: String = "",
    val name: String = "",
    val breed: String = "",
    val birthDate: String = "",
    val gender: String = "मादी",
    val weight: String = "",
    val lineageTag: String = "",
    val errorMessage: String? = null,
    val isSaving: Boolean = false,
)

@HiltViewModel
class AddCowViewModel @Inject constructor(
    private val cowRepository: CowRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddCowUiState())
    val uiState: StateFlow<AddCowUiState> = _uiState.asStateFlow()

    fun onTagChange(v: String) = _uiState.update { it.copy(tag = v, errorMessage = null) }
    fun onNameChange(v: String) = _uiState.update { it.copy(name = v, errorMessage = null) }
    fun onBreedChange(v: String) = _uiState.update { it.copy(breed = v) }
    fun onBirthDateChange(v: String) = _uiState.update { it.copy(birthDate = v) }
    fun onGenderChange(v: String) = _uiState.update { it.copy(gender = v) }
    fun onWeightChange(v: String) = _uiState.update { it.copy(weight = v) }
    fun onLineageTagChange(v: String) = _uiState.update { it.copy(lineageTag = v) }

    fun save(onDone: () -> Unit) {
        val state = _uiState.value
        if (state.tag.isBlank() || state.name.isBlank()) {
            _uiState.update { it.copy(errorMessage = "टॅग आणि नाव आवश्यक आहे") }
            return
        }
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val cow = CowEntity(
                tag = state.tag.trim(),
                name = state.name.trim(),
                breed = state.breed.trim(),
                birthDate = state.birthDate.trim().takeIf { it.isNotBlank() }?.let {
                    runCatching { LocalDate.parse(it) }.getOrNull()
                },
                gender = state.gender,
                weight = state.weight.trim().toDoubleOrNull(),
                lineageTag = state.lineageTag.trim().takeIf { it.isNotBlank() },
                photoUri = null,
                remoteId = null,
                updatedAt = Clock.System.now(),
                dirty = true,
            )
            cowRepository.saveCow(cow)
            _uiState.update { it.copy(isSaving = false) }
            onDone()
        }
    }
}
