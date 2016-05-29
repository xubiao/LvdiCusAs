package com.lvdi.ruitianxia_cus.view;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.ab.global.AbActivityManager;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbViewUtil;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.BTerminaActivity;
import com.lvdi.ruitianxia_cus.activity.LoginActivity;
import com.lvdi.ruitianxia_cus.activity.MainActivity;
import com.lvdi.ruitianxia_cus.activity.NagivationActivity;
import com.lvdi.ruitianxia_cus.activity.PersonCenterActivity;
import com.lvdi.ruitianxia_cus.activity.ServiceCustomActivity;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.CustomerC;

public class MainMenuPop extends PopupWindow {
	private Context mContext;
	private View mParent;

	public MainMenuPop(Context context, View parent,
			final CustomerC mCustomerC, String activityName) {
		mContext = context;
		mParent = parent;
		View view = LayoutInflater.from(context).inflate(
				R.layout.home_menu_view, null);
		setContentView(view);
		setWidth(LayoutParams.WRAP_CONTENT);
		setHeight(LayoutParams.WRAP_CONTENT);
		// 使其聚集
		setFocusable(true);
		// 设置允许在外点击消失
		setOutsideTouchable(true);
		// 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景
		setBackgroundDrawable(new BitmapDrawable());
		// popupWindow.showAtLocation(parent, Gravity.BOTTOM, 0, 0);
		OnClickListener listener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.infoRl:
					// 个人中心
					AccountInfo accountInfo = Cache.getAccountInfo();
					if (null != accountInfo) {
						Intent intent = new Intent(mContext,
								PersonCenterActivity.class);
						mContext.startActivity(intent);
					} else {
						showLoginTip();
					}
					break;
				case R.id.customRl:
					// 服务定制
					accountInfo = Cache.getAccountInfo();
					if (null != accountInfo) {
						if (null == mCustomerC) {
							AbToastUtil.showToast(mContext, "没有可用的项目");
							break;
						}
						Intent intent = new Intent(mContext,
								ServiceCustomActivity.class);
						intent.putExtra(CustomerC.class.getSimpleName(),
								mCustomerC);
						mContext.startActivity(intent);
					} else {
						showLoginTip();
					}
					break;
				case R.id.naviRl:
					if (null == mCustomerC) {
						AbToastUtil.showToast(mContext, "没有可用的项目");
						break;
					}

					if (Config.APPMODE == Config.BMODE) {
						AbActivityManager.getInstance().clearActivity(
								BTerminaActivity.class.getSimpleName());
						Intent intent = new Intent(mContext,
								BTerminaActivity.class);
						intent.putExtra(CustomerC.class.getSimpleName(),
								mCustomerC);
						mContext.startActivity(intent);
						((Activity) mContext).finish();
						break;
					}
					Intent intent = new Intent(mContext,
							NagivationActivity.class);
					intent.putExtra(CustomerC.class.getSimpleName(), mCustomerC);
					mContext.startActivity(intent);
					break;
				default:
					break;
				}
				dismiss();
			}
		};
		view.findViewById(R.id.infoRl).setOnClickListener(listener);
		view.findViewById(R.id.customRl).setOnClickListener(listener);
		view.findViewById(R.id.naviRl).setOnClickListener(listener);
		if (activityName.equals(MainActivity.class.getSimpleName())) {
		} else {
			view.findViewById(R.id.customRl).setVisibility(View.GONE);
		}

		if (Config.APPMODE == Config.BMODE) {
			((ImageView) view.findViewById(R.id.image3))
					.setBackgroundResource(R.drawable.home_icon);
			((TextView) view.findViewById(R.id.text3)).setText("企业首页");
			view.findViewById(R.id.infoRl).setVisibility(View.GONE);
			view.findViewById(R.id.naviView).setVisibility(View.GONE);
			setHeight((int) AbViewUtil.dip2px(mContext, 48));
		}

	}

	public void show() {
		showAsDropDown(mParent, 0, 4);
	}

	/**
	 * 提示去登录
	 * 
	 * @author Xubiao
	 */
	private void showLoginTip() {
		final View mView = LayoutInflater.from(mContext).inflate(
				R.layout.dialog_confirm_view, null);
		AbDialogUtil.showAlertDialog(mView);
		((TextView) mView.findViewById(R.id.messageTv)).setText("请先登录账号");

		OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				AbDialogUtil.removeDialog(mView);
				if (v.getId() == R.id.cancelBt) {

				} else if (v.getId() == R.id.okBt) {
					Intent intent = new Intent(mContext, LoginActivity.class);
					mContext.startActivity(intent);
				}

			}
		};

		mView.findViewById(R.id.cancelBt).setOnClickListener(clickListener);
		mView.findViewById(R.id.okBt).setOnClickListener(clickListener);
	}
}
