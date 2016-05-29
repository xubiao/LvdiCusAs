package com.lvdi.ruitianxia_cus.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.order.OrderListActivity;
import com.lvdi.ruitianxia_cus.activity.shopcart.ShopCartActivity;
import com.lvdi.ruitianxia_cus.constants.OrderType;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.CustomerMainData;
import com.lvdi.ruitianxia_cus.request.GetCustomerMainDataRequest;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.lvdi.ruitianxia_cus.view.PersonCenterMenuPop;
import com.lvdi.ruitianxia_cus.view.RoundImageView;

/**
 * 
 * 类的详细描述： 个人中心
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午8:31:48
 */
public class PersonCenterActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	private final static int TO_FINFO_REQUEST = 1;
	@AbIocView(id = R.id.photoIv)
	RoundImageView mPhotoIv;// 头像
	@AbIocView(id = R.id.nameTv)
	TextView mnameTv;// 名称
	@AbIocView(click = "btnClick", id = R.id.yuyueIngLl)
	LinearLayout mYuyueIngLl;// 预约中
	@AbIocView(id = R.id.yuyueIngTv)
	TextView mYuyueIngTv;// 预约中
	@AbIocView(id = R.id.waitEvaTv)
	TextView mWaitEvaTv;// 待评价

	@AbIocView(click = "btnClick", id = R.id.infoRl)
	RelativeLayout mInfoRl;// 顶部点击进入个人信息布局
	@AbIocView(click = "btnClick", id = R.id.myorderRl)
	RelativeLayout mMyorderRl;// 我的订单
	@AbIocView(click = "btnClick", id = R.id.waitPayRl)
	LinearLayout mWaitPayRl;// 待付款
	@AbIocView(click = "btnClick", id = R.id.waitfhRl)
	LinearLayout mWaitfhRl;// 待发货
	@AbIocView(click = "btnClick", id = R.id.waitRewardRl)
	LinearLayout mWaitRewardRl;// 待收货
	@AbIocView(click = "btnClick", id = R.id.waitTkRl)
	LinearLayout mWaitTkRl;// 待退款
	@AbIocView(click = "btnClick", id = R.id.myReserRl)
	RelativeLayout mMyReserRl;// 我的预约
	@AbIocView(click = "btnClick", id = R.id.myShopCarRl)
	RelativeLayout mMyShopCarRl;// 我的购物车
	@AbIocView(click = "btnClick", id = R.id.myAddressRl)
	RelativeLayout mMyAddressRl;// 我的收货地址

	/**
	 * 待付款
	 */
	@AbIocView(id = R.id.waitpayTv)
	TextView mWaitPayTv;//
	/**
	 * 待发货
	 */
	@AbIocView(id = R.id.waitfhTv)
	TextView mWaitfhTv;//
	/**
	 * 待收货
	 */
	@AbIocView(id = R.id.waitRewardTv)
	TextView mWaitRewardTv;//
	/**
	 * 待退款
	 */
	@AbIocView(id = R.id.waitTkTv)
	TextView mWaitTkTv;//

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(PersonCenterActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_GET_CUSTOMER_MAIN_DATA_SUCC:
				CustomerMainData customerMainData = (CustomerMainData) msg.obj;
				updataData(customerMainData);
				break;
			case HandleAction.HttpType.HTTP_GET_CUSTOMER_MAIN_DATA_FAIL:
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
		setAbContentView(R.layout.activity_person_center);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("个人中心");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);

	}

	/**
	 * 加载布局
	 * 
	 * @author Xubiao
	 */
	private void initView() {
		mAbTitleBar.clearRightView();
		View rightViewMore = mInflater.inflate(R.layout.right_menu_btn, null);
		mAbTitleBar.addRightView(rightViewMore);
		final TextView setButton = (TextView) rightViewMore
				.findViewById(R.id.menuBtn);
		setButton.setBackgroundResource(R.drawable.main_more);
		mAbTitleBar.getRightLayout().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				PersonCenterMenuPop mainMenPop = new PersonCenterMenuPop(
						PersonCenterActivity.this, setButton);
				mainMenPop.show();
				findViewById(R.id.arrowIv).setVisibility(View.INVISIBLE);
				mainMenPop.setOnDismissListener(new OnDismissListener() {

					@Override
					public void onDismiss() {
						// TODO Auto-generated method stub
						findViewById(R.id.arrowIv).setVisibility(View.VISIBLE);
					}
				});
			}
		});
	}

	/**
	 * 加载数据
	 * 
	 * @author Xubiao
	 */
	public void initData() {
		AccountInfo accountInfo = Cache.getAccountInfo();
		if (null != accountInfo) {

			Drawable defaultPhoto = Cache.defaultPhoto(this);
			if (null != defaultPhoto) {
				ImageLoaderHelper.displayImage(accountInfo.headIconPath,
						mPhotoIv, defaultPhoto);
			} else {
				ImageLoaderHelper.displayImage(accountInfo.headIconPath,
						mPhotoIv, R.drawable.person_photo_def);
			}
			mnameTv.setText(accountInfo.nickName);
			if (AbWifiUtil.isConnectivity(this)) {
				GetCustomerMainDataRequest.getInstance().sendRequest(mHandler,
						Cache.getUser().userName);
				AbDialogUtil.showProgressDialog(PersonCenterActivity.this, 0,
						"获取个人中心数据...");
			} else {
				AbToastUtil.showToast(this, R.string.please_check_network);
			}
		}
	}

	/**
	 * 
	 * @param customerMainData
	 * @author Xubiao
	 */
	private void updataData(CustomerMainData customerMainData) {

		if (null != customerMainData) {
			if (customerMainData.noPayCount > 0) {
				mWaitPayTv.setText(customerMainData.noPayCount > 99 ? "99+"
						: customerMainData.noPayCount + "");
				mWaitPayTv.setVisibility(View.VISIBLE);
			} else {
				mWaitPayTv.setVisibility(View.INVISIBLE);
			}
			if (customerMainData.orderSentCount > 0) {
				mWaitRewardTv
						.setText(customerMainData.orderSentCount > 99 ? "99+"
								: customerMainData.orderSentCount + "");
				mWaitRewardTv.setVisibility(View.VISIBLE);
			} else {
				mWaitRewardTv.setVisibility(View.INVISIBLE);
			}
			if (customerMainData.processingCount > 0) {
				mWaitfhTv.setText(customerMainData.processingCount > 99 ? "99+"
						: customerMainData.processingCount + "");
				mWaitfhTv.setVisibility(View.VISIBLE);
			} else {
				mWaitfhTv.setVisibility(View.INVISIBLE);
			}
			if (customerMainData.refundProcessingCount > 0) {
				mWaitTkTv
						.setText(customerMainData.refundProcessingCount > 99 ? "99+"
								: customerMainData.refundProcessingCount + "");
				mWaitTkTv.setVisibility(View.VISIBLE);

			} else {
				mWaitTkTv.setVisibility(View.INVISIBLE);
			}
			if (customerMainData.reservationCount > 0) {
				mYuyueIngTv.setText(customerMainData.reservationCount + "");
				mYuyueIngTv.setVisibility(View.VISIBLE);
			} else {
				mYuyueIngTv.setText("0");
				mYuyueIngTv.setVisibility(View.VISIBLE);
			}

		}
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.infoRl:
			Intent intent = new Intent(PersonCenterActivity.this,
					PersonInfoActivity.class);
			startActivityForResult(intent, TO_FINFO_REQUEST);
			break;
		case R.id.myorderRl:// 我的订单
			startOrderList(OrderListActivity.STATUS_ALL);
			break;
		case R.id.waitPayRl:// 待付款
			startOrderList(OrderListActivity.STATUS_PAY);
			// startActivity(UnifyWebViewActivity
			// .toWebPage(this,
			// "http://172.16.16.144:8080/rui/control/getHomePageProducts?id=Gshop"));
			break;
		case R.id.waitRewardRl:// 待收货
			startOrderList(OrderListActivity.STATUS_RECEIVE);

			break;
		case R.id.waitfhRl:// 待发货
			startOrderList(OrderListActivity.STATUS_SEND);

			// startActivity(UnifyWebViewActivity
			// .toWebPage(this,
			// "http://172.16.16.144:8080/rui/control/getHomePageProducts?id=Gshop"));
			break;
		case R.id.yuyueIngLl:
		case R.id.myReserRl:// 我的预约
			startAppointOrderList();
			break;
		case R.id.waitTkRl:// 待退款
			startOrderList(OrderListActivity.STATUS_MONEY);

			break;
		case R.id.myShopCarRl:
			intent = new Intent(PersonCenterActivity.this,
					ShopCartActivity.class);
			startActivity(intent);
			break;
		case R.id.myAddressRl:
			intent = new Intent(PersonCenterActivity.this,
					MyAddressActivity.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	private void startOrderList(int orderStatus) {
		Intent intent = new Intent(PersonCenterActivity.this,
				OrderListActivity.class);
		intent.putExtra("orderStatus", orderStatus);
		intent.putExtra("orderType", OrderType.SALES_ORDER_O2O_SALE.toString()
				+ "," + OrderType.SALES_ORDER_B2C.toString());
		startActivity(intent);
	}

	private void startAppointOrderList() {
		Intent intent = new Intent(PersonCenterActivity.this,
				OrderListActivity.class);
		intent.putExtra("orderStatus", OrderListActivity.STATUS_ALL);
		intent.putExtra("orderType",
				OrderType.SALES_ORDER_O2O_SERVICE.toString());
		startActivity(intent);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case TO_FINFO_REQUEST:
			AccountInfo accountInfo = Cache.getAccountInfo();
			if (null != accountInfo) {
				Drawable defaultPhoto = Cache.defaultPhoto(this);
				if (null != defaultPhoto) {
					ImageLoaderHelper.displayImage(accountInfo.headIconPath,
							mPhotoIv, defaultPhoto);
				} else {
					ImageLoaderHelper.displayImage(accountInfo.headIconPath,
							mPhotoIv, R.drawable.person_photo_def);
				}
				mnameTv.setText(accountInfo.nickName);
			}
			break;
		}
	}
}
