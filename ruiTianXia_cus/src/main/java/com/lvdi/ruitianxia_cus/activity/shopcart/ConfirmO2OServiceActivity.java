package com.lvdi.ruitianxia_cus.activity.shopcart;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.google.gson.Gson;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.LvDiActivity;
import com.lvdi.ruitianxia_cus.activity.MyAddressActivity;
import com.lvdi.ruitianxia_cus.activity.order.OrderDetailActivity;
import com.lvdi.ruitianxia_cus.constants.OrderType;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.AddressInfo;
import com.lvdi.ruitianxia_cus.model.shopcart.DbProduct;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqConfirmOrder;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqCreateOrder;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqProductItem;
import com.lvdi.ruitianxia_cus.model.shopcart.RspConfrimOrder;
import com.lvdi.ruitianxia_cus.model.shopcart.RspCreateOrder;
import com.lvdi.ruitianxia_cus.request.GetAddressRequest;
import com.lvdi.ruitianxia_cus.request.order.ConfirmOrderRequest;
import com.lvdi.ruitianxia_cus.request.order.CreateOrderRequest;
import com.lvdi.ruitianxia_cus.util.TimesUtil;

/**
 * @author Administrator o2o预约确定页面
 */
public class ConfirmO2OServiceActivity extends LvDiActivity implements
		OnClickListener {
	public static final int REQ_TO_SELECT_ADDRESS = 201;
	public static final int REQ_TO_SELECT_O2O_SALE_TIME = 202;
	/**
	 * 地址名称
	 */
	@AbIocView(id = R.id.tv_address_name)
	private TextView mTvAddressName;
	/**
	 * 联系手机
	 */
	@AbIocView(id = R.id.tv_address_phone)
	private TextView mTvAddressPhone;
	/**
	 * 详细地址
	 */
	@AbIocView(id = R.id.tv_address_address)
	private TextView mTvAddressAddress;
	/**
	 * 预约时间
	 */
	@AbIocView(id = R.id.tv_appoint_time)
	private TextView mTvAppointTime;
	@AbIocView(id = R.id.btn_confirm)
	private Button mBtnConfirm;
	/** 确认订单接口请求参数 */
	private ReqConfirmOrder mReqConfirmOrder;
	/** 确认订单接口返回数据 */
	private RspConfrimOrder mRspConfirmOrder;
	/** 选择的地址信息 */
	private AddressInfo mAddressInfo;

	/**
	 * 配送时间
	 */
	private String mExpTime;
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			AbDialogUtil.removeDialog(ConfirmO2OServiceActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_CHECK_POST_CONFIRM_ORDER_SUCC:
				if (msg.obj != null) {
					mRspConfirmOrder = (RspConfrimOrder) msg.obj;
					if (mRspConfirmOrder.shoppingCartInfo != null
							&& mRspConfirmOrder.shoppingCartInfo.categoryName != null) {
						setAbTitle(mRspConfirmOrder.shoppingCartInfo.categoryName);
					}
				}
				break;
			case HandleAction.HttpType.HTTP_CHECK_POST_CONFIRM_ORDER_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_CREATE_ORDER_SUCC:
				if (msg.obj != null) {
					RspCreateOrder bean = (RspCreateOrder) msg.obj;
//					Intent intent = new Intent(ConfirmO2OServiceActivity.this,
//							OrderListActivity.class);
//					intent.putExtra("orderStatus", OrderListActivity.STATUS_ALL);
//					intent.putExtra("orderType",
//							OrderType.SALES_ORDER_O2O_SERVICE.toString());
//					startActivity(intent);

					startActivity(new Intent(ConfirmO2OServiceActivity.this,
							OrderDetailActivity.class).putExtra("orderId", bean.orderId) 
							 );
					// 清空选中的购物车、
					finish();
				}
				break;
			case HandleAction.HttpType.HTTP_CREATE_ORDER_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_GET_ADDRESS_LIST_SUCC:
				if (msg.obj != null) {
					List<AddressInfo> mAddressInfos = (List<AddressInfo>) msg.obj;

					if (mAddressInfos != null && mAddressInfos.size() > 0) {
						for (AddressInfo address : mAddressInfos) {
							if ("Y".equals(address.isDefault)) {
								mAddressInfo = address;
								mTvAddressName.setText(mAddressInfo.recipient);
								mTvAddressPhone
										.setText(mAddressInfo.contactNumber);
								mTvAddressAddress.setText(mAddressInfo.zipCode
										+ mAddressInfo.address);
								break;
							}
						}
					}
				}
				break;
			case 250:
				if (mAddressInfo != null) {
					mTvAddressName.setText(mAddressInfo.recipient);
					mTvAddressPhone.setText(mAddressInfo.contactNumber);
					mTvAddressAddress.setText(mAddressInfo.zipCode
							+ mAddressInfo.address);
//					mTvAddressAddress.invalidate();
//					findViewById(R.id.layout_1).invalidate();
				}
				break;
			default:
				break;
			}
		};
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_order_service_confirm);
//		setAbTitle("");
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		super.initData();
		if (isLogin()) {
			GetAddressRequest.getInstance().sendRequest(mHandler,
					Cache.getAccountInfo().partyId);
		}
		if (getIntent().hasExtra("DbProductList")) {// 从h5页面直接提交订单
			ArrayList<DbProduct> mDbProductList = (ArrayList<DbProduct>) getIntent()
					.getExtras().get("DbProductList");
			mReqConfirmOrder = new ReqConfirmOrder();

			mReqConfirmOrder.orderTypeId = OrderType.SALES_ORDER_B2C.toString();
			mReqConfirmOrder.promoCode = "";
			AccountInfo accountInfo = Cache.getAccountInfo();

			if (accountInfo != null) {
				mReqConfirmOrder.userLoginId = Cache.getUser().userName;
			}
			mReqConfirmOrder.productItems = new ArrayList<ReqProductItem>();

			ReqProductItem item;
			for (DbProduct pi : mDbProductList) {
				mReqConfirmOrder.categoryId = pi.categoryId;
				item = new ReqProductItem();
				item.productId = pi.productId;
				// item.productId="10004";//测试用
				item.quantity = pi.quantity + "";
				mReqConfirmOrder.catalogId = pi.catalogId;
				mReqConfirmOrder.orderTypeId = pi.orderTypeId;
				mReqConfirmOrder.productItems.add(item);
			}
			sendReqConfrimOrder();
		}
	}

	private void submitOrder() {
		if (mAddressInfo == null) {
			AbToastUtil.showToast(this, "请选择地址	");
			return;
		}
		if (TextUtils.isEmpty(mExpTime)) {
			AbToastUtil.showToast(this, "请选择预约时间");
			return;
		}
		if (mReqConfirmOrder == null) {
			AbToastUtil.showToast(this, "请重新提交订单");
			return;
		}
		if (mRspConfirmOrder == null) {
			AbToastUtil.showToast(this, "确认订单失败");
			return;
		}
		if (!isLogin()) {
			startLoginActivity();
			return;
		}

		ReqCreateOrder content = new ReqCreateOrder();
		content.catalogId = mReqConfirmOrder.catalogId;
		content.categoryId = mReqConfirmOrder.categoryId;
		content.contactMechId = mAddressInfo.contactMechId;
		content.orderTypeId = mReqConfirmOrder.orderTypeId;
		content.productItems = mReqConfirmOrder.productItems;
		content.productStoreId = mReqConfirmOrder.productStoreId;
		content.remark = "";

		content.reservationDate = mExpTime;

		content.userLoginId = Cache.getUser().userName;

		CreateOrderRequest req = new CreateOrderRequest();
		req.sendRequest(mHandler, new Gson().toJson(content));
		AbDialogUtil.showProgressDialog(this, 0, "正在提交订单...");
	}

	private void sendReqConfrimOrder() {
		AbDialogUtil.showProgressDialog(this, 0, "请稍等...");
		ConfirmOrderRequest req = new ConfirmOrderRequest();
		req.sendRequest(mHandler, new Gson().toJson(mReqConfirmOrder));
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_confirm:// 提交
			submitOrder();
			break;
		case R.id.layout_select_appoint_time:// 选择预约时间
			if (mRspConfirmOrder != null) {
				startActivityForResult(new Intent(this,
						SelectO2OServiceTimeActivity.class).putExtra(
						"orderType", mReqConfirmOrder.orderTypeId),
						REQ_TO_SELECT_O2O_SALE_TIME);
			} else {
				AbToastUtil.showToast(this, "数据异常，请重新预约");
			}
			break;
		case R.id.layout_address_phone:// 选择地址
			if (mRspConfirmOrder == null) {
				AbToastUtil.showToast(this, "数据异常，请重新预约");
				return;
			}
			if (isLogin()) {
				startActivityForResult(
						new Intent(this, MyAddressActivity.class).putExtra(
								"mode", MyAddressActivity.MODE_SELECT),
						REQ_TO_SELECT_ADDRESS);
			} else {
				startLoginActivity();
			}
			break;

		default:
			break;
		}
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		findViewById(R.id.layout_address_phone).setOnClickListener(this);
		findViewById(R.id.layout_select_appoint_time).setOnClickListener(this);
		findViewById(R.id.btn_confirm).setOnClickListener(this);
	}
	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == RESULT_OK) {
			switch (arg0) {
			case REQ_TO_SELECT_ADDRESS:// 选择地址返回
				if (arg2 != null && arg2.hasExtra("AddressInfo")) {
					mAddressInfo = (AddressInfo) arg2
							.getSerializableExtra("AddressInfo");
					mTvAddressName.setText(mAddressInfo.recipient);
					mTvAddressPhone.setText(mAddressInfo.contactNumber);
					mTvAddressAddress.setText(mAddressInfo.zipCode
							+ mAddressInfo.address);
//					mHandler.sendEmptyMessageDelayed(250, 100);
				}
				break;
			case REQ_TO_SELECT_O2O_SALE_TIME:// 选择配送时间
				if (arg2 != null && arg2.hasExtra("data")) {
					mExpTime = arg2.getStringExtra("data");
					mTvAppointTime.setText(TimesUtil.formatTime3(mExpTime));
				}
				break;

			default:
				break;
			}
		}
	}
}
