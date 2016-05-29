package com.lvdi.ruitianxia_cus.request;

import java.math.BigDecimal;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.CustomerC;
import com.lvdi.ruitianxia_cus.model.CustomerC.CustomerCLayout;

/**
 * C端首页获取所有项目 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月31日 下午4:03:58
 */
public class GetCustomerCLayoutRequest extends BaseRequest {
	private Handler mHandler;

	private String party_id; // 客户的partyId，非必须
	private String longtitude; // 经度
	private String latitude;// 纬度
	private String organization_id;// 项目的organizationId

	private static GetCustomerCLayoutRequest getCustomerCLayoutRequest;

	public static GetCustomerCLayoutRequest getInstance() {
		if (null == getCustomerCLayoutRequest) {
			getCustomerCLayoutRequest = new GetCustomerCLayoutRequest();
		}
		return getCustomerCLayoutRequest;
	}

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		party_id = params[0];
		longtitude = params[1];
		latitude = params[2];
//		BigDecimal lonBd = new BigDecimal(longtitude);
//		BigDecimal latBd = new BigDecimal(latitude);
//		longtitude = lonBd.toPlainString();
//		latitude = latBd.toPlainString();
		organization_id = params[3];
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
		return IStrutsAction.HTTP_GET_CUSTOMERCLAYOUT_FORC;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestParams params = new AbRequestParams();
		params.put("party-id", party_id);
		params.put("organization-id", organization_id);
		params.put("longtitude", longtitude);
		params.put("latitude", latitude);
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		CustomerC customerC = (CustomerC) AbJsonUtil.fromJson(content,
				CustomerC.class);

		CustomerCLayout customerCLayout = (CustomerCLayout) AbJsonUtil
				.fromJson(customerC.layout, CustomerCLayout.class);

		if (customerCLayout != null)
			customerC.layouts = customerCLayout.layout;
		Message msg;
		if (null != customerC
				&& customerC.resultCode.equals(Config.HTTPSUCCESSRESULT)) {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_GET_CUSTOMER_C_LAYOUT_SUCC,
					customerC);
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_GET_CUSTOMER_C_LAYOUT_FAIL,
					customerC != null ? customerC.errorMessage : "获取首页数据失败");
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
				HandleAction.HttpType.HTTP_GET_CUSTOMER_C_LAYOUT_FAIL,
				"获取首页数据失败");
		mHandler.sendMessage(msg);
	}

}
