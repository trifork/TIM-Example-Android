package com.trifork.timandroid.util

import android.content.Context
import android.net.Uri
import com.trifork.timandroid.TIM
import com.trifork.timandroid.models.TIMConfiguration
import com.trifork.timandroid.models.openid.OIDScopeOpenID
import com.trifork.timandroid.models.openid.OIDScopeProfile
import java.net.URL


//TODO(Can we utilize di better?)
fun configureTIM(context: Context) : TIM {

    val timConfiguration = TIMConfiguration(
        URL("https://oidc-test.hosted.trifork.com"),
        "dev",
        "test_mock",
        Uri.parse("test:/"),
        listOf(OIDScopeOpenID, OIDScopeProfile)
    )

    TIM.configure(timConfiguration, context)

    return TIM
}

