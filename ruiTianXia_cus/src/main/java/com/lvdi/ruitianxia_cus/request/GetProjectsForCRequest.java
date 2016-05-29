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
import com.lvdi.ruitianxia_cus.activity.ChangeLocationActivity;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.ProjectForC;

/**
 * C端首页获取首页模板 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月31日 下午4:03:58
 */
public class GetProjectsForCRequest extends BaseRequest {
	private Handler mHandler;
	private String type;// 查询类型（city, project）
	private String cityId;// 市级的id，当查询省时可为空
	private int requectMode;
	private static GetProjectsForCRequest getProjectsForCRequestRequest;

	public static GetProjectsForCRequest getInstance() {
		if (null == getProjectsForCRequestRequest) {
			getProjectsForCRequestRequest = new GetProjectsForCRequest();
		}
		return getProjectsForCRequestRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		type = params[0];
		cityId = params[1];
		requectMode = Integer.parseInt(params[2]);
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
		return IStrutsAction.HTTP_GET_PROJECTS_FORC;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("type", type);
		params.put("cityId", cityId);
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
			JSONArray jsonArray = getJsonArray(jsonObject, "citiesOrProjects");
			List<ProjectForC> projects = new ArrayList<ProjectForC>();
			if (null != jsonArray) {
				int size = jsonArray.length();
				for (int i = 0; i < size; i++) {
					try {
						projects.add((ProjectForC) AbJsonUtil.fromJson(
								jsonArray.get(i).toString(), ProjectForC.class));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_GET_POJECTSFORC_SUCC,
					requectMode, 0, projects);
		} else {
			msg = mHandler
					.obtainMessage(
							HandleAction.HttpType.HTTP_GET_POJECTSFORC_FAIL,
							getString(
									jsonObject,
									"errorMessage",
									requectMode == ChangeLocationActivity.CITYMODE ? "获取城市数据失败"
											: "获取项目数据失败"));
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
				HandleAction.HttpType.HTTP_GET_POJECTSFORC_FAIL,
				requectMode == ChangeLocationActivity.CITYMODE ? "获取城市数据失败"
						: "获取项目数据失败");
		mHandler.sendMessage(msg);
	}

}
