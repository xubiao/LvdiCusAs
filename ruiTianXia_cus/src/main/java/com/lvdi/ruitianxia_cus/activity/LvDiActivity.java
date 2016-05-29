package com.lvdi.ruitianxia_cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.util.AbDialogUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.umeng.analytics.MobclickAgent;

public class LvDiActivity extends AbActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	public void setAbTitle(String title) {
		AbTitleBar mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText(title);
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		// JPushInterface.onPause(this);
		String activityName = getClass().getSimpleName();
		if (!activityName.equals(UnifyWebViewActivity.class.getSimpleName())) {
			MobclickAgent.onPageEnd(activityName); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
			// onPageEnd 在onPause
			// 之前调用,因为 onPause
			// 中会保存信息。"SplashScreen"为页面名称，可自定义

			MobclickAgent.onPause(this);
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		// JPushInterface.onResume(this);
		String activityName = getClass().getSimpleName();
		if (!activityName.equals(UnifyWebViewActivity.class.getSimpleName())) {
			MobclickAgent.onPageStart(activityName); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
			MobclickAgent.onResume(this);
		}
	}

	/**
	 * 描述：用指定资源ID表示的View填充主界面.
	 * 
	 * @param resId
	 *            指定的View的资源ID
	 */
	public void setAbContentView(int resId) {
		super.setAbContentView(resId);
		initUi();
		initData();
		initListener();
	};

	public void initUi() {
	};

	public void initListener() {
	};

	public void initData() {
	};

	/**
	 * @param str
	 * @return 防止空指针异常
	 */
	public String formatStr(String str) {
		if (str == null) {
			return "";
		}
		return str;
	}

	/**
	 * @return true:已登录 false:未登录
	 */
	public boolean isLogin() {
		return Cache.getAccountInfo() != null ? true : false;
	}

	/**
	 * 请先去登录
	 */
	public void startLoginActivity() {
		startLoginActivity(true);
	}

	/**
	 * 请先去登录 isBackLastPage是否返回到上个页面
	 * 
	 * @param
	 */
	public void startLoginActivity(boolean isBackLastPage) {
		Intent intent = new Intent(this, LoginActivity.class);
		intent.putExtra("isBackLastPage", isBackLastPage);
		startActivity(intent);
	}

	public void displayProgressDlg(String title) {
		View v = LayoutInflater.from(this).inflate(
				R.layout.view_progress_dialog, null);
		((TextView) (v.findViewById(R.id.tv_1))).setText(title);
		AbDialogUtil.showAlertDialog(v);// AlertDialog.THEME_HOLO_LIGHT
	}

	public void hideProgressDlg() {
		AbDialogUtil.removeDialog(this);
	}
}
