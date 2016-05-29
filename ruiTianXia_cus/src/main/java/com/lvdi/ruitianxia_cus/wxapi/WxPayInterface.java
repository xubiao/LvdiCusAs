/*** */
package com.lvdi.ruitianxia_cus.wxapi;

import java.util.LinkedList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;

import com.lvdi.ruitianxia_cus.activity.LvDiActivity;
import com.lvdi.ruitianxia_cus.model.order.PayInfoWx.WxPayBean;
import com.tencent.mm.sdk.modelpay.PayReq;
import com.tencent.mm.sdk.openapi.IWXAPI;

/**
 * @author wangcz
 * 
 */
public class WxPayInterface {
	private Activity mContext;
	private PayReq req;

	public WxPayInterface(Activity mContext) {
		this.mContext = mContext;
		req = new PayReq();
	}

	public void genPayReq(WxPayBean pp, IWXAPI msgApi) {
		
		req.appId = pp.appid;
		req.partnerId = pp.partnerid;
		req.prepayId = pp.prepayid;
//		req.packageValue = pp.extend;
		req.nonceStr = pp.noncestr;
		req.timeStamp = pp.timestamp;
		req.packageValue = pp.packageValue;

		List<NameValuePair> signParams = new LinkedList<NameValuePair>();
		signParams.add(new BasicNameValuePair("appid", req.appId));
		signParams.add(new BasicNameValuePair("noncestr", req.nonceStr));
		signParams.add(new BasicNameValuePair("package", req.packageValue));
		signParams.add(new BasicNameValuePair("partnerid", req.partnerId));
		signParams.add(new BasicNameValuePair("prepayid", req.prepayId));
		signParams.add(new BasicNameValuePair("timestamp", req.timeStamp));

//		req.sign = genAppSign(signParams, pp.sign);

		req.sign = pp.sign;
		msgApi.sendReq(req);

	}

	private String genAppSign(List<NameValuePair> params, String sign) {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < params.size(); i++) {
			sb.append(params.get(i).getName());
			sb.append('=');
			sb.append(params.get(i).getValue());
			sb.append('&');
		}
		sb.append("key=");
		sb.append(sign);

		String appSign = MD5.getMessageDigest(sb.toString().getBytes())
				.toUpperCase();
		return appSign;
	}
}
