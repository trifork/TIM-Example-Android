package com.trifork.timandroid.util

import android.content.Context
import android.net.Uri
import com.trifork.timandroid.TIM
import com.trifork.timandroid.biometric.TIMBiometricData
import com.trifork.timandroid.models.TIMConfiguration
import com.trifork.timandroid.models.openid.OIDScopeOpenID
import com.trifork.timandroid.models.openid.OIDScopeProfile
import java.net.URL

//TODO(Can we utilize di better?)
fun configureTIM(context: Context): TIM {

    val timConfiguration = TIMConfiguration(
        URL("https://oidc-test.hosted.trifork.com"),
        "dev",
        "test_mock",
        Uri.parse("test:/"),
        listOf(OIDScopeOpenID, OIDScopeProfile)
    )

    val timBiometricUtil = TIMBiometricData.Builder()
        .title("My title")
        .subtitle("My subtitle")
        .description("My description")
        .build()

    TIM.configure(timConfiguration, context = context, timBiometricUtil = timBiometricUtil)

    return TIM
}

