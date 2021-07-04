package com.mcal.disassembler.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.os.Build;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.mcal.disassembler.R;
import com.mcal.disassembler.util.Utils;

public class FloatingButton {
    public static int xPos = 0;
    public static int yPos = 0;
    private final Context context;
    private final String path;
    public boolean isAdded = false;
    public WindowManager wm;
    public WindowManager.LayoutParams params;
    public View floatView;

    public FloatingButton(Context c, String p) {
        context = c;
        path = p;
    }

    public void show() {
        int LAYOUT_FLAG;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_FLAG = WindowManager.LayoutParams.TYPE_PHONE;
        }

        floatView = new View(context);
        floatView.setClickable(true);

        floatView.setBackgroundResource(R.mipmap.ic_launcher_round);
        floatView.setOnClickListener(p1 -> {
            FloatingMenu menu = new FloatingMenu(context, path);
            menu.show();
            FloatingButton.this.dismiss();
        });
        wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();

        params.type = LAYOUT_FLAG; // WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        params.format = PixelFormat.TRANSPARENT;

        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = Utils.dp(context, 64);
        ;
        params.height = Utils.dp(context, 64);
        ;
        params.x = xPos;
        params.y = yPos;

        floatView.setOnTouchListener(new OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;

            @SuppressLint("ClickableViewAccessibility")
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        lastX = (int) event.getRawX();
                        lastY = (int) event.getRawY();
                        paramX = params.x;
                        paramY = params.y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        int dx = (int) event.getRawX() - lastX;
                        int dy = (int) event.getRawY() - lastY;
                        params.x = paramX + dx;
                        params.y = paramY + dy;
                        wm.updateViewLayout(floatView, params);
                        break;
                }
                FloatingButton.xPos = params.x;
                FloatingButton.yPos = params.y;
                return false;
            }
        });

        wm.addView(floatView, params);
        isAdded = true;
    }

    public void dismiss() {
        wm.removeView(floatView);
    }
}