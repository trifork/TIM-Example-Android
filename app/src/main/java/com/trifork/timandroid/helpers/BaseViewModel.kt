package com.trifork.timandroid.helpers

import androidx.lifecycle.*
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

open class BaseViewModel<T : BaseViewModel.ViewModelEvent> : ViewModel() {

    open class ViewModelEvent

    private val eventChannel = Channel<T>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    internal val loading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = loading

    internal fun sendEventOnChannel(event : () -> Deferred<T>) = viewModelScope.launch {
        loading.value = true
        eventChannel.send(event().await())
        loading.value = false
    }

    fun subscribeToChannel(viewLifecycleOwner: LifecycleOwner, eventHandler: (event: T) -> Unit) = this.eventsFlow
        .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
        .onEach {
            eventHandler(it)
        }.launchIn(viewLifecycleOwner.lifecycleScope)
}