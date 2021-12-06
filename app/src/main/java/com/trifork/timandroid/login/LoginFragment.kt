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
import androidx.navigation.fragment.navArgs
import com.trifork.timandroid.TIM
import com.trifork.timandroid.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var binding: FragmentLoginBinding? = null

    @Inject
    lateinit var viewModel: LoginViewModel

    val args: LoginFragmentArgs by navArgs()

    //TODO(Can we utilize di better?)
    @Inject
    lateinit var tim: TIM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentLoginBinding.inflate(inflater, container, false)

        initListeners()

        binding?.textViewUser?.text = args.userId
        viewModel.onUserIdChange(args.userId)
        return binding?.root
    }

    private fun initListeners() {
        lifecycleScope.launch {
            viewModel.isSubmitEnabled.collect {
                binding?.buttonLogin?.isEnabled = it
            }
        }

        binding?.buttonLogin?.setOnClickListener {
            viewModel.login()
        }

        binding?.textInputEditTextUserPin?.addTextChangedListener {
            viewModel.onPinChange(it.toString())
        }

        viewModel.eventsFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when (it) {
                    is LoginViewModel.Event.NavigateToMain -> navigateToMain()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToMain() {
        lifecycleScope.launchWhenResumed {
            //findNavController().navigate(R.id.action_, null)
        }
    }
}