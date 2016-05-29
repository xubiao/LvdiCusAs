package com.lvdi.ruitianxia_cus.wxapi;

import android.content.Intent;
import android.os.Bundle;

import com.ab.util.AbLogUtil;
import com.ab.util.AbToastUtil;
import com.lvdi.ruitianxia_cus.activity.LvDiActivity;
import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXPayEntryActivity extends LvDiActivity implements
		IWXAPIEventHandler {
	/*** 广播-微信支付成功 */
	public static final String ACTION_WX_PAY_SUCESS = "android.intent.action.lvdi.wxpaysucess";
	private IWXAPI api;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.pay_result);
		api = WXAPIFactory.createWXAPI(this, WxConstants.APP_ID);
		api.handleIntent(getIntent(), this);
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		AbLogUtil.d("WXPayEntryActivity", "onPayFinish, errCode = " + resp.errCode
				+ " errStr=" + resp.errStr + " ");

		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			// resp 0 :成功 -1错误 -2 用户取消
			if (0 == resp.errCode) {
				sendBroadcast(new Intent(ACTION_WX_PAY_SUCESS));
				WXPayEntryActivity.this.finish();
			} else if (-1 == resp.errCode) {
				AbToastUtil.showToast(this, "支付异常") ;
				finish();
			} else if (-2 == resp.errCode) {
				AbToastUtil.showToast(this, "支付取消") ;
				finish();
			}
			// AlertDialog.Builder builder = new AlertDialog.Builder(this);
			// builder.setTitle(R.string.app_tip);
			// builder.setMessage(getString(R.string.pay_result_callback_msg,
			// resp.errStr +";code=" + String.valueOf(resp.errCode)));
			// builder.show();
		}
	}
	 
}