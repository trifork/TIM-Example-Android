package com.trifork.timandroid.di

import com.trifork.timandroid.authenticated.AuthenticatedViewModel
import com.trifork.timandroid.biometricSettings.BiometricSettingsViewModel
import com.trifork.timandroid.createnewpincode.CreateNewPinCodeViewModel
import com.trifork.timandroid.login.LoginViewModel
import com.trifork.timandroid.token.TokenViewModel
import com.trifork.timandroid.util.AuthenticatedUsers
import com.trifork.timandroid.welcome.WelcomeViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ViewModelModule {

    @Provides
    fun provideCreateNewPinCodeViewModel(authenticatedUsers: AuthenticatedUsers): CreateNewPinCodeViewModel = CreateNewPinCodeViewModel(authenticatedUsers)

    @Provides
    fun provideLoginViewModel(): LoginViewModel = LoginViewModel()

    @Provides
    fun provideWelcomeViewModel(): WelcomeViewModel = WelcomeViewModel()

    @Provides
    fun provideAuthenticatedViewModel(authenticatedUsers: AuthenticatedUsers): AuthenticatedViewModel = AuthenticatedViewModel(authenticatedUsers)

    @Provides
    fun provideTokenViewModel(): TokenViewModel = TokenViewModel()

    @Provides
    fun provideBiometricSettingsViewModel(): BiometricSettingsViewModel = BiometricSettingsViewModel()
}