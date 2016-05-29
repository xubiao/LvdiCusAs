package com.lvdi.ruitianxia_cus.global;

/**
 * 
 * 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 上午10:02:47
 */
public class HandleAction {

	/** 常量标定的起始值 */
	public static final int BASE_INIT_NUM = 0x00000100;

	/** 每种常量类型的间距，即每个类型中，最大的成员个数 */
	public static final int GAP = 0x00000100;

	/**
	 * 界面HTTP操作相关请求类型定义
	 */
	public static final class HttpType {
		public static final int HTTP_BASE_NUM = BASE_INIT_NUM + 2 * GAP;
		// 登录
		public static final int HTTP_LOGIN_SUCC = HTTP_BASE_NUM + 1;
		public static final int HTTP_LOGIN_FAIL = HTTP_BASE_NUM + 2;
		// 注册
		public static final int HTTP_REGISTER_SUCC = HTTP_BASE_NUM + 3;
		public static final int HTTP_REGISTER_FAIL = HTTP_BASE_NUM + 4;
		// 设置密码
		public static final int HTTP_SETPASSWORD_SUCC = HTTP_BASE_NUM + 5;
		public static final int HTTP_SETPASSWORD_FAIL = HTTP_BASE_NUM + 6;
		// 获取验证码
		public static final int HTTP_GET_VER_CODE_SUCC = HTTP_BASE_NUM + 7;
		public static final int HTTP_GET_VER_CODE_FAIL = HTTP_BASE_NUM + 8;
		// 修改个人信息
		public static final int HTTP_SET_PERSON_INFO_SUCC = HTTP_BASE_NUM + 9;
		public static final int HTTP_SET_PERSON_INFO_FAIL = HTTP_BASE_NUM + 10;
		// 获取地址列表
		public static final int HTTP_GET_ADDRESS_LIST_SUCC = HTTP_BASE_NUM + 11;
		public static final int HTTP_GET_ADDRESS_LIST_FAIL = HTTP_BASE_NUM + 12;
		// 添加地址
		public static final int HTTP_ADD_ADDRESS_SUCC = HTTP_BASE_NUM + 13;
		public static final int HTTP_ADD_ADDRESS_FAIL = HTTP_BASE_NUM + 14;
		// 删除地址
		public static final int HTTP_DELETE_ADDRESS_SUCC = HTTP_BASE_NUM + 15;
		public static final int HTTP_DELETE_ADDRESS_FAIL = HTTP_BASE_NUM + 16;
		// 更新地址
		public static final int HTTP_UPDATA_ADDRESS_SUCC = HTTP_BASE_NUM + 17;
		public static final int HTTP_UPDATA_ADDRESS_FAIL = HTTP_BASE_NUM + 18;
		// 设置默认地址
		public static final int HTTP_SET_DEFAULT_ADDRESS_SUCC = HTTP_BASE_NUM + 19;
		public static final int HTTP_SET_DEFAULT_ADDRESS_FAIL = HTTP_BASE_NUM + 20;
		// 获取省市区
		public static final int HTTP_GET_LOCATION_SUCC = HTTP_BASE_NUM + 21;
		public static final int HTTP_GET_LOCATION_FAIL = HTTP_BASE_NUM + 22;
		// C端首页获取所有项目
		public static final int HTTP_GET_POJECTSFORC_SUCC = HTTP_BASE_NUM + 23;
		public static final int HTTP_GET_POJECTSFORC_FAIL = HTTP_BASE_NUM + 24;
		// C端首页获取首页模板
		public static final int HTTP_GET_CUSTOMER_C_LAYOUT_SUCC = HTTP_BASE_NUM + 25;
		public static final int HTTP_GET_CUSTOMER_C_LAYOUT_FAIL = HTTP_BASE_NUM + 26;
		// 客户更新某项目的C端首页模板
		public static final int HTTP_UPDATE_CUSTOMER_C_LAYOUT_SUCC = HTTP_BASE_NUM + 27;
		public static final int HTTP_UPDATE_CUSTOMER_C_LAYOUT_FAIL = HTTP_BASE_NUM + 28;
		// 校验客户是否有权访问B
		public static final int HTTP_CKECK_PERMISSION_TOB_SUCC = HTTP_BASE_NUM + 29;
		public static final int HTTP_CKECK_PERMISSION_TOB_FAIL = HTTP_BASE_NUM + 30;
		// 获取某项目C端所有可用应用
		public static final int HTTP_GET_APPLICATIONS_IN_CONFIG_SUCC = HTTP_BASE_NUM + 31;
		public static final int HTTP_GET_APPLICATIONS_IN_CONFIG_FAIL = HTTP_BASE_NUM + 32;
		// 导航页条件查询C端应用
		public static final int HTTP_GET_APPLICATIONS_IN_NAVIGATOR_SUCC = HTTP_BASE_NUM + 33;
		public static final int HTTP_GET_APPLICATIONS_IN_NAVIGATOR_FAIL = HTTP_BASE_NUM + 34;
		// 获取用户的个人信息
		public static final int HTTP_GET_CUSTOMER_INFO_SUCC = HTTP_BASE_NUM + 35;
		public static final int HTTP_GET_CUSTOMER_INFO_FAIL = HTTP_BASE_NUM + 36;
		// 更新客户头像
		public static final int HTTP_UPDATE_CUSTOMER_HEAD_ICON_SUCC = HTTP_BASE_NUM + 37;
		public static final int HTTP_UPDATE_CUSTOMER_HEAD_ICON_FAIL = HTTP_BASE_NUM + 38;
		// 客户B端首页，获取请求时的项目ID可用的B端应用
		public static final int HTTP_GET_GET_APPLICATIONS_FORB_SUCC = HTTP_BASE_NUM + 39;
		public static final int HTTP_GET_GET_APPLICATIONS_FORB_FAIL = HTTP_BASE_NUM + 40;
		// 摇一摇
		public static final int HTTP_RANDOM_PRODUCT_SUCC = HTTP_BASE_NUM + 41;
		public static final int HTTP_RANDOM_PRODUCT_FAIL = HTTP_BASE_NUM + 42;
		// 校验验证码
		public static final int HTTP_CHECK_VER_CODE_SUCC = HTTP_BASE_NUM + 43;
		public static final int HTTP_CHECK_VER_CODE_FAIL = HTTP_BASE_NUM + 44;
		// 客户获取个人订单，预约数量接口
		public static final int HTTP_GET_CUSTOMER_MAIN_DATA_SUCC = HTTP_BASE_NUM + 46;
		public static final int HTTP_GET_CUSTOMER_MAIN_DATA_FAIL = HTTP_BASE_NUM + 47;

