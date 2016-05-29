package com.lvdi.ruitianxia_cus.activity.order;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.lvdi.ruitianxia_cus.model.order.GetOrderListBean;
import com.lvdi.ruitianxia_cus.model.order.OrderLListItem;
import com.lvdi.ruitianxia_cus.model.order.OrderProduct;
import com.lvdi.ruitianxia_cus.request.order.CancelOrderRequest;
import com.lvdi.ruitianxia_cus.request.order.CompleteOrderRequest;
import com.lvdi.ruitianxia_cus.request.order.GetOrderListRequest;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.lvdi.ruitianxia_cus.util.TimesUtil;
import com.lvdi.ruitianxia_cus.view.dialog.DialogBase;
import com.lvdi.ruitianxia_cus.view.dialog.DialogBase.OnButtonClickListener;
import com.lvdi.ruitianxia_cus.view.pulltorefresh.PullToRefreshBase;
import com.lvdi.ruitianxia_cus.view.pulltorefresh.PullToRefreshBase.OnRefreshListener;
import com.lvdi.ruitianxia_cus.view.pulltorefresh.PullToRefreshListView;

public class OrderListActivity extends LvDiActivity {
	public static final int REQ_TO_REFUND = 102;
	public static final int REQ_TO_PAY = 103;
	/**
	 * 全部
	 */
	public static final int STATUS_ALL = 0;
	/**
	 * 待付款
	 */
	public static final int STATUS_PAY = 1;
	/**
	 * 待发货
	 */
	public static final int STATUS_SEND = 2;
	/**
	 * 待收货
	 */
	public static final int STATUS_RECEIVE = 3;
	/**
	 * 待退款
	 */
	public static final int STATUS_MONEY = 4;
	public static final int REQ_TO_ORDER_DETAIL = 101;
	/** 全部 */
	@AbIocView(click = "btnClick", id = R.id.btn_all)
	Button mBtnAll;
	/** 待付款 */
	@AbIocView(click = "btnClick", id = R.id.btn_pay)
	Button mBtnPay;
	/** 待发货 */
	@AbIocView(click = "btnClick", id = R.id.btn_send)
	Button mBtnSend;
	/** 待收货 */
	@AbIocView(click = "btnClick", id = R.id.btn_receive)
	Button mBtnReveive;
	/** 待退款 */
	@AbIocView(click = "btnClick", id = R.id.btn_back_money)
	Button mBtnMoney;

	/** 当前请求的页标 */
	private int curPageIndex = 0;
	/**
	 * 当前选中的订单状态
	 */
	private String mCurOrderStatus = "";
	/** 订单数据列表 */
	private List<OrderLListItem> mOrderList;
	@AbIocView(id = R.id.listview)
	private PullToRefreshListView mPullListView;
	private ListView mListView;
	private MyAdapter mAdapter;
	private int orderStatus = STATUS_ALL;

