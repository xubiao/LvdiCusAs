package com.lvdi.ruitianxia_cus.model.shopcart;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 * 购物车-店铺信息
 */
public class DbCategory implements Serializable{
	/**
	 * 店铺id
	 */
	public String categoryId;
	/**
	 * 应用ID（通过H5加入购物车获取ID）
	 */
	public String catalogId;
	
	/**
	 * 订单类型
	 */
	public String orderTypeId;
	
	/**对应的商品列表*/
	public List<DbProduct> prodList;
}
