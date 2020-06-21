package dev.cquintana.shoppinglist

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull

class HostSelectionInterceptor(val preferencesManager: SharedPreferencesManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        var request = chain.request()

        val host = preferencesManager.getBaseUrl()
        if (host != null) {
            val parsed = host.toHttpUrlOrNull()
            if (parsed != null) {
                val newUrl = request.url.newBuilder()
                    .scheme(parsed.scheme)
                    .host(parsed.host)
                    .port(parsed.port)

                request = request.newBuilder()
                    .url(newUrl.build())
                    .build()
            }
        }

        return chain.proceed(request)
    }

}