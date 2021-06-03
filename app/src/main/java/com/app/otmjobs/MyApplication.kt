package com.app.otmjobs

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate
import com.app.otmjobs.authentication.di.authenticationModule
import com.app.otmjobs.common.utils.AppConstants
import com.app.otmjobs.managejob.di.manageJobModule
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class MyApplication : Application() {
    companion object {
        lateinit var context: MyApplication private set
        lateinit var sharedPreferencesEditor: SharedPreferences.Editor
        lateinit var sharedPreferences: SharedPreferences
    }

    override fun onCreate() {
        super.onCreate()
        context = this
        provideSharedPreference()

        startKoin {
            androidContext(this@MyApplication)
            androidLogger(Level.DEBUG)
            modules(
                listOf(
                    authenticationModule,
                    manageJobModule,
                )
            )
        }

        when (MyApplication().preferenceGetInteger(
            AppConstants.SharedPrefKey.THEME_MODE,
            0
        )) {
            0 -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_NO
            )
            1 -> AppCompatDelegate.setDefaultNightMode(
                AppCompatDelegate.MODE_NIGHT_YES
            )
        }


    }

    private fun provideSharedPreference() {
        sharedPreferences = this@MyApplication.getSharedPreferences(
            this@MyApplication.packageName + "Preferences_",
            Context.MODE_PRIVATE
        )
        sharedPreferencesEditor = sharedPreferences.edit()
    }

    fun preferencePutInteger(key: String, value: Int) {
        sharedPreferencesEditor.putInt(key, value).apply()
    }

    fun preferenceGetInteger(key: String, defaultValue: Int): Int {
        return sharedPreferences.getInt(key, defaultValue)
    }

    fun preferencePutString(key: String, value: String) {
        sharedPreferencesEditor.putString(key, value).apply()
    }

    fun preferenceGetString(key: String, defaultValue: String): String {
        return sharedPreferences.getString(key, defaultValue)!!
    }

    fun preferenceRemoveKey(key: String?) {
        sharedPreferencesEditor.remove(key).apply()
    }

    fun provideGson(): Gson {
        return GsonBuilder().serializeNulls().create()
    }

    fun getContext(): Context {
        return context
    }

    fun clearData() {
        preferenceRemoveKey(AppConstants.SharedPrefKey.USER_INFO)
    }
}