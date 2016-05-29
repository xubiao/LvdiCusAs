package com.lvdi.ruitianxia_cus.request;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.CustomerMainData;

/**
 * 客户获取个人订单，预约数量接口 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月19日 下午12:40:03
 */
public class GetCustomerMainDataRequest extends BaseRequest {
	private Handler mHandler;
	private String user_login_id;// 用户登录名
	private static GetCustomerMainDataRequest getCustomerMainDataRequest;

	public static GetCustomerMainDataRequest getInstance() {
		if (null == getCustomerMainDataRequest) {
			getCustomerMainDataRequest = new GetCustomerMainDataRequest();
		}
		return getCustomerMainDataRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		user_login_id = params[0];
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
		return IStrutsAction.HTTP_GET_CUSTOMER_MAIN_DATA;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("user-login-id", user_login_id);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		CustomerMainData customerMainData = (CustomerMainData) AbJsonUtil
				.fromJson(content, CustomerMainData.class);
		Message msg;
		if (null != customerMainData
				&& customerMainData.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_GET_CUSTOMER_MAIN_DATA_SUCC,
					customerMainData);
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_GET_CUSTOMER_MAIN_DATA_FAIL,
					customerMainData != null ? customerMainData.errorMessage
							: "获取个人中心数据失败");
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
		AbLogUtil.d(getClass(), "onFailure--" + "statusCode:" + statusCode
				+ "content:" + content);
		Message msg = mHandler.obtainMessage(
				HandleAction.HttpType.HTTP_GET_CUSTOMER_MAIN_DATA_FAIL,
				"获取个人中心数据失败");
		mHandler.sendMessage(msg);
	}

}
