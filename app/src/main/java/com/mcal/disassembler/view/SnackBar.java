package com.mcal.disassembler.view;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.mcal.disassembler.R;

public class SnackBar extends Dialog {

    String text;
    float textSize = 14;//Roboto Regular 14sp
    String buttonText;
    View.OnClickListener onClickListener;
    AppCompatActivity activity;
    View view;
    ExtendedFloatingActionButton button;
    int backgroundSnackBar = Color.parseColor("#333333");
    int backgroundButton = Color.parseColor("#1E88E5");

    OnHideListener onHideListener;
    OnBackPressedListener onBackPressedListener;
    Handler handler = new Handler(new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            if (onHideListener != null)
                onHideListener.onHide();
            dismiss();
            return false;
        }
    });
    // Timer
    private boolean mIndeterminate = false;
    private int mTimer = 3 * 1000;
    // Dismiss timer
    Thread dismissTimer = new Thread(() -> {
        try {
            Thread.sleep(mTimer);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        handler.sendMessage(new Message());
    });

    // With action button
    public SnackBar(AppCompatActivity activity, String text, String buttonText, View.OnClickListener onClickListener) {
        super(activity, android.R.style.Theme_Translucent);
        this.activity = activity;
        this.text = text;
        this.buttonText = buttonText;
        this.onClickListener = onClickListener;
    }

    // Only text
    public SnackBar(AppCompatActivity activity, String text) {
        super(activity, android.R.style.Theme_Translucent);
        this.activity = activity;
        this.text = text;
    }

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.material_snackbar);
        setCanceledOnTouchOutside(false);
        ((TextView) findViewById(R.id.snackbartext)).setText(text);
        ((TextView) findViewById(R.id.snackbartext)).setTextSize(textSize); //set textSize
        button = findViewById(R.id.snackbarbuttonflat);
        if (text == null || onClickListener == null)
            button.setVisibility(View.GONE);
        else if (text != null && onClickListener != null) {
            button.setText(buttonText);
            //button.setBackgroundColor(backgroundButton);

            button.setOnClickListener(v -> {
                SnackBar.this.onClickListener.onClick(v);
                dismiss();
            });
        }
        view = findViewById(R.id.snackbar);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return activity.dispatchTouchEvent(event);
    }

    public void setOnBackPressedListener(OnBackPressedListener l) {
        onBackPressedListener = l;
    }

    @Override
    public void onBackPressed() {
        if (onBackPressedListener != null)
            onBackPressedListener.onBackPressed();
        super.onBackPressed();
    }

    @Override
    public void show() {
        super.show();
        view.setVisibility(View.VISIBLE);
        view.startAnimation(AnimationUtils.loadAnimation(activity, R.anim.snackbar_show_animation));
        if (!mIndeterminate)
            dismissTimer.start();
    }

    /**
     * @author Jack Tony
     */
    @Override
    public void dismiss() {
        Animation anim = AnimationUtils.loadAnimation(activity, R.anim.snackbar_hide_animation);
        anim.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                SnackBar.super.dismiss();
            }
        });
        view.startAnimation(anim);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK)
            dismiss();
        return super.onKeyDown(keyCode, event);
    }

    public void setMessageTextSize(float size) {
        textSize = size;
    }

    public boolean isIndeterminate() {
        return mIndeterminate;
    }

    public void setIndeterminate(boolean indeterminate) {
        mIndeterminate = indeterminate;
    }

    public int getDismissTimer() {
        return mTimer;
    }

    public void setDismissTimer(int time) {
        mTimer = time;
    }

    /**
     * Change background color of SnackBar
     *
     * @param color
     */
    public void setBackgroundSnackBar(int color) {
        backgroundSnackBar = color;
        if (view != null)
            view.setBackgroundColor(color);
    }

    /**
     * Chage color of FlatButton in Snackbar
     *
     * @param color
     */
    public void setColorButton(int color) {
        backgroundButton = color;
        if (button != null)
            button.setBackgroundColor(color);
    }

    public void setOnhideListener(OnHideListener onHideListener) {
        this.onHideListener = onHideListener;
    }

    public interface OnBackPressedListener {
        void onBackPressed();
    }

    /**
     * This event start when snackbar dismish without push the button
     *
     * @author Navas
     */
    public interface OnHideListener {
        public void onHide();
    }
}