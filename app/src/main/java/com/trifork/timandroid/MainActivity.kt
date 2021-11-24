package com.trifork.timandroid

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.trifork.timandroid.models.TIMConfiguration
import com.trifork.timandroid.models.openid.OIDScopeOpenID
import com.trifork.timandroid.models.openid.OIDScopeProfile
import com.trifork.timencryptedstorage.models.TIMResult
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.net.URL

class MainActivity : AppCompatActivity() {

    private val RC_AUTH = 1
    private val TAG = "MainActivity"

    //TODO(Which scope should we use?)
    private val scope = GlobalScope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val timConfiguration = TIMConfiguration(
            URL("https://oidc-test.hosted.trifork.com"),
            "dev",
            "test_mock",
            Uri.parse("test:/"),
            listOf(OIDScopeOpenID, OIDScopeProfile)
        )

        TIM.configure(timConfiguration, this)
        Log.d(TAG, "${TIM.storage.availableUserIds.size}")

        lifecycleScope.launch {
            val intentResult = TIM.auth.getOpenIDConnectLoginIntent(scope).await()

            when (intentResult) {
                is TIMResult.Success -> startActivityForResult(intentResult.value, RC_AUTH)
                is TIMResult.Failure -> Toast.makeText(applicationContext, "Failed to get intentResult", Toast.LENGTH_LONG).show()
            }

        }

        //TIM.auth.handleOpenIDConnectLoginResult(GlobalScope, intent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_AUTH) {
            Log.d(TAG, "resultCode $resultCode")

            if(data != null) {
                lifecycleScope.launch {
                    val loginResult = TIM.auth.handleOpenIDConnectLoginResult(scope, data).await()

                    when (loginResult) {
                        is TIMResult.Success -> Toast.makeText(applicationContext, "Successfully logged in", Toast.LENGTH_LONG).show()
                        is TIMResult.Failure -> Toast.makeText(applicationContext, "Failed to handle loginResult", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    fun onReceivedLoginToken()

}