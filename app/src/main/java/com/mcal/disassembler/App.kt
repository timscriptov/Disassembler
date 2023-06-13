package com.mcal.disassembler

import android.R
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import com.mcal.disassembler.data.Database
import com.mcal.disassembler.data.Preferences
import org.jetbrains.annotations.Nullable

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
        preferences = PreferenceManager.getDefaultSharedPreferences(this)
        Database(getContext())
        if (Preferences.isNightModeEnabled()) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
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
                preferences = PreferenceManager.getDefaultSharedPreferences(this.getContext()!!)
            }
            return preferences!!
        }

        @JvmStatic
        fun dp(context: Context, i: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                i.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }

        @JvmStatic
        fun dpToPx(dp: Float, resources: Resources): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                dp,
                resources.displayMetrics
            ).toInt()
        }

        @JvmStatic
        fun getRelativeTop(myView: View): Int {
            return if (myView.id == R.id.content) {
                myView.top
            } else {
                myView.top + getRelativeTop(myView.parent as View)
            }
        }

        @JvmStatic
        fun getRelativeLeft(myView: View): Int {
            return if (myView.id == R.id.content) {
                myView.left
            } else {
                myView.left + getRelativeLeft(myView.parent as View)
            }
        }
    }
}
