/*
 * Copyright (C) 2018-2019 Тимашков Иван
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mcal.mcdesign.widget;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.widget.Switch;

import com.mcal.disassembler.R;

//##################################################################

/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MCDSwitch extends Switch
//##################################################################
{
    Bitmap bitmap;
    Bitmap bitmapClicked;
    Bitmap bitmapNI;

    public MCDSwitch(android.content.Context context) {
        super(context);
    }

    public MCDSwitch(android.content.Context context, android.util.AttributeSet attrs) {
        super(context, attrs);
    }

    public MCDSwitch(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @SuppressLint("NewApi")
    public MCDSwitch(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (bitmap == null || bitmapClicked == null) {
            bitmap = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.mcd_checkbox_default);
            bitmapClicked = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.mcd_checkbox_checked);
            bitmapNI = BitmapFactory.decodeResource(getContext().getResources(), R.drawable.mcd_checkbox_not_important);

        }
        if (!super.isClickable())
            canvas.drawBitmap(bitmapNI, 0, 0, null);
        else if (super.isChecked())
            canvas.drawBitmap(bitmapClicked, 0, 0, null);
        else
            canvas.drawBitmap(bitmap, 0, 0, null);
        invalidate();
    }
}