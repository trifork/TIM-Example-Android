package com.trifork.timandroid.helpers

import android.widget.Toast
import androidx.fragment.app.Fragment

open class BaseFragment : Fragment() {
    internal fun showError(resourceId: Int) {
        Toast.makeText(requireContext(), getString(resourceId), Toast.LENGTH_LONG).show()
    }
}