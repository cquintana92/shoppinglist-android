package dev.cquintana.shoppinglist

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Configuration
import androidx.core.content.edit
import androidx.preference.PreferenceManager

class SharedPreferencesManager(context: Context) {

    companion object {
        private val DAY_NIGHT_MODE = "current_night_mode"
        private val BASE_URL = "base_url"
        private val SECRET_BEARER = "secret_bearer"

        val NIGHT_MODE_NO = Configuration.UI_MODE_NIGHT_NO
        val NIGHT_MODE_YES = Configuration.UI_MODE_NIGHT_YES

    }

    private val sharedPreferences: SharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)

    fun setBaseUrl(url: String) {
        this.sharedPreferences.edit().putString(BASE_URL, url).apply()
    }

    fun getBaseUrl(): String? {
        val url = this.sharedPreferences.getString(BASE_URL, "")
        if (url != "") {
            return url
        } else {
            return null
        }
    }

    fun setBearer(bearer: String) {
        this.sharedPreferences.edit().putString(SECRET_BEARER, bearer).apply()
    }

    fun getBearer(): String? {
        val bearer = this.sharedPreferences.getString(SECRET_BEARER, "")
        if (bearer != "") {
            return bearer
        } else {
            return null
        }
    }

    fun setNightMode(nightMode: Int) {
        this.sharedPreferences.edit().putInt(DAY_NIGHT_MODE, nightMode).apply()
    }

    fun getNightMode(): Int {
        return this.sharedPreferences.getInt(DAY_NIGHT_MODE, NIGHT_MODE_NO)
    }
}