	/**
	 * 订单类型 OrderType.SALES_ORDER_O2O_SERVICE ;//预约
	 * OrderType.SALES_ORDER_B2C+","+OrderType.SALES_ORDER_B2C//b2c,o2o订单
	 */
	private String orderType = "";
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(OrderListActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_GET_OREDER_LIST_FAIL:
				mPullListView.onPullDownRefreshComplete();
				mPullListView.onPullUpRefreshComplete();
				if (msg.obj != null)
					AbToastUtil.showToast(getApplicationContext(),
							(String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_GET_OREDER_LIST_SUCC:
				mPullListView.onPullDownRefreshComplete();
				mPullListView.onPullUpRefreshComplete();
				if (msg.obj != null) {
					GetOrderListBean mBean = (GetOrderListBean) msg.obj;
					if ("N".equals(mBean.nextPage)) {
						mPullListView.setHasMoreData(false);
					} else {
						mPullListView.setHasMoreData(true);
					}
					if (curPageIndex == 1) {
						mOrderList.clear();
					}
					if (mBean.orderList != null) {
						mOrderList.addAll(mBean.orderList);
					}
					mAdapter.notifyDataSetChanged();
				}
				break;
			case HandleAction.HttpType.HTTP_CANCEL_ORDER_SUCC:// 取消订单
				mPullListView.doPullRefreshing(true, 200);
				break;
			case HandleAction.HttpType.HTTP_CANCEL_ORDER_FAIL:
				if (msg.obj != null)
					AbToastUtil.showToast(getApplicationContext(),
							(String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_COMPLETE_ORDER_SUCC:// 确认收货
				mPullListView.doPullRefreshing(true, 200);
				break;
			case HandleAction.HttpType.HTTP_COMPLETE_ORDER_FAIL:
				if (msg.obj != null)
					AbToastUtil.showToast(getApplicationContext(),
							(String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_REFUND_ORDER_SUCC:// 退货
				mPullListView.doPullRefreshing(true, 200);
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
		setAbContentView(R.layout.activity_oder_list);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(orderStatusReceiver != null){
			unregisterReceiver(orderStatusReceiver);
		}
	}
	/**
	 *  
	 */
	private BroadcastReceiver orderStatusReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			if(mPullListView != null && !TextUtils.isEmpty(orderType)){
				curPageIndex = 0;
				sendOrderReq();
			}
			 
		}
	};
	@Override
	public void initData() {
		// TODO Auto-generated method stub
		super.initData();
		
		registerReceiver(orderStatusReceiver, new IntentFilter(
				Constant.ACTION_ORDER_STATUS_CHANGE));
		mOrderList = new ArrayList<OrderLListItem>();
		mAdapter = new MyAdapter();
		mPullListView.setPullRefreshEnabled(true);
		mPullListView.setScrollLoadEnabled(true);
		mListView = mPullListView.getRefreshableView();
		mListView.setAdapter(mAdapter);
		mListView.setDividerHeight(15);
		mListView.setDivider(getResources().getDrawable(R.color.transparent));

		if (getIntent().hasExtra("orderStatus")) {
			orderStatus = getIntent().getIntExtra("orderStatus", STATUS_ALL);
			switch (orderStatus) {
			case STATUS_ALL://全部
				mCurOrderStatus = "";
				break;
			case STATUS_MONEY://待退款
				mCurOrderStatus = OrderStatus.ORDER_REFUND_CREATED.toString() + ","
						+ OrderStatus.ORDER_REFUND_PROCESSING.toString();
				break;
			case STATUS_PAY://待付款
				mCurOrderStatus = OrderStatus.ORDER_CREATED.toString();
				break;
			case STATUS_RECEIVE://待收货
				mCurOrderStatus = OrderStatus.ORDER_SENT.toString();
				break;
			case STATUS_SEND://待发货
				mCurOrderStatus = OrderStatus.ORDER_PROCESSING.toString() + ","
						+ OrderStatus.ORDER_APPROVED.toString();
				break;
			default:
				break;
			}
		}
		if (getIntent().hasExtra("orderType")) {
			orderType = getIntent().getStringExtra("orderType");
		}
		if (OrderType.SALES_ORDER_O2O_SERVICE.toString().equals(orderType)) {// 预约列表
			findViewById(R.id.line_top).setVisibility(View.GONE);
			findViewById(R.id.layout_top).setVisibility(View.GONE);
			setAbTitle("我的预约");
		} else {
			setAbTitle("我的订单");
		}
		mPullListView.doPullRefreshing(true, 200);
		refreshBtnStatus();
	}

	@Override
	public void initUi() {
		// TODO Auto-generated method stub
		super.initUi();
	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

			@Override
			public void onPullDownToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				curPageIndex = 0;
				sendOrderReq();
			}

			@Override
			public void onPullUpToRefresh(
					PullToRefreshBase<ListView> refreshView) {
				sendOrderReq();
			}
		});
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				startActivityForResult(new Intent(OrderListActivity.this,
						OrderDetailActivity.class).putExtra("orderId", ""), 101);
			}
		});
	}

	public void btnClick(View view) {
		switch (view.getId()) {
		case R.id.btn_all:// 全部订单
			mCurOrderStatus = "";
			orderStatus = STATUS_ALL;
			mPullListView.doPullRefreshing(true, 200);
			refreshBtnStatus();
			break;
		case R.id.btn_pay:// 待付款
			orderStatus = STATUS_PAY;
			mCurOrderStatus = OrderStatus.ORDER_CREATED.toString();
			mPullListView.doPullRefreshing(true, 200);
			refreshBtnStatus();
			break;
		case R.id.btn_send:// 待发货
			orderStatus = STATUS_SEND;
			mCurOrderStatus = OrderStatus.ORDER_PROCESSING.toString() + ","
					+ OrderStatus.ORDER_APPROVED.toString();
			mPullListView.doPullRefreshing(true, 200);
			refreshBtnStatus();
			break;
		case R.id.btn_receive:// 待收货
			orderStatus = STATUS_RECEIVE;
			mCurOrderStatus = OrderStatus.ORDER_SENT.toString();
			mPullListView.doPullRefreshing(true, 200);
			refreshBtnStatus();
			break;
		case R.id.btn_back_money:// 待退款
			orderStatus = STATUS_MONEY;
			mCurOrderStatus = OrderStatus.ORDER_REFUND_CREATED.toString() + ","
					+ OrderStatus.ORDER_REFUND_PROCESSING.toString();
			mPullListView.doPullRefreshing(true, 200);
			refreshBtnStatus();
			break;
		default:
			break;
		}
	}

	private void refreshBtnStatus() {
		if (orderStatus == STATUS_ALL) {
			mBtnAll.setSelected(true);
		} else {
			mBtnAll.setSelected(false);
		}
		if (orderStatus == STATUS_MONEY) {
			mBtnMoney.setSelected(true);
		} else {
			mBtnMoney.setSelected(false);
		}
		if (orderStatus == STATUS_PAY) {
			mBtnPay.setSelected(true);
		} else {
			mBtnPay.setSelected(false);
		}
		if (orderStatus == STATUS_RECEIVE) {
			mBtnReveive.setSelected(true);
		} else {
			mBtnReveive.setSelected(false);
		}
		if (orderStatus == STATUS_SEND) {
			mBtnSend.setSelected(true);
		} else {
			mBtnSend.setSelected(false);
		}

	}

	private void sendOrderReq() {
		curPageIndex++;
		GetOrderListRequest mRequest = new GetOrderListRequest();
		mRequest.sendRequest(mHandler, curPageIndex + "", mCurOrderStatus,
				orderType);
		// AbDialogUtil.showProgressDialog(
		// this, 0, "获取订单数据中...");
	}

	@Override
	protected void onActivityResult(int arg0, int arg1, Intent arg2) {
		// TODO Auto-generated method stub
		super.onActivityResult(arg0, arg1, arg2);
		if (arg1 == RESULT_OK) {
			switch (arg0) {
			case REQ_TO_REFUND:
				mPullListView.doPullRefreshing(true, 200);
				break;
			case REQ_TO_ORDER_DETAIL:
				mPullListView.doPullRefreshing(true, 200);
				break;
			case REQ_TO_PAY:
				mPullListView.doPullRefreshing(true, 200);
				if(arg2 != null && arg2.hasExtra("orderId")){
					startActivityForResult(new Intent(OrderListActivity.this,
							OrderDetailActivity.class).putExtra("orderId", arg2.getStringExtra("orderId")),
							REQ_TO_ORDER_DETAIL);
				}
				break;
			default:
				break;
			}
		}
	}

	class MyAdapter extends BaseAdapter {

		private LayoutInflater inflater;

		public MyAdapter() {
			inflater = LayoutInflater.from(OrderListActivity.this);
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mOrderList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mOrderList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder;
			if (convertView == null) {
				convertView = inflater
						.inflate(R.layout.item_order_detail, null);

				holder = new ViewHolder();
				holder.mTvOrderNum = (TextView) convertView
						.findViewById(R.id.tv_order_num);
				holder.mTvOrderStatus = (TextView) convertView
						.findViewById(R.id.tv_order_status);
				holder.mTvTotalPrice = (TextView) convertView
						.findViewById(R.id.tv_total_price);
				holder.mBtnGray = (Button) convertView
						.findViewById(R.id.btn_gray);
				holder.mBtnOrange = (Button) convertView
						.findViewById(R.id.btn_orange);
				holder.mBtnGray2 = (Button) convertView
						.findViewById(R.id.btn_gray2);
				holder.mLayoutProducts = (LinearLayout) convertView
						.findViewById(R.id.layout_products);
				convertView.setTag(holder);

			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			holder.mLayoutProducts.removeAllViews();
			final OrderLListItem item = mOrderList.get(position);
			if (item.orderProduct != null) {// 添加产品列表
				View v;
				for (OrderProduct product : item.orderProduct) {
					v = inflater.inflate(R.layout.item_order_detail_product,
							null);

					ImageView mIv = (ImageView) v.findViewById(R.id.iv_pic);
					TextView mTvProductName = (TextView) v
							.findViewById(R.id.tv_product_name);
					TextView mTvAvgPrice = (TextView) v
							.findViewById(R.id.tv_product_avg_price);
					TextView mTvNum = (TextView) v
							.findViewById(R.id.tv_product_num);

					mTvProductName.setText(formatStr(product.productName));
					mTvAvgPrice.setText(getString(R.string.app_rmb)
							+ formatStr(product.unitPrice));
					mTvNum.setText("x" + product.quantity);
					if (OrderType.SALES_ORDER_O2O_SERVICE.toString().equals(
							orderType)) {// 预约列表
						if (TextUtils.isEmpty(product.unitPrice)) {

						}
					} else {

					}
					ImageLoaderHelper.displayImage(Config.HttpURLPrefix3
							+ product.smallImageUrl, mIv);

					holder.mLayoutProducts.addView(v);
				}
			}
			if (item.orderHeader != null) {
				String orderTypeId = item.orderHeader.orderTypeId;
				holder.mTvOrderNum.setText(item.orderHeader.orderId);
				OrderStatus os = OrderStatus
						.getByKey(item.orderHeader.orderStatus);
				if (OrderType.SALES_ORDER_O2O_SERVICE.toString().equals(
						orderTypeId)) {// 预约列表
					holder.mTvOrderStatus.setText(os.getAppointlabel());
					holder.mTvTotalPrice
							.setText("预约时间："
									+ TimesUtil
											.formatTime3(item.orderHeader.reservationDate));
					holder.mBtnGray.setVisibility(View.GONE);
					holder.mBtnGray2.setVisibility(View.GONE);
					holder.mBtnOrange.setVisibility(View.GONE);
					if (os == OrderStatus.ORDER_CREATED || os == OrderStatus.ORDER_APPROVED) {// 订单初始状态,待付款
						holder.mBtnOrange.setVisibility(View.VISIBLE);
						holder.mBtnOrange.setText("取消预约");
						holder.mBtnOrange
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										showCancelOrderDlg(
												item.orderHeader.orderId,
												"确定要取消预约？");
									}
								});
					}

				} else {// b2c、o2o订单处理开始

					holder.mTvOrderStatus.setText(os.getLable());
					holder.mTvTotalPrice.setText("总价：￥"
							+ item.orderHeader.grandTotal);
					if (os == OrderStatus.ORDER_CREATED) {// 订单初始状态,待付款
						holder.mBtnGray.setVisibility(View.VISIBLE);
						holder.mBtnGray2.setVisibility(View.GONE);
						holder.mBtnOrange.setVisibility(View.VISIBLE);
						holder.mBtnGray.setText("取消订单");
						holder.mBtnOrange.setText("付款");
						holder.mBtnGray
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										showCancelOrderDlg(
												item.orderHeader.orderId,
												"确定要取消订单？");
									}
								});
						holder.mBtnOrange
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View v) {
										startActivityForResult(new Intent(
												OrderListActivity.this,
												PayMethodActivity.class)
												.putExtra(
														"orderId",
														item.orderHeader.orderId)
												.putExtra(
														"totalPrice",
														item.orderHeader.grandTotal),REQ_TO_PAY);
									}
								});

					} else if (os == OrderStatus.ORDER_PROCESSING) {// 待发货
						holder.mBtnGray.setVisibility(View.GONE);
						holder.mBtnGray2.setVisibility(View.GONE);
						holder.mBtnOrange.setVisibility(View.VISIBLE);
						holder.mBtnOrange.setText("退货/退款");
						holder.mBtnOrange
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										toRefundOrder(item.orderHeader.orderId);
									}
								});
					} else if (os == OrderStatus.ORDER_SENT) {// 待收货
						if (OrderType.SALES_ORDER_B2C.toString().equals(
								orderTypeId)){
							holder.mBtnGray.setVisibility(View.VISIBLE);
						}else{
							holder.mBtnGray.setVisibility(View.GONE);
						}
						holder.mBtnGray2.setVisibility(View.VISIBLE);
						holder.mBtnOrange.setVisibility(View.VISIBLE);
						holder.mBtnGray.setText("查看物流");
						holder.mBtnGray2.setText("退款/退货");
						holder.mBtnOrange.setText("确认收货");
						holder.mBtnGray
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										toOrderDetail(item.orderHeader.orderId);
									}
								});
						holder.mBtnGray2
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										toRefundOrder(item.orderHeader.orderId);
									}
								});
						holder.mBtnOrange
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub

										toConfirmOrder(item.orderHeader.orderId);
									}
								});

					} else if (os == OrderStatus.ORDER_COMPLETED) {// 订单完成
						holder.mBtnGray.setVisibility(View.GONE);
						holder.mBtnGray2.setVisibility(View.GONE);
						if (OrderType.SALES_ORDER_B2C.toString().equals(
								orderTypeId)) {
							holder.mBtnOrange.setVisibility(View.VISIBLE);
							holder.mBtnOrange.setText("查看物流");
							holder.mBtnOrange
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View arg0) {
											// TODO Auto-generated method stub
											toOrderDetail(item.orderHeader.orderId);
										}
									});
						}else{
							holder.mBtnOrange.setVisibility(View.GONE);
						}
						
					} else if (os == OrderStatus.ORDER_APPROVED) {// 待接单
						holder.mBtnGray.setVisibility(View.GONE);
						holder.mBtnGray2.setVisibility(View.GONE);
						holder.mBtnOrange.setVisibility(View.VISIBLE);
						holder.mBtnOrange.setText("退款/退货");
						holder.mBtnOrange
								.setOnClickListener(new OnClickListener() {

									@Override
									public void onClick(View arg0) {
										// TODO Auto-generated method stub
										toRefundOrder(item.orderHeader.orderId);
									}
								});
					} else {
						holder.mBtnGray.setVisibility(View.GONE);
						holder.mBtnGray2.setVisibility(View.GONE);
						holder.mBtnOrange.setVisibility(View.GONE);
					}

				}// b2c、o2o订单处理结束
				convertView.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						// TODO Auto-generated method stub
						toOrderDetail(item.orderHeader.orderId);
					}
				});
			}

			return convertView;
		}

		/**
		 * 确认收货
		 * 
		 * @param orderId
		 */
		private void toConfirmOrder(final String orderId) {
			new DialogBase(OrderListActivity.this).defSetCancelBtn("取消", null)
					.defSetConfirmBtn("确定", new OnButtonClickListener() {
						@Override
						public void onClick(DialogBase dialog) {
							CompleteOrderRequest req = new CompleteOrderRequest();
							req.sendRequest(mHandler, orderId);
							AbDialogUtil.showProgressDialog(
									OrderListActivity.this, 0, "请稍等...");
						}
					}).defSetContentTxt("确认收货？").show();
		}

		/**
		 * 查看物流/查看订单详情
		 * 
		 * @param orderId
		 */
		private void toOrderDetail(final String orderId) {
			startActivityForResult(new Intent(OrderListActivity.this,
					OrderDetailActivity.class).putExtra("orderId", orderId),
					REQ_TO_ORDER_DETAIL);
		}

		/**
		 * 退款
		 * 
		 * @param orderId
		 */
		private void toRefundOrder(final String orderId) {
			startActivityForResult(new Intent(OrderListActivity.this,
					RefundOrderActivity.class).putExtra("orderId", orderId),
					REQ_TO_REFUND);
		}

		/**
		 * 取消订单
		 * 
		 * @param orderId
		 */
		private void showCancelOrderDlg(final String orderId, String title) {
			new DialogBase(OrderListActivity.this).defSetCancelBtn("取消", null)
					.defSetConfirmBtn("确定", new OnButtonClickListener() {
						@Override
						public void onClick(DialogBase dialog) {
							CancelOrderRequest req = new CancelOrderRequest();
							req.sendRequest(mHandler, orderId);
							AbDialogUtil.showProgressDialog(
									OrderListActivity.this, 0, "取消订单中...");
						}
					}).defSetContentTxt(title).defSetTitleTxt("温馨提示").show();
		}

		class ViewHolder {
			/** 订单编号 */
			TextView mTvOrderNum;
			/** 订单状态 */
			TextView mTvOrderStatus;
			/** 订单总价 */
			TextView mTvTotalPrice;
			/** 灰色字体按钮 */
			Button mBtnGray;
			/** 灰色字体按钮2 */
			Button mBtnGray2;

			/** 橘黄色字体按钮 */
			Button mBtnOrange;
			LinearLayout mLayoutProducts;
		}

	}
}
