package com.mcal.disassembler.activities

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mcal.disassembler.view.CenteredToolBar

open class BaseActivity : AppCompatActivity() {
    fun setupToolbar(toolbar: CenteredToolBar, title: Int) {
        setupToolbar(toolbar, title)
    }

    fun setupToolbar(toolbar: CenteredToolBar, title: String, back: Boolean = true) {
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setTitle(title)
            setDisplayHomeAsUpEnabled(back)
            setDisplayShowHomeEnabled(back)
        }
    }

    companion object {
        fun setVisibility(view: View, mode: Int) {
            if (view.visibility != mode) {
                view.visibility = mode
            }
        }
    }
}