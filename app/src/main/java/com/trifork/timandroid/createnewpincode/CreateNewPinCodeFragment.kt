package com.trifork.timandroid.createnewpincode

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.trifork.timandroid.R
import com.trifork.timandroid.databinding.FragmentCreateNewPinCodeBinding
import com.trifork.timandroid.helpers.BaseFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateNewPinCodeFragment : BaseFragment() {

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

        lifecycleScope.launch {
            viewModel.loading.collect {
                binding?.textInputEditTextUserName?.isEnabled = !it
                binding?.textInputLayoutUserPin?.isEnabled = !it
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

        viewModel.subscribeToChannel(viewLifecycleOwner) {
            when (it) {
                is CreateNewPinCodeViewModel.CreateNewPinCodeEvent.NavigateToLogin -> navigateToLoginFragment()
                is CreateNewPinCodeViewModel.CreateNewPinCodeEvent.StoredRefreshToken -> navigateToBiometricSettingsFragment(it.userId, it.pinCode)
                CreateNewPinCodeViewModel.CreateNewPinCodeEvent.NoTokenFailure -> showError(R.string.fragment_create_new_pin_code_no_refresh_token)
                CreateNewPinCodeViewModel.CreateNewPinCodeEvent.StoreRefreshTokenFailure -> showError(R.string.fragment_create_new_pin_code_could_not_store_refresh_token)
            }
        }
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