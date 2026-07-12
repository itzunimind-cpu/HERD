package com.motisoft.herd.ui.screens.dailylog

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.local.entity.DailyLogEntity
import com.motisoft.herd.data.repository.DailyLogRepository
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
class DailyInformationViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val dailyLogRepository: DailyLogRepository,
) : ViewModel() {

    val cowTag: String = checkNotNull(savedStateHandle[NavRoutes.ARG_TAG])

    val logs: StateFlow<List<DailyLogEntity>> = dailyLogRepository.observeLogs(cowTag)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addLog(date: LocalDate, feed: String, water: String, temperature: Double?, notes: String) {
        viewModelScope.launch {
            dailyLogRepository.saveLog(
                DailyLogEntity(
                    cowTag = cowTag,
                    date = date,
                    feed = feed,
                    water = water,
                    temperature = temperature,
                    activityNotes = notes,
                    remoteId = null,
                    updatedAt = Clock.System.now(),
                    dirty = true,
                ),
            )
        }
    }
}
