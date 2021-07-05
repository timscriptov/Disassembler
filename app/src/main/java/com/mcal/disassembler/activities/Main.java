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
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.mcal.disassembler.BuildConfig;
import com.mcal.disassembler.R;
import com.mcal.disassembler.data.Constants;
import com.mcal.disassembler.data.Preferences;
import com.mcal.disassembler.iap.DataWrappers;
import com.mcal.disassembler.iap.IapConnector;
import com.mcal.disassembler.iap.PurchaseServiceListener;
import com.mcal.disassembler.iap.SubscriptionServiceListener;
import com.mcal.disassembler.util.AdsAdmob;
import com.mcal.materialdesign.view.CenteredToolBar;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import kotlin.Unit;
import kotlin.jvm.functions.Function0;

public class Main extends AppCompatActivity {

    public static int ACTION_MANAGE_OVERLAY_PERMISSION_REQUEST_CODE = 5469;
    public static LinearLayout adLayout;

    static {
        System.loadLibrary("disassembler");
    }

    private IapConnector iapConnector;

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
        AdsAdmob.loadInterestialAd(this);
        adLayout = findViewById(R.id.ad_view);
        adLayout.addView(AdsAdmob.getBanner(this));
        checkPermission();
        List<String> nonConsumablesList = Collections.singletonList("premium");
        List<String> consumablesList = Arrays.asList("donate_disassembler", "moderate", "quite", "plenty", "yearly");
        List<String> subsList = Collections.singletonList("subscription");

        iapConnector = new IapConnector(
                this,
                nonConsumablesList,
                consumablesList,
                subsList,
                Constants.LK,
                BuildConfig.DEBUG
        );

        iapConnector.addPurchaseListener(new PurchaseServiceListener() {
            public void onPricesUpdated(@NotNull Map<String, String> iapKeyPrices) {

            }

            public void onProductPurchased(DataWrappers.@NotNull PurchaseInfo purchaseInfo) {
                if (purchaseInfo.getSku().equals("donate_disassembler")) {

                }
            }

            public void onProductRestored(DataWrappers.@NotNull PurchaseInfo purchaseInfo) {

            }
        });
        iapConnector.addSubscriptionListener(new SubscriptionServiceListener() {
            public void onSubscriptionRestored(DataWrappers.@NotNull PurchaseInfo purchaseInfo) {
            }

            public void onSubscriptionPurchased(DataWrappers.@NotNull PurchaseInfo purchaseInfo) {
                if (purchaseInfo.getSku().equals("subscription")) {

                }
            }

            public void onPricesUpdated(@NotNull Map<String, String> iapKeyPrices) {

            }
        });
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
        AdsAdmob.showInterestialAd(this, null);
        this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://github.com/TimScriptov/Disassembler.git")));
    }

    public void telegram(View view) {
        AdsAdmob.showInterestialAd(this, null);
        this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/dexprotect")));
    }

    public void toNameDemangler(View view) {
        AdsAdmob.showInterestialAd(this, null);
        startActivity(new Intent(this, NameDemanglerActivity.class));
    }

    public void translator(View view) {
        AdsAdmob.showInterestialAd(this, null);
        startActivity(new Intent(this, com.mcal.elfeditor.MainActivity.class));
    }

    public void symbols(View view) {
        AdsAdmob.showInterestialAd(this, runSymbols());
    }

    public Function0<Unit> runSymbols() {
        AdsAdmob.showInterestialAd(this, null);
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        return null;
    }

    public void hexViewer(View view) {
        AdsAdmob.showInterestialAd(this, runHexViewer());
    }

    public Function0<Unit> runHexViewer() {
        Intent intent = new Intent(this, fr.ralala.hexviewer.ui.activities.MainActivity.class);
        startActivity(intent);
        return null;
    }

    public void donate(View v) {
        iapConnector.purchase(this, "donate_disassembler");
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
