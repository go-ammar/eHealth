package com.project.projecte_health.di

import com.google.gson.GsonBuilder
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Singleton
    @Provides
    fun provideOkHttpClient() = run {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .build()
    }

    @Singleton
    @Provides
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .addConverterFactory(
            GsonConverterFactory.create(
            GsonBuilder()
                .serializeNulls()
                .setLenient()
                .create()
        ))
        .baseUrl("https://test.com/")
        .client(okHttpClient)
        .build()

//    @Provides
//    @Singleton
//    fun provideApiService(retrofit: Retrofit): ApiNetworkService =
//        retrofit.create(ApiNetworkService::class.java)

//    @Provides
//    @Singleton
//    fun provideUserDetailsService(retrofit: Retrofit): UserDetailsService =
//        retrofit.create(UserDetailsService::class.java)

}