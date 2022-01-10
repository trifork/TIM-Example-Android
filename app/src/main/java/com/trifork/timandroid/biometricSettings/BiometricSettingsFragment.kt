package com.trifork.timandroid.biometricSettings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.trifork.timandroid.R
import com.trifork.timandroid.databinding.FragmentBiometricSettingsBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        viewModel.determineBiometricAuthentication(requireContext())

        return binding?.root
    }

    private fun initListeners() {
        binding?.buttonAvailableEnable?.setOnClickListener {
            viewModel.storeRefreshTokenWithBiometric( this)
        }

        binding?.buttonAvailableSkip?.addTextChangedListener {
            navigateToWelcomeScreen()
        }

        binding?.buttonUnavailableClose?.addTextChangedListener {
            navigateToWelcomeScreen()
        }

        viewModel.eventsFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when (it) {
                    BiometricSettingsViewModel.Event.BiometricNoneEnrolled -> hideBiometricViews(true)
                    BiometricSettingsViewModel.Event.BiometricSecurityUpdateRequired -> hideBiometricViews(false)
                    BiometricSettingsViewModel.Event.BiometricUnavailable -> hideBiometricViews(false)
                    BiometricSettingsViewModel.Event.BiometricSuccess -> {
                        hideBiometricViews(true)
                    }
                    BiometricSettingsViewModel.Event.NavigateToWelcome -> navigateToWelcomeScreen()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToWelcomeScreen() {
        lifecycleScope.launchWhenResumed {
            findNavController().navigate(R.id.action_fragment_biometric_settings_fragment_welcome, null)
        }
    }

    private fun available(visible: Boolean) = if (visible) View.VISIBLE else View.GONE

    private fun hideBiometricViews(biometricAvailable: Boolean) {
        binding?.groupBiometricAvailable?.visibility = available(biometricAvailable)
        binding?.groupBiometricUnavailable?.visibility = available(!biometricAvailable)
    }

}