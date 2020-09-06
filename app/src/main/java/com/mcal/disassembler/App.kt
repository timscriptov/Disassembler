package com.mcal.disassembler

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.mcal.disassembler.data.Database
import org.jetbrains.annotations.Nullable

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        Database(getContext())
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null
        private var app: Application? = null
        private var preferences: SharedPreferences? = null

        @JvmStatic
        fun getContext(): Context? {
            if (context == null) {
                context = App()
            }
            return context
        }

        fun getApp(): Application? {
            if (app == null) {
                app = App()
            }
            return app
        }

        @JvmStatic
        @Nullable
        fun getPreferences(): SharedPreferences {
            if (preferences == null) {
                preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext())
            }
            return preferences!!
        }
    }
}