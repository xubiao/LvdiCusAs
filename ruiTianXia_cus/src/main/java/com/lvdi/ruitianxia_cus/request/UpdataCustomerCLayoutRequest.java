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
import com.lvdi.ruitianxia_cus.model.BaseObject;

/**
 * 客户更新某项目的C端首页模板 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月31日 下午4:03:58
 */
public class UpdataCustomerCLayoutRequest extends BaseRequest {
	private Handler mHandler;
	private String partyId;// 客户的partyId
	private String organizationId;// 项目的organizationId
	private String layout;// 页面配置组合模板
	private static UpdataCustomerCLayoutRequest updataCustomerCLayoutRequest;

	public static UpdataCustomerCLayoutRequest getInstance() {
		if (null == updataCustomerCLayoutRequest) {
			updataCustomerCLayoutRequest = new UpdataCustomerCLayoutRequest();
		}
		return updataCustomerCLayoutRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		partyId = params[0];
		organizationId = params[1];
		layout = params[2];
		httpConnect(true, this);
	}

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return Config.HttpURLPrefix;
	}

	@Override
	public String getAction() {
		// TODO Auto-generated method stub
		return IStrutsAction.HTTP_UPDATA_CUSTOMERCLAYOUT_FORC;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("partyId", partyId);
		params.put("organizationId", organizationId);
		params.put("layout", layout);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		BaseObject baseObject = (BaseObject) AbJsonUtil.fromJson(content,
				BaseObject.class);
		Message msg;
		if (null != baseObject
				&& baseObject.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_C_LAYOUT_SUCC,
					"修改成功");
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_C_LAYOUT_FAIL,
					null != baseObject ? baseObject.errorMessage : "修改失败");
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
				HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_C_LAYOUT_FAIL,
				"修改失败");
		mHandler.sendMessage(msg);
	}

}
