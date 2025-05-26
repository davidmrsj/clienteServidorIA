package com.example.aplicacion1

import android.util.Log
import com.example.aplicacion1.data.network.services.ApiService
import com.example.aplicacion1.data.network.services.ServicesDataSource
import com.example.aplicacion1.data.repository.ServicesRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
        return OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .connectTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(client: OkHttpClient): Retrofit {
        Log.d("ImageRepository", "Creando Retrofit")
        return Retrofit.Builder()
            .baseUrl("http://192.168.1.136:8000/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideApiService(retrofit: Retrofit): ApiService {
        Log.d("ImageRepository", "Creando Api service")
        return retrofit.create(ApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideServiceDataSource(apiService: ApiService): ServicesDataSource {
        Log.d("ImageRepository", "Creando servicedatasource")
        return ServicesDataSource(apiService)
    }

    @Provides
    @Singleton
    fun provideImageRepository(imageDataSource: ServicesDataSource): ServicesRepository {
        Log.d("ImageRepository", "Creando ImageRepository")
        return ServicesRepository(imageDataSource)
    }
}
