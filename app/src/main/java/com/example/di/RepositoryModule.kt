package com.example.di

import com.example.data.repository.MosqueRepository
import com.example.data.repository.UserPreferencesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    fun provideMosqueRepository(): MosqueRepository = MosqueRepository

    @Provides
    fun provideUserPreferencesRepository(): UserPreferencesRepository = UserPreferencesRepository
}
