package com.trifork.timandroid.authenticated

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.trifork.timandroid.R
import com.trifork.timandroid.TIM
import com.trifork.timandroid.databinding.FragmentAuthenticatedBinding
import com.trifork.timandroid.token.TokenType
import com.trifork.timandroid.util.AuthenticatedUsers
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class AuthenticatedFragment : Fragment() {

    private var binding: FragmentAuthenticatedBinding? = null

    @Inject
    lateinit var viewModel: AuthenticatedViewModel

    @Inject
    lateinit var authenticatedUsers: AuthenticatedUsers

    private val args: AuthenticatedFragmentArgs by navArgs()

    //TODO(Can we utilize di better?)
    @Inject
    lateinit var tim: TIM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAuthenticatedBinding.inflate(inflater, container, false)

        binding?.userName?.text = context?.getString(R.string.fragment_authenticated_text_view_user_name)?.replace("%1", authenticatedUsers.getName(args.userId) ?: args.userId)
        binding?.textViewUserId?.text = context?.getString(R.string.fragment_authenticated_text_view_user_id)?.replace("%1", args.userId)
        viewModel.onUserIdChange(args.userId)

        initListeners()

        return binding?.root
    }

    private fun initListeners() {
        binding?.buttonAccessToken?.setOnClickListener {
            navigateToTokenFragment(TokenType.Access)
        }

        binding?.buttonRefreshToken?.setOnClickListener {
            navigateToTokenFragment(TokenType.Refresh)
        }

        binding?.buttonLogOut?.setOnClickListener {
            viewModel.logout()
        }

        binding?.buttonDelete?.setOnClickListener {
            viewModel.deleteUser()
        }

        //Are we not already biometric authenticated? Then make it possible to do so
        if(!viewModel.biometricAuthentication()) {
            binding?.buttonBiometricSettings?.setOnClickListener {
                navigateToBiometricSettingsFragment(viewModel.getUserId())
            }
            binding?.textViewBiometricSettings?.text = context?.getString(R.string.fragment_authenticated_text_view_biometric_available_not_configured)
        }
        else {
            binding?.buttonBiometricSettings?.visibility = View.GONE
            if(viewModel.biometricAvailable(this.requireContext())) {
                binding?.textViewBiometricSettings?.text = context?.getString(R.string.fragment_authenticated_text_view_biometric_enabled)
            }
            else {
                binding?.textViewBiometricSettings?.text = context?.getString(R.string.fragment_authenticated_text_view_biometric_not_available)
            }
        }

        viewModel.eventsFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when (it) {
                    is AuthenticatedViewModel.Event.NavigateToWelcome -> navigateToWelcomeFragment()
                    is AuthenticatedViewModel.Event.NavigateToBiometricSettings -> navigateToBiometricSettingsFragment(it.userId)
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToWelcomeFragment() {
        lifecycleScope.launchWhenResumed {
            findNavController().navigate(R.id.action_fragment_authenticated_to_fragment_welcome, null)
        }
    }

    private fun navigateToTokenFragment(tokenType: TokenType) {
        lifecycleScope.launchWhenResumed {
            val action = AuthenticatedFragmentDirections.actionFragmentAuthenticatedToFragmentToken(tokenType)
            findNavController().navigate(action)
        }
    }

    private fun navigateToBiometricSettingsFragment(userId: String) {
        lifecycleScope.launchWhenResumed {
            val action = AuthenticatedFragmentDirections.actionFragmentAuthenticatedToFragmentBiometricSettings(userId)
            findNavController().navigate(action)
        }
    }
}