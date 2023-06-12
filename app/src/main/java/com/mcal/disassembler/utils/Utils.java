package com.mcal.disassembler.utils;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;
import android.view.View;

import androidx.annotation.NonNull;

import org.jetbrains.annotations.NotNull;

public class Utils {
    public static int dp(@NonNull Context context, int i) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, (float) i, context.getResources().getDisplayMetrics());
    }

    public static int dpToPx(float dp, @NotNull Resources resources) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, resources.getDisplayMetrics());
    }

    public static int getRelativeTop(@NotNull View myView) {
        if (myView.getId() == android.R.id.content) {
            return myView.getTop();
        } else {
            return myView.getTop() + getRelativeTop((View) myView.getParent());
        }
    }

    public static int getRelativeLeft(@NotNull View myView) {
        if (myView.getId() == android.R.id.content) {
            return myView.getLeft();
        } else {
            return myView.getLeft() + getRelativeLeft((View) myView.getParent());
        }
    }
}
