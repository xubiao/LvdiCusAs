package com.lvdi.ruitianxia_cus.model.shopcart;

import java.io.Serializable;
import java.util.List;

import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.model.BaseObject;

/**
 *4.20	确认订单接口
 */
public class ReqConfirmOrder implements Serializable {
	
	/**用户帐号（手机号） */
	public String userLoginId;
	/** 商城ID（全局配置常量） */
	public String productStoreId  = Config.PRODUCT_STOREID;
	/** 应用ID（通过H5加入购物车获取ID） */
	public String catalogId ;
	/**  商户ID*/
	public String categoryId;
	/** 订单类型（具体枚举值见附录） */
	public String orderTypeId;
	/** 促销码（可选） */
	public String promoCode;
	/** 产品条目 */
	public List<ReqProductItem> productItems;
}
