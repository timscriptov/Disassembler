package com.mcal.disassembler;

import android.content.ContentValues;
import android.database.Cursor;

import static com.mcal.disassembler.Database.getDatabase;

class RecentsManager {
    static Cursor getRecents() {
        return getDatabase().rawQuery("SELECT * FROM Recents", null);
    }

    static void add(String path) {
        ContentValues cv = new ContentValues();
        cv.put("Path", path);
        getDatabase().insert("Recents", null, cv);
    }

    static void remove(String path) {
        getDatabase().execSQL("DELETE FROM Recents WHERE Path='" + path + "'");
    }
}
