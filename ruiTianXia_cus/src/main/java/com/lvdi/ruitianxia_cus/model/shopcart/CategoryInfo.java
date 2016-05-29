package com.lvdi.ruitianxia_cus.model.shopcart;

import java.io.Serializable;
import java.util.List;

/**
 * @author Administrator
 * 购物车-店铺信息
 */
public class CategoryInfo implements Serializable{
	/**
	 * 店铺id
	 */
	public String categoryId;
	/**
	 * 商户名称
	 */
	public String categoryName;
	
	/**对应的商品列表*/
	public List<ProductInfo> prodList;
}
