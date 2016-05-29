package com.lvdi.ruitianxia_cus.util;

import java.io.File;
import java.io.IOException;

import com.ab.util.AbLogUtil;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.text.TextUtils;

/**
 * 
 * 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年12月5日 下午3:15:01
 */
public class BitmapRotateUtil {

	/**
	 * TAG
	 */
	private static final String TAG = BitmapRotateUtil.class.getSimpleName();

	// 定义静态的角度
	public interface Inter_angle {
		public String ANGLE_ZERO = "0";
		public String ANGLE_CIRCLE = "360";
		public String ANGLE_NULL = "null";
	}

	/**
	 * 处理Bitmap旋转
	 * 
	 * @param bitmap
	 *            Bitmap对象
	 * @param angle
	 *            角度
	 */
	public static Bitmap matrixBitmapByAngle(Bitmap bitmap, String angle) {
		if (bitmap == null) {
			return null;
		}
		if (!TextUtils.isEmpty(angle) && !angle.equals(Inter_angle.ANGLE_ZERO)
				&& !angle.equals(Inter_angle.ANGLE_CIRCLE)
				&& !angle.equals(Inter_angle.ANGLE_NULL)) {
			// Matrix对Bitmap进行旋转处理
			Matrix matrix = new Matrix();
			int width = bitmap.getWidth();
			int height = bitmap.getHeight();
			matrix.setRotate(Float.parseFloat(angle));
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix,
					true);
		}
		return bitmap;
	}

	/**
	 * 上传完成后 删除本地图片
	 * 
	 * @param path
	 *            图片的本地路径
	 */
	public void deleteBitmap(String path) {
		if (TextUtils.isEmpty(path)) {
			return;
		}
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	/**
	 * 根据图片的本地路径获取它的旋转角度
	 * 
	 * @param path
	 *            本地路径
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
}
