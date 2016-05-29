package com.lvdi.ruitianxia_cus.global;

/**
 * 
 * 类的详细描述： Http请求地址
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 上午1:13:03
 */
public class IStrutsAction {
	// 登录
	public static String HTTP_LOGIN = "RUI-CustomerJSONWebService-portlet.account/customer-login";
	// 注册
	public static String HTTP_REGISTER = "RUI-CustomerJSONWebService-portlet.account/customer-register";
	// 添加地址
	public static String HTTP_ADD_ADDRESS = "RUI-CustomerJSONWebService-portlet.postaladdress/create-customer-postal-address";
	// 获取地址列表
	public static String HTTP_GET_ADDRESS = "RUI-CustomerJSONWebService-portlet.postaladdress/get-customer-postal-address";
	// 修改地址
	public static String HTTP_UPDATA_ADDRESS = "RUI-CustomerJSONWebService-portlet.postaladdress/update-customer-postal-address";
	// 删除地址
	public static String HTTP_DELETE_ADDRESS = "RUI-CustomerJSONWebService-portlet.postaladdress/delete-customer-postal-address";
	// 设置默认地址
	public static String HTTP_SET_DEFAULT_ADDRESS = "RUI-CustomerJSONWebService-portlet.postaladdress/config-as-default-postal-address";
	// 获取省市区
	public static String HTTP_GETLOCATION_ADDRESS = "RUI-CustomerJSONWebService-portlet.postaladdress/get-locations-data";
	// C端首页获取所有项目
	public static String HTTP_GET_PROJECTS_FORC = "RUI-CustomerJSONWebService-portlet.customerlayout/get-projects-for-c";
	// C端首页获取首页模板
	public static String HTTP_GET_CUSTOMERCLAYOUT_FORC = "RUI-CustomerJSONWebService-portlet.customerlayout/get-customer-c-layout";
	// 户更新某项目的C端首页模板
	public static String HTTP_UPDATA_CUSTOMERCLAYOUT_FORC = "RUI-CustomerJSONWebService-portlet.customerlayout/update-customer-c-layout";
	// 校验客户是否有权访问B端
	public static String HTTP_CHECK_PERMISSION_TOB = "RUI-CustomerJSONWebService-portlet.customerlayout/check-permission-to-b";
	// 获取某项目C端所有可用应用
	public static String HTTP_GET_APPLICATIONS_IN_CONFIG = "RUI-CustomerJSONWebService-portlet.application/get-applications-in-config";
	// 导航页条件查询C端应用
	public static String HTTP_GET_APPLICATIONS_IN_NAVIGATOR = "RUI-CustomerJSONWebService-portlet.application/get-applications-in-navigator";
	// 获取用户的个人信息接口
	public static String HTTP_GET_CUSTOMER_INFO = "RUI-CustomerJSONWebService-portlet.account/get-customer-info";
	// 更新客户的某一项信息接口
	public static String HTTP_UPDATE_CUSTOMER_INFO = "RUI-CustomerJSONWebService-portlet.account/update-customer-info";
	// 更新客户的头像息接口
	public static String HTTP_UPDATE_CUSTOMER_HEAD = "RUI-CustomerJSONWebService-portlet.account/update-customer-head-icon";
	// 客户修改密码接口
	public static String HTTP_FORGET_PASSWORD = "RUI-CustomerJSONWebService-portlet.account/forget-password";
	// 客户B端首页，获取请求时的项目ID可用的B端应用
	public static String HTTP_GET_APPLICATIONS_FORB = "RUI-CustomerJSONWebService-portlet.application/get-applications-for-b";
	// 摇一摇
	public static String HTTP_RANDOM_PRODUCT = "RUI-CustomerJSONWebService-portlet.product/random-product";
	// 获取验证码
	public static String HTTP_GET_VER_CODE = "RUI-CustomerJSONWebService-portlet.account/send-captcha";
	// 检验验证码
	public static String HTTP_CHECK_VER_CODE = "RUI-CustomerJSONWebService-portlet.account/check-captcha";
	// 客户获取个人订单，预约数量接口
	public static String HTTP_GET_CUSTOMER_MAIN_DATA = "RUI-CustomerJSONWebService-portlet.order/get-customer-main-data";
	// 4.31	客户查询订单列表
    public static String HTTP_GET_ORDER_LIST = "RUI-CustomerJSONWebService-portlet.order/get-order-list";
    //4.23	客户查询订单详情
    public static String HTTP_GET_ORDER_DETAIL = "RUI-CustomerJSONWebService-portlet.order/get-order-view";
	// 检查库存
	public static String HTTP_CHECK_INVENTORY = "RUI-CustomerJSONWebService-portlet.order/check-inventory";
	// 购物车商品信息查询接口
	public static String HTTP_GET_SHOP_CARTS = "RUI-CustomerJSONWebService-portlet.order/get-shopping-carts";
	// 4.20	确认订单接口
	public static String HTTP_POST_CONFIRM_ORDER = "RUI-CustomerJSONWebService-portlet.order/confirm-order";
	// 4.25	客户创建订单接口
	public static String HTTP_POST_CREATE_ORDER = "RUI-CustomerJSONWebService-portlet.order/create-sales-order";
	// 支付宝支付接口
	public static String HTTP_GET_PAY_ALI = "RUI-CustomerJSONWebService-portlet.payment/pay-order-by-alipay-app";
	// 4.29	微信APP支付接口(接口特殊,和其他不一样)
	public static String HTTP_GET_PAY_WX = "RUI-CustomerJSONWebService-portlet/weixin/pay";
	// 4.24	客户取消订单接口（cancleOrder）
	public static String HTTP_POST_CANCEL_ORDER = "RUI-CustomerJSONWebService-portlet.order/cancle-order";
	// 	4.30	客户确认收货接口
	public static String HTTP_GET_COMPLETE_ORDER = "RUI-CustomerJSONWebService-portlet.order/complete-order";
	// 4.32	客户申请退款(refundOrder)
	public static String HTTP_POST_REFUND_ORDER = "RUI-CustomerJSONWebService-portlet.order/refund-order";	
	
}
