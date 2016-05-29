
package com.lvdi.ruitianxia_cus.constants;
	
public enum OrderType {
	/**
	 * 020订单
	 */
	SALES_ORDER_O2O_SALE("SALES_ORDER_O2O_SALE"),   
	/**
	 * 020预约
	 */
	SALES_ORDER_O2O_SERVICE("SALES_ORDER_O2O_SERVICE"),  
	/**
	 * B2C订单
	 */
	SALES_ORDER_B2C("SALES_ORDER_B2C"),  
	/**
	 * 所有订单
	 */
	SALES_ORDER_ALL(""),
	;
	
	
	private OrderType(String status){
		this.status = status;
	}
	
	private String status;
	
	public String toString(){
		return status;
	} 
}

	