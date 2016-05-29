package com.lvdi.ruitianxia_cus.model.order;

import com.ab.util.AbJsonUtil;

/**
 * 
 * 订单详情
 */
public class OrderProduct {

	/**
	 * 商品id
	 */
	public String productId;
	/**
	 * 商品名称
	 */
	public String productName;
	/**
	 * 商品类型
	 */
	public String productTypeId;
	/**
	 * 数量
	 */
	public String quantity;
	/**
	 * 缩略图
	 */
	public String smallImageUrl;
	/**
	 * 单价
	 */
	public String unitPrice;
	 
	/**
	 * 
	 */
	private static final long serialVersionUID = -5229045440621971454L;

	public String toJson() {
		// TODO Auto-generated method stub
		return AbJsonUtil.toJson(this);
	}

}