		// 查詢訂單列表
		public static final int HTTP_GET_OREDER_LIST_SUCC = HTTP_BASE_NUM + 48;
		public static final int HTTP_GET_OREDER_LIST_FAIL = HTTP_BASE_NUM + 49;
		public static final int HTTP_GET_OREDER_DETAIL_SUCC = HTTP_BASE_NUM + 50;
		public static final int HTTP_GET_OREDER_DETAIL_FAIL = HTTP_BASE_NUM + 51;
		// 4.21	商品库存检查接口
		public static final int HTTP_CHECK_INVENTORY_SUCC = HTTP_BASE_NUM + 52;
		public static final int HTTP_CHECK_INVENTORY_FAIL = HTTP_BASE_NUM + 53;
		// 4.22	购物车商品信息查询接口
		public static final int HTTP_CHECK_GET_SHOP_CARTS_SUCC = HTTP_BASE_NUM + 54;
		public static final int HTTP_CHECK_GET_SHOP_CARTS_FAIL = HTTP_BASE_NUM + 55;
		// 4.20	确认订单接口
		public static final int HTTP_CHECK_POST_CONFIRM_ORDER_SUCC = HTTP_BASE_NUM + 56;
		public static final int HTTP_CHECK_POST_CONFIRM_ORDER_FAIL = HTTP_BASE_NUM + 57;
		// 4.25	客户创建订单接口
		public static final int HTTP_CREATE_ORDER_SUCC = HTTP_BASE_NUM + 58;
		public static final int HTTP_CREATE_ORDER_FAIL = HTTP_BASE_NUM + 59;
		//4.28	支付宝APP支付接口
		public static final int HTTP_PAY_ALI_SUCESS = HTTP_BASE_NUM + 60;
		public static final int HTTP_PAY_ALI_FAIL = HTTP_BASE_NUM + 61;
		//4.29	微信APP支付接口(接口特殊,和其他不一样)
		public static final int HTTP_PAY_WX_SUCC = HTTP_BASE_NUM + 62;
		public static final int HTTP_PAY_WX_FAIL = HTTP_BASE_NUM + 63;
		//4.24	客户取消订单接口
		public static final int HTTP_CANCEL_ORDER_SUCC = HTTP_BASE_NUM + 64;
		public static final int HTTP_CANCEL_ORDER_FAIL = HTTP_BASE_NUM + 65;
		//客户确认收货接口
		public static final int HTTP_COMPLETE_ORDER_SUCC = HTTP_BASE_NUM + 66;
		public static final int HTTP_COMPLETE_ORDER_FAIL = HTTP_BASE_NUM + 67;
		//4.32	客户申请退款(refundOrder)
		public static final int HTTP_REFUND_ORDER_SUCC = HTTP_BASE_NUM + 68;
		public static final int HTTP_REFUND_ORDER_FAIL = HTTP_BASE_NUM + 69;		
	}

	/**
	 * Activity刷新页面请求类型定义
	 */
	public static final class ActivityType {
		public static final int UI_BASE_NUM = BASE_INIT_NUM + 3 * GAP;

	}
}
