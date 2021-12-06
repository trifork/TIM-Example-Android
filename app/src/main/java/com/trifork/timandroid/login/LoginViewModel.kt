package com.trifork.timandroid.login

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    val tim: TIM,
    val scope: CoroutineScope
) : ViewModel() {

    sealed class Event {
        object NavigateToMain : Event()
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

    val isSubmitEnabled: Flow<Boolean> =  _pinCode.map {
        it.length >=4
    }

    fun login() = viewModelScope.launch {

        val result = tim.auth.loginWithPassword(scope, _userId.value, _pinCode.value, true).await()

        Log.d("LoginViewModel", "result ${result.toString()}")

    }

}