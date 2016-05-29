package com.lvdi.ruitianxia_cus.global;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;

import cn.jpush.android.api.JPushInterface;

import com.ab.util.AbFileUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbSharedUtil;
import com.baidu.location.BDLocation;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.LoginInfo;
import com.lvdi.ruitianxia_cus.model.ProjectForC;

/**
 * 
 * 类的详细描述： 缓存
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午1:29:08
 */
public class Cache {

	// 登录信息
	private static LoginInfo mUser = null;
	private static AccountInfo mAccountInfo = null;
	private static BDLocation mBdLocation = null;

	/**
	 * 
	 * @return
	 * @author Xubiao
	 */
	public static LoginInfo getUser() {
		if (null == mUser) {
			initLoginParams();
		}
		return mUser;
	}

	/**
	 * 上次登录参数
	 */
	public static void initLoginParams() {
		String userJson = AbSharedUtil.getString(MyApplication.getInstance(),
				ShareKey.LOGININFO, "");
		if (!TextUtils.isEmpty(userJson)) {
			mUser = (LoginInfo) AbJsonUtil.fromJson(userJson, LoginInfo.class);
		}
	}

	public static void initAccountParams() {
		String accountJson = AbSharedUtil.getString(
				MyApplication.getInstance(), ShareKey.ACCOUNTINFO, "");
		if (!TextUtils.isEmpty(accountJson)) {
			mAccountInfo = (AccountInfo) AbJsonUtil.fromJson(accountJson,
					AccountInfo.class);
		}
	}

	/**
	 * 
	 * @param user
	 * @author Xubiao
	 */
	public static void updateLoginParams(LoginInfo user) {
		if (null != user) {
			mUser = user;
			AbSharedUtil.putString(MyApplication.getInstance(),
					ShareKey.LOGININFO, mUser.toJson());
		}
	}

	/**
	 * 清空上次登录信息
	 */
	public static void clearLoginParams() {
		mUser = null;
		mAccountInfo = null;
		AbSharedUtil.putString(MyApplication.getInstance(), ShareKey.LOGININFO,
				null);
		AbSharedUtil.putString(MyApplication.getInstance(),
				ShareKey.ACCOUNTINFO, null);
		JPushInterface.setAliasAndTags(MyApplication.getInstance(), "", null,
				null);

	}

	public static void updataAccountParams(AccountInfo accountInfo) {
		if (null != accountInfo) {
			mAccountInfo = accountInfo;
			AbSharedUtil.putString(MyApplication.getInstance(),
					ShareKey.ACCOUNTINFO, mAccountInfo.toJson());
		}
	}

	public static AccountInfo getAccountInfo() {
		if (null == mAccountInfo) {
			initAccountParams();
		}
		return mAccountInfo;
	}

	public static void setLocation(BDLocation location) {
		if (null != location) {
			mBdLocation = location;
			AbSharedUtil.putString(MyApplication.getInstance(),
					ShareKey.BAIDULOCATION, AbJsonUtil.toJson(mBdLocation));
		}
	}

	public static BDLocation getLocation() {
		if (mBdLocation == null) {
			mBdLocation = (BDLocation) AbJsonUtil.fromJson(AbSharedUtil
					.getString(MyApplication.getInstance(),
							ShareKey.BAIDULOCATION, null), BDLocation.class);
		}
		return mBdLocation;
	}

	public static Drawable defaultPhoto(Context context) {
		File FILE_LOCAL = new File(AbFileUtil.getImageDownloadDir(context));
		if (FILE_LOCAL.exists()) {
			Bitmap bitmap = BitmapFactory.decodeFile(FILE_LOCAL.getPath()
					+ File.separator + "." + Cache.getAccountInfo().partyId
					+ Config.CROPFILENAME);
			if (null != bitmap) {
				return new BitmapDrawable(context.getResources(), bitmap);
			}
		}
		return null;
	}

	public static ProjectForC getSelectProjectForC() {
		String partyId = getAccountInfo() == null ? "" : mAccountInfo.partyId;
		String selectProjectString = AbSharedUtil.getString(
				MyApplication.getInstance(), ShareKey.SELECTPROJECT + partyId,
				"");
		if (!TextUtils.isEmpty(selectProjectString)) {
			return (ProjectForC) AbJsonUtil.fromJson(selectProjectString,
					ProjectForC.class);
		}
		return null;
	}

	public static void putSelectProjectForC(ProjectForC selectProjectForC) {
		if (null != selectProjectForC) {
			String partyId = getAccountInfo() == null ? ""
					: mAccountInfo.partyId;
			AbSharedUtil.putString(MyApplication.getInstance(),
					ShareKey.SELECTPROJECT + partyId,
					selectProjectForC.toJson());
		}
	}
}
