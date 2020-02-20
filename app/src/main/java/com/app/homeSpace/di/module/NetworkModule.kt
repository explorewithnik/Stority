package com.app.homeSpace.di.module


import com.app.homeSpace.BuildConfig
import com.app.homeSpace.helper.AppExecutors
import com.app.homeSpace.helper.Constants
import com.app.homeSpace.remoteUtils.LiveDataCallAdapterFactory
import com.app.homeSpace.remoteUtils.WebService
import com.app.homeSpace.remoteUtils.WebServiceHolder
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {

    @Provides
    fun provideExecutor() = AppExecutors()

    @Provides
    fun webServiceHolder() = WebServiceHolder.instance

    @Provides
    @Singleton
    fun provideGson() = Gson()

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(150, TimeUnit.SECONDS)
        .readTimeout(150, TimeUnit.SECONDS)
        .writeTimeout(150, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val original = chain.request()
            // add request headers
            val request = original.newBuilder()
                .header("api_key", "")
                .header("appName", Constants.APP_NAME)
                .header("empID", Constants.DUMMY_EMP_ID)
                .header("appID", "")
                .header("token", Constants.TOKEN)
                .header("version", "" + BuildConfig.VERSION_CODE)
                .header("User-Agent", System.getProperty("http.agent"))
                .build()
            chain.proceed(request)
        }.build()

    @Provides
    @Singleton
    fun provideRetrofit(gson: Gson, okHttpClient: OkHttpClient): Retrofit = Retrofit.Builder()
        .baseUrl(BuildConfig.primaryBaseUrl)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create(gson))
        .addCallAdapterFactory(LiveDataCallAdapterFactory())
        .build()

    @Provides
    @Singleton
    fun provideApiWebservice(restAdapter: Retrofit): WebService {
        val webService = restAdapter.create(WebService::class.java)
        WebServiceHolder.instance.setAPIService(webService)
        return webService
    }
}