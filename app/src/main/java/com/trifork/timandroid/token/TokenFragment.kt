package com.trifork.timandroid.token

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.trifork.timandroid.R
import com.trifork.timandroid.TIM
import com.trifork.timandroid.databinding.FragmentTokenBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

enum class TokenType {
    Access,
    Refresh
}

@AndroidEntryPoint
class TokenFragment : Fragment() {

    private var binding: FragmentTokenBinding? = null

    @Inject
    lateinit var viewModel: TokenViewModel

    val args: TokenFragmentArgs by navArgs()

    //TODO(Can we utilize di better?)
    @Inject
    lateinit var tim: TIM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTokenBinding.inflate(inflater, container, false)

        binding?.textViewTitleToken?.text = when (args.tokenType) {
            TokenType.Access -> context?.getString(R.string.fragment_token_text_view_title_access_token)
            TokenType.Refresh -> context?.getString(R.string.fragment_token_text_view_title_refresh_token)
        }

        viewModel.setTokenType(args.tokenType)

        lifecycleScope.launch {
            viewModel.jwtToken.collect {
                binding?.textViewToken?.text = it?.token
                binding?.textViewExpiration?.text = it?.expire.toString()
            }
        }

        return binding?.root
    }
}