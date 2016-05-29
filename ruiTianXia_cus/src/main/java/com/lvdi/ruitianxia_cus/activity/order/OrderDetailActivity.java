package com.lvdi.ruitianxia_cus.activity.order;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.LvDiActivity;
import com.lvdi.ruitianxia_cus.constants.OrderStatus;
import com.lvdi.ruitianxia_cus.constants.OrderType;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.Constant;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.order.OrderDetail;
import com.lvdi.ruitianxia_cus.model.order.OrderProduct;
import com.lvdi.ruitianxia_cus.model.shopcart.RspConfrimOrder.CartItem;
import com.lvdi.ruitianxia_cus.request.order.CancelOrderRequest;
import com.lvdi.ruitianxia_cus.request.order.CompleteOrderRequest;
import com.lvdi.ruitianxia_cus.request.order.GetOrderDetailRequest;
import com.lvdi.ruitianxia_cus.request.order.RefundOrderRequest;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.lvdi.ruitianxia_cus.util.MathUtil;
import com.lvdi.ruitianxia_cus.util.TimesUtil;
import com.lvdi.ruitianxia_cus.view.dialog.DialogBase;
import com.lvdi.ruitianxia_cus.view.dialog.DialogBase.OnButtonClickListener;

public class OrderDetailActivity extends LvDiActivity {
	public static final int REQ_TO_REFUND = 102;
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
	/** 订单实付总额 */
	@AbIocView(id = R.id.tv_total_price)
	TextView mTvTotalMoney;
	/** 下单时间 */
	@AbIocView(id = R.id.tv_order_time)
	TextView mTvOrderTime;
	/** 订单编号 */
	@AbIocView(id = R.id.tv_order_num)
	TextView mTvOrderNum;
	/** 订单总额 */
	@AbIocView(id = R.id.tv_order_price)
	TextView mTvOrderPrice;
	/** 备注 */
	@AbIocView(id = R.id.tv_remark)
	TextView mTvRemark;
	/** 订单状态 */
	@AbIocView(id = R.id.tv_order_status)
	TextView mTvOrderStatus;
	/** 预约时间 */
	@AbIocView(id = R.id.tv_appoint_time)
	TextView mTvAppointTime;
	/** 退款原因 */
	@AbIocView(id = R.id.tv_refund_reason)
	TextView mTvRefundReason;
	/** 配送费 */
	@AbIocView(id = R.id.tv_order_express_price)
	TextView mTvExpressPrice;
	/** 商品列表 */
	@AbIocView(id = R.id.layout_products)
	LinearLayout mLayoutProducts;

	/**
	 * 绿颜色字体按钮
	 */
	private Button mBtnGreen;
	/**
	 * 橘黄色字体按钮
	 */
	private Button mBtnOrange;

	/** 订单Id */
	private String orderId;

	private OrderDetail mData;

