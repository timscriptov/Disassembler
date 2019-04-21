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
package com.mcal.mcdesign.app;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatTextView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;

import com.mcal.mcdesign.utils.BitmapRepeater;
import com.mcal.disassembler.R;

//##################################################################
/**
 * @author Тимашков Иван
 * @author https://github.com/TimScriptov
 */
public class MCDActivity extends AppCompatActivity
//##################################################################
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setDefaultActionBar();
	}

	protected void setDefaultActionBar()
	{
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null)
		{
			RelativeLayout actionBarCustomView = (RelativeLayout)LayoutInflater.from(this).inflate(R.layout.mcd_actionbar, null);
			ActionBar.LayoutParams layoutParams = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
			layoutParams.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
			actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
			actionBar.setDisplayShowHomeEnabled(false);
			actionBar.setDisplayShowCustomEnabled(true);
			actionBar.setDisplayShowTitleEnabled(false);
			actionBar.setCustomView(actionBarCustomView, layoutParams);
			android.support.v7.widget.Toolbar parent = (android.support.v7.widget.Toolbar) actionBarCustomView.getParent();
			parent.setContentInsetsAbsolute(0, 0);

			AppCompatTextView titleTV=actionBarCustomView.findViewById(R.id.mcd_actionbar_title);
			titleTV.setText(getTitle());
		}
	}

	@Override
	public void setTitle(int titleId)
	{
		super.setTitle(titleId);

		if (getSupportActionBar() != null)
		{
			View actionBarCustomView=getSupportActionBar().getCustomView();
			AppCompatTextView titleTV=actionBarCustomView.findViewById(R.id.mcd_actionbar_title);
			titleTV.setText(titleId);
		}
	}

	@Override
	public void setTitle(CharSequence title)
	{
		super.setTitle(title);

		if (getSupportActionBar() != null)
		{
			View actionBarCustomView=getSupportActionBar().getCustomView();
			AppCompatTextView titleTV=actionBarCustomView.findViewById(R.id.mcd_actionbar_title);
			titleTV.setText(title);
		}
	}

	protected void setActionBarViewRight(View view)
	{
		if (getSupportActionBar() != null)
		{
			View actionBarCustomView=getSupportActionBar().getCustomView();
			RelativeLayout layout=actionBarCustomView.findViewById(R.id.mcd_actionbar_ViewRight);
			layout.removeAllViews();
			layout.addView(view);
		}
	}

	protected void setActionBarViewLeft(View view)
	{
		if (getSupportActionBar() != null)
		{
			View actionBarCustomView=getSupportActionBar().getCustomView();
			RelativeLayout layout=actionBarCustomView.findViewById(R.id.mcd_actionbar_ViewLeft);
			layout.removeAllViews();
			layout.addView(view);
		}
	}

	protected void setActionBarButtonCloseRight()
	{
		View buttonClose=getLayoutInflater().inflate(R.layout.moddedpe_ui_button_close, null);
		buttonClose.findViewById(R.id.moddedpe_ui_button_item_image_button).setOnClickListener(new View.OnClickListener()
			{

				@Override
				public void onClick(View p1)
				{
					finish();
				}


			});
		setActionBarViewRight(buttonClose);
	}


	@Override
	public void onWindowFocusChanged(boolean hasFocus)
	{
		super.onWindowFocusChanged(hasFocus);

		Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.mcd_bg);
		bitmap = BitmapRepeater.repeat(getWindowManager().getDefaultDisplay().getWidth(), getWindowManager().getDefaultDisplay().getHeight(), bitmap);
		getWindow().getDecorView().setBackground(new BitmapDrawable(bitmap));
	}
}
