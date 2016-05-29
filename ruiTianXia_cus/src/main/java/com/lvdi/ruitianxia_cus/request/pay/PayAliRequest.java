package com.lvdi.ruitianxia_cus.request.pay;

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
import com.lvdi.ruitianxia_cus.model.shopcart.RspConfrimOrder;
import com.lvdi.ruitianxia_cus.request.BaseRequest;

/**
 * 4.28 支付宝APP支付接口
 * 
 * @version 1.0.1
 */
public class PayAliRequest extends BaseRequest {
	private Handler mHandler;
	private String order_id;// 产品ID

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		order_id = params[0];
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
		return IStrutsAction.HTTP_GET_PAY_ALI;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("order-id", order_id);
		params.put("user-login-id", Cache.getUser().userName);
		
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d("ConfirmOrderRequest", "onSuccess--" + "statusCode:"
				+ statusCode + "content:" + content);

		JSONObject jsonObject = resolveJson(content);
		String resultCode = getString(jsonObject, "resultCode", "");
		String payInfo = getString(jsonObject, "payInfo", "");
		Message msg;
		if (resultCode.equals(Config.HTTPSUCCESSRESULT)
				&& !TextUtils.isEmpty(payInfo)) {
			msg = mHandler
					.obtainMessage(HandleAction.HttpType.HTTP_PAY_ALI_SUCESS);
			msg.obj = payInfo;
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_PAY_ALI_FAIL,
					getString(jsonObject, "errorMessage", "支付失败"));
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
				HandleAction.HttpType.HTTP_PAY_ALI_FAIL, "支付失败");
		mHandler.sendMessage(msg);
	}

}
