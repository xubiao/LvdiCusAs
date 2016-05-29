package com.lvdi.ruitianxia_cus.global;

import com.lvdi.ruitianxia_cus.constants.OrderType;
import com.lvdi.ruitianxia_cus.model.CustomerC;

/**
 * 
 * 类的详细描述： 配置
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 上午1:14:37
 */
public class Config {
	public static final int CMODE = 1;
	public static final int BMODE = 2;
	public static int APPMODE = CMODE;
	public static final boolean DEBUG = false;
	public static final boolean CHECKTIME = false;
	public static final int INVALIDTIME = 20160110;
	/** 商城ID（全局配置常量）(h5加购物车用) */
	public static final String PRODUCT_STOREID = "9000";
	/*** b2c里categoryId */
	public static final String B2C_CATEGORYID = "Gshop";
	/*** b2c里categoryName */
	public static final String B2C_CATEGORYNAME = "绿地全球精选";
	/** 应用ID（全局配置常量）(h5加购物车用) */
	public static String CATALOGID = "Gshop";
	public static String CONTACT = "400-832-0087";
	public static long lastSendCodtTime;
	/**
	 * 订单类型(h5加购物车用)
	 */
	public static String ORDER_TYPE = OrderType.SALES_ORDER_B2C.toString();
	/***/
	public static CustomerC selectCustomerC;
	public static final String HTTPSUCCESSRESULT = "CODE-00000";
	public static String PHOTOFILENAME = "rtx_photo.jpg";
	public static String CROPFILENAME = "rtx_crop.jpg";

	// public static String IP = "101.201.170.141";// 测试
	// public static String PORT = "8080";// 测试

	public static String IP = "182.92.31.87";// 发布
	public static String PORT = "8080";// 发布

	public static String LAST = "/api/jsonws/";
	public static String HttpURLPrefix = "http://" + IP + ":" + PORT
			+ "/api/jsonws/";

	public static String HttpURLPrefix2 = "http://" + IP + ":" + PORT + "/";
	/**
	 * 图片地址前缀
	 */
	public static String HttpURLPrefix3 = "http://" + IP;

	/**
	 * jpush别名 后拼_dev（研发环境） _test（测试环境），现网不拼
	 */
	// public static String JPUSH_END = "_test";// 测试
	public static String JPUSH_END = "";// 发布

}
