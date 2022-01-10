package com.trifork.timandroid.biometricSettings

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import com.trifork.timandroid.biometric.TIMAuthenticationStatusEnum
import com.trifork.timandroid.biometric.status
import com.trifork.timandroid.createnewpincode.CreateNewPinCodeViewModel
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BiometricSettingsViewModel @Inject constructor() : ViewModel() {

    private val TAG = "BiometricSettingsViewModel"

    sealed class Event {
        object NavigateToWelcome : Event()
        object BiometricSuccess: Event()
        object BiometricNoneEnrolled: Event()
        object BiometricSecurityUpdateRequired: Event()
        object BiometricUnavailable: Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _loading = MutableStateFlow(false)
    private val _userId = MutableStateFlow("")
    private val _pinCode = MutableStateFlow("")

    fun onUserIdChange(userId: String) {
        _userId.value = userId
    }

    fun onPinChange(text: String) {
        _pinCode.value = text
    }

    val isSubmitEnabled: Flow<Boolean> = _pinCode.transform {pinCode ->
        val isPasswordCorrect = pinCode.length >= 4
        emit(isPasswordCorrect)
    }

    fun storeRefreshTokenWithBiometric(fragment: Fragment) = viewModelScope.launch {
        _loading.value = true
        val result = TIM.storage.enableBiometricAccessForRefreshToken(this, _pinCode.value, _userId.value, fragment).await()

        when (result) {
            is TIMResult.Failure -> Log.d(TAG, result.error.toString())
            is TIMResult.Success -> eventChannel.send(Event.NavigateToWelcome)
        }
        _loading.value = false
    }

    fun determineBiometricAuthentication(context: Context) = viewModelScope.launch {
        val status = TIM.hasBiometricCapability(context)

        when (status.status) {
            TIMAuthenticationStatusEnum.BIOMETRIC_SUCCESS -> {
                Log.d(TAG, "BIOMETRIC_SUCCESS, we can use biometric")
                eventChannel.send(Event.BiometricSuccess)
            }
            TIMAuthenticationStatusEnum.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Log.d(TAG, "BIOMETRIC_ERROR_NONE_ENROLLED, ask the the user to enroll")
                eventChannel.send(Event.BiometricNoneEnrolled)
            }
            TIMAuthenticationStatusEnum.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Log.d(TAG, "BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED, ask the the user to update")
                eventChannel.send(Event.BiometricSecurityUpdateRequired)
            }
            else -> {
                Log.d(TAG, "${status.status}, biometric authentication is not available")
                eventChannel.send(Event.BiometricUnavailable)
            }
        }
    }
}