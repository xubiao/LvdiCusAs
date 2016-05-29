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
 * 省市区数据级联查询 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月30日 下午8:00:57
 */
public class SearchLocationRequest extends BaseRequest {
	private Handler mHandler;
	private String type;// 查询类型（省：province，市：city，区：area）
	private String dataId;// 父级Id
	private static SearchLocationRequest loginRequest;

	public static SearchLocationRequest getInstance() {
		if (null == loginRequest) {
			loginRequest = new SearchLocationRequest();
		}
		return loginRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		type = params[0];
		dataId = params[1];
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
		return IStrutsAction.HTTP_GETLOCATION_ADDRESS;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("type", type);
		params.put("dataId", dataId);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		// JSONObject jsonObject = resolveJson(content);
		// String resultCode = getString(jsonObject, "resultCode", "");
		// Message msg ;
		// if (resultCode.equals(Config.HTTPSUCCESSRESULT)) {
		// AccountInfo accountInfo = (AccountInfo) AbJsonUtil.fromJson(
		// content, AccountInfo.class);
		// Cache.updataAccountParams(accountInfo);
		// msg =
		// mHandler.obtainMessage(HandleAction.HttpType.HTTP_LOGIN_SUCC,
		// "登录成功");
		// } else {
		// msg =
		// mHandler.obtainMessage(HandleAction.HttpType.HTTP_LOGIN_FAIL,
		// getString(jsonObject, "errorMessage", "登陆失败"));
		// }
		// mHandler.sendMessage(msg);
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
		// Message msg = mHandler.obtainMessage(
		// HandleAction.HttpType.HTTP_LOGIN_FAIL, "登录失败");
		// mHandler.sendMessage(msg);
	}

}
