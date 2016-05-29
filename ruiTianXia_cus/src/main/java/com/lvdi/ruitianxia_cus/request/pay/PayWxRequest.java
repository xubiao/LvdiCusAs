package com.lvdi.ruitianxia_cus.request.pay;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestGetJsonParams;
import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.order.PayInfoWx;
import com.lvdi.ruitianxia_cus.model.order.PayInfoWx.WxPayBean;
import com.lvdi.ruitianxia_cus.request.BaseRequest;

/**
 * 4.28 支付宝APP支付接口
 * 
 * @version 1.0.1
 */
public class PayWxRequest extends BaseRequest {
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
		return Config.HttpURLPrefix2;
	}

	@Override
	public String getAction() {
		// TODO Auto-generated method stub
		return IStrutsAction.HTTP_GET_PAY_WX;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestGetJsonParams params = new AbRequestGetJsonParams();
		params.put("orderId", order_id);
		params.put("userLoginId", Cache.getUser().userName);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d("ConfirmOrderRequest", "onSuccess--" + "statusCode:"
				+ statusCode + "content:" + content);
		// PayInfoWx

		Message msg;
		try {
//			if(baseObject == null)
//			baseObject = new Gson().fromJson(content, PayInfoWx.class);
			
			JSONObject job = new JSONObject(content);
			String resultCode = job.getString("resultCode");

			String str1=  job.getString("payInfo");
			JSONObject payInfo = new JSONObject(str1);
			
			WxPayBean mPayInfo = new WxPayBean();
			mPayInfo.appid = payInfo.getString("appid");
			mPayInfo.noncestr = payInfo.getString("noncestr");
			mPayInfo.packageValue = payInfo.getString("packageValue");
			mPayInfo.partnerid = payInfo.getString("partnerid");
			mPayInfo.prepayid = payInfo.getString("prepayid");
			mPayInfo.sign = payInfo.getString("sign");
			mPayInfo.timestamp = payInfo.getString("timestamp");
			
			
			if (mPayInfo != null 
					&&  resultCode.equals(Config.HTTPSUCCESSRESULT)) {
				msg = mHandler
						.obtainMessage(HandleAction.HttpType.HTTP_PAY_WX_SUCC);
				msg.obj = mPayInfo;
			} else {
				msg = mHandler.obtainMessage(
						HandleAction.HttpType.HTTP_PAY_WX_FAIL, "支付失败");
			}
			
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_PAY_WX_FAIL, "支付失败");
			e.printStackTrace();
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
				HandleAction.HttpType.HTTP_PAY_WX_FAIL, "支付失败");
		mHandler.sendMessage(msg);
	}

}
