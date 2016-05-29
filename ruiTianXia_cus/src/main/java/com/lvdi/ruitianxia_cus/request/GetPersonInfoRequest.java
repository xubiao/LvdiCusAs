package com.lvdi.ruitianxia_cus.request;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.AddressInfo;

/**
 * 获取个人信息 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月30日 下午8:00:57
 */
public class GetPersonInfoRequest extends BaseRequest {
	private Handler mHandler;
	private String partyId;// 客户的partyId
	private static GetPersonInfoRequest getPersonInfoRequest;

	public static GetPersonInfoRequest getInstance() {
		if (null == getPersonInfoRequest) {
			getPersonInfoRequest = new GetPersonInfoRequest();
		}
		return getPersonInfoRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		partyId = params[0];
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
		return IStrutsAction.HTTP_GET_CUSTOMER_INFO;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("party-Id", partyId);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		AccountInfo accountInfo = (AccountInfo) AbJsonUtil.fromJson(content,
				AccountInfo.class);
		Message msg;
		if (null != accountInfo
				&& accountInfo.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			accountInfo.partyId = partyId;
			Cache.updataAccountParams(accountInfo);
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_GET_CUSTOMER_INFO_SUCC,
					"获取个人信息成功");
		} else {
			msg = mHandler
					.obtainMessage(
							HandleAction.HttpType.HTTP_GET_CUSTOMER_INFO_FAIL,
							accountInfo != null ? accountInfo.errorMessage
									: "获取个人信息失败");
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
				HandleAction.HttpType.HTTP_GET_CUSTOMER_INFO_FAIL, "获取个人信息失败");
		mHandler.sendMessage(msg);
	}

}
