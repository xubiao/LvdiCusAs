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
 * 4.14 导航页条件查询C端应用接口 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月31日 下午4:03:58
 */
public class GetApplicationsInNavigatorRequest extends BaseRequest {
	private Handler mHandler;

	private String organization_id;// 项目的organizationId
	private String app_category;// 应用类别，还未定
	private String keyword;// 查询关键字，非必须
	private String page;// 页码
	private String row;// 每页条数

	private static GetApplicationsInNavigatorRequest getApplicationsInNavigatorRequest;

	public static GetApplicationsInNavigatorRequest getInstance() {
		if (null == getApplicationsInNavigatorRequest) {
			getApplicationsInNavigatorRequest = new GetApplicationsInNavigatorRequest();
		}
		return getApplicationsInNavigatorRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		organization_id = params[0];
		app_category = params[1];
		keyword = params[2];
		page = params[3];
		row = params[4];
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
		return IStrutsAction.HTTP_GET_APPLICATIONS_IN_NAVIGATOR;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("page", page);
		params.put("organization-id", organization_id);
		params.put("row", row);
		params.put("app-category", app_category);
		params.put("keyword", keyword);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		ApplicationEntity navigationApps = (ApplicationEntity) AbJsonUtil
				.fromJson(content, ApplicationEntity.class);
		Message msg;
		if (null != navigationApps
				&& navigationApps.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			msg = mHandler
					.obtainMessage(
							HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_NAVIGATOR_SUCC,
							navigationApps);
		} else {
			msg = mHandler
					.obtainMessage(
							HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_NAVIGATOR_FAIL,
							null != navigationApps ? navigationApps.errorMessage
									: "获取导航数据失败");
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
				HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_NAVIGATOR_FAIL,
				"获取导航数据失败");
		mHandler.sendMessage(msg);
	}

}
