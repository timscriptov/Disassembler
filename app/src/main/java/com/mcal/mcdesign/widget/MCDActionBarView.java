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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.widget.RelativeLayout;

import com.mcal.mcdesign.utils.BitmapRepeater;
import com.mcal.disassembler.R;

//##################################################################
/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MCDActionBarView extends RelativeLayout
//##################################################################
{
	public MCDActionBarView(android.content.Context context)
	{
		super(context);
	}

    public MCDActionBarView(android.content.Context context, android.util.AttributeSet attrs)
	{
		super(context, attrs);
	}

    public MCDActionBarView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
	}

    public MCDActionBarView(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr, int defStyleRes)
	{
		super(context, attrs, defStyleAttr, defStyleRes);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mcd_header_bg);  
		setBackgroundDrawable(new BitmapDrawable(BitmapRepeater.repeat(w, h, bitmap)));
	}
}
