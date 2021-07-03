package com.mcal.disassembler.activities;

import android.Manifest;
import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.developer.filepicker.model.DialogConfigs;
import com.developer.filepicker.model.DialogProperties;
import com.developer.filepicker.view.FilePickerDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mcal.disassembler.R;
import com.mcal.disassembler.adapters.ListAdapter;
import com.mcal.disassembler.data.Database;
import com.mcal.disassembler.data.Preferences;
import com.mcal.disassembler.data.RecentsManager;
import com.mcal.disassembler.interfaces.MainView;
import com.mcal.disassembler.nativeapi.DisassemblerDumper;
import com.mcal.disassembler.nativeapi.Dumper;
import com.mcal.disassembler.util.ScopedStorage;
import com.mcal.disassembler.view.CenteredToolBar;
import com.mcal.disassembler.widgets.SnackBar;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements MainView {

    static {
        System.loadLibrary("disassembler");
    }

    ProgressDialog dialog;
    private RecyclerView recentOpened;
    private final ArrayList<String> paths = new ArrayList<>();
    private String path;
    private LinearLayout welcomeLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        setupToolbar(getString(R.string.app_name));
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Settings.ACTION_MANAGE_OVERLAY_PERMISSION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE, Settings.ACTION_MANAGE_OVERLAY_PERMISSION}, 1);
            }
        }
        new Database(this);
        welcomeLayout = findViewById(R.id.welcome_layout);
        recentOpened = findViewById(R.id.items);
        recentOpened.setLayoutManager(new LinearLayoutManager(this));
        /*Cursor cursor = RecentsManager.getRecents();
        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                paths.add(cursor.getString(0));
            }
        }
        recentOpened.setAdapter(new ListAdapter(paths, this));*/
        updateRecents();
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

    void updateRecents() {
        paths.clear();
        Cursor cursor = RecentsManager.getRecents();
        if (cursor.getCount() == 0) { // TODO: FIXME
            recentOpened.setVisibility(View.GONE);
            welcomeLayout.setVisibility(View.VISIBLE);
            recentOpened.setAdapter(new ListAdapter(paths, this));
        } else {
            welcomeLayout.setVisibility(View.INVISIBLE);
            recentOpened.setVisibility(View.VISIBLE);
            recentOpened.setLayoutManager(new LinearLayoutManager(this));
            if (cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    paths.add(cursor.getString(0));
                }
            }
            recentOpened.setAdapter(new ListAdapter(paths, this));
        }

        if (cursor.getCount() > 0) {
            while (cursor.moveToNext()) {
                paths.add(cursor.getString(0));
            }
        }
        recentOpened.getAdapter().notifyDataSetChanged();
    }

    public void chooseSdcard(View view) {
        showFileChooser();
    }

    private void showFileChooser() {
        DialogProperties properties = new DialogProperties();
        properties.selection_mode = DialogConfigs.SINGLE_MODE;
        properties.selection_type = DialogConfigs.FILE_SELECT;
        properties.root = new File(ScopedStorage.getRootDirectory().getAbsolutePath());
        properties.extensions = new String[]{".so", ".SO"};
        //Instantiate FilePickerDialog with Context and DialogProperties.
        FilePickerDialog dialog = new FilePickerDialog(this, properties, R.style.AlertDialogTheme);
        dialog.setTitle(R.string.pickSo);
        dialog.setPositiveBtnName(getString(R.string.choose_button_label));
        dialog.setNegativeBtnName(getString(R.string.cancel_button_label));
        dialog.setDialogSelectionListener(files -> {
            for (String path : files) {
                File file = new File(path);
                if (file.getName().endsWith(".so") || file.getName().endsWith(".SO")) {
                    RecentsManager.add(file.getAbsolutePath());
                    updateRecents();
                    loadSo(file.getAbsolutePath());
                } else {
                    new SnackBar(this, getString(R.string.noFile)).show();
                }
            }
        });
        dialog.show();
    }

    public void loadSo(final String path) {
        showProgressDialog();
        this.path = path;
        new Thread() {
            public void run() {
                DisassemblerDumper.load(path);
                Dumper.readData();
                MainActivity.this.toClassesActivity();
            }
        }.start();
    }

    public void showProgressDialog() {
        dialog = new ProgressDialog(MainActivity.this);
        dialog.setTitle(getString(R.string.loading));
        dialog.show();
    }

    public void dismissProgressDialog() {
        if (dialog != null)
            dialog.dismiss();
        dialog = null;
    }

    public void toClassesActivity() {
        Bundle bundle = new Bundle();
        bundle.putString("filePath", path);
        Intent intent = new Intent(MainActivity.this, SymbolsActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
        dismissProgressDialog();
    }

    @Override
    public void onResume() {
        super.onResume();
    }
}