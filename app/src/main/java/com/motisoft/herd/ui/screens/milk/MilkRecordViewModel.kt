package com.motisoft.herd.ui.screens.milk

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.local.entity.MilkRecordEntity
import com.motisoft.herd.data.repository.MilkRepository
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
class MilkRecordViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val milkRepository: MilkRepository,
) : ViewModel() {

    val cowTag: String = checkNotNull(savedStateHandle[NavRoutes.ARG_TAG])

    val records: StateFlow<List<MilkRecordEntity>> = milkRepository.observeRecords(cowTag)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun addRecord(date: LocalDate, morning: Double, evening: Double, fatPct: Double?, snfPct: Double?) {
        viewModelScope.launch {
            milkRepository.saveRecord(
                MilkRecordEntity(
                    cowTag = cowTag,
                    date = date,
                    morningYield = morning,
                    eveningYield = evening,
                    fatPct = fatPct,
                    snfPct = snfPct,
                    remoteId = null,
                    updatedAt = Clock.System.now(),
                    dirty = true,
                ),
            )
        }
    }
}
