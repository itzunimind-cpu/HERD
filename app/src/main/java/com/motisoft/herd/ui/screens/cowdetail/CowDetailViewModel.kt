package com.motisoft.herd.ui.screens.cowdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.local.entity.CowEntity
import com.motisoft.herd.data.repository.CowRepository
import com.motisoft.herd.ui.navigation.NavRoutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class CowDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    cowRepository: CowRepository,
) : ViewModel() {

    val cowTag: String = checkNotNull(savedStateHandle[NavRoutes.ARG_TAG])

    val cow: StateFlow<CowEntity?> = cowRepository.observeCow(cowTag)
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
}
