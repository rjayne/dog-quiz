package au.com.jayne.dogquiz.common.dagger.modules

import android.content.Context
import au.com.jayne.dogquiz.BuildConfig
import au.com.jayne.dogquiz.common.util.ResourceProvider
import au.com.jayne.dogquiz.domain.repo.DogRepository
import au.com.jayne.dogquiz.domain.repo.DogRepositoryImpl
import au.com.jayne.dogquiz.domain.service.DogApiService
import au.com.jayne.dogquiz.domain.service.SimpleMoshiJsonParser
import dagger.Module
import dagger.Provides
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit
import javax.inject.Singleton


@Module
class NetworkModule {

    private val READ_TIMEOUT = 60L // 60 sec
    private val CONNECT_TIMEOUT = 60L
    private val WRITE_TIMEOUT = 60L
    private val HTTP_RESPONSE_DISK_CACHE_MAX_SIZE = 10 * 1024 * 1024.toLong() // 10 MiB

    private fun provideNetworkClient(context: Context): OkHttpClient {
        val logger = HttpLoggingInterceptor()
        logger.level = HttpLoggingInterceptor.Level.BODY

        val httpCacheDir = File(context.getCacheDir(), "HttpResponseCache")

        var builder = OkHttpClient.Builder()
            .addInterceptor(logger)
            .cache(Cache(httpCacheDir, HTTP_RESPONSE_DISK_CACHE_MAX_SIZE))
            .retryOnConnectionFailure(true)
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
            .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideDogApiService(context: Context): DogApiService {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.DOG_API_ENDPOINT)
            .addConverterFactory(MoshiConverterFactory.create(SimpleMoshiJsonParser.moshi))
            .client(provideNetworkClient(context))
            .build()
            .create(DogApiService::class.java)
    }

    @Provides
    @Singleton
    internal fun providesDogRepository(resourceProvider: ResourceProvider, dogApiService: DogApiService): DogRepository {
        return DogRepositoryImpl(resourceProvider, dogApiService)
    }

}
