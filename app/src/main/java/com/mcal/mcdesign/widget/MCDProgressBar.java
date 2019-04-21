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

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.ProgressBar;

//##################################################################
/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MCDProgressBar extends ProgressBar
//##################################################################
{
	private Paint mPaint;

	public MCDProgressBar(android.content.Context context) 
	{
		super(context);
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.parseColor("#FF2C9EF4"));
	}

    public MCDProgressBar(android.content.Context context, android.util.AttributeSet attrs)
	{
		super(context, attrs);
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.parseColor("#FF2C9EF4"));
	}

    public MCDProgressBar(android.content.Context context, android.util.AttributeSet attrs, int defStyleAttr)
	{
		super(context, attrs, defStyleAttr);
		mPaint = new Paint();
		mPaint.setStyle(Paint.Style.FILL);
		mPaint.setColor(Color.parseColor("#FF2C9EF4"));
	}

	private int mWidth = 0;
	private int mHeight = 0;

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh)
	{
		super.onSizeChanged(w, h, oldw, oldh);
		mWidth = w;
		mHeight = h;
	}

	private static final float mDefaultSpeed = 0.075F;

	private float mBlockDrawingProgress =  0;
	private int mShowedBlocks = 1;
	private boolean mIsScaling = true;

	@Override
	protected void onDraw(Canvas canvas)
	{
		if (mIsScaling)
			mBlockDrawingProgress += (mDefaultSpeed / 2);
		else
			mBlockDrawingProgress += mDefaultSpeed;
		if (mBlockDrawingProgress >= 1 && !mIsScaling)
		{
			mBlockDrawingProgress = 0;
			++mShowedBlocks;
			if (mShowedBlocks > 4)
			{
				mShowedBlocks = 1;
				mIsScaling = true;
			}
		}
		else if (mBlockDrawingProgress >= 0.5 && mIsScaling)
		{
			mIsScaling = false;
			mBlockDrawingProgress = 0;
			mShowedBlocks = 2;
		}

		switch (mShowedBlocks)
		{
			case 1:
				{
					int drawWidth = (int) (((float)mWidth) * mBlockDrawingProgress);
					int drawHeight = (int) (((float)mHeight) * mBlockDrawingProgress);
					canvas.drawRect(0, drawHeight, mWidth - drawWidth, mHeight, mPaint);
					break;
				}
			case 2:
				{
					canvas.drawRect(0, mHeight / 2, mWidth / 2, mHeight , mPaint);
					int blockDrawHeight=(int) (((float)mHeight / 2) * mBlockDrawingProgress);
					canvas.drawRect(mWidth / 2 , blockDrawHeight, mWidth, blockDrawHeight + mHeight / 2, mPaint);
					break;
				}
			case 3:
				{
					canvas.drawRect(0, mHeight / 2, mWidth , mHeight , mPaint);
					int blockDrawHeight=(int) (((float)mHeight / 2) * mBlockDrawingProgress);
					canvas.drawRect(0, 0 , mWidth / 2, blockDrawHeight + 1 , mPaint);
					break;
				}
			case 4:
				{
					canvas.drawRect(0, mHeight / 2, mWidth , mHeight , mPaint);
					canvas.drawRect(0, 0, mWidth / 2 , mHeight / 2, mPaint);
					int blockDrawHeight=(int) (((float)mHeight / 2) * mBlockDrawingProgress);
					canvas.drawRect(mWidth / 2, 0 , mWidth , blockDrawHeight + 1 , mPaint);
					break;
				}
		}
		invalidate();
	}
}
