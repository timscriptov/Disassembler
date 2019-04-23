package com.mcal.disassembler;

import android.content.ContentValues;
import android.database.Cursor;

public class RecentsManager 
{
	public static Cursor getRecents() {
        return Database.getDatabase().rawQuery("SELECT * FROM Recents", null);
    }

    public static boolean add(String path) {
        ContentValues cv = new ContentValues();
        cv.put("Path", path);
        long result = Database.getDatabase().insert("Recents", null, cv);
        return result != -1;
    }

    public static void remove(String path) {
        Database.getDatabase().execSQL("DELETE FROM Recents WHERE Path='" + path + "'");
    }

}
