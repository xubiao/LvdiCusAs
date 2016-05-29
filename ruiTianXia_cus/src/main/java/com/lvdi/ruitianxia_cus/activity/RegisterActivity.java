package com.lvdi.ruitianxia_cus.activity;

import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.global.AbActivityManager;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.LoginInfo;
import com.lvdi.ruitianxia_cus.request.GetVerCodeRequest;
import com.lvdi.ruitianxia_cus.request.LoginRequest;
import com.lvdi.ruitianxia_cus.request.RegisterRequest;

/**
 * 
 * 类的详细描述： 登录界面
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月24日 下午11:04:52
 */
public class RegisterActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(id = R.id.userEt)
	EditText mUserEt;// 用户名输入框
	@AbIocView(id = R.id.passwordEt)
	EditText mPasswordEt;// 密码输入框
	@AbIocView(id = R.id.passwordEt2)
	EditText mPasswordEt2;// 再次密码输入框
	@AbIocView(id = R.id.codeEt)
	EditText mCodeEt;// 验证码输入框
	@AbIocView(click = "btnClick", id = R.id.sendcodeBt)
	Button sendcondeBt;// 发送验证码
	@AbIocView(click = "btnClick", id = R.id.registerBt)
	Button registerBt;// 注册按牛牛
	@AbIocView(click = "btnClick", id = R.id.agrTv)
	TextView agrTv;// 同意协议
	@AbIocView(id = R.id.agrCb)
	CheckBox agrCb;// 选中同意协议
	private Timer mTimer;// 计时器
	private int time;// 倒计时

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
			case HandleAction.HttpType.HTTP_REGISTER_SUCC:
				AbDialogUtil.removeDialog(RegisterActivity.this);
				registerSuccess();
				break;
			case HandleAction.HttpType.HTTP_REGISTER_FAIL:
				AbDialogUtil.removeDialog(RegisterActivity.this);
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_GET_VER_CODE_SUCC:
				break;
			case HandleAction.HttpType.HTTP_GET_VER_CODE_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				cancelTimer();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_register);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("注册");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		initView();
	}

	/**
	 * 加载布局
	 * 
	 * @author Xubiao
	 */
	private void initView() {
		agrTv.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG); // 下划线
		agrTv.getPaint().setAntiAlias(true);// 抗锯齿
		mUserEt.addTextChangedListener(textWatcher);
		mPasswordEt.addTextChangedListener(textWatcher);
		mPasswordEt2.addTextChangedListener(textWatcher);
		mCodeEt.addTextChangedListener(textWatcher);
		agrCb.setChecked(true);
		agrCb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView,
					boolean isChecked) {
				// TODO Auto-generated method stub
			}
		});
		checkEnabled();
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
		String passwordString = mPasswordEt.getText().toString();
		String password2String = mPasswordEt2.getText().toString();
		String codeString = mCodeEt.getText().toString();
		if (TextUtils.isEmpty(userString)) {
			sendcondeBt.setBackgroundResource(R.drawable.send_code_bg_inva);
			sendcondeBt.setEnabled(false);
		} else {
			if (time <= 0) {
				sendcondeBt.setBackgroundResource(R.drawable.send_code_bg);
				sendcondeBt.setEnabled(true);
			}
		}

		if (TextUtils.isEmpty(userString) || TextUtils.isEmpty(passwordString)
				|| TextUtils.isEmpty(password2String)
				|| TextUtils.isEmpty(codeString)) {
			registerBt.setBackgroundResource(R.drawable.login_bg_inva);
			registerBt.setEnabled(false);
		} else {
			registerBt.setBackgroundResource(R.drawable.login_bg);
			registerBt.setEnabled(true);
		}
	}

	/**
	 * 注册
	 * 
	 * @author Xubiao
	 */
	private void register() {
		if (checkInput()) {
			if (AbWifiUtil.isConnectivity(this)) {
				AbDialogUtil.showProgressDialog(RegisterActivity.this, 0,
						"注册中...");
				RegisterRequest.getInstance().sendRequest(mHandler,
						mUserEt.getText().toString().trim(),
						mPasswordEt.getText().toString(),
						mCodeEt.getText().toString().trim());
			} else {
				AbToastUtil.showToast(this, R.string.please_check_network);
			}
		} else {

		}
	}

	/**
	 * 获取验证码
	 * 
	 * @author Xubiao
	 */
	private void sendGetCode() {
		if (AbWifiUtil.isConnectivity(this)) {
			addCodeTimer();
			GetVerCodeRequest.getInstance().sendRequest(mHandler,
					mUserEt.getText().toString().trim());
		} else {
			AbToastUtil.showToast(this, R.string.please_check_network);
		}
	}

	/**
	 * 
	 * 注册成功
	 * 
	 * @author Xubiao
	 */
	private void registerSuccess() {
		LoginInfo account = new LoginInfo();
		account.userName = mUserEt.getText().toString();
		account.passWord = mPasswordEt.getText().toString().trim();
		Cache.updateLoginParams(account);
		AbActivityManager.getInstance().clearActivity(
				MainActivity.class.getSimpleName());
		LoginRequest.getInstance().sendRequest(null,
				mUserEt.getText().toString().trim(),
				mPasswordEt.getText().toString().trim());
		Intent intent = new Intent(RegisterActivity.this,
				RegisterSuccessActivity.class);
		startActivity(intent);
		finish();
	}

	/**
	 * 功能描述: <br>
	 * 检查输入的用户名和密码是否合法,合法返回null,否则返回错误信息
	 * 
	 * @param account
	 * @param pwd
	 * @return
	 */
	private boolean checkInput() {
		String account = mUserEt.getText().toString().trim();
		String pwd = mPasswordEt.getText().toString().trim();
		String pwd2 = mPasswordEt2.getText().toString().trim();
		String code = mCodeEt.getText().toString().trim();
		if (TextUtils.isEmpty(account)) {
			AbToastUtil.showToast(this, "请输入11位手机号");
			return false;
		} else if (account.length() != 11) {
			AbToastUtil.showToast(this, "请输入11位手机号");
			return false;
		} else if (TextUtils.isEmpty(pwd)) {
			AbToastUtil.showToast(this, "请输入密码");
			return false;
		} else if (pwd.length() < 6 || pwd.length() > 15) {
			AbToastUtil.showToast(this, "密码格式不对");
			return false;
		} else if (!pwd.equals(pwd2)) {
			AbToastUtil.showToast(this, "两次密码输入不一样");
			return false;
		} else if (TextUtils.isEmpty(code)) {
			AbToastUtil.showToast(this, "请输入验证码");
			return false;
		} else if (!agrCb.isChecked()) {
			AbToastUtil.showToast(this, "请勾选协议");
			return false;
		}
		return true;
	}

	/**
	 * 发送验证啊计时器
	 * 
	 * @author Xubiao
	 */

	private void addCodeTimer() {
		sendcondeBt.setEnabled(false);
		sendcondeBt.setBackgroundResource(R.drawable.send_code_bg_inva);
		TimerTask task = new TimerTask() {
			public void run() {
				Message message = new Message();
				message.what = 0;
				mHandler.sendMessage(message);
			}
		};
		time = 60;
		sendcondeBt.setText(time + "秒重新发送");
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
		sendcondeBt.setText("发送验证码");
		sendcondeBt.setEnabled(true);
		sendcondeBt.setBackgroundResource(R.drawable.send_code_bg);
	}

	/**
	 * 
	 * 检查密码格式
	 * 
	 * @param str
	 * @return
	 */
	private boolean checkPassword(String str) {
		int num = 0;
		num = Pattern.compile("\\d").matcher(str).find() ? num + 1 : num;
		num = Pattern.compile("[a-zA-Z]").matcher(str).find() ? num + 1 : num;

		char[] specialChar = str.toCharArray();
		int s = specialChar.length;
		boolean hasUnFitSpecialChar = false;
		Pattern pAddress = Pattern
				.compile("[-\\da-zA-Z`=\"\\[\\],./~!@#$%^&*()_+|:]+");
		for (int i = 0; i < s; i++) {
			if (!pAddress.matcher(String.valueOf(specialChar[i])).find()) {
				hasUnFitSpecialChar = true;
			}
		}
		num = Pattern.compile("[-`=\"\\[\\],./~!@#$%^&*()_+|:]+").matcher(str)
				.find() ? num + 1 : num;
		if (!hasUnFitSpecialChar) {
			return num >= 2;
		} else {
			return false;
		}
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.registerBt:
			register();
			break;
		case R.id.agrTv:
			Intent intent = new Intent(RegisterActivity.this,
					ProtocolActivity.class);
			intent.putExtra("title", "用户使用协议");
			intent.putExtra("url", "file:///android_asset/html/yhsyxy.html");
			startActivity(intent);
			break;
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
		default:
			break;
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		cancelTimer();
	}
}
