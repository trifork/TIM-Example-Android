package com.trifork.timandroid.biometricSettings

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.trifork.timandroid.R
import com.trifork.timandroid.TIM
import com.trifork.timandroid.databinding.FragmentBiometricSettingsBinding
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class BiometricSettingsFragment : Fragment() {

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

        initListeners()

        viewModel.onUserIdChange(args.userId)
        viewModel.onPinChange(args.pinCode)

        return binding?.root
    }

    override fun onResume() {
        super.onResume()
        determineBiometricAuthentication()
    }

    private fun initListeners() {
        binding?.buttonAvailableEnable?.setOnClickListener {
            viewModel.storeRefreshTokenWithBiometric( this)
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

        viewModel.eventsFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when (it) {
                    BiometricSettingsViewModel.Event.NavigateToWelcome -> navigateToWelcomeScreen()
                    BiometricSettingsViewModel.Event.BiometricSuccess -> {
                        binding?.groupBiometricAvailable?.visibility = viewVisibility(true)
                        binding?.groupBiometricUnavailable?.visibility = viewVisibility(false)
                        binding?.groupBiometricNotSetup?.visibility = viewVisibility(false)
                    }
                    BiometricSettingsViewModel.Event.BiometricNoneEnrolled -> {
                        binding?.groupBiometricAvailable?.visibility = viewVisibility(false)
                        binding?.groupBiometricUnavailable?.visibility = viewVisibility(false)
                        binding?.groupBiometricNotSetup?.visibility = viewVisibility(true)
                    }
                    BiometricSettingsViewModel.Event.BiometricSecurityUpdateRequired -> {
                        binding?.groupBiometricAvailable?.visibility = viewVisibility(false)
                        binding?.groupBiometricUnavailable?.visibility = viewVisibility(false)
                        binding?.groupBiometricNotSetup?.visibility = viewVisibility(true)
                    }
                    BiometricSettingsViewModel.Event.BiometricUnavailable -> {
                        binding?.groupBiometricAvailable?.visibility = viewVisibility(false)
                        binding?.groupBiometricUnavailable?.visibility = viewVisibility(true)
                        binding?.groupBiometricNotSetup?.visibility = viewVisibility(false)
                    }
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
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

    private fun viewVisibility(visible: Boolean) = if (visible) View.VISIBLE else View.GONE
}