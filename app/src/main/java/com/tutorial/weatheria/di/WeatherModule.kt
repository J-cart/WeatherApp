package com.tutorial.weatheria.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tutorial.weatheria.WeatherService
import com.tutorial.weatheria.arch.MainRepository
import com.tutorial.weatheria.arch.WeatherRepositoryImpl
import com.tutorial.weatheria.db.WeatherDao
import com.tutorial.weatheria.db.WeatherDataBase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WeatherModule {

    @Singleton
    @Provides
    fun getDao(db: WeatherDataBase): WeatherDao = db.weatherDao()

    @Singleton
    @Provides
    fun provideAppDatabase(@ApplicationContext appContext: Context) =
        Room.databaseBuilder(
            appContext,
            WeatherDataBase::class.java,
            "WEATHER_DATABASE"
        ).build()

    @Singleton
    @Provides
    fun getOkHttp(): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BASIC
        return OkHttpClient.Builder().addInterceptor(logger).build()
    }

    @Singleton
    @Provides
    fun getMainRepImpl(api: WeatherService):MainRepository = WeatherRepositoryImpl(api)



    @Singleton
    @Provides
    fun getRetrofit(http: OkHttpClient): WeatherService =
        Retrofit.Builder()
            .client(http)
            .baseUrl("http://api.weatherapi.com/v1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(WeatherService::class.java)

}