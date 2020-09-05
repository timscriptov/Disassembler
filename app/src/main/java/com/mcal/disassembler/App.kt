package com.mcal.disassembler

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        private var context: Context? = null

        @JvmStatic
        fun getContext(): Context? {
            if (context == null) {
                context = App()
            }
            return context
        }
    }
}