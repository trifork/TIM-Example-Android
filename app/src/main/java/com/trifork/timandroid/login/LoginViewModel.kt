package com.trifork.timandroid.login

import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import com.trifork.timandroid.helpers.BaseViewModel
import com.trifork.timandroid.helpers.JWT
import com.trifork.timandroid.models.errors.TIMAuthError
import com.trifork.timandroid.models.errors.TIMError
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor() : BaseViewModel<LoginViewModel.LoginEvent>() {

    private val TAG = "LoginViewModel"

    sealed class LoginEvent : BaseViewModel.ViewModelEvent() {
        object WrongPassword : LoginEvent()
        object KeyIsLocked : LoginEvent()
        class BiometricFailedError(val throwable: Throwable) : LoginEvent()
        object BiometricCanceled : LoginEvent()
        object KeyServiceFailed : LoginEvent()
        object GenericError : LoginEvent()
        object NavigateToAuthenticated : LoginEvent()
    }

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

    val isSubmitEnabled: Flow<Boolean> = combine(_pinCode, loading) { pinCode, loading ->
        val isPasswordCorrect = pinCode.length >= 4
        return@combine isPasswordCorrect and !loading
    }

    fun loginPassword() = sendEventOnChannel {
        viewModelScope.async {
            val result = TIM.auth.loginWithPassword(this, _userId.value, _pinCode.value, true).await()
            handleLoginResult(result)
        }
    }

    fun loginBiometric(fragment: Fragment) = viewModelScope.launch {
        val result = TIM.auth.loginWithBiometricId(this, _userId.value, fragment = fragment).await()
        handleLoginResult(result)
    }

    private fun handleLoginResult(result: TIMResult<JWT, TIMError>): LoginEvent {
        return when (result) {
            is TIMResult.Failure -> {
                val error = result.error
                return when (error) {
                    is TIMError.Auth -> {
                        //TODO How do we want to use isRefreshTokenExpiredError
                        /*if (error.timAuthError.isRefreshTokenExpiredError()) {
                            // Refresh Token has expired.
                            LoginEvent.GenericError
                        }
                        else -> {*/
                        LoginEvent.GenericError
                    }
                    is TIMError.Storage -> {
                        if (error.timStorageError.isKeyLocked()) {
                            // Handle key locked (three wrong password logins)
                            LoginEvent.KeyIsLocked
                        } else if (error.timStorageError.isWrongPassword()) {
                            // Handle wrong password
                            LoginEvent.WrongPassword
                        } else if (error.timStorageError.isBiometricFailedError()) {
                            // Handle biometric failed error
                            LoginEvent.BiometricFailedError(error)
                        } else if (error.timStorageError.isBiometricCanceledError()) {
                            // Biometric canceled, do nothing
                            LoginEvent.BiometricCanceled
                        } else if (error.timStorageError.isKeyServiceError()) {
                            // Something went wrong while communicating with the key service (possible network failure)
                            LoginEvent.KeyServiceFailed
                        } else {
                            // Something failed - please try again.
                            LoginEvent.GenericError
                        }
                    }
                    else -> {
                        // Should not hit this. Something failed - please try again.
                        LoginEvent.GenericError
                    }
                }

            }
            is TIMResult.Success -> {
                // Successfully logged in
                LoginEvent.NavigateToAuthenticated
            }
        }
    }
}