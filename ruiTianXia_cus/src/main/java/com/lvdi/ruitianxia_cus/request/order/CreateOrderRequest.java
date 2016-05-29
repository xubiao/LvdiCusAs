package com.lvdi.ruitianxia_cus.request.order;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.http.AbRequestPostJsonParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.shopcart.RspConfrimOrder;
import com.lvdi.ruitianxia_cus.model.shopcart.RspCreateOrder;
import com.lvdi.ruitianxia_cus.request.BaseRequest;

/**
 * 4.25	客户创建订单接口
 * 
 * @version 1.0.1
 */
public class CreateOrderRequest extends BaseRequest {
	private Handler mHandler;
	private String content;//产品ID

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		content = params[0];
		httpConnect(true, this);
	}

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return Config.HttpURLPrefix;
	}

	@Override
	public String getAction() {
		// TODO Auto-generated method stub
		return IStrutsAction.HTTP_POST_CREATE_ORDER;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestPostJsonParams params = new AbRequestPostJsonParams();
		params.put("content", content);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d("ConfirmOrderRequest", "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		RspCreateOrder baseObject = (RspCreateOrder) AbJsonUtil.fromJson(content,
				RspCreateOrder.class);	
		Message msg;
		if (null != baseObject
				&& baseObject.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CREATE_ORDER_SUCC );
			msg.obj = baseObject;
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CREATE_ORDER_FAIL,
					null != baseObject ? baseObject.errorMessage : "创建订单失败");
		}
		mHandler.sendMessage(msg);
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFinish() {
		// TODO Auto-generated method stub
	}

	@Override
	public void onFailure(int statusCode, String content, Throwable error) {
		// TODO Auto-generated method stub
		AbLogUtil.d("LoginRequest", "onFailure--" + "statusCode:" + statusCode
				+ "content:" + content);
		Message msg = mHandler.obtainMessage(
				HandleAction.HttpType.HTTP_CHECK_POST_CONFIRM_ORDER_FAIL, "创建订单失败");
		mHandler.sendMessage(msg);
	}

}
