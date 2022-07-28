package com.mprusina.killingk.di

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.mprusina.killingk.data.api.ProfileService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    /**
     * Base URL for ProfileService API
     */
    private const val BASE_URL = "https://eu-west-1.aws.data.mongodb-api.com/"

    /**
     * Provides instance of ProfileService Retrofit client
     */
    @Singleton
    @Provides
    fun providesApiService(retrofit: Retrofit): ProfileService {
        return retrofit.create(ProfileService::class.java)
    }

    /**
     * Provides instance of Retrofit, for building client for ProfileService
     */
    @Singleton
    @Provides
    fun providesRetrofit(gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Provides instance of GSON, for Retrofit
     */
    @Singleton
    @Provides
    fun providesGson(): Gson = GsonBuilder().create()
}