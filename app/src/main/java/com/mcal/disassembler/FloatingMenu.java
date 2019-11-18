package com.mcal.disassembler;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

public class FloatingMenu {
    private static int xPos = 0;
    private static int yPos = 0;
    private WindowManager wm;
    public WindowManager.LayoutParams params;
    private FloatingMenuView floatView;

    private Context context;
    private String path;

    FloatingMenu(Context c, String filePath) {
        context = c;
        path = filePath;
    }

    @SuppressLint("ClickableViewAccessibility")
    public void show() {
        wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();


        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        params.format = PixelFormat.RGBA_8888;


        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = wm.getDefaultDisplay().getWidth() / 2;
        params.height = wm.getDefaultDisplay().getHeight() / 2;
        params.x = xPos;
        params.y = yPos;

        floatView = new FloatingMenuView(context, this, path, params.width);
        floatView.setClickable(true);

        floatView.setOnTouchListener(new OnTouchListener() {
            int lastX, lastY;
            int paramX, paramY;

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
                FloatingMenu.xPos = params.x;
                FloatingMenu.yPos = params.y;
                return false;
            }
        });

        wm.addView(floatView, params);
    }

    public void dismiss() {
        wm.removeView(floatView);
    }
}
