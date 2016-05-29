package com.ab.global;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;

/**
 * 
 * © 2012 amsoft.cn 名称：AbActivityManager.java
 * 描述：用于处理退出程序时可以退出所有的activity，而编写的通用类
 * 
 * @author 还如一梦中
 * @date 2015年4月10日 下午6:10:28
 * @version v1.0
 */
public class AbActivityManager {

	private List<Activity> activityList = new ArrayList<Activity>();
	private static AbActivityManager instance;

	private AbActivityManager() {
	}

	/**
	 * 单例模式中获取唯一的AbActivityManager实例.
	 * 
	 * @return
	 */
	public static AbActivityManager getInstance() {
		if (null == instance) {
			instance = new AbActivityManager();
		}
		return instance;
	}

	/**
	 * 添加Activity到容器中.
	 * 
	 * @param activity
	 */
	public void addActivity(Activity activity) {
		activityList.add(activity);
	}

	/**
	 * 移除Activity从容器中.
	 * 
	 * @param activity
	 */
	public void removeActivity(Activity activity) {
		activityList.remove(activity);
	}

	/**
	 * 遍历所有Activity并finish.
	 */
	public void clearAllActivity() {
		ArrayList<Activity> tempList = new ArrayList<Activity>();
		for (Activity activity : activityList) {
			tempList.add(activity);
		}
		for (Activity activity : tempList) {
			if (activity != null) {
				activity.finish();
			}
		}
	}

	public void clearAllOtherActivity(String activityName) {
		ArrayList<Activity> tempList = new ArrayList<Activity>();
		for (Activity activity : activityList) {
			tempList.add(activity);
		}
		for (Activity activity : tempList) {
			if (!activity.getClass().getSimpleName().equals(activityName)) {
				activity.finish();
			}
		}
	}

	public void clearActivity(String activityName) {
		ArrayList<Activity> tempList = new ArrayList<Activity>();
		for (Activity activity : activityList) {
			tempList.add(activity);
		}
		for (Activity activity : tempList) {
			if (activity.getClass().getSimpleName().equals(activityName)) {
				activity.finish();
			}
		}
	}

}