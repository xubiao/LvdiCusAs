package com.lvdi.ruitianxia_cus.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.ab.activity.AbActivity;
import com.ab.global.AbActivityManager;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Config;

/**
 * 
 * 类的详细描述： 找回密码第三步
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午2:09:08
 */
public class FindPassword3Activity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(click = "btnClick", id = R.id.backBt)
	Button backBt;// 返回按钮

	private Timer mTimer;// 计时器
	private int time;// 倒计时间
	private int setMode;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				time -= 1;
				if (time == 0) {
					onBack();
					break;
				}
				backBt.setText(time + "秒后返回首页");
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_findpassword3);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("设置密码");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		setMode = getIntent().getIntExtra("setMode",
				FindPassword2Activity.RESETPASSWORD);
		addTimer();
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.backBt:
			onBack();
			break;
		default:
			break;
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
				Message message = new Message();
				message.what = 0;
				mHandler.sendMessage(message);
			}
		};
		time = 1;
		backBt.setText(time + "秒后返回首页");
		mTimer = new Timer(true);
		mTimer.schedule(task, 1000, 1000); // 延时1000ms后执行，1000ms执行一次
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

	private void onBack() {
		if (setMode == FindPassword2Activity.FINDPASSWORD) {
			if (Config.APPMODE == Config.BMODE) {
				Intent intent = new Intent(FindPassword3Activity.this,
						BTerminaActivity.class);
				startActivity(intent);
			} else {
				Intent intent = new Intent(FindPassword3Activity.this,
						MainActivity.class);
				startActivity(intent);
			}
		} else {
			finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			onBack();
		}
		return false;
	}
}
