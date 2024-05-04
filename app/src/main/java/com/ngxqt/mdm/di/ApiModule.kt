package com.ngxqt.mdm.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.ngxqt.mdm.data.local.UserPreferences
import com.ngxqt.mdm.data.remote.ApiInterface
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "my_data_store")
@Module
@InstallIn(ViewModelComponent::class)
object ApiModule {
    @Provides
    @ViewModelScoped
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        String
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    @Provides
    @ViewModelScoped
    fun provideOkHttpClient(logging: HttpLoggingInterceptor): OkHttpClient{
        return OkHttpClient.Builder()
            .addInterceptor(logging)
            .addInterceptor(Interceptor { chain: Interceptor.Chain ->
                val request =
                    chain.request()
                        .newBuilder()
                        .header("Content-Type", "application/json")
                        .build()
                chain.proceed(request)
            })
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideUserPreferencesProvider(@ApplicationContext context: Context): UserPreferencesProvider {
        val userPreferences = UserPreferences(context)
        return UserPreferencesProviderImpl(userPreferences)
    }

    @Provides
    @ViewModelScoped
    fun provideRetrofit(
        client: OkHttpClient,
        userPreferencesProvider: UserPreferencesProvider
    ): Retrofit {
        val baseUrlFlow = userPreferencesProvider.getBaseUrl()
        var baseUrl = runBlocking { baseUrlFlow.first() }
        Log.d("provideRetrofit", "RUN HERE ${baseUrl}")
        if (baseUrl == null) baseUrl = "http://bvdemo.ibme.edu.vn"
        return Retrofit.Builder()
            .baseUrl(baseUrl)
            //.addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    @Provides
    @ViewModelScoped
    fun provideNewsApi(retrofit: Retrofit): ApiInterface {
        return retrofit.create(ApiInterface::class.java)
    }
}