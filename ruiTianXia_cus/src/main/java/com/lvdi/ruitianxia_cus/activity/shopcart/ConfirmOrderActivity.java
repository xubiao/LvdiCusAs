package com.lvdi.ruitianxia_cus.activity.shopcart;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.google.gson.Gson;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.LvDiActivity;
import com.lvdi.ruitianxia_cus.activity.MyAddressActivity;
import com.lvdi.ruitianxia_cus.activity.order.OrderDetailActivity;
import com.lvdi.ruitianxia_cus.activity.order.OrderListActivity;
import com.lvdi.ruitianxia_cus.activity.order.PayMethodActivity;
import com.lvdi.ruitianxia_cus.constants.OrderType;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.AddressInfo;
import com.lvdi.ruitianxia_cus.model.shopcart.DbProduct;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqConfirmOrder;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqCreateOrder;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqProductItem;
import com.lvdi.ruitianxia_cus.model.shopcart.RspConfrimOrder;
import com.lvdi.ruitianxia_cus.model.shopcart.RspConfrimOrder.CartItem;
import com.lvdi.ruitianxia_cus.model.shopcart.RspCreateOrder;
import com.lvdi.ruitianxia_cus.request.GetAddressRequest;
import com.lvdi.ruitianxia_cus.request.order.ConfirmOrderRequest;
import com.lvdi.ruitianxia_cus.request.order.CreateOrderRequest;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.lvdi.ruitianxia_cus.util.MathUtil;

public class ConfirmOrderActivity extends LvDiActivity {
	public static final int REQ_TO_SELECT_ADDRESS = 201;
	public static final int REQ_TO_SELECT_O2O_SALE_TIME = 202;
//	public static final int REQ_TO_SELECT_O2O_SERVICE_TIME = 203;
	public static final int REQ_TO_PAY = 103;
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
	 * 店铺名称
	 */
	@AbIocView(id = R.id.tv_shop_name)
	private TextView mTvShopName;

	/**
	 * 商品集父控件
	 */
	@AbIocView(id = R.id.layout_products)
	private LinearLayout mLayoutProducts;
	/**
	 * 运费
	 */
	@AbIocView(id = R.id.tv_price_remark)
	private TextView mTvExpress;
	/**
	 * 价格合计
	 */
	@AbIocView(id = R.id.tv_total_price_1)
	private TextView mTvCenterPrice;

	/**
	 * 价格合计
	 */
	@AbIocView(id = R.id.tv_total_price_2)
	private TextView mTvBottomPrice;
	/**
	 * 确认
	 */
	@AbIocView(id = R.id.tv_submit, click = "btnClick")
	private TextView mTvConfirm;
	/**
	 * 兑换
	 */
	@AbIocView(id = R.id.btn_exchange, click = "btnClick")
	private Button mBtnExchange;
	/**
	 * 地址
	 */
	@AbIocView(id = R.id.layout_address, click = "btnClick")
	private RelativeLayout mLayoutAddress;
	/**
	 * 兑换码提示
	 */
	@AbIocView(id = R.id.layout_reduce_tip, click = "btnClick")
	private RelativeLayout mLayoutReduceTip;
	/**
	 * 兑换码
	 */
	@AbIocView(id = R.id.layout_reduce)
	private RelativeLayout mLayoutReduce;

	/**
	 * 选择配送时间
	 */
	@AbIocView(id = R.id.layout_slect_exp_time, click = "btnClick")
	private RelativeLayout mLayoutSelectExpTime;
	/**
	 * 配送时间
	 */
	@AbIocView(id = R.id.tv_exp_time)
	private TextView mTvExpTime;

	/** 备注 */
	@AbIocView(id = R.id.et_remark)
	private EditText mEtMark;
	/** 促销吗 */
	@AbIocView(id = R.id.et_promote_code)
	private EditText mEtPromoteCode;
	/** 确认订单接口请求参数 */
	private ReqConfirmOrder mReqConfirmOrder;
	/** 确认订单接口返回数据 */
	private RspConfrimOrder mRspConfirmOrder;
	/** 选择的地址信息 */
	private AddressInfo mAddressInfo;

