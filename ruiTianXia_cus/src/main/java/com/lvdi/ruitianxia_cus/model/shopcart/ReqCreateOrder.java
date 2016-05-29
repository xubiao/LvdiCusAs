package com.lvdi.ruitianxia_cus.model.shopcart;

import java.util.List;

/**
 * @author Administrator
 * 4.25	客户创建订单接口
 */
public class ReqCreateOrder {
	/**  商户id*/
	public String productStoreId;
	/** catalogId */
	public String catalogId;
	/** 备注 */
	public String remark;
	/** 用户登录账号 */
	public String userLoginId;
	/**  categoryId*/
	public String categoryId;
	/**邮寄地址Id  */
	public String contactMechId;
	/** 预约时间 */
	public String reservationDate;
	/** 订单类型 */
	public String orderTypeId;
	/** 促销吗 */
	public String promoCode;
	/** 产品条目 */
	public List<ReqProductItem> productItems;
}
