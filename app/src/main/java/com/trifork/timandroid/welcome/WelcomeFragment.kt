package com.trifork.timandroid.welcome

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.trifork.timandroid.R
import com.trifork.timandroid.TIM
import com.trifork.timandroid.databinding.FragmentWelcomeBinding
import com.trifork.timencryptedstorage.models.TIMResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class WelcomeFragment : Fragment() {

    @Inject
    lateinit var viewModel: WelcomeViewModel

    private var binding: FragmentWelcomeBinding? = null

    //TODO(Which scope should we use?)
    private val scope = GlobalScope

    private val RC_AUTH = 1
    private val TAG = "WelcomeFragment"

    //TODO(Can we utilize di better?)
    @Inject
    lateinit var tim : TIM

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentWelcomeBinding.inflate(inflater, container, false)

        binding?.buttonLogin?.setOnClickListener {
            login()
        }

        return binding?.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_AUTH) {
            if(data != null) {
                lifecycleScope.launch {
                    val loginResult = tim.auth.handleOpenIDConnectLoginResult(scope, data).await()

                    when (loginResult) {
                        is TIMResult.Success -> navigateToCreateNewPinCodeFragment()
                        is TIMResult.Failure -> Toast.makeText(activity, "Failed to handle loginResult", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun login() {
        lifecycleScope.launch {
            val intentResult = tim.auth.getOpenIDConnectLoginIntent(scope).await()

            when (intentResult) {
                is TIMResult.Success -> startActivityForResult(intentResult.value, RC_AUTH)
                is TIMResult.Failure -> Toast.makeText(activity, "Failed to get intentResult", Toast.LENGTH_LONG).show()
            }

        }
    }

    private fun navigateToCreateNewPinCodeFragment() {
        lifecycleScope.launchWhenResumed {
            findNavController().navigate(R.id.action_fragment_welcome_to_fragment_create_new_pin_code, null)
        }
    }
}