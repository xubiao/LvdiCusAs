package com.lvdi.ruitianxia_cus.view;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.R;

public class SnakeLayout extends FrameLayout {
	private static final String LOG_TAG = "SnakeLayout";
	private GestureDetector mGestureDetector;
	private SnakeOnGestureListener mGestureListener;
	private List<View> ViewHolder;
	private int selectImg;
	private int totalViewNum;
	private View mContentView;
	private SnakeView ScrollView;
	private Context mContext;

	private enum State {
		ABOUT_TO_ANIMATE, ANIMATING, ANIMATE_END, READY, TRACKING
	};

	private State mState;
	private double aniStartPos;// Value = scrollNum + percent*direction;
	private double aniStopPos;// Value = scrollNum + percent*direction;
	private Date aniStartTime;
	private long aniTime = 1000;
	private double aniSpeed = 500;
	private double aniDefG = 500;

	private int mContentWidth = 0;
	private int mContentHeight = 0;
	private int clickItem = -1;
	private int direction = 0;
	private int movDirection = 0;
	private double percent = 0;
	private int scrollNum = 0;

	private PathScale myPathViews;

	private List<AppCardView> BmpRecViews;
	private OnSelectListener selectListener;
	private OnClickListener clickListener;
	private int currentIndex = 0;

