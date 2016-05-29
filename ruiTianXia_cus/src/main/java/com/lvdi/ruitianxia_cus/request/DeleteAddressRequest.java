package com.lvdi.ruitianxia_cus.request;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbSharedUtil;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.global.MyApplication;
import com.lvdi.ruitianxia_cus.global.ShareKey;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.BaseObject;

/**
 * 删除地址 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月30日 下午8:00:57
 */
public class DeleteAddressRequest extends BaseRequest {
	private Handler mHandler;
	private String partyId;// 客户的partyId、
	private String contactMechId;// 收货地址的contactMechId
	private static DeleteAddressRequest loginRequest;

	public static DeleteAddressRequest getInstance() {
		if (null == loginRequest) {
			loginRequest = new DeleteAddressRequest();
		}
		return loginRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		partyId = params[0];
		contactMechId = params[1];
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
		return IStrutsAction.HTTP_DELETE_ADDRESS;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("partyId", partyId);
		params.put("contactMechId", contactMechId);
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
					HandleAction.HttpType.HTTP_DELETE_ADDRESS_SUCC,
					Integer.parseInt(contactMechId), 0, "删除成功");
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_DELETE_ADDRESS_FAIL,
					null != baseObject ? baseObject.errorMessage : "删除失败");
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
				HandleAction.HttpType.HTTP_DELETE_ADDRESS_FAIL, "删除失败");
		mHandler.sendMessage(msg);
	}

}
