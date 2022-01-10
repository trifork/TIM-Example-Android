package com.trifork.timandroid.createnewpincode

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
class CreateNewPinCodeViewModel @Inject constructor() : ViewModel() {

    sealed class Event {
        class StoredRefreshToken(val userId: String, val pinCode: String) : Event()
        object NavigateToLogin : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _loading = MutableStateFlow(false)
    private val _name = MutableStateFlow("")
    private val _pinCode = MutableStateFlow("")

    fun onNameChange(text: String) {
        _name.value = text
    }

    fun onPinChange(text: String) {
        _pinCode.value = text
    }

    val isSubmitEnabled: Flow<Boolean> = combine(_name, _pinCode) { name, pinCode ->
        val isPasswordCorrect = pinCode.length >= 4
        val isNameCorrect = name.isNotEmpty()
        return@combine isPasswordCorrect and isNameCorrect
    }

    fun storeRefreshToken() = viewModelScope.launch {
        //TODO(Save to shared preferences) TIM.auth.refreshToken.userId
        _loading.value = true
        val refreshToken = TIM.auth.getRefreshToken()
        if (refreshToken != null) {
            val storeResult = TIM.storage.storeRefreshTokenWithNewPassword(this, refreshToken, _pinCode.value).await()

            when (storeResult) {
                is TIMResult.Success -> eventChannel.send(Event.StoredRefreshToken(refreshToken.userId, _pinCode.value))
            }
        }
        _loading.value = false
    }
}