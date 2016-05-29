package com.lvdi.ruitianxia_cus.model.shopcart;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "table_product_shopcart")
public class ProductInfo implements Serializable{
	public int _id;
	/**
	 *商品名称
	 */
	public String productName;
	/**
	 * 商品数量（库存够的话返回传入的数量，否则返回剩下的库存的值）
	 */
	public int quantity; 
	/**
	 * 产品id
	 */
	public String productId;
	/**
	 *商品类型（不需要展示）
	 */
	public String productTypeId;
	/**
	 *图片地址(相对路径)
	 */
	public String smallImageUrl;
	/**
	 *商品价格
	 */
	public String unitPrice;
	 
}
