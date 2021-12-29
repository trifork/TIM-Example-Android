package com.trifork.timandroid.di

import com.trifork.timandroid.TIM
import com.trifork.timandroid.authenticated.AuthenticatedViewModel
import com.trifork.timandroid.createnewpincode.CreateNewPinCodeViewModel
import com.trifork.timandroid.login.LoginViewModel
import com.trifork.timandroid.token.TokenViewModel
import com.trifork.timandroid.welcome.WelcomeViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ViewModelModule {

    @Provides
    fun provideCreateNewPinCodeViewModel(tim: TIM): CreateNewPinCodeViewModel = CreateNewPinCodeViewModel(tim)

    @Provides
    fun provideLoginViewModel(tim: TIM): LoginViewModel = LoginViewModel(tim)

    @Provides
    fun provideWelcomeViewModel(tim: TIM): WelcomeViewModel = WelcomeViewModel()

    @Provides
    fun provideAuthenticatedViewModel(tim: TIM): AuthenticatedViewModel = AuthenticatedViewModel(tim)

    @Provides
    fun provideTokenViewModel(tim: TIM): TokenViewModel = TokenViewModel(tim)
}