package com.motisoft.herd.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.repository.DailyMilkTotalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.todayIn
import javax.inject.Inject

data class QuickMilkUiState(
    val morningTotal: String = "",
    val eveningTotal: String = "",
    val isSaving: Boolean = false,
    val savedMessage: String? = null,
)

@HiltViewModel
class QuickMilkEntryViewModel @Inject constructor(
    private val dailyMilkTotalRepository: DailyMilkTotalRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(QuickMilkUiState())
    val uiState: StateFlow<QuickMilkUiState> = _uiState.asStateFlow()

    fun onMorningTotalChange(value: String) = _uiState.update { it.copy(morningTotal = value, savedMessage = null) }
    fun onEveningTotalChange(value: String) = _uiState.update { it.copy(eveningTotal = value, savedMessage = null) }

    fun saveTotal(onDone: () -> Unit) {
        val state = _uiState.value
        val morning = state.morningTotal.trim().toDoubleOrNull()
        val evening = state.eveningTotal.trim().toDoubleOrNull()
        if (morning == null && evening == null) return
        _uiState.update { it.copy(isSaving = true) }
        viewModelScope.launch {
            val today = Clock.System.todayIn(TimeZone.currentSystemDefault())
            dailyMilkTotalRepository.saveTotal(today, morning, evening)
            _uiState.update { it.copy(isSaving = false, savedMessage = "जतन झाले") }
            onDone()
        }
    }
}