	/**
	 * 配送时间(O2O订单使用)
	 */
	private String mExpTime;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(ConfirmOrderActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_CHECK_POST_CONFIRM_ORDER_SUCC:
				if (msg.obj != null) {
					mRspConfirmOrder = (RspConfrimOrder) msg.obj;
					refreshUi();
				}
				break;
			case HandleAction.HttpType.HTTP_CHECK_POST_CONFIRM_ORDER_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_CREATE_ORDER_SUCC:
				if (msg.obj != null) {
					RspCreateOrder bean = (RspCreateOrder) msg.obj;
					if(OrderType.SALES_ORDER_O2O_SERVICE
						.toString().equals(mReqConfirmOrder.orderTypeId)){//预约
						Intent intent = new Intent(ConfirmOrderActivity.this,
								OrderListActivity.class);
						intent.putExtra("orderStatus",
								OrderListActivity.STATUS_ALL);
						intent.putExtra("orderType",
								OrderType.SALES_ORDER_O2O_SERVICE.toString());
						startActivity(intent);
						finish();
					}else{
						startActivityForResult(new Intent(ConfirmOrderActivity.this,
								PayMethodActivity.class).putExtra("orderId",
								bean.orderId).putExtra("totalPrice",
								mRspConfirmOrder.shoppingCartInfo.totalPayAmount),REQ_TO_PAY);
					}
					// 清空选中的购物车、
					CartManager.getInstance().delCarts(mReqConfirmOrder);
					
				}
				break;
			case HandleAction.HttpType.HTTP_CREATE_ORDER_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_GET_ADDRESS_LIST_SUCC:
				if(msg.obj != null){
					List<AddressInfo> mAddressInfos = (List<AddressInfo>) msg.obj;

					if(mAddressInfos != null && mAddressInfos.size() >0){
						for(AddressInfo address:mAddressInfos){
							if("Y".equals(address.isDefault)){
								mAddressInfo = address;
								mTvAddressName.setText(mAddressInfo.recipient);
								mTvAddressPhone.setText(mAddressInfo.contactNumber);
								mTvAddressAddress.setText(mAddressInfo.zipCode+mAddressInfo.address);
								break;
							}
						}
					}
				}
				
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
		setAbContentView(R.layout.activity_order_confirm);
		setAbTitle("确认订单");
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		super.initData();

		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mEtMark.getWindowToken(), 0);
		if (getIntent().hasExtra("ReqConfirmOrder")) {// 从购物车页面过来
			mReqConfirmOrder = (ReqConfirmOrder) getIntent().getExtras().get(
					"ReqConfirmOrder");
		}
		if (getIntent().hasExtra("RspConfrimOrder")) {// 从购物车页面过来
			mRspConfirmOrder = (RspConfrimOrder) getIntent().getExtras().get(
					"RspConfrimOrder");
			refreshUi();
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
		if (mReqConfirmOrder != null
				&& (OrderType.SALES_ORDER_O2O_SALE.toString().equals(
						mReqConfirmOrder.orderTypeId) || OrderType.SALES_ORDER_O2O_SERVICE
						.toString().equals(mReqConfirmOrder.orderTypeId))) {// O2O订单显示选择时间
			findViewById(R.id.layout_slect_exp_time)
					.setVisibility(View.VISIBLE);
			if (OrderType.SALES_ORDER_O2O_SALE.toString().equals(
					mReqConfirmOrder.orderTypeId)) {
				mTvExpTime.setText("配送时间：");
			} else {
				mTvExpTime.setText("预约时间：");
			}
		}
		if (isLogin()) {
			GetAddressRequest.getInstance().sendRequest(mHandler,
					Cache.getAccountInfo().partyId);
		}
	}

	private void sendReqConfrimOrder() {
		AbDialogUtil.showProgressDialog(this, 0, "请稍等...");
		ConfirmOrderRequest req = new ConfirmOrderRequest();
		req.sendRequest(mHandler, new Gson().toJson(mReqConfirmOrder));
	}
	private boolean isCode = false;

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.layout_address:// 去选择地址
			if (isLogin()) {
				startActivityForResult(
						new Intent(this, MyAddressActivity.class).putExtra(
								"mode", MyAddressActivity.MODE_SELECT),
						REQ_TO_SELECT_ADDRESS);
			} else {
				startLoginActivity();
			}
			break;
		case R.id.tv_submit:// 去结算
			submitOrder();
			break;
		case R.id.layout_reduce_tip://
			mLayoutReduceTip.setVisibility(View.GONE);
			mLayoutReduce.setVisibility(View.VISIBLE);

			break;
		case R.id.btn_exchange:// 兑换
			if (mReqConfirmOrder != null) {
				String code = mEtPromoteCode.getText().toString();
				if (!TextUtils.isEmpty(code)) {
					mReqConfirmOrder.promoCode = code;
					isCode = true;
					sendReqConfrimOrder();
				} else {
					AbToastUtil.showToast(this, "请输入兑换码");
				}
			} else {
				AbToastUtil.showToast(this, "数据异常，请重新选择商品");
				finish();
			}
			break;
		case R.id.layout_slect_exp_time:// 选择配送时间
			if (mReqConfirmOrder != null){
				if(OrderType.SALES_ORDER_O2O_SALE.toString().equals(mReqConfirmOrder.orderTypeId)){
					startActivityForResult(new Intent(this,
							SelectO2OSaleActivity.class).putExtra("orderType",
							mReqConfirmOrder.orderTypeId), REQ_TO_SELECT_O2O_SALE_TIME);
				}else {
					startActivityForResult(new Intent(this,
							SelectO2OServiceTimeActivity.class).putExtra("orderType",
							mReqConfirmOrder.orderTypeId), REQ_TO_SELECT_O2O_SALE_TIME);
				}
				
			}
				
			break;

