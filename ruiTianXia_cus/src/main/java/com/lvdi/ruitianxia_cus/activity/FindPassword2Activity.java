package com.lvdi.ruitianxia_cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.global.AbActivityManager;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.LoginInfo;
import com.lvdi.ruitianxia_cus.request.LoginRequest;
import com.lvdi.ruitianxia_cus.request.SetPasswordRequest;

/**
 * 
 * 类的详细描述： 找回密码第二步
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午2:08:38
 */
public class FindPassword2Activity extends LvDiActivity {
	public static final int RESETPASSWORD = 1;// 设置密码
	public static final int FINDPASSWORD = 2;// 忘记密码
	private int setMode;
	private String mUserName;// 用户名
	private AbTitleBar mAbTitleBar = null;

	@AbIocView(id = R.id.passwordEt)
	EditText mPasswordEt;// 密码框
	@AbIocView(id = R.id.passwordEt2)
	EditText mPasswordEt2;// 再次输入密码框
	@AbIocView(click = "btnClick", id = R.id.nextBt)
	Button nextBt;// 下一步
	@AbIocView(click = "btnClick", id = R.id.loginTv)
	TextView loginTv;// 直接去登录

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(FindPassword2Activity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_SETPASSWORD_SUCC:
				setPasswordSuccess();
				break;
			case HandleAction.HttpType.HTTP_SETPASSWORD_FAIL:
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
		setAbContentView(R.layout.activity_findpassword2);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		initView();
		loadData();
	}

	/**
	 * 加载布局
	 * 
	 * @author Xubiao
	 */
	private void initView() {
		mPasswordEt.addTextChangedListener(textWatcher);
		mPasswordEt2.addTextChangedListener(textWatcher);
		checkEnabled();
	}

	/**
	 * 加载数据
	 * 
	 * @author Xubiao
	 */
	private void loadData() {
		setMode = getIntent().getIntExtra("setMode", RESETPASSWORD);
		mUserName = getIntent().getStringExtra("userName");
		// mAbTitleBar.setTitleText(setMode == RESETPASSWORD ? "设置密码" : "忘记密码");
		mAbTitleBar.setTitleText("设置密码");
		if (setMode == FindPassword2Activity.RESETPASSWORD) {
			loginTv.setVisibility(View.GONE);
		}
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
		String passwordString = mPasswordEt.getText().toString();
		String password2String = mPasswordEt2.getText().toString();
		if (TextUtils.isEmpty(passwordString)
				|| TextUtils.isEmpty(password2String)) {
			nextBt.setBackgroundResource(R.drawable.login_bg_inva);
			nextBt.setEnabled(false);
		} else {
			nextBt.setBackgroundResource(R.drawable.login_bg);
			nextBt.setEnabled(true);
		}
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
		String pwd = mPasswordEt.getText().toString().trim();
		String pwd2 = mPasswordEt2.getText().toString().trim();
		if (TextUtils.isEmpty(pwd)) {
			AbToastUtil.showToast(this, "请输入密码");
			return false;
		}
		if (pwd.length() < 6 || pwd.length() > 15) {
			AbToastUtil.showToast(this, "密码格式不对");
			return false;
		} else if (!pwd.equals(pwd2)) {
			AbToastUtil.showToast(this, "两次密码输入不一样");
			return false;
		}
		return true;
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.nextBt:
			setPassword();
			break;
		case R.id.loginTv:
			AbActivityManager.getInstance().clearActivity(
					FindPassword1Activity.class.getSimpleName());
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 
	 * 设置密码请求
	 * 
	 * @author Xubiao
	 */
	private void setPassword() {
		if (checkInput()) {
			if (AbWifiUtil.isConnectivity(this)) {
				SetPasswordRequest.getInstance().sendRequest(mHandler,
						mUserName, mPasswordEt.getText().toString().trim());
				AbDialogUtil.showProgressDialog(FindPassword2Activity.this, 0,
						"设置密码中...");
			} else {
				AbToastUtil.showToast(this, R.string.please_check_network);
			}
		} else {
		}

	}

	/**
	 * 设置密码成功
	 * 
	 * @author Xubiao
	 */
	private void setPasswordSuccess() {
		AbActivityManager.getInstance().clearActivity(
				MainActivity.class.getSimpleName());
		AbActivityManager.getInstance().clearActivity(
				FindPassword1Activity.class.getSimpleName());
		AbActivityManager.getInstance().clearActivity(
				LoginActivity.class.getSimpleName());
		AbActivityManager.getInstance().clearActivity(
				BTerminaActivity.class.getSimpleName());

		LoginInfo account = new LoginInfo();
		account.userName = mUserName;
		account.passWord = mPasswordEt.getText().toString().trim();
		Cache.updateLoginParams(account);
		// if (setMode == FindPassword2Activity.FINDPASSWORD) {
		// } else {
		LoginRequest.getInstance().sendRequest(null, mUserName,
				mPasswordEt.getText().toString().trim());
		// }
		Intent intent = new Intent(FindPassword2Activity.this,
				FindPassword3Activity.class);
		intent.putExtra("setMode", setMode);
		startActivity(intent);
		finish();
	}
}
