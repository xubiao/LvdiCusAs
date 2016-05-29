package com.lvdi.ruitianxia_cus.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Random;

import com.ab.util.AbLogUtil;
import com.ab.util.AbToastUtil;
import com.nostra13.universalimageloader.core.ImageLoader;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.text.TextUtils;
import android.util.FloatMath;
import android.util.Log;

/**
 * @author 13120678
 * 
 *         图片工具类
 */
public class BitmapUtil {
	/**
	 * 
	 */
	private static final int SIZE_WIDTH = 1028;
	/**
	 * 
	 */
	private static final int SIZE_HEIGHT = 800;

	/**
	 * 按比例对图片进行缩放(防止内存溢出)
	 * 
	 * @param path
	 *            图片的存储路径
	 * @param width
	 * @param height
	 * @return
	 */
	public static Bitmap getBitmapFromFile(String path, int width, int height) {
		BitmapFactory.Options opts = null;
		if (width > 0 && height > 0) {
			opts = new BitmapFactory.Options();
			opts.inPreferredConfig = Bitmap.Config.RGB_565;
			// 不分配内存
			opts.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(path, opts);
			// 计算图片缩放比例
			opts.inSampleSize = computeSampleSize(opts, width, height);
			opts.inJustDecodeBounds = false;
			opts.inInputShareable = true;
			opts.inPurgeable = true;
		}
		try {
			return BitmapFactory.decodeFile(path, opts);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Android提供计算inSampleSize的方法
	 */
	public static int computeSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		int initialSize = computeInitialSampleSize(options, minSideLength,
				maxNumOfPixels);
		int roundedSize;
		if (initialSize <= 8) {
			roundedSize = 1;
			while (roundedSize < initialSize) {
				roundedSize <<= 1;
			}
		} else {
			roundedSize = (initialSize + 7) / 8 * 8;
		}
		return roundedSize;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param options
	 * @param minSideLength
	 * @param maxNumOfPixels
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	private static int computeInitialSampleSize(BitmapFactory.Options options,
			int minSideLength, int maxNumOfPixels) {
		double w = options.outWidth;
		double h = options.outHeight;

		int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math
				.sqrt(w * h / maxNumOfPixels));
		int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(
				Math.floor(w / minSideLength), Math.floor(h / minSideLength));

		if (upperBound < lowerBound) {
			return lowerBound;
		}

		if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
			return 1;
		} else if (minSideLength == -1) {
			return lowerBound;
		} else {
			return upperBound;
		}
	}

