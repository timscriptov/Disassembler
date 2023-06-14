package com.mcal.disassembler.activities

import android.Manifest
import android.annotation.SuppressLint
import android.annotation.TargetApi
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatDelegate
import com.mcal.disassembler.R
import com.mcal.disassembler.data.Preferences
import com.mcal.disassembler.databinding.ActivityMainBinding

class MainActivity : BaseActivity() {
    private val binding by lazy(LazyThreadSafetyMode.NONE) {
        ActivityMainBinding.inflate(
            layoutInflater
        )
    }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        setupToolbar(binding.toolbar, getString(R.string.app_name), false)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION
                    ), 1
                )
            }
        }
        checkPermission()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("WrongConstant")
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.night_mode) {
            val preferences = Preferences(this)
            if (preferences.nightMode) {
                preferences.nightMode = false
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                delegate.applyDayNight()
            } else {
                preferences.nightMode = true
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                delegate.applyDayNight()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun github(view: View?) {
        startActivity(
            Intent(
                Intent.ACTION_VIEW,
                Uri.parse("https://github.com/TimScriptov/Disassembler.git")
            )
        )
    }

    fun telegram(view: View?) {
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/dexprotect")))
    }

    fun toNameDemangler(view: View?) {
        startActivity(Intent(this, DemanglerActivity::class.java))
    }

    fun symbols(view: View?) {
        val intent = Intent(this, RecentFilesActivity::class.java)
        startActivity(intent)
    }

    @Deprecated("Deprecated in Java")
    @TargetApi(Build.VERSION_CODES.M)
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        @Suppress("DEPRECATION")
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            checkPermission()
        }
    }

    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                startActivityForResult(
                    Intent(
                        Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:$packageName")
                    ),
                    ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE
                )
            }
        }
    }

    companion object {
        var ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469

        init {
            System.loadLibrary("disassembler")
        }
    }
}