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

/**
 * 更新用户信息 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月30日 下午8:00:57
 */
public class UpdataCustomerInfoRequest extends BaseRequest {
	private Handler mHandler;
	private String partyId;// 客户的partyId
	private String update_key;// 要更新的字段名
	private String update_value;// 要更新的字段值

	private static UpdataCustomerInfoRequest updataCustomerInfoRequest;

	public static UpdataCustomerInfoRequest getInstance() {
		if (null == updataCustomerInfoRequest) {
			updataCustomerInfoRequest = new UpdataCustomerInfoRequest();
		}
		return updataCustomerInfoRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		partyId = params[0];
		update_key = params[1];
		update_value = params[2];
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
		return IStrutsAction.HTTP_UPDATE_CUSTOMER_INFO;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("party-id", partyId);
		params.put("update-key", update_key);
		params.put("update-value", update_value);
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
			AccountInfo accountInfo = Cache.getAccountInfo();
			if (update_key.equals("nickname")) {
				accountInfo.nickName = update_value;
			} else if (update_key.equals("gender")) {
				accountInfo.gender = update_value;
			}
			Cache.updataAccountParams(accountInfo);
			msg = mHandler
					.obtainMessage(
							HandleAction.HttpType.HTTP_SET_PERSON_INFO_SUCC,
							"修改个人信息成功");
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_SET_PERSON_INFO_FAIL,
					getString(jsonObject, "errorMessage", "修改个人信息失败"));
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
				HandleAction.HttpType.HTTP_SET_PERSON_INFO_FAIL, "修改个人信息失败");
		mHandler.sendMessage(msg);
	}

}
