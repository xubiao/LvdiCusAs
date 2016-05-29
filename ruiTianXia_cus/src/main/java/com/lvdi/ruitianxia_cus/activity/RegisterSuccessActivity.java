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
import android.view.View.OnClickListener;
import android.widget.Button;

import com.ab.activity.AbActivity;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;

/**
 * 
 * 类的详细描述： 注册成功
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午1:18:20
 */
public class RegisterSuccessActivity extends AbActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(click = "btnClick", id = R.id.backBt)
	Button backBt;
	private Timer mTimer;
	private int time;

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
		setAbContentView(R.layout.activity_register_succ);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("注册");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		initView();

	}

	private void initView() {
		addTimer();
		mAbTitleBar.getLogoView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBack();
			}
		});
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
		time = 5;
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

	private void onBack() {
		cancelTimer();
		Intent intent = new Intent(RegisterSuccessActivity.this,
				MainActivity.class);
		startActivity(intent);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}
}
