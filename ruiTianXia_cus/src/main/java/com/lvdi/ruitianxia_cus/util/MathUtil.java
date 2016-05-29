package com.lvdi.ruitianxia_cus.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.text.TextUtils;

public class MathUtil {

	/**
	 * @param 去掉小数点后的值
	 * @return
	 */
	public static String formatNoPoint(String str) {
		if(TextUtils.isEmpty(str)){
			return "";
		}
		BigDecimal bd = new BigDecimal(str).setScale(0, BigDecimal.ROUND_FLOOR);
		return bd.toString();
	}
	/**
	 * 
	 * @Description:如果输入为12.010000，返回 12.01 ;如果输入为12，返回12.00
	 * @param:
	 */
	public static String formatPrice(String price) {
		String tempPrice = "0.00";
		double d = 0;
		if (null != price && !"".equals(price)) {
			try {
				d = Double.parseDouble(price);
			} catch (NumberFormatException e) {
			}

			if (d != 0) {
				DecimalFormat df = new DecimalFormat("##0.00");
				tempPrice = df.format(Double.parseDouble(price));
			}
		}
		return tempPrice;
	}
}
