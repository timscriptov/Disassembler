package com.mcal.disassembler.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.mcal.disassembler.R;
import com.mcal.disassembler.data.Preferences;
import com.mcal.disassembler.view.CenteredToolBar;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class Main extends AppCompatActivity {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;

    static {
        System.loadLibrary("disassembler");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        setupToolbar(getString(R.string.app_name));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Settings.ACTION_MANAGE_OVERLAY_PERMISSION}, 1);
            }
        }
        checkPermission();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @SuppressLint("WrongConstant")
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.night_mode) {
            if (Preferences.isNightModeEnabled()) {
                Preferences.setNightModeEnabled(false);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                getDelegate().applyDayNight();
            } else {
                Preferences.setNightModeEnabled(true);
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                getDelegate().applyDayNight();
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("ConstantConditions")
    private void setupToolbar(String title) {
        CenteredToolBar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
    }

    public void github(View view) {
        this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TimScriptov/Disassembler.git")));
    }

    public void telegram(View view) {
        this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/dexprotect")));
    }

    public void toNameDemangler(View view) {
        startActivity(new Intent(this, NameDemanglerActivity.class));
    }

//    public void translator(View view) {
//        startActivity(new Intent(this, com.mcal.elfeditor.MainActivity.class));
//    }

    public void symbols(View view) {
        runSymbols();
    }

    public Function0<Unit> runSymbols() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return null;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE) {
            if (!Settings.canDrawOverlays(this)) {
                checkPermission();
            }
        }
    }

    public void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE);
            }
        }
    }
}
