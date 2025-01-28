package com.example.livefrontcodechallenge.di

import com.example.livefrontcodechallenge.repository.OmdbRepository
import com.example.livefrontcodechallenge.repository.OmdbRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
interface RepositoryModule {

    @Binds
    fun bindOmdbRepository(omdbRepositoryImpl: OmdbRepositoryImpl): OmdbRepository

}
