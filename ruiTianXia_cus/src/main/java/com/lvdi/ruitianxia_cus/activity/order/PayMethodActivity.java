package com.lvdi.ruitianxia_cus.activity.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.LvDiActivity;
import com.lvdi.ruitianxia_cus.alipay.AlipayInterface;
import com.lvdi.ruitianxia_cus.global.Constant;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.order.PayInfoWx.WxPayBean;
import com.lvdi.ruitianxia_cus.request.pay.PayAliRequest;
import com.lvdi.ruitianxia_cus.request.pay.PayWxRequest;
import com.lvdi.ruitianxia_cus.wxapi.WXPayEntryActivity;
import com.lvdi.ruitianxia_cus.wxapi.WxConstants;
import com.lvdi.ruitianxia_cus.wxapi.WxPayInterface;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class PayMethodActivity extends  LvDiActivity implements OnClickListener {

	
	/** 订单号 */
	@AbIocView(id = R.id.tv_order_num)
	TextView mTvOrderNum;
	/** 订单总额 */
	@AbIocView(id = R.id.tv_total_money)
	TextView mTvTotalMoney;
	/** 订单Id */
	private String orderId;
	/** 订单价格总额 */
	private String totalPrice;
	final IWXAPI msgApi = WXAPIFactory.createWXAPI(this, null);

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(PayMethodActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_PAY_WX_SUCC:// 微信参数请求成功
				if (msg.obj != null) {
					WxPayBean payWx = (WxPayBean) msg.obj;
					WxPayInterface wp = new WxPayInterface(PayMethodActivity.this);
					wp.genPayReq(payWx, msgApi);
				}
				break;
			case HandleAction.HttpType.HTTP_PAY_WX_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_PAY_ALI_SUCESS:// 支付宝参数请求成功

				if (msg.obj != null) {
					AlipayInterface payAli = new AlipayInterface(PayMethodActivity.this);
					payAli.pay((String)msg.obj);
				}
				break;
			case HandleAction.HttpType.HTTP_PAY_ALI_FAIL://
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_pay_methor);
		setAbTitle("订单支付");
		msgApi.registerApp(WxConstants.APP_ID);
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		super.initData();
		if(getIntent().hasExtra("orderId")){
			orderId = getIntent().getStringExtra("orderId");
			mTvOrderNum.setText("订单号： " + orderId);
		}
		if(getIntent().hasExtra("totalPrice")){
			totalPrice = getIntent().getStringExtra("totalPrice");
			mTvTotalMoney.setText(getString(R.string.app_rmb)+totalPrice);
		}

		registerReceiver(mPayResultReceiver, new IntentFilter(
				WXPayEntryActivity.ACTION_WX_PAY_SUCESS));
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		findViewById(R.id.layout_wx).setOnClickListener(this);
		findViewById(R.id.layout_zfb).setOnClickListener(this);
	}
	
	public void dealAfterPaySucess(){
//		sendBroadcast(new Intent(Constant.ACTION_ORDER_STATUS_CHANGE));
//		startActivity(new Intent(this, OrderDetailActivity.class).putExtra(
//				"orderId", orderId));
		setResult(RESULT_OK,new Intent().putExtra("orderId", orderId));
		finish();
		AbToastUtil.showToast(this, "支付成功！");
	}
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		unregisterReceiver(mPayResultReceiver);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.layout_wx:
			if (!TextUtils.isEmpty(orderId)) {
				PayWxRequest reqWx = new PayWxRequest();
				reqWx.sendRequest(mHandler, orderId);
				AbDialogUtil.showProgressDialog(this, 0, "正在提交中...");
			}
			break;
		case R.id.layout_zfb:
			if (!TextUtils.isEmpty(orderId)) {
				PayAliRequest reqAli = new PayAliRequest();
				reqAli.sendRequest(mHandler, orderId);
				AbDialogUtil.showProgressDialog(this, 0, "正在提交中...");
			}
			break;
		default:
			break;
		}
	}
	
	/**
	 *  
	 */
	private BroadcastReceiver mPayResultReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			dealAfterPaySucess();
		}
	};
}
