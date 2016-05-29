package com.lvdi.ruitianxia_cus.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.ContentObserver;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.ab.global.AbActivityManager;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.baidu.location.BDLocation;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
import com.lvdi.ruitianxia_cus.model.CustomerC;
import com.lvdi.ruitianxia_cus.model.ProjectForC;
import com.lvdi.ruitianxia_cus.request.CheckPermissionToBRequest;
import com.lvdi.ruitianxia_cus.request.GetApplicationsForBRequest;
import com.lvdi.ruitianxia_cus.request.GetCustomerCLayoutRequest;
import com.lvdi.ruitianxia_cus.util.ApplicationController;
import com.lvdi.ruitianxia_cus.view.customview.circlemenu.CircleMenuLayout;
import com.lvdi.ruitianxia_cus.view.customview.circlemenu.CircleMenuLayout.OnMenuItemClickListener;
import com.lvdi.ruitianxia_cus.view.dialog.DialogBase;

/**
 * 
 * 类的详细描述：转盘
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月29日 下午5:25:37
 */
public class BTerminaActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(id = R.id.id_menulayout)
	CircleMenuLayout mCircleMenuLayout;

	@AbIocView(id = R.id.hourTv)
	TextView mHourTv;

	@AbIocView(id = R.id.minTv)
	TextView mMinTv;

	@AbIocView(id = R.id.midIconTv)
	TextView mMideIconTv;

	@AbIocView(id = R.id.midTitleTv)
	TextView mMidTitleTv;

	@AbIocView(id = R.id.midDesTv)
	TextView mMidDesTv;

	@AbIocView(click = "btnClick", id = R.id.showRl)
	LinearLayout mShowRl;

	private CustomerC mCustomerC;// C端首页获取首页模板;
	private List<Application> mApplications;
	private Application mSelectApplication;// 转盘选中的应用
	private boolean hasPermissionToB;
	private boolean mFromC;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_GET_GET_APPLICATIONS_FORB_SUCC:
				ApplicationEntity applicationEntity = (ApplicationEntity) msg.obj;
				List<Application> applications = applicationEntity.applications;
				updateData(applications);
				AbDialogUtil.removeDialog(BTerminaActivity.this);
				break;
			case HandleAction.HttpType.HTTP_GET_GET_APPLICATIONS_FORB_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				AbDialogUtil.removeDialog(BTerminaActivity.this);
				break;
			// 获取首页数据
			case HandleAction.HttpType.HTTP_GET_CUSTOMER_C_LAYOUT_SUCC:
				mCustomerC = (CustomerC) msg.obj;
				Config.selectCustomerC = mCustomerC;
				checkPermissionToB();
				break;
			case HandleAction.HttpType.HTTP_GET_CUSTOMER_C_LAYOUT_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				AbDialogUtil.removeDialog(BTerminaActivity.this);
				break;
			// 检测B端权限
			case HandleAction.HttpType.HTTP_CKECK_PERMISSION_TOB_SUCC:
				hasPermissionToB = (Boolean) msg.obj;
				loadData();
				break;
			case HandleAction.HttpType.HTTP_CKECK_PERMISSION_TOB_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				AbDialogUtil.removeDialog(BTerminaActivity.this);
				break;
			default:
				AbDialogUtil.removeDialog(BTerminaActivity.this);
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_turntable);
		mAbTitleBar = this.getTitleBar();
		// mAbTitleBar.setLogo(R.drawable.main_change_bg);
		mAbTitleBar.setLogoText("切换用户端");
		mAbTitleBar.setLogoTextSizeSp(15);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg_black);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		mFromC = getIntent().getBooleanExtra("fromC", false);
		if (!mFromC) {
			GetCustomerCLayout();
		} else {
			if (null != Cache.getAccountInfo()) {
				checkPermissionToB();
			} else {
				loadData();
			}
			Config.APPMODE = Config.BMODE;
		}
	}

	private void initView() {
		mCircleMenuLayout.setVisibility(View.INVISIBLE);
		// mAbTitleBar.clearRightView();
		// View rightViewMore = mInflater.inflate(R.layout.right_menu_btn,
		// null);
		// mAbTitleBar.addRightView(rightViewMore);
		// TextView personBt = (TextView)
		// rightViewMore.findViewById(R.id.menuBtn);
		// personBt.setBackgroundResource(R.drawable.main_head_bg);
		// mAbTitleBar.getRightLayout().setOnClickListener(
		// new View.OnClickListener() {
		//
		// @Override
		// public void onClick(View v) {
		// // 个人中心
		// AccountInfo accountInfo = Cache.getAccountInfo();
		// if (null != accountInfo) {
		// Intent intent = new Intent(BTerminaActivity.this,
		// PersonCenterActivity.class);
		// startActivity(intent);
		// } else {
		// showLoginTip();
		//
		// }
		//
		// }
		// });
		mAbTitleBar.getLogoView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBack();
			}
		});
		setLayoutParams();
		AbActivityManager.getInstance().clearAllOtherActivity(
				BTerminaActivity.class.getSimpleName());
	}

	private void updateData(List<Application> applications) {
		mApplications = new ArrayList<Application>();
		if (null == applications)
			return;
		int size = applications.size();
		if (size <= 6) {
			mApplications.addAll(applications);
			mApplications.addAll(applications);
			mApplications.addAll(applications);
			mApplications.addAll(applications);
		} else if (size <= 8) {
			mApplications.addAll(applications);
			mApplications.addAll(applications);
			mApplications.addAll(applications);
		} else if (size <= 12) {
			mApplications.addAll(applications);
			mApplications.addAll(applications);
		}
		mCircleMenuLayout.setMenuItemIconsAndTexts(mApplications);
		mCircleMenuLayout
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void itemClick(View view, int pos) {
						if (null == mApplications || mApplications.size() == 0)
							return;
						Application application = mApplications.get(pos);
						clickAppliction(application);
					}

					@Override
					public void itemCenterClick(View view) {

					}

					@Override
					public void itemSelect(View view, int pos) {
						// TODO Auto-generated method stub
						updateSelect(pos);
					}
				});
		mCircleMenuLayout.setVisibility(View.VISIBLE);
	}

	private void updateSelect(int pos) {
		mSelectApplication = mApplications.get(pos);
		mMideIconTv.setText(mSelectApplication.appIconKey);
		mMidTitleTv.setText(mSelectApplication.appName);
		mMidDesTv.setText(mSelectApplication.appIntroduction);
	}

	/**
	 * 设置转盘位置
	 * 
	 * @author Xubiao
	 */
	private void setLayoutParams() {
		final ImageView bottomImageView = (ImageView) findViewById(R.id.bottomIv);
		RelativeLayout root = (RelativeLayout) findViewById(R.id.mainroot);
		root.post(new Runnable() {

			public void run() {
				int width = bottomImageView.getWidth();
				int height = bottomImageView.getHeight();
				int wh = width > height ? height : width;
				RelativeLayout.LayoutParams lParams = (LayoutParams) bottomImageView
						.getLayoutParams();
				lParams.width = wh;
				lParams.height = wh;
				bottomImageView.setLayoutParams(lParams);
				mCircleMenuLayout.setDefaultWidth(wh * 2);
			}
		});

	}

	/**
	 * 获取数据
	 * 
	 * @author Xubiao
	 */
	private void loadData() {
		if (mFromC) {
			mCustomerC = (CustomerC) getIntent().getSerializableExtra(
					CustomerC.class.getSimpleName());
		}
		if (AbWifiUtil.isConnectivity(this)) {
			int organizationId = 0;
			if (null != mCustomerC && null != mCustomerC.selectedProject) {
				organizationId = mCustomerC.selectedProject.organizationId;
				mAbTitleBar.setTitleText(mCustomerC.selectedProject.name);
			}
			GetApplicationsForBRequest.getInstance().sendRequest(mHandler,
					organizationId + "", "-1", "-1");
			if (mFromC && null == Cache.getAccountInfo()) {
				AbDialogUtil.showProgressDialog(BTerminaActivity.this, 0,
						"获取企业数据中...");
			}
		} else {
			AbToastUtil.showToast(this, R.string.please_check_network);
		}
	}

	/**
	 * C端首页获取首页模板
	 * 
	 * @author Xubiao
	 */
	private void GetCustomerCLayout() {
		if (AbWifiUtil.isConnectivity(this)) {
			ProjectForC mSelectProjectForC = Cache.getSelectProjectForC();
			if (null == mSelectProjectForC) {
				BDLocation location = Cache.getLocation();
				if (null != location) {
					AccountInfo accountInfo = Cache.getAccountInfo();
					GetCustomerCLayoutRequest.getInstance().sendRequest(
							mHandler,
							accountInfo == null ? "" : accountInfo.partyId,
							location.getLongitude() + "",
							location.getLatitude() + "", "0");
					AbDialogUtil.showProgressDialog(BTerminaActivity.this, 0,
							"获取企业数据中...");
				} else {
					AbToastUtil.showToast(BTerminaActivity.this,
							"定位失败，请检查网络是否正常");
				}
			} else {
				AccountInfo accountInfo = Cache.getAccountInfo();
				GetCustomerCLayoutRequest.getInstance().sendRequest(mHandler,
						accountInfo == null ? "" : accountInfo.partyId, "", "",
						mSelectProjectForC.organizationId + "");
				AbDialogUtil.showProgressDialog(BTerminaActivity.this, 0,
						"获取企业数据中...");
			}

		} else {
			AbToastUtil.showToast(this, R.string.please_check_network);
		}

	}

	/**
	 * 检测B权限
	 * 
	 * @author Xubiao
	 */
	private void checkPermissionToB() {
		AccountInfo accountInfo = Cache.getAccountInfo();
		if (null != accountInfo) {
			if (AbWifiUtil.isConnectivity(BTerminaActivity.this)) {
				CheckPermissionToBRequest.getInstance().sendRequest(mHandler,
						null == accountInfo ? "" : accountInfo.partyId);
				if (mFromC) {
					AbDialogUtil.showProgressDialog(BTerminaActivity.this, 0,
							"获取企业数据中...");
				}
			} else {
				AbToastUtil.showToast(BTerminaActivity.this,
						R.string.please_check_network);
				AbDialogUtil.removeDialog(BTerminaActivity.this);
			}
		} else {
			AbDialogUtil.removeDialog(BTerminaActivity.this);
		}

	}

	Calendar mCalendar;
	/** 时区发生变化的广播接收者 */
	private BroadcastReceiver receiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if (intent.getAction().equals(Intent.ACTION_TIMEZONE_CHANGED)) {
				mCalendar = Calendar.getInstance();
			}
			updateTime();

		}
	};

	/** 当时间改变时的观察者类 */
	private class FormatChangeObserver extends ContentObserver {

		public FormatChangeObserver() {
			super(new Handler());
		}

		@Override
		public void onChange(boolean selfChange) {
			// TODO Auto-generated method stub
			super.onChange(selfChange);
			updateTime();
		}

	}

	FormatChangeObserver mFormatChangeObserver;
	boolean mAttached;

	@Override
	public void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		if (mAttached)
			return;
		mAttached = true;

		IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_TIME_CHANGED);
		filter.addAction(Intent.ACTION_TIME_TICK);
		filter.addAction(Intent.ACTION_TIMEZONE_CHANGED);
		registerReceiver(receiver, filter);

		mFormatChangeObserver = new FormatChangeObserver();
		getContentResolver().registerContentObserver(
				Settings.System.CONTENT_URI, true, mFormatChangeObserver);
		updateTime();
	}

	@Override
	public void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		if (!mAttached)
			return;
		mAttached = false;
		unregisterReceiver(receiver);
		getContentResolver().unregisterContentObserver(mFormatChangeObserver);

	}

	private void updateTime() {
		Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		mHourTv.setText(hour < 10 ? "0" + hour : "" + hour);
		mMinTv.setText(":" + (minute < 10 ? "0" + minute : "" + minute));
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
		// if (!changeLocation) {
		// finish();
		// } else {
		// Intent intent = new Intent();
		// setResult(RESULT_OK, intent);
		// finish();
		// }

		AbActivityManager.getInstance().clearActivity(
				MainActivity.class.getSimpleName());
		Intent intent = new Intent(BTerminaActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.showRl:
			clickAppliction(mSelectApplication);
			break;
		default:
			break;
		}
	}

	/**
	 * 点击应用
	 * 
	 * @param application
	 * @author Xubiao
	 */
	private void clickAppliction(Application application) {
		AccountInfo accountInfo = Cache.getAccountInfo();
		if (null != accountInfo) {
			if (!hasPermissionToB) {
				showNoPermiTobDialog();
			} else {
				ApplicationController.responseController(BTerminaActivity.this,
						application, BTerminaActivity.class.getSimpleName());
			}

		} else {
			showLoginTip();

		}

	}

	private void showNoPermiTobDialog() {
		final View view = mInflater.inflate(R.layout.no_permission_tob_view,
				null);
		TextView textView = (TextView) view.findViewById(R.id.text1);
		String str = "请将您所在公司的企业营业执照、企\n业税务登记证、企业组织机构代码证\n以及个人联系方式邮件发送到";

		int fstart = str.indexOf("企业营业执照、企\n业税务登记证、企业组织机构代码证");
		int fend = fstart + "企业营业执照、企\n业税务登记证、企业组织机构代码证".length();
		SpannableStringBuilder style = new SpannableStringBuilder(str);
		style.setSpan(new ForegroundColorSpan(Color.parseColor("#144f3d")),
				fstart, fend, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		textView.setText(style);

		// AbDialogUtil.showAlertDialog(view);
		final DialogBase dialogBase = new DialogBase(this);
		dialogBase.setContentView(view);
		ImageView closeImageView = (ImageView) view.findViewById(R.id.closeIv);
		closeImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// AbDialogUtil.removeDialog(view);
				dialogBase.dismiss();
			}
		});
		dialogBase.show();
	}

	/**
	 * 提示去登录
	 * 
	 * @author Xubiao
	 */
	private void showLoginTip() {
		final View mView = mInflater
				.inflate(R.layout.dialog_confirm_view, null);
		AbDialogUtil.showAlertDialog(mView);
		((TextView) mView.findViewById(R.id.messageTv)).setText("请先登录账号");

		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AbDialogUtil.removeDialog(mView);
				if (v.getId() == R.id.cancelBt) {

				} else if (v.getId() == R.id.okBt) {
					Intent intent = new Intent(BTerminaActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}

			}
		};

		mView.findViewById(R.id.cancelBt).setOnClickListener(clickListener);
		mView.findViewById(R.id.okBt).setOnClickListener(clickListener);
	}
}
