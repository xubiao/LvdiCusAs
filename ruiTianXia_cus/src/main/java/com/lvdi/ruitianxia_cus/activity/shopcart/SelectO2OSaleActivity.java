package com.lvdi.ruitianxia_cus.activity.shopcart;

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
 * @author Administrator 如果当前时间为下午2点之前 ，
 *  则显示 10:00-11:00 11:00-12:00 12:00-13:00
 *         2点之后则显示 16:00-17:00 17:00-18:00 18:00-19:00 19:00-20:00
 */
public class SelectO2OSaleActivity extends LvDiActivity {
	@AbIocView(id = R.id.timeLl)
	private ScrollerNumberPicker picker;
	@AbIocView(click = "btnClick", id = R.id.saveTv)
	TextView mTvSave;
	@AbIocView(click = "btnClick", id = R.id.closeTv)
	TextView mTvClose;
	ArrayList<String> data;

	@AbIocView(click = "btnClick", id = R.id.view_empty)
	View emptey;
	@AbIocView(click = "btnClick", id = R.id.tv_1)
	TextView mTvTip;
	private String selectTime="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_select_exp_time);
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
		if(getIntent().hasExtra("orderType")){
			String orderType = getIntent().getStringExtra("orderType");
			if(orderType.equals(OrderType.SALES_ORDER_O2O_SALE.toString())){
				mTvTip.setText("请选择配送时间");
			}else if(orderType.equals(OrderType.SALES_ORDER_O2O_SERVICE.toString())){
				mTvTip.setText("请选择预约时间");
			}
		}
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		
//1、如果当前时间为下午13:30之前，则显示11:00-12:00、12:00-13:00、13:00-14:00；
//		 2、13:30之后则显示14:00-15:00、15:00-16:00、16:00-17:00、17:00-18:00、18:00-19:00、19:00-20:00、20:00-21:00
//		 3、以半小时为中间时刻，如当前是11:29，则显示11:00-12:00、12:00-13:00、13:00-14:00；如当前是11:31，则显示12:00-13:00、13:00-14:00
		List<String> strList = null;
		if (hour < 13 || (hour == 13 && min <30) ) {//下午13:30之前
//			strList = Arrays.asList(getResources().getStringArray(
//					R.array.array_time_o2o_sale_hour));
			strList = new ArrayList<String>();
			for(int i=11;i<14;i++){
				if(i>hour ||(i==hour && min<30)){
					strList.add(i+":00-"+(i+1)+":00");
				}
			}
		}else if(hour < 20 || (hour == 20 && min <30)){// 
			strList = new ArrayList<String>();
			for(int i=14;i<21;i++){
				if(i>hour ||(i==hour && min<30)){
					strList.add(i+":00-"+(i+1)+":00");
				}
			}
		}else {
			AbToastUtil.showToast(this, "不好意思，当前时间不在配送时间范围内!");
		}
		if (strList != null) {
			selectTime = strList.get(0);
			picker.setData(new ArrayList<String>(strList));
			picker.setDefault(0);
		}else{
			picker.setVisibility(View.GONE);
		}

		picker.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {

			}

			@Override
			public void endSelect(int id, String text) {
				selectTime = text;
			}
		});
	}

	private boolean isCanYuYue(){
		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		int min = cal.get(Calendar.MINUTE);
		
	    int selectEndHour = Integer.parseInt(selectTime.substring(0, 2));
	    if(hour < selectEndHour || (hour == selectEndHour && min<30)){
	    	return true;
	    }
		return false;
	}
	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.saveTv:
			if(TextUtils.isEmpty(selectTime)){
				AbToastUtil.showToast(this, "请选择预约时间");
			}else if(isCanYuYue()){
				setResult(RESULT_OK, new Intent().putExtra("data", selectTime));
				finish();
			}else{
				AbToastUtil.showToast(this, "预约时间不能早于当前时间");
			}
			break;
		case R.id.view_empty:
		case R.id.closeTv:
//			setResult(RESULT_OK, new Intent().putExtra("data", selectTime));
			finish();
			break;

		default:
			break;
		}
	}
	@Override
	public void onBackPressed() {
		// TODO Auto-generated method stub
//		setResult(RESULT_OK, new Intent().putExtra("data", selectTime));
		super.onBackPressed();
	}
}
