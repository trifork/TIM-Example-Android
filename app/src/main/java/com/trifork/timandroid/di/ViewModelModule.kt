package com.trifork.timandroid.di

import com.trifork.timandroid.TIM
import com.trifork.timandroid.createnewpincode.CreateNewPinCodeViewModel
import com.trifork.timandroid.login.LoginViewModel
import com.trifork.timandroid.welcome.WelcomeViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.GlobalScope

@Module
@InstallIn(SingletonComponent::class)
class ViewModelModule {

    //TODO(Which scope are we supposed to use?)
    @Provides
    fun provideCreateNewPinCodeViewModel(tim: TIM): CreateNewPinCodeViewModel = CreateNewPinCodeViewModel(tim, GlobalScope)

    @Provides
    fun provideLoginViewModel(tim: TIM): LoginViewModel = LoginViewModel(tim, GlobalScope)

    @Provides
    fun provideWelcomeViewModel(): WelcomeViewModel = WelcomeViewModel()
}