	/**
	 * 得到圆角图片 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bmpSrc
	 * @param rx
	 * @param ry
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getRoundedCornerBitmap(Bitmap bmpSrc, float rx,
			float ry) {
		if (null == bmpSrc) {
			return null;
		}

		int bmpSrcWidth = bmpSrc.getWidth();
		int bmpSrcHeight = bmpSrc.getHeight();

		Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight,
				Config.ARGB_8888);
		if (null != bmpDest) {
			Canvas canvas = new Canvas(bmpDest);
			final int color = 0xff424242;
			final Paint paint = new Paint();
			final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
			final RectF rectF = new RectF(rect);

			// Setting or clearing the ANTI_ALIAS_FLAG bit AntiAliasing smooth
			// out
			// the edges of what is being drawn, but is has no impact on the
			// interior of the shape.
			paint.setAntiAlias(true);

			canvas.drawARGB(0, 0, 0, 0);
			paint.setColor(color);
			canvas.drawRoundRect(rectF, rx, ry, paint);
			paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
			canvas.drawBitmap(bmpSrc, rect, rect, paint);
		}

		return bmpDest;
	}

	/**
	 * 得到上面两个角是圆角的图片 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bmpSrc
	 * @param rx
	 * @param ry
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getRoundedCornerForTop(Bitmap bmpSrc, float rx,
			float ry) {
		if (null == bmpSrc) {
			return null;
		}

		int bmpSrcWidth = bmpSrc.getWidth();
		int bmpSrcHeight = bmpSrc.getHeight();
		Bitmap bmpDest = null;
		try {
			bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight,
					Config.ARGB_8888);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		if (bmpDest == null) {
			return null;
		}

		Canvas canvas = new Canvas(bmpDest);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);
		final RectF rectF = new RectF(rect);

		// Setting or clearing the ANTI_ALIAS_FLAG bit AntiAliasing smooth
		// out
		// the edges of what is being drawn, but is has no impact on the
		// interior of the shape.
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, rx, ry, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bmpSrc, rect, rect, paint);

		return bmpDest;
	}

	/**
	 * 合并两张图片
	 * 
	 * @param bmpSrc
	 * @return
	 */
	public static Bitmap duplicateBitmap(Bitmap bmpSrc) {
		if (null == bmpSrc) {
			return null;
		}
		int bmpSrcWidth = bmpSrc.getWidth();
		int bmpSrcHeight = bmpSrc.getHeight();

		Bitmap bmpDest = Bitmap.createBitmap(bmpSrcWidth, bmpSrcHeight,
				Config.ARGB_8888);
		if (null != bmpDest) {
			Canvas canvas = new Canvas(bmpDest);
			final Rect rect = new Rect(0, 0, bmpSrcWidth, bmpSrcHeight);

			canvas.drawBitmap(bmpSrc, rect, rect, null);
		}
		return bmpDest;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bitmap
	 * @param wScale
	 * @param hScale
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getScaleBitmap(Bitmap bitmap, float wScale,
			float hScale) {
		Matrix matrix = new Matrix();
		matrix.postScale(wScale, hScale);
		Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
				bitmap.getHeight(), matrix, true);
		return bmp;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bitmap
	 * @param ratio
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getScaleBitmap(Bitmap bitmap, float ratio) {
		if (bitmap == null) {
			return null;
		}

		final int width = bitmap.getWidth();
		final int height = bitmap.getHeight();
		Matrix matrix = new Matrix();
		matrix.postScale(ratio, ratio);
		try {
			Bitmap bmp = Bitmap.createBitmap(bitmap, 0, 0, width, height,
					matrix, true);
			return bmp;
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param srcImg
	 * @param width
	 * @param height
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getScaleBitmap(Bitmap srcImg, int width, int height) {
		int sw = srcImg.getWidth();
		int sh = srcImg.getHeight();

		int dw = 150;
		int dh = 150;

		double bv = 1;

		int nh = dh;// 最终图片高
		int nw = dw;// 最终图片宽
		int sx = 0; // 裁切X起点
		int sy = 0; // 裁切Y起点

		if (sw > sh) {
			nh = sh;
			nw = nh;
			sx = (sw - nw) / 2;
			sy = 0;
		} else {
			nw = sw;
			nh = nw;
			sx = 0;
			sy = (sh - nh) / 2;
		}
		bv = dw * 1.0 / nw;

		Bitmap newmap = Bitmap.createBitmap(dw, dh, Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(newmap);
		c.drawBitmap(srcImg, new Rect(sx, sy, sx + nw, sy + nh), new Rect(0, 0,
				dw, dh), null);

		return newmap;
	}

	/**
	 * 通过尺寸缩小图片 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bitmap
	 * @param dstWidth
	 * @param dstHeight
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getSizedBitmap(Bitmap bitmap, int dstWidth,
			int dstHeight) {
		if (null != bitmap) {
			Bitmap result = Bitmap.createScaledBitmap(bitmap, dstWidth,
					dstHeight, false);
			return result;
		}
		return null;
	}

	/**
	 * 得到全屏的图片 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bitmap
	 * @param wScale
	 * @param hScale
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getFullScreenBitmap(Bitmap bitmap, int wScale,
			int hScale) {
		int dstWidth = bitmap.getWidth() * wScale;
		int dstHeight = bitmap.getHeight() * hScale;
		Bitmap result = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight,
				false);
		return result;
	}

	/**
	 * 将字节数组转换成bitmap对象
	 */
	public static Bitmap byteArrayToBitmap(byte[] array) {
		if (null == array) {
			return null;
		}
		return BitmapFactory.decodeByteArray(array, 0, array.length);
	}

	/**
	 * 将bitmap对象转换成字节数组
	 */
	public static byte[] bitampToByteArray(Bitmap bitmap) {
		byte[] array = null;
		try {
			if (null != bitmap) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				bitmap.compress(Bitmap.CompressFormat.JPEG, 30, os);
				array = os.toByteArray();
				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return array;
	}

	/**
	 * 将bitmap对象转换成字节数组 compress压缩质量
	 */
	public static byte[] bitampToByteArray(Bitmap bitmap, int compress,
			Bitmap.CompressFormat format) {
		byte[] array = null;
		try {
			if (null != bitmap) {
				ByteArrayOutputStream os = new ByteArrayOutputStream();
				bitmap.compress(format, compress, os);
				array = os.toByteArray();
				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return array;
	}

	/**
	 * 将一张图片保存到指定目录中
	 * 
	 * @param context
	 * @param bmp
	 * @param name
	 */
	public static void saveBitmapToFile(Context context, Bitmap bmp, String name) {
		if (null != context && null != bmp && null != name && name.length() > 0) {
			try {
				FileOutputStream fos = context.openFileOutput(name,
						Context.MODE_WORLD_WRITEABLE);
				bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
				fos.flush();
				fos.close();
				fos = null;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param context
	 * @param name
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap loadBitmapFromFile(Context context, String name) {
		Bitmap bmp = null;
		try {
			if (null != context && null != name && name.length() > 0) {
				FileInputStream fis = context.openFileInput(name);
				bmp = BitmapFactory.decodeStream(fis);
				fis.close();
				fis = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		return bmp;
	}

	/*
	 * public static Bitmap drawableToBitmap(Drawable drawable) { if (null ==
	 * drawable) { return null; }
	 * 
	 * int width = drawable.getIntrinsicWidth(); int height =
	 * drawable.getIntrinsicHeight(); Config config = (drawable.getOpacity() !=
	 * PixelFormat.OPAQUE) ? Config.ARGB_8888 : Config.RGB_565; //部分手机这里获取不到宽和高
	 * Bitmap bitmap = Bitmap.createBitmap(width, height, config);
	 * 
	 * if (null != bitmap) { Canvas canvas = new Canvas(bitmap);
	 * drawable.setBounds(0, 0, width, height); drawable.draw(canvas); }
	 * 
	 * return bitmap; }
	 */
	public static void saveBitmapToSDCard(Bitmap bmp, String strPath) {
		if (null != bmp && null != strPath && !strPath.equalsIgnoreCase("")) {
			try {
				File file = new File(strPath);
				if (!checkDir(file)) {
					return;
				}
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = BitmapUtil.bitampToByteArray(bmp);
				fos.write(buffer);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bmp
	 * @param strPath
	 * @param compress
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static void saveBitmapToSDCard(Bitmap bmp, String strPath,
			int compress) {
		saveBitmapToSDCard(bmp, strPath, compress, Bitmap.CompressFormat.JPEG);
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bmp
	 * @param strPath
	 * @param compress
	 * @param format
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static void saveBitmapToSDCard(Bitmap bmp, String strPath,
			int compress, Bitmap.CompressFormat format) {
		if (null != bmp && null != strPath && !strPath.equalsIgnoreCase("")) {
			try {
				File file = new File(strPath);
				if (!checkDir(file)) {
					return;
				}
				FileOutputStream fos = new FileOutputStream(file);
				byte[] buffer = BitmapUtil.bitampToByteArray(bmp, compress,
						format);
				fos.write(buffer);
				fos.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 如果目录存在，或者目录创建成功返回true，否则返回false
	 * 
	 * @param file
	 * @return
	 */
	private static boolean checkDir(File file) {
		File dir = file.getParentFile();
		if (dir.exists()) {
			return true;
		}

		return dir.mkdirs();
	}

	/**
	 * 从SD卡中读取图片，防止溢出
	 * 
	 * @param strPath
	 * @param screenWidth
	 * @param screenHeight
	 * @return
	 */
	public static Bitmap loadBitmapFromSDCard(String strPath) {
		if (TextUtils.isEmpty(strPath)) {
			return null;
		}
		Bitmap bm = null;
		try {
			bm = BitmapFactory.decodeStream(new FileInputStream(new File(
					strPath)));
			Log.e("tag", strPath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			return null;
		}

		return bm;
	}

	/**
	 * 得到指定尺寸的图片
	 * 
	 * @param srcPath
	 * @param desPath
	 * @param x
	 * @param y
	 */
	public static void createScaleBitmap(String srcPath, String desPath,
			float x, float y) {
		Bitmap thumPic = BitmapUtil.loadBitmapFromSDCard(srcPath);
		Bitmap calPic = BitmapUtil.getScaleBitmap(thumPic, x, y);
		BitmapUtil.saveBitmapToSDCard(calPic, desPath);
		calPic.recycle();
	}

	/**
	 * 得到重叠图片
	 * 
	 * @param b1
	 * @param b2
	 * @return
	 */
	public static LayerDrawable getLayerBitmap(Bitmap b1, Bitmap b2) {
		Drawable[] array = new Drawable[2];
		array[0] = new BitmapDrawable(b1);
		array[1] = new BitmapDrawable(b2);
		LayerDrawable la = new LayerDrawable(array);
		// 其中第一个参数为层的索引号，后面的四个参数分别为left、top、right和bottom
		la.setLayerInset(0, 0, 0, 0, 0);
		la.setLayerInset(1, 70, 90, 0, 0);
		return la;
	}

	/**
	 * 达到图片重叠
	 * 
	 * @param mbitmap
	 * @return
	 */
	public static Bitmap drawPicture(Bitmap mbitmap) {
		Bitmap bitmap = mbitmap;
		Bitmap mBitmap = Bitmap.createBitmap(bitmap.getWidth() + 4,
				bitmap.getHeight() + 4, Bitmap.Config.ARGB_8888);
		Canvas mCanvas = new Canvas(mBitmap);
		Paint mPaint = new Paint();

		mPaint.setColor(Color.WHITE);
		mCanvas.drawRect(0, 0, bitmap.getWidth() + 4, bitmap.getHeight() + 4,
				mPaint);
		mCanvas.drawBitmap(bitmap, 2, 2, new Paint());
		mCanvas.save(Canvas.ALL_SAVE_FLAG);
		mCanvas.restore();

		Matrix matrix = new Matrix();
		matrix.setRotate(8);
		Bitmap newBitmap = Bitmap.createBitmap(mBitmap, 0, 0,
				mBitmap.getWidth(), mBitmap.getHeight(), matrix, true);
		matrix.setRotate(5);
		Bitmap xBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(),
				mBitmap.getHeight(), matrix, true);

		Canvas canvas = new Canvas(newBitmap);
		canvas.drawBitmap(xBitmap, 0, 0, new Paint());
		canvas.drawBitmap(mBitmap, 0, 4, new Paint());
		canvas.drawBitmap(mBitmap, 0, 10, new Paint());
		canvas.save(Canvas.ALL_SAVE_FLAG);
		canvas.restore();
		String filename = "/sdcard/12.png";
		File file = new File(filename);

		FileOutputStream out;
		try {
			file.createNewFile();
			out = new FileOutputStream(file);
			newBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
			Log.e("System.out", "Save Ok");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			Log.e("System.out", e.toString());
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Log.e("System.out", e.toString());
			e.printStackTrace();
		}
		return newBitmap;
	}

	/**
	 * 圆角
	 * 
	 * @param bitmap
	 * @param pixels
	 * @return
	 */
	public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {
		Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
				bitmap.getHeight(), Config.ARGB_8888);
		Canvas canvas = new Canvas(output);
		final int color = 0xff424242;
		final Paint paint = new Paint();
		final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
		final RectF rectF = new RectF(rect);
		final float roundPx = pixels;
		paint.setAntiAlias(true);
		canvas.drawARGB(0, 0, 0, 0);
		paint.setColor(color);
		canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
		paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
		canvas.drawBitmap(bitmap, rect, rect, paint);
		return output;
	}

	/*
	 * 截取上一页的下半部分图片 截图下一页的上半部分图片 组合成新图片返回
	 * 
	 * @prama bitmapUp 上一页图片
	 * 
	 * @prama bitmapDown 下一页图片
	 * 
	 * @prama offset 偏移量
	 */
	public static Bitmap cropUp(Bitmap bitmapUp, Bitmap bitmapDown, int offset) {
		Bitmap result = Bitmap.createBitmap(SIZE_WIDTH, SIZE_HEIGHT,
				Bitmap.Config.ARGB_4444);
		Canvas canvas = new Canvas(result);
		Paint mBitmapPaint = new Paint(Paint.DITHER_FLAG);

		Rect rcSrc = new Rect(0, SIZE_HEIGHT - offset, SIZE_WIDTH, SIZE_HEIGHT);// 下部分
		Rect rcDst = new Rect(0, 0, SIZE_WIDTH, offset);// 上步分
		canvas.drawBitmap(bitmapUp, rcSrc, rcDst, mBitmapPaint);

		rcSrc = new Rect(0, 0, SIZE_WIDTH, SIZE_HEIGHT - offset);// 上半部分，0,0表示上半
		rcDst = new Rect(0, offset, SIZE_WIDTH, SIZE_HEIGHT);// 下半部分
		canvas.drawBitmap(bitmapDown, rcSrc, rcDst, mBitmapPaint);
		return result;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param context
	 * @param resId
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param fileName
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap readBitMap(String fileName) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		return BitmapFactory.decodeFile(fileName, opt);
	}

	/**
	 * 倒影 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bitmap
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);
		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		// Set the Transfer mode to be porter duff and destination in
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		// Draw a rectangle using the paint with our linear gradient
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);

		return bitmapWithReflection;
	}

	/**
	 * 灰度 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bmpOriginal
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap toGrayscale(Bitmap bmpOriginal) {
		int width, height;
		height = bmpOriginal.getHeight();
		width = bmpOriginal.getWidth();

		Bitmap bmpGrayscale = Bitmap.createBitmap(width, height,
				Bitmap.Config.RGB_565);
		Canvas c = new Canvas(bmpGrayscale);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpOriginal, 0, 0, paint);
		return bmpGrayscale;
	}

	/**
	 * 浮雕 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param mBitmap
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap toFuDiao(Bitmap mBitmap) {
		Paint mPaint;

		int mBitmapWidth = 0;
		int mBitmapHeight = 0;

		int mArrayColor[] = null;
		int mArrayColorLengh = 0;
		long startTime = 0;
		int mBackVolume = 0;

		mBitmapWidth = mBitmap.getWidth();
		mBitmapHeight = mBitmap.getHeight();

		Bitmap bmpReturn = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight,
				Bitmap.Config.RGB_565);
		mArrayColorLengh = mBitmapWidth * mBitmapHeight;
		int preColor = 0;
		int prepreColor = 0;
		preColor = mBitmap.getPixel(0, 0);

		for (int i = 0; i < mBitmapWidth; i++) {
			for (int j = 0; j < mBitmapHeight; j++) {
				int curr_color = mBitmap.getPixel(i, j);
				int r = Color.red(curr_color) - Color.red(prepreColor) + 128;
				int g = Color.green(curr_color) - Color.red(prepreColor) + 128;
				int b = Color.green(curr_color) - Color.blue(prepreColor) + 128;
				int a = Color.alpha(curr_color);
				int modif_color = Color.argb(a, r, g, b);
				bmpReturn.setPixel(i, j, modif_color);
				prepreColor = preColor;
				preColor = curr_color;
			}
		}

		Canvas c = new Canvas(bmpReturn);
		Paint paint = new Paint();
		ColorMatrix cm = new ColorMatrix();
		cm.setSaturation(0);
		ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
		paint.setColorFilter(f);
		c.drawBitmap(bmpReturn, 0, 0, paint);

		return bmpReturn;
	}

	/**
	 * 模糊 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bmpSource
	 * @param Blur
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap toMohu(Bitmap bmpSource, int Blur) {
		Bitmap bmpReturn = Bitmap.createBitmap(bmpSource.getWidth(),
				bmpSource.getHeight(), Bitmap.Config.ARGB_8888);
		int pixels[] = new int[bmpSource.getWidth() * bmpSource.getHeight()];
		int pixelsRawSource[] = new int[bmpSource.getWidth()
				* bmpSource.getHeight() * 3];
		int pixelsRawNew[] = new int[bmpSource.getWidth()
				* bmpSource.getHeight() * 3];

		bmpSource.getPixels(pixels, 0, bmpSource.getWidth(), 0, 0,
				bmpSource.getWidth(), bmpSource.getHeight());

		for (int k = 1; k <= Blur; k++) {
			for (int i = 0; i < pixels.length; i++) {
				pixelsRawSource[i * 3 + 0] = Color.red(pixels[i]);
				pixelsRawSource[i * 3 + 1] = Color.green(pixels[i]);
				pixelsRawSource[i * 3 + 2] = Color.blue(pixels[i]);
			}
			int CurrentPixel = bmpSource.getWidth() * 3 + 3;
			for (int i = 0; i < bmpSource.getHeight() - 3; i++) {
				for (int j = 0; j < bmpSource.getWidth() * 3; j++) {
					CurrentPixel += 1;
					int sumColor = 0;
					sumColor = pixelsRawSource[CurrentPixel
							- bmpSource.getWidth() * 3];
					sumColor = sumColor + pixelsRawSource[CurrentPixel - 3];
					sumColor = sumColor + pixelsRawSource[CurrentPixel + 3];
					sumColor = sumColor
							+ pixelsRawSource[CurrentPixel
									+ bmpSource.getWidth() * 3];
					pixelsRawNew[CurrentPixel] = Math.round(sumColor / 4);
				}
			}

			for (int i = 0; i < pixels.length; i++) {
				pixels[i] = Color.rgb(pixelsRawNew[i * 3 + 0],
						pixelsRawNew[i * 3 + 1], pixelsRawNew[i * 3 + 2]);
			}
		}

		bmpReturn.setPixels(pixels, 0, bmpSource.getWidth(), 0, 0,
				bmpSource.getWidth(), bmpSource.getHeight());
		return bmpReturn;
	}

	/**
	 * 积木 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param mBitmap
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap toHeibai(Bitmap mBitmap) {
		Paint mPaint;

		int mBitmapWidth = 0;
		int mBitmapHeight = 0;

		int mArrayColor[] = null;
		int mArrayColorLengh = 0;
		long startTime = 0;
		int mBackVolume = 0;

		mBitmapWidth = mBitmap.getWidth();
		mBitmapHeight = mBitmap.getHeight();
		Bitmap bmpReturn = Bitmap.createBitmap(mBitmapWidth, mBitmapHeight,
				Bitmap.Config.ARGB_8888);
		mArrayColorLengh = mBitmapWidth * mBitmapHeight;
		int count = 0;
		int preColor = 0;
		int color = 0;

		int iPixel = 0;
		for (int i = 0; i < mBitmapWidth; i++) {
			for (int j = 0; j < mBitmapHeight; j++) {
				int curr_color = mBitmap.getPixel(i, j);

				int avg = (Color.red(curr_color) + Color.green(curr_color) + Color
						.blue(curr_color)) / 3;
				if (avg >= 100) {
					iPixel = 255;
				} else {
					iPixel = 0;
				}
				int modif_color = Color.argb(255, iPixel, iPixel, iPixel);

				bmpReturn.setPixel(i, j, modif_color);
			}
		}
		return bmpReturn;
	}

	/**
	 * 油画 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param bmpSource
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap toYouHua(Bitmap bmpSource) {
		Bitmap bmpReturn = Bitmap.createBitmap(bmpSource.getWidth(),
				bmpSource.getHeight(), Bitmap.Config.RGB_565);
		int color = 0;
		int Radio = 0;
		int width = bmpSource.getWidth();
		int height = bmpSource.getHeight();

		Random rnd = new Random();
		int iModel = 10;
		int i = width - iModel;
		while (i > 1) {
			int j = height - iModel;
			while (j > 1) {
				int iPos = rnd.nextInt(100000) % iModel;
				color = bmpSource.getPixel(i + iPos, j + iPos);
				bmpReturn.setPixel(i, j, color);
				j = j - 1;
			}
			i = i - 1;
		}
		return bmpReturn;
	}

	/** 回收Bitmap对象 **/
	public static void recyleBitmap(Bitmap bitmap) {
		if (bitmap != null && !bitmap.isRecycled()) {
			bitmap.recycle();
			bitmap = null;
		}
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param srcPath
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getimage(String srcPath) {
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeFile(srcPath, newOpts);// 此时返回bm为空

		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) {
			be = 1;
		}
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param image
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	private static Bitmap compressImage(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
		int options = 100;
		while (baos.toByteArray().length / 1024 > 100) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
			options -= 10;// 每次都减少10
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
		return bitmap;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param image
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap comp(Bitmap image) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		if (baos.toByteArray().length / 1024 > 200) {// 判断如果图片大于1M,进行压缩避免在生成图片（BitmapFactory.decodeStream）时溢出
			baos.reset();// 重置baos即清空baos
			image.compress(Bitmap.CompressFormat.JPEG, 50, baos);// 这里压缩50%，把压缩后的数据存放到baos中
		}
		ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
		BitmapFactory.Options newOpts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		newOpts.inJustDecodeBounds = true;
		Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		newOpts.inJustDecodeBounds = false;
		int w = newOpts.outWidth;
		int h = newOpts.outHeight;
		// 现在主流手机比较多是800*480分辨率，所以高和宽我们设置为
		float hh = 800f;// 这里设置高度为800f
		float ww = 480f;// 这里设置宽度为480f
		// 缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
		int be = 1;// be=1表示不缩放
		if (w > h && w > ww) {// 如果宽度大的话根据宽度固定大小缩放
			be = (int) (newOpts.outWidth / ww);
		} else if (w < h && h > hh) {// 如果高度高的话根据宽度固定大小缩放
			be = (int) (newOpts.outHeight / hh);
		}
		if (be <= 0) {
			be = 1;
		}
		newOpts.inSampleSize = be;// 设置缩放比例
		// 重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
		isBm = new ByteArrayInputStream(baos.toByteArray());
		bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
		return compressImage(bitmap);// 压缩好比例大小后再进行质量压缩
	}

	/**
	 * 添加到图库
	 */
	public static void galleryAddPic(Context context, String path) {
		Intent mediaScanIntent = new Intent(
				Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		File f = new File(path);
		Uri contentUri = Uri.fromFile(f);
		mediaScanIntent.setData(contentUri);
		context.sendBroadcast(mediaScanIntent);
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param path
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap fitSizeImgDip(String path) {
		if (path == null || path.length() < 1)
			return null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 开始读入图片，此时把options.inJustDecodeBounds 设回true了
		opts.inJustDecodeBounds = true;
		Bitmap resizeBmp = BitmapFactory.decodeFile(path, opts);// 此时返回bm为空
		int w = opts.outWidth;
		int h = opts.outHeight;

		int dip = w * h;
		if (dip < 320 * 480) {
			opts.inSampleSize = 0;
		} else if (dip >= 320 * 480 && dip < 480 * 800) {
			opts.inSampleSize = 1;
		} else if (dip >= 480 * 800 && dip < 960 * 640) {
			opts.inSampleSize = 2;
		} else if (dip >= 960 * 640 && dip < 960 * 640 * 2) {
			opts.inSampleSize = 3;
		} else if (dip >= 960 * 640 * 2 && dip < 960 * 640 * 3) {
			opts.inSampleSize = 4;
		} else if (dip >= 960 * 640 * 4 && dip < 960 * 640 * 5) {
			opts.inSampleSize = 5;
		} else if (dip >= 960 * 640 * 5 && dip < 960 * 640 * 6) {
			opts.inSampleSize = 6;
		} else if (dip >= 960 * 640 * 6 && dip < 960 * 640 * 7) {
			opts.inSampleSize = 7;
		} else if (dip >= 960 * 640 * 7 && dip < 960 * 640 * 8) {
			opts.inSampleSize = 8;
		} else if (dip >= 960 * 640 * 8 && dip < 960 * 640 * 9) {
			opts.inSampleSize = 9;
		} else {
			opts.inSampleSize = 10;
		}
		opts.inJustDecodeBounds = false;

		resizeBmp = BitmapFactory.decodeFile(path, opts);
		return resizeBmp;
	}

	/**
	 * 图片加载本地的方法
	 * 
	 * @param fileName
	 *            本地的路径 根据图片大小 进行伸缩处理
	 * @return
	 */
	public static Bitmap fitSizeImg2(String path, int maxTime) {
		if (0 == maxTime)
			return null;
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		File file = new File(path);
		if (!file.exists()) { // 文件不存在
			return null;
		}
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = false;

		if (file.length() < 512000) { // 小于500K 不压缩
			opts.inSampleSize = 1;
		} else if (file.length() < 819200) { // 500-800K 压缩2
			opts.inSampleSize = 2;
		} else if (file.length() < 1048576) { // 800K-1M 压缩2
			opts.inSampleSize = 4;
		} else { // 大于1M 压缩4
			opts.inSampleSize = 8;
		}

		try {
			resizeBmp = BitmapFactory.decodeFile(file.getAbsolutePath(), opts);
		} catch (OutOfMemoryError e) {
			// TODO: handle exception
			AbLogUtil.d("", "内存过低,释放内存中部分资源");
			ImageLoader.getInstance().clearMemoryCache();
			maxTime--;
			return fitSizeImg(path, maxTime);
		}
		return resizeBmp;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param path
	 * @param size
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap fitSizeImg(String path, int size) {
		if (path == null || path.length() < 1)
			return null;

		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		opts.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		resizeBmp = BitmapFactory.decodeFile(path, opts);
		opts.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = opts.outHeight;
		int w = opts.outWidth;

		int dip = w * h;
		// 数字越大读出的图片占用的heap越小 不然总是溢出
		if (dip <= 200 * 200) {// 图片大于200x200才所小一点
			opts.inSampleSize = 0;
		} else if (dip >= 200 * 200 && dip <= 800 * 600) {
			opts.inSampleSize = 1;
		} else {
			opts.inSampleSize = size;
		}
		resizeBmp = BitmapFactory.decodeFile(path, opts);
		return resizeBmp;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param path
	 * @param size
	 * @param maxX
	 * @param maxY
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap zoomScaleImage(String path, int size, int maxX,
			int maxY) {
		float scale;
		if (TextUtils.isEmpty(path)) {
			return null;
		}

		/** 取原图缩小一点 **/
		File file = new File(path);
		Bitmap resizeBmp = null;
		BitmapFactory.Options opts = new BitmapFactory.Options();
		// 数字越大读出的图片占用的heap越小 不然总是溢出
		opts.inSampleSize = size;
		resizeBmp = BitmapFactory.decodeFile(file.getPath(), opts);

		int imgWidth = resizeBmp.getWidth();
		int imgHeight = resizeBmp.getHeight();

		if (imgWidth > maxX || imgHeight > maxY) {
			float scaleWidth = ((float) maxX) / imgWidth;
			float scaleHeight = ((float) maxY) / imgHeight;

			Matrix matrix = new Matrix();
			matrix.postScale(scaleWidth, scaleHeight);
			Bitmap newBitmap = Bitmap.createBitmap(resizeBmp, 0, 0, imgWidth,
					imgHeight, matrix, true);
			return newBitmap;
		} else {
			return resizeBmp;
		}
	}

	/**
	 * 获取合适大小的快捷方式正方形的图标
	 * 
	 * @param path
	 *            图片路径
	 * @param maxWidth
	 *            图标的最大宽度
	 * @return
	 */
	public static Bitmap getFitBitmap(String path, int maxWidth) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, options);
		final int width = options.outWidth;
		final int height = options.outHeight;
		Log.d("may", "width: " + width + ", height: " + height + ", maxWidth: "
				+ maxWidth);

		final int min = Math.min(width, height);
		int w = maxWidth;
		if (min < maxWidth) {
			w = min;
		}

		// 小图还是使用小图，大图则会缩小
		float scale = w * 1f / min;
		options = new BitmapFactory.Options();
		options.inSampleSize = (int) FloatMath.ceil(scale);

		try {
			Bitmap tmp = BitmapFactory.decodeFile(path, options);
			Matrix matrix = new Matrix();
			matrix.postScale(scale, scale);
			return Bitmap.createBitmap(tmp, 0, 0, width, height, matrix, true);
		} catch (OutOfMemoryError e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param imagePath
	 * @param width
	 * @param height
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getImageThumbnail(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;

		int dip = w * h;

		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
		bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
				ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
		return bitmap;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param imagePath
	 * @param width
	 * @param height
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap getImageThumbnail6x6(String imagePath, int width,
			int height) {
		Bitmap bitmap = null;
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高，注意此处的bitmap为null
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		options.inJustDecodeBounds = false; // 设为 false
		// 计算缩放比
		int h = options.outHeight;
		int w = options.outWidth;

		int beWidth = w / width;
		int beHeight = h / height;
		int be = 1;
		if (beWidth < beHeight) {
			be = beWidth;
		} else {
			be = beHeight;
		}
		if (be <= 0) {
			be = 1;
		}
		options.inSampleSize = be;
		// 重新读入图片，读取缩放后的bitmap，注意这次要把options.inJustDecodeBounds 设为 false
		bitmap = BitmapFactory.decodeFile(imagePath, options);
		if (bitmap != null) {
			int bw = bitmap.getWidth();
			int bh = bitmap.getHeight();
			// 利用ThumbnailUtils来创建缩略图，这里要指定要缩放哪个Bitmap对象
			int fenbianlv = bw * bh;// 图片缩放后的分辨率
			double newfenbianlv = 1;
			if (fenbianlv < 100 * 100) {
				return bitmap;
			} else {
				// 缩放完之后小于200*200就不处理
				bitmap = ThumbnailUtils.extractThumbnail(bitmap, width, height,
						ThumbnailUtils.OPTIONS_RECYCLE_INPUT);
				return bitmap;
			}
		}
		return null;
	}

	/***
	 * 等比缩小图片
	 * 
	 * @param bitmap
	 * @return
	 */
	public static Bitmap scaleBitmap(Bitmap bitmap) {
		float width = bitmap.getWidth();
		float height = bitmap.getHeight();
		float scale = 1.0f;
		if (width > height && width - height >= 15) {
			scale = height / width;
			return BitmapUtil.getScaleBitmap(bitmap, scale, 1.0f);
		} else if (height > width && height - width >= 15) {
			scale = width / height;
			return BitmapUtil.getScaleBitmap(bitmap, 1.0f, scale);
		}
		return bitmap;
	}

	/**
	 * 图片翻转 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param sourceBitmap
	 * @param angle
	 * @return
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	public static Bitmap rotaleBitmap(Bitmap sourceBitmap, int angle) {
		Matrix matrix = new Matrix();
		matrix.postRotate(90); /* 翻转90度 */
		int width = sourceBitmap.getWidth();
		int height = sourceBitmap.getHeight();
		return Bitmap.createBitmap(sourceBitmap, 0, 0, width, height, matrix,
				true);
	}

	/**
	 * 从resource载入Bitmap
	 * 
	 * @param context
	 *            上下文
	 * @param resid
	 *            资源ID
	 * @return
	 */
	public static Bitmap getBitmapFromRes(Context context, int resid) {
		Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
				resid);
		return bitmap;
	}

	/**
	 * 判断图片是否损坏 (简单处理)
	 * 
	 * @param path
	 * @return
	 */
	public static boolean isImgFile(String path) {
		if (TextUtils.isEmpty(path)) {
			return false;
		}
		File file = new File(path);
		return file.exists();
	}
}
