package com.nbsp.materialfilepicker.ui;

import org.jetbrains.annotations.NotNull;

import java.util.Locale;

public class SpaceFormatter {
    @NotNull
    public static String format(long originalSize) {
        String label = "B";
        double size = originalSize;

        if (size > 1024) {
            size /= 1024;
            label = "KB";
        }

        if (size > 1024) {
            size /= 1024;
            label = "MB";
        }

        if (size > 1024) {
            size /= 1024;
            label = "GB";
        }

        if (size % 1 == 0) {
            return String.format(Locale.getDefault(), "%d %s", (long) size, label);
        } else {
            return String.format(Locale.getDefault(), "%.1f %s", size, label);
        }
    }
}