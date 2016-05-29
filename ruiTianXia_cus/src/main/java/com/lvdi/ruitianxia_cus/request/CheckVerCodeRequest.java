package com.lvdi.ruitianxia_cus.request;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.BaseObject;
import com.lvdi.ruitianxia_cus.model.CustomerC;
import com.lvdi.ruitianxia_cus.model.RandomProduct;

/**
 * 校验验证码 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月14日 下午3:30:22
 */
public class CheckVerCodeRequest extends BaseRequest {
	private Handler mHandler;
	private String cellphone;// 手机号
	private String captcha;// 是你收到的验证码
	private static CheckVerCodeRequest getVerCodeRequest;

	public static CheckVerCodeRequest getInstance() {
		if (null == getVerCodeRequest) {
			getVerCodeRequest = new CheckVerCodeRequest();
		}
		return getVerCodeRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		cellphone = params[0];
		captcha = params[1];
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
		return IStrutsAction.HTTP_CHECK_VER_CODE;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("cellphone", cellphone);
		params.put("captcha", captcha);
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
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CHECK_VER_CODE_SUCC, "验证码校验成功");
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CHECK_VER_CODE_FAIL,
					null != baseObject ? baseObject.errorMessage : "验证码校验失败");
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
				HandleAction.HttpType.HTTP_CHECK_VER_CODE_FAIL, "验证码校验失败");
		mHandler.sendMessage(msg);
	}

}
