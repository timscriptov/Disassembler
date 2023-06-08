package com.mcal.disassembler.data;

import static com.mcal.disassembler.data.Database.getDatabase;

import android.content.ContentValues;
import android.database.Cursor;

public class RecentsManager {
    public static Cursor getRecents() {
        return getDatabase().rawQuery("SELECT * FROM Recents", null);
    }

    public static void add(String path) {
        ContentValues cv = new ContentValues();
        cv.put("Path", path);
        getDatabase().insert("Recents", null, cv);
    }

    public static void remove(String path) {
        getDatabase().execSQL("DELETE FROM Recents WHERE Path='" + path + "'");
    }
}