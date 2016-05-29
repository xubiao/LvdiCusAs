package com.lvdi.ruitianxia_cus.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/***
 * 宽高一样 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月27日 下午11:19:31
 */
public class SquareLayout extends RelativeLayout {
	public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public SquareLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public SquareLayout(Context context) {
		super(context);
	}

	@SuppressWarnings("unused")
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

		setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
				getDefaultSize(0, heightMeasureSpec));

		// Children are just made to fill our space.
		int childWidthSize = getMeasuredWidth();
		int childHeightSize = getMeasuredHeight();
		// 高度和宽度一样
		heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(
				childWidthSize, MeasureSpec.EXACTLY);
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
}
