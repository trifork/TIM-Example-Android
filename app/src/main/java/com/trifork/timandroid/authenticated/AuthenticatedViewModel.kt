package com.trifork.timandroid.authenticated

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.trifork.timandroid.TIM
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthenticatedViewModel @Inject constructor(
    val tim: TIM,
    val scope: CoroutineScope
) : ViewModel() {

    sealed class Event {
        object NavigateToWelcome : Event()
        object NavigateToAccessToken : Event()
        object NavigateToRefreshToken : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _userId = MutableStateFlow("")

    fun onUserIdChange(text: String) {
        _userId.value = text
    }

    fun logout() = viewModelScope.launch {
        tim.auth.logout()
        eventChannel.send(Event.NavigateToWelcome)
    }

    fun deleteUser() = viewModelScope.launch {
        tim.storage.clear(_userId.value)
        eventChannel.send(Event.NavigateToWelcome)
    }
}