package com.lvdi.ruitianxia_cus.request;

import android.os.Handler;
import android.os.Message;

import com.ab.http.AbRequestParams;
import com.ab.http.AbRequestPostJsonParams;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.global.IStrutsAction;
import com.lvdi.ruitianxia_cus.model.shopcart.RspCategory;

/**
 * 4.22 购物车商品信息查询接口
 * 
 * @version 1.0.1
 */
public class GetShopCartsRequest extends BaseRequest {
	private Handler mHandler;
	private String content;//

	public void sendRequest(Handler handler, String... params) {
		// TODO Auto-generated method stub
		mHandler = handler;
		content = params[0];
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
		return IStrutsAction.HTTP_GET_SHOP_CARTS;
	}

	@Override
	public AbRequestParams getPostParams() {
		// TODO Auto-generated method stub
		AbRequestPostJsonParams params = new AbRequestPostJsonParams();
		params.put("content", content);//
		return params;
	}

	@Override
	public void onSuccess(int statusCode, String content) {
		// TODO Auto-generated method stub
		AbLogUtil.d(getClass(), "onSuccess--" + "statusCode:" + statusCode
				+ "content:" + content);
		RspCategory mBean = (RspCategory) AbJsonUtil.fromJson(
				content, RspCategory.class);
		Message msg;
		if (mBean != null) {
			msg = mHandler
					.obtainMessage(HandleAction.HttpType.HTTP_CHECK_GET_SHOP_CARTS_SUCC);
			msg.obj = mBean;
		} else {
			msg = mHandler.obtainMessage(
					HandleAction.HttpType.HTTP_CHECK_GET_SHOP_CARTS_FAIL, "");
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
				HandleAction.HttpType.HTTP_CHECK_GET_SHOP_CARTS_FAIL, "获取数据失败");
		mHandler.sendMessage(msg);
	}

	public static class GetShopCartReqBean {

		public String categoryId;
		public String quantity;
		public String productIds;
	}
}
