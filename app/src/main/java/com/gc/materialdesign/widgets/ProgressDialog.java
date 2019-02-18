package com.gc.materialdesign.widgets;

import android.content.Context;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.mcal.disassembler.R;
import com.gc.materialdesign.views.ButtonFlat;
import com.gc.materialdesign.views.ProgressBarCircularIndeterminate;
import com.gc.materialdesign.views.*;

public class ProgressDialog extends android.app.Dialog
{
	Context context;
	View view;
	View backView;
	String title;
	TextView titleTextView;
	int maxProgress;
	int progressColor = -1;

	public ProgressDialog(Context context, String title)
	{
		super(context, android.R.style.Theme_Translucent);
		this.title = title;
		this.context = context;
	}

	public ProgressDialog(Context context, String title, int progressColor)
	{
		super(context, android.R.style.Theme_Translucent);
		this.title = title;
		this.progressColor = progressColor;
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.gc_materialdesign_progress_dialog);

		view = (RelativeLayout)findViewById(R.id.contentDialog);
		backView = (RelativeLayout)findViewById(R.id.dialog_rootView);
		backView.setOnTouchListener(new OnTouchListener()
			{
				@Override
				public boolean onTouch(View v, MotionEvent event)
				{
					if (event.getX() < view.getLeft() 
						|| event.getX() > view.getRight()
						|| event.getY() > view.getBottom() 
						|| event.getY() < view.getTop())
					{
						dismiss();
					}
					return false;
				}
			});

	    this.titleTextView = (TextView) findViewById(R.id.title);
	    setTitle(title);
	    if (progressColor != -1)
		{
	    	ProgressBarIndeterminateDeterminate progressBarIndeterminateDeterminate = (ProgressBarIndeterminateDeterminate) findViewById(R.id.progressBarIndeterminateDeterminate);
	    	progressBarIndeterminateDeterminate.setBackgroundColor(progressColor);
	    }
	}

	@Override
	public void show()
	{
		// TODO 自动生成的方法存根
		super.show();
	}

	// GETERS & SETTERS

	public String getTitle()
	{
		return title;
	}

	public void setTitle(String title)
	{
		this.title = title;
		if (title == null)
			titleTextView.setVisibility(View.GONE);
		else
		{
			titleTextView.setVisibility(View.VISIBLE);
			titleTextView.setText(title);
		}
	}

	public TextView getTitleTextView() 
	{
		return titleTextView;
	}

	public void setTitleTextView(TextView titleTextView) 
	{
		this.titleTextView = titleTextView;
	}

	@Override
	public void dismiss()
	{
		ProgressDialog.super.dismiss();
	}
}
 
