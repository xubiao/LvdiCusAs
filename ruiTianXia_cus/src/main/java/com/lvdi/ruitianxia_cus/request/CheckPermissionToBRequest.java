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
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.AddressInfo;

/**
 * 校验客户是否有权访问B 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月30日 下午8:00:57
 */
public class CheckPermissionToBRequest extends BaseRequest {
	private Handler mHandler;
	private String partyId;// 客户的partyId
	private static CheckPermissionToBRequest loginRequest;

	public static CheckPermissionToBRequest getInstance() {
		if (null == loginRequest) {
			loginRequest = new CheckPermissionToBRequest();
		}
		return loginRequest;
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
		return IStrutsAction.HTTP_CHECK_PERMISSION_TOB;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("partyId", partyId);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		JSONObject jsonObject = resolveJson(content);
		String resultCode = getString(jsonObject, "resultCode", "");
		Message msg;
		if (resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			boolean hasPermissionToB = getBoolean(jsonObject,
					"hasPermissionToB", false);
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CKECK_PERMISSION_TOB_SUCC,
					hasPermissionToB);
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CKECK_PERMISSION_TOB_FAIL,
					getString(jsonObject, "errorMessage", "进入企业端失败"));
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
		Message msg = mHandler
				.obtainMessage(
						HandleAction.HttpType.HTTP_CKECK_PERMISSION_TOB_FAIL,
						"进入企业端失败");
		mHandler.sendMessage(msg);
	}

}
