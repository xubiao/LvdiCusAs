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
 * 修改地址 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月30日 下午8:00:57
 */
public class UpdataAddressRequest extends BaseRequest {
	private Handler mHandler;
	private String partyId;// 客户的partyId
	private String contactMechId;// 收货地址的
	private String recipient;// 收货人
	private String contactNumber;// 收货人联系电话
	private String address;// 收货人地址
	private String zipCode;// 邮政编码

	private static UpdataAddressRequest loginRequest;

	public static UpdataAddressRequest getInstance() {
		if (null == loginRequest) {
			loginRequest = new UpdataAddressRequest();
		}
		return loginRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		partyId = params[0];
		contactMechId = params[1];
		recipient = params[2];
		contactNumber = params[3];
		address = params[4];
		zipCode = params[5];
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
		return IStrutsAction.HTTP_UPDATA_ADDRESS;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("partyId", partyId);
		params.put("contactMechId", contactMechId);
		params.put("recipient", recipient);
		params.put("contactNumber", contactNumber);
		params.put("address", address);
		params.put("zipCode", zipCode);
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
					HandleAction.HttpType.HTTP_UPDATA_ADDRESS_SUCC, "修改地址成功");
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_UPDATA_ADDRESS_FAIL,
					null != baseObject ? baseObject.errorMessage : "修改地址失败");
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
				HandleAction.HttpType.HTTP_UPDATA_ADDRESS_FAIL, "修改地址失败");
		mHandler.sendMessage(msg);
	}

}
