package com.lvdi.ruitianxia_cus.model.shopcart;

import java.io.Serializable;

import net.tsz.afinal.annotation.sqlite.Id;
import net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "table_product_shopcart")
public class DbProduct implements Serializable{
	@Id
	public int _id;
	/**
	 * 店铺id
	 */
	public String categoryId;
	/**
	 * 数量
	 */
	public int quantity; 
	/**
	 * 商品Id
	 */
	public String productId; 
	
	/**
	 * 应用ID（通过H5加入购物车获取ID）
	 */
	public String catalogId;
	
	/**
	 * 订单类型
	 */
	public String orderTypeId;
	public String getCatalogId() {
		return catalogId;
	}
	public void setCatalogId(String catalogId) {
		this.catalogId = catalogId;
	}
	public String getOrderTypeId() {
		return orderTypeId;
	}
	public void setOrderTypeId(String orderTypeId) {
		this.orderTypeId = orderTypeId;
	}
	public int get_id() {
		return _id;
	}
	public void set_id(int _id) {
		this._id = _id;
	}
	public String getCategoryId() {
		return categoryId;
	}
	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}
	 
	public int getQuantity() {
		return quantity;
	}
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	public String getProductId() {
		return productId;
	}
	public void setProductId(String productId) {
		this.productId = productId;
	}
	 
}
