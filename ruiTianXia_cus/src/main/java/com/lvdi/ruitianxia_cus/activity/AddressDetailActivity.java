package com.lvdi.ruitianxia_cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.util.InputUtil;

/**
 * 
 * 类的详细描述：转盘
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月29日 下午5:25:37
 */
public class AddressDetailActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(id = R.id.detailEt)
	EditText mDetailEt;// 输入框
	@AbIocView(click = "btnClick", id = R.id.cleanIv)
	RelativeLayout mCleanIv;// 

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_address_detail);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("详细地址");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
	}

	private void initView() {
		InputUtil.filterAddress(mDetailEt);
		mAbTitleBar.clearRightView();
		View rightViewMore = mInflater.inflate(R.layout.right_menu_btn, null);
		mAbTitleBar.addRightView(rightViewMore);
		TextView personBt = (TextView) rightViewMore.findViewById(R.id.menuBtn);
		personBt.setBackgroundDrawable(null);
		personBt.setText("保存");
		mAbTitleBar.getRightLayout().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String detailAddress = mDetailEt.getText().toString();
				if (TextUtils.isEmpty(detailAddress)) {
					AbToastUtil.showToast(AddressDetailActivity.this,
							"详细地址不能为空");
					return;
				}
				Intent intent = new Intent();
				intent.putExtra("address", detailAddress);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	/**
	 * 获取数据
	 * 
	 * @author Xubiao
	 */
	public void initData() {
		String address = getIntent().getStringExtra("address");
		mDetailEt.setText(address);
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.cleanIv:
			mDetailEt.setText("");
			break;
		}
	};
}
