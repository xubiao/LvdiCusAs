package com.lvdi.ruitianxia_cus.request;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;

/**
 * 
 * 类的详细描述： 设置新密码
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午2:59:55
 */
public class SetPasswordRequest extends BaseRequest {
	private Handler mHandler;
	private String username;// 用户名目前为手机号
	private String new_password;// 要更新的字段值

	private static SetPasswordRequest setPasswordRequest;

	public static SetPasswordRequest getInstance() {
		if (null == setPasswordRequest) {
			setPasswordRequest = new SetPasswordRequest();
		}
		return setPasswordRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		username = params[0];
		new_password = params[1];
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
		return IStrutsAction.HTTP_FORGET_PASSWORD;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("username", username);
		params.put("new-password", new_password);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d("LoginRequest", "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		Message msg = mHandler.obtainMessage(
				HandleAction.HttpType.HTTP_SETPASSWORD_SUCC, "设置密码成功");
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
				HandleAction.HttpType.HTTP_SETPASSWORD_FAIL, "设置密码失败");
		mHandler.sendMessage(msg);
	}

}