		default:
			break;
		}
	}

	private void submitOrder() {
		if (mAddressInfo == null) {
			AbToastUtil.showToast(this, "请选择地址	");
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
		content.remark = mEtMark.getText().toString();
		content.promoCode = mEtPromoteCode.getText().toString();
		if (mReqConfirmOrder != null
				&& (OrderType.SALES_ORDER_O2O_SALE.toString().equals(
						mReqConfirmOrder.orderTypeId) || OrderType.SALES_ORDER_O2O_SERVICE
						.toString().equals(mReqConfirmOrder.orderTypeId))) {
			if (TextUtils.isEmpty(mExpTime)) {
				if (OrderType.SALES_ORDER_O2O_SALE.toString().equals(
						mReqConfirmOrder.orderTypeId)) {
					AbToastUtil.showToast(this, "请选择配送时间");
				} else {
					AbToastUtil.showToast(this, "请选择预约时间");
				}

				return;
			} else {
				content.reservationDate = mExpTime;
			}
		} else {
			content.reservationDate = "";
		}

		content.userLoginId = Cache.getUser().userName;

		CreateOrderRequest req = new CreateOrderRequest();
		req.sendRequest(mHandler, new Gson().toJson(content));
//		AbDialogUtil.showProgressDialog(this, 0, "正在提交订单...");
		displayProgressDlg("正在提交订单...");
	}

	private void refreshUi() {
		if (mRspConfirmOrder.shoppingCartInfo != null) {
			mTvShopName
					.setText(formatStr(mRspConfirmOrder.shoppingCartInfo.categoryName));
			mTvCenterPrice
					.setText(formatStr(mRspConfirmOrder.shoppingCartInfo.totalPayAmount));
			mTvBottomPrice
					.setText("合计："
							+ getString(R.string.app_rmb)
							+ formatStr(mRspConfirmOrder.shoppingCartInfo.totalPayAmount));
			if (!TextUtils
					.isEmpty(mRspConfirmOrder.shoppingCartInfo.totalShipping)) {
				mTvExpress
						.setText("(含运费￥"
								+ formatStr(mRspConfirmOrder.shoppingCartInfo.totalShipping)
								+ ")");
			} else {
				mTvExpress.setText("");
			}
			if (mRspConfirmOrder.shoppingCartInfo.promoAmount < 0) {
				mLayoutReduceTip.setVisibility(View.VISIBLE);
				mLayoutReduce.setVisibility(View.GONE);
				TextView tv_duihuan_tip = (TextView) findViewById(R.id.tv_duihuan_tip);
				tv_duihuan_tip.setText("您已优惠："+getString(R.string.app_rmb)
						+ MathUtil.formatPrice(Math.abs(mRspConfirmOrder.shoppingCartInfo.promoAmount)+""));
			}
			mLayoutProducts.removeAllViews();
			if (mRspConfirmOrder.shoppingCartInfo.cartItems != null) {
				View v = null;
				for (CartItem item : mRspConfirmOrder.shoppingCartInfo.cartItems) {
					v = LayoutInflater.from(this).inflate(
							R.layout.item_order_confirm_product, null);
					ImageView mIvIcon = (ImageView) v.findViewById(R.id.iv_pic);
					TextView mTvProductName = (TextView) v
							.findViewById(R.id.tv_product_name);
					TextView mTvProductAvgPrice = (TextView) v
							.findViewById(R.id.tv_product_avg_price);
					TextView mTvProductNum = (TextView) v
							.findViewById(R.id.tv_product_num);
					ImageLoaderHelper.displayImage(Config.HttpURLPrefix3
							+ item.smallImageUrl, mIvIcon);

					mTvProductName.setText(formatStr(item.productName));
					mTvProductAvgPrice.setText("￥" + formatStr(item.ItemPrice));
					mTvProductNum
							.setText("x "
									+ MathUtil
											.formatNoPoint(formatStr(item.ItemQuantity)));

					mLayoutProducts.addView(v);
				}
			}

		}
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if(arg0 == REQ_TO_PAY){
			if(arg1 == RESULT_OK){
				if(arg2 != null && arg2.hasExtra("orderId")){
					startActivity(new Intent(this,
							OrderDetailActivity.class).putExtra("orderId", arg2.getStringExtra("orderId"))
							);
				}
			}
			finish();
			return;
		}
		if (arg1 == RESULT_OK) {
			switch (arg0) {
			case REQ_TO_SELECT_ADDRESS:// 选择地址返回
				if (arg2 != null && arg2.hasExtra("AddressInfo")) {
					mAddressInfo = (AddressInfo) arg2
							.getSerializableExtra("AddressInfo");
					mTvAddressName.setText(mAddressInfo.recipient);
					mTvAddressPhone.setText(mAddressInfo.contactNumber);
					mTvAddressAddress.setText(mAddressInfo.zipCode+mAddressInfo.address);
				}
				break;
			case REQ_TO_SELECT_O2O_SALE_TIME:// 选择配送时间
				if (arg2 != null && arg2.hasExtra("data")) {
					mExpTime = arg2.getStringExtra("data");
					if (OrderType.SALES_ORDER_O2O_SALE.toString().equals(
							mReqConfirmOrder.orderTypeId)) {
						mTvExpTime.setText("配送时间：" + mExpTime);
					} else {
						mTvExpTime.setText("预约时间：" + mExpTime);
					}
				}

				break;

			default:
				break;
			}
		}
	}
}
