package com.lvdi.ruitianxia_cus.model.shopcart;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.lvdi.ruitianxia_cus.model.BaseObject;

public class RspConfrimOrder extends BaseObject{

	/**购物车信息 */
	public ShopCartInfo shoppingCartInfo;
	
	
	public class ShopCartInfo implements Serializable{
		/** 商户名称*/
		public String categoryName;
		/** 购买商品总数量*/
		public String totalQuantity;
		/** 总优惠价格*/
		public double promoAmount;
		/**购物车所有产品总价 */
		public String totalAmount;
		/**运费（UI界面显示的运费） */
		public String totalShipping;
		/**支付价格（UI界面显示实付价格） */
		public String totalPayAmount;
//		/***兑换码优惠金额 */
//		public String discountAmount;
		/**购买产品列表信息 */
		public ArrayList<CartItem> cartItems;
	}
	
	public class CartItem implements Serializable{
		/** 产品总价 */
		public String ItemTotalPrice;
		/** 产品单价 */
		public String ItemPrice;
		/** 产品数量 */
		public String ItemQuantity;
		/** 产品ID */
		public String productId;
		/** 产品优惠价 */
		public String ItemPromo;
		
		public String smallImageUrl;
		/**
		 * 
		 */
		public String productName;
	}
}
