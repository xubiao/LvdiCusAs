package com.lvdi.ruitianxia_cus.activity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.ab.util.AbFileUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.util.BitmapUtil;
import com.lvdi.ruitianxia_cus.view.clipimageview.ClipImageView;
import com.lvdi.ruitianxia_cus.view.clipimageview.ClipView;

/**
 * 
 * 类的详细描述： 图片裁剪
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月26日 下午9:07:57
 */
public class CropImageActivity extends LvDiActivity {
	private static final String TAG = "CropImageActivity";
	/**
	 * 显示要裁剪的图片
	 */
	@AbIocView(id = R.id.iv3)
	ClipImageView clip;
	/**
	 * 裁剪框
	 */
	@AbIocView(id = R.id.view)
	ClipView cv;
	/**
	 * 图片的保存路径
	 */
	private File FILE_LOCAL = null;
	/**
	 * 要裁剪的图片对象
	 */
	private Bitmap mBitmap;
	/**
	 * 传过来的屏幕路径
	 */
	private String mPath = "CropImageActivity";
	/***
	 * 屏幕宽高
	 */
	private int screenWidth = 0;
	private int screenHeight = 0;

	private AbTitleBar mAbTitleBar = null;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				break;

			}

		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.crop_image);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("图片裁剪");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		init();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mBitmap != null) {
			mBitmap = null;
		}
	}

	/**
	 * 初始化
	 * 
	 * @author Xubiao
	 */
	private void init() {
		// 初始化图片保存路径
		FILE_LOCAL = new File(
				AbFileUtil.getImageDownloadDir(getApplicationContext()));
		if (!FILE_LOCAL.exists()) {
			FILE_LOCAL.mkdirs();
		}

		initView();
		getWindowWH();
		cv.setLineHeight(5);
		cv.setLineWidth(5);
		mPath = getIntent().getStringExtra("PATH");
		// 按照屏幕大小显示图片
		mBitmap = decodeSampledBitmapFromFile(mPath, screenWidth, screenHeight);
		if (readPictureDegree(mPath) != 0) {
			mBitmap = rotaingImageBitmap(readPictureDegree(mPath), mBitmap);
		}
		if (mBitmap == null) {
			Toast.makeText(CropImageActivity.this, "没有找到图片", 0).show();
			finish();
		}
		// 将处理过的图片显示在界面上
		clip.setImageBitmap(mBitmap);
		// 检测人脸
		// Face[] faces = new FaceDetector.Face[50];
		// FaceDetector fd = new FaceDetector(mBitmap.getWidth(),
		// mBitmap.getHeight(), 50);
	}

	private void initView() {
		mAbTitleBar.clearRightView();
		View rightViewMore = mInflater.inflate(R.layout.right_menu_btn, null);
		mAbTitleBar.addRightView(rightViewMore);
		TextView personBt = (TextView) rightViewMore.findViewById(R.id.menuBtn);
		personBt.setBackgroundDrawable(null);
		personBt.setText("保存");
		mAbTitleBar.getRightLayout().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// 个人中心
				String path = saveToLocal(clip.clip());
				clip.clearBitmap();
				AbLogUtil.i(TAG, "裁剪后图片的路径是 = " + path);
				Intent intent = new Intent();
				intent.putExtra("PATH", path);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	/**
	 * Save to local.
	 * 
	 * @param bitmap
	 *            the bitmap
	 * @return the string
	 */
	public String saveToLocal(Bitmap bitmap) {
		// 需要裁剪后保存为新图片
		String mFileName = Config.CROPFILENAME;
		String path = FILE_LOCAL + File.separator + "."
				+ Cache.getAccountInfo().partyId + mFileName;
		try {
			FileOutputStream fos = new FileOutputStream(path);
			bitmap.compress(CompressFormat.JPEG, 75, fos);
			fos.flush();
			fos.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (null != bitmap && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
		return path;
	}

	/**
	 * 获取屏幕的高和宽
	 */
	private void getWindowWH() {
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		screenWidth = dm.widthPixels;
		screenHeight = dm.heightPixels;
	}

	/**
	 * 根据传入的宽和高，计算出合适的inSampleSize值
	 * 
	 * @param options
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 */
	private int calculateInSampleSize(BitmapFactory.Options options,
			int reqWidth, int reqHeight) {
		// 源图片的高度和宽度
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			// 计算出实际宽高和目标宽高的比率
			final float heightRatio = (float) height / (float) reqHeight;
			final float widthRatio = (float) width / (float) reqWidth;
			// 选择宽和高中最小的比率作为inSampleSize的值，这样可以保证最终图片的宽和高
			// 一定都会大于等于目标的宽和高。
			inSampleSize = (int) Math
					.ceil(heightRatio < widthRatio ? heightRatio : widthRatio);
		}
		return inSampleSize;
	}

	/**
	 * 从文件中加载图片并压缩成指定大小 先通过BitmapFactory.decodeStream方法，创建出一个bitmap，
	 * 再调用上述方法将其设为ImageView的 source。decodeStream最大的秘密在
	 * 于其直接调用JNI>>nativeDecodeAsset()来完成decode，无需再使用java层的createBitmap，
	 * 从而节省了java层的空间
	 * 
	 * @param pathName
	 * @param reqWidth
	 * @param reqHeight
	 * @return
	 * @throws FileNotFoundException
	 */
	private Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth,
			int reqHeight) {
		Bitmap bitmap;
		try {
			bitmap = BitmapUtil.fitSizeImg2(pathName, 5);
		} catch (OutOfMemoryError e) {
			AbToastUtil.showToast(this, "内存过低,释放内存中部分资源");
			bitmap = BitmapUtil.fitSizeImg2(pathName, 5);
		}

		return bitmap;
	}

	/**
	 * 旋转图片
	 * 
	 * @param angle
	 * @param mBitmap
	 * @return
	 */
	private Bitmap rotaingImageBitmap(int angle, Bitmap mBitmap) {
		Matrix matrix = new Matrix();
		matrix.postRotate(angle);
		Bitmap b = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
				mBitmap.getHeight(), matrix, true);
		if (mBitmap != null && !mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
		return b;
	}

	/**
	 * 读取图片旋转角度 三星手机会旋转图片
	 * 
	 * @param imagePath
	 * @return
	 */
	private int readPictureDegree(String imagePath) {
		int imageDegree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(imagePath);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				imageDegree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				imageDegree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				imageDegree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return imageDegree;
	}

	/**
	 * 返回按比例缩减后的bitmap
	 * 
	 * @param bmp
	 * @param width
	 * @param height
	 * @return
	 */
	private Bitmap picZoom(Bitmap bmp, int width, int height) {
		int bmpWidth = bmp.getWidth();
		int bmpHeght = bmp.getHeight();
		// 等比例自动缩放图片适应控件
		float f1 = (float) bmpWidth / width;
		float f2 = (float) bmpHeght / height;
		float scale = 1f;
		if (f1 > 1 || f2 > 1) {
			// 放大
			scale = f1 < f2 ? f2 : f1;
		} else if (f1 < 1 || f2 < 1) {
			// 缩小
			scale = f1 < f2 ? f1 : f2;
		}
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale);
		return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		// 及时回收
		if (mBitmap != null && mBitmap.isRecycled()) {
			mBitmap.recycle();
			mBitmap = null;
		}
	}
}
