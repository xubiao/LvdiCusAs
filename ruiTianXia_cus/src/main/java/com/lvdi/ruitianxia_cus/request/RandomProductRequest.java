package com.lvdi.ruitianxia_cus.request;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.RandomProduct;

/**
 * 摇一摇 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月11日 下午6:50:04
 */
public class RandomProductRequest extends BaseRequest {
	private Handler mHandler;
	private static RandomProductRequest randomProductRequest;

	public static RandomProductRequest getInstance() {
		if (null == randomProductRequest) {
			randomProductRequest = new RandomProductRequest();
		}
		return randomProductRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
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
		return IStrutsAction.HTTP_RANDOM_PRODUCT;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d("LoginRequest", "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		RandomProduct randomProduct = (RandomProduct) AbJsonUtil.fromJson(
				content, RandomProduct.class);
		Message msg;
		if (null != randomProduct
				&& randomProduct.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_RANDOM_PRODUCT_SUCC,
					randomProduct);
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_RANDOM_PRODUCT_FAIL,
					null != randomProduct ? randomProduct.errorMessage
							: "获取数据失败");
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
				HandleAction.HttpType.HTTP_RANDOM_PRODUCT_FAIL, "获取数据失败");
		mHandler.sendMessage(msg);
	}

}
