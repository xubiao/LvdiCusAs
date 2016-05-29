package com.lvdi.ruitianxia_cus.util;

import com.lvdi.ruitianxia_cus.global.MyApplication;
import com.umeng.analytics.MobclickAgent;

public class UMengMobclickAgent {
	public class EventId {
		public final static String LOGIN = "login";
		public final static String REGISTER = "register";
	}

	public static void onEventValue(String id) {
		MobclickAgent.onEvent(MyApplication.getInstance()
				.getApplicationContext(), id);
	}
}
