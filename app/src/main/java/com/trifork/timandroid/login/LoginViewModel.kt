package com.trifork.timandroid.login

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : ViewModel() {

    private val TAG = "LoginViewModel"

    sealed class Event {
        object NavigateToMain : Event()
        object NavigateToAuthenticated : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _pinCode = MutableStateFlow("")
    private val _userId = MutableStateFlow("")

    fun onPinChange(text: String) {
        _pinCode.value = text
    }

    fun onUserIdChange(text: String) {
        _userId.value = text
    }

    val userId: StateFlow<String>
        get() = _userId

    val isSubmitEnabled: Flow<Boolean> = _pinCode.map {
        it.length >= 4
    }

    fun login() = viewModelScope.launch {
        val result = TIM.auth.loginWithPassword(this, _userId.value, _pinCode.value, true).await()

        when (result) {
            is TIMResult.Failure -> Log.d(TAG, result.error.toString())
            is TIMResult.Success -> eventChannel.send(Event.NavigateToAuthenticated)
        }
    }

    fun biometricLogin(fragment: Fragment) = viewModelScope.launch {
        val result = TIM.auth.loginWithBiometricId(this, _userId.value, fragment = fragment).await()

        when (result) {
            is TIMResult.Failure -> Log.d(TAG, result.error.toString())
            is TIMResult.Success -> eventChannel.send(Event.NavigateToAuthenticated)
        }
    }

}