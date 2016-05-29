package com.lvdi.ruitianxia_cus.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;

import com.ab.util.AbDialogUtil;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.RandomProductActivity;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * 导航界面 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月30日 下午10:30:55
 */
public class NavigationView implements OnClickListener, OnItemClickListener {
	private View mView;
	/**
	 * 常用
	 */
	private RelativeLayout mCommonLayout;
	/**
	 * 周边
	 */
	private RelativeLayout mPeripheryLayout;
	/**
	 * 企业
	 */
	private RelativeLayout mBusinessLayout;
	/**
	 * 发现
	 */
	private RelativeLayout mFindLayout;
	/**
	 * 列表
	 */
	private GridView mGridView;
	/**
	 * 关闭按钮
	 */
	private ImageView mCloseImageView;
	private Context mContext;
	private LayoutInflater mInflater;
	/**
	 * 列表适配器
	 */
	private NavAdapter mAdapter;
	/**
	 * 列表每个项目的宽高
	 */
	private int itemWH;

	public NavigationView(Context context) {
		mContext = context;
		initView();
	}

	/**
	 * 显示
	 * 
	 * @author Xubiao
	 */
	public void showView() {
		AbDialogUtil.showFullScreenDialog(mView);
	}

	/**
	 * 消失
	 * 
	 * @author Xubiao
	 */
	public void dismiss() {
		AbDialogUtil.removeDialog(mView);
	}

	private void initView() {
		mInflater = LayoutInflater.from(mContext);
		mView = mInflater.inflate(R.layout.activity_navigation, null);
		mCommonLayout = (RelativeLayout) mView.findViewById(R.id.comRl);
		mPeripheryLayout = (RelativeLayout) mView.findViewById(R.id.perRl);
		mBusinessLayout = (RelativeLayout) mView.findViewById(R.id.busRl);
		mFindLayout = (RelativeLayout) mView.findViewById(R.id.findRl);
		mGridView = (GridView) mView.findViewById(R.id.categoryGv);
		mCloseImageView = (ImageView) mView.findViewById(R.id.closeIv);
		mCommonLayout.setOnClickListener(this);
		mPeripheryLayout.setOnClickListener(this);
		mBusinessLayout.setOnClickListener(this);
		mFindLayout.setOnClickListener(this);
		mGridView.setOnItemClickListener(this);
		mCloseImageView.setOnClickListener(this);

		ViewTreeObserver vto2 = mGridView.getViewTreeObserver();
		vto2.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {
			@Override
			public void onGlobalLayout() {
				mGridView.getViewTreeObserver().removeGlobalOnLayoutListener(
						this);
				int gridH = mGridView.getHeight();
				itemWH = (gridH - 280) / 5;
				mAdapter = new NavAdapter();
				mGridView.setAdapter(mAdapter);
				updataTable(R.id.perRl);
			}
		});

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		if (arg2 == 0) {
			Intent intent = new Intent(mContext, RandomProductActivity.class);
			mContext.startActivity(intent);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.closeIv:
			AbDialogUtil.removeDialog(mView);
			break;
		case R.id.comRl:
		case R.id.perRl:
		case R.id.busRl:
		case R.id.findRl:
			updataTable(v.getId());
			break;
		default:
			break;
		}
	}

	/**
	 * 更新选中的table
	 * 
	 * @param viewId
	 * @author Xubiao
	 */
	private void updataTable(int viewId) {
		mCommonLayout.setSelected(false);
		mPeripheryLayout.setSelected(false);
		mBusinessLayout.setSelected(false);
		mFindLayout.setSelected(false);
		mView.findViewById(viewId).setSelected(true);
		mAdapter.notifyDataSetChanged();
	}

	/**
	 * 列表适配器 类的详细描述：
	 * 
	 * @author XuBiao
	 * @version 1.0.1
	 * @time 2015年11月8日 下午9:34:30
	 */
	class NavAdapter extends BaseAdapter {
		private DisplayImageOptions options;

		public NavAdapter() {
			options = new DisplayImageOptions.Builder()
					.showImageOnLoading(R.drawable.pic_default_bg)
					.showImageForEmptyUri(R.drawable.pic_default_bg)
					.showImageOnFail(R.drawable.pic_default_bg)
					.cacheInMemory(true).cacheOnDisk(true)
					.considerExifParams(false)
					.imageScaleType(ImageScaleType.EXACTLY)
					// .displayer(new RoundedBitmapDisplayer(10))
					.bitmapConfig(Bitmap.Config.RGB_565).build();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return 10;
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
			if (position == 0) {
				ImageLoader.getInstance().displayImage(
						"drawable://" + R.drawable.navi_yyy, holder.mImageView,
						options);
			} else {

			}
			ImageLoader.getInstance().displayImage(
					"drawable://" + R.drawable.ic_launcher, holder.mImageView,
					options);
			return convertView;
		}
	}

	private class ViewHolder {
		ImageView mImageView;
	}
}
