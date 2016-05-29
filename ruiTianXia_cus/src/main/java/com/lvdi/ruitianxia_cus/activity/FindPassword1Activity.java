package com.lvdi.ruitianxia_cus.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.request.CheckVerCodeRequest;
import com.lvdi.ruitianxia_cus.request.GetVerCodeRequest;

/**
 * 
 * 类的详细描述： 找回密码第一步
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午2:07:57
 */
public class FindPassword1Activity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(id = R.id.userEt)
	EditText mUserEt;// 密码框
	@AbIocView(id = R.id.codeEt)
	EditText mCodeEt;// 验证码
	@AbIocView(click = "btnClick", id = R.id.sendcodeBt)
	Button sendcondeBt;// 发送验证码
	@AbIocView(click = "btnClick", id = R.id.nextBt)
	Button nextBt;// 下一步按钮
	@AbIocView(click = "btnClick", id = R.id.loginTv)
	TextView loginTv;// 直接登陆
	private int setMode;// 区分是从忘记密码过来的 还是从修改密码过来的

	private Timer mTimer;// 计时器
	private int time;// 倒计时时间

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				time -= 1;
				if (time == 0) {
					cancelTimer();
					break;
				}
				sendcondeBt.setText(time + "秒重新发送");
				break;
			// 校验验证码
			case HandleAction.HttpType.HTTP_CHECK_VER_CODE_SUCC:
				AbDialogUtil.removeDialog(FindPassword1Activity.this);
				checkCodeSuccess();
				break;
			case HandleAction.HttpType.HTTP_CHECK_VER_CODE_FAIL:
				AbDialogUtil.removeDialog(FindPassword1Activity.this);
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			// 获取验证码
			case HandleAction.HttpType.HTTP_GET_VER_CODE_SUCC:
				break;
			case HandleAction.HttpType.HTTP_GET_VER_CODE_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				Config.lastSendCodtTime = 0;
				cancelTimer();
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_findpassword1);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		initView();
		loadData();
		int plusTime = (int) ((System.currentTimeMillis() - Config.lastSendCodtTime) / 1000);
		if (plusTime > 0 && plusTime < 60) {
			addCodeTimer(60 - plusTime);
		}
	}

	/**
	 * 加载布局
	 * 
	 * @author Xubiao
	 */
	private void initView() {
		mUserEt.addTextChangedListener(textWatcher);
		mCodeEt.addTextChangedListener(textWatcher);
		checkEnabled();
	}

	/**
	 * 加载数据
	 * 
	 * @author Xubiao
	 */
	private void loadData() {
		setMode = getIntent().getIntExtra("setMode",
				FindPassword2Activity.RESETPASSWORD);
		mAbTitleBar.setTitleText("输入验证码");
		mUserEt.setText(getPhoneNumber());
		if (setMode == FindPassword2Activity.RESETPASSWORD) {
			loginTv.setVisibility(View.GONE);
		}
	}

	/**
	 * 获取本机号码
	 * 
	 * @return
	 * @author Xubiao
	 */
	private String getPhoneNumber() {
		// 设置密码返回当前登录的账户 找回密码返回本机号码
		if (setMode == FindPassword2Activity.RESETPASSWORD) {
			mUserEt.setEnabled(false);
			return Cache.getUser().userName;
		}
		TelephonyManager mTelephonyMgr;
		mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		String phoneNumber = mTelephonyMgr.getLine1Number();
		if (!TextUtils.isEmpty(phoneNumber) && phoneNumber.startsWith("+86")) {
			phoneNumber = phoneNumber.substring(3);
		}
		return phoneNumber;
	}

	/**
	 * TextWatcher
	 */
	private TextWatcher textWatcher = new TextWatcher() {

		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub

		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before,
				int count) {
			checkEnabled();
		}
	};

	private void checkEnabled() {
		String userString = mUserEt.getText().toString();
		String codeString = mCodeEt.getText().toString();
		if (TextUtils.isEmpty(userString)) {
			sendcondeBt.setBackgroundColor(Color.parseColor("#bbbbbb"));
			sendcondeBt.setEnabled(false);
		} else {
			if (time <= 0) {
				sendcondeBt.setBackgroundResource(R.drawable.get_code_bg);
				sendcondeBt.setEnabled(true);
			}
		}

		if (TextUtils.isEmpty(userString) || TextUtils.isEmpty(codeString)) {
			nextBt.setBackgroundResource(R.drawable.login_bg_inva);
			nextBt.setEnabled(false);
		} else {
			nextBt.setBackgroundResource(R.drawable.login_bg);
			nextBt.setEnabled(true);
		}
	}

	/**
	 * 发送验证码计时器
	 * 
	 * @author Xubiao
	 */

	private void addCodeTimer(int plusTime) {
		sendcondeBt.setEnabled(false);
		sendcondeBt.setBackgroundColor(Color.parseColor("#bbbbbb"));
		TimerTask task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 0;
				mHandler.sendMessage(message);
			}
		};
		time = plusTime;
		sendcondeBt.setText(time + "秒重新发送");
		mTimer = new Timer(true);
		mTimer.schedule(task, 1000, 1000); // 延时1000ms后执行，1000ms执行一次
	}

	/**
	 * 取消计时器
	 * 
	 * @author Xubiao
	 */
	private void cancelTimer() {
		if (null != mTimer)
			mTimer.cancel();
		sendcondeBt.setText("获取验证码");
		sendcondeBt.setEnabled(true);
		sendcondeBt.setBackgroundResource(R.drawable.get_code_bg);
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.sendcodeBt:
			String account = mUserEt.getText().toString().trim();
			if (TextUtils.isEmpty(account)) {
				AbToastUtil.showToast(this, "请输入11位手机号");
				break;
			} else if (account.length() != 11) {
				AbToastUtil.showToast(this, "请输入11位手机号");
				break;
			} else {
				sendGetCode();
			}
			break;
		case R.id.loginTv:
			finish();
			break;
		case R.id.nextBt:
			toNext();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * 下一步 校验验证码先
	 * 
	 * @author Xubiao
	 */
	private void toNext() {
		String account = mUserEt.getText().toString().trim();
		String code = mCodeEt.getText().toString().trim();
		if (TextUtils.isEmpty(account)) {
			AbToastUtil.showToast(this, "请输入11位手机号");
			return;
		} else if (account.length() != 11) {
			AbToastUtil.showToast(this, "请输入11位手机号");
			return;
		} else if (TextUtils.isEmpty(code)) {
			AbToastUtil.showToast(this, "请输入验证码");
			return;
		}
		CheckVerCode();
	}

	/**
	 * 获取验证码
	 * 
	 * @author Xubiao
	 */
	private void sendGetCode() {
		if (AbWifiUtil.isConnectivity(this)) {
			Config.lastSendCodtTime = System.currentTimeMillis();
			addCodeTimer(60);
			GetVerCodeRequest.getInstance().sendRequest(mHandler,
					mUserEt.getText().toString().trim());
		} else {
			AbToastUtil.showToast(this, R.string.please_check_network);
		}
	}

	/**
	 * 校验验证码
	 * 
	 * @author Xubiao
	 */
	private void CheckVerCode() {
		if (AbWifiUtil.isConnectivity(this)) {
			CheckVerCodeRequest.getInstance().sendRequest(mHandler,
					mUserEt.getText().toString().trim(),
					mCodeEt.getText().toString().trim());
			AbDialogUtil.showProgressDialog(FindPassword1Activity.this, 0,
					"校验验证码...");
		} else {
			AbToastUtil.showToast(this, R.string.please_check_network);
		}
	}

	/**
	 * 验证码通过 去到下一步
	 * 
	 * @author Xubiao
	 */
	private void checkCodeSuccess() {
		Intent intent = new Intent(FindPassword1Activity.this,
				FindPassword2Activity.class);
		intent.putExtra("setMode", setMode);
		intent.putExtra("userName", mUserEt.getText().toString().trim());
		startActivity(intent);
	}
}
