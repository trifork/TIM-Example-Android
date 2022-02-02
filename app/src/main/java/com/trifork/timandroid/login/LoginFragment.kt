package com.trifork.timandroid.login

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
import com.trifork.timandroid.TIM
import com.trifork.timandroid.databinding.FragmentLoginBinding
import com.trifork.timandroid.helpers.BaseFragment
import com.trifork.timandroid.util.AuthenticatedUsers
import com.trifork.timandroid.util.viewVisibility
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private var binding: FragmentLoginBinding? = null

    @Inject
    lateinit var viewModel: LoginViewModel

    @Inject
    lateinit var authenticatedUsers: AuthenticatedUsers

    val args: LoginFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        initListeners()

        binding?.textViewUser?.text = authenticatedUsers.getName(args.userId) ?: args.userId
        viewModel.onUserIdChange(args.userId)

        if (TIM.storage.hasBiometricAccessForRefreshToken(args.userId)) {
            viewModel.loginBiometric(this)
        }

        return binding?.root
    }

    private fun initListeners() {
        lifecycleScope.launch {
            viewModel.isLoading.collect {
                binding?.progressBarLoading?.visibility = viewVisibility(it)
                binding?.textInputEditTextUserPin?.isEnabled = !it
            }
        }

        lifecycleScope.launch {
            viewModel.isSubmitEnabled.collect {
                binding?.buttonLogin?.isEnabled = it
            }
        }

        binding?.buttonLogin?.setOnClickListener {
            viewModel.loginPassword()
        }

        binding?.textInputEditTextUserPin?.addTextChangedListener {
            viewModel.onPinChange(it.toString())
        }

        viewModel.subscribeToChannel(viewLifecycleOwner) {
            when (it) {
                is LoginViewModel.LoginEvent.NavigateToAuthenticated -> navigateToAuthenticated()
                LoginViewModel.LoginEvent.BiometricCanceled -> {
                    //Do nothing besides showing login view
                }
                is LoginViewModel.LoginEvent.BiometricFailedError -> {
                    showError(R.string.fragment_login_biometric_failed)
                }
                LoginViewModel.LoginEvent.KeyIsLocked -> {
                    showError(R.string.fragment_login_key_locked)
                }
                LoginViewModel.LoginEvent.KeyServiceFailed -> {
                    showError(R.string.fragment_login_key_server_error)
                }
                LoginViewModel.LoginEvent.WrongPassword -> {
                    showError(R.string.fragment_login_wrong_password)
                }
                LoginViewModel.LoginEvent.GenericError -> {
                    showError(R.string.fragment_login_generic_error)
                }
            }
        }
    }

    private fun navigateToAuthenticated() {
        lifecycleScope.launchWhenResumed {
            val action = LoginFragmentDirections.actionFragmentLoginToFragmentAuthenticated(viewModel.userId.value)
            findNavController().navigate(action)
        }
    }
}