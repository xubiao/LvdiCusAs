package com.lvdi.ruitianxia_cus.request;

import java.io.File;

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
 * 更新用户头像 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月30日 下午8:00:57
 */
public class UpdataCustomerHeadIconRequest extends BaseRequest {
	private Handler mHandler;
	private String partyId;// 客户的partyId
	private String file;// 头像路径

	private static UpdataCustomerHeadIconRequest updataCustomerHeadIconRequest;

	public static UpdataCustomerHeadIconRequest getInstance() {
		if (null == updataCustomerHeadIconRequest) {
			updataCustomerHeadIconRequest = new UpdataCustomerHeadIconRequest();
		}
		return updataCustomerHeadIconRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		partyId = params[0];
		file = params[1];
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
		return IStrutsAction.HTTP_UPDATE_CUSTOMER_HEAD;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("partyId", partyId);
		params.put("file", new File(file));
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
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_HEAD_ICON_SUCC,
					"修改头像成功");
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_HEAD_ICON_FAIL,
					getString(jsonObject, "errorMessage", "修改头像失败"));
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
				HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_HEAD_ICON_FAIL,
				"修改头像失败");
		mHandler.sendMessage(msg);
	}

}
