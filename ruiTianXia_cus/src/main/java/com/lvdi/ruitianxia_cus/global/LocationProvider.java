package com.lvdi.ruitianxia_cus.global;

import android.content.Context;

import com.ab.util.AbLogUtil;
import com.ab.util.AbToastUtil;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;

/**
 * 
 * 类的详细描述： 百度定位
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月24日 下午4:00:03
 */
public class LocationProvider {

	private Context context;
	private LocationClient locationClient = null;
	private MyBDListener bDListener = new MyBDListener();
	private LocationListener listener;

	private static LocationProvider locationProvider;

	public static LocationProvider getInstance() {
		if (null == locationProvider) {
			locationProvider = new LocationProvider(MyApplication.getInstance()
					.getApplicationContext());
		}
		return locationProvider;
	}

	public LocationProvider(Context context) {
		super();
		this.context = context;
	}

	public void startLocation() {
		locationClient = new LocationClient(context);
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		int span = 1000 * 10;
		option.setScanSpan(span);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		locationClient.setLocOption(option);
		locationClient.registerLocationListener(bDListener);
		locationClient.start();
		AbLogUtil.e(getClass(), "startLocation");
	}

	/**
	 * 停止，减少资源消耗
	 */
	public void stopListener() {
		if (locationClient != null && locationClient.isStarted()) {
			locationClient.stop();
			locationClient = null;
		}
	}

	/**
	 * 更新位置并保存到SItude中
	 */
	public void updateListener() {
		if (locationClient != null && locationClient.isStarted()) {
			locationClient.requestLocation();
		}
	}

	public LocationListener getListener() {
		return listener;
	}

	public void setListener(LocationListener listener) {
		this.listener = listener;
	}

	private class MyBDListener implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {

			if (location.getLocType() == BDLocation.TypeGpsLocation) {// GPS定位结果
				AbLogUtil.e(getClass(), "GPS 定位成功");

				Cache.setLocation(location);
				stopListener();
			} else if (location.getLocType() == BDLocation.TypeNetWorkLocation) {// 网络定位结果
				AbLogUtil.e(getClass(), "网络定位成功");
				Cache.setLocation(location);
				stopListener();
			} else if (location.getLocType() == BDLocation.TypeOffLineLocation) {// 离线定位结果
				AbLogUtil.e(getClass(), "离线定位成功");
				Cache.setLocation(location);
				stopListener();
			} else if (location.getLocType() == BDLocation.TypeServerError) {
				// sb.append("\ndescribe : ");
				// sb.append("服务端网络定位失败，可以反馈IMEI号和大体定位时间到loc-bugs@baidu.com，会有人追查原因");
				AbToastUtil.showToast(MyApplication.getInstance()
						.getApplicationContext(), "服务端网络定位失败");
			} else if (location.getLocType() == BDLocation.TypeNetWorkException) {
				// sb.append("\ndescribe : ");
				// sb.append("网络不同导致定位失败，请检查网络是否通畅");
				AbToastUtil.showToast(MyApplication.getInstance()
						.getApplicationContext(), "网络不通导致定位失败，请检查网络是否通畅");
			} else if (location.getLocType() == BDLocation.TypeCriteriaException) {
				// sb.append("\ndescribe : ");
				// sb.append("无法获取有效定位依据导致定位失败，一般是由于手机的原因，处于飞行模式下一般会造成这种结果，可以试着重启手机");
				AbToastUtil.showToast(MyApplication.getInstance()
						.getApplicationContext(), "无法获取有效定位依据导致定位失败");
			}

			AbLogUtil.d(getClass(), "Latitude:" + location.getLatitude()
					+ "-Longitude:" + location.getLongitude());
		}
	}

	public interface LocationListener {
		public void onReceiveLocation(BDLocation location);
	}
}
