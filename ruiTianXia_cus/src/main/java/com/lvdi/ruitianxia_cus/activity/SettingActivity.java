package com.lvdi.ruitianxia_cus.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.activity.AbActivity;
import com.ab.global.AbActivityManager;
import com.ab.util.AbDialogUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.util.UpdateUtil;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateConfig;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

/**
 * 设置界面 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月29日 下午10:44:38
 */
public class SettingActivity extends LvDiActivity {
	public static final int FROM_SETTING = 1;
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(click = "btnClick", id = R.id.logoutBt)
	// 注销
	Button logoutBt;
	@AbIocView(click = "btnClick", id = R.id.editPasswordRl)
	// 修改密码
	RelativeLayout mEditPasswordRl;
	@AbIocView(click = "btnClick", id = R.id.callRl)
	// 服务热线
	RelativeLayout mCallRl;
	@AbIocView(click = "btnClick", id = R.id.aboutRl)
	// 关于
	RelativeLayout mAboutRl;
	@AbIocView(click = "btnClick", id = R.id.releaseRl)
	// 当前版本
	RelativeLayout mReleaseRl;
	@AbIocView(id = R.id.versionTv)
	// 版本
	TextView mVersionTv;
	@AbIocView(click = "btnClick", id = R.id.protocolRl)
	// 企业隐私条款
	RelativeLayout mProtocolRl;

	@AbIocView(click = "btnClick", id = R.id.mzsmRl)
	// 免责声明
	RelativeLayout mMzsmRl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_setting);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("设置");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.releaseRl:
			UpdateUtil.UmengCheckUpdate(SettingActivity.this);
			break;
		case R.id.aboutRl:
			Intent intent = new Intent(this, GuideActivity.class);
			intent.putExtra("from", SettingActivity.class.getSimpleName());
			startActivity(intent);

			break;
		case R.id.callRl:
			// 用intent启动拨打电话
			// intent = new Intent(Intent.ACTION_DIAL,
			// Uri.parse("tel://400400400"));// ACTION_CALL
			intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"
					+ Config.CONTACT));// ACTION_CALL
			startActivity(intent);
			break;
		case R.id.editPasswordRl:
			intent = new Intent(SettingActivity.this,
					FindPassword1Activity.class);
			intent.putExtra("setMode", FindPassword2Activity.RESETPASSWORD);
			startActivity(intent);
			break;
		case R.id.logoutBt:

			final View mView = mInflater.inflate(R.layout.dialog_confirm_view,
					null);
			AbDialogUtil.showAlertDialog(mView);
			((TextView) mView.findViewById(R.id.messageTv)).setText("确定退出?");

			OnClickListener dialogClickListener = new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					AbDialogUtil.removeDialog(mView);
					if (v.getId() == R.id.cancelBt) {
					} else if (v.getId() == R.id.okBt) {
						logoutSuccess();
					}

				}
			};
			mView.findViewById(R.id.cancelBt).setOnClickListener(
					dialogClickListener);
			mView.findViewById(R.id.okBt).setOnClickListener(
					dialogClickListener);

			break;
		case R.id.protocolRl:
			intent = new Intent(SettingActivity.this, ProtocolActivity.class);
			intent.putExtra("title", "企业隐私条款");
			intent.putExtra("url", "file:///android_asset/html/qyystk.html");
			startActivity(intent);
			break;
		case R.id.mzsmRl:
			intent = new Intent(SettingActivity.this, ProtocolActivity.class);
			intent.putExtra("title", "免责声明");
			intent.putExtra("url", "file:///android_asset/html/mzsm.html");
			startActivity(intent);
			break;
		default:
			break;
		}
	}

	/**
	 * 注销成功
	 * 
	 * @author Xubiao
	 */
	private void logoutSuccess() {
		Cache.clearLoginParams();
		AbActivityManager.getInstance().clearActivity(
				MainActivity.class.getSimpleName());
		Intent intent = new Intent(SettingActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
	}

	private void initView() {
		String versionString = getVersion();
		if (!TextUtils.isEmpty(versionString))
			mVersionTv.setText("当前版本 V" + getVersion());
	}

	private String getVersion() {
		try {
			PackageManager manager = this.getPackageManager();
			PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
			String version = info.versionName;
			return version;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
}
