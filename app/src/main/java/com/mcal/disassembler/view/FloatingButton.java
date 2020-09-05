package com.mcal.disassembler.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.PixelFormat;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;

import com.mcal.disassembler.R;

public class FloatingButton {
    private static int xPos = 0;
    private static int yPos = 0;
    public WindowManager.LayoutParams params;
    private WindowManager wm;
    private View floatView;

    private Context context;
    private String path;

    public FloatingButton(Context c, String p) {
        context = c;
        path = p;
    }

    public void show() {
        floatView = new View(context);
        floatView.setClickable(true);


        floatView.setBackgroundResource(R.drawable.box_pink);
        floatView.setOnClickListener(p1 -> {
            FloatingMenu menu = new FloatingMenu(context, path);
            menu.show();
            FloatingButton.this.dismiss();
        });
        wm = (WindowManager) context.getApplicationContext().getSystemService(Context.WINDOW_SERVICE);
        params = new WindowManager.LayoutParams();


        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        params.format = PixelFormat.TRANSPARENT;


        params.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        params.width = 50;
        params.height = 50;
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
                xPos = params.x;
                yPos = params.y;
                return false;
            }
        });

        wm.addView(floatView, params);
    }

    public void dismiss() {
        wm.removeView(floatView);
    }
}