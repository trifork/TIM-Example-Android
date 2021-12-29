package com.trifork.timandroid.token

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import com.trifork.timandroid.helpers.JWT
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TokenViewModel @Inject constructor(
    val tim: TIM
) : ViewModel() {

    private val _tokenType = MutableStateFlow(TokenType.Access)

    fun setTokenType(tokenType: TokenType) {
        _tokenType.value = tokenType
        getToken()
    }

    private val _token = MutableStateFlow<JWT?>(null)

    val jwtToken: StateFlow<JWT?>
        get() = _token

    private fun getToken() = viewModelScope.launch {
        _token.value = when (_tokenType.value) {
            TokenType.Access -> {
                val accessTokenResult = tim.auth.accessToken(this).await()
                when (accessTokenResult) {
                    is TIMResult.Failure -> null //TODO Show error toast?
                    is TIMResult.Success -> accessTokenResult.value
                }
            }
            TokenType.Refresh -> tim.auth.getRefreshToken()
        }
    }


}