package com.trifork.timandroid

import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"

    //TODO Can we somehow avoid calling this in our main activity? JHE (10.01.21)
    @Inject
    lateinit var tim: TIM

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        TIM.auth.enableBackgroundTimeout {
            Log.d(TAG, "Background timeout triggered")
            dialogBuilder().show()
        }
    }

    private fun dialogBuilder(): AlertDialog {
        val builder: AlertDialog.Builder = this.let {
            AlertDialog.Builder(it)
        }

        builder
            .setCancelable(false)
            .setTitle(R.string.dialog_fragment_title)
            .setMessage(R.string.dialog_fragment_message)
            .setPositiveButton(R.string.dialog_fragment_ok,
                DialogInterface.OnClickListener { _, _ ->
                    navigateToWelcomeFragment()
                })
        return builder.create()
    }

    //We use a global action defined in out nav graph file to navigate to the welcome screen
    private fun navigateToWelcomeFragment() {
        this.findNavController(R.id.nav_host_fragment).navigate(R.id.action_to_fragment_login, null)
    }

}