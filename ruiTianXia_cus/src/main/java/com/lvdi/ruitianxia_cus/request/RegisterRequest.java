package com.lvdi.ruitianxia_cus.request;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.util.UMengMobclickAgent;
import com.lvdi.ruitianxia_cus.util.UMengMobclickAgent.EventId;

/**
 * 注册请求 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午1:02:56
 */
public class RegisterRequest extends BaseRequest {
	private Handler mHandler;
	private String userStr;
	private String passwordStr;
	private String codeStr;
	private static RegisterRequest registerRequest;

	public static RegisterRequest getInstance() {
		if (null == registerRequest) {
			registerRequest = new RegisterRequest();
		}
		return registerRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		userStr = params[0];
		passwordStr = params[1];
		codeStr = params[2];
		httpConnect(false, this);
		UMengMobclickAgent.onEventValue(EventId.REGISTER);
	}

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return Config.HttpURLPrefix;
	}

	@Override
	public String getAction() {
		// TODO Auto-generated method stub
		return IStrutsAction.HTTP_REGISTER;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("username", userStr);
		params.put("password", passwordStr);
		params.put("captcha", codeStr);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d("LoginRequest", "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		JSONObject jsonObject = resolveJson(content);
		String resultCode = getString(jsonObject, "resultCode", "");
		Message msg;
		if (resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_REGISTER_SUCC, "注册成功");
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_REGISTER_FAIL,
					getString(jsonObject, "errorMessage", "注册失败"));
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
				HandleAction.HttpType.HTTP_REGISTER_FAIL, "注册失败");
		mHandler.sendMessage(msg);
	}

}
