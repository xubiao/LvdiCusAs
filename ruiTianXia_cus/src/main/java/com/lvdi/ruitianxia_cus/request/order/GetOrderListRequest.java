package com.lvdi.ruitianxia_cus.request.order;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.constants.PageInfo;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.ProjectForC;
import com.lvdi.ruitianxia_cus.model.order.GetOrderListBean;
import com.lvdi.ruitianxia_cus.request.BaseRequest;

/**
 * 4.31	客户查询订单列表
 * 
 * @version 1.0.1
 */
public class GetOrderListRequest extends BaseRequest {
	private Handler mHandler;
	private String order_status_id;// 订单状态
	private String order_type_id;// 订单类型
	private String page;// 显示第几页
	private String row;// 每页显示条数


	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		page = params[0];
		order_status_id =  params[1] ;
		order_type_id = params[2];
		httpConnect(false, this);
	}

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return Config.HttpURLPrefix;
	}

	@Override
	public String getAction() {
		// TODO Auto-generated method stub
		return IStrutsAction.HTTP_GET_ORDER_LIST;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("user-login-id", Cache.getUser().userName);
		params.put("order-id", "");
		params.put("order-status-id", order_status_id);//订单状态
		params.put("order-type-id", order_type_id);//订单状态
		params.put("page", page);// 
		params.put("row", PageInfo.PAGE_NUM);// 

		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		GetOrderListBean mBean = (GetOrderListBean) AbJsonUtil.fromJson(content, GetOrderListBean.class );
		Message msg;
		if(mBean != null){
		    msg = mHandler.obtainMessage(HandleAction.HttpType.HTTP_GET_OREDER_LIST_SUCC 
				 );
		    msg.obj = mBean;
		}else{
			msg = mHandler.obtainMessage(HandleAction.HttpType.HTTP_GET_OREDER_LIST_FAIL 
					 ,"");
		}
		mHandler.sendMessage(msg);
	}

	@Override
	public void onStart() {
	}

	@Override
	public void onFinish() {
	}

	@Override
	public void onFailure(int statusCode, String content, Throwable error) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onFailure--" + "statusCode:" + statusCode
				+ "content:" + content);
		Message msg = mHandler.obtainMessage(
				HandleAction.HttpType.HTTP_GET_OREDER_LIST_FAIL, "获取数据失败");
		mHandler.sendMessage(msg);
	}

}
