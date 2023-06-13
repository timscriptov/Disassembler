package com.mcal.disassembler

import android.app.Application
import android.content.Context
import android.util.TypedValue
import androidx.appcompat.app.AppCompatDelegate
import com.mcal.disassembler.data.Database
import com.mcal.disassembler.data.Preferences

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        Database(this)
        if (Preferences(this).nightMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    companion object {
        @JvmStatic
        fun dp(context: Context, i: Int): Int {
            return TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                i.toFloat(),
                context.resources.displayMetrics
            ).toInt()
        }
    }
}
