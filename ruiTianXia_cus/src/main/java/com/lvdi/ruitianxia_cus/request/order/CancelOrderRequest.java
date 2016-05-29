package com.lvdi.ruitianxia_cus.request.order;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.BaseObject;
import com.lvdi.ruitianxia_cus.model.shopcart.RspConfrimOrder;
import com.lvdi.ruitianxia_cus.request.BaseRequest;

/**
 * 4.24	客户取消订单接口（cancleOrder）
 * 
 * @version 1.0.1
 */
public class CancelOrderRequest extends BaseRequest {
	private Handler mHandler;
	private String order_id;// 产品ID

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		order_id = params[0];
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
		return IStrutsAction.HTTP_POST_CANCEL_ORDER;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("orderId", order_id);
		params.put("userLoginId", Cache.getUser().userName);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d("ConfirmOrderRequest", "onSuccess--" + "statusCode:"
				+ statusCode + "content:" + content);

		BaseObject baseObject = (BaseObject) AbJsonUtil.fromJson(content, BaseObject.class);
		Message msg;
		if (baseObject != null && baseObject.resultCode.equals(Config.HTTPSUCCESSRESULT)
				 ) {
			msg = mHandler
					.obtainMessage(HandleAction.HttpType.HTTP_CANCEL_ORDER_SUCC);
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CANCEL_ORDER_FAIL, "取消订单失败" );
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
				HandleAction.HttpType.HTTP_CANCEL_ORDER_FAIL, "取消订单失败");
		mHandler.sendMessage(msg);
	}

}
