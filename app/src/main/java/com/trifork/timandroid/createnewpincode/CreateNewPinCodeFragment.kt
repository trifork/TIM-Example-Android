package com.trifork.timandroid.createnewpincode

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.trifork.timandroid.TIM
import com.trifork.timandroid.databinding.FragmentCreateNewPinCodeBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class CreateNewPinCodeFragment : Fragment() {

    private var binding: FragmentCreateNewPinCodeBinding? = null

    @Inject
    lateinit var viewModel: CreateNewPinCodeViewModel

    //TODO(Can we utilize di better?)
    @Inject
    lateinit var tim : TIM

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCreateNewPinCodeBinding.inflate(inflater, container, false)

        initListeners()

        return binding?.root
    }

    private fun initListeners() {
        lifecycleScope.launch {
            viewModel?.isSubmitEnabled?.collect {
                binding?.buttonSave?.isEnabled = it
            }

            binding?.textInputEditTextUserName?.addTextChangedListener {
                viewModel?.onNameChange(it.toString())
            }

            binding?.textInputEditTextUserPin?.addTextChangedListener {
                viewModel?.onPinChange(it.toString())
            }
        }

    }

}