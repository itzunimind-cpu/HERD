package com.motisoft.herd.ui.screens.cowinfo

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.local.entity.CowEntity
import com.motisoft.herd.data.repository.CowRepository
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

data class CowInformationEditState(
    val name: String = "",
    val breed: String = "",
    val birthDate: String = "",
    val gender: String = "मादी",
    val weight: String = "",
    val lineageTag: String = "",
)

@HiltViewModel
class CowInformationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cowRepository: CowRepository,
) : ViewModel() {

    val cowTag: String = checkNotNull(savedStateHandle[NavRoutes.ARG_TAG])

    val cow: StateFlow<CowEntity?> = cowRepository.observeCow(cowTag)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _editState = MutableStateFlow(CowInformationEditState())
    val editState: StateFlow<CowInformationEditState> = _editState.asStateFlow()

    fun startEditing(current: CowEntity) {
        _editState.value = CowInformationEditState(
            name = current.name,
            breed = current.breed,
            birthDate = current.birthDate?.toString().orEmpty(),
            gender = current.gender,
            weight = current.weight?.toString().orEmpty(),
            lineageTag = current.lineageTag.orEmpty(),
        )
        _isEditing.value = true
    }

    fun cancelEditing() {
        _isEditing.value = false
    }

    fun onNameChange(v: String) = _editState.update { it.copy(name = v) }
    fun onBreedChange(v: String) = _editState.update { it.copy(breed = v) }
    fun onBirthDateChange(v: String) = _editState.update { it.copy(birthDate = v) }
    fun onGenderChange(v: String) = _editState.update { it.copy(gender = v) }
    fun onWeightChange(v: String) = _editState.update { it.copy(weight = v) }
    fun onLineageTagChange(v: String) = _editState.update { it.copy(lineageTag = v) }

    fun save(current: CowEntity) {
        val edit = _editState.value
        viewModelScope.launch {
            cowRepository.saveCow(
                current.copy(
                    name = edit.name.trim(),
                    breed = edit.breed.trim(),
                    birthDate = edit.birthDate.trim().takeIf { it.isNotBlank() }
                        ?.let { runCatching { LocalDate.parse(it) }.getOrNull() },
                    gender = edit.gender,
                    weight = edit.weight.trim().toDoubleOrNull(),
                    lineageTag = edit.lineageTag.trim().takeIf { it.isNotBlank() },
                    updatedAt = Clock.System.now(),
                    dirty = true,
                ),
            )
            _isEditing.value = false
        }
    }
}
