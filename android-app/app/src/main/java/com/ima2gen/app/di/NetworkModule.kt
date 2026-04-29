package com.ima2gen.app.di

import com.ima2gen.app.data.api.OpenAiApi
import com.ima2gen.app.data.local.SecureKeyStore
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideAuthInterceptor(secureKeyStore: SecureKeyStore): Interceptor =
        Interceptor { chain ->
            val originalRequest = chain.request()
            val apiKey = secureKeyStore.getApiKey()
            val request = if (apiKey != null) {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $apiKey")
                    .build()
            } else {
                originalRequest
            }
            chain.proceed(request)
        }

    @Provides
    @Singleton
    fun provideOkHttpClient(authInterceptor: Interceptor): OkHttpClient {
        val logging = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)
            .addInterceptor(logging)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        okHttpClient: OkHttpClient,
        moshi: Moshi,
    ): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.openai.com/v1/")
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    @Singleton
    fun provideOpenAiApi(retrofit: Retrofit): OpenAiApi =
        retrofit.create(OpenAiApi::class.java)

    @Provides
    @Singleton
    fun provideIma2GenApi(retrofit: Retrofit): com.ima2gen.app.data.api.Ima2GenApi =
        retrofit.create(com.ima2gen.app.data.api.Ima2GenApi::class.java)
}
