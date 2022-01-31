package com.trifork.timandroid.biometricSettings

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.trifork.timandroid.R
import com.trifork.timandroid.TIM
import com.trifork.timandroid.databinding.FragmentBiometricSettingsBinding
import com.trifork.timandroid.helpers.BaseFragment
import com.trifork.timandroid.util.viewVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BiometricSettingsFragment : BaseFragment() {

    private var binding: FragmentBiometricSettingsBinding? = null

    @Inject
    lateinit var viewModel: BiometricSettingsViewModel

    val args: BiometricSettingsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentBiometricSettingsBinding.inflate(inflater, container, false)

        viewModel.onUserIdChange(args.userId)
        if(args.pinCode != null) {
            viewModel.onPinChange(args.pinCode)
            viewModel.requirePinCode(false)
        }

        initListeners()

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        determineBiometricAuthentication()
    }

    private fun initListeners() {
        binding?.buttonAvailableEnable?.setOnClickListener {
            viewModel.storeRefreshTokenWithBiometric(this)
        }

        binding?.buttonAvailableEnablePin?.setOnClickListener {
            viewModel.storeRefreshTokenWithBiometric(this)
        }

        binding?.buttonNotSetupEnable?.setOnClickListener {
            navigateToBiometricSettings()
        }

        binding?.buttonAvailableSkip?.setOnClickListener {
            navigateToWelcomeScreen()
        }

        binding?.buttonUnavailableClose?.setOnClickListener {
            navigateToWelcomeScreen()
        }

        binding?.buttonNotSetupSkip?.setOnClickListener {
            navigateToWelcomeScreen()
        }

        binding?.textInputEditTextPinCode?.addTextChangedListener {
            viewModel.onPinChange(it.toString())
        }

        lifecycleScope.launch {
            viewModel.isSubmitEnabled.collect {
                binding?.buttonAvailableEnablePin?.isEnabled = it
            }
        }

        viewModel.subscribeToChannel(viewLifecycleOwner) {
            when (it) {
                BiometricSettingsViewModel.Event.NavigateToWelcome -> {
                    if(viewModel.requirePinCode()) {
                        activity?.onBackPressed()
                    }
                    else {
                        navigateToWelcomeScreen()
                    }
                }
                BiometricSettingsViewModel.Event.BiometricSuccess -> {
                    if(viewModel.requirePinCode()) {
                        binding?.groupBiometricAvailable?.visibility = viewVisibility(false)
                        binding?.groupBiometricAvailableNoPin?.visibility = viewVisibility(true)
                    }
                    else {
                        binding?.groupBiometricAvailable?.visibility = viewVisibility(true)
                        binding?.groupBiometricAvailableNoPin?.visibility = viewVisibility(false)
                    }
                    binding?.groupBiometricUnavailable?.visibility = viewVisibility(false)
                    binding?.groupBiometricNotSetup?.visibility = viewVisibility(false)
                }
                BiometricSettingsViewModel.Event.BiometricNoneEnrolled -> {
                    binding?.groupBiometricAvailable?.visibility = viewVisibility(false)
                    binding?.groupBiometricUnavailable?.visibility = viewVisibility(false)
                    binding?.groupBiometricNotSetup?.visibility = viewVisibility(true)
                    binding?.groupBiometricAvailableNoPin?.visibility = viewVisibility(false)
                }
                BiometricSettingsViewModel.Event.BiometricSecurityUpdateRequired -> {
                    binding?.groupBiometricAvailable?.visibility = viewVisibility(false)
                    binding?.groupBiometricUnavailable?.visibility = viewVisibility(false)
                    binding?.groupBiometricNotSetup?.visibility = viewVisibility(true)
                    binding?.groupBiometricAvailableNoPin?.visibility = viewVisibility(false)
                }
                BiometricSettingsViewModel.Event.BiometricUnavailable -> {
                    binding?.groupBiometricAvailable?.visibility = viewVisibility(false)
                    binding?.groupBiometricUnavailable?.visibility = viewVisibility(true)
                    binding?.groupBiometricNotSetup?.visibility = viewVisibility(false)
                    binding?.groupBiometricAvailableNoPin?.visibility = viewVisibility(false)
                }
                is BiometricSettingsViewModel.Event.BiometricFailed -> {
                    showError(R.string.fragment_biometric_settings_toast_error)
                }
            }
        }
    }

    private fun navigateToBiometricSettings() {
        resultLauncher.launch(TIM.createBiometricSettingsIntent())
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            determineBiometricAuthentication()
        }
    }

    private fun determineBiometricAuthentication() {
        viewModel.determineBiometricAuthentication(requireContext())
    }

    private fun navigateToWelcomeScreen() {
        lifecycleScope.launchWhenResumed {
            findNavController().navigate(R.id.action_fragment_biometric_settings_fragment_welcome, null)
        }
    }
}