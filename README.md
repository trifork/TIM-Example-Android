# TIM-Example-Android


Following dependencies
implementation 'org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.1'
implementation 'androidx.lifecycle:lifecycle-runtime-ktx:2.4.0'
implementation "androidx.browser:browser:1.4.0"


BuildConfig
android.defaultConfig.manifestPlaceholders = ['appAuthRedirectScheme': 'test']


Manifest
<!-- App Auth -->
<activity
    android:exported="true"
    android:name="net.openid.appauth.RedirectUriReceiverActivity">
    <intent-filter>
        <action android:name="android.intent.action.VIEW"/>
        <category android:name="android.intent.category.DEFAULT"/>
        <category android:name="android.intent.category.BROWSABLE"/>
        <data android:scheme="${appAuthRedirectScheme}"/>
    </intent-filter>
</activity>

