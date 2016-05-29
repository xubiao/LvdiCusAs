package com.lvdi.ruitianxia_cus.global;

import android.app.Application;
import android.content.Intent;
import cn.jpush.android.api.JPushInterface;

import com.ab.util.AbLogUtil;
import com.ab.util.AbSharedUtil;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.MainActivity;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月24日 下午5:07:11
 */
public class MyApplication extends Application implements
		Thread.UncaughtExceptionHandler {

	private static MyApplication mApplication;
	public DisplayImageOptions defaultImgOptions;

	public static MyApplication getInstance() {
		return mApplication;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		mApplication = this;
		Cache.initLoginParams();
		initImageLoader();
		if (!Config.DEBUG) {
			Thread.setDefaultUncaughtExceptionHandler(this);
		}
		AbLogUtil.debug(Config.DEBUG);
		JPushInterface.setDebugMode(Config.DEBUG); // 设置开启日志,发布时请关闭日志
		JPushInterface.init(this); // 初始化 JPush
		LocationProvider.getInstance().startLocation();
		MobclickAgent.setDebugMode(Config.DEBUG);
		MobclickAgent.openActivityDurationTrack(false);// 首先，需要在程序入口处，调用
														// MobclickAgent.openActivityDurationTrack(false)
														// 禁止默认的页面统计方式，这样将不会再自动统计Activity。
	}

	/**
	 * 判断是否是第一次启动
	 * 
	 * @return
	 * @author Xubiao
	 */
	public boolean isFirstStart() {
		return AbSharedUtil.getBoolean(getApplicationContext(),
				ShareKey.ISFIRSTSTART, true);
	}

	/**
	 * 置为非第一次使用
	 * 
	 * @author Xubiao
	 */
	public void resetFirstStart() {
		AbSharedUtil.putBoolean(getApplicationContext(), ShareKey.ISFIRSTSTART,
				false);
	}

	public void initImageLoader() {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(
				getApplicationContext());
		// config.memoryCacheExtraOptions(maxImageWidthForMemoryCache,
		// maxImageHeightForMemoryCache)
		config.threadPriority(Thread.NORM_PRIORITY - 2);
		config.threadPoolSize(10);// 线程池内加载的数量
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());
		config.diskCacheSize(50 * 1024 * 1024); // 50 MiB
		config.memoryCacheSize(50 * 1024 * 1024);
		config.memoryCache(new UsingFreqLimitedMemoryCache(50 * 1024 * 1024));// 缓存大小超过指定值时,删除最少使的bitmap
		config.discCacheFileCount(100); // 缓存的文件数量
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		config.imageDownloader(new BaseImageDownloader(getApplicationContext(),
				15000, 15000));
		// if (Config.DEBUG) {
		// config.writeDebugLogs(); // Remove for release app
		// // Initialize ImageLoader with configuration.
		// }

		ImageLoader.getInstance().init(config.build());
		defaultImgOptions = ImageLoaderHelper
				.getImageOptions(R.drawable.pic_default_bg);

	}

	@Override
	public void onTerminate() {
		super.onTerminate();
	}

	@Override
	public void uncaughtException(Thread arg0, Throwable arg1) {
		// TODO Auto-generated method stub
		Intent intent = new Intent(this, MainActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(intent);
		android.os.Process.killProcess(android.os.Process.myPid()); // 结束进程之前可以把你程序的注销或者退出代码放在这段代码之前

	}

}
