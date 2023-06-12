package com.mcal.disassembler.vtable;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

public final class Utils {
    @NonNull
    private static String b2hex(@NonNull byte[] bytes) {
        StringBuilder result = new StringBuilder();
        for (byte b : bytes) {
            String h;
            if (b >= 0) {
                h = Integer.toHexString(b);
            } else {
                h = Integer.toHexString(2 * 128 + b);
            }
            if (h.length() < 2) {
                result.insert(0, "0" + h);
            } else {
                result.insert(0, h);
            }
        }
        return result.toString();
    }

    public static boolean saveFile(String fileName, byte[] arys) {
        File file = new File(fileName);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(arys);
            fos.flush();
            return true;
        } catch (Exception e) {
            System.out.println("save file error:" + e.toString());
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (Exception e) {
                    System.out.println("close file error:" + e.toString());
                }
            }
        }
        return false;
    }

    @Nullable
    static byte[] readFile(String fileName) {
        try {
            File file = new File(fileName);
            FileInputStream fis = new FileInputStream(file);
            byte[] b = new byte[(int) file.length()];
            fis.read(b);
            fis.close();
            return b;
        } catch (Exception e) {
            System.out.println(e);
        }
        return null;
    }

    static byte[] cp(byte[] res, int start, int count) {
        if (res == null) {
            return null;
        }
        byte[] result = new byte[count];
        System.arraycopy(res, start, result, 0, count);
        return result;
    }

    private static int b2i(byte[] src) {
        return Integer.parseInt(b2hex(src), 16);
    }

    static int cb2i(byte[] res, int start, int count) {
        return b2i(cp(res, start, count));
    }
}
