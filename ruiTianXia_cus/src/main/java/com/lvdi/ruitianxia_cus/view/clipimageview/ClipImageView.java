package com.lvdi.ruitianxia_cus.view.clipimageview;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;

import com.ab.util.AbToastUtil;
import com.lvdi.ruitianxia_cus.util.TouchManager;
import com.lvdi.ruitianxia_cus.util.Vector2D;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 裁剪图片 并进行缩放 在裁剪框内进行移动
 * 
 * 
 * 
 */
public class ClipImageView extends ImageView implements OnTouchListener {
	private static final Bitmap.Config BITMAP_CONFIG = Bitmap.Config.ARGB_8888;
	private static final int COLORDRAWABLE_DIMENSION = 1;
	private Bitmap bitmap;
	private int height;
	private int width;
	private boolean isInitialized = false;
	private Vector2D position = new Vector2D();
	private float scale = 1.0F;
	public TouchManager touchManager = new TouchManager(2);
	private Matrix transform = new Matrix();
	private Paint mPaint = new Paint();
	/** 初始缩放倍数是否大于最大缩放倍数 */
	private boolean flag = false;
	/** 最大缩放倍数 */
	private static final int MAX_SCALE = 12;

	private static final int BORDER_DISTANCE = ClipView.BORDER_DISTANCE;

	/**
	 * 边框宽度
	 */
	private int borderlengthW;

	/**
	 * 边框高度
	 */
	private int borderlengthH;

	public ClipImageView(Context context) {
		super(context);
		init();
	}

