package com.lvdi.ruitianxia_cus.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.request.GetPersonInfoRequest;
import com.lvdi.ruitianxia_cus.request.UpdataCustomerHeadIconRequest;
import com.lvdi.ruitianxia_cus.request.UpdataCustomerInfoRequest;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.lvdi.ruitianxia_cus.util.InputUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;

/**
 * 
 * 类的详细描述：个人信息
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午5:25:37
 */
public class PersonInfoActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	// 修改头像
	private final int GOTO_EDIT_PHOTO = 1;
	@AbIocView(id = R.id.photoIv)
	ImageView mPhotoIv;// 头像
	@AbIocView(id = R.id.ninckTv)
	TextView mNickTv;// 昵称
	@AbIocView(id = R.id.sexTv)
	TextView mSexTv;// 性别
	@AbIocView(id = R.id.phoneTv)
	TextView mPhoneTv;// 手机
	@AbIocView(id = R.id.compTv)
	TextView mCompTv;// 公司名称
	@AbIocView(id = R.id.locationTv)
	TextView mLocationTv;// 公司地址
	@AbIocView(id = R.id.officeTv)
	TextView mOfficeTv;// 公司职务

	@AbIocView(click = "btnClick", id = R.id.photoRl)
	RelativeLayout mPhotoRl;// 头像区域
	@AbIocView(click = "btnClick", id = R.id.nickRl)
	RelativeLayout mNinckRl;// 昵称区域
	@AbIocView(click = "btnClick", id = R.id.sexRl)
	RelativeLayout mSexRl;// 性别
	@AbIocView(click = "btnClick", id = R.id.phoneRl)
	RelativeLayout mPhoneRl;// 手机号
	@AbIocView(click = "btnClick", id = R.id.compRl)
	RelativeLayout mCompRl;// 公司名称
	@AbIocView(click = "btnClick", id = R.id.locationRl)
	RelativeLayout mLocationRl;// 公司地址
	@AbIocView(click = "btnClick", id = R.id.officeRl)
	RelativeLayout mOfficeRl;// 公司职务
	/**
	 * 标识信息是否修改了
	 */
	private boolean editInfo;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(PersonInfoActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_GET_CUSTOMER_INFO_SUCC:
				updataData(false);
				break;
			case HandleAction.HttpType.HTTP_GET_CUSTOMER_INFO_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_SET_PERSON_INFO_SUCC:
				updataData(false);
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				editInfo = true;
				break;
			case HandleAction.HttpType.HTTP_SET_PERSON_INFO_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_HEAD_ICON_SUCC:
				updateHead();
				AbDialogUtil.removeDialog(PersonInfoActivity.this);
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				editInfo = true;
				break;
			case HandleAction.HttpType.HTTP_UPDATE_CUSTOMER_HEAD_ICON_FAIL:
				AbDialogUtil.removeDialog(PersonInfoActivity.this);
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
		setAbContentView(R.layout.activity_person_info);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("个人信息");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		updataData(true);
	}

	private void initView() {
		mAbTitleBar.getLogoView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onBack();
			}
		});
	}

	/**
	 * 第一次进入
	 * 
	 * @param firstLoad
	 * @author Xubiao
	 */
	private void updataData(boolean firstLoad) {
		AccountInfo accountInfo = Cache.getAccountInfo();
		if (null != accountInfo) {
			Drawable defaultPhoto = Cache.defaultPhoto(this);
			if (null != defaultPhoto) {
				ImageLoaderHelper.displayImage(accountInfo.headIconPath,
						mPhotoIv, defaultPhoto);
			} else {
				ImageLoaderHelper.displayImage(accountInfo.headIconPath,
						mPhotoIv, R.drawable.person_photo_def);
			}
			mNickTv.setText(accountInfo.nickName);
			String gender = "";
			if ("M".equals(accountInfo.gender)) {
				gender = "男";
			} else if ("F".equals(accountInfo.gender)) {
				gender = "女";
			}
			mSexTv.setText(gender);
			mPhoneTv.setText(Cache.getUser().userName);
			mCompTv.setText(accountInfo.companyName);
			mLocationTv.setText(accountInfo.companyAddress);
			mOfficeTv.setText(accountInfo.positionInCompany);
			if (firstLoad) {
				if (AbWifiUtil.isConnectivity(this)) {
					GetPersonInfoRequest.getInstance().sendRequest(mHandler,
							accountInfo.partyId);
					AbDialogUtil.showProgressDialog(PersonInfoActivity.this, 0,
							"获取个人信息中...");
				} else {
					AbToastUtil.showToast(this, R.string.please_check_network);
				}
			}
		}

	}

	/**
	 * 更新头像
	 * 
	 * @author Xubiao
	 */
	private void updateHead() {
		AccountInfo accountInfo = Cache.getAccountInfo();
		if (null != accountInfo) {
			try {
				if (null != DiskCacheUtils.findInCache(
						accountInfo.headIconPath, ImageLoader.getInstance()
								.getDiskCache()))
					DiskCacheUtils.removeFromCache(accountInfo.headIconPath,
							ImageLoader.getInstance().getDiskCache());
			} catch (Exception e) {

				e.printStackTrace();
			}
			try {
				if (null != MemoryCacheUtils.findCachedBitmapsForImageUri(
						accountInfo.headIconPath, ImageLoader.getInstance()
								.getMemoryCache())) {
					MemoryCacheUtils.removeFromCache(accountInfo.headIconPath,
							ImageLoader.getInstance().getMemoryCache());
				}
			} catch (Exception e) {

				e.printStackTrace();
			}
			Drawable defaultPhoto = Cache.defaultPhoto(this);
			if (null != defaultPhoto) {
				ImageLoaderHelper.displayImage(accountInfo.headIconPath,
						mPhotoIv, defaultPhoto);
			} else {
				ImageLoaderHelper.displayImage(accountInfo.headIconPath,
						mPhotoIv, R.drawable.person_photo_def);
			}
		}
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.photoRl:
			// 去图片选择界面
			Intent intent = new Intent(PersonInfoActivity.this,
					PicSelectActivity.class);
			startActivityForResult(intent, GOTO_EDIT_PHOTO);
			break;
		case R.id.nickRl:
			showEditNickNameDialog();
			break;
		case R.id.sexRl:
			showEditSexDialog();
			break;
		case R.id.phoneRl:
			break;
		case R.id.compRl:
			break;
		case R.id.locationRl:
			break;
		case R.id.officeRl:
			break;
		default:
			break;
		}
	}

	/**
	 * 展示修改昵称对话框
	 * 
	 * @author Xubiao
	 */
	private void showEditNickNameDialog() {
		final View mView = mInflater.inflate(
				R.layout.dialog_edit_nickname_view, null);
		AbDialogUtil.showAlertDialog(mView);
		String nick = mNickTv.getText().toString();
		final EditText inputEditText = ((EditText) mView
				.findViewById(R.id.inputEt));
		inputEditText.setText(nick);
		InputUtil.filterName(inputEditText);
		inputEditText.setSelection(inputEditText.getText().length());
		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.cancelBt) {
					AbDialogUtil.removeDialog(mView);
				} else if (v.getId() == R.id.okBt) {
					if (AbWifiUtil.isConnectivity(PersonInfoActivity.this)) {
						AccountInfo accountInfo = Cache.getAccountInfo();
						String input = inputEditText.getText().toString();
						if (!TextUtils.isEmpty(input)) {
							UpdataCustomerInfoRequest.getInstance()
									.sendRequest(mHandler, accountInfo.partyId,
											"nickname", input);
							AbDialogUtil.showProgressDialog(
									PersonInfoActivity.this, 0, "修改个人信息中...");
						} else {
							AbToastUtil.showToast(PersonInfoActivity.this,
									"昵称不能为空");
							return;
						}
						AbDialogUtil.removeDialog(mView);
					} else {
						AbToastUtil.showToast(PersonInfoActivity.this,
								R.string.please_check_network);
					}

				} else if (v.getId() == R.id.clearIv) {
					((EditText) mView.findViewById(R.id.inputEt)).setText("");
				}
			}
		};

		mView.findViewById(R.id.cancelBt).setOnClickListener(clickListener);
		mView.findViewById(R.id.okBt).setOnClickListener(clickListener);
		mView.findViewById(R.id.clearIv).setOnClickListener(clickListener);

	}

	/**
	 * 展示修改性别对话框
	 * 
	 * @author Xubiao
	 */
	private void showEditSexDialog() {
		final View mView2 = mInflater.inflate(R.layout.dialog_edit_sex_view,
				null);
		AbDialogUtil.showFullScreenDialog(mView2);
		OnClickListener clickListener2 = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (v.getId() == R.id.maleRl || v.getId() == R.id.femalRl) {// 选择男/女
					if (AbWifiUtil.isConnectivity(PersonInfoActivity.this)) {
						String gender = (v.getId() == R.id.maleRl ? "M" : "F");
						AccountInfo accountInfo = Cache.getAccountInfo();
						if (!gender.equals(accountInfo.gender)) {
							UpdataCustomerInfoRequest.getInstance()
									.sendRequest(mHandler, accountInfo.partyId,
											"gender", gender);
							AbDialogUtil.showProgressDialog(
									PersonInfoActivity.this, 0, "修改个人信息中...");
						}
					} else {
						AbToastUtil.showToast(PersonInfoActivity.this,
								R.string.please_check_network);
					}

				} else if (v.getId() == R.id.cancelRl) {

				} else if (v.getId() == R.id.topView) {

				}
				AbDialogUtil.removeDialog(mView2);
			}
		};
		mView2.findViewById(R.id.maleRl).setOnClickListener(clickListener2);
		mView2.findViewById(R.id.femalRl).setOnClickListener(clickListener2);
		mView2.findViewById(R.id.cancelRl).setOnClickListener(clickListener2);
		mView2.findViewById(R.id.topView).setOnClickListener(clickListener2);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		// 从截图界面回来
		case GOTO_EDIT_PHOTO:
			String path = mIntent.getStringExtra("PATH");
			if (AbWifiUtil.isConnectivity(this)) {
				UpdataCustomerHeadIconRequest.getInstance().sendRequest(
						mHandler, Cache.getAccountInfo().partyId, path);
				AbDialogUtil.showProgressDialog(PersonInfoActivity.this, 0,
						"修改头像中...");
			} else {
				AbToastUtil.showToast(this, R.string.please_check_network);
			}
			break;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	private void onBack() {
		if (editInfo) {
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
		}
		finish();
	}
}
