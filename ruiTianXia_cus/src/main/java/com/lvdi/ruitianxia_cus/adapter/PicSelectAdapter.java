package com.lvdi.ruitianxia_cus.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;

/**
 * 
 * 类的详细描述： 图片列表适配器
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午10:35:33
 */
public class PicSelectAdapter extends BaseAdapter {
	private Context mContext;
	private LayoutInflater mInflater;
	private List<String> mPathList;

	public PicSelectAdapter(Context context, List<String> pathList) {
		this.mContext = context;
		this.mPathList = pathList;
		mPathList.add(0, "");
		mInflater = LayoutInflater.from(mContext);
	}

	@Override
	public int getCount() {
		return mPathList.size();
	}

	@Override
	public Object getItem(int position) {
		return mPathList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.pic_select_grid_item,
					parent, false);
			holder.mImageView = (ImageView) convertView
					.findViewById(R.id.id_item_image);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		if (position == 0) {
			ImageLoaderHelper.displayImage("drawable://"
					+ R.drawable.pic_carmer_bg, holder.mImageView,
					R.drawable.pic_default_bg);

		} else {
			ImageLoaderHelper.displayImage(
					"file:///" + mPathList.get(position), holder.mImageView,
					R.drawable.pic_default_bg);
		}
		return convertView;
	}

	private class ViewHolder {
		ImageView mImageView;
	}

}
