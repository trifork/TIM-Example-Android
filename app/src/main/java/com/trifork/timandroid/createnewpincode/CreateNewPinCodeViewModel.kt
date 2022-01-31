package com.trifork.timandroid.createnewpincode

import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import com.trifork.timandroid.helpers.BaseViewModel
import com.trifork.timandroid.util.AuthenticatedUsers
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CreateNewPinCodeViewModel @Inject constructor(val authenticatedUsers: AuthenticatedUsers) : BaseViewModel<CreateNewPinCodeViewModel.CreateNewPinCodeEvent>() {

    sealed class CreateNewPinCodeEvent: ViewModelEvent() {
        class StoredRefreshToken(val userId: String, val pinCode: String) : CreateNewPinCodeEvent()
        object NoTokenFailure : CreateNewPinCodeEvent()
        object StoreRefreshTokenFailure : CreateNewPinCodeEvent()
        object NavigateToLogin : CreateNewPinCodeEvent()
    }

    private val _name = MutableStateFlow("")
    private val _pinCode = MutableStateFlow("")

    fun onNameChange(text: String) {
        _name.value = text
    }

    fun onPinChange(text: String) {
        _pinCode.value = text
    }

    val isSubmitEnabled: Flow<Boolean> = combine(_name, _pinCode, loading) { name, pinCode, loading ->
        val isPasswordCorrect = pinCode.length >= 4
        val isNameCorrect = name.isNotEmpty()
        return@combine isPasswordCorrect and isNameCorrect and !loading
    }

    fun storeRefreshToken() = sendEventOnChannel {
        viewModelScope.async {
            val refreshToken = TIM.auth.getRefreshToken()
            if (refreshToken != null) {
                val storeResult = TIM.storage.storeRefreshTokenWithNewPassword(this, refreshToken, _pinCode.value).await()
                return@async when (storeResult) {
                    is TIMResult.Success -> {
                        //The user can now authenticate easily on next launch, we therefore add the user to the available users for easy access to login
                        authenticatedUsers.addAvailableUser(refreshToken.userId, _name.value)
                        CreateNewPinCodeEvent.StoredRefreshToken(refreshToken.userId, _pinCode.value)
                    }
                    is TIMResult.Failure -> CreateNewPinCodeEvent.StoreRefreshTokenFailure
                }
            }
            //Should not happen, the user should not be here if not already logged in
            return@async CreateNewPinCodeEvent.NoTokenFailure
        }
    }
}