	private boolean isStatusChange = false;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(OrderDetailActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_GET_OREDER_DETAIL_SUCC:
				if (msg.obj != null) {
					mData = (OrderDetail) msg.obj;
					refreshUi();
				}
				break;
			case HandleAction.HttpType.HTTP_GET_OREDER_DETAIL_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_CANCEL_ORDER_SUCC:// 取消订单
				isStatusChange = true;
				sendOrderDetailReq();
				break;
			case HandleAction.HttpType.HTTP_CANCEL_ORDER_FAIL:
				if (msg.obj != null)
					AbToastUtil.showToast(getApplicationContext(),
							(String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_COMPLETE_ORDER_SUCC:// 确认收货
				isStatusChange = true;
				sendOrderDetailReq();
				break;
			case HandleAction.HttpType.HTTP_COMPLETE_ORDER_FAIL:
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
		setAbContentView(R.layout.activity_order_detail);
		setAbTitle("订单详情");
	}
	/**
	 *  
	 */
	private BroadcastReceiver orderStatusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(!TextUtils.isEmpty(orderId))
			sendOrderDetailReq();
		}
	};
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(orderStatusReceiver != null){
			unregisterReceiver(orderStatusReceiver);
		}
	}
	@Override
	public void initData() {
		// TODO Auto-generated method stub
		super.initData();
		registerReceiver(orderStatusReceiver, new IntentFilter(
				Constant.ACTION_ORDER_STATUS_CHANGE));
		if (getIntent().hasExtra("orderId")) {
			orderId = getIntent().getStringExtra("orderId");
			sendOrderDetailReq();
		}

	}

	@Override
	public void onBackPressed() {
		if (isStatusChange) {
			setResult(RESULT_OK);
		}
		super.onBackPressed();
	}

	private void sendOrderDetailReq() {
		AbDialogUtil.showProgressDialog(this, 0, "请稍等...");
		GetOrderDetailRequest mRequest = new GetOrderDetailRequest();
		mRequest.sendRequest(mHandler, orderId);
	}

	private String getTotalPrice(){
		String price = "";
		try {
			price = Double.parseDouble(mData.orderHeader.grandTotal)
					+ Double.parseDouble(mData.discountAmount) + "";
		} catch (Exception e) {
			// TODO: handle exception
			price = mData.orderHeader.grandTotal;
		}
		return price;
	}
	private void refreshUi() {
		if (mData.postAddress != null) {
			mTvAddressName.setText(mData.postAddress.recipient);
			mTvAddressPhone.setText(mData.postAddress.contactNumber);
			mTvAddressAddress.setText(mData.postAddress.address);
		}

		mTvOrderTime.setText(TimesUtil.formatTime9(mData.createDate));
		mTvExpressPrice.setText(getString(R.string.app_rmb)+mData.orderShippingTotal);
		
		showExpress();
		addProducts();
		if (mData.orderHeader != null) {
			mTvOrderNum.setText(mData.orderHeader.orderId);
			
			mTvOrderPrice.setText(getString(R.string.app_rmb)+MathUtil.formatPrice(getTotalPrice()));
			mTvShopName.setText(mData.orderHeader.storeName);
			mTvTotalMoney.setText(mData.orderHeader.grandTotal);
			mTvRemark.setText(formatStr(mData.orderHeader.remark));

			if (OrderType.SALES_ORDER_B2C.toString().equals(
					mData.orderHeader.orderTypeId)) {// b2c订单
				findViewById(R.id.layout_status).setVisibility(View.GONE);
				mBtnGreen = (Button) findViewById(R.id.btn_green);
				mBtnOrange = (Button) findViewById(R.id.btn_orange);
				
				findViewById(R.id.layout_order_status).setVisibility(View.VISIBLE);
				dealO2OAndB2c();
			} else if (OrderType.SALES_ORDER_O2O_SALE.toString().equals(
					mData.orderHeader.orderTypeId)) {// o2o订单
				findViewById(R.id.layout_status).setVisibility(View.VISIBLE);
				mBtnGreen = (Button) findViewById(R.id.btn_green_1);
				mBtnOrange = (Button) findViewById(R.id.btn_orange_1);
				dealO2OAndB2c();
				
				findViewById(R.id.layout_order_exp_time).setVisibility(View.VISIBLE);
				TextView mTvExpTime = (TextView) findViewById(R.id.tv_order_exp_time);
				mTvExpTime.setText(mData.orderHeader.reservationDate);
				
			} else if (OrderType.SALES_ORDER_O2O_SERVICE.toString().equals(
					mData.orderHeader.orderTypeId)) {// 预约订单
				findViewById(R.id.layout_status).setVisibility(View.VISIBLE);
				findViewById(R.id.layout_b2c).setVisibility(View.GONE);
				findViewById(R.id.layout_order_price).setVisibility(View.GONE);
				findViewById(R.id.layout_order_express).setVisibility(View.GONE);

				mTvRemark.setVisibility(View.GONE);
				mTvAppointTime.setVisibility(View.VISIBLE);
				String time = TimesUtil
				.formatTime8(mData.orderHeader.reservationDate);
				time = "".equals(time)? mData.orderHeader.reservationDate:time;
				mTvAppointTime
						.setText("预约时间： "
								+ time);
				mBtnGreen = (Button) findViewById(R.id.btn_green_1);
				mBtnOrange = (Button) findViewById(R.id.btn_orange_1);
				OrderStatus os = OrderStatus
						.getByKey(mData.orderHeader.orderStatus);
				mTvOrderStatus.setText(os.getAppointlabel());

				if (os == OrderStatus.ORDER_CREATED || os == OrderStatus.ORDER_APPROVED
						) {// 订单初始状态,待付款
					mBtnGreen.setVisibility(View.VISIBLE);

					mBtnGreen.setText("取消预约");
					mBtnGreen.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							showCancelOrderDlg(mData.orderHeader.orderId,
									"确定要取消预约？");
						}
					});
				} else {
					mBtnGreen.setVisibility(View.GONE);
					mBtnOrange.setVisibility(View.GONE);
				}
			}

		}

	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == RESULT_OK) {
			switch (arg0) {
			case REQ_TO_REFUND:
			case REQ_TO_PAY:
				isStatusChange = true;
				sendOrderDetailReq();
				break;

			default:
				break;
			}
		}
	}

	private void dealO2OAndB2c() {
		OrderStatus os = OrderStatus.getByKey(mData.orderHeader.orderStatus);
		((TextView)findViewById(R.id.tv_order_status_2) ).setText(os.getLable());//b2c订单状态

		mTvOrderStatus.setText(os.getLable());
		mBtnGreen.setVisibility(View.GONE);
		mBtnOrange.setVisibility(View.GONE);
		if(!TextUtils.isEmpty(mData.returnReason)){
			findViewById(R.id.layout_refund).setVisibility(View.VISIBLE);
			mTvRefundReason.setText(mData.returnReason);
		}
		if (!TextUtils.isEmpty(mData.discountAmount)) {
			findViewById(R.id.layout_order_discount)
					.setVisibility(View.VISIBLE);
			((TextView) findViewById(R.id.tv_order_discount_price))
					.setText(getString(R.string.app_rmb) + mData.discountAmount);//

		}

		if (os == OrderStatus.ORDER_CREATED) {// 订单初始状态,待付款
			mBtnGreen.setVisibility(View.VISIBLE);
			mBtnOrange.setVisibility(View.VISIBLE);
			mBtnGreen.setText("取消订单");
			mBtnOrange.setText("付款");
			mBtnGreen.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					showCancelOrderDlg(mData.orderHeader.orderId, "确定取消订单？");
				}
			});
			mBtnOrange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					startActivityForResult(new Intent(OrderDetailActivity.this,
							PayMethodActivity.class).putExtra("orderId",
							mData.orderHeader.orderId).putExtra("totalPrice",
							mData.orderHeader.grandTotal),REQ_TO_PAY);
				}
			});

		} else if (os == OrderStatus.ORDER_PROCESSING) {// 待发货
			mBtnGreen.setVisibility(View.GONE);
			mBtnOrange.setVisibility(View.VISIBLE);
			mBtnOrange.setText("退货/退款");
			mBtnOrange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					toRefundOrder(mData.orderHeader.orderId);
				}
			});
		} else if (os == OrderStatus.ORDER_SENT) {// 待收货
			mBtnGreen.setVisibility(View.VISIBLE);
			mBtnOrange.setVisibility(View.VISIBLE);
			mBtnGreen.setText("退款/退货");
			mBtnOrange.setText("确认收货");

			mBtnGreen.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					toRefundOrder(mData.orderHeader.orderId);
				}
			});
			mBtnOrange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub

					toConfirmOrder(mData.orderHeader.orderId);
				}
			});

		} else if (os == OrderStatus.ORDER_APPROVED) {// 待接单
			mBtnGreen.setVisibility(View.GONE);
			mBtnOrange.setVisibility(View.VISIBLE);
			mBtnOrange.setText("退款/退货");
			mBtnOrange.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					toRefundOrder(mData.orderHeader.orderId);
				}
			});
		} else {
			mBtnGreen.setVisibility(View.GONE);
			mBtnOrange.setVisibility(View.GONE);
		}
	}

	/**
	 * 确认收货
	 * 
	 * @param orderId
	 */
	private void toConfirmOrder(final String orderId) {
		new DialogBase(OrderDetailActivity.this).defSetCancelBtn("取消", null)
				.defSetConfirmBtn("确定", new OnButtonClickListener() {
					@Override
					public void onClick(DialogBase dialog) {
						CompleteOrderRequest req = new CompleteOrderRequest();
						req.sendRequest(mHandler, orderId);
						AbDialogUtil.showProgressDialog(
								OrderDetailActivity.this, 0, "请稍等...");
					}
				}).defSetContentTxt("确认收货？").show();
	}

	/**
	 * 退款
	 * 
	 * @param orderId
	 */
	private void toRefundOrder(final String orderId) {
		startActivityForResult(
				new Intent(this, RefundOrderActivity.class).putExtra("orderId",
						orderId), REQ_TO_REFUND);
	}

	/**
	 * 取消订单
	 * 
	 * @param orderId
	 */
	private void showCancelOrderDlg(final String orderId, String title) {
		new DialogBase(OrderDetailActivity.this).defSetCancelBtn("取消", null)
				.defSetConfirmBtn("确定", new OnButtonClickListener() {
					@Override
					public void onClick(DialogBase dialog) {
						CancelOrderRequest req = new CancelOrderRequest();
						req.sendRequest(mHandler, orderId);
						AbDialogUtil.showProgressDialog(
								OrderDetailActivity.this, 0, "取消订单中...");
					}
				}).defSetContentTxt(title).defSetTitleTxt("温馨提示").show();
	}

	/**
	 * 显示物流信息
	 */
	private void showExpress() {
		if (mData != null
				&& mData.orderHeader != null
				&& OrderType.SALES_ORDER_B2C.toString()
						.equals(mData.orderHeader.orderTypeId)) {// 只有b2c订单有物流信息
			findViewById(R.id.layout_logistic).setVisibility(View.VISIBLE);

			TextView mTvExpCompany = (TextView) findViewById(R.id.tv_exp_company);
			TextView mTvExpNum = (TextView) findViewById(R.id.tv_exp_num);
			LinearLayout mLayoutLogisDetail = (LinearLayout) findViewById(R.id.layout_logistic_detail);

			mTvExpCompany.setText("物流公司：" + formatStr(mData.expressCompany));
			mTvExpNum.setText("快递单号：" + formatStr(mData.trackingNum));
			mLayoutLogisDetail.removeAllViews();

			boolean isCreate = false;
			boolean isSend = false;
			boolean isComplete = false;
			if (!TextUtils.isEmpty(mData.createDate)) {// 下单时间
				isCreate = true;
			}
			if (!TextUtils.isEmpty(mData.sentDate)) {// 发货时间（未发货，该值为空）
				isSend = true;
			}
			if (!TextUtils.isEmpty(mData.completedDate)) {// 收货时间（未收货，该值为空）
				isComplete = true;
			}
			if (isCreate) {
				if (isSend && isComplete) {
					findViewById(R.id.layout_logistic_company).setVisibility(View.VISIBLE);
					mLayoutLogisDetail.addView(getLogisticItem("商品已签收",
							mData.completedDate, true, false, true, false));
					mLayoutLogisDetail.addView(getLogisticItem("出库",
							mData.sentDate, false, true, true, false));
					mLayoutLogisDetail.addView(getLogisticItem("下单",
							mData.createDate, false, true, false, true));
				} else if (isSend) {
					findViewById(R.id.layout_logistic_company).setVisibility(View.VISIBLE);
					mLayoutLogisDetail.addView(getLogisticItem("出库",
							mData.sentDate, true, false, true, false));
					mLayoutLogisDetail.addView(getLogisticItem("下单",
							mData.createDate, false, true, false, true));
				} else { 
					findViewById(R.id.layout_logistic_company).setVisibility(View.GONE);
					mLayoutLogisDetail.addView(getLogisticItem("下单",
							mData.createDate, true, false, false, true));
				}
			}

		} else {
			findViewById(R.id.layout_logistic).setVisibility(View.GONE);
		}
	}

	/**
	 * @param expStatus
	 *            物流状态描述
	 * @param expTime
	 *            物流更新时间
	 * @param isCurrent
	 *            是否处为最新的物流状态，
	 * @param isTopLineShow
	 *            上面的线条
	 * @param isBottomLineShow
	 *            下面的线条
	 * @param isLast
	 *            右下方的线条
	 * @return
	 */
	private View getLogisticItem(String expStatus, String expTime,
			boolean isCurrent, boolean isTopLineShow, boolean isBottomLineShow,
			boolean isLast) {
		View v = LayoutInflater.from(this).inflate(
				R.layout.item_order_detail_logistic, null);
		TextView mTvExpStatus = (TextView) v.findViewById(R.id.tv_exp_status);
		TextView mTvExpTime = (TextView) v.findViewById(R.id.tv_exp_time);
		ImageView icon = (ImageView) v.findViewById(R.id.icon);
		mTvExpStatus.setText(expStatus);
		mTvExpTime.setText(TimesUtil.formatTime7(expTime));
		if (isCurrent) {
			icon.setImageResource(R.drawable.cirle_green);
		} else {
			icon.setImageResource(R.drawable.cirle_gray);
		}
		if (isTopLineShow) {
			v.findViewById(R.id.line_top).setVisibility(View.VISIBLE);
		} else {
			v.findViewById(R.id.line_top).setVisibility(View.INVISIBLE);
		}
		if (isBottomLineShow) {
			v.findViewById(R.id.line_bottom).setVisibility(View.VISIBLE);
		} else {
			v.findViewById(R.id.line_bottom).setVisibility(View.INVISIBLE);
		}

		if (isLast) {// 最后一条物流状态，右下面的线隐藏
			v.findViewById(R.id.line_right).setVisibility(View.INVISIBLE);
		} else {
			v.findViewById(R.id.line_right).setVisibility(View.VISIBLE);
		}
		return v;
	}

	/**
	 * 增加商品详情
	 */
	private void addProducts() {
		mLayoutProducts.removeAllViews();
		if (mData.productList != null) {
			View v = null;
			for (OrderProduct item : mData.productList) {
				v = LayoutInflater.from(this).inflate(
						R.layout.item_order_detail_product2, null);
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
				mTvProductAvgPrice.setText("￥" + formatStr(item.unitPrice));
				mTvProductNum.setText("x " + formatStr(item.quantity));

				mLayoutProducts.addView(v);
			}
		}
	}
}
