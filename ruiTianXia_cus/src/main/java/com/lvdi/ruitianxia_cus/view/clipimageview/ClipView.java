package com.lvdi.ruitianxia_cus.view.clipimageview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.lvdi.ruitianxia_cus.R;

/**
 * 裁剪框
 * 
 * 
 * 
 */
public class ClipView extends View {

	/**
	 * 边框距左右边界距离，用于调整边框长度
	 */
	public static final int BORDER_DISTANCE = 100;

	public static final int DEFAULT_BORDER_WIDTH = 1;
	public static final int DEFAULT_BORDER_HEIGHT = 1;
	public static final int DEFAULT_BORDER_COLOR = Color.parseColor("#2b9071");

	private Paint mPaint;
	/**
	 * 边框宽高所占比例
	 */
	public static int lineWidth, lineHeight;
	/**
	 * 边框颜色
	 */
	private int lineColor;
	/**
	 * 是否是圆形裁剪框
	 */
	private boolean isRound;

	/** 临时的画布对象 */
	private Bitmap mBitmap;

	/** 临时的画布 */
	private Canvas mCanvas;

	/** 边框宽度 */
	private int borderlengthW;
	/** 边框高度 */
	private int borderlengthH;

	/** 边框画笔宽度 */
	private static final float penWidth = 2f;

	/** 蒙版背景色 */
	private static final int BG_COLOR = 0x66000000;

	public ClipView(Context context) {
		this(context, null);
	}

	public ClipView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ClipView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ClipView, defStyle, 0);

		lineWidth = a.getInteger(R.styleable.ClipView_clip_view_width, DEFAULT_BORDER_WIDTH);
		lineHeight = a.getInteger(R.styleable.ClipView_clip_view_height, DEFAULT_BORDER_HEIGHT);
		lineColor = a.getColor(R.styleable.ClipView_clip_view_color, DEFAULT_BORDER_COLOR);
		isRound = a.getBoolean(R.styleable.ClipView_clip_view_round, false);

		a.recycle();

		mPaint = new Paint();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int width = this.getWidth();
		int height = this.getHeight();
		mPaint.reset();
		mPaint.setAntiAlias(true);
		if (isRound) {
			// 把准备好的bitmap绘制到当前画布上
			canvas.drawBitmap(mBitmap, 0, 0, null);
		} else {
			// 边框长度(宽)，据屏幕左右边缘50px
			borderlengthW = width - BORDER_DISTANCE * 2;
			// 高度 根据比例计算
			borderlengthH = borderlengthW * lineHeight / lineWidth;
			mPaint.setColor(BG_COLOR);
			mPaint.setStyle(Paint.Style.FILL);

			// 以下绘制透明暗色区域 四个矩形
			// top
			canvas.drawRect(0, 0, width, (height - borderlengthH) / 2, mPaint);
			// bottom
			canvas.drawRect(0, (height + borderlengthH) / 2, width, height, mPaint);
			// left
			canvas.drawRect(0, (height - borderlengthH) / 2, BORDER_DISTANCE, (height + borderlengthH) / 2, mPaint);
			// right
			canvas.drawRect(borderlengthW + BORDER_DISTANCE, (height - borderlengthH) / 2, width, (height + borderlengthH) / 2, mPaint);
			// 以下绘制边框线
			mPaint.setColor(lineColor);
			mPaint.setStrokeWidth(penWidth);
			mPaint.setStyle(Paint.Style.STROKE);
			canvas.drawRect(BORDER_DISTANCE, (height - borderlengthH) / 2, width - BORDER_DISTANCE, (height + borderlengthH) / 2, mPaint);
		}

		// 或者下面这种方式
		// top
		// canvas.drawLine(BORDERDISTANCE, (height - borderlengthW) / 2, width -
		// BORDERDISTANCE, (height - borderlengthW) / 2, mPaint);
		// // bottom
		// canvas.drawLine(BORDERDISTANCE, (height + borderlengthW) / 2, width -
		// BORDERDISTANCE, (height + borderlengthW) / 2, mPaint);
		// // left
		// canvas.drawLine(BORDERDISTANCE, (height - borderlengthW) / 2,
		// BORDERDISTANCE, (height + borderlengthW) / 2, mPaint);
		// // right
		// canvas.drawLine(width - BORDERDISTANCE, (height - borderlengthW) / 2,
		// width - BORDERDISTANCE, (height + borderlengthW) / 2, mPaint);
	}

	public int getLineColor() {
		return lineColor;
	}

	public void setLineColor(int lineColor) {
		this.lineColor = lineColor;
	}

	public int getLineWidth() {
		return lineWidth;
	}

	public void setLineWidth(int lineWidth) {
		ClipView.lineWidth = lineWidth;
	}

	public int getLineHeight() {
		return lineHeight;
	}

	public void setLineHeight(int lineHeight) {
		ClipView.lineHeight = lineHeight;
	}

	public boolean isRound() {
		return isRound;
	}

	public void setRound(boolean isRound) {
		this.isRound = isRound;
	}

	/**
	 * 设置圆形区域及背景
	 * 
	 * @param width
	 *            背景宽
	 * @param height
	 *            背景高
	 */
	public void setRoundArea(int width, int height) {
		borderlengthH = borderlengthW = width - BORDER_DISTANCE * 2;
		mPaint.reset();
		mPaint.setColor(lineColor);
		mPaint.setStrokeWidth(penWidth);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setAntiAlias(true);
		mBitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);
		mCanvas = new Canvas(mBitmap);
		// 画灰色蒙版
		mCanvas.drawColor(BG_COLOR);
		// 画边框白色线
		mCanvas.drawCircle(width / 2f, height / 2f, borderlengthH / 2f, mPaint);

		// 设置画笔
		mPaint.reset();
		mPaint.setAlpha(0);
		// 只在源图像和目标图像相交的地方绘制目标图像
		mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStrokeWidth(1); // 笔宽
		// 痕迹
		Path mPath = new Path();
		// 减去画笔宽度
		mPath.addCircle(width / 2f, height / 2f, borderlengthH / 2f - penWidth, Direction.CCW);
		// 画高亮透明的实心圆
		mCanvas.drawPath(mPath, mPaint);
	}

}