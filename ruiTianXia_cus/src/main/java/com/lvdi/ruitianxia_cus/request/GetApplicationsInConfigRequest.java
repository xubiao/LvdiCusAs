package com.lvdi.ruitianxia_cus.request;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity;

/**
 * 4.13 获取某项目C端所有可用应用接口 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月31日 下午4:03:58
 */
public class GetApplicationsInConfigRequest extends BaseRequest {
	private Handler mHandler;

	private String organization_id;// 项目的organizationId
	private String page;// 页码
	private String row;// 每页条数

	private static GetApplicationsInConfigRequest getCustomerCLayoutRequest;

	public static GetApplicationsInConfigRequest getInstance() {
		if (null == getCustomerCLayoutRequest) {
			getCustomerCLayoutRequest = new GetApplicationsInConfigRequest();
		}
		return getCustomerCLayoutRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		organization_id = params[0];
		page = params[1];
		row = params[2];
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
		return IStrutsAction.HTTP_GET_APPLICATIONS_IN_CONFIG;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("page", page);
		params.put("organization-id", organization_id);
		params.put("row", row);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		ApplicationEntity applicationsInConfigData = (ApplicationEntity) AbJsonUtil
				.fromJson(content, ApplicationEntity.class);
		Message msg;
		if (null != applicationsInConfigData
				&& applicationsInConfigData.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_CONFIG_SUCC,
					applicationsInConfigData);
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_CONFIG_FAIL,
					applicationsInConfigData != null ? applicationsInConfigData.errorMessage
							: "获取服务定制数据失败");
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
				HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_CONFIG_FAIL,
				"获取服务定制数据失败");
		mHandler.sendMessage(msg);
	}

}
