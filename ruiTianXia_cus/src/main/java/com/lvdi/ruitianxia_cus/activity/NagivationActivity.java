package com.lvdi.ruitianxia_cus.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbViewUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.adapter.NavAdapter;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
import com.lvdi.ruitianxia_cus.model.CustomerC;
import com.lvdi.ruitianxia_cus.request.GetApplicationsInNavigatorRequest;
import com.lvdi.ruitianxia_cus.util.ApplicationController;
import com.lvdi.ruitianxia_cus.view.NagivationMenuPop;

/**
 * 导航界面 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月13日 下午7:45:28
 */
public class NagivationActivity extends LvDiActivity {
	private CustomerC mCustomerC;// C端首页获取首页模板
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(click = "btnClick", id = R.id.comRl)
	/**
	 * 常用
	 */
	private RelativeLayout mCommonLayout;

	@AbIocView(click = "btnClick", id = R.id.perRl)
	/**
	 * 周边
	 */
	private RelativeLayout mPeripheryLayout;
	@AbIocView(click = "btnClick", id = R.id.busRl)
	/**
	 * 企业
	 */
	private RelativeLayout mBusinessLayout;
	@AbIocView(click = "btnClick", id = R.id.findRl)
	/**
	 * 发现
	 */
	private RelativeLayout mFindLayout;
	@AbIocView(itemClick = "itemClick", id = R.id.categoryGv)
	/**
	 * 列表
	 */
	private GridView mGridView;
	/**
	 * 列表适配器
	 */
	private NavAdapter mAdapter;
	private ApplicationEntity navigationApps;
	/**
	 * 列表每个项目的宽高
	 */
	private int itemWH;

	private int verticalSpacing;// 80;

	private int horizontalSpacing;// 70;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(NagivationActivity.this);
			switch (msg.what) {
			// 导航
			case HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_NAVIGATOR_SUCC:
				navigationApps = (ApplicationEntity) msg.obj;
				updataNavigation();
				break;
			case HandleAction.HttpType.HTTP_GET_APPLICATIONS_IN_NAVIGATOR_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_navigation);
		verticalSpacing = (int) AbViewUtil.dip2px(this, 25f);// 80;
		horizontalSpacing = (int) AbViewUtil.dip2px(this, 20f);// 70;
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("导航");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		loadData();
	}

	public void itemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Application application = navigationApps.applications.get(arg2);
		ApplicationController.responseController(this, application,
				NagivationActivity.class.getSimpleName());
	}

	/**
	 * 加载布局
	 * 
	 * @author Xubiao
	 */
	private void initView() {
		mGridView.setVerticalSpacing(verticalSpacing);
		mGridView.setHorizontalSpacing(horizontalSpacing);
		ViewTreeObserver vto2 = mGridView.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
				int gridH = mGridView.getHeight();
				itemWH = (gridH - verticalSpacing * 4 - (int) AbViewUtil
						.dip2px(NagivationActivity.this, 25f)) / 5;
				mAdapter = new NavAdapter(NagivationActivity.this, itemWH);
				mGridView.setAdapter(mAdapter);
				initData();
			}
		});

		mAbTitleBar.clearRightView();
		View rightViewMore = mInflater.inflate(R.layout.right_menu_btn, null);
		mAbTitleBar.addRightView(rightViewMore);
		final TextView personBt = (TextView) rightViewMore
				.findViewById(R.id.menuBtn);
		personBt.setBackgroundResource(R.drawable.main_more);
		mAbTitleBar.getRightLayout().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				NagivationMenuPop mainMenPop = new NagivationMenuPop(
						NagivationActivity.this, personBt, mCustomerC,
						NagivationActivity.class.getSimpleName());
				mainMenPop.show();
			}
		});
	}

	/**
	 * 更新列表
	 * 
	 * @author Xubiao
	 */
	private void updataNavigation() {
		mAdapter.setData(navigationApps);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 更新选中的table
	 * 
	 * @param viewId
	 * @author Xubiao
	 */
	private void updataTable(int viewId) {
		mCommonLayout.setSelected(false);
		mPeripheryLayout.setSelected(false);
		mBusinessLayout.setSelected(false);
		mFindLayout.setSelected(false);
		findViewById(viewId).setSelected(true);
		if (null != mAdapter) {
			mAdapter.clearData();
			mAdapter.notifyDataSetChanged();
		}
		requestData(viewId);
	}

	/**
	 * 加载数据
	 * 
	 * @author Xubiao
	 */
	private void loadData() {
		mCustomerC = (CustomerC) getIntent().getSerializableExtra(
				CustomerC.class.getSimpleName());
		updataTable(R.id.comRl);
	}

	private void requestData(int viewId) {
		if (AbWifiUtil.isConnectivity(this)) {
			if (null != mCustomerC && null != mCustomerC.selectedProject) {
				String app_category = "1";
				switch (viewId) {
				case R.id.comRl:
					app_category = "1";
					break;
				case R.id.perRl:
					app_category = "2";
					break;
				case R.id.busRl:
					app_category = "3";
					break;
				case R.id.findRl:
					app_category = "4";
					break;
				default:
					break;
				}
				GetApplicationsInNavigatorRequest.getInstance().sendRequest(
						mHandler,
						mCustomerC.selectedProject.organizationId + "",
						app_category, "", "1", "10");// 通过
				// 从1开始
				AbDialogUtil.showProgressDialog(NagivationActivity.this, 0,
						"获取导航页数据中...");
			}

		} else {
			AbToastUtil.showToast(this, R.string.please_check_network);
		}
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.comRl:
		case R.id.perRl:
		case R.id.busRl:
		case R.id.findRl:
			updataTable(v.getId());
		default:
			break;
		}
	}
}
