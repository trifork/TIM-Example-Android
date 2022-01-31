package com.trifork.timandroid.biometricSettings

import android.content.Context
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import com.trifork.timandroid.biometric.TIMAuthenticationStatus
import com.trifork.timandroid.biometric.status
import com.trifork.timandroid.helpers.BaseViewModel
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class BiometricSettingsViewModel @Inject constructor() : BaseViewModel<BiometricSettingsViewModel.Event>() {

    private val TAG = "BiometricSettingsViewModel"

    sealed class Event : ViewModelEvent() {
        object NavigateToWelcome : Event()
        object BiometricSuccess : Event()
        object BiometricNoneEnrolled : Event()
        object BiometricSecurityUpdateRequired : Event()
        object BiometricUnavailable : Event()
        class BiometricFailed(val error: Throwable) : Event()
    }

    private val _userId = MutableStateFlow("")
    private val _pinCode = MutableStateFlow<String?>(null)
    private var _requirePinCode = MutableStateFlow(true)

    fun onUserIdChange(userId: String) {
        _userId.value = userId
    }

    fun requirePinCode(boolean: Boolean) {
        _requirePinCode.value = boolean
    }

    fun onPinChange(text: String?) {
        _pinCode.value = text
    }

    fun requirePinCode(): Boolean {
        return _requirePinCode.value
    }

    val isSubmitEnabled: Flow<Boolean> = _pinCode.map {
        it != null && it.length >= 4
    }

    fun storeRefreshTokenWithBiometric(fragment: Fragment) = sendEventOnChannel {
        viewModelScope.async {
            val pinCode = _pinCode.value
            if (pinCode == null) {
                Log.d(TAG, "No pin code set")
                return@async Event.BiometricFailed(Throwable("No pin code set"))
            }

            val result = TIM.storage.enableBiometricAccessForRefreshToken(this, pinCode, _userId.value, fragment).await()

            when (result) {
                is TIMResult.Failure -> Event.BiometricFailed(result.error)
                is TIMResult.Success -> Event.NavigateToWelcome
            }
        }
    }

    fun determineBiometricAuthentication(context: Context) = sendEventOnChannel {
        viewModelScope.async {
            val status = TIM.hasBiometricCapability(context)

            when (status.status) {
                TIMAuthenticationStatus.BIOMETRIC_SUCCESS -> {
                    Log.d(TAG, "BIOMETRIC_SUCCESS, we can use biometric")
                    Event.BiometricSuccess
                }
                TIMAuthenticationStatus.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                    Log.d(TAG, "BIOMETRIC_ERROR_NONE_ENROLLED, ask the the user to enroll")
                    Event.BiometricNoneEnrolled
                }
                TIMAuthenticationStatus.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                    Log.d(TAG, "BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED, ask the the user to update")
                    Event.BiometricSecurityUpdateRequired
                }
                else -> {
                    Log.d(TAG, "${status.status}, biometric authentication is not available")
                    Event.BiometricUnavailable
                }
            }
        }
    }
}