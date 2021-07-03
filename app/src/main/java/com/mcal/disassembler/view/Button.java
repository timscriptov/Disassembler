package com.mcal.disassembler.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.TextView;

import com.mcal.disassembler.R;
import com.mcal.disassembler.util.Utils;

public abstract class Button extends CustomView {
    final static String ANDROIDXML = "http://schemas.android.com/apk/res/android";

    int minWidth;
    int minHeight;
    int background;
    float rippleSpeed = 12f;
    int rippleSize = 3;
    Integer rippleColor;
    OnClickListener onClickListener;
    boolean clickAfterRipple = true;
    int backgroundColor = Color.parseColor("#1E88E5");
    TextView textButton;
    float x = -1, y = -1;
    float radius = -1;

    public Button(Context context, AttributeSet attrs) {
        super(context, attrs);
        setDefaultProperties();
        clickAfterRipple = attrs.getAttributeBooleanValue(MATERIALDESIGNXML,
                "animate", true);
        setAttributes(attrs);
        beforeBackground = backgroundColor;
        if (rippleColor == null)
            rippleColor = makePressColor();
    }

    public Button(Context context) {
        super(context);
        setDefaultProperties();
        beforeBackground = backgroundColor;
        if (rippleColor == null)
            rippleColor = makePressColor();
    }

    // ### RIPPLE EFFECT ###

    protected void setDefaultProperties() {
        setMinimumHeight(Utils.dpToPx(minHeight, getResources()));
        setMinimumWidth(Utils.dpToPx(minWidth, getResources()));
        setBackgroundResource(background);
        setBackgroundColor(backgroundColor);
    }

    abstract protected void setAttributes(AttributeSet attrs);

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        if (isEnabled()) {
            isLastTouch = true;
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                radius = getHeight() / rippleSize;
                x = event.getX();
                y = event.getY();
            } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
                radius = getHeight() / rippleSize;
                x = event.getX();
                y = event.getY();
                if (!((event.getX() <= getWidth() && event.getX() >= 0) && (event
                        .getY() <= getHeight() && event.getY() >= 0))) {
                    isLastTouch = false;
                    x = -1;
                    y = -1;
                }
            } else if (event.getAction() == MotionEvent.ACTION_UP) {
                if ((event.getX() <= getWidth() && event.getX() >= 0)
                        && (event.getY() <= getHeight() && event.getY() >= 0)) {
                    ++radius;
                    if (!clickAfterRipple && onClickListener != null) {
                        onClickListener.onClick(this);
                    }
                } else {
                    isLastTouch = false;
                    x = -1;
                    y = -1;
                }
            } else if (event.getAction() == MotionEvent.ACTION_CANCEL) {
                isLastTouch = false;
                x = -1;
                y = -1;
            }
        }
        return true;
    }

    @Override
    protected void onFocusChanged(boolean gainFocus, int direction,
                                  Rect previouslyFocusedRect) {
        super.onFocusChanged(gainFocus, direction, previouslyFocusedRect);
        if (!gainFocus) {
            x = -1;
            y = -1;
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    public Bitmap makeCircle() {
        Bitmap output = Bitmap.createBitmap(
                getWidth() - Utils.dpToPx(0, getResources()), getHeight()
                        - Utils.dpToPx(0, getResources()), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(rippleColor);
        canvas.drawCircle(x, y, radius, paint);
        if (radius > getHeight() / rippleSize)
            radius += rippleSpeed;
        if (radius >= getWidth()) {
            x = -1;
            y = -1;
            radius = getHeight() / rippleSize;
            if (onClickListener != null && clickAfterRipple)
                onClickListener.onClick(this);
        }
        return output;
    }

    protected int makePressColor() {
        int r = (this.backgroundColor >> 16) & 0xFF;
        int g = (this.backgroundColor >> 8) & 0xFF;
        int b = (this.backgroundColor >> 0) & 0xFF;
        r = (r - 30 < 0) ? 0 : r - 30;
        g = (g - 30 < 0) ? 0 : g - 30;
        b = (b - 30 < 0) ? 0 : b - 30;
        return Color.rgb(r, g, b);
    }

    @Override
    public void setOnClickListener(OnClickListener l) {
        onClickListener = l;
    }

    // Set color of background
    public void setBackgroundColor(int color) {
        this.backgroundColor = color;
        if (isEnabled())
            beforeBackground = backgroundColor;
        try {
            LayerDrawable layer = (LayerDrawable) getBackground();
            GradientDrawable shape = (GradientDrawable) layer
                    .findDrawableByLayerId(R.id.shape_bacground);
            shape.setColor(backgroundColor);
            rippleColor = makePressColor();
        } catch (Exception ex) {
            // Without bacground
        }
    }

    public float getRippleSpeed() {
        return this.rippleSpeed;
    }

    public void setRippleSpeed(float rippleSpeed) {
        this.rippleSpeed = rippleSpeed;
    }

    public void setTextColor(int color) {
        textButton.setTextColor(color);
    }

    public TextView getTextView() {
        return textButton;
    }

    public String getText() {
        return textButton.getText().toString();
    }

    public void setText(String text) {
        textButton.setText(text);
    }
}