package com.trifork.timandroid.authenticated

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import com.trifork.timandroid.biometric.TIMAuthenticationStatus
import com.trifork.timandroid.biometric.status
import com.trifork.timandroid.util.AuthenticatedUsers
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticatedViewModel @Inject constructor(val authenticatedUsers: AuthenticatedUsers) : ViewModel() {

    sealed class Event {
        object NavigateToWelcome : Event()
        class NavigateToBiometricSettings(val userId: String) : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _userId = MutableStateFlow("")

    fun getUserId(): String {
        return _userId.value
    }

    fun biometricAuthentication() = TIM.storage.hasBiometricAccessForRefreshToken(_userId.value)

    fun biometricAvailable(context: Context) = TIM.hasBiometricCapability(context).status == TIMAuthenticationStatus.BIOMETRIC_SUCCESS

    fun onUserIdChange(text: String) {
        _userId.value = text
    }

    fun logout() = viewModelScope.launch {
        TIM.auth.logout()
        eventChannel.send(Event.NavigateToWelcome)
    }

    fun deleteUser() = viewModelScope.launch {
        TIM.storage.clear(_userId.value)
        authenticatedUsers.clear(_userId.value)
        eventChannel.send(Event.NavigateToWelcome)
    }
}