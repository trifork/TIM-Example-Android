package com.trifork.timandroid.createnewpincode

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class CreateNewPinCodeViewModel @Inject constructor() : ViewModel() {

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

}