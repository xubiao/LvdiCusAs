package com.lvdi.ruitianxia_cus.request;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.BaseObject;

/**
 * 4.21	商品库存检查接口
 * 
 * @version 1.0.1
 */
public class CheckInventoryRequest extends BaseRequest {
	private Handler mHandler;
	private String productId;//产品ID
	private String quantity;// 商品数量
	private static CheckInventoryRequest getVerCodeRequest;
	private	Bundle data;
	public static CheckInventoryRequest getInstance() {
		if (null == getVerCodeRequest) {
			getVerCodeRequest = new CheckInventoryRequest();
		}
		return getVerCodeRequest;
	}

	public void sendRequest(Handler handler,Bundle data, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		productId = params[0];
		quantity = params[1];
		this.data = data;
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
		return IStrutsAction.HTTP_CHECK_INVENTORY;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("productId", productId);
		params.put("quantity", quantity);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d("LoginRequest", "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		BaseObject baseObject = (BaseObject) AbJsonUtil.fromJson(content,
				BaseObject.class);	
		Message msg;
		if (null != baseObject
				&& baseObject.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			boolean flag = false;
			try {
				//true:有库存
               // false:库存不够

				flag =	getBoolean(new JSONObject(content), "flag", false);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CHECK_INVENTORY_SUCC );
			msg.setData(data);
			msg.obj = flag;
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CHECK_INVENTORY_FAIL,
					null != baseObject ? baseObject.errorMessage : "检查库存失败");
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
				HandleAction.HttpType.HTTP_CHECK_INVENTORY_FAIL, "检查库存失败");
		mHandler.sendMessage(msg);
	}

}
