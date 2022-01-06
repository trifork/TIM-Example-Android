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
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment : Fragment(), UserLoginAdapter.UserLoginAdapterClickListener {

    @Inject
    lateinit var viewModel: WelcomeViewModel

    private val userLoginAdapter = UserLoginAdapter(this)

    private var binding: FragmentWelcomeBinding? = null

    private val TAG = "WelcomeFragment"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        binding?.buttonLogin?.setOnClickListener {
            login()
        }

        userLoginAdapter.userLogins = TIM.storage.availableUserIds.toMutableList()

        binding?.recyclerView?.adapter = userLoginAdapter
        binding?.recyclerView?.layoutManager = LinearLayoutManager(context)

        return binding?.root
    }

    private fun login() {
        lifecycleScope.launch {
            val intentResult = TIM.auth.getOpenIDConnectLoginIntent(this).await()
            when (intentResult) {
                is TIMResult.Success -> resultLauncher.launch(intentResult.value)
                is TIMResult.Failure -> Toast.makeText(activity, "Failed to get intentResult", Toast.LENGTH_LONG).show()
            }
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            if (data != null) {
                lifecycleScope.launch {
                    val loginResult = TIM.auth.handleOpenIDConnectLoginResult(this, data).await()

                    when (loginResult) {
                        is TIMResult.Success -> navigateToCreateNewPinCodeFragment()
                        is TIMResult.Failure -> Toast.makeText(activity, "Failed to handle loginResult", Toast.LENGTH_LONG).show()
                    }
                }
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