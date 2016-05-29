package com.lvdi.ruitianxia_cus.model.order;

import java.util.List;

import com.lvdi.ruitianxia_cus.model.AddressInfo;
import com.lvdi.ruitianxia_cus.model.BaseObject;

/**
 * @author Administrator
 * 
 */
public class OrderDetail extends BaseObject{
	/**
	 * 快递单号
	 */
	public String trackingNum;
	/**
	 * 快递公司
	 */
	public String expressCompany; 
	/**
	 * 下单时间
	 */
	public String createDate; 
	/**
	 * 发货时间（未发货，该值为空）
	 */
	public String sentDate; 
	/**
	 * 收货时间（未收货，该值为空）
	 */
	public String completedDate; 
	/**
	 * 配送费
	 */
	public String orderShippingTotal; 
	/**
	 *  
	 */
	public String orderSubTotal; 
	/**
	 *  优惠金额
	 */
	public String discountAmount; 
	/**
	 * 退货理由(未申请退货，该值为空)
	 */
	public String returnReason; 
	public OrderHeader orderHeader;  
	public List<OrderProduct> productList;
	/**
	 * 收货地址
	 */
	public AddressInfo postAddress;
}
