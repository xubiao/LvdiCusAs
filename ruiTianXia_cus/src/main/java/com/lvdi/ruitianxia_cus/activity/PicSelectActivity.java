package com.lvdi.ruitianxia_cus.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.ab.task.AbTask;
import com.ab.task.AbTaskItem;
import com.ab.task.AbTaskListener;
import com.ab.util.AbFileUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbStrUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.adapter.PicSelectAdapter;
import com.lvdi.ruitianxia_cus.global.Config;

/**
 * 
 * 类的详细描述： 图片选择
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月25日 下午9:12:06
 */
public class PicSelectActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(itemClick = "itemClick", id = R.id.id_gridView)
	GridView mGirdView;// 图片展示列表
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 0x01;
	/* 用来标识请求裁剪图片后的activity */
	private static final int CAMERA_CROP_DATA = 0x03;

	private List<String> mImgs;// 所有图片路径的集合
	private ListAdapter mAdapter;// 图片列表适配器
	/* 拍照的照片存储位置 */
	private File PHOTO_DIR = null;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {

			switch (msg.what) {
			case 0:
				mAdapter = new PicSelectAdapter(getApplicationContext(), mImgs);
				mGirdView.setAdapter(mAdapter);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_pic_select);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("选择图片");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		getImages();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 列表点击 〈功能详细描述〉
	 * 
	 * @param arg0
	 * @param arg1
	 * @param arg2
	 * @param arg3
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public void itemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if (arg2 == 0) {
			doPickPhotoAction();
		} else {
			gotoCrop(mImgs.get(arg2));
		}
	}

	/**
	 * 从照相机获取
	 */
	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		// 判断是否有SD卡,如果有sd卡存入sd卡在说，没有sd卡直接转换为图片
		if (status.equals(Environment.MEDIA_MOUNTED)) {
			doTakePhoto();
		} else {
			AbToastUtil.showToast(PicSelectActivity.this, "没有可用的存储卡");
		}
	}

	/**
	 * 拍照获取图片
	 */
	protected void doTakePhoto() {

		try {

			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE, null);
			// Samsung的系统相机，版式是横板的,同时此activity不要设置单例模式
			intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoFileUri());
			startActivityForResult(intent, CAMERA_WITH_DATA);
		} catch (Exception e) {
			AbToastUtil.showToast(PicSelectActivity.this, "未找到系统相机程序");
		}
	}

	private Uri photoFileUri() {
		if (null == PHOTO_DIR) {
			// 初始化图片保存路径
			String photo_dir = AbFileUtil.getImageDownloadDir(this);
			if (AbStrUtil.isEmpty(photo_dir)) {
				AbToastUtil.showToast(PicSelectActivity.this, "存储卡不存在");
			} else {
				PHOTO_DIR = new File(photo_dir);
			}
		}
		File mCurrentPhotoFile = new File(PHOTO_DIR, "." + Config.PHOTOFILENAME);
		return Uri.fromFile(mCurrentPhotoFile);
	}

	/**
	 * 利用ContentProvider扫描手机中的图片，此方法在运行在子线程中 完成图片的扫描
	 */
	private void getImages() {
		if (!Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			AbToastUtil.showToast(this, "暂无外部存储");
			return;
		}
		mImgs = new ArrayList<String>();
		AbTask mAbTask = AbTask.newInstance();
		// 定义异步执行的对象
		final AbTaskItem item = new AbTaskItem();
		item.setListener(new AbTaskListener() {

			@Override
			public void update() {
				// 完成
				// 通知Handler扫描图片完成
				mHandler.sendEmptyMessage(0);
			}

			@Override
			public void get() {
				try {
					// 开始
					// Thread.sleep(10);
					// 下面写要执行的代码，如下载数据
					Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
					ContentResolver mContentResolver = PicSelectActivity.this
							.getContentResolver();
					// 只查询jpeg和png的图片
					Cursor mCursor = mContentResolver.query(mImageUri, null,
							MediaStore.Images.Media.MIME_TYPE + "=? or "
									+ MediaStore.Images.Media.MIME_TYPE + "=?",
							new String[] { "image/jpeg", "image/png" },
							MediaStore.Images.Media.DATE_MODIFIED + " DESC");

					while (mCursor.moveToNext()) {
						// 获取图片的路径
						String path = mCursor.getString(mCursor
								.getColumnIndex(MediaStore.Images.Media.DATA));
						mImgs.add(path);
					}
					mCursor.close();
				} catch (Exception e) {
				}
			};
		});
		// 开始执行
		mAbTask.execute(item);

	}

	/**
	 * 
	 * 功能描述: <br>
	 * 去裁剪界面 〈功能详细描述〉
	 * 
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	private void gotoCrop(String path) {
		Intent intent2 = new Intent(this, CropImageActivity.class);
		intent2.putExtra("PATH", path);
		startActivityForResult(intent2, CAMERA_CROP_DATA);
	}

	protected void onActivityResult(int requestCode, int resultCode,
			Intent mIntent) {
		if (resultCode != RESULT_OK) {
			return;
		}
		switch (requestCode) {
		case CAMERA_WITH_DATA:// 拍照回来
			String pathString = photoFileUri().getPath();
			MediaScannerConnection.scanFile(this, new String[] { pathString },
					null, null);
			getImages();
			AbLogUtil.d(PicSelectActivity.class, "将要进行裁剪的图片的路径是 = "
					+ pathString);
			String currentFilePath2 = pathString;
			gotoCrop(currentFilePath2);
			break;
		case CAMERA_CROP_DATA:// 截图回来
			String path = mIntent.getStringExtra("PATH");
			AbLogUtil.d(PicSelectActivity.class, "裁剪后得到的图片的路径是 = " + path);
			Intent intent = new Intent();
			intent.putExtra("PATH", path);
			setResult(RESULT_OK, intent);
			finish();
			break;
		}
	}
}
