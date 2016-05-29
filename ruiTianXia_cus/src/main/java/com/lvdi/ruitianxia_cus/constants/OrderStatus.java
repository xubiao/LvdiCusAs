
package com.lvdi.ruitianxia_cus.constants;
	
public enum OrderStatus {
	/**
	 * //O2O ,b2c订单初始状态
	 */
	ORDER_CREATED("ORDER_CREATED","待付款","预约中"),   //预约中
	/**
	 *  020 （已支付）
	 */
	ORDER_APPROVED("ORDER_APPROVED","待接单","预约中"), //
	/**
	 * 商家接单(020) b2c已支付状态
	 */
	ORDER_PROCESSING("ORDER_PROCESSING","待发货","商家已确认"), // 商家已确认
	
	/**
	 * 取消成功
	 */
	ORDER_CANCELLED("ORDER_CANCELLED","订单关闭","已取消"), //已取消
	/**
	 * 已发货
	 */
	ORDER_SENT("ORDER_SENT","待收货","待收货"), //
	/**
	 * 商家拒单
	 */
	ORDER_REJECTED("ORDER_REJECTED","商家拒单","已取消"), //已取消
	/**
	 * 订单完成
	 */
	ORDER_COMPLETED("ORDER_COMPLETED","订单完成","已完成"), // 
	/**
	 * 申请退货（退款）
	 */
	ORDER_REFUND_CREATED("RETURN_REQUESTED","申请退款","申请退款"), //
	/**
	 * 拒绝退货（退款）
	 */
	ORDER_REFUND_REJECTED("RETURN_MAN_REFUND","拒绝退货","拒绝退货"), //
	/**
	 * 退款中（同意退款）
	 */
	ORDER_REFUND_PROCESSING("RETURN_ACCEPTED","退款中","退款中"), //
	/**
	 * 退货（退款）完成
	 */
	ORDER_REFUND_COMPLETED("RETURN_COMPLETED","退款成功","退款成功");//
	
	private String status;
	private String label;
	private String appointlabel;
	private OrderStatus(String status,String label,String appointlabel){
		this.status = status;
		this.label = label;
		this.appointlabel = appointlabel;
	}
	
	public static OrderStatus getByKey(String key){
		OrderStatus type = null;
		for(OrderStatus typeTemp:OrderStatus.values()){
			if(key.equals(typeTemp.toString()))
				return type = typeTemp;
		}
		return type;
	}
	
	public String toString(){
		return status;
	} 
	
	/**
	 * @return 返回b2c、o2o订单对应订单状态下应显示的文本
	 */
	public String getLable(){
		return label;
	}
	
	/**
	 * @return 返回预约订单对应订单状态下应显示的文本
	 */
	public String getAppointlabel(){
		return appointlabel;
	}
}

	