package com.lvdi.ruitianxia_cus.util;

import android.content.Context;
import android.widget.Toast;

import com.lvdi.ruitianxia_cus.global.MyApplication;
import com.umeng.update.UmengUpdateAgent;
import com.umeng.update.UmengUpdateListener;
import com.umeng.update.UpdateConfig;
import com.umeng.update.UpdateResponse;
import com.umeng.update.UpdateStatus;

public class UpdateUtil {

	public static void AutoUmengUpdate(final Context context) {
		UpdateConfig.setDebug(true);
		UmengUpdateAgent.setDefault();
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					UmengUpdateAgent.showUpdateDialog(
							MyApplication.getInstance(), updateInfo);
					break;
				case UpdateStatus.No: // has no update
					break;
				case UpdateStatus.NoneWifi: // none wifi
					break;
				case UpdateStatus.Timeout: // time out
					break;
				}
			}
		});
		UmengUpdateAgent.update(context);
	}

	public static void UmengCheckUpdate(final Context context) {
		UpdateConfig.setDebug(true);
		UmengUpdateAgent.setDefault();
		UmengUpdateAgent.forceUpdate(context);
		UmengUpdateAgent.setUpdateAutoPopup(false);
		UmengUpdateAgent.setUpdateOnlyWifi(false);
		UmengUpdateAgent.setUpdateListener(new UmengUpdateListener() {
			@Override
			public void onUpdateReturned(int updateStatus,
					UpdateResponse updateInfo) {
				switch (updateStatus) {
				case UpdateStatus.Yes: // has update
					UmengUpdateAgent.showUpdateDialog(
							MyApplication.getInstance(), updateInfo);
					break;
				case UpdateStatus.No: // has no update
					Toast.makeText(context, "没有发现新版本", Toast.LENGTH_SHORT)
							.show();
					break;
				case UpdateStatus.NoneWifi: // none wifi
					Toast.makeText(context, "没有wifi连接， 只在wifi环境下自动更新",
							Toast.LENGTH_SHORT).show();
					break;
				case UpdateStatus.Timeout: // time out
					Toast.makeText(context, "超时", Toast.LENGTH_SHORT).show();
					break;
				}
			}
		});
		UmengUpdateAgent.update(context);
	}
}
