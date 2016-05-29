package com.lvdi.ruitianxia_cus.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;

/**
 * 
 * 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月19日 下午11:05:05
 */
public class MainServiceAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private List<Application> mLayouts;
	private int mMiddleItem;

	public MainServiceAdapter(Context context) {
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
		mLayouts = new ArrayList<Application>();
	}

	public void setData(ArrayList<Application> layouts) {
		mLayouts.clear();
		mLayouts.addAll(layouts);
	}

	public int getMiddleItem() {
		return mMiddleItem;
	}

	public void setMiddleItem(int item) {
		mMiddleItem = item;
		notifyDataSetChanged();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return null == mLayouts ? 0 : mLayouts.size() * 3;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mLayouts.get(position % mLayouts.size());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position % mLayouts.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.main_service_view_item,
					parent, false);
			holder.mTimeTv = (TextView) convertView.findViewById(R.id.timeTv);
			holder.mSelectIv = (ImageView) convertView
					.findViewById(R.id.selectIv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Application mLayout = mLayouts.get(position % mLayouts.size());
		holder.mTimeTv.setText(mLayout.timeNode);
		if (position == mMiddleItem) {
			holder.mTimeTv.setTextAppearance(mContext, R.style.MainItemSelect);
			holder.mSelectIv.setVisibility(View.VISIBLE);
		} else {
			holder.mTimeTv.setTextAppearance(mContext, R.style.MainItemNormal);
			holder.mSelectIv.setVisibility(View.GONE);
		}
		return convertView;
	}

	private class ViewHolder {
		TextView mTimeTv;
		ImageView mSelectIv;
	}
}
