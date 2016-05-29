package com.lvdi.ruitianxia_cus.request;

import java.util.Set;

import org.json.JSONObject;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbSharedUtil;
import com.ab.util.AbWifiUtil;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.global.MyApplication;
import com.lvdi.ruitianxia_cus.global.ShareKey;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.util.UMengMobclickAgent;
import com.lvdi.ruitianxia_cus.util.UMengMobclickAgent.EventId;

/**
 * 
 * 类的详细描述： 登录请求
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 上午1:03:38
 */
public class LoginRequest extends BaseRequest {
	private Handler mHandler;
	private String userStr;
	private String passwordStr;
	private static LoginRequest loginRequest;
	private static final int MSG_SET_ALIAS = 1001;

	private Handler tHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_SET_ALIAS:
				if (Cache.getAccountInfo() != null)
					JPushInterface.setAliasAndTags(MyApplication.getInstance(),
							Cache.getAccountInfo().partyId + Config.JPUSH_END,
							null, mAliasCallback);
				break;

			default:
				break;
			}
		};
	};

	public static LoginRequest getInstance() {
		if (null == loginRequest) {
			loginRequest = new LoginRequest();
		}
		return loginRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		userStr = params[0];
		passwordStr = params[1];
		httpConnect(false, this);
		UMengMobclickAgent.onEventValue(EventId.LOGIN);
	}

	@Override
	public String getPrefix() {
		// TODO Auto-generated method stub
		return Config.HttpURLPrefix;
	}

	@Override
	public String getAction() {
		// TODO Auto-generated method stub
		return IStrutsAction.HTTP_LOGIN;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("username", userStr);
		params.put("password", passwordStr);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d("LoginRequest", "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		AccountInfo accountInfo = (AccountInfo) AbJsonUtil.fromJson(content,
				AccountInfo.class);
		Message msg;
		if (null != accountInfo
				&& accountInfo.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			Cache.updataAccountParams(accountInfo);
			tHandler.sendEmptyMessage(MSG_SET_ALIAS);
			if (null == mHandler)
				return;
			msg = mHandler.obtainMessage(HandleAction.HttpType.HTTP_LOGIN_SUCC,
					"登录成功");
		} else {
			if (null == mHandler)
				return;
			msg = mHandler.obtainMessage(HandleAction.HttpType.HTTP_LOGIN_FAIL,
					null != accountInfo ? accountInfo.errorMessage : "登陆失败");
		}
		mHandler.sendMessage(msg);
	}

	private final TagAliasCallback mAliasCallback = new TagAliasCallback() {

		String TAG = "TagAliasCallback";

		@Override
		public void gotResult(int code, String alias, Set<String> tags) {
			String logs;
			switch (code) {
			case 0:
				logs = "Set tag and alias success";
				Log.i(TAG, logs);
				break;

			case 6002:
				logs = "Failed to set alias and tags due to timeout. Try again after 60s.";
				Log.i(TAG, logs);
				if (AbWifiUtil.isConnectivity(MyApplication.getInstance())) {
					tHandler.sendMessageDelayed(
							tHandler.obtainMessage(MSG_SET_ALIAS, alias),
							1000 * 60);
				} else {
				}
				break;

			default:
				logs = "Failed with errorCode = " + code;
			}

		}

	};

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
		if (null == mHandler)
			return;
		Message msg = mHandler.obtainMessage(
				HandleAction.HttpType.HTTP_LOGIN_FAIL, "登录失败");
		mHandler.sendMessage(msg);
	}

}
