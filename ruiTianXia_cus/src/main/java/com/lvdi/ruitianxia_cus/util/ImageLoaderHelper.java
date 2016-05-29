package com.lvdi.ruitianxia_cus.util;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.lvdi.ruitianxia_cus.global.MyApplication;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoaderHelper {

	public static void displayImage(String uri, ImageView imageView,
			DisplayImageOptions options, ImageLoadingListener listener) {
		try {
			if (null == listener) {
				listener = new ImageLoadingListener() {

					@Override
					public void onLoadingStarted(String imageUri, View view) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onLoadingFailed(String imageUri, View view,
							FailReason failReason) {
						// TODO Auto-generated method stub
						switch (failReason.getType()) {
						case IO_ERROR:
							break;
						case DECODING_ERROR:
							break;
						case NETWORK_DENIED:
							break;
						case OUT_OF_MEMORY:
							clearMemoryCache();
							break;
						case UNKNOWN:
							break;
						default:
							break;
						}
					}

					@Override
					public void onLoadingComplete(String imageUri, View view,
							Bitmap loadedImage) {
						// TODO Auto-generated method stub
					}

					@Override
					public void onLoadingCancelled(String imageUri, View view) {
						// TODO Auto-generated method stub

					}
				};
			}
			ImageLoader.getInstance().displayImage(uri, imageView, options,
					listener);
		} catch (IllegalStateException e) {
			// 若crash后，重新初始化ImageLoaderConfiguration
			MyApplication.getInstance().initImageLoader();
		}
	}

	public static void displayImage(String uri, ImageView imageView,
			DisplayImageOptions options) {
		displayImage(uri, imageView, options, null);
	}

	public static void displayImage(String uri, ImageView imageView,
			ImageLoadingListener listener) {
		displayImage(uri, imageView,
				MyApplication.getInstance().defaultImgOptions, listener);
	}

	/**
	 * @param uri
	 * @param imageView
	 * @param isSetBg
	 *            是否把请求的图片设置为ImageView背景
	 */
	public static void displayImage(String uri, ImageView imageView) {
		displayImage(uri, imageView,
				MyApplication.getInstance().defaultImgOptions);
	}

	/**
	 * 请求图片按照给定的宽度等比例显示
	 * 
	 * @param uri
	 * @param imageView
	 * @param isSetBg
	 * 
	 */
	public static void displayImageToWidth(String uri, ImageView imageView,
			final int targetWidth) {
		displayImage(uri, imageView, new ImageLoadingListener() {

			@Override
			public void onLoadingStarted(String imageUri, View view) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onLoadingFailed(String imageUri, View view,
					FailReason failReason) {
				// TODO Auto-generated method stub
				switch (failReason.getType()) {
				case IO_ERROR:
					break;
				case DECODING_ERROR:
					break;
				case NETWORK_DENIED:
					break;
				case OUT_OF_MEMORY:
					clearMemoryCache();
					break;
				case UNKNOWN:
					break;
				default:
					break;
				}
			}

			@Override
			public void onLoadingComplete(String imageUri, View view,
					Bitmap loadedImage) {
				// TODO Auto-generated method stub
				float mImgVHeight = DimenUtil.getHeight(loadedImage.getWidth(),
						loadedImage.getHeight(), targetWidth);
				view.getLayoutParams().height = (int) mImgVHeight;
				((ImageView) view).setImageBitmap(loadedImage);
			}

			@Override
			public void onLoadingCancelled(String imageUri, View view) {
				// TODO Auto-generated method stub

			}
		});
	}

	/**
	 * @param uri
	 * @param imageView
	 * @param isSetBg
	 *            是否把请求的图片设置为ImageView背景
	 */
	public static void displayImage(String uri, ImageView imageView,
			int defImgRes) {
		displayImage(uri, imageView, getImageOptions(defImgRes));
	}

	public static void displayImage(String uri, ImageView imageView,
			Drawable defImgRes) {
		displayImage(uri, imageView, getImageOptions(defImgRes));
	}

	public static void displayImage(String uri, ImageView imageView,
			int defImgRes, ImageLoadingListener listener) {
		displayImage(uri, imageView, getImageOptions(defImgRes), listener);
	}

	/**
	 * 获取图片配置
	 * 
	 * @param avatarRes
	 * @return
	 */
	public static DisplayImageOptions getImageOptions(int avatarRes) {
		return new DisplayImageOptions.Builder().showImageOnLoading(avatarRes)
				.showImageForEmptyUri(avatarRes).showImageOnFail(avatarRes)
				.cacheInMemory(true).cacheOnDisc(true)
				/* .displayer(new FadeInBitmapDisplayer(150)) */// 先注释 有些列表滑动会抖动
				.bitmapConfig(Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
	}

	public static DisplayImageOptions getImageOptions(Drawable avatarRes) {
		return new DisplayImageOptions.Builder().showImageOnLoading(avatarRes)
				.showImageForEmptyUri(avatarRes).showImageOnFail(avatarRes)
				.cacheInMemory(true).cacheOnDisc(true)
				/* .displayer(new FadeInBitmapDisplayer(150)) */// 先注释 有些列表滑动会抖动
				.bitmapConfig(Config.RGB_565)
				.imageScaleType(ImageScaleType.IN_SAMPLE_INT).build();
	}

	public static void clearMemoryCache() {
		ImageLoader.getInstance().clearMemoryCache();
	}

}
