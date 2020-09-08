package com.mcal.disassembler.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.jetbrains.annotations.NotNull;

public final class Database extends SQLiteOpenHelper {

    private static SQLiteDatabase database;

    public Database(Context context) {
        super(context, "assembler.db", null, 1);
        database = getWritableDatabase();
    }

    static SQLiteDatabase getDatabase() {
        return database;
    }

    @Override
    public void onCreate(@NotNull SQLiteDatabase db) {
        db.execSQL("CREATE TABLE Recents (Path TEXT PRIMARY KEY)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.e("onUpgrade", ": " + db + ": " + oldVersion + ": " + newVersion);
    }
}