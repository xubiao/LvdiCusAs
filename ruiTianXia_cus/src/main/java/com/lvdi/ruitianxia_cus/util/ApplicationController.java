package com.lvdi.ruitianxia_cus.util;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;

import com.ab.util.AbToastUtil;
import com.lvdi.ruitianxia_cus.activity.ExpressQueryActivity;
import com.lvdi.ruitianxia_cus.activity.RandomProductActivity;
import com.lvdi.ruitianxia_cus.activity.UnifyWebViewActivity;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月19日 下午8:09:46
 */
public class ApplicationController {
	public static void responseController(Context context,
			Application application, String className) {
		if (TextUtils.isEmpty(application.visitType))
			return;
		if (application.visitType.equals("H5")) {
			// H5页面
			Config.CATALOGID = application.catalogId;
			Config.ORDER_TYPE = application.orderType;
			String url = application.protocol + application.visitkeyword;
			AccountInfo accountInfo = Cache.getAccountInfo();
			String partyId = (accountInfo != null ? accountInfo.partyId : "");
			url += ("&partyId=" + partyId + "&organizationId=" + Config.selectCustomerC.selectedProject.organizationId);
			Intent intent = UnifyWebViewActivity.toWebPage(context, url, "",
					application.appName);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(intent);
		} else {
			// 原生页面
			if (TextUtils.isEmpty(application.visitkeyword))
				return;
			if (application.visitkeyword.equals("yyy")) {
				// 摇一摇
				Intent intent = new Intent(context, RandomProductActivity.class);
				context.startActivity(intent);
			} else if (application.visitkeyword.equals("kdcx")) {
				// 快递查询
				// Intent intent = new Intent(context,
				// ExpressQueryActivity.class);
				// context.startActivity(intent);

				Intent intent = UnifyWebViewActivity.toWebPage(context,
						"http://m.kuaidi100.com/", "", application.appName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} else if (application.visitkeyword.equals("wyjkd")) {
				Intent intent = UnifyWebViewActivity
						.toWebPage(
								context,
								"http://www.sf-express.com/mobile/cn/sc/dynamic_functions/ship/ship.html?isappinstalled=1",
								"", application.appName);
				intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startActivity(intent);
			} else {
				AbToastUtil.showToast(context, "该功能还未启用,敬请期待!");
			}
		}

	}
}
