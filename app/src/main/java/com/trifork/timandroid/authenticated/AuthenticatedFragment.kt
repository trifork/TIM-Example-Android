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
import com.trifork.timandroid.login.LoginFragmentArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@AndroidEntryPoint
class AuthenticatedFragment : Fragment() {

    private var binding: FragmentAuthenticatedBinding? = null

    @Inject
    lateinit var viewModel: AuthenticatedViewModel

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

        initListeners()

        //TODO Add user name instead of just showing userId
        binding?.userName?.text = context?.getString(R.string.fragment_authenticated_text_view_user_name)?.replace("%1", args.userId)
        binding?.textViewUserId?.text = context?.getString(R.string.fragment_authenticated_text_view_user_id)?.replace("%1", args.userId)
        viewModel.onUserIdChange(args.userId)

        return binding?.root
    }

    private fun initListeners() {
        binding?.buttonAccessToken?.setOnClickListener {

        }

        binding?.buttonRefreshToken?.setOnClickListener {

        }

        binding?.buttonLogOut?.setOnClickListener {
            viewModel.logout()
        }

        binding?.buttonDelete?.setOnClickListener {
            viewModel.deleteUser()
        }

        viewModel.eventsFlow
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach {
                when (it) {
                    is AuthenticatedViewModel.Event.NavigateToWelcome -> navigateToWelcomeFragment()
                }
            }
            .launchIn(viewLifecycleOwner.lifecycleScope)
    }

    private fun navigateToWelcomeFragment() {
        lifecycleScope.launchWhenResumed {
            findNavController().navigate(R.id.action_fragment_authenticated_to_fragment_welcome, null)
        }
    }


}