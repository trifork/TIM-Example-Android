package com.trifork.timandroid.di

import android.content.Context
import com.trifork.timandroid.TIM
import com.trifork.timandroid.util.configureTIM
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class TIMModule {

    @Provides
    @Singleton
    fun provideTIM(@ApplicationContext context: Context): TIM {
        return configureTIM(context)
    }

}