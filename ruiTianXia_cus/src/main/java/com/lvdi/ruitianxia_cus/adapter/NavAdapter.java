package com.lvdi.ruitianxia_cus.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 列表适配器 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月8日 下午9:34:30
 */
public class NavAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private int itemWH;
	private List<Application> applications;

	public NavAdapter(Context context, int itemWH) {
		mContext = context;
		this.itemWH = itemWH;
		mInflater = LayoutInflater.from(mContext);
		applications = new ArrayList<Application>();
	}

	public void setData(ApplicationEntity navigationApps) {
		applications.clear();
		applications.addAll(navigationApps.applications);
	}

	public void clearData() {
		applications.clear();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return applications.size();
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
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.view_nav_item, parent,
					false);
			holder.mImageView = (ImageView) convertView
					.findViewById(R.id.navIv);

			LinearLayout.LayoutParams lParams = (LayoutParams) holder.mImageView
					.getLayoutParams();
			lParams.height = itemWH;
			lParams.width = itemWH;
			holder.mImageView.setLayoutParams(lParams);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		Application application = applications.get(position);
		ImageLoaderHelper.displayImage(
				application.appIcon + "_android_nav.png", holder.mImageView,
				R.drawable.jz_android_nav);

		return convertView;
	}

	private class ViewHolder {
		ImageView mImageView;
	}
}
