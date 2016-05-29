package com.lvdi.ruitianxia_cus.activity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.AddressInfo;
import com.lvdi.ruitianxia_cus.request.AddAddressRequest;
import com.lvdi.ruitianxia_cus.request.UpdataAddressRequest;
import com.lvdi.ruitianxia_cus.util.InputUtil;
import com.lvdi.ruitianxia_cus.view.CitySelectView;
import com.lvdi.ruitianxia_cus.view.CitySelectView.CitySelectCallback;

/**
 * 添加地址 类的详细描述：
 *
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月15日 上午11:45:20
 */
public class AddAddressActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	// 添加地址
	public static final int ADD_ADDRESS = 1;
	// 修改地址
	public static final int EDIT_ADDRESS = 2;
	private int mAddressMode;
	private AddressInfo mAddressInfo;// 需要编辑的地址对象
	@AbIocView(id = R.id.nameTv)
	TextView mNameTv;// 收货人姓名
	@AbIocView(id = R.id.contactTv)
	TextView mContactTv;// 联系方式
	@AbIocView(id = R.id.areaTv)
	TextView mAreaTv;// 所在地区
	@AbIocView(id = R.id.addreDetailTv)
	TextView mAddreDetailTv;// 详细地址

	@AbIocView(click = "btnClick", id = R.id.nameRl)
	RelativeLayout mNameRl;
	@AbIocView(click = "btnClick", id = R.id.contactRl)
	RelativeLayout mContactRl;
	@AbIocView(click = "btnClick", id = R.id.areaRl)
	RelativeLayout mAreaRl;
	@AbIocView(click = "btnClick", id = R.id.addreDetailRl)
	RelativeLayout mAddreDetailRl;
	@AbIocView(click = "btnClick", id = R.id.saveBt)
	Button mSaveBt;
	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentDistrictName = "";

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(AddAddressActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_ADD_ADDRESS_SUCC:
				Intent intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
				break;
			case HandleAction.HttpType.HTTP_UPDATA_ADDRESS_SUCC:
				intent = new Intent();
				setResult(RESULT_OK, intent);
				finish();
				break;
			case HandleAction.HttpType.HTTP_ADD_ADDRESS_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_UPDATA_ADDRESS_FAIL:
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
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_add_address);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("新增收货地址");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
	}

	private void initView() {
		mAbTitleBar.getLogoView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBack();
			}
		});
		mNameTv.addTextChangedListener(textWatcher);
		mContactTv.addTextChangedListener(textWatcher);
		mAreaTv.addTextChangedListener(textWatcher);
		mAddreDetailTv.addTextChangedListener(textWatcher);
		checkEnabled();
		mAbTitleBar.setTitleText(mAddressMode == ADD_ADDRESS ? "新增收货地址"
				: "编辑收货地址");
	}

	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			checkEnabled();
		}
	};

	private void checkEnabled() {
		String nameString = mNameTv.getText().toString();
		String contactString = mContactTv.getText().toString();
		String areaString = mAreaTv.getText().toString();
		String addressString = mAddreDetailTv.getText().toString();
		if (TextUtils.isEmpty(nameString) || TextUtils.isEmpty(contactString)
				|| TextUtils.isEmpty(areaString)
				|| TextUtils.isEmpty(addressString)) {
			mSaveBt.setBackgroundResource(R.drawable.save_address_bg);
			mSaveBt.setEnabled(false);
		} else {
			mSaveBt.setBackgroundResource(R.drawable.login_bg);
			mSaveBt.setEnabled(true);
		}
	}

	/**
	 * 
	 * 
	 * @author Xubiao
	 */
	public void initData() {
		mAddressMode = getIntent().getIntExtra("addressMode", ADD_ADDRESS);
		if (mAddressMode == EDIT_ADDRESS) {
			mAddressInfo = (AddressInfo) getIntent().getSerializableExtra(
					"addressInfo");
			mNameTv.setText(mAddressInfo.recipient);
			mContactTv.setText(mAddressInfo.contactNumber);
			mAreaTv.setText(mAddressInfo.zipCode);
			mAddreDetailTv.setText(mAddressInfo.address);
		}
	}

	/**
	 *
	 * @param v
     */
	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.nameRl:
			showNameOrContactDialog(R.id.nameTv);
			break;
		case R.id.contactRl:
			showNameOrContactDialog(R.id.contactTv);
			break;
		case R.id.areaRl:
			CitySelectCallback callback = new CitySelectCallback() {

				@Override
				public void onSaveCity(String province, String city,
						String district, String zipcode) {
					// TODO Auto-generated method stub
					mCurrentProviceName = province;
					mCurrentCityName = city;
					mCurrentDistrictName = district;
					mAreaTv.setText(mCurrentProviceName + mCurrentCityName
							+ mCurrentDistrictName);
					//mAddreDetailTv.setText("");
				}
			};
			CitySelectView citySelectView = new CitySelectView(
					AddAddressActivity.this, callback);
			citySelectView.showView();
			break;
		case R.id.addreDetailRl:
			Intent intent = new Intent(AddAddressActivity.this,
					AddressDetailActivity.class);
			intent.putExtra("address", mAddreDetailTv.getText().toString());
			startActivityForResult(intent, 1);
			break;
		case R.id.saveBt:
			saveAddress();
			break;
		default:
			break;
		}
	}

	private void saveAddress() {
		String nameString = mNameTv.getText().toString();
		String contactString = mContactTv.getText().toString();
		String cityString = mAreaTv.getText().toString();
		String detailString = mAddreDetailTv.getText().toString();
		if (TextUtils.isEmpty(nameString)) {
			AbToastUtil.showToast(AddAddressActivity.this, "收货人不能为空");
			return;
		}
		if (TextUtils.isEmpty(contactString)) {
			AbToastUtil.showToast(AddAddressActivity.this, "联系方式不能为空");
			return;
		}
		// if (contactString.length() < 11) {
		// AbToastUtil.showToast(AddAddressActivity.this, "联系方式为11位手机号");
		// return;
		// }
		if (TextUtils.isEmpty(cityString)) {
			AbToastUtil.showToast(AddAddressActivity.this, "所在地区不能为空");
			return;
		}
		if (TextUtils.isEmpty(detailString)) {
			AbToastUtil.showToast(AddAddressActivity.this, "详细地址不能为空");
			return;
		}

		if (AbWifiUtil.isConnectivity(AddAddressActivity.this)) {
			if (mAddressMode == ADD_ADDRESS) {
				AddAddressRequest.getInstance().sendRequest(mHandler,
						Cache.getAccountInfo().partyId, nameString,
						contactString, detailString, cityString);
			} else {
				UpdataAddressRequest.getInstance().sendRequest(mHandler,
						Cache.getAccountInfo().partyId,
						mAddressInfo.contactMechId, nameString, contactString,
						detailString, cityString);
			}
			AbDialogUtil.showProgressDialog(AddAddressActivity.this, 0, "");
		} else {
			AbToastUtil.showToast(AddAddressActivity.this,
					R.string.please_check_network);
		}
	}

	/**
	 * 
	 * @param viewId
	 * @author Xubiao
	 */
	private void showNameOrContactDialog(final int viewId) {
		final View mView = mInflater.inflate(
				R.layout.dialog_edit_address_name_view, null);
		AbDialogUtil.showAlertDialog(mView);
		String inputText = ((TextView) findViewById(viewId)).getText()
				.toString();
		final EditText inputEditText = ((EditText) mView
				.findViewById(R.id.inputEt));
		if (viewId == R.id.nameTv) {
			((TextView) mView.findViewById(R.id.titleTv)).setText("编辑姓名");
			InputUtil.filterName(inputEditText);
			inputEditText
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							30) });
		} else {
			((TextView) mView.findViewById(R.id.titleTv)).setText("编辑联系方式");
			inputEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
			inputEditText
					.setFilters(new InputFilter[] { new InputFilter.LengthFilter(
							15) });
		}

		inputEditText.setText(inputText);
		inputEditText.setSelection(inputText.length());
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.cancelBt) {
					AbDialogUtil.removeDialog(mView);
				} else if (v.getId() == R.id.okBt) {
					String input = inputEditText.getText().toString();
					if (!TextUtils.isEmpty(input)) {
						((TextView) findViewById(viewId)).setText(input);
					} else {
						AbToastUtil.showToast(AddAddressActivity.this,
								"输入信息不能为空");
						return;
					}
					AbDialogUtil.removeDialog(mView);

				} else if (v.getId() == R.id.clearIv) {
					((EditText) mView.findViewById(R.id.inputEt)).setText("");
				}
			}
		};

		mView.findViewById(R.id.cancelBt).setOnClickListener(clickListener);
		mView.findViewById(R.id.okBt).setOnClickListener(clickListener);
		mView.findViewById(R.id.clearIv).setOnClickListener(clickListener);

	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void onBack() {
		if (!mSaveBt.isEnabled()) {
			finish();
			return;
		}
		final View mView = mInflater
				.inflate(R.layout.dialog_confirm_view, null);
		AbDialogUtil.showAlertDialog(mView);
		((TextView) mView.findViewById(R.id.messageTv))
				.setText(mAddressMode == ADD_ADDRESS ? "是否确认取消新增地址？"
						: "是否确认取消编辑地址");
		((Button) mView.findViewById(R.id.cancelBt)).setText("否");
		((Button) mView.findViewById(R.id.okBt)).setText("是");
		OnClickListener dialogClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AbDialogUtil.removeDialog(mView);
				if (v.getId() == R.id.cancelBt) {
				} else if (v.getId() == R.id.okBt) {
					finish();
				}

			}
		};
		mView.findViewById(R.id.cancelBt).setOnClickListener(
				dialogClickListener);
		mView.findViewById(R.id.okBt).setOnClickListener(dialogClickListener);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case 1:
			String address = mIntent.getStringExtra("address");
			mAddreDetailTv.setText(address);
			break;
		default:
			break;
		}
	}

}
