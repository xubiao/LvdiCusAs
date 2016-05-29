//package com.lvdi.ruitianxia_cus.activity;
//
//import java.util.ArrayList;
//import java.util.List;
//import android.annotation.SuppressLint;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.format.Time;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.LayoutInflater;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.animation.Animation;
//import android.view.animation.TranslateAnimation;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import com.ab.util.AbDialogUtil;
//import com.ab.util.AbJsonUtil;
//import com.ab.util.AbLogUtil;
//import com.ab.util.AbToastUtil;
//import com.ab.util.AbWifiUtil;
//import com.ab.view.ioc.AbIocView;
//import com.ab.view.pullview.AbPullToRefreshView;
//import com.ab.view.titlebar.AbTitleBar;
//import com.lvdi.ruitianxia_cus.R;
//import com.lvdi.ruitianxia_cus.adapter.MainServiceAdapter;
//import com.lvdi.ruitianxia_cus.global.Cache;
//import com.lvdi.ruitianxia_cus.global.HandleAction;
//import com.lvdi.ruitianxia_cus.model.ApplicationEntity;
//import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
//import com.lvdi.ruitianxia_cus.model.CustomerC;
//import com.lvdi.ruitianxia_cus.model.CustomerC.CustomerCLayout;
//import com.lvdi.ruitianxia_cus.request.GetApplicationsInConfigRequest;
//import com.lvdi.ruitianxia_cus.request.UpdataCustomerCLayoutRequest;
//import com.lvdi.ruitianxia_cus.util.ApplicationController;
//import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
//import com.lvdi.ruitianxia_cus.view.AppScrollView;
//import com.lvdi.ruitianxia_cus.view.AppScrollView2;
//import com.lvdi.ruitianxia_cus.view.AppScrollView2.ScrollMoveCallback;
//import com.lvdi.ruitianxia_cus.view.ScrollViewCustom;
//
///**
// * 服务定制 类的详细描述：
// * 
// * @author XuBiao
// * @version 1.0.1
// * @time 2015年11月20日 下午12:51:24
// */
//public class ServiceCustomActivity extends LvDiActivity implements
//		OnScrollListener, ScrollMoveCallback {
//
//	private AbTitleBar mAbTitleBar = null;
//
//	@AbIocView(itemClick = "itemClick", id = R.id.serviceLv)
//	ListView mListView;// 主页列表
//	@AbIocView(id = R.id.refreshView)
//	AbPullToRefreshView mAbPullToRefreshView;// 当前界面的布局
//	@AbIocView(id = R.id.srollLayout)
//	AppScrollView2 mSrollLayout;
//	@AbIocView(id = R.id.contetRl)
//	RelativeLayout contentRl;
//	@AbIocView(id = R.id.configTableLl)
//	LinearLayout mTabelLL;
//	@AbIocView(id = R.id.bottomRl)
//	RelativeLayout mBottomRl;
//	@AbIocView(id = R.id.leftIv)
//	ImageView mLeftIv;
//	@AbIocView(id = R.id.rightIv)
//	ImageView mRightIv;
//	@AbIocView(id = R.id.horizontalScrollView1)
//	ScrollViewCustom mHorizontalScrollView;
//	@AbIocView(id = R.id.scrollRl)
//	RelativeLayout mScrollRl;// 刷新提示
//	private CustomerC mCustomerC;// C端首页获取首页模板
//	private List<Application> mApplications;
//	private MainServiceAdapter mAdapter;// 列表适配器
//	private Button saveBt;
//	private TranslateAnimation mShowAction, mHiddenAction;
//
//	private Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			AbDialogUtil.removeDialog(ServiceCustomActivity.this);
//			switch (msg.what) {
//			case HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_CONFIG_SUCC:
//				ApplicationEntity applicationsInConfigData = (ApplicationEntity) msg.obj;
//				mApplications = applicationsInConfigData.applications;
//				updateConfigData();
//				break;
//			case HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_CONFIG_FAIL:
//				AbToastUtil
//						.showToast(getApplicationContext(), (String) msg.obj);
//				break;
//			case HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_C_LAYOUT_SUCC:
//				Intent intent = new Intent(ServiceCustomActivity.this,
//						MainActivity.class);
//				startActivity(intent);
//				break;
//			case HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_C_LAYOUT_FAIL:
//				AbToastUtil
//						.showToast(getApplicationContext(), (String) msg.obj);
//				break;
//			default:
//				break;
//			}
//		}
//	};
//
//	@Override
//	protected void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		setAbContentView(R.layout.activity_service_custom);
//		mAbTitleBar = this.getTitleBar();
//		mAbTitleBar.setTitleText("服务定制");
//		mAbTitleBar.setLogo(R.drawable.button_selector_back);
//		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg_black);
//		initView();
//		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
//		loadData();
//	}
//
//	public void btnClick(View v) {
//		switch (v.getId()) {
//		default:
//			break;
//		}
//	}
//
//	private void initView() {
//		mAbTitleBar.clearRightView();
//		View rightViewMore = mInflater.inflate(R.layout.right_menu_btn, null);
//		mAbTitleBar.addRightView(rightViewMore);
//		saveBt = (Button) rightViewMore.findViewById(R.id.menuBtn);
//		saveBt.setBackgroundDrawable(null);
//		saveBt.setText("确认");
//		saveBt.setVisibility(View.INVISIBLE);
//		saveBt.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//
//				if (AbWifiUtil.isConnectivity(ServiceCustomActivity.this)) {
//					CustomerCLayout customerCLayout = new CustomerCLayout();
//					customerCLayout.layout = (ArrayList<Application>) mSrollLayout
//							.getLayous();
//					String layoutString = AbJsonUtil
//							.toJson(customerCLayout.layout);
//					layoutString = "{\"layout\":" + layoutString + "}";
//					UpdataCustomerCLayoutRequest.getInstance().sendRequest(
//							mHandler, Cache.getAccountInfo().partyId,
//							mCustomerC.selectedProject.organizationId + "",
//							layoutString);// 通过
//					AbDialogUtil.showProgressDialog(ServiceCustomActivity.this,
//							0, "保存修改...");
//					AbLogUtil.e("", layoutString);
//
//				} else {
//					AbToastUtil.showToast(ServiceCustomActivity.this,
//							R.string.please_check_network);
//
//				}
//
//			}
//		});
//		initAnimation();
//		mSrollLayout.setViewMode(AppScrollView.CUSTOM_MODE);
//		mBottomRl.setVisibility(View.GONE);
//		mHorizontalScrollView.setOnTouchListener(new OnTouchListener() {
//			public boolean onTouch(View v, MotionEvent event) {
//				if (event.getAction() == MotionEvent.ACTION_UP) {
//					mHorizontalScrollView.startScrollerTask();
//				}
//				return false;
//			}
//		});
//		mAbTitleBar.getLogoView().setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				onBack();
//			}
//		});
//		mListView.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return true;
//			}
//		});
//
//	}
//
//	/**
//	 * 
//	 * 
//	 * @author Xubiao
//	 */
//	private void loadData() {
//		mCustomerC = (CustomerC) getIntent().getSerializableExtra(
//				CustomerC.class.getSimpleName());
//		if (AbWifiUtil.isConnectivity(this)) {
//			GetApplicationsInConfigRequest.getInstance().sendRequest(mHandler,
//					mCustomerC.selectedProject.organizationId + "", "-1", "-1");// 通过
//			AbDialogUtil.showProgressDialog(ServiceCustomActivity.this, 0,
//					"获取数据中..."); // 从1开始
//		} else {
//			AbToastUtil.showToast(this, R.string.please_check_network);
//		}
//	}
//
//	private boolean setSelete = true;
//
//	private void getDefaultSelect() {
//		Time t = new Time();
//		t.setToNow(); // 取得系统时间。
//		int hour = t.hour; // 0-23
//		int minute = t.minute;
//		if (minute > 30) {
//			hour++;
//		}
//		if (hour == 24) {
//			hour = 0;
//		}
//
//		if (hour < 7) {
//			hour = 7;
//		}
//		if (hour > 21) {// 10 11 点
//			hour = 21;
//		}
//
//		AbLogUtil.d(getClass(), "now time: hour-" + hour);
//
//		ArrayList<Application> applications = mCustomerC.layouts;
//		int size = applications.size();
//		for (int i = 0; i < size; i++) {
//			String timeNode = applications.get(i).timeNode;
//			String hourNode = timeNode.split(":")[0];
//			if (timeNode.indexOf("pm") != -1) {
//				try {
//					hourNode = Integer.parseInt(hourNode) + 12 + "";
//				} catch (Exception e) {
//				}
//			}
//			if (hourNode.equals(hour + "")) {
//				setSelete = true;
//				int distance = i - 2;
//				if (distance > 0) {
//					// 左移
//					for (int removeIdex = 0; removeIdex < distance; removeIdex++) {
//						Application application = applications.remove(0);
//						applications.add(application);
//					}
//				} else if (distance < 0) {
//					// 右移
//					for (int removeIdex = 0; removeIdex < -distance; removeIdex++) {
//						Application application = applications
//								.remove(applications.size() - 1);
//						applications.add(0, application);
//					}
//				}
//
//				break;
//			}
//		}
//	}
//
//	private void updateConfigData() {
//		if (null != mApplications) {
//			mTabelLL.removeAllViews();
//			int size = mApplications.size();
//			for (int i = 0; i < size; i++) {
//				final Application application = mApplications.get(i);
//				View view = LayoutInflater.from(this).inflate(
//						R.layout.item_service_config_tab, null);
//				((TextView) view.findViewById(R.id.nameTv))
//						.setText(application.appName);
//
//				ImageLoaderHelper.displayImage(application.appIcon
//						+ "_android_" + (i % 5 + 1) + ".png",
//						((ImageView) view.findViewById(R.id.iconIv)),
//						R.drawable.app_default_bg);
//
//				mTabelLL.addView(view);
//				view.setOnClickListener(new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						if (checkExist(application)) {
//							AbToastUtil.showToast(ServiceCustomActivity.this,
//									"该应用已经存在,请选择其他应用");
//						} else {
//							ArrayList<Application> mLayous = (ArrayList<Application>) mSrollLayout
//									.getLayous();
//							int selectIndex = mSrollLayout.getSelectIndex();
//							Application selectLayout = mLayous.get(selectIndex
//									% mLayous.size());
//							selectLayout.update(application);
//							mSrollLayout.updataSelect();
//							mBottomRl.startAnimation(mHiddenAction);
//							mBottomRl.setVisibility(View.GONE);
//							saveBt.setVisibility(View.VISIBLE);
//						}
//					}
//				});
//			}
//
//			getDefaultSelect();
//			mSrollLayout.setAppList(mCustomerC.layouts);
//			mAdapter = new MainServiceAdapter(getApplicationContext());
//			mAdapter.setData(mCustomerC.layouts);
//			mListView.setAdapter(mAdapter);
//			mListView.setOnScrollListener(this);
//			mAdapter.setMiddleItem(mCustomerC.layouts.size() + 2);
//			mSrollLayout.setVisibility(View.VISIBLE);
//			mSrollLayout.setListener(ServiceCustomActivity.this);
//		}
//
//	}
//
//	private boolean checkExist(Application application) {
//		ArrayList<Application> mLayous = (ArrayList<Application>) mSrollLayout
//				.getLayous();
//		int size = mLayous.size();
//		for (int i = 0; i < size; i++) {
//			if (mLayous.get(i).id == application.id) {
//				return true;
//			}
//		}
//		return false;
//	}
//
//	private void initAnimation() {
//		mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//				1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//		mShowAction.setDuration(500);
//
//		mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
//				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
//				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//				1.0f);
//		mHiddenAction.setDuration(500);
//	}
//
//	private int visibleItemCount;
//
//	@SuppressLint("NewApi")
//	@Override
//	public void onScroll(AbsListView view, int firstVisibleItem,
//			int visibleItemCount, int totalItemCount) {
//		// TODO Auto-generated method stub
//		if (0 != visibleItemCount) {
//			this.visibleItemCount = visibleItemCount;
//			if (setSelete) {
//				setSelete = false;
//				mListView.smoothScrollToPositionFromTop(
//						mCustomerC.layouts.size() + 2 - visibleItemCount / 2
//								+ 1, 0, 300);
//				mAdapter.setMiddleItem(mCustomerC.layouts.size() + 2);
//			}
//		}
//		/** 到顶部添加数据 */
//		if (firstVisibleItem <= 2) {
//			mListView.setSelection(mCustomerC.layouts.size() + 2);
//		} else if (firstVisibleItem + visibleItemCount > mAdapter.getCount() - 2) {// 到底部添加数据
//			mListView
//					.setSelection(firstVisibleItem - mCustomerC.layouts.size());
//		}
//
//	}
//
//	@Override
//	public void onScrollStateChanged(AbsListView arg0, int arg1) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		// setBackgroundByTime();
//	}
//
//	/**
//	 * 通过判断白天和晚上设置背景图
//	 * 
//	 * @author Xubiao
//	 */
//	private void setBackgroundByTime() {
//		Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
//		t.setToNow(); // 取得系统时间。
//		int hour = t.hour; // 0-23
//		AbLogUtil.d(getClass(), "now time: hour-" + hour);
//		// 以下午五点为分割点
//		if (hour >= 17) {
//			contentRl.setBackgroundResource(R.drawable.main_light_bg);
//		} else {
//			contentRl.setBackgroundResource(R.drawable.main_day_bg);
//		}
//	}
//
//	@Override
//	public void removeIconClick(Application layout) {
//		// TODO Auto-generated method stub
//		saveBt.setVisibility(View.VISIBLE);
//	}
//
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
//			onBack();
//			return true;
//		}
//		return super.onKeyDown(keyCode, event);
//	}
//
//	private void onBack() {
//		if (saveBt.getVisibility() != View.VISIBLE) {
//			finish();
//			return;
//		}
//		final View mView = mInflater
//				.inflate(R.layout.dialog_confirm_view, null);
//		AbDialogUtil.showAlertDialog(mView);
//		((TextView) mView.findViewById(R.id.messageTv))
//				.setText("您当前的操作未保存，确认返回到首页？");
//		((Button) mView.findViewById(R.id.okBt)).setText("确认");
//		OnClickListener dialogClickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				AbDialogUtil.removeDialog(mView);
//				if (v.getId() == R.id.cancelBt) {
//				} else if (v.getId() == R.id.okBt) {
//					finish();
//				}
//
//			}
//		};
//		mView.findViewById(R.id.cancelBt).setOnClickListener(
//				dialogClickListener);
//		mView.findViewById(R.id.okBt).setOnClickListener(dialogClickListener);
//	}
//
//	@Override
//	public void onSelected(int position) {
//		// TODO Auto-generated method stub
//		if (visibleItemCount > 0) {
//			mAdapter.setMiddleItem(position + mCustomerC.layouts.size());
//			mListView.setSelection(position + mCustomerC.layouts.size()
//					- visibleItemCount / 2 + 1);
//		}
//	}
//
//	@Override
//	public void onClicked(int position) {
//		// TODO Auto-generated method stub
//		if (mCustomerC.layouts.get(position).id == 0) {
//			mBottomRl.startAnimation(mShowAction);
//			mBottomRl.setVisibility(View.VISIBLE);
//		} else {
//			ApplicationController.responseController(this,
//					mCustomerC.layouts.get(position),
//					MainActivity2.class.getSimpleName());
//
//		}
//	}
//
//	@Override
//	public void onLongClicked(int position) {
//		// TODO Auto-generated method stub
//
//	}
//}

