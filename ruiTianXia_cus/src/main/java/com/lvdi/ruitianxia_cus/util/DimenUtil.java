package com.lvdi.ruitianxia_cus.util;

import android.content.Context;

public class DimenUtil {

	/**
	 * @Description:dip转换为px
	 */
	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int sp2px(Context context, float spValue) {
		final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
		return (int) (spValue * fontScale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, int pxValue) {
		float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * @param context
	 * @param initWidth
	 * @param initHeight
	 * @param targetWidth
	 */
	public static float getHeight(float initWidth, float initHeight,
			float targetWidth) {
		return initHeight * targetWidth / initWidth;
	}
}
