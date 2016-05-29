package com.lvdi.ruitianxia_cus.view;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Scroller;

import com.ab.util.AbLogUtil;
import com.ab.util.AbViewUtil;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;
import com.lvdi.ruitianxia_cus.view.AppCardView.ImageLoadListener;
import com.nostra13.universalimageloader.core.assist.FailReason;

public class AppScrollView extends RelativeLayout implements
		OnLongClickListener, OnClickListener, ImageLoadListener {
	public interface ScrollMoveCallback {
		public void moveY(int dy, int dx);

		public void cardClick(Application layout);

		public void cardLongClick(Application layout);

		public void addIconClick(Application layout);

		public void removeIconClick(Application layout);

		public void select(int posion);
	}

	/**
	 * 首页
	 */
	public static final int HOME_MODE = 1;
	/**
	 * 服务定制
	 */
	public static final int CUSTOM_MODE = 2;

	private int viewMode = HOME_MODE;

	private ScrollMoveCallback mListener;
	/**
	 * 卡片列表
	 */
	private ImageView[] mCardViews;

	private List<AppCardView> mCardViewList;
	/**
	 * 应用列表
	 */
	private List<Application> mLayouts;

	private int mLayoutCount;
	/**
	 * 中间位置
	 */
	private int mSelectItem = -1;
	private int[] alphas;
	private float[] rotations;
	private float[] scales;
	private int mAddIconResids[] = new int[] { R.drawable.tj_android_1,
			R.drawable.tj_android_2, R.drawable.tj_android_3,
			R.drawable.tj_android_4, R.drawable.tj_android_5 };
	private ImageView mAddIconIv;
	private ImageView mDeleteIconIv;
	private float mScrollDance;
	private Scroller mScroller;

	public AppScrollView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public AppScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public AppScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public void setViewMode(int mode) {
		viewMode = mode;
		if (viewMode == CUSTOM_MODE) {
			mCardViews[2].setOnLongClickListener(this);
			mAddIconIv.setOnClickListener(this);
			mDeleteIconIv.setOnClickListener(this);
		} else {
			mCardViews[2].setOnClickListener(this);
		}
	}

	public void setListener(ScrollMoveCallback listener) {
		mListener = listener;
	}

	public ArrayList<Application> getLayous() {
		return (ArrayList<Application>) mLayouts;
	}

	public int getSelectIndex() {
		return mSelectItem;
	}

	private void resetConfig() {
		alphas = new int[] { (int) (0.4 * 255), (int) (0.8 * 255), 255,
				(int) (0.8 * 255), (int) (0.4 * 255) };
		rotations = new float[] { -15f, -20f, 0f, 20f, 15f };
		scales = new float[] { 0.8f, 0.85f, 1f, 0.85f, 0.8f };

	}

	private void initView(final Context context) {
		resetConfig();
		LayoutInflater.from(context).inflate(R.layout.app_card_group, this,
				true);
		ImageView mCardView1 = (ImageView) findViewById(R.id.card1);
		ImageView mCardView2 = (ImageView) findViewById(R.id.card2);
		ImageView mCardView3 = (ImageView) findViewById(R.id.card3);
		mCardView3.setSelected(true);
		ImageView mCardView4 = (ImageView) findViewById(R.id.card4);
		ImageView mCardView5 = (ImageView) findViewById(R.id.card5);
		mAddIconIv = (ImageView) findViewById(R.id.addIcon);
		mDeleteIconIv = (ImageView) findViewById(R.id.deleteIcon);
		mAddIconIv.setVisibility(View.GONE);
		mDeleteIconIv.setVisibility(View.GONE);
		mCardViews = new ImageView[] { mCardView1, mCardView2, mCardView3,
				mCardView4, mCardView5 };

		mCardView2.bringToFront();
		mCardView4.bringToFront();
		mCardView3.bringToFront();
		mAddIconIv.bringToFront();
		mDeleteIconIv.bringToFront();
		mScroller = new Scroller(mCardView3.getContext());
		mLayouts = new ArrayList<Application>();
		mCardViewList = new ArrayList<AppCardView>();
	}

	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		super.onWindowFocusChanged(hasFocus);
		// 这里来获取容器的宽和高
		if (hasFocus) {
			mScrollDance = AbViewUtil.dip2px(getContext(), 90);
		}
	}

	public void setAppList(ArrayList<Application> layouts) {
		resetConfig();
		mLayouts.clear();
		if (null != layouts)
			mLayouts.addAll(layouts);
		mLayoutCount = mLayouts.size();
		mSelectItem = 2;
		mAddIconIv.setBackgroundResource(mAddIconResids[mSelectItem % 5]);
		addCardList();
		setSelectItem(2);
		resetConfig();
		AnimationOver();
		for (int i = 0; i < mLayoutCount; i++) {
			mCardViewList.get(i).loadImage(this);
		}
	}

	private void addCardList() {
		mCardViewList.clear();
		for (int i = 0; i < mLayoutCount; i++) {
			AppCardView appCardView = new AppCardView(getContext());
			appCardView.setTextData(mLayouts.get(i), i);
			appCardView.setLayoutParams(new RelativeLayout.LayoutParams(
					RelativeLayout.LayoutParams.WRAP_CONTENT,
					RelativeLayout.LayoutParams.WRAP_CONTENT));

			appCardView.setDrawingCacheEnabled(true);
			appCardView.measure(
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED),
					MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
			appCardView.layout(0, 0, appCardView.getMeasuredWidth(),
					appCardView.getMeasuredHeight());
			appCardView.buildDrawingCache();
			mCardViewList.add(appCardView);
		}
	}

	private void setSelectItem(int item) {
		if (item == 2) {
			item = mLayoutCount + 2;
		}
		mDeleteIconIv.setVisibility(View.GONE);
		if (-1 != item) {
			mSelectItem = item;
			updateCardData(item);
			mListener.select(item);
		}
	}

	public void updataCard(int item) {
		mCardViewList.get(mSelectItem % mLayoutCount).onRefresh();
	}

	public void updateCardData(int item) {
		mSelectItem = item;
		for (int i = 0; i < 5; i++) {
			int index = (item - 2 + i) % mLayoutCount;
			Application application = mLayouts.get(index);
			AppCardView appCardView = mCardViewList.get(index);
			if (i == 2) {
				if (viewMode == CUSTOM_MODE) {
					if (application.id == 0) {
						mAddIconIv.setVisibility(View.VISIBLE);
						mAddIconIv
								.setBackgroundResource(mAddIconResids[index % 5]);
					} else {
						mAddIconIv.setVisibility(View.GONE);
					}
				}
				appCardView.setSelected(true);
			} else {
				appCardView.setSelected(false);
			}
			appCardView.invalidate();
			mCardViews[i].setImageBitmap(appCardView.getDrawingCache());
		}
	}

	@SuppressLint("NewApi")
	private void AnimationOver() {
		for (int i = 0; i < 5; i++) {
			mCardViews[i].setAlpha(alphas[i]);
			mCardViews[i].setRotationX(rotations[i]);
			mCardViews[i].setScaleX(scales[i]);
			mCardViews[i].setScaleY(scales[i]);
		}
	}

	private float mLastMotionY;

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		float y = ev.getY();
		switch (ev.getAction()) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionY = y;
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			break;
		case MotionEvent.ACTION_MOVE:
			move(y);
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			smoothScrollTo(0, 0, 2000);
			resetConfig();
			AnimationOver();
			break;
		default:
			break;
		}
		return super.dispatchTouchEvent(ev);
	}// 这个方法用来分发TouchEvent

	@SuppressLint("NewApi")
	private void move(float y) {
		float dy = y - mLastMotionY;
		if (dy > mScrollDance) {
			mLastMotionY = y;
			setSelectItem(mSelectItem - 1);
			// smoothScrollTo(0, 0, 0);
			return;
		} else if (dy < -mScrollDance) {
			mLastMotionY = y;
			setSelectItem(mSelectItem + 1);
			// smoothScrollTo(0, 0, 0);
			return;
		}
		if (!mScroller.isFinished()) {
			return;
		}
		float scale = Math.abs(dy) / mScrollDance;
		// 下拉
		if (dy > 0) {
			alphas[0] = (int) ((0.4f + 0.4f * scale) * 255);
			alphas[1] = (int) ((0.8f + 0.2f * scale) * 255);
			alphas[2] = (int) ((1f - 0.2f * scale) * 255);
			alphas[3] = (int) ((0.8f - 0.4f * scale) * 255);
			alphas[4] = (int) ((0.4f - 0.2f * scale) * 255);

			scales[0] = 0.8f + 0.05f * scale;
			scales[1] = 0.85f + 0.15f * scale;
			scales[2] = 1f - 0.15f * scale;
			scales[3] = 0.85f - 0.05f * scale;
			scales[4] = 0.8f - 0.05f * scale;

			rotations[0] = -15f - 5f * scale;
			rotations[1] = -20f + 20f * scale;
			rotations[2] = 0f + 20 * scale;
			rotations[3] = 20f + 5f * scale;
			rotations[4] = 15f + 15f * scale;
		} else if (dy < 0) {
			// 上拉
			alphas[0] = (int) ((0.4f - 0.2f * scale) * 255);
			alphas[1] = (int) ((0.8f - 0.4f * scale) * 255);
			alphas[2] = (int) ((1f - 0.2f * scale) * 255);
			alphas[3] = (int) ((0.8f + 0.2f * scale) * 255);
			alphas[4] = (int) ((0.4f + 0.4f * scale) * 255);

			scales[0] = 0.8f - 0.05f * scale;
			scales[1] = 0.85f - 0.05f * scale;
			scales[2] = 1f - 0.15f * scale;
			scales[3] = 0.85f + 0.15f * scale;
			scales[4] = 0.8f + 0.05f * scale;

			rotations[0] = -15f - 15f * scale;
			rotations[1] = -20f - 5f * scale;
			rotations[2] = 0f - 20 * scale;
			rotations[3] = 20f - 20f * scale;
			rotations[4] = 15f + 5f * scale;
		}
		smoothScrollTo(0, -(int) dy / 2, 0);
		AnimationOver();
	}

	// 调用此方法滚动到目标位置
	private void smoothScrollTo(int fx, int fy, int duration) {
		int dx = fx - mScroller.getFinalX();
		int dy = fy - mScroller.getFinalY();
		smoothScrollBy(dx, dy, duration);
	}

	// 调用此方法设置滚动的相对偏移
	private void smoothScrollBy(int dx, int dy, int duration) {
		// 设置mScroller的滚动偏移量
		mScroller.startScroll(mScroller.getFinalX(), mScroller.getFinalY(), dx,
				dy, duration);
		invalidate();// 这里必须调用invalidate()才能保证computeScroll()会被调用，否则不一定会刷新界面，看不到滚动效果
	}

	@Override
	public void computeScroll() {
		// 先判断mScroller滚动是否完成
		if (mScroller.computeScrollOffset()) {
			// 这里调用View的scrollTo()完成实际的滚动
			scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
			// 必须调用该方法，否则不一定能看到滚动效果
			postInvalidate();
		}
		super.computeScroll();
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		return super.onInterceptTouchEvent(ev);
	}// 这个方法用来拦截TouchEvent

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		return true;

	}// 这个方法用来处理TouchEvent

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		switch (arg0.getId()) {
		case R.id.addIcon:
			mListener.addIconClick(mLayouts.get(mSelectItem % mLayoutCount));
			break;
		case R.id.deleteIcon:
			mLayouts.get(mSelectItem % mLayoutCount).removeApplication();
			updataCard(mSelectItem % mLayoutCount);
			mDeleteIconIv.setVisibility(View.GONE);
			updateCardData(mSelectItem);
			mListener.removeIconClick(mLayouts.get(mSelectItem % mLayoutCount));
			break;
		case R.id.card3:
			mListener.cardClick(mLayouts.get(mSelectItem % mLayoutCount));
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
			Application application = mLayouts.get(mSelectItem % mLayoutCount);
			if (application.id == 0) {
				break;
			}
			mDeleteIconIv.setVisibility(View.VISIBLE);
			mDeleteIconIv.bringToFront();
			AbLogUtil.e(getClass(), "删除");
			break;
		default:
			break;
		}

		return true;
	}

	@Override
	public void onLoadingStarted(String arg0, View arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadingFailed(String arg0, View arg1, FailReason arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLoadingComplete(String arg0, View arg1, Bitmap arg2, int index) {
		// TODO Auto-generated method stub
		if (mSelectItem > 1)
			updateCardData(mSelectItem);
	}

	@Override
	public void onLoadingCancelled(String arg0, View arg1) {
		// TODO Auto-generated method stub

	}

}
