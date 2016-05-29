package com.lvdi.ruitianxia_cus.view;

import com.lvdi.ruitianxia_cus.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebSettings.RenderPriority;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉 重写WebView实现载入Html5功能
 * 
 * @author
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class HTML5WebView extends WebView {
	/**
	 * Context
	 */
	private Context mContext;
	/**
	 * MyWebChromeClient
	 */
	private MyWebChromeClient mWebChromeClient;
	/**
	 * MyWebViewDownLoadListener
	 */
	private MyWebViewDownLoadListener mMyWebViewDownLoadListener;
	/**
	 * View
	 */
	private View mCustomView;
	/**
	 * FrameLayout
	 */
	private FrameLayout mCustomViewContainer;
	/**
	 * CustomViewCallback
	 */
	private WebChromeClient.CustomViewCallback mCustomViewCallback;
	/**
	 * FrameLayout
	 */
	private FrameLayout mContentView;
	/**
	 * RelativeLayout
	 */
	private RelativeLayout mBrowserRelativeLayout;
	/**
	 * FrameLayout
	 */
	private FrameLayout mLayout;
	/**
	 * TAG
	 */
	static final String LOGTAG = "HTML5WebView";
	/**
	 * 加载进度条
	 */
	private ProgressBar webProgressBar;
	// private ProgressBar webProgressBar2;
	/**
	 * 设置背景
	 */
	static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
			ViewGroup.LayoutParams.MATCH_PARENT,
			ViewGroup.LayoutParams.MATCH_PARENT);

	private RelativeLayout errPageLayout;
	private ImageView errImgView;
	private TextView errTextView;

	/**
	 * 
	 * 功能描述: <br>
	 * 〈功能详细描述〉
	 * 
	 * @param context
	 * @see [相关类/方法](可选)
	 * @since [产品/模块版本](可选)
	 */
	private void init(Context context) {
		CookieSyncManager.createInstance(context);
		CookieManager manager = CookieManager.getInstance();
		manager.setAcceptCookie(true);
		manager.removeSessionCookie();
		CookieSyncManager.getInstance().sync();
		mContext = context;
		Activity a = (Activity) mContext;

		mLayout = new FrameLayout(context);

		mBrowserRelativeLayout = (RelativeLayout) LayoutInflater.from(a)
				.inflate(R.layout.custom_screen, null);
		mContentView = (FrameLayout) mBrowserRelativeLayout
				.findViewById(R.id.main_content);
		mCustomViewContainer = (FrameLayout) mBrowserRelativeLayout
				.findViewById(R.id.fullscreen_custom_content);
		webProgressBar = (ProgressBar) mBrowserRelativeLayout
				.findViewById(R.id.webPro);
		// webProgressBar2 = (ProgressBar) mBrowserRelativeLayout
		// .findViewById(R.id.progressBar1);
		errPageLayout = (RelativeLayout) mBrowserRelativeLayout
				.findViewById(R.id.web_error_layout);
		errImgView = (ImageView) mBrowserRelativeLayout
				.findViewById(R.id.iv_web_error);
		errTextView = (TextView) mBrowserRelativeLayout
				.findViewById(R.id.tv_error);
		errPageLayout.setVisibility(View.GONE);
		// 设置背景透明度
		mLayout.addView(mBrowserRelativeLayout, COVER_SCREEN_PARAMS);

		mWebChromeClient = new MyWebChromeClient();
		setWebChromeClient(mWebChromeClient);
		mMyWebViewDownLoadListener = new MyWebViewDownLoadListener();
		setDownloadListener(mMyWebViewDownLoadListener);

		WebSettings s = getSettings();
		s.setBuiltInZoomControls(true);
		s.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
		s.setUseWideViewPort(true);
		s.setLoadWithOverviewMode(true);
		s.setSavePassword(true);
		s.setSaveFormData(true);
		s.setJavaScriptEnabled(true);
		s.setRenderPriority(RenderPriority.HIGH);
		// 把图片加载放在最后来加载渲染
		s.setBlockNetworkImage(false);
		// WebView不使用缓存
		s.setCacheMode(s.LOAD_NO_CACHE);
		s.setDefaultTextEncodingName("UTF-8");

		// 启用地理定位
		s.setDatabaseEnabled(true);
		s.setGeolocationEnabled(true);
		String dir = mContext.getApplicationContext()
				.getDir("database", Context.MODE_PRIVATE).getPath();
		s.setGeolocationDatabasePath(dir);
		s.setDomStorageEnabled(true);

		mContentView.addView(this);
	}

	public void showErrorPageView(boolean show) {
		errPageLayout.setVisibility(show ? View.VISIBLE : View.GONE);
		errImgView.setVisibility(show ? View.VISIBLE : View.GONE);
		errTextView.setVisibility(show ? View.VISIBLE : View.GONE);
		errImgView.setImageResource(R.drawable.h5_error_bg);
		errTextView.setText("");
	}

	public void showErrorNetView(boolean show) {
		errPageLayout.setVisibility(show ? View.VISIBLE : View.GONE);
		errImgView.setVisibility(show ? View.VISIBLE : View.GONE);
		errTextView.setVisibility(show ? View.VISIBLE : View.GONE);
		errImgView.setImageResource(R.drawable.h5_error_bg);
		errTextView.setText(mContext.getResources().getString(
				R.string.please_check_network));
	}

	/**
	 * 初始化H5页面
	 * 
	 * @param context
	 */
	public HTML5WebView(Context context) {
		super(context);
		init(context);
	}

	/**
	 * 初始化H5页面
	 * 
	 * @param context
	 * @param attrs
	 */
	public HTML5WebView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	/**
	 * 初始化H5页面
	 */
	public HTML5WebView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init(context);
	}

	/**
	 * 获取失败页面布局
	 */
	public FrameLayout getLayout() {
		return mLayout;
	}

	/**
	 * WebChromeClient 主要辅助WebView处理JS的对话框 网站图标 网站的Title 加载进度等
	 */
	private class MyWebChromeClient extends WebChromeClient {
		private Bitmap mDefaultVideoPoster;
		private View mVideoProgressView;

		@Override
		public void onShowCustomView(View view,
				WebChromeClient.CustomViewCallback callback) {
			HTML5WebView.this.setVisibility(View.GONE);

			if (mCustomView != null) {
				callback.onCustomViewHidden();
				return;
			}

			mCustomViewContainer.addView(view);
			mCustomView = view;
			mCustomViewCallback = callback;
			mCustomViewContainer.setVisibility(View.VISIBLE);
		}

		@Override
		public void onHideCustomView() {

			if (mCustomView == null)
				return;

			mCustomView.setVisibility(View.GONE);

			mCustomViewContainer.removeView(mCustomView);
			mCustomView = null;
			mCustomViewContainer.setVisibility(View.GONE);
			mCustomViewCallback.onCustomViewHidden();

			HTML5WebView.this.setVisibility(View.VISIBLE);
		}

		@Override
		public Bitmap getDefaultVideoPoster() {
			if (mDefaultVideoPoster == null) {
				mDefaultVideoPoster = BitmapFactory.decodeResource(
						getResources(), R.drawable.default_video_poster);
			}
			return mDefaultVideoPoster;
		}

		@Override
		public View getVideoLoadingProgressView() {
			if (mVideoProgressView == null) {
				LayoutInflater inflater = LayoutInflater.from(mContext);
				mVideoProgressView = inflater.inflate(
						R.layout.video_loading_progress, null);
			}
			return mVideoProgressView;
		}
	}

	/**
	 * WebView适配链接下载
	 */
	private class MyWebViewDownLoadListener implements DownloadListener {

		@Override
		public void onDownloadStart(String url, String userAgent,
				String contentDisposition, String mimetype, long contentLength) {
			Uri uri = Uri.parse(url);
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			mContext.startActivity(intent);
		}

	}

	/**
	 * 设置网页加载进度
	 */
	public void setLoadProgress(int progress) {
		webProgressBar.setProgress(progress);
	}

	/**
	 * 显示网页加载进度条
	 */
	public void showLoadProgress() {
		webProgressBar.setVisibility(View.VISIBLE);
		webProgressBar.setProgress(0);
		// webProgressBar2.setVisibility(View.VISIBLE);
	}

	/**
	 * 隐藏网页进度条
	 */
	public void hideLoadProgress() {
		webProgressBar.setVisibility(View.GONE);
		// webProgressBar2.setVisibility(View.GONE);
	}
}