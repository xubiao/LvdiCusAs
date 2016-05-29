package com.lvdi.ruitianxia_cus.activity;

import java.util.Set;
import java.util.regex.Pattern;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

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

/**
 * 
 * 类的详细描述： 登录界面
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月24日 下午11:04:52
 */
public class LoginActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(id = R.id.userEt)
	EditText mUserEt;// 用户名输入框
	@AbIocView(id = R.id.passwordEt)
	EditText mPasswordEt;// 密码输入框
	@AbIocView(click = "btnClick", id = R.id.loginBt)
	Button loginBt;// 登录按钮
	@AbIocView(click = "btnClick", id = R.id.registerBt)
	TextView registerBt;// 注册点击
	@AbIocView(click = "btnClick", id = R.id.forgetTv)
	TextView forgetTv;// 忘记密码点击

	/**
	 * 账号
	 */
	private String mAccountName = "";
	/**
	 * 密码
	 */
	private String mPassword = "";

	/**
	 * 是否返回上个页面
	 */
	private boolean isBackLastPage = false;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(LoginActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_LOGIN_SUCC:
				loginSuccess();
				break;
			case HandleAction.HttpType.HTTP_LOGIN_FAIL:
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
		setAbContentView(R.layout.activity_login);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("登录");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		initView();

		isBackLastPage = getIntent().getBooleanExtra("isBackLastPage", false);
	}

	/**
	 * 加载布局
	 * 
	 * @author Xubiao
	 */
	private void initView() {
		mUserEt.addTextChangedListener(textWatcher);
		mPasswordEt.setImeActionLabel("登录", EditorInfo.IME_ACTION_DONE);
		mPasswordEt.setOnFocusChangeListener(mOnFocusChangeListener);
		mPasswordEt.setOnEditorActionListener(mOnEditerActionListener);
		mPasswordEt.addTextChangedListener(textWatcher);
		checkEnabled();
	}

	/**
	 * 帐号框焦点监听
	 */
	private OnFocusChangeListener mOnFocusChangeListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			if (hasFocus) {
			}
		}
	};
	/**
	 * 输入状态
	 */
	private OnEditorActionListener mOnEditerActionListener = new OnEditorActionListener() {

		@Override
		public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
			if (actionId == EditorInfo.IME_ACTION_DONE) {
				login();
			}
			return false;
		}
	};
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

		mAccountName = mUserEt.getText().toString().trim();
		mPassword = mPasswordEt.getText().toString().trim();
		if (TextUtils.isEmpty(mAccountName) || TextUtils.isEmpty(mPassword)) {
			loginBt.setBackgroundResource(R.drawable.login_bg_inva);
			loginBt.setEnabled(false);
		} else {
			loginBt.setBackgroundResource(R.drawable.login_bg);
			loginBt.setEnabled(true);
		}
	}

	/**
	 * 登录
	 * 
	 * @author Xubiao
	 */
	private void login() {
		if (checkInput(mAccountName, mPassword)) {
			if (AbWifiUtil.isConnectivity(this)) {
				LoginRequest.getInstance().sendRequest(mHandler, mAccountName,
						mPassword);
				AbDialogUtil
						.showProgressDialog(LoginActivity.this, 0, "登录中...");
			} else {
				AbToastUtil.showToast(this, R.string.please_check_network);
			}
		} else {

		}
	}

	/**
	 * 登录成功
	 * 
	 * @author Xubiao
	 */
	private void loginSuccess() {

		LoginInfo account = new LoginInfo();
		account.userName = mAccountName;
		account.passWord = mPassword;
		Cache.updateLoginParams(account);
		if (isBackLastPage) {
			setResult(RESULT_OK);
			finish();
		} else {
			if (Config.APPMODE == Config.BMODE) {
				AbActivityManager.getInstance().clearActivity(
						BTerminaActivity.class.getSimpleName());
				Intent intent = new Intent(LoginActivity.this,
						BTerminaActivity.class);
				startActivity(intent);
				finish();
			} else {
				AbActivityManager.getInstance().clearActivity(
						MainActivity.class.getSimpleName());
				Intent intent = new Intent(LoginActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
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
	private boolean checkInput(String account, String pwd) {
		if (TextUtils.isEmpty(account)) {
			AbToastUtil.showToast(this, "请输入账号");
			return false;
		} else if (account.length() != 11) {
			AbToastUtil.showToast(this, "账号格式不对");
			return false;
		} else if (TextUtils.isEmpty(pwd)) {
			AbToastUtil.showToast(this, "请输入密码");
			return false;
		} else if (pwd.length() < 6 || pwd.length() > 15) {
			AbToastUtil.showToast(this, "密码格式不对");
			return false;
		}
		return true;
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
		case R.id.loginBt:
			login();
			break;
		case R.id.registerBt:
			Intent intent = new Intent(LoginActivity.this,
					RegisterActivity.class);
			startActivity(intent);
			break;
		case R.id.forgetTv:
			intent = new Intent(LoginActivity.this, FindPassword1Activity.class);
			intent.putExtra("setMode", FindPassword2Activity.FINDPASSWORD);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}
