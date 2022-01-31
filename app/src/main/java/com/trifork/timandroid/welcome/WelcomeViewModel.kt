package com.trifork.timandroid.welcome

import android.content.Intent
import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import com.trifork.timandroid.helpers.BaseViewModel
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class WelcomeViewModel @Inject constructor() : BaseViewModel<WelcomeViewModel.WelcomeEvent>() {

    sealed class WelcomeEvent : BaseViewModel.ViewModelEvent() {
        class ResultLauncher(val intent: Intent) : WelcomeEvent()
        object ResultLauncherError : WelcomeEvent()

        object HandledLoginResult : WelcomeEvent()
        object HandledLoginResultFailure : WelcomeEvent()
    }

    fun getLoginIntent() = sendEventOnChannel {
        viewModelScope.async {
            val intentResult = TIM.auth.getOpenIDConnectLoginIntent(this).await()
            when (intentResult) {
                is TIMResult.Success -> {
                    WelcomeEvent.ResultLauncher(intentResult.value)
                }
                is TIMResult.Failure -> {
                    WelcomeEvent.ResultLauncherError
                }
            }
        }
    }

    fun handleLoginIntent(intent: Intent) = sendEventOnChannel {
        viewModelScope.async {
            val loginResult = TIM.auth.handleOpenIDConnectLoginResult(this, intent).await()

            when (loginResult) {
                is TIMResult.Success -> WelcomeEvent.HandledLoginResult
                is TIMResult.Failure -> WelcomeEvent.HandledLoginResultFailure
            }
        }
    }
}