//package com.lvdi.ruitianxia_cus.activity;
//
//import java.util.ArrayList;
//import java.util.Timer;
//import java.util.TimerTask;
//
//import android.content.Intent;
//import android.content.res.Resources;
//import android.graphics.Color;
//import android.graphics.drawable.Drawable;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Message;
//import android.text.format.Time;
//import android.view.Gravity;
//import android.view.KeyEvent;
//import android.view.MotionEvent;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.View.OnTouchListener;
//import android.widget.AbsListView;
//import android.widget.AbsListView.OnScrollListener;
//import android.widget.Button;
//import android.widget.ImageView;
//import android.widget.ListView;
//import android.widget.RelativeLayout;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.ab.global.AbActivityManager;
//import com.ab.util.AbDialogUtil;
//import com.ab.util.AbLogUtil;
//import com.ab.util.AbToastUtil;
//import com.ab.util.AbWifiUtil;
//import com.ab.view.ioc.AbIocView;
//import com.ab.view.pullview.AbPullToRefreshView;
//import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
//import com.ab.view.titlebar.AbTitleBar;
//import com.baidu.location.BDLocation;
//import com.lvdi.ruitianxia_cus.R;
//import com.lvdi.ruitianxia_cus.adapter.MainServiceAdapter;
//import com.lvdi.ruitianxia_cus.global.Cache;
//import com.lvdi.ruitianxia_cus.global.Config;
//import com.lvdi.ruitianxia_cus.global.HandleAction;
//import com.lvdi.ruitianxia_cus.model.AccountInfo;
//import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
//import com.lvdi.ruitianxia_cus.model.CustomerC;
//import com.lvdi.ruitianxia_cus.model.ProjectForC;
//import com.lvdi.ruitianxia_cus.request.CheckPermissionToBRequest;
//import com.lvdi.ruitianxia_cus.request.GetCustomerCLayoutRequest;
//import com.lvdi.ruitianxia_cus.util.ApplicationController;
//import com.lvdi.ruitianxia_cus.util.UpdateUtil;
//import com.lvdi.ruitianxia_cus.view.AppScrollView;
//import com.lvdi.ruitianxia_cus.view.AppScrollView2;
//import com.lvdi.ruitianxia_cus.view.AppScrollView2.ScrollMoveCallback;
//import com.lvdi.ruitianxia_cus.view.MainMenuPop;
//
///**
// * 
// * 类的详细描述：
// * 
// * @author XuBiao
// * @version 1.0.1
// * @time 2015年10月25日 下午5:25:37
// */
//public class MainActivity extends LvDiActivity implements
//		OnHeaderRefreshListener, OnScrollListener, ScrollMoveCallback {
//	public final static int REQUESTCODE_CHANGELOCATION = 1;
//	private final int REQUESTCODE_UPDATECUSTOMER = 2;
//	private final int REQUESTCODE_BHOME = 3;
//	private AbTitleBar mAbTitleBar = null;
//	@AbIocView(itemClick = "itemClick", id = R.id.serviceLv)
//	ListView mListView;// 主页列表
//	@AbIocView(id = R.id.contetRl)
//	RelativeLayout contentRl;
//	@AbIocView(id = R.id.refreshView)
//	AbPullToRefreshView mAbPullToRefreshView;// 当前界面的布局
//	@AbIocView(id = R.id.srollLayout)
//	AppScrollView2 mSrollLayout;
//	@AbIocView(id = R.id.refreshTipTv)
//	TextView mRefreshTipTv;// 刷新提示
//	@AbIocView(id = R.id.scrollRl)
//	RelativeLayout mScrollRl;// 刷新提示
//	private MainServiceAdapter mAdapter;// 列表适配器
//	private ProjectForC mSelectProjectForC;// 选中的项目
//	private CustomerC mCustomerC;// C端首页获取首页模板
//	private Handler mHandler = new Handler() {
//		@Override
//		public void handleMessage(Message msg) {
//			AbDialogUtil.removeDialog(MainActivity.this);
//			switch (msg.what) {
//			// 获取首页数据
//			case HandleAction.HttpType.HTTP_GET_CUSTOMER_C_LAYOUT_SUCC:
//				mCustomerC = (CustomerC) msg.obj;
//				Config.selectCustomerC = mCustomerC;
//				updataCustomerCLayout();
//				mAbPullToRefreshView.onHeaderRefreshFinish();
//				mAbPullToRefreshView.setPullRefreshEnable(false);
//				mRefreshTipTv.setVisibility(View.INVISIBLE);
//				break;
//			case HandleAction.HttpType.HTTP_GET_CUSTOMER_C_LAYOUT_FAIL:
//				mAbPullToRefreshView.onHeaderRefreshFinish();
//				AbToastUtil
//						.showToast(getApplicationContext(), (String) msg.obj);
//				if (null == mCustomerC) {
//					mAbPullToRefreshView.setPullRefreshEnable(true);
//					mRefreshTipTv.setVisibility(View.VISIBLE);
//				}
//				break;
//			// 检测B端权限
//			case HandleAction.HttpType.HTTP_CKECK_PERMISSION_TOB_SUCC:
//				boolean hasPermissionToB = (Boolean) msg.obj;
//				if (hasPermissionToB) {
//					if (null == mCustomerC) {
//						AbToastUtil.showToast(getApplicationContext(),
//								"没有可用的项目");
//						return;
//					}
//					Intent intent = new Intent(MainActivity.this,
//							BTerminaActivity.class);
//					intent.putExtra(CustomerC.class.getSimpleName(), mCustomerC);
//					startActivityForResult(intent, REQUESTCODE_BHOME);
//				} else {
//					showNoPermiTobDialog();
//					// Intent intent = new Intent(MainActivity.this,
//					// BTerminaActivity.class);
//					// intent.putExtra(CustomerC.class.getSimpleName(),
//					// mCustomerC);
//					// startActivityForResult(intent, REQUESTCODE_BHOME);
//				}
//				break;
//			case HandleAction.HttpType.HTTP_CKECK_PERMISSION_TOB_FAIL:
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
//		setAbContentView(R.layout.activity_main);
//		mAbTitleBar = this.getTitleBar();
//		mAbTitleBar.setTitleText("");
//
//		Resources res = getResources();
//		Drawable titleIcon = res.getDrawable(R.drawable.main_location_bg);
//		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
//		titleIcon.setBounds(0, 0, titleIcon.getMinimumWidth(),
//				titleIcon.getMinimumHeight());
//		mAbTitleBar.setCompoundDrawables(titleIcon, null, null, null);
//		mAbTitleBar.setLogo(R.drawable.main_change_bg);
//		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg_black);
//		initView();
//		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
//		mSelectProjectForC = Config.mSelectProjectForC;
//		GetCustomerCLayout();
//	}
//
//	/**
//	 * 加载布局
//	 * 
//	 * @author Xubiao
//	 */
//	private void initView() {
//		mSrollLayout.setViewMode(AppScrollView.HOME_MODE);
//		mListView.setOnTouchListener(new OnTouchListener() {
//
//			@Override
//			public boolean onTouch(View v, MotionEvent event) {
//				// TODO Auto-generated method stub
//				return true;
//			}
//		});
//		AbActivityManager.getInstance().clearAllOtherActivity(
//				MainActivity.class.getSimpleName());
//		mSrollLayout.setVisibility(View.GONE);
//		mAbTitleBar.clearRightView();
//		View rightViewMore = mInflater.inflate(R.layout.right_menu_btn, null);
//		mAbTitleBar.addRightView(rightViewMore);
//		final Button personBt = (Button) rightViewMore
//				.findViewById(R.id.menuBtn);
//		personBt.setBackgroundResource(R.drawable.main_more);
//		personBt.setOnClickListener(new View.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				MainMenuPop mainMenPop = new MainMenuPop(MainActivity.this,
//						personBt, mCustomerC, MainActivity.class
//								.getSimpleName());
//				mainMenPop.show();
//			}
//		});
//		mAbTitleBar.getLogoView().setOnClickListener(
//				new View.OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						// 跳转B端
//						jumpToB();
//					}
//				});
//		mAbTitleBar.getTitleTextButton().setOnClickListener(
//				new OnClickListener() {
//
//					@Override
//					public void onClick(View v) {
//						// TODO Auto-generated method stub
//						// 切换位置
//						Intent intent = new Intent(MainActivity.this,
//								ChangeLocationActivity.class);
//						startActivityForResult(intent,
//								REQUESTCODE_CHANGELOCATION);
//					}
//				});
//		new Handler().postDelayed(new Runnable() {
//
//			@Override
//			public void run() {
//				// TODO Auto-generated method stub
//				UpdateUtil.AutoUmengUpdate(MainActivity.this);
//			}
//		}, 25000);
//
//		// 设置监听器
//		mAbPullToRefreshView.setOnHeaderRefreshListener(this);
//		mAbPullToRefreshView.setPullRefreshEnable(false);
//		mAbPullToRefreshView.setLoadMoreEnable(false);
//		// 设置进度条的样式
//		mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(
//				this.getResources().getDrawable(R.drawable.progress_circular));
//		mAbPullToRefreshView.getHeaderView().setTextColor(
//				Color.parseColor("#7ee3c5"));
//	}
//
//	/**
//	 * 刷新首页数据
//	 * 
//	 * @author Xubiao
//	 */
//	private void updataCustomerCLayout() {
//		if (null != mCustomerC) {
//			if (mCustomerC.selectedProject != null)
//				mAbTitleBar
//						.setTitleText(mCustomerC.selectedProject.name != null ? mCustomerC.selectedProject.name
//								: "");
//			getDefaultSelect();
//			mSrollLayout.setAppList(mCustomerC.layouts);
//			mAdapter = new MainServiceAdapter(getApplicationContext());
//			mAdapter.setData(mCustomerC.layouts);
//			mListView.setAdapter(mAdapter);
//			mListView.setOnScrollListener(this);
//			mAdapter.setMiddleItem(mCustomerC.layouts.size() + 2);
//			mSrollLayout.setVisibility(View.VISIBLE);
//			mSrollLayout.setListener(MainActivity.this);
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
//	/**
//	 * C端首页获取首页模板
//	 * 
//	 * @author Xubiao
//	 */
//	private void GetCustomerCLayout() {
//		if (AbWifiUtil.isConnectivity(this)) {
//			if (null == mSelectProjectForC) {
//				BDLocation location = Cache.getLocation();
//				if (null != location) {
//					AccountInfo accountInfo = Cache.getAccountInfo();
//					// GetCustomerCLayoutRequest.getInstance().sendRequest(
//					// mHandler,
//					// accountInfo == null ? "" : accountInfo.partyId,
//					// "116.32935", "39.969147", "0");
//					GetCustomerCLayoutRequest.getInstance().sendRequest(
//							mHandler,
//							accountInfo == null ? "" : accountInfo.partyId,
//							location.getLongitude() + "",
//							location.getLatitude() + "", "0");
//					AbDialogUtil.showProgressDialog(MainActivity.this, 0,
//							"获取首页数据中...");
//				} else {
//					AbToastUtil.showToast(MainActivity.this, "定位失败，请检查网络是否正常");
//					mRefreshTipTv.setVisibility(View.VISIBLE);
//					mAbPullToRefreshView.setPullRefreshEnable(true);
//					mAbPullToRefreshView.onHeaderRefreshFinish();
//				}
//			} else {
//				AccountInfo accountInfo = Cache.getAccountInfo();
//				GetCustomerCLayoutRequest.getInstance().sendRequest(mHandler,
//						accountInfo == null ? "" : accountInfo.partyId, "", "",
//						mSelectProjectForC.organizationId + "");
//				AbDialogUtil.showProgressDialog(MainActivity.this, 0,
//						"获取首页数据中...");
//
//			}
//
//		} else {
//			AbToastUtil.showToast(this, R.string.please_check_network);
//			if (null == mCustomerC) {
//				mRefreshTipTv.setVisibility(View.VISIBLE);
//				mAbPullToRefreshView.setPullRefreshEnable(true);
//				mAbPullToRefreshView.onHeaderRefreshFinish();
//			}
//		}
//
//	}
//
//	/**
//	 * 去B端
//	 * 
//	 * @author Xubiao
//	 */
//	private void jumpToB() {
//		AccountInfo accountInfo = Cache.getAccountInfo();
//		if (null != accountInfo) {
//			if (AbWifiUtil.isConnectivity(MainActivity.this)) {
//				CheckPermissionToBRequest.getInstance().sendRequest(mHandler,
//						null == accountInfo ? "" : accountInfo.partyId);
//				AbDialogUtil.showProgressDialog(MainActivity.this, 0,
//						"企业端加载中...");
//			} else {
//				AbToastUtil.showToast(MainActivity.this,
//						R.string.please_check_network);
//			}
//		} else {
//			showLoginTip();
//		}
//
//	}
//
//	/**
//	 * 提示去登录
//	 * 
//	 * @author Xubiao
//	 */
//	private void showLoginTip() {
//		final View mView = mInflater
//				.inflate(R.layout.dialog_confirm_view, null);
//		AbDialogUtil.showAlertDialog(mView);
//		((TextView) mView.findViewById(R.id.messageTv)).setText("请先登录账号");
//
//		OnClickListener clickListener = new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				AbDialogUtil.removeDialog(mView);
//				if (v.getId() == R.id.cancelBt) {
//
//				} else if (v.getId() == R.id.okBt) {
//					Intent intent = new Intent(MainActivity.this,
//							LoginActivity.class);
//					startActivity(intent);
//				}
//
//			}
//		};
//
//		mView.findViewById(R.id.cancelBt).setOnClickListener(clickListener);
//		mView.findViewById(R.id.okBt).setOnClickListener(clickListener);
//	}
//
//	private void showNoPermiTobDialog() {
//		final View view = mInflater.inflate(R.layout.no_permission_tob_view,
//				null);
//		AbDialogUtil.showAlertDialog(view);
//		// final DialogBase dialogBase = new DialogBase(this);
//		// dialogBase.setContentView(view);
//		ImageView closeImageView = (ImageView) view.findViewById(R.id.closeIv);
//		closeImageView.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				AbDialogUtil.removeDialog(view);
//				// dialogBase.dismiss();
//			}
//		});
//		// dialogBase.show();
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
//	public void onHeaderRefresh(AbPullToRefreshView view) {
//		// TODO Auto-generated method stub
//		GetCustomerCLayout();
//	}
//
//	private int visibleItemCount;
//
//	@Override
//	public void onScroll(AbsListView view, int firstVisibleItem,
//			int visibleItemCount, int totalItemCount) {
//		// TODO Auto-generated method stub
//		if (0 != visibleItemCount) {
//			this.visibleItemCount = visibleItemCount;
//			if (setSelete) {
//				mAdapter.setMiddleItem(mCustomerC.layouts.size() + 2);
//				setSelete = false;
//				mListView.setSelection(mCustomerC.layouts.size() + 2
//						- visibleItemCount / 2 + 1);
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
//		AbLogUtil.e(getClass(), "onScroll");
//	}
//
//	@Override
//	public void onScrollStateChanged(AbsListView arg0, int arg1) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
//		case RESULT_OK:
//			if (requestCode == REQUESTCODE_CHANGELOCATION) {
//				mSelectProjectForC = (ProjectForC) data
//						.getSerializableExtra("selectProject");
//				Config.mSelectProjectForC = mSelectProjectForC;
//				GetCustomerCLayout();
//			} else if (requestCode == REQUESTCODE_UPDATECUSTOMER) {
//				GetCustomerCLayout();
//			} else if (requestCode == REQUESTCODE_BHOME) {
//				mCustomerC = Config.selectCustomerC;
//				updataCustomerCLayout();
//			}
//			break;
//
//		default:
//			break;
//		}
//	}
//
//	@Override
//	protected void onResume() {
//		super.onResume();
//		Config.APPMODE = Config.CMODE;
//		// setBackgroundByTime();
//	}
//
//	/**
//	 * 菜单、返回键响应
//	 */
//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		// TODO Auto-generated method stub
//		if (keyCode == KeyEvent.KEYCODE_BACK) {
//			exitBy2Click(); // 调用双击退出函数
//		}
//		return false;
//	}
//
//	/**
//	 * 双击退出函数
//	 */
//	private static Boolean isExit = false;
//
//	private void exitBy2Click() {
//		Timer tExit = null;
//		if (isExit == false) {
//			isExit = true; // 准备退出
//			Toast.makeText(this, "再次点击退出程序", Toast.LENGTH_SHORT).show();
//			tExit = new Timer();
//			tExit.schedule(new TimerTask() {
//				@Override
//				public void run() {
//					isExit = false; // 取消退出
//				}
//			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务
//
//		} else {
//			finish();
//		}
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
//		ApplicationController.responseController(this,
//				mCustomerC.layouts.get(position),
//				MainActivity2.class.getSimpleName());
//	}
//
//	@Override
//	public void onLongClicked(int position) {
//		// TODO Auto-generated method stub
//
//	}
//
//	@Override
//	public void removeIconClick(Application layout) {
//		// TODO Auto-generated method stub
//		
//	}
//
//}

