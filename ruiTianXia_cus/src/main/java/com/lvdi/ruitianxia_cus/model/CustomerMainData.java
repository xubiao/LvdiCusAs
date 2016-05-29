package com.lvdi.ruitianxia_cus.model;

/**
 * 
 * 类的详细描述： 客户获取个人订单，预约数量接口
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月19日 下午12:44:36
 */
public class CustomerMainData extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8786324671332723703L;

	/**
	 * 待付款
	 */
	public int noPayCount;// 
	/**
	 * 待收货
	 */
	public int orderSentCount;// 待收货
	/**
	 * 待发货
	 */
	public int processingCount;// 
	/**
	 * 待退款
	 */
	public int refundProcessingCount;// 
	/**
	 * 预约中
	 */
	public int reservationCount;// 
}
