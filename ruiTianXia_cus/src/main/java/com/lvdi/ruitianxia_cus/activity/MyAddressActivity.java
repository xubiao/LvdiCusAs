package com.lvdi.ruitianxia_cus.activity;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.ab.activity.AbActivity;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.adapter.AddressAdapter;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.AddressInfo;
import com.lvdi.ruitianxia_cus.request.GetAddressRequest;

/**
 * 
 * 类的详细描述：我的收货地址
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月29日 下午5:25:37
 */
public class MyAddressActivity extends LvDiActivity {
	/** 选择模式 */
	public static final int MODE_SELECT = 2;
	/** 列表模式 */
	public static final int MODE_LIST = 1;
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(itemClick = "itemClick", id = R.id.addressLv)
	ListView mListView;// 地址了列表
	private View footView;
	private AddressAdapter mAdapter;// 地址适配器
	private List<AddressInfo> mAddressInfos;// 列表数据
	private int mode = MODE_LIST;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(MyAddressActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_GET_ADDRESS_LIST_SUCC:
				footView.setVisibility(View.VISIBLE);
				mAddressInfos = (List<AddressInfo>) msg.obj;
				mAdapter.setData(mAddressInfos);
				mAdapter.notifyDataSetChanged();
				break;
			case HandleAction.HttpType.HTTP_GET_ADDRESS_LIST_FAIL:
				footView.setVisibility(View.VISIBLE);
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_SET_DEFAULT_ADDRESS_SUCC:
				// String contactMechId = msg.arg1 + "";
				// setDefaultSucc(contactMechId);
				GetAddressRequest.getInstance().sendRequest(mHandler,
						Cache.getAccountInfo().partyId);
				AbDialogUtil.showProgressDialog(MyAddressActivity.this, 0, "");
				break;
			case HandleAction.HttpType.HTTP_SET_DEFAULT_ADDRESS_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_DELETE_ADDRESS_SUCC:
				String contactMechId = msg.arg1 + "";
				deleteSucc(contactMechId);
				break;
			case HandleAction.HttpType.HTTP_DELETE_ADDRESS_FAIL:
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
		setAbContentView(R.layout.activity_my_address);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("收货地址");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
	}

	private void initView() {
		if (null == footView) {
			footView = mInflater.inflate(R.layout.view_address_footview, null);
			mListView.addFooterView(footView);
		}
		footView.findViewById(R.id.saveBt).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						Intent intent = new Intent(MyAddressActivity.this,
								AddAddressActivity.class);
						intent.putExtra("addressMode",
								AddAddressActivity.ADD_ADDRESS);
						startActivityForResult(intent, 1);
					}
				});
		mAdapter = new AddressAdapter(this, mHandler);
		mListView.setAdapter(mAdapter);

		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if (mode == MODE_SELECT) {
					setResult(
							RESULT_OK,
							new Intent().putExtra("AddressInfo",
									mAddressInfos.get(arg2)));
					finish();
				}
			}
		});

	}

	public void onItemClick(int position) {

	}

	public void initData() {
		if (AbWifiUtil.isConnectivity(this)) {
			if (null == footView) {
				footView = mInflater.inflate(R.layout.view_address_footview,
						null);
				mListView.addFooterView(footView);
			}
			footView.setVisibility(View.INVISIBLE);
			GetAddressRequest.getInstance().sendRequest(mHandler,
					Cache.getAccountInfo().partyId);
			AbDialogUtil.showProgressDialog(MyAddressActivity.this, 0,
					"获取地址列表中...");
		} else {
			AbToastUtil.showToast(this, R.string.please_check_network);
		}

		mode = getIntent().getIntExtra("mode", MODE_LIST);
	}

	/**
	 * 设置默认成功
	 * 
	 * @param contactMechId
	 * @author Xubiao
	 */
	private void setDefaultSucc(String contactMechId) {
		int size = mAddressInfos.size();
		for (int i = 0; i < size; i++) {
			AddressInfo addressInfo = mAddressInfos.get(i);
			if (addressInfo.contactMechId.equals(contactMechId)) {
				addressInfo.isDefault = "Y";
				addressInfo = mAddressInfos.remove(i);
				mAddressInfos.add(0, addressInfo);
			} else {
				addressInfo.isDefault = "N";
			}
		}
		mAdapter.setData(mAddressInfos);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 删除地址成功
	 * 
	 * @param contactMechId
	 * @author Xubiao
	 */
	private void deleteSucc(String contactMechId) {
		int size = mAddressInfos.size();
		for (int i = 0; i < size; i++) {
			AddressInfo addressInfo = mAddressInfos.get(i);
			if (addressInfo.contactMechId.equals(contactMechId)) {
				mAddressInfos.remove(addressInfo);
				break;
			}
		}
		mAdapter.setData(mAddressInfos);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 列表点击 〈功能详细描述〉
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public void itemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg2 == 0) {
		} else {
		}
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case 1:
			if (AbWifiUtil.isConnectivity(this)) {
				GetAddressRequest.getInstance().sendRequest(mHandler,
						Cache.getAccountInfo().partyId);
				AbDialogUtil.showProgressDialog(MyAddressActivity.this, 0, "");
			} else {
				AbToastUtil.showToast(this, R.string.please_check_network);
			}
			break;
		default:
			break;
		}
	}
}
