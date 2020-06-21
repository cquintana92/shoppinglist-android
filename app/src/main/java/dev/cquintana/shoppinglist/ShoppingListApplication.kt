package dev.cquintana.shoppinglist

import android.app.Application
import android.content.Context
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import timber.log.Timber

class ShoppingListApplication : Application() {
    companion object {
        private lateinit var globalApplication: ShoppingListApplication
        private lateinit var shoppingListInteractor: ShoppingListInteractor
        private lateinit var preferencesManager: SharedPreferencesManager

        fun application(): ShoppingListApplication {
            return globalApplication
        }

        fun globalContext(): Context {
            return globalApplication
        }

        fun shoppingListInteractor(): ShoppingListInteractor {
            return shoppingListInteractor
        }

        fun preferencesManager(): SharedPreferencesManager {
            return preferencesManager
        }
    }

    override fun onCreate() {
        super.onCreate()
        globalApplication = this
        Timber.plant(Timber.DebugTree())
        preferencesManager = SharedPreferencesManager(this)
        initializeRetrofit()
    }

    private fun initializeRetrofit() {
        val interceptor = HttpLoggingInterceptor()
        if (BuildConfig.DEBUG) {
            interceptor.level = HttpLoggingInterceptor.Level.BODY
        } else {
            interceptor.level = HttpLoggingInterceptor.Level.NONE
        }
        val client = OkHttpClient.Builder()
            .addInterceptor(HostSelectionInterceptor(preferencesManager))
            .addInterceptor(interceptor)
            .build()

        val moshi = Moshi.Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val retrofit = Retrofit.Builder()
            .client(client)
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl("http://localhost")
            .build()

        val shoppingListService = retrofit.create(ShoppingListService::class.java)
        shoppingListInteractor = ShoppingListInteractor(shoppingListService)
    }

}