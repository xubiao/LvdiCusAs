package com.lvdi.ruitianxia_cus.activity.order;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.LvDiActivity;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.request.order.RefundOrderRequest;

public class RefundOrderActivity extends LvDiActivity {
	/**
	 * 确认
	 */
	@AbIocView(id = R.id.btn_confirm, click = "btnClick")
	private Button mBtnConfirm;
	/**
	 * 取消
	 */
	@AbIocView(id = R.id.btn_cancel, click = "btnClick")
	private Button mBtnCancel;
	@AbIocView(id = R.id.et_remark )
	private EditText mEtMark;
	String orderId;
	
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(RefundOrderActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_REFUND_ORDER_SUCC:// 退货
				setResult(RESULT_OK);
				finish();
				break;
			case HandleAction.HttpType.HTTP_REFUND_ORDER_FAIL:
				if (msg.obj != null)
					AbToastUtil.showToast(getApplicationContext(),
							(String) msg.obj);
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
		setAbContentView(R.layout.activity_refund_order);
		setAbTitle("申请退款");
		
		orderId = getIntent().getStringExtra("orderId");
	}
	
	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.btn_confirm:
			if(TextUtils.isEmpty(mEtMark.getText().toString())){
				AbToastUtil.showToast(this, "请输入退款理由");
				return;
			}
			if(!TextUtils.isEmpty(orderId)){
				RefundOrderRequest req = new RefundOrderRequest();
				req.sendRequest(mHandler, orderId, mEtMark.getText().toString());
				AbDialogUtil.showProgressDialog(
						 this, 0, "请稍等...");
			}
			
			break;
		case R.id.btn_cancel:
			finish();
			break;
		default:
			break;
		}
	}
}
