package com.gc.materialdesign.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import java.util.*;

import com.mcal.disassembler.R;


public class ScrollView extends android.widget.ScrollView
{
	
	public ScrollView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		this.setBackgroundColor(0x888888);
		//this.setScrollBarStyle(R.style.scrollBarTheme);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
	    for(int i = 0; i < ((ViewGroup)getChildAt(0)).getChildCount(); ++i)
		{
	    	try 
			{
				CustomView child =(CustomView) ((ViewGroup)getChildAt(0)).getChildAt(i);
				if(child.isLastTouch)
				{
					child.onTouchEvent(ev);
					return true;
				}
			} 
			catch (ClassCastException e) 
			{
				
			}
	    }
	    return super.onTouchEvent(ev);
	}
	

}
