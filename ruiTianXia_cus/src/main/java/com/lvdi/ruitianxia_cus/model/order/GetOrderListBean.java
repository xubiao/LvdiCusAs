package com.lvdi.ruitianxia_cus.model.order;

import java.util.List;

import com.lvdi.ruitianxia_cus.model.BaseObject;

/**
 * @author Administrator
 * 订单列表对象
 */ 
public class GetOrderListBean extends BaseObject{
	/**
	 * 是否有下一页(Y:有 N:没有)
	 */
	public String nextPage;
	/**
	 * 总数
	 */
	public String totalCount;
	/**
	 * 
	 */
	public List<OrderLListItem> orderList;
}
