package com.lvdi.ruitianxia_cus.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class AppCardView extends RelativeLayout implements OnLongClickListener,
		OnClickListener {
	public interface ImageLoadListener {
		public void onLoadingStarted(String arg0, View arg1);

		public void onLoadingFailed(String arg0, View arg1, FailReason arg2);

		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2,
				int index);

		public void onLoadingCancelled(String arg0, View arg1);
	}

	private Application mApplication;
	private int mCardResids[] = new int[] { R.drawable.app_card_bg1,
			R.drawable.app_card_bg2, R.drawable.app_card_bg3,
			R.drawable.app_card_bg4, R.drawable.app_card_bg5 };
	private int mPicIndexs[] = new int[] { 0, 1, 2, 3, 4 };
	private int mIndex;

	private ImageLoadListener mListener;
	private TextView appNameTv;
	private TextView appDesTv;
	private ImageView iconIv;

	private ImageView backIv;

	public AppCardView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public AppCardView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public AppCardView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	private void initView(Context context) {
		LayoutInflater.from(context).inflate(R.layout.view_app_card_item, this,
				true);
		appNameTv = (TextView) findViewById(R.id.appNameTv);
		appDesTv = (TextView) findViewById(R.id.appDesTv);
		iconIv = (ImageView) findViewById(R.id.appIconIv);
		backIv = (ImageView) findViewById(R.id.cardRl);
	}

	public void setSelect(int index) {

	}

	public void setData(Application application, int index) {
		mApplication = application;
		mIndex = index;
		if (application.id == 0) {
			appNameTv.setText("    ");
			appDesTv.setText("    ");
			ImageLoaderHelper.displayImage("", iconIv,
					R.drawable.syjz_android_, mImageLoadingListener);
		} else {
			appNameTv.setText(application.appName);
			appDesTv.setText(application.appIntroduction);
			ImageLoaderHelper.displayImage(application.appIcon + "_android_"
					+ (mPicIndexs[index % 5] + 1) + ".png", iconIv,
					R.drawable.syjz_android_, mImageLoadingListener);
		}
		backIv.setBackgroundResource(mCardResids[index % 5]);
	}

	public void setTextData(Application application, int index) {
		mApplication = application;
		mIndex = index;
		if (application.id == 0) {
			appNameTv.setText("    ");
			appDesTv.setText("    ");
		} else {
			appNameTv.setText(application.appName);
			appDesTv.setText(application.appIntroduction);
		}
		backIv.setBackgroundResource(mCardResids[index % 5]);
	}

	public void loadImage(ImageLoadListener listener) {
		mListener = listener;
		ImageView iconIv = (ImageView) findViewById(R.id.appIconIv);
		if (mApplication.id == 0) {
			ImageLoaderHelper.displayImage("", iconIv,
					R.drawable.syjz_android_, mImageLoadingListener);
		} else {
			ImageLoaderHelper.displayImage(mApplication.appIcon + "_android_"
					+ (mPicIndexs[mIndex % 5] + 1) + ".png", iconIv,
					R.drawable.syjz_android_, mImageLoadingListener);

		}
	}

	public void onRefresh() {
		setData(mApplication, mIndex);
	}

	ImageLoadingListener mImageLoadingListener = new ImageLoadingListener() {

		@Override
		public void onLoadingStarted(String arg0, View arg1) {
			// TODO Auto-generated method stub
			if (null != mListener)
				mListener.onLoadingStarted(arg0, arg1);
		}

		@Override
		public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
			// TODO Auto-generated method stub
			if (null != mListener)
				mListener.onLoadingStarted(arg0, arg1);

			switch (arg2.getType()) {
			case IO_ERROR:
				break;
			case DECODING_ERROR:
				break;
			case NETWORK_DENIED:
				break;
			case OUT_OF_MEMORY:
				ImageLoaderHelper.clearMemoryCache();
				break;
			case UNKNOWN:
				break;
			default:
				break;
			}

		}

		@Override
		public void onLoadingComplete(String arg0, View arg1, Bitmap arg2) {
			// TODO Auto-generated method stub
			if (null != mListener)
				mListener.onLoadingComplete(arg0, arg1, arg2, mIndex);
		}

		@Override
		public void onLoadingCancelled(String arg0, View arg1) {
			// TODO Auto-generated method stub
			if (null != mListener)
				mListener.onLoadingCancelled(arg0, arg1);
		}
	};

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.addIcon:
			break;
		case R.id.deleteIcon:
			break;
		case R.id.card3:

			break;
		default:
			break;
		}
	}

	@Override
	public boolean onLongClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.card3:
			break;
		default:
			break;
		}

		return true;
	}
}
