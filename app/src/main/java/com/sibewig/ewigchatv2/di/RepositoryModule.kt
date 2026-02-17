package com.sibewig.ewigchatv2.di

import com.sibewig.ewigchatv2.data.repository.AuthRepositoryImpl
import com.sibewig.ewigchatv2.data.repository.ChatRepositoryImpl
import com.sibewig.ewigchatv2.domain.repository.AuthRepository
import com.sibewig.ewigchatv2.domain.repository.ChatRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindAuthRepository(impl: AuthRepositoryImpl): AuthRepository

    @Binds
    @Singleton
    abstract fun bindChatRepository(impl: ChatRepositoryImpl): ChatRepository
}