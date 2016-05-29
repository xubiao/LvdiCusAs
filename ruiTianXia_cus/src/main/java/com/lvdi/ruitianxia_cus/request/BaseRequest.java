package com.lvdi.ruitianxia_cus.request;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ab.http.AbHttpUtil;
import com.ab.http.AbRequestParams;
import com.ab.http.AbStringHttpResponseListener;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.MyApplication;

/**
 * 
 * 类的详细描述： 请求基类
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 上午1:03:49
 */
public abstract class BaseRequest extends AbStringHttpResponseListener {

	/**
	 * 设置网络请求地址前缀
	 * 
	 * @return
	 */
	public abstract String getPrefix();

	/**
	 * 设置网络请求接口
	 * 
	 * @return
	 */
	public abstract String getAction();

	/**
	 * 设置网络请求参数
	 * 
	 * @return
	 */
	public abstract AbRequestParams getPostParams();

	/**
	 * 
	 * @param isPost
	 * @param listener
	 * @author Xubiao
	 */
	protected void httpConnect(boolean isPost, AbStringHttpResponseListener listener) {
		// 获取Http工具类
		AbHttpUtil mAbHttpUtil = AbHttpUtil.getInstance(MyApplication.getInstance());
		mAbHttpUtil.setTimeout(15000);
		String prefix = getPrefix();
		String action = getAction();
		StringBuffer url = new StringBuffer();
		url.append(prefix);
		url.append(action);
		AbLogUtil.d("httpConnect", "url:" + url);

		AbRequestParams params = getPostParams();
		if (isPost) {
			AbLogUtil.d("httpConnect", "post-params:" + params.getParamString());
			mAbHttpUtil.post(url.toString(), params, listener);
		} else {
			AbLogUtil.d("httpConnect", "get-params:" + params.getParamString());
			mAbHttpUtil.get(url.toString(), params, listener);
		}
	}

	/**
	 * 
	 * @param strResult
	 * @return
	 * @author Xubiao
	 */
	protected JSONObject resolveJson(String strResult) {
		JSONObject jsonObject = null;
		try {
			jsonObject = new JSONObject(strResult);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return jsonObject;
	}

	/**
	 * 
	 * @param JSONArray
	 * @param name
	 * @return
	 * @author Xubiao
	 */
	protected JSONArray getJsonArray(JSONObject jsonObject, String name) {
		if (null == jsonObject)
			return null;
		if (jsonObject.has(name)) {
			try {
				return jsonObject.getJSONArray(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param jsonObject
	 * @param name
	 * @return
	 * @author Xubiao
	 */
	protected JSONObject getJson(JSONObject jsonObject, String name) {
		if (null == jsonObject)
			return null;
		if (jsonObject.has(name)) {
			try {
				return jsonObject.getJSONObject(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param jasonStr
	 * @param name
	 * @return
	 * @author Xubiao
	 */
	protected JSONArray getJsonArray(String jasonStr, String name) {
		JSONObject jsonObject = resolveJson(jasonStr);
		if (null != jsonObject) {
			return getJsonArray(jsonObject, name);
		} else {
			return null;
		}
	}

	/**
	 * 
	 * @param jsonObject
	 * @param name
	 * @param defaultVal
	 * @return
	 * @author Xubiao
	 */
	protected String getString(JSONObject jsonObject, String name, String defaultVal) {
		if (null == jsonObject)
			return defaultVal;
		if (jsonObject.has(name)) {
			try {
				return jsonObject.getString(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return defaultVal;
			}
		} else {
			return defaultVal;
		}
	}

	/**
	 * 
	 * @param jsonObject
	 * @param name
	 * @param defaultVal
	 * @return
	 * @author Xubiao
	 */
	protected Double getDouble(JSONObject jsonObject, String name, Double defaultVal) {
		if (null == jsonObject)
			return defaultVal;
		if (jsonObject.has(name)) {
			try {
				return jsonObject.getDouble(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return defaultVal;
			}
		} else {
			return defaultVal;
		}
	}

	/**
	 * 
	 * @param jsonObject
	 * @param name
	 * @param defaultVal
	 * @return
	 * @author Xubiao
	 */
	protected int getInt(JSONObject jsonObject, String name, int defaultVal) {
		if (null == jsonObject)
			return defaultVal;
		if (jsonObject.has(name)) {
			try {
				return jsonObject.getInt(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return defaultVal;
			}
		} else {
			return defaultVal;
		}
	}

	/**
	 * 
	 * @param jsonObject
	 * @param name
	 * @param defaultVal
	 * @return
	 * @author Xubiao
	 */
	protected boolean getBoolean(JSONObject jsonObject, String name, boolean defaultVal) {
		if (null == jsonObject)
			return defaultVal;
		if (jsonObject.has(name)) {
			try {
				return jsonObject.getBoolean(name);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return defaultVal;
			}
		} else {
			return defaultVal;
		}
	}

}
