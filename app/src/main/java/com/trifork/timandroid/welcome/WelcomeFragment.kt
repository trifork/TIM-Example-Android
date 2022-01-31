package com.trifork.timandroid.welcome

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.trifork.timandroid.R
import com.trifork.timandroid.TIM
import com.trifork.timandroid.databinding.FragmentWelcomeBinding
import com.trifork.timandroid.util.AuthenticatedUsers
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment : Fragment(), UserLoginAdapter.UserLoginAdapterClickListener {

    @Inject
    lateinit var viewModel: WelcomeViewModel

    @Inject
    lateinit var authenticatedUsers: AuthenticatedUsers

    private var userLoginAdapter: UserLoginAdapter? = null

    private var binding: FragmentWelcomeBinding? = null

    private val TAG = "WelcomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        userLoginAdapter = UserLoginAdapter(this, authenticatedUsers)

        userLoginAdapter?.userLogins = TIM.storage.availableUserIds.toList()

        binding?.recyclerView?.adapter = userLoginAdapter
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)

        initializeListeners()

        return binding?.root
    }

    private fun initializeListeners() {
        binding?.buttonLogin?.setOnClickListener {
            viewModel.getLoginIntent()
        }

        viewModel.subscribeToChannel(viewLifecycleOwner) {
            when (it) {
                is WelcomeViewModel.WelcomeEvent.ResultLauncher -> {
                    resultLauncher.launch(it.intent)
                }
                WelcomeViewModel.WelcomeEvent.ResultLauncherError -> {
                    Toast.makeText(activity, "Failed to create login intent", Toast.LENGTH_LONG).show()
                }
                WelcomeViewModel.WelcomeEvent.HandledLoginResult -> {
                    navigateToCreateNewPinCodeFragment()
                }
                WelcomeViewModel.WelcomeEvent.HandledLoginResultFailure -> {
                    Toast.makeText(activity, "Failed to handle login result", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private val resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val dataIntent: Intent? = result.data
            if (dataIntent != null) {
                //Send the received intent to TIM using our view model
                viewModel.handleLoginIntent(dataIntent)
            }
        }
    }

    private fun navigateToCreateNewPinCodeFragment() {
        lifecycleScope.launchWhenResumed {
            findNavController().navigate(R.id.action_fragment_welcome_to_fragment_create_new_pin_code, null)
        }
    }

    private fun navigateToLoginFragment(userId: String) {
        lifecycleScope.launchWhenResumed {
            val action = WelcomeFragmentDirections.actionFragmentWelcomeToFragmentLogin(userId)
            findNavController().navigate(action)
        }
    }

    override fun userLoginClick(item: String) {
        navigateToLoginFragment(item)
    }
}