	public ClipImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		super.setScaleType(ScaleType.CENTER_CROP);
		setOnTouchListener(this);
	}

	protected void onDraw(Canvas canvas) {
		if (getDrawable() == null) {
			return;
		}
		if (bitmap == null) {
			return;
		}
		borderlengthW = (int) (getWidth() - BORDER_DISTANCE * 2);
		borderlengthH = borderlengthW * ClipView.lineHeight
				/ ClipView.lineWidth;

		this.width = this.bitmap.getWidth();
		this.height = this.bitmap.getHeight();

		// 缩放的时候防止移动出边框
		// this.width*scale/2 代表缩放后的图片中心点
		if (position.getY() > getHeight() / 2f + this.height * scale / 2f
				- borderlengthH / 2f) {
			// bottom
			position.set(position.getX(), getHeight() / 2f + this.height
					* scale / 2f - borderlengthH / 2f);
		}
		if (position.getY() <= (getHeight() + borderlengthH) / 2f - this.height
				* scale / 2f) {
			// top
			position.set(position.getX(), (getHeight() + borderlengthH) / 2f
					- this.height * scale / 2f);
		}
		if (position.getX() < getWidth() - this.width * scale / 2f
				- BORDER_DISTANCE) {
			// right
			position.set(
					getWidth() - this.width * scale / 2f - BORDER_DISTANCE,
					position.getY());
		}
		if (position.getX() > this.width * scale / 2f + BORDER_DISTANCE) {
			// left
			position.set(this.width * scale / 2f + BORDER_DISTANCE,
					position.getY());
		}
		/**
		 * 首先按照屏幕边框进行缩放，保证不超出屏幕 如果没有充满选择框，则以选择框为标准进行缩放
		 * 如果缩放倍数超出最大缩放倍数，则不进行缩放，固定为选择框大小
		 */
		if (!this.isInitialized) {
			int w = getWidth();
			int h = getHeight();
			this.position.set(w / 2f, h / 2f);
			// 等比例自动缩放图片适应控件
			float f1 = (float) w / width;
			float f2 = (float) h / height;
			scale = f1 < f2 ? f1 : f2;
			if (scale >= MAX_SCALE || width * scale <= borderlengthW
					|| height * scale <= borderlengthH) {
				flag = true;
				// 按照图片边框缩放
				float f11 = (float) borderlengthW / width;
				float f22 = (float) borderlengthH / height;
				scale = f11 > f22 ? f11 : f22;
			}
			this.isInitialized = true;
		}
		this.transform.reset();
		this.transform.postTranslate(-this.width / 2.0F, -this.height / 2.0F);
		this.transform.postScale(this.scale, this.scale);
		this.transform
				.postTranslate(this.position.getX(), this.position.getY());
		canvas.drawBitmap(this.bitmap, this.transform, mPaint);
	}

	public boolean onTouch(View paramView, MotionEvent event) {
		this.touchManager.update(event);
		if (this.touchManager.getPressCount() == 1) {
			this.position.add(touchManager.moveDelta(0));
		} else if (this.touchManager.getPressCount() >= 2) {
			Vector2D localVector2D1 = this.touchManager.getVector(0, 1);
			Vector2D localVector2D2 = this.touchManager.getPreviousVector(0, 1);
			float f1 = localVector2D1.getLength();
			float f2 = localVector2D2.getLength();
			if (scale >= MAX_SCALE && flag) {
				// 初始缩放倍数过大则不进行缩放
				return true;
			}
			if (f2 != 0.0F) {
				// 缩放倍数限制(0.2f-3f)
				this.scale *= f1 / f2;
			}
			if (scale >= MAX_SCALE) {
				scale = MAX_SCALE;
			} else {
				if (width * scale <= borderlengthW
						|| height * scale <= borderlengthH) {
					// 保证缩放不小于边框
					float f11 = (float) borderlengthW / width;
					float f22 = (float) borderlengthH / height;
					scale = f11 > f22 ? f11 : f22;
				}
			}
		}
		// 调用一次会执行一次onDraw方法
		invalidate();
		return true;
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		bitmap = bm;
	}

	@Override
	public void setImageDrawable(Drawable drawable) {
		super.setImageDrawable(drawable);
		bitmap = getBitmapFromDrawable(drawable);
	}

	@Override
	public void setImageResource(int resId) {
		super.setImageResource(resId);
		bitmap = getBitmapFromDrawable(getDrawable());
	}

	@Override
	public void setImageURI(Uri uri) {
		super.setImageURI(uri);
		bitmap = getBitmapFromDrawable(getDrawable());
	}

	private Bitmap getBitmapFromDrawable(Drawable drawable) {
		if (drawable == null) {
			return null;
		}

		if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		try {
			Bitmap bitmap;

			if (drawable instanceof ColorDrawable) {
				bitmap = Bitmap.createBitmap(COLORDRAWABLE_DIMENSION,
						COLORDRAWABLE_DIMENSION, BITMAP_CONFIG);
			} else {
				bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(),
						drawable.getIntrinsicHeight(), BITMAP_CONFIG);
			}

			Canvas canvas = new Canvas(bitmap);
			drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
			drawable.draw(canvas);
			return bitmap;
		} catch (OutOfMemoryError e) {
			return null;
		}
	}

	/**
	 * 剪切图片，返回剪切后的bitmap对象
	 * 
	 * @return
	 */
	public Bitmap clip() {
		Bitmap bitmap;
		try {
			bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
					Bitmap.Config.RGB_565);
		} catch (OutOfMemoryError e) {
			ImageLoader.getInstance().clearMemoryCache();
			AbToastUtil.showToast(getContext(), "内存过低,释放内存中部分资源");
			return clip();
		}
		Canvas canvas = new Canvas(bitmap);
		Matrix matrix = new Matrix();
		matrix.setScale(0.5f, 0.5f);
		draw(canvas);
		// 凑整，防止出现黑边
		bitmap = Bitmap.createBitmap(bitmap,
				(int) Math.ceil((getWidth() - borderlengthW) / 2f),
				(int) Math.ceil((getHeight() - borderlengthH) / 2f),
				borderlengthW, borderlengthH, matrix, false);
		return bitmap;
	}

	// public Bitmap getClip() {
	// Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
	// Bitmap.Config.ARGB_8888);
	// Canvas canvas = new Canvas(bitmap);
	// draw(canvas);
	// return Bitmap
	// .createBitmap(bitmap, (getWidth() - borderlengthW) / 2,
	// (getHeight() - borderlengthH) / 2, borderlengthW,
	// borderlengthH);
	// }

	public void clearBitmap() {
		if (null != bitmap && !bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}

}