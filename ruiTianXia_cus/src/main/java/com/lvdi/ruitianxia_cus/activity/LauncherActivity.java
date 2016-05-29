package com.lvdi.ruitianxia_cus.activity;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import android.R.integer;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import cn.jpush.android.api.JPushInterface;

import com.ab.util.AbToastUtil;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.LocationProvider;
import com.lvdi.ruitianxia_cus.global.MyApplication;
import com.lvdi.ruitianxia_cus.request.LoginRequest;
import com.lvdi.ruitianxia_cus.util.UpdateUtil;

@SuppressLint("NewApi")
public class LauncherActivity extends LvDiActivity {
	/**
	 * 第一次启动应用
	 */
	private final int MSG_FIRST_USE = 1;
	/**
	 * 跳登录页
	 */
	private final int MSG_TO_LOGIN = 2;
	/**
	 * 跳主页面
	 */
	private final int MSG_TO_MAIN = 3;

	private ImageView[] images = new ImageView[15];

	private Timer mTimer;// 计时器
	private int time;// 倒计时间

	/**
	 * Handler
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				time++;
				if (time < 16)
					images[time - 1].setVisibility(View.VISIBLE);
				if (time == 15) {
					mHandler.sendEmptyMessage(getMsgWhat());
					UpdateUtil.AutoUmengUpdate(LauncherActivity.this);
				}
				break;
			case MSG_FIRST_USE:
				Intent intent = new Intent(LauncherActivity.this,
						GuideActivity.class);
				intent.putExtra("from", LauncherActivity.class.getSimpleName());
				startActivity(intent);
				finish();
				break;
			case MSG_TO_MAIN:
				// 后台登陆
				LoginRequest.getInstance().sendRequest(mHandler,
						Cache.getUser().userName, Cache.getUser().passWord);
				startActivity(new Intent(LauncherActivity.this,
						MainActivity.class));
				finish();
				break;
			case MSG_TO_LOGIN:
				startActivity(new Intent(LauncherActivity.this,
						MainActivity.class));
				finish();
				break;
			default:
				break;
			}
		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		checkTime();
		setAbContentView(R.layout.activity_launcher);
		// 解决打包应用程序安装后，点击“打开”进入程序，点击主页回到桌面，重新打开程序出现两个登录页面
		if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
			finish();
			return;
		}
		images[0] = (ImageView) findViewById(R.id.image1);
		images[1] = (ImageView) findViewById(R.id.image2);
		images[2] = (ImageView) findViewById(R.id.image3);
		images[3] = (ImageView) findViewById(R.id.image4);
		images[4] = (ImageView) findViewById(R.id.image5);
		images[5] = (ImageView) findViewById(R.id.image6);
		images[6] = (ImageView) findViewById(R.id.image7);
		images[7] = (ImageView) findViewById(R.id.image8);
		images[8] = (ImageView) findViewById(R.id.image9);
		images[9] = (ImageView) findViewById(R.id.image10);
		images[10] = (ImageView) findViewById(R.id.image11);
		images[11] = (ImageView) findViewById(R.id.image12);
		images[12] = (ImageView) findViewById(R.id.image13);
		images[13] = (ImageView) findViewById(R.id.image14);
		images[14] = (ImageView) findViewById(R.id.image15);
		addTimer();
	}

	private void checkTime() {
		if (Config.CHECKTIME) {
			Calendar calendar = Calendar.getInstance();
			int year = calendar.get(Calendar.YEAR);
			int month = calendar.get(Calendar.MONTH) + 1;
			int day = calendar.get(Calendar.DAY_OF_MONTH);
			String yearString = year + "";
			String monthString = month < 10 ? "0" + month : month + "";
			String dayString = day < 10 ? "0" + day : day + "";
			int time = Integer.parseInt(yearString + monthString + dayString);
			if (time > Config.INVALIDTIME) {
				AbToastUtil.showToast(this, "软件已过期,请联系软件作者");
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		}
	}

	/**
	 * 跳转到介绍页、主页面、登录页
	 * 
	 * @return
	 */
	private int getMsgWhat() {
		if (MyApplication.getInstance().isFirstStart()) {
			return MSG_FIRST_USE;
		} else {
			if (null == Cache.getUser()) {
				return MSG_TO_LOGIN;
			} else {
				return MSG_TO_MAIN;
			}
		}

	}

	/**
	 * 计时器
	 * 
	 * @author Xubiao
	 */

	private void addTimer() {
		TimerTask task = new TimerTask() {
			public void run() {
				mHandler.sendEmptyMessage(0);
			}
		};
		time = 0;
		mTimer = new Timer();
		mTimer.schedule(task, 0, 100);
	}

	/**
	 * 销毁计时器
	 * 
	 * @author Xubiao
	 */
	private void cancelTimer() {
		if (null != mTimer)
			mTimer.cancel();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelTimer();
	}
}