package com.lvdi.ruitianxia_cus.view.pulltorefresh;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lvdi.ruitianxia_cus.R;

/**
 * 这个类封装了下拉刷新的布局
 * 
 */
public class FooterLoadingLayout extends LoadingLayout {
	/** 进度条 */
	private ProgressBar mProgressBar;
	/** 显示的文本 */
	private TextView mHintView;
	private View container;

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 */
	public FooterLoadingLayout(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 构造方法
	 * 
	 * @param context
	 *            context
	 * @param attrs
	 *            attrs
	 */
	public FooterLoadingLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化
	 * 
	 * @param context
	 *            context
	 */
	private void init(Context context) {
		mProgressBar = (ProgressBar) findViewById(R.id.pull_to_load_footer_progressbar);
		mHintView = (TextView) findViewById(R.id.pull_to_load_footer_hint_textview);

		setState(State.RESET);
	}

	@Override
	protected View createLoadingView(Context context, AttributeSet attrs) {
		View v = LayoutInflater.from(context).inflate(
				R.layout.pull_to_load_footer, null);
		container = v.findViewById(R.id.pull_to_load_footer_content);
		return v;
	}

	@Override
	public void setLastUpdatedLabel(CharSequence label) {
	}

	@Override
	public int getContentSize() {
		View v = findViewById(R.id.pull_to_load_footer_content);
		if (null != v) {
			return v.getHeight();
		}

		return (int) (getResources().getDisplayMetrics().density * 40);
	}

	@Override
	protected void onStateChanged(State curState, State oldState) {
		mProgressBar.setVisibility(View.GONE);
		mHintView.setVisibility(View.INVISIBLE);

		super.onStateChanged(curState, oldState);
	}

	@Override
	protected void onReset() {
		mHintView.setText(R.string.pull_to_refresh_header_hint_loading);
	}

	@Override
	protected void onPullToRefresh() {
		container.setVisibility(View.VISIBLE);
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_to_refresh_header_hint_normal2);
	}

	@Override
	protected void onReleaseToRefresh() {
		container.setVisibility(View.VISIBLE);
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_to_refresh_header_hint_ready);
	}

	@Override
	protected void onRefreshing() {
		mProgressBar.setVisibility(View.VISIBLE);
		container.setVisibility(View.VISIBLE);
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pull_to_refresh_header_hint_loading);
	}

	@Override
	protected void onNoMoreData() {
		container.setVisibility(View.GONE);
		mHintView.setVisibility(View.VISIBLE);
		mHintView.setText(R.string.pushmsg_center_no_more_msg);
	}
}
