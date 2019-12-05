package brigitte.di.koin.module

import brigitte.Json
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module
import org.slf4j.Logger
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.jackson.JacksonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Created by <a href="mailto:aucd29@gmail.com">Burke Choi</a> on 2019-05-29 <p/>
 */

val okhttpModule = module {
    single {
        val logInterceptor = get<HttpLoggingInterceptor>()
        logInterceptor.level = get<HttpLoggingInterceptor.Level>()

        OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(logInterceptor)
            .build()
    }
    single {
        HttpLoggingInterceptor(HttpLoggingInterceptor.Logger {
            val logger = get<Logger>()
            if (logger.isDebugEnabled) {
                logger.debug(it)
            }
        })
    }
    single { RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()) }
    single { JacksonConverterFactory.create(Json.mapper) }
    single { ScalarsConverterFactory.create() }

    factory {
        Retrofit.Builder()
            .addCallAdapterFactory(get<RxJava2CallAdapterFactory>())
            .addConverterFactory(get<ScalarsConverterFactory>())
            .addConverterFactory(get<JacksonConverterFactory>())
            .client(get<OkHttpClient>())
    }
}