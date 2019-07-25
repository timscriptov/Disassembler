package com.gc.materialdesign.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.gc.materialdesign.utils.Utils;
import com.mcal.disassembler.R;

public class ButtonRectangle extends Button
{
	TextView textButton;

	int paddingTop,paddingBottom, paddingLeft, paddingRight;


	public ButtonRectangle(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		setDefaultProperties();
	}
	@Override
	protected void setDefaultProperties()
	{
		super.minWidth = 80;
		super.minHeight = 36;
		super.background = R.drawable.background_button_rectangle;
		super.setDefaultProperties();
	}



	protected void setAttributes(AttributeSet attrs)
	{

		int bacgroundColor = attrs.getAttributeResourceValue(ANDROIDXML, "background", -1);
		if (bacgroundColor != -1)
		{
			setBackgroundColor(getResources().getColor(bacgroundColor));
		}
		else
		{
			background = attrs.getAttributeIntValue(ANDROIDXML, "background", -1);
			if (background != -1)
				setBackgroundColor(background);
		}


		// Set text button
		String text = null;
		int textResource = attrs.getAttributeResourceValue(ANDROIDXML, "text", -1);
		if (textResource != -1)
		{
			text = getResources().getString(textResource);
		}
		else
		{
			text = attrs.getAttributeValue(ANDROIDXML, "text");
		}
		if (text != null)
		{
			textButton = new TextView(getContext());
			textButton.setText(text);
			textButton.setTextColor(Color.WHITE);
			textButton.setTypeface(null, Typeface.BOLD);
			LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			params.setMargins(Utils.dpToPx(5, getResources()), Utils.dpToPx(5, getResources()), Utils.dpToPx(5, getResources()), Utils.dpToPx(5, getResources()));
			textButton.setLayoutParams(params);			
			addView(textButton);
			int textColor = attrs.getAttributeResourceValue(ANDROIDXML, "textColor", -1);
			if (textColor != -1)
			{
				textButton.setTextColor(textColor);
			}
			else
			{
				textColor = attrs.getAttributeIntValue(ANDROIDXML, "textColor", -1);
				if (textColor != -1)
					textButton.setTextColor(textColor);
			}
			int[] array = {android.R.attr.textSize};
			TypedArray values = getContext().obtainStyledAttributes(attrs, array);
	        float textSize = values.getDimension(0, -1);
	        values.recycle();
	        if (textSize != -1)
	        	textButton.setTextSize(textSize);

		}

		rippleSpeed = attrs.getAttributeFloatValue(MATERIALDESIGNXML,
												   "rippleSpeed", Utils.dpToPx(6, getResources())) * ((float)2.5);
	}
	Integer height;
	Integer width;
	@Override
	protected void onDraw(Canvas canvas) 
	{
		super.onDraw(canvas);
		if (x != -1)
		{
			Rect src = new Rect(0, 0, getWidth() - Utils.dpToPx(6, getResources()), getHeight() - Utils.dpToPx(7, getResources()));
			Rect dst = new Rect(Utils.dpToPx(6, getResources()), Utils.dpToPx(6, getResources()), getWidth() - Utils.dpToPx(6, getResources()), getHeight() - Utils.dpToPx(7, getResources()));
			canvas.drawBitmap(makeCircle(), src, dst, null);
			invalidate();
		}
	}

}