package com.lvdi.ruitianxia_cus.activity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.global.AbActivityManager;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbViewUtil;
import com.ab.util.AbWifiUtil;
import com.ab.util.AndroidVersionCheckUtils;
import com.ab.view.ioc.AbIocView;
import com.ab.view.pullview.AbPullToRefreshView;
import com.ab.view.pullview.AbPullToRefreshView.OnHeaderRefreshListener;
import com.baidu.location.BDLocation;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.adapter.MainServiceAdapter;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
import com.lvdi.ruitianxia_cus.model.CustomerC;
import com.lvdi.ruitianxia_cus.model.ProjectForC;
import com.lvdi.ruitianxia_cus.request.GetCustomerCLayoutRequest;
import com.lvdi.ruitianxia_cus.util.ApplicationController;
import com.lvdi.ruitianxia_cus.view.AppScrollView;
import com.lvdi.ruitianxia_cus.view.AppScrollView.ScrollMoveCallback;
import com.lvdi.ruitianxia_cus.view.MainMenuPop;

/**
 * 
 * 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午5:25:37
 */
public class MainActivity extends LvDiActivity implements
		OnHeaderRefreshListener, OnScrollListener, ScrollMoveCallback {
	public final static int REQUESTCODE_CHANGELOCATION = 1;
	private final int REQUESTCODE_UPDATECUSTOMER = 2;
	private final int REQUESTCODE_BHOME = 3;
	@AbIocView(itemClick = "itemClick", id = R.id.serviceLv)
	ListView mListView;// 主页列表
	@AbIocView(id = R.id.contetRl)
	RelativeLayout contentRl;
	@AbIocView(id = R.id.refreshView)
	AbPullToRefreshView mAbPullToRefreshView;// 当前界面的布局
	@AbIocView(id = R.id.srollLayout)
	AppScrollView mSrollLayout;
	@AbIocView(id = R.id.refreshTipTv)
	TextView mRefreshTipTv;// 刷新提示
	@AbIocView(id = R.id.scrollRl)
	RelativeLayout mScrollRl;// 刷新提示
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

	private MainServiceAdapter mAdapter;// 列表适配器
	private ProjectForC mSelectProjectForC;// 选中的项目
	private CustomerC mCustomerC;// C端首页获取首页模板
	private TranslateAnimation mShowAction, mHiddenAction;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(MainActivity.this);
			switch (msg.what) {
			// 获取首页数据
			case HandleAction.HttpType.HTTP_GET_CUSTOMER_C_LAYOUT_SUCC:
				mCustomerC = (CustomerC) msg.obj;
				Config.selectCustomerC = mCustomerC;
				updataCustomerCLayout();
				mAbPullToRefreshView.onHeaderRefreshFinish();
				mAbPullToRefreshView.setPullRefreshEnable(false);
				mRefreshTipTv.setVisibility(View.INVISIBLE);
				break;
			case HandleAction.HttpType.HTTP_GET_CUSTOMER_C_LAYOUT_FAIL:
				mAbPullToRefreshView.onHeaderRefreshFinish();
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				if (null == mCustomerC) {
					mAbPullToRefreshView.setPullRefreshEnable(true);
					mRefreshTipTv.setVisibility(View.VISIBLE);
				}
				break;
			// 检测B端权限
			case HandleAction.HttpType.HTTP_CKECK_PERMISSION_TOB_SUCC:
				boolean hasPermissionToB = (Boolean) msg.obj;
				if (null == mCustomerC) {
					AbToastUtil.showToast(getApplicationContext(), "没有可用的项目");
					return;
				}
				Intent intent = new Intent(MainActivity.this,
						BTerminaActivity.class);
				intent.putExtra(CustomerC.class.getSimpleName(), mCustomerC);
				startActivityForResult(intent, REQUESTCODE_BHOME);
				break;
			case HandleAction.HttpType.HTTP_CKECK_PERMISSION_TOB_FAIL:
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
		setAbContentView(R.layout.activity_main);
		Resources res = getResources();
		Drawable titleIcon = res.getDrawable(R.drawable.main_location_bg);
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		titleIcon.setBounds(0, 0, titleIcon.getMinimumWidth(),
				titleIcon.getMinimumHeight());
		initView();
		mSelectProjectForC = Cache.getSelectProjectForC();
		GetCustomerCLayout();
	}

	/**
	 * 加载布局
	 * 
	 * @author Xubiao
	 */
	private void initView() {
		mLeftIv.setBackgroundDrawable(null);
		mLeftIv.setText("切换企业端");
		mLeftIv.setTextColor(Color.parseColor("#ffffff"));
		//mLeftIv.setVisibility(View.GONE);
		initAnimation();
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

		mSrollLayout.setViewMode(AppScrollView.HOME_MODE);
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		AbActivityManager.getInstance().clearAllOtherActivity(
				MainActivity.class.getSimpleName());
		mSrollLayout.setVisibility(View.GONE);
		mAdapter = new MainServiceAdapter(getApplicationContext());
		mListView.setAdapter(mAdapter);
		mRightRl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MainMenuPop mainMenPop = new MainMenuPop(MainActivity.this,
						mRightIv, mCustomerC, MainActivity.class
								.getSimpleName());
				mainMenPop.show();
			}
		});
		mLeftRl.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 跳转B端
				jumpToB();
			}
		});
		mMidRl.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 切换位置
				Intent intent = new Intent(MainActivity.this,
						ChangeLocationActivity.class);
				startActivityForResult(intent, REQUESTCODE_CHANGELOCATION);
			}
		});
		// 设置监听器
		mAbPullToRefreshView.setOnHeaderRefreshListener(this);
		mAbPullToRefreshView.setPullRefreshEnable(false);
		mAbPullToRefreshView.setLoadMoreEnable(false);
		// 设置进度条的样式
		mAbPullToRefreshView.getHeaderView().setHeaderProgressBarDrawable(
				this.getResources().getDrawable(R.drawable.progress_circular));
		mAbPullToRefreshView.getHeaderView().setTextColor(
				Color.parseColor("#7ee3c5"));

	}

	/**
	 * 刷新首页数据
	 * 
	 * @author Xubiao
	 */
	private void updataCustomerCLayout() {
		if (null != mCustomerC) {
			if (mCustomerC.selectedProject != null)
				mMidTv.setText(mCustomerC.selectedProject.name != null ? mCustomerC.selectedProject.name
						: "");
			mSrollLayout.setListener(MainActivity.this);
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
				int distance = i - 2;
				if (distance > 0) {
					// 左移
					for (int removeIdex = 0; removeIdex < distance; removeIdex++) {
						Application application = applications.remove(0);
						applications.add(application);
					}
				} else if (distance < 0) {
					// 右移
					for (int removeIdex = 0; removeIdex < -distance; removeIdex++) {
						Application application = applications
								.remove(applications.size() - 1);
						applications.add(0, application);
					}
				}

				break;
			}
		}
	}

	/**
	 * C端首页获取首页模板
	 * 
	 * @author Xubiao
	 */
	private void GetCustomerCLayout() {
		if (AbWifiUtil.isConnectivity(this)) {
			if (null == mSelectProjectForC) {
				BDLocation location = Cache.getLocation();
				if (null != location) {
					AccountInfo accountInfo = Cache.getAccountInfo();
					// GetCustomerCLayoutRequest.getInstance().sendRequest(
					// mHandler,
					// accountInfo == null ? "" : accountInfo.partyId,
					// "116.32935", "39.969147", "0");
					GetCustomerCLayoutRequest.getInstance().sendRequest(
							mHandler,
							accountInfo == null ? "" : accountInfo.partyId,
							location.getLongitude() + "",
							location.getLatitude() + "", "0");
					AbDialogUtil.showProgressDialog(MainActivity.this, 0,
							"获取首页数据中...");
				} else {
					AbToastUtil.showToast(MainActivity.this, "定位失败，请检查网络是否正常");
					mRefreshTipTv.setVisibility(View.VISIBLE);
					mAbPullToRefreshView.setPullRefreshEnable(true);
					mAbPullToRefreshView.onHeaderRefreshFinish();
				}
			} else {
				AccountInfo accountInfo = Cache.getAccountInfo();
				GetCustomerCLayoutRequest.getInstance().sendRequest(mHandler,
						accountInfo == null ? "" : accountInfo.partyId, "", "",
						mSelectProjectForC.organizationId + "");
				AbDialogUtil.showProgressDialog(MainActivity.this, 0,
						"获取首页数据中...");

			}

		} else {
			AbToastUtil.showToast(this, R.string.please_check_network);
			if (null == mCustomerC) {
				mRefreshTipTv.setVisibility(View.VISIBLE);
				mAbPullToRefreshView.setPullRefreshEnable(true);
				mAbPullToRefreshView.onHeaderRefreshFinish();
			}
		}

	}

	/**
	 * 去B端
	 * 
	 * @author Xubiao
	 */
	private void jumpToB() {
		if (null == mCustomerC) {
			AbToastUtil.showToast(getApplicationContext(), "没有可用的项目");
			return;
		}
		Intent intent = new Intent(MainActivity.this, BTerminaActivity.class);
		intent.putExtra(CustomerC.class.getSimpleName(), mCustomerC);
		intent.putExtra("fromC", true);
		startActivityForResult(intent, REQUESTCODE_BHOME);
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
					Intent intent = new Intent(MainActivity.this,
							LoginActivity.class);
					startActivity(intent);
				}

			}
		};

		mView.findViewById(R.id.cancelBt).setOnClickListener(clickListener);
		mView.findViewById(R.id.okBt).setOnClickListener(clickListener);
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
	public void onHeaderRefresh(AbPullToRefreshView view) {
		// TODO Auto-generated method stub
		GetCustomerCLayout();
	}

	private int visibleItemCount;

	@SuppressLint("NewApi")
	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			final int visibleItemCount, int totalItemCount) {
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

	}

	@Override
	public void onScrollStateChanged(AbsListView arg0, int arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (resultCode) { // resultCode为回传的标记，我在B中回传的是RESULT_OK
		case RESULT_OK:
			if (requestCode == REQUESTCODE_CHANGELOCATION) {
				mSelectProjectForC = (ProjectForC) data
						.getSerializableExtra("selectProject");
				Cache.putSelectProjectForC(mSelectProjectForC);
				GetCustomerCLayout();
			} else if (requestCode == REQUESTCODE_UPDATECUSTOMER) {
				GetCustomerCLayout();
			} else if (requestCode == REQUESTCODE_BHOME) {
				mCustomerC = Config.selectCustomerC;
				updataCustomerCLayout();
			}
			break;

		default:
			break;
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		Config.APPMODE = Config.CMODE;
		// setBackgroundByTime();
	}

	/**
	 * 菜单、返回键响应
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			exitBy2Click(); // 调用双击退出函数
		}
		return false;
	}

	/**
	 * 双击退出函数
	 */
	private static Boolean isExit = false;

	private void exitBy2Click() {
		Timer tExit = null;
		if (isExit == false) {
			isExit = true; // 准备退出
			Toast.makeText(this, "再次点击退出程序", Toast.LENGTH_SHORT).show();
			tExit = new Timer();
			tExit.schedule(new TimerTask() {
				@Override
				public void run() {
					isExit = false; // 取消退出
				}
			}, 2000); // 如果2秒钟内没有按下返回键，则启动定时器取消掉刚才执行的任务

		} else {
			finish();
		}
	}

	@Override
	public void moveY(int dy, int dx) {
		// TODO Auto-generated method stub
		// mListView.smoothScrollBy(dy, dx);
	}

	@Override
	public void cardClick(Application layout) {
		// TODO Auto-generated method stub
		ApplicationController.responseController(this, layout,
				MainActivity.class.getSimpleName());
	}

	@Override
	public void cardLongClick(Application layout) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addIconClick(Application layout) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeIconClick(Application layout) {
		// TODO Auto-generated method stub

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
