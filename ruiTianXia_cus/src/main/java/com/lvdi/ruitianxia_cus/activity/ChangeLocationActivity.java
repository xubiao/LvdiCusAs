package com.lvdi.ruitianxia_cus.activity;

import java.util.List;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.activity.AbActivity;
import com.ab.global.AbActivityManager;
import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.app.AbPopoverView;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.ProjectForC;
import com.lvdi.ruitianxia_cus.request.GetProjectsForCRequest;

/**
 * 切换位置项目 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月4日 下午9:13:22
 */
public class ChangeLocationActivity extends LvDiActivity {
	public static final int CITYMODE = 1;
	public static final int PROJECTMODE = 2;
	private AbTitleBar mAbTitleBar = null;
	/**
	 * 选择城市布局
	 */
	@AbIocView(click = "btnClick", id = R.id.selectCityRl)
	RelativeLayout mSelectCityRl;
	/**
	 * 选择项目布局
	 */
	@AbIocView(click = "btnClick", id = R.id.selectProjectRl)
	RelativeLayout mSelectProjectRl;
	/**
	 * 确定按钮
	 */
	@AbIocView(click = "btnClick", id = R.id.okBt)
	Button mOkButton;
	/**
	 * 城市显示控件
	 */
	@AbIocView(id = R.id.selectCityTv)
	TextView mSelectCityTv;
	/**
	 * 项目显示控件
	 */
	@AbIocView(id = R.id.selectProjectTv)
	TextView mSelectProjectTv;
	/**
	 * 弹出的列表展示pop
	 */
	private PopupWindow mPopupWindow;
	/**
	 * 城市列表
	 */
	private List<ProjectForC> mCits;
	/**
	 * 项目列表
	 */
	private List<ProjectForC> mProjects;
	/**
	 * 选中的城市
	 */
	private ProjectForC mSelectCity;
	/**
	 * 选中的项目
	 */
	private ProjectForC mSelectProject;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(ChangeLocationActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_GET_POJECTSFORC_SUCC:
				int requestMode = msg.arg1;
				if (requestMode == CITYMODE) {
					mCits = (List<ProjectForC>) msg.obj;
					loadCitys(0);
					requestProject(mSelectCity);
				} else {
					mProjects = (List<ProjectForC>) msg.obj;
					loadProjects(0);
				}
				break;
			case HandleAction.HttpType.HTTP_GET_POJECTSFORC_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_change_location);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("位置切换");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		requectVity();
	}

	/**
	 * 
	 * 
	 * @author Xubiao
	 */
	private void initView() {
		mOkButton.setBackgroundResource(R.drawable.login_bg_inva);
		mOkButton.setEnabled(false);
	}

	/**
	 * 请求城市列表
	 * 
	 * @author Xubiao
	 */
	private void requectVity() {
		if (AbWifiUtil.isConnectivity(ChangeLocationActivity.this)) {
			GetProjectsForCRequest.getInstance().sendRequest(mHandler, "city",
					"0", CITYMODE + "");
			AbDialogUtil.showProgressDialog(ChangeLocationActivity.this, 0,
					"获取城市列表中...");
		} else {
			AbToastUtil.showToast(ChangeLocationActivity.this,
					R.string.please_check_network);
		}

	}

	/**
	 * 请求项目列表
	 * 
	 * @param cityid
	 * @author Xubiao
	 */
	private void requestProject(ProjectForC projectForC) {
		if (AbWifiUtil.isConnectivity(ChangeLocationActivity.this)) {
			GetProjectsForCRequest.getInstance().sendRequest(mHandler,
					"project", projectForC.organizationId + "",
					PROJECTMODE + "");
			AbDialogUtil.showProgressDialog(ChangeLocationActivity.this, 0,
					"获取项目列表中...");
		} else {
			AbToastUtil.showToast(ChangeLocationActivity.this,
					R.string.please_check_network);
		}
	}

	/**
	 * 加载城市
	 * 
	 * @author Xubiao
	 */
	private void loadCitys(int location) {
		mSelectCity = mCits.get(location);
		mSelectCityTv.setText(mSelectCity.name);
	}

	/**
	 * 
	 * 加载项目
	 * 
	 * @author Xubiao
	 */
	private void loadProjects(int location) {
		mSelectProject = mProjects.get(location);
		mSelectProjectTv.setText(mSelectProject.name);
		mOkButton.setBackgroundResource(R.drawable.login_bg);
		mOkButton.setEnabled(true);
	}

	/**
	 * 
	 * @param v
	 * @author Xubiao
	 */
	public void btnClick(View v) {
		switch (v.getId()) {
		case R.id.selectCityRl:
			showCityPopu();
			break;
		case R.id.selectProjectRl:
			showProjectPopu();
			break;
		case R.id.okBt:
			if (null == mSelectProject) {
				break;
			}
			Cache.putSelectProjectForC(mSelectProject);
			// Intent intent = new Intent();
			// intent.putExtra("selectProject", mSelectProject);
			// setResult(RESULT_OK, intent);
			// finish();
			AbActivityManager.getInstance().clearActivity(
					MainActivity.class.getSimpleName());
			Intent intent = new Intent(ChangeLocationActivity.this,
					MainActivity.class);
			startActivity(intent);
			finish();
			break;
		default:
			break;
		}
	}

	/**
	 * 城市列表
	 * 
	 * @author Xubiao
	 */
	private void showCityPopu() {
		View popoverContentView = mInflater.inflate(
				R.layout.change_location_pop, null);
		ListView listView = (ListView) popoverContentView
				.findViewById(R.id.listview);
		ListAdapter adapter = new ListAdapter(mCits);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				loadCitys(arg2);
				requestProject(mSelectCity);
				mPopupWindow.dismiss();
			}
		});

		mPopupWindow = new PopupWindow(popoverContentView,
				mSelectCityRl.getWidth(), LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.showAsDropDown(mSelectCityRl, 0, 0);
	}

	/**
	 * 项目列表
	 * 
	 * @author Xubiao
	 */
	private void showProjectPopu() {
		View popoverContentView = mInflater.inflate(
				R.layout.change_location_pop, null);
		ListView listView = (ListView) popoverContentView
				.findViewById(R.id.listview);
		ListAdapter adapter = new ListAdapter(mProjects);
		listView.setAdapter(adapter);
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				loadProjects(arg2);
				mPopupWindow.dismiss();
			}
		});

		mPopupWindow = new PopupWindow(popoverContentView,
				mSelectProjectRl.getWidth(), LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.showAsDropDown(mSelectProjectRl, 0, 0);
	}

	/**
	 * 列表适配器 类的详细描述：
	 * 
	 * @author XuBiao
	 * @version 1.0.1
	 * @time 2015年11月8日 下午9:20:50
	 */
	class ListAdapter extends BaseAdapter {
		List<ProjectForC> cityOrprojects;

		public ListAdapter(List<ProjectForC> projects) {
			this.cityOrprojects = projects;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return cityOrprojects.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			if (convertView == null) {
				holder = new ViewHolder();
				convertView = mInflater.inflate(
						R.layout.change_location_item_view, parent, false);
				holder.mContentTv = (TextView) convertView
						.findViewById(R.id.contenTv);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}
			holder.mContentTv.setText(cityOrprojects.get(position).name);
			return convertView;
		}

		private class ViewHolder {
			TextView mContentTv;
		}

	}
}
