/***************************************************************************
 * 
 * Copyright (c) by ihiyo.com, Inc. All Rights Reserved
 * 
 **************************************************************************/
package com.ywl5320.player.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 功能：一个正方形的ImageView<br>
 */
public class SquareImageView extends AppCompatImageView {

	private Context context;

	public SquareImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;
	}

	public SquareImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.context = context;
	}

	public SquareImageView(Context context) {
		super(context);
		this.context = context;
	}

	@SuppressWarnings("unused")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		// For simple implementation, or internal size is always 0.
		// We depend on the container to specify the layout size of
		// our view. We can't really know what it is since we will be
		// adding and removing different arbitrary views and do not
		// want the layout to change as this happens.
		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec), getDefaultSize(0, heightMeasureSpec));

		// Children are just made to fill our space.
		int childWidthSize = getMeasuredWidth();
		int childHeightSize = getMeasuredHeight();
		// 高度和宽度一样
		heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(childWidthSize, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}

	@Override
	protected void onDraw(Canvas canvas) { // 这里就是重写的方法了，想画什么形状自己动手
		// TODO Auto-generated method stub
		super.onDraw(canvas);
	}

}
