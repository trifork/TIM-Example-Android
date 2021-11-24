package com.trifork.timandroid.di

import com.trifork.timandroid.createnewpincode.CreateNewPinCodeViewModel
import com.trifork.timandroid.welcome.WelcomeViewModel
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
class ViewModelModule {

    @Provides
    fun provideCreateNewPinCodeViewModel(): CreateNewPinCodeViewModel = CreateNewPinCodeViewModel()

    @Provides
    fun provideWelcomeViewModel(): WelcomeViewModel = WelcomeViewModel()
}