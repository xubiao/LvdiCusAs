package com.lvdi.ruitianxia_cus.model.order;

import com.ab.util.AbJsonUtil;

/**
 * 
 * 订单详情
 */
public class OrderHeader {

	/**
	 * 创建人
	 */
	public String createBy;
	/**
	 *总价
	 */
	public String grandTotal;
	/**
	 * 创建时间
	 */
	public String orderDate;
	/**
	 * 订单id
	 */
	public String orderId;
	/**
	 * 订单状态
	 */
	public String orderStatus;
	/**
	 * 订单类型
	 */
	public String orderTypeId;
	/**
	 * 除去邮费外的价格
	 */
	public String remainingSubTotal;
	/**
	 * 预约时间
	 */
	public String reservationDate;
	/**
	 * 商户id
	 */
	public String storeId;
	/**
	 * 商户名称
	 */
	public String storeName;
	
	/**
	 * 备注
	 */
	public String remark;
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -5229045440621971454L;

	public String toJson() {
		// TODO Auto-generated method stub
		return AbJsonUtil.toJson(this);
	}

}