package com.lvdi.ruitianxia_cus.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbViewUtil;
import com.ab.util.AbWifiUtil;
import com.ab.util.AndroidVersionCheckUtils;
import com.ab.view.ioc.AbIocView;
import com.ab.view.pullview.AbPullToRefreshView;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.adapter.MainServiceAdapter;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
import com.lvdi.ruitianxia_cus.model.CustomerC;
import com.lvdi.ruitianxia_cus.model.CustomerC.CustomerCLayout;
import com.lvdi.ruitianxia_cus.request.GetApplicationsInConfigRequest;
import com.lvdi.ruitianxia_cus.request.UpdataCustomerCLayoutRequest;
import com.lvdi.ruitianxia_cus.util.ApplicationController;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.lvdi.ruitianxia_cus.view.AppScrollView;
import com.lvdi.ruitianxia_cus.view.AppScrollView.ScrollMoveCallback;
import com.lvdi.ruitianxia_cus.view.ScrollViewCustom;

/**
 * 服务定制 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月20日 下午12:51:24
 */
public class ServiceCustomActivity extends LvDiActivity implements
		OnScrollListener, ScrollMoveCallback {

	@AbIocView(itemClick = "itemClick", id = R.id.serviceLv)
	ListView mListView;// 主页列表
	@AbIocView(id = R.id.refreshView)
	AbPullToRefreshView mAbPullToRefreshView;// 当前界面的布局
	@AbIocView(id = R.id.srollLayout)
	AppScrollView mSrollLayout;
	@AbIocView(id = R.id.contetRl)
	RelativeLayout contentRl;
	@AbIocView(id = R.id.configTableLl)
	LinearLayout mTabelLL;
	@AbIocView(id = R.id.bottomRl)
	RelativeLayout mBottomRl;
	@AbIocView(id = R.id.horizontalScrollView1)
	ScrollViewCustom mHorizontalScrollView;
	@AbIocView(id = R.id.scrollRl)
	RelativeLayout mScrollRl;// 刷新提示
	private CustomerC mCustomerC;// C端首页获取首页模板
	private List<Application> mApplications;
	private MainServiceAdapter mAdapter;// 列表适配器
	@AbIocView(id = R.id.title_bar_rl)
	RelativeLayout mTitleBar;//
	@AbIocView(id = R.id.leftIv)
	TextView mLeftIv;//
	@AbIocView(id = R.id.rightIv)
	TextView mRightIv;//
	@AbIocView(id = R.id.midTv)
	TextView mMidTv;//

	@AbIocView(id = R.id.leftRl)
	RelativeLayout mLeftRl;//
	@AbIocView(id = R.id.rightRl)
	RelativeLayout mRightRl;//
	@AbIocView(id = R.id.midRl)
	RelativeLayout mMidRl;//
	private TranslateAnimation mShowAction, mHiddenAction;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(ServiceCustomActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_CONFIG_SUCC:
				ApplicationEntity applicationsInConfigData = (ApplicationEntity) msg.obj;
				mApplications = applicationsInConfigData.applications;
				updateConfigData();
				break;
			case HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_CONFIG_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_C_LAYOUT_SUCC:
				Intent intent = new Intent(ServiceCustomActivity.this,
						MainActivity.class);
				startActivity(intent);
				break;
			case HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_C_LAYOUT_FAIL:
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
		setAbContentView(R.layout.activity_service_custom);
		mMidTv.setText("服务定制");
		mMidTv.setCompoundDrawables(null, null, null, null);
		mLeftIv.setBackgroundResource(R.drawable.button_selector_back);
		initView();
		loadData();
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		default:
			break;
		}
	}

	private void initView() {
		LayoutParams titleBarLp = (LayoutParams) mTitleBar.getLayoutParams();
		if (AndroidVersionCheckUtils.hasKitKat()) {
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(
					WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
			titleBarLp.height = (int) AbViewUtil.dip2px(this, 60f);
			titleBarLp.topMargin = (int) AbViewUtil.dip2px(this, 15f);
		} else {
			titleBarLp.height = (int) AbViewUtil.dip2px(this, 45f);
		}
		mTitleBar.setLayoutParams(titleBarLp);

		mRightIv.setBackgroundDrawable(null);
		mRightIv.setText(" 确认 ");
		mRightRl.setVisibility(View.INVISIBLE);
		mRightRl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				if (AbWifiUtil.isConnectivity(ServiceCustomActivity.this)) {
					CustomerCLayout customerCLayout = new CustomerCLayout();
					customerCLayout.layout = mSrollLayout.getLayous();
					String layoutString = AbJsonUtil
							.toJson(customerCLayout.layout);
					layoutString = "{\"layout\":" + layoutString + "}";
					UpdataCustomerCLayoutRequest.getInstance().sendRequest(
							mHandler, Cache.getAccountInfo().partyId,
							mCustomerC.selectedProject.organizationId + "",
							layoutString);// 通过
					AbDialogUtil.showProgressDialog(ServiceCustomActivity.this,
							0, "保存修改...");
					AbLogUtil.e("", layoutString);

				} else {
					AbToastUtil.showToast(ServiceCustomActivity.this,
							R.string.please_check_network);

				}

			}
		});
		initAnimation();
		mSrollLayout.setViewMode(AppScrollView.CUSTOM_MODE);
		mSrollLayout.setVisibility(View.GONE);
		mBottomRl.setVisibility(View.GONE);
		mAdapter = new MainServiceAdapter(getApplicationContext());
		mListView.setAdapter(mAdapter);
		mHorizontalScrollView.setOnTouchListener(new OnTouchListener() {
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_UP) {
					mHorizontalScrollView.startScrollerTask();
				}
				return false;
			}
		});
		mLeftRl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBack();
			}
		});
	}

	/**
	 * 
	 * 
	 * @author Xubiao
	 */
	private void loadData() {
		mCustomerC = (CustomerC) getIntent().getSerializableExtra(
				CustomerC.class.getSimpleName());
		if (AbWifiUtil.isConnectivity(this)) {
			GetApplicationsInConfigRequest.getInstance().sendRequest(mHandler,
					mCustomerC.selectedProject.organizationId + "", "-1", "-1");// 通过
			AbDialogUtil.showProgressDialog(ServiceCustomActivity.this, 0,
					"获取数据中..."); // 从1开始
		} else {
			AbToastUtil.showToast(this, R.string.please_check_network);
		}
	}

	private boolean setSelete = true;

	private void getDefaultSelect() {
		Time t = new Time();
		t.setToNow(); // 取得系统时间。
		int hour = t.hour; // 0-23
		int minute = t.minute;
		if (minute > 30) {
			hour++;
		}
		if (hour == 24) {
			hour = 0;
		}

		if (hour < 7) {
			hour = 7;
		}
		if (hour > 21) {// 10 11 点
			hour = 21;
		}

		AbLogUtil.d(getClass(), "now time: hour-" + hour);

		ArrayList<Application> applications = mCustomerC.layouts;
		int size = applications.size();
		for (int i = 0; i < size; i++) {
			String timeNode = applications.get(i).timeNode;
			String hourNode = timeNode.split(":")[0];
			if (timeNode.indexOf("pm") != -1) {
				try {
					hourNode = Integer.parseInt(hourNode) + 12 + "";
				} catch (Exception e) {
				}
			}
			if (hourNode.equals(hour + "")) {
				setSelete = true;
				break;
			}
		}
	}

	private void updateConfigData() {
		if (null != mApplications) {
			mTabelLL.removeAllViews();
			int size = mApplications.size();
			for (int i = 0; i < size; i++) {
				final Application application = mApplications.get(i);
				View view = LayoutInflater.from(this).inflate(
						R.layout.item_service_config_tab, null);
				((TextView) view.findViewById(R.id.nameTv))
						.setText(application.appName);

				ImageLoaderHelper.displayImage(application.appIcon
						+ "_android_" + (i % 5 + 1) + ".png",
						((ImageView) view.findViewById(R.id.iconIv)),
						R.drawable.app_default_bg);

				mTabelLL.addView(view);
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (checkExist(application)) {
							AbToastUtil.showToast(ServiceCustomActivity.this,
									"该应用已经存在,请选择其他应用");
						} else {
							ArrayList<Application> mLayous = mSrollLayout
									.getLayous();
							int selectIndex = mSrollLayout.getSelectIndex();
							Application selectLayout = mLayous.get(selectIndex
									% mLayous.size());
							selectLayout.update(application);
							mSrollLayout.updataCard(selectIndex);
							mSrollLayout.updateCardData(selectIndex);
							mBottomRl.startAnimation(mHiddenAction);
							mBottomRl.setVisibility(View.GONE);
							mRightRl.setVisibility(View.VISIBLE);
						}
					}
				});
			}

			mSrollLayout.setListener(ServiceCustomActivity.this);
			getDefaultSelect();
			mSrollLayout.setAppList(mCustomerC.layouts);
			mAdapter = new MainServiceAdapter(getApplicationContext());
			mAdapter.setData(mCustomerC.layouts);
			mListView.setAdapter(mAdapter);
			mListView.setOnScrollListener(this);
			mAdapter.setMiddleItem(mCustomerC.layouts.size() + 2);
			mSrollLayout.setVisibility(View.VISIBLE);
			mSrollLayout.setAnimation(mShowAction);
		}

	}

	private boolean checkExist(Application application) {
		ArrayList<Application> mLayous = mSrollLayout.getLayous();
		int size = mLayous.size();
		for (int i = 0; i < size; i++) {
			if (mLayous.get(i).id == application.id) {
				return true;
			}
		}
		return false;
	}

	private void initAnimation() {
		mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
		mShowAction.setDuration(500);

		mHiddenAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF,
				0.0f, Animation.RELATIVE_TO_SELF, 0.0f,
				Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
				1.0f);
		mHiddenAction.setDuration(500);
	}

	private int visibleItemCount;

	@SuppressLint("NewApi")
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub
		if (0 != visibleItemCount) {
			this.visibleItemCount = visibleItemCount;
			if (setSelete) {
				setSelete = false;
				// TODO Auto-generated method stub
				mListView.smoothScrollToPositionFromTop(
						mCustomerC.layouts.size() + 2 - visibleItemCount / 2
								+ 1, 0, 1000);
				mAdapter.setMiddleItem(mCustomerC.layouts.size() + 2);
			}
		}
		/** 到顶部添加数据 */
		if (firstVisibleItem <= 2) {
			mListView.setSelection(mCustomerC.layouts.size() + 2);
		} else if (firstVisibleItem + visibleItemCount > mAdapter.getCount() - 2) {// 到底部添加数据
			mListView
					.setSelection(firstVisibleItem - mCustomerC.layouts.size());
		}
		AbLogUtil.e(getClass(), "onScroll");

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onResume() {
		super.onResume();
		// setBackgroundByTime();
	}

	/**
	 * 通过判断白天和晚上设置背景图
	 * 
	 * @author Xubiao
	 */
	private void setBackgroundByTime() {
		Time t = new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。
		t.setToNow(); // 取得系统时间。
		int hour = t.hour; // 0-23
		AbLogUtil.d(getClass(), "now time: hour-" + hour);
		// 以下午五点为分割点
		if (hour >= 17) {
			contentRl.setBackgroundResource(R.drawable.main_light_bg);
		} else {
			contentRl.setBackgroundResource(R.drawable.main_day_bg);
		}
	}

	@Override
	public void moveY(int dy, int dx) {
		// TODO Auto-generated method stub
		mListView.smoothScrollBy(dy, dx);
	}

	@Override
	public void cardClick(Application layout) {
		// TODO Auto-generated method stub
		ApplicationController.responseController(this, layout,
				ServiceCustomActivity.class.getSimpleName());
	}

	@Override
	public void cardLongClick(Application layout) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addIconClick(Application layout) {
		// TODO Auto-generated method stub
		mBottomRl.startAnimation(mShowAction);
		mBottomRl.setVisibility(View.VISIBLE);
	}

	@Override
	public void removeIconClick(Application layout) {
		// TODO Auto-generated method stub
		mRightRl.setVisibility(View.VISIBLE);
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
		if (mRightRl.getVisibility() != View.VISIBLE) {
			finish();
			return;
		}
		final View mView = mInflater
				.inflate(R.layout.dialog_confirm_view, null);
		AbDialogUtil.showAlertDialog(mView);
		((TextView) mView.findViewById(R.id.messageTv))
				.setText("您当前的操作未保存，确认返回到首页？");
		((Button) mView.findViewById(R.id.okBt)).setText("确认");
		OnClickListener dialogClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.cancelBt) {
				} else if (v.getId() == R.id.okBt) {
					finish();
				}
				AbDialogUtil.removeDialog(mView);
			}
		};
		mView.findViewById(R.id.cancelBt).setOnClickListener(
				dialogClickListener);
		mView.findViewById(R.id.okBt).setOnClickListener(dialogClickListener);
	}

	@Override
	public void select(int position) {
		// TODO Auto-generated method stub
		if (visibleItemCount > 0) {
			mAdapter.setMiddleItem(position % mCustomerC.layouts.size()
					+ mCustomerC.layouts.size());
			mListView.setSelection(position % mCustomerC.layouts.size()
					+ mCustomerC.layouts.size() - visibleItemCount / 2 + 1);
		}
	}
}
