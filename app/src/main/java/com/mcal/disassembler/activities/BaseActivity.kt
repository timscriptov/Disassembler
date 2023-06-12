package com.mcal.disassembler.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.mcal.disassembler.view.CenteredToolBar

open class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    fun setupToolbar(toolbar: CenteredToolBar, title: Integer) {
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

    fun setVisibility(view: View, mode: Int) {
        if (view.visibility != mode) {
            view.visibility = mode
        }
    }
}