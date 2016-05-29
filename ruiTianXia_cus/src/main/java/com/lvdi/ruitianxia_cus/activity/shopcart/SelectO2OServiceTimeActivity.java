package com.lvdi.ruitianxia_cus.activity.shopcart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.LvDiActivity;
import com.lvdi.ruitianxia_cus.constants.OrderType;
import com.lvdi.ruitianxia_cus.view.ScrollerNumberPicker;
import com.lvdi.ruitianxia_cus.view.ScrollerNumberPicker.OnSelectListener;

/**
 * @author Administrator 如果当前时间为下午2点之前 ， 则显示 10:00-11:00 11:00-12:00 12:00-13:00
 *         2点之后则显示 16:00-17:00 17:00-18:00 18:00-19:00 19:00-20:00
 */
public class SelectO2OServiceTimeActivity extends LvDiActivity {
	@AbIocView(id = R.id.time_day)
	private ScrollerNumberPicker pickerDay;
	@AbIocView(id = R.id.time_hour)
	private ScrollerNumberPicker pickerHour;
	@AbIocView(id = R.id.time_minute)
	private ScrollerNumberPicker pickerMinute;

	@AbIocView(click = "btnClick", id = R.id.saveTv)
	TextView mTvSave;
	@AbIocView(click = "btnClick", id = R.id.closeTv)
	TextView mTvClose;
	ArrayList<String> data;

	@AbIocView(click = "btnClick", id = R.id.view_empty)
	View emptey;
	@AbIocView(click = "btnClick", id = R.id.tv_1)
	TextView mTvTip;
	private int selectDayIndex = 0;
	private int selectHourIndex = 0;
	private int selectMinuteIndex = 0;
	private List<String> dayList = new ArrayList<String>();
	private List<Integer> hourList = new ArrayList<Integer>();
	private List<String> minList = new ArrayList<String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_select_o2o_service_time);
		WindowManager m = getWindowManager();
		Display d = m.getDefaultDisplay(); // 为获取屏幕宽、高
		WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
		p.width = (int) (d.getWidth() * 1.0); // 宽度设置为屏幕的
		getWindow().setAttributes(p); // 设置生效
	}

	@Override
	public void initData() {
		// TODO Auto-generated method stub
		super.initData();
		mTvTip.setText("请选择预约时间");
	
		dayList = Arrays.asList(getResources().getStringArray(
				R.array.o2o_service_day));
		pickerDay.setData(new ArrayList<String>(dayList));
		pickerDay.setDefault(selectDayIndex);

		selectHour();

	}

	@Override
	public void initListener() {
		// TODO Auto-generated method stub
		super.initListener();
		pickerDay.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {

			}

			@Override
			public void endSelect(int id, String text) {
				selectDayIndex = id;
				selectHour();
			}
		});
		pickerHour.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {

			}

			@Override
			public void endSelect(int id, String text) {
				selectHourIndex = id;
				selectMinute();
			}
		});
		pickerMinute.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {

			}

			@Override
			public void endSelect(int id, String text) {
				selectMinuteIndex = id;
			}
		});
	}

	private void selectHour() {
		hourList.clear();
		List<String> hourListStr = new ArrayList<String>();
		if (selectDayIndex == 0) {// 今天
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);
//			if (min > 30) {// 当前小时内不能预约
//				hour++;
//			}
			for (int i = hour; i < 24; i++) {
				hourListStr.add(i + "点");
				hourList.add(i);
			}
		} else {
			for (int i = 0; i < 24; i++) {
				hourListStr.add(i + "点");
				hourList.add(i);
			}
		}
		pickerHour.setData(new ArrayList<String>(hourListStr));
		if (hourListStr.size() > 0)
			pickerHour.setDefault(0);
		selectMinute();
	}

	private void selectMinute() {
		List<String> minListStr = new ArrayList<String>();
		minList.clear();
		if (selectDayIndex == 0 && selectHourIndex == 0) {// 今天
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int hour = cal.get(Calendar.HOUR_OF_DAY);
			int min = cal.get(Calendar.MINUTE);

			if (hourList.size()>0 && hourList.get(0) == hour) {
				minListStr.add("30分");
				minList.add("30");
			} else {
				minListStr.add("00分");
				minListStr.add("30分");
				minList.add("00");
				minList.add("30");
			}

		} else {
			minListStr.add("00分");
			minListStr.add("30分");
			minList.add("00");
			minList.add("30");
		}
		pickerMinute.setData(new ArrayList<String>(minListStr));
		pickerMinute.setDefault(0);
	}

	private boolean isCanYuYue() {
		if (selectDayIndex == 0 && selectHourIndex == 0){
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			int min = cal.get(Calendar.MINUTE);
			if(min>=30){
				return false;
			}
		}
		return true;
	}

	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.saveTv:
			if (selectHourIndex == -1 || selectDayIndex == -1
					|| selectMinuteIndex == -1) {
				AbToastUtil.showToast(this, "请选择预约时间");
			} else if (isCanYuYue()) {
				StringBuffer sb = new StringBuffer();
				try {
					Calendar cal = Calendar.getInstance();
					cal.setTime(new Date());
					cal.add(Calendar.DAY_OF_YEAR, selectDayIndex);
					SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
					sb.append(format.format(cal.getTime()));

					int hour = hourList.get(selectHourIndex);
					if (hour < 10) {
						sb.append("0");
					}
					sb.append(hour);
					sb.append(minList.get(selectMinuteIndex));
					sb.append("00");
				} catch (Exception e) {
					// TODO: handle exception
				}

				setResult(RESULT_OK,
						new Intent().putExtra("data", sb.toString()));
				finish();
			} else {
				AbToastUtil.showToast(this, "预约时间不能早于当前时间");
			}
			break;
		case R.id.view_empty:
		case R.id.closeTv:
			// setResult(RESULT_OK, new Intent().putExtra("data", selectTime));
			finish();
			break;

		default:
			break;
		}
	}

	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
		// setResult(RESULT_OK, new Intent().putExtra("data", selectTime));
		super.onBackPressed();
	}
}