	public SnakeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
		Log.d(LOG_TAG, "Init Snake Layout");
		mContext = context;
		TypedArray a = context.obtainStyledAttributes(attrs,
				R.styleable.SnakeLayout);
		selectImg = a.getInteger(R.styleable.SnakeLayout_selectImg, -1);
		a.recycle();
		mGestureListener = new SnakeOnGestureListener();
		mGestureDetector = new GestureDetector(mGestureListener);
		mGestureDetector.setIsLongpressEnabled(true);
		BmpRecViews = new ArrayList<AppCardView>();
		myPathViews = new PathScale();
		mState = State.READY;
	}

	public void Init() {
		int size = BmpRecViews.size();
		for (int i = 0; i < totalViewNum; i++) {
			ImageView v = (ImageView) ViewHolder.get(i);
			v.setScaleType(ImageView.ScaleType.FIT_XY);
			View view = BmpRecViews.get((i + currentIndex) % size);
			view.invalidate();
			v.setImageBitmap(view.getDrawingCache());
		}
	}

	public void onRefresh(int index) {
		int size = BmpRecViews.size();
		if (size == 0)
			return;
		for (int i = 0; i < totalViewNum; i++) {
			if ((i + currentIndex) % size == index) {
				ImageView v = (ImageView) ViewHolder.get(i);
				AppCardView view = BmpRecViews.get((i + currentIndex) % size);
				view.invalidate();
				v.setImageBitmap(view.getDrawingCache());
			}
		}
		ScrollView.invalidate();
	}

	public void addRec(List<AppCardView> list) {
		currentIndex = 0;
		BmpRecViews.clear();
		if (null != list && list.size() > 0) {
			BmpRecViews.addAll(list);
		}
	}

	private class PathScale {
		private int totalNum;
		List<Double> myLen;
		List<Rect> myRect;
		int recentScrollNum = 0;
		double RecentPercent = 0;

		public PathScale() {
			totalNum = 0;
			myLen = new ArrayList<Double>();
			myRect = new ArrayList<Rect>();
		}

		public double getAverageLen() {
			double len = 0;
			if (totalNum < 2)
				return len;
			for (int i = 0; i < totalNum - 2; i++) {
				len += myLen.get(i);
			}
			return len / (totalNum - 1);
		}

		public int size() {
			return totalNum;
		}

		public void addPointView(int left, int top, int right, int bottom) {
			totalNum++;
			Log.d(LOG_TAG, "total num : " + totalNum);
			Rect r = new Rect(left, top, right, bottom);
			myRect.add(r);
			if (totalNum > 1) {
				Rect r0 = myRect.get(totalNum - 2);
				int dx = r0.centerX() - r.centerX();
				int dy = r0.centerY() - r.centerY();
				double len = Math.sqrt(dx * dx + dy * dy);
				myLen.add(len);
			}
		}

		public int getRecentScrollNum() {
			return recentScrollNum;
		}

		public double getPercent() {
			return RecentPercent;
		}

		public void computeScroll(int clickNum, int dir, double len) {
			double remain = len;
			recentScrollNum = 0;
			RecentPercent = 0;
			if (dir == 1) {
				for (int i = clickNum;; i++) {
					if (i > totalNum - 2)
						i = totalNum - 2;
					if (i < 0)
						i = 0;
					double tmp = myLen.get(i);
					if (remain >= tmp) {
						recentScrollNum++;
						remain -= tmp;
					} else {
						RecentPercent = remain / tmp;
						break;
					}
				}
			} else if (dir == -1) {
				for (int i = clickNum;; i--) {
					if (i - 1 > totalNum - 2)
						i = totalNum - 1;
					if (i - 1 < 0)
						i = 1;
					double tmp = myLen.get(i - 1);
					if (remain >= tmp) {
						recentScrollNum--;
						remain -= tmp;
					} else {
						RecentPercent = remain / tmp;
						break;
					}
				}
			}
		}

		public Rect getMoveRect(int selectNum) {
			if (direction == 1) {
				int newIndex = selectNum + scrollNum;
				if (newIndex >= totalNum - 1) {
					Rect r0 = myRect.get(totalNum - 1);
					Rect r1 = myRect.get(totalNum - 2);
					int nx = (int) (r0.centerX() - (r1.centerX() - r0.centerX())
							* percent);
					int ny = (int) (r0.centerY() - (r1.centerY() - r0.centerY())
							* percent);
					int nw = (int) (r0.width() - (r1.width() - r0.width())
							* percent);
					int nh = (int) (r0.height() - (r1.height() - r0.height())
							* percent);
					return new Rect(nx - nw / 2, ny - nh / 2, nx + nw / 2, ny
							+ nh / 2);
				} else if (newIndex == -1) {
					Rect r0 = myRect.get(0);
					Rect r1 = myRect.get(1);
					int nx = (int) (r0.centerX() - (r1.centerX() - r0.centerX())
							* (1 - percent));
					int ny = (int) (r0.centerY() - (r1.centerY() - r0.centerY())
							* (1 - percent));
					int nw = (int) (r0.width() - (r1.width() - r0.width())
							* (1 - percent));
					int nh = (int) (r0.height() - (r1.height() - r0.height())
							* (1 - percent));
					return new Rect(nx - nw / 2, ny - nh / 2, nx + nw / 2, ny
							+ nh / 2);
				} else if (newIndex > totalNum - 1 || newIndex < -1) {
					return null;
				} else {
					Rect r0 = myRect.get(newIndex);
					Rect r1 = myRect.get(newIndex + 1);
					int nx = (int) (r0.centerX() + (r1.centerX() - r0.centerX())
							* percent);
					int ny = (int) (r0.centerY() + (r1.centerY() - r0.centerY())
							* percent);
					int nw = (int) (r0.width() + (r1.width() - r0.width())
							* percent);
					int nh = (int) (r0.height() + (r1.height() - r0.height())
							* percent);
					return new Rect(nx - nw / 2, ny - nh / 2, nx + nw / 2, ny
							+ nh / 2);
				}
			} else if (direction == -1) {
				int newIndex = selectNum + scrollNum;
				if (newIndex == 0) {
					Rect r0 = myRect.get(0);
					Rect r1 = myRect.get(1);
					int nx = (int) (r0.centerX() - (r1.centerX() - r0.centerX())
							* percent);
					int ny = (int) (r0.centerY() - (r1.centerY() - r0.centerY())
							* percent);
					int nw = (int) (r0.width() - (r1.width() - r0.width())
							* percent);
					int nh = (int) (r0.height() - (r1.height() - r0.height())
							* percent);
					return new Rect(nx - nw / 2, ny - nh / 2, nx + nw / 2, ny
							+ nh / 2);
				} else if (newIndex == totalNum) {
					Rect r0 = myRect.get(totalNum - 1);
					Rect r1 = myRect.get(totalNum - 2);
					int nx = (int) (r0.centerX() - (r1.centerX() - r0.centerX())
							* (1 - percent));
					int ny = (int) (r0.centerY() - (r1.centerY() - r0.centerY())
							* (1 - percent));
					int nw = (int) (r0.width() - (r1.width() - r0.width())
							* (1 - percent));
					int nh = (int) (r0.height() - (r1.height() - r0.height())
							* (1 - percent));
					return new Rect(nx - nw / 2, ny - nh / 2, nx + nw / 2, ny
							+ nh / 2);
				} else if (newIndex > totalNum || newIndex < 0) {
					return null;
				} else {
					Rect r0 = myRect.get(newIndex);
					Rect r1 = myRect.get(newIndex - 1);
					int nx = (int) (r0.centerX() + (r1.centerX() - r0.centerX())
							* percent);
					int ny = (int) (r0.centerY() + (r1.centerY() - r0.centerY())
							* percent);
					int nw = (int) (r0.width() + (r1.width() - r0.width())
							* percent);
					int nh = (int) (r0.height() + (r1.height() - r0.height())
							* percent);
					return new Rect(nx - nw / 2, ny - nh / 2, nx + nw / 2, ny
							+ nh / 2);
				}
			} else {
				return null;
			}
		}
	}

	public static interface OnSelectListener {
		public void onSelected(int position);
	}

	public void setOnSelectListener(OnSelectListener l) {
		selectListener = l;
	}

	public static interface OnClickListener {
		public void onClicked(int position);

		public void onLongClicked(int position);

		public void onMove();

		// public void onUp();
	}

	public void setOnClickListener(OnClickListener l) {
		clickListener = l;
	}

	protected void onFinishInflate() {
		super.onFinishInflate();
		Log.d(LOG_TAG, "onFinishInflate");
		ViewHolder = new ArrayList<View>();
		View v = findViewById(R.id.snakeImg0);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg1);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg2);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg3);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg4);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg5);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg6);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg7);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg8);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg9);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg10);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg11);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg12);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg13);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg14);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg15);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg16);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg17);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg18);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg19);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg20);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg21);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg22);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg23);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg24);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg25);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg26);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg27);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg28);
		if (v != null)
			ViewHolder.add(v);
		v = findViewById(R.id.snakeImg29);
		if (v != null)
			ViewHolder.add(v);

		mContentView = findViewById(R.id.snakeContent);

		totalViewNum = ViewHolder.size();
		if (totalViewNum < 3) {
			throw new RuntimeException(
					"Please confirm that there are more than Three "
							+ "ImageViews with id snakeImg* in your xml file, * is a interger");
		}
		if (selectImg == -1)
			selectImg = totalViewNum / 2;

		this.setOnTouchListener(touchListener);
		this.setLongClickable(true);
	}

	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
		if (mContentView != null) {
			mContentWidth = mContentView.getWidth();
			mContentHeight = mContentView.getHeight();
		}
		if (myPathViews.size() == 0) {
			for (int i = 0; i < totalViewNum; i++) {
				View v = ViewHolder.get(i);
				int left = getLeft(v), top = getTop(v);
				int right = getRight(v), bottom = getBottom(v);
				myPathViews.addPointView(left, top, right, bottom);
			}
		}
		Log.d(LOG_TAG, "onLayout mContentWidth:" + mContentWidth
				+ " mContentHeight:" + mContentHeight);
	}

	protected void dispatchDraw(Canvas canvas) {
		Log.d(LOG_TAG, "dispatchDraw");
		if (ScrollView == null) {
			ScrollView = new SnakeView(mContext);
			ScrollView.setLayoutParams(new LayoutParams(mContentWidth,
					mContentHeight));
			addView(ScrollView);
		}
		super.dispatchDraw(canvas);
	}

	public void getGlobalLocation(View v, int location[]) {
		int v_location[] = new int[2], p_location[] = new int[2];
		v.getLocationOnScreen(v_location);
		mContentView.getLocationOnScreen(p_location);
		location[0] = v_location[0] - p_location[0];
		location[1] = v_location[1] - p_location[1];
	}

	public int getLeft(View v) {
		int v_location[] = new int[2], p_location[] = new int[2];
		v.getLocationOnScreen(v_location);
		int l = ((ImageView) v).getPaddingLeft();
		mContentView.getLocationOnScreen(p_location);
		return l + v_location[0] - p_location[0];
	}

	public int getRight(View v) {
		int v_location[] = new int[2], p_location[] = new int[2];
		v.getLocationOnScreen(v_location);
		int r = ((ImageView) v).getPaddingRight();
		mContentView.getLocationOnScreen(p_location);
		return v.getWidth() - r + v_location[0] - p_location[0];
	}

	public int getTop(View v) {
		int v_location[] = new int[2], p_location[] = new int[2];
		v.getLocationOnScreen(v_location);
		int t = ((ImageView) v).getPaddingTop();
		mContentView.getLocationOnScreen(p_location);
		return t + v_location[1] - p_location[1];
	}

	public int getBottom(View v) {
		int v_location[] = new int[2], p_location[] = new int[2];
		v.getLocationOnScreen(v_location);
		int b = ((ImageView) v).getPaddingBottom();
		mContentView.getLocationOnScreen(p_location);
		return v.getHeight() - b + v_location[1] - p_location[1];
	}

	private float deatY;
	private float lastY;

	OnTouchListener touchListener = new OnTouchListener() {
		public boolean onTouch(View v, MotionEvent event) {
			Log.d(LOG_TAG,
					"onTouch " + " x: " + event.getX() + " y: " + event.getY());
			int action = event.getAction();
			if (action == MotionEvent.ACTION_UP) {
				Log.d(LOG_TAG, "ACTION_UP " + percent);
				if (mState == State.TRACKING) {
					aniStartPos = 1.0 * scrollNum + direction * percent;
					aniStopPos = scrollNum;
					if (percent > 0.5) {
						aniStopPos += direction;
					}
					aniTime = (long) (1000 * percent);
					mState = State.ABOUT_TO_ANIMATE;
					aniStartTime = new Date();
					ScrollView.invalidate();
				}
				deatY = 0;
			} else if (action == MotionEvent.ACTION_DOWN) {
				lastY = event.getY();

			} else if (action == MotionEvent.ACTION_MOVE) {
				float y = event.getY();
				deatY = y - lastY;
				lastY = y;
				Log.d(LOG_TAG, "move");
			}
			mGestureDetector.onTouchEvent(event);
			return false;
		}
	};

	public void moveView() {
		Log.d(LOG_TAG, "move View");
		clickListener.onMove();
		if (direction != 0) {
			if (percent > 1)
				percent = 1;

			if (direction == -1) {
				int size = BmpRecViews.size();
				for (int i = 0; i < totalViewNum + 1; i++) {
					Rect r = myPathViews.getMoveRect(i - scrollNum);
					if (r == null)
						continue;
					int bmpIndex = (i - scrollNum + currentIndex) % size;
					while (bmpIndex < 0)
						bmpIndex += size;
					// BmpRecViews.get(bmpIndex).invalidate();
					// Bitmap b = BmpRecViews.get(bmpIndex).getDrawingCache();
					AppCardView b = BmpRecViews.get(bmpIndex);
					if (b != null) {
						ScrollView.addView(BmpRecViews.get(bmpIndex), r.left,
								r.top, r.width(), r.height());
					}
				}
			} else if (direction == 1) {
				int size = BmpRecViews.size();
				for (int i = -1; i < totalViewNum; i++) {
					Rect r = myPathViews.getMoveRect(i - scrollNum);
					if (r == null)
						continue;
					int bmpIndex = (i - scrollNum + currentIndex) % size;
					while (bmpIndex < 0)
						bmpIndex += size;
					// BmpRecViews.get(bmpIndex).invalidate();
					// Bitmap b = BmpRecViews.get(bmpIndex).getDrawingCache();
					AppCardView b = BmpRecViews.get(bmpIndex);
					if (b != null) {
						ScrollView.addView(b, r.left, r.top, r.width(),
								r.height());
					}
				}
			}
		}
	}

	class SnakeOnGestureListener implements OnGestureListener {
		int initX;
		int initY;
		int scrollView;
		int mClickView = -1;

		public boolean onDown(MotionEvent event) {
			Log.d(LOG_TAG, "onDown");
			initX = (int) event.getX();
			initY = (int) event.getY();
			if (mState == State.ANIMATING) {
				mState = State.TRACKING;
			} else {
				direction = 0;
				percent = 0;
				scrollNum = 0;
				aniSpeed = 0;
				for (int i = 0; i < totalViewNum; i++) {
					ImageView tempView = (ImageView) ViewHolder.get(i);
					int local[] = new int[2];
					getGlobalLocation(tempView, local);
					int l = getLeft(tempView);
					int t = getTop(tempView);
					int w = getRight(tempView) - l;
					int h = getBottom(tempView) - t;
					tempView.setVisibility(View.INVISIBLE);
					int newIndex = i + currentIndex;
					int size = BmpRecViews.size();
					while (newIndex < 0) {
						newIndex += size;
					}
					newIndex = newIndex % size;
					// Bitmap b = BmpRecViews.get(newIndex).getDrawingCache();
					AppCardView b = BmpRecViews.get(newIndex);
					if (b != null) {
						ScrollView.addView(b, l, t, w, h);
					}
				}
			}
			return false;
		}

		public boolean onFling(MotionEvent e1, MotionEvent e2, float vx,
				float vy) {
			aniSpeed = Math.sqrt(vx * vx + vy * vy);
			double len = myPathViews.getAverageLen();
			double v = aniSpeed / (2 * len);
			aniTime = (long) (v * 1000 / aniDefG);
			double s = v * v / (2 * aniDefG);
			Log.d(LOG_TAG, "onFling,velocityX:" + vx + " velocityY:" + vy
					+ " aniTime:" + aniTime + " Fling s:" + s);
			aniStartPos = 1.0 * scrollNum + direction * percent;
			aniStopPos = aniStartPos + direction * s;
			aniStopPos = Math.round(aniStopPos);
			mState = State.ABOUT_TO_ANIMATE;
			aniStartTime = new Date();
			ScrollView.invalidate();

			return false;
		}

		public void onLongPress(MotionEvent e) {
			Log.d(LOG_TAG, "onLongPress");

			mClickView = getClickView((int) e.getX(), (int) e.getY());
			Log.d(LOG_TAG, "View " + mClickView + " is clicked!");
			clickItem = mClickView;
			if (clickListener != null) {
				int s = currentIndex + clickItem, size = BmpRecViews.size();
				s %= size;
				if (s < 0)
					s += size;
				if (select == s) {
					clickListener.onLongClicked(s);
					// Toast.makeText(mContext, "onLongPress",
					// Toast.LENGTH_SHORT)
					// .show();
				}
			}
		}

		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			Log.d(LOG_TAG, "onScroll, distanceX:" + distanceX + "distanceY:"
					+ distanceY);
			mState = State.TRACKING;
			scrollView = getNearClickView(initX, initY);
			double dx = e2.getX() - initX;
			double dy = e2.getY() - initY;
			initX = (int) e2.getX();
			initY = (int) e2.getY();
			// compute the direction
			if (scrollView != 0 && scrollView != totalViewNum - 1) {
				View v0 = ViewHolder.get(scrollView);
				View v1 = ViewHolder.get(scrollView - 1);
				View v2 = ViewHolder.get(scrollView + 1);
				double len1, len2;
				double x0 = (getLeft(v0) + getRight(v0)) * 1.0 / 2;
				double y0 = (getTop(v0) + getBottom(v0)) * 1.0 / 2;
				double x1 = (getLeft(v1) + getRight(v1)) * 1.0 / 2;
				double y1 = (getTop(v1) + getBottom(v1)) * 1.0 / 2;
				double x2 = (getLeft(v2) + getRight(v2)) * 1.0 / 2;
				double y2 = (getTop(v2) + getBottom(v2)) * 1.0 / 2;
				double dx1 = x1 - x0, dy1 = y1 - y0;
				double dx2 = x2 - x0, dy2 = y2 - y0;
				len1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
				len2 = Math.sqrt(dx2 * dx2 + dy2 * dy2);
				double r1 = (dx1 * dx + dy1 * dy) / len1, r2 = (dx2 * dx + dy2
						* dy)
						/ len2;
				if (r1 > r2 && r1 > 0) {
					movDirection = -1;
					myPathViews.computeScroll(scrollView, movDirection, r1);
				} else if (r2 > r1 && r2 > 0) {
					movDirection = 1;
					myPathViews.computeScroll(scrollView, movDirection, r2);
				}
			} else if (scrollView == 0) {
				View v0 = ViewHolder.get(scrollView);
				View v1 = ViewHolder.get(scrollView + 1);
				double len1;
				double x0 = (getLeft(v0) + getRight(v0)) * 1.0 / 2;
				double y0 = (getTop(v0) + getBottom(v0)) * 1.0 / 2;
				double x1 = (getLeft(v1) + getRight(v1)) * 1.0 / 2;
				double y1 = (getTop(v1) + getBottom(v1)) * 1.0 / 2;
				double dx1 = x1 - x0, dy1 = y1 - y0;
				len1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
				double r1 = (dx1 * dx + dy1 * dy) / len1;
				if (r1 > 0) {
					movDirection = 1;
					myPathViews.computeScroll(scrollView, movDirection, r1);
				} else {
					movDirection = -1;
					myPathViews.computeScroll(scrollView, movDirection, -r1);
				}
			} else if (scrollView == totalViewNum - 1) {
				View v0 = ViewHolder.get(scrollView);
				View v1 = ViewHolder.get(scrollView - 1);
				double len1;
				double x0 = (getLeft(v0) + getRight(v0)) * 1.0 / 2;
				double y0 = (getTop(v0) + getBottom(v0)) * 1.0 / 2;
				double x1 = (getLeft(v1) + getRight(v1)) * 1.0 / 2;
				double y1 = (getTop(v1) + getBottom(v1)) * 1.0 / 2;
				double dx1 = x1 - x0, dy1 = y1 - y0;
				len1 = Math.sqrt(dx1 * dx1 + dy1 * dy1);
				double r1 = (dx1 * dx + dy1 * dy) / len1;
				if (r1 > 0) {
					movDirection = -1;
					myPathViews.computeScroll(scrollView, movDirection, r1);
				} else {
					movDirection = 1;
					myPathViews.computeScroll(scrollView, movDirection, -r1);
				}
			}
			if (direction == 0)
				direction = movDirection;
			double pos = scrollNum * 1.0 + percent * direction
					+ myPathViews.getPercent() * movDirection
					+ myPathViews.getRecentScrollNum() * 1.0;
			if (pos > 0)
				direction = 1;
			else
				direction = -1;
			scrollNum = (int) pos;
			percent = Math.abs(pos - 1.0 * scrollNum);

			Log.d(LOG_TAG, "pos:" + pos + " scrollNum:" + scrollNum
					+ " percent:" + percent + " direction = " + direction);
			// move the view
			ScrollView.clear();
			moveView();
			ScrollView.invalidate();
			return false;
		}

		public void onShowPress(MotionEvent e) {
			Log.d(LOG_TAG, "onShowPress");
		}

		public boolean onSingleTapUp(MotionEvent e) {
			Log.d(LOG_TAG, "onSingleTapUp");
			if (mState == State.ANIMATING) {
				mState = State.TRACKING;
			} else if (mState == State.READY) {
				mClickView = getClickView((int) e.getX(), (int) e.getY());
				Log.d(LOG_TAG, "View " + mClickView + " is clicked!");
				clickItem = mClickView;
				if (clickListener != null) {
					int s = currentIndex + clickItem, size = BmpRecViews.size();
					s %= size;
					if (s < 0)
						s += size;
					if (select == s) {
						clickListener.onClicked(s);
						// Toast.makeText(mContext, "click", Toast.LENGTH_SHORT)
						// .show();
					}
				}
				if (mClickView != -1) {
					aniStopPos = selectImg - mClickView;
					if (aniStopPos == 0)
						return false;
					if (aniStopPos > 0)
						direction = 1;
					else
						direction = -1;
					aniStartPos = 0;
					aniTime = (long) Math.abs(500 * aniStopPos);
					mState = State.ABOUT_TO_ANIMATE;
					aniStartTime = new Date();
					ScrollView.invalidate();
				}
			}
			return false;
		}
	}

	public void setSelected(int position) {
		currentIndex = position - selectImg;
	}

	public void setSelectImg(int selectImg) {
		this.selectImg = selectImg;
	}

	public void setSelectedWithAnimation(int position) {
		int size = BmpRecViews.size();
		currentIndex %= size;
		if (currentIndex < 0)
			currentIndex += size;
		aniStopPos = position - selectImg - currentIndex;
		if (aniStopPos == 0)
			return;
		if (aniStopPos > 0)
			direction = 1;
		else
			direction = -1;
		aniStartPos = 0;
		aniTime = 1000;
		mState = State.ABOUT_TO_ANIMATE;
		aniStartTime = new Date();
		ScrollView.invalidate();
	}

	public void setSelectedWithAnimation(int position, int selectImg) {
		int size = BmpRecViews.size();
		currentIndex %= size;
		if (currentIndex < 0)
			currentIndex += size;
		aniStopPos = selectImg - position - size;// position - selectImg -
													// currentIndex;
		if (aniStopPos == 0)
			return;
		if (aniStopPos > 0)
			direction = 1;
		else
			direction = -1;
		aniStartPos = 0;
		aniTime = 1000;
		mState = State.ABOUT_TO_ANIMATE;
		aniStartTime = new Date();
		ScrollView.invalidate();
	}

	private int getClickView(int x, int y) {
		int cn = -1;
		View v;
		for (int i = 0; i < totalViewNum; i++) {
			v = ViewHolder.get(i);
			if (v == null)
				continue;
			int l = getLeft(v), r = getRight(v);
			int t = getTop(v), b = getBottom(v);
			if (l < x && x < r && t < y && y < b) {
				cn = i;
			}
		}

		return cn;
	}

	public int getNearClickView(int x, int y) {
		int minLen = 10000, cn = -1;
		View v;
		for (int i = 0; i < totalViewNum; i++) {
			v = ViewHolder.get(i);
			if (v == null)
				continue;
			int vx = (getLeft(v) + getRight(v)) / 2;
			int vy = (getTop(v) + getBottom(v)) / 2;
			int len = (int) Math
					.sqrt((x - vx) * (x - vx) + (y - vy) * (y - vy));
			if (len < minLen) {
				minLen = len;
				cn = i;
			}
		}
		return cn;
	}

	public boolean getAnimateData() {
		long time = aniTime;
		Date date = new Date();
		long t = date.getTime() - aniStartTime.getTime();
		if (t < 0 || t > time) {
			mState = State.ANIMATE_END;
			return false;
		} else {
			mState = State.ANIMATING;
			double s = aniStopPos - aniStartPos;
			double g = 2 * s / (time * time);
			double mScroll = 0;
			mScroll = g * time * t - g * t * t / 2;
			scrollNum = (int) (mScroll + aniStartPos);
			percent = Math.abs(mScroll + aniStartPos - 1.0 * scrollNum);
			ScrollView.clear();
			moveView();
			return true;
		}
	}

	private int select = 2;

	public void handleAniEnd() {
		int size = BmpRecViews.size();
		currentIndex -= aniStopPos;
		clickItem = -1;
		for (int i = 0; i < totalViewNum; i++) {
			int newIndex = i + currentIndex;
			while (newIndex < 0) {
				newIndex += size;
			}
			newIndex = newIndex % size;
			ImageView myView = (ImageView) ViewHolder.get(i);
			myView.setVisibility(View.VISIBLE);
			BmpRecViews.get(newIndex).invalidate();
			myView.setImageBitmap(BmpRecViews.get(newIndex).getDrawingCache());
		}
		invalidate();
		mState = State.READY;
		if (selectListener != null) {
			int s = currentIndex + selectImg;
			s %= size;
			if (s < 0) {
				s += size;
			}
			if (select != s)
				selectListener.onSelected(s);
			select = s;

		}
	}

	public class SnakeView extends View {
		private List<MyView> mListView;
		public int test = 0;

		public SnakeView(Context context) {
			super(context);
			setWillNotDraw(false);
			mListView = new ArrayList<MyView>();
		}

		private class MyView {
			AppCardView bitmap;
			int left;
			int top;
			int width;
			int height;

			public MyView(AppCardView b, int l, int t, int w, int h) {
				bitmap = b;
				left = l;
				top = t;
				width = w;
				height = h;
			}
		}

		public void addView(AppCardView b, int left, int top, int width,
				int height) {
			if (b != null) {
				MyView v = new MyView(b, left, top, width, height);
				mListView.add(v);
			}
		}

		public void clear() {
			mListView.clear();
		}

		protected void onDraw(Canvas canvas) {
			Paint defaultPaint = new Paint();
			defaultPaint.setAlpha(0);
			defaultPaint.setAntiAlias(true);

			int num = mListView.size();
			if (num == 0)
				canvas.drawRect(0, 0, mContentWidth, mContentHeight,
						defaultPaint);
			Log.d(LOG_TAG, "Num need to show : " + num);
			if (selectImg >= 0 && selectImg < num) {

				// 下滑
				if (deatY >= 0) {
					for (int i = num - 1; i > selectImg; i--) {
						canvas.save();
						MyView v = mListView.get(i);
						Matrix m = new Matrix();
						int bw = v.bitmap.getWidth(), bh = v.bitmap.getHeight();
						m.postScale((float) v.width / bw, (float) v.height / bh);
						// v.bitmap.setSelected(false);
						v.bitmap.invalidate();
						Bitmap dstbmp = Bitmap.createBitmap(
								v.bitmap.getDrawingCache(), 0, 0, bw, bh, m,
								true);
						canvas.drawBitmap(dstbmp, v.left, v.top, null);
						if (!dstbmp.isRecycled())
							dstbmp.recycle();
					}

					for (int i = 0; i <= selectImg; i++) {
						canvas.save();
						MyView v = mListView.get(i);
						Matrix m = new Matrix();
						int bw = v.bitmap.getWidth(), bh = v.bitmap.getHeight();
						m.postScale((float) v.width / bw, (float) v.height / bh);
						// if (i == selectImg)
						// v.bitmap.setSelected(true);
						// else
						// v.bitmap.setSelected(false);
						v.bitmap.invalidate();
						Bitmap dstbmp = Bitmap.createBitmap(
								v.bitmap.getDrawingCache(), 0, 0, bw, bh, m,
								true);
						canvas.drawBitmap(dstbmp, v.left, v.top, null);
						if (!dstbmp.isRecycled())
							dstbmp.recycle();
					}
				} else if (deatY < 0) {
					for (int i = 0; i <= selectImg; i++) {
						canvas.save();
						MyView v = mListView.get(i);
						Matrix m = new Matrix();
						int bw = v.bitmap.getWidth(), bh = v.bitmap.getHeight();
						m.postScale((float) v.width / bw, (float) v.height / bh);
						// if (i == selectImg)
						// v.bitmap.setSelected(true);
						// else
						// v.bitmap.setSelected(false);
						v.bitmap.invalidate();
						Bitmap dstbmp = Bitmap.createBitmap(
								v.bitmap.getDrawingCache(), 0, 0, bw, bh, m,
								true);
						canvas.drawBitmap(dstbmp, v.left, v.top, null);
						if (!dstbmp.isRecycled())
							dstbmp.recycle();
					}
					for (int i = num - 1; i > selectImg; i--) {
						canvas.save();
						MyView v = mListView.get(i);
						Matrix m = new Matrix();
						int bw = v.bitmap.getWidth(), bh = v.bitmap.getHeight();
						m.postScale((float) v.width / bw, (float) v.height / bh);
						// v.bitmap.setSelected(false);
						v.bitmap.invalidate();
						Bitmap dstbmp = Bitmap.createBitmap(
								v.bitmap.getDrawingCache(), 0, 0, bw, bh, m,
								true);
						canvas.drawBitmap(dstbmp, v.left, v.top, null);
						if (!dstbmp.isRecycled())
							dstbmp.recycle();
					}

				} else {
				}
			}
			if (mState == State.ABOUT_TO_ANIMATE || mState == State.ANIMATING) {
				getAnimateData();
				if (mState == State.ANIMATE_END) {
					mListView.clear();
					handleAniEnd();
				}
				this.invalidate();
			}
		}
	}
}