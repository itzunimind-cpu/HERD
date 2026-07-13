package com.motisoft.herd.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.motisoft.herd.data.notifications.NotificationSettingsRepository
import com.motisoft.herd.data.notifications.ReminderScheduler
import com.motisoft.herd.data.notifications.ReminderSession
import com.motisoft.herd.data.repository.AuthRepository
import com.motisoft.herd.data.repository.CowRepository
import com.motisoft.herd.data.repository.ProfileRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class DashboardUiState(
    val email: String = "",
    val name: String = "",
    val phone: String = "",
    val farmName: String = "",
    val cowCount: Int = 0,
    val isSavingProfile: Boolean = false,
    val profileSavedMessage: String? = null,
    val newPassword: String = "",
    val confirmPassword: String = "",
    val isChangingPassword: Boolean = false,
    val passwordError: String? = null,
    val passwordChangedMessage: String? = null,
    val morningReminderHour: Int = 7,
    val morningReminderMinute: Int = 0,
    val eveningReminderHour: Int = 18,
    val eveningReminderMinute: Int = 0,
)

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val profileRepository: ProfileRepository,
    private val cowRepository: CowRepository,
    private val notificationSettingsRepository: NotificationSettingsRepository,
    private val reminderScheduler: ReminderScheduler,
) : ViewModel() {

    private val _uiState = MutableStateFlow(
        DashboardUiState(
            email = authRepository.currentUserEmail.orEmpty(),
            morningReminderHour = notificationSettingsRepository.getTime(ReminderSession.MORNING).hour,
            morningReminderMinute = notificationSettingsRepository.getTime(ReminderSession.MORNING).minute,
            eveningReminderHour = notificationSettingsRepository.getTime(ReminderSession.EVENING).hour,
            eveningReminderMinute = notificationSettingsRepository.getTime(ReminderSession.EVENING).minute,
        ),
    )
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch { profileRepository.refreshFromRemote() }
        viewModelScope.launch {
            profileRepository.observeProfile().collect { profile ->
                _uiState.update {
                    it.copy(
                        name = profile?.name.orEmpty(),
                        phone = profile?.phone.orEmpty(),
                        farmName = profile?.farmName.orEmpty(),
                    )
                }
            }
        }
        viewModelScope.launch {
            cowRepository.observeCows().collect { cows ->
                _uiState.update { it.copy(cowCount = cows.size) }
            }
        }
    }

    fun onNameChange(value: String) = _uiState.update { it.copy(name = value, profileSavedMessage = null) }
    fun onPhoneChange(value: String) = _uiState.update { it.copy(phone = value, profileSavedMessage = null) }
    fun onFarmNameChange(value: String) = _uiState.update { it.copy(farmName = value, profileSavedMessage = null) }

    fun saveProfile() {
        val state = _uiState.value
        _uiState.update { it.copy(isSavingProfile = true, profileSavedMessage = null) }
        viewModelScope.launch {
            profileRepository.saveProfile(state.name.trim(), state.phone.trim(), state.farmName.trim())
            _uiState.update { it.copy(isSavingProfile = false, profileSavedMessage = "जतन झाले") }
        }
    }

    fun onNewPasswordChange(value: String) = _uiState.update { it.copy(newPassword = value, passwordError = null, passwordChangedMessage = null) }
    fun onConfirmPasswordChange(value: String) = _uiState.update { it.copy(confirmPassword = value, passwordError = null, passwordChangedMessage = null) }

    fun changePassword() {
        val state = _uiState.value
        if (state.newPassword.length < 6) {
            _uiState.update { it.copy(passwordError = "किमान ६ अक्षरे आवश्यक आहेत") }
            return
        }
        if (state.newPassword != state.confirmPassword) {
            _uiState.update { it.copy(passwordError = "पासवर्ड जुळत नाहीत") }
            return
        }
        _uiState.update { it.copy(isChangingPassword = true, passwordError = null) }
        viewModelScope.launch {
            runCatching { authRepository.updatePassword(state.newPassword) }
                .onSuccess {
                    _uiState.update {
                        it.copy(
                            isChangingPassword = false,
                            newPassword = "",
                            confirmPassword = "",
                            passwordChangedMessage = "पासवर्ड बदलला",
                        )
                    }
                }
                .onFailure { e ->
                    _uiState.update {
                        it.copy(isChangingPassword = false, passwordError = e.message ?: "पासवर्ड बदलता आला नाही")
                    }
                }
        }
    }

    fun signOut(onDone: () -> Unit) {
        viewModelScope.launch {
            authRepository.signOut()
            onDone()
        }
    }

    fun setMorningReminderTime(hour: Int, minute: Int) {
        notificationSettingsRepository.setTime(ReminderSession.MORNING, hour, minute)
        reminderScheduler.reschedule(ReminderSession.MORNING)
        _uiState.update { it.copy(morningReminderHour = hour, morningReminderMinute = minute) }
    }

    fun setEveningReminderTime(hour: Int, minute: Int) {
        notificationSettingsRepository.setTime(ReminderSession.EVENING, hour, minute)
        reminderScheduler.reschedule(ReminderSession.EVENING)
        _uiState.update { it.copy(eveningReminderHour = hour, eveningReminderMinute = minute) }
    }
}
