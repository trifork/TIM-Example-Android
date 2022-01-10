package com.trifork.timandroid.createnewpincode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.trifork.timandroid.R
import com.trifork.timandroid.TIM
import com.trifork.timandroid.authenticated.AuthenticatedFragmentDirections
import com.trifork.timandroid.databinding.FragmentCreateNewPinCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateNewPinCodeFragment : Fragment() {

    private var binding: FragmentCreateNewPinCodeBinding? = null

    @Inject
    lateinit var viewModel: CreateNewPinCodeViewModel

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateNewPinCodeBinding.inflate(inflater, container, false)

        initListeners()

        return binding?.root
    }

    private fun initListeners() {
        lifecycleScope.launch {
            viewModel.isSubmitEnabled.collect {
                binding?.buttonSave?.isEnabled = it
            }
        }

        binding?.buttonSave?.setOnClickListener {
            viewModel.storeRefreshToken()
        }

        binding?.textInputEditTextUserName?.addTextChangedListener {
            viewModel.onNameChange(it.toString())
        }

        binding?.textInputEditTextUserPin?.addTextChangedListener {
            viewModel.onPinChange(it.toString())
        }

        viewModel.eventsFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when(it) {
                    is CreateNewPinCodeViewModel.Event.NavigateToLogin -> navigateToLoginFragment()
                    is CreateNewPinCodeViewModel.Event.StoredRefreshToken -> navigateToBiometricSettingsFragment(it.userId, it.pinCode)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToLoginFragment() {
        lifecycleScope.launchWhenResumed {
            findNavController().navigate(R.id.action_fragment_create_new_pin_code_to_fragment_welcome, null)
        }
    }

    private fun navigateToBiometricSettingsFragment(userId: String, pinCode: String) {
        lifecycleScope.launchWhenResumed {
            val action = CreateNewPinCodeFragmentDirections.actionFragmentCreateNewPinCodeToFragmentBiometricSettings(userId, pinCode)
            findNavController().navigate(action)
        }
    }

}