package com.lvdi.ruitianxia_cus.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.GeolocationPermissions;
import android.webkit.JavascriptInterface;
import android.webkit.SslErrorHandler;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbJsonUtil;
import com.ab.util.AbLogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.titlebar.AbTitleBar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.shopcart.CartManager;
import com.lvdi.ruitianxia_cus.activity.shopcart.ConfirmO2OServiceActivity;
import com.lvdi.ruitianxia_cus.activity.shopcart.ConfirmOrderActivity;
import com.lvdi.ruitianxia_cus.activity.shopcart.ShopCartActivity;
import com.lvdi.ruitianxia_cus.constants.OrderType;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.shopcart.CategoryInfo;
import com.lvdi.ruitianxia_cus.model.shopcart.DbProduct;
import com.lvdi.ruitianxia_cus.model.shopcart.ProductInfo;
import com.lvdi.ruitianxia_cus.model.shopcart.RspCategory;
import com.lvdi.ruitianxia_cus.request.GetShopCartsRequest;
import com.lvdi.ruitianxia_cus.util.HttpAsyncDownload.OnProgressListener;
import com.lvdi.ruitianxia_cus.view.HTML5WebView;
import com.lvdi.ruitianxia_cus.view.MainMenuPop;
import com.umeng.analytics.MobclickAgent;

/**
 * 加载H5界面 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月8日 下午9:41:25
 */
@SuppressLint("NewApi")
public class UnifyWebViewActivity extends LvDiActivity implements
		OnProgressListener {

	/**
	 * 商品列表地址
	 */
	public static final String URL_PRODUCT_LIST = "rui/control/gproduct4phone";
	/**
	 * 商品列表地址
	 */
	public static final String URL_PRODUCT_DETAIL = "/rui/control/productInfo4phone";
	/**
	 * 加载失败布局
	 */
	private FrameLayout mFrameLayout;
	/**
	 * 继承H5的WebView页面
	 */
	private HTML5WebView mWebView;
	/**
	 * 设置页面Title配置类
	 */
	private MyWebChromeClient mWebChromeClient;
	/**
	 * 加载WebView的URL
	 */
	private String mLoadUrl;
	/**
	 * 当前页面Title
	 */
	private String mOutTitle;
	/**
	 * html5调手机系统上传文件
	 */
	private ValueCallback<Uri> mUploadMessage;
	/**
	 * 加载超时
	 */
	private static final int WEBVIEW_LOAD_OVERTIME = 0x111;
	/**
	 * 超时
	 */
	private final long LOADING_TIMEOUT = 20000;
	/**
	 * 系统上传文件回调
	 */
	private static final int FILECHOOSER_RESULTCODE = 0x11;
	private String from;
	private String appName;
	private AbTitleBar mAbTitleBar = null;
	/*** 立即下单商品信息 */
	private ArrayList<DbProduct> mDbProductlist;
	/**
	 * 标识按返回键直接退出H5的应用名
	 */
	// private List<String> mFinishWebApps;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			AbDialogUtil.removeDialog(UnifyWebViewActivity.this);
			switch (msg.what) {
			case WEBVIEW_LOAD_OVERTIME:
				mWebView.showErrorPageView(true);
				mWebView.hideLoadProgress();
				break;
			case HandleAction.HttpType.HTTP_CHECK_GET_SHOP_CARTS_SUCC:// 4.22
				// 购物车商品信息查询接口
				if (msg.obj != null && mDbProductlist != null) {
					RspCategory rc = (RspCategory) msg.obj;
					boolean isEnable = true;
					if (rc.categoryVOList != null) {
						for (CategoryInfo ci : rc.categoryVOList) {
							if (ci.prodList != null) {
								for (ProductInfo pi : ci.prodList) {
									if (0 == pi.quantity) {
										isEnable = false;
									} else {
										for (DbProduct pp : mDbProductlist) {
											if (pp.productId
													.equals(pi.productId)) {
												pp.quantity = pi.quantity;
											}
										}
									}
								}
							}
						}
					}
					if (isEnable) {
						startActivity(new Intent(UnifyWebViewActivity.this,
								ConfirmOrderActivity.class).putExtra(
								"DbProductList", mDbProductlist));
					} else {
						AbToastUtil.showToast(UnifyWebViewActivity.this,
								"亲，此商品库存量为：0件，库存不足！");
					}
				}
				break;
			case HandleAction.HttpType.HTTP_CHECK_GET_SHOP_CARTS_FAIL:
				mDbProductlist = null;
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mWebView = new HTML5WebView(this);
		mFrameLayout = mWebView.getLayout();
		setAbContentView(mFrameLayout);
		initDatas();
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initViews();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		if (appName.equals("快递查询")) {
			mOutTitle = appName;
			mAbTitleBar.setTitleText(appName);
		}
	}

	/**
	 * 初始化Intent数据
	 */
	private void initDatas() {
		mLoadUrl = getIntentString("loadUrl");
		AbLogUtil.i(this, "加载网页： " + mLoadUrl);
		from = getIntentString("from");
		appName = getIntentString("appName");
		// mFinishWebApps = new ArrayList<String>();
		// mFinishWebApps.add("周边餐饮");
		// mFinishWebApps.add("G咖啡");
		// mFinishWebApps.add("订桶装水");
		// mFinishWebApps.add("预约租车");
		// mFinishWebApps.add("健身预约");
		// mFinishWebApps.add("洗衣预约");
		// mFinishWebApps.add("全球精选");
		// mFinishWebApps.add("绿植服务");
	}

	/**
	 * 初始化View
	 */
	private void initViews() {
		mAbTitleBar.clearRightView();
		View rightViewMore = mInflater.inflate(R.layout.right_menu_btn, null);
		mAbTitleBar.addRightView(rightViewMore);
		final TextView personBt = (TextView) rightViewMore
				.findViewById(R.id.menuBtn);
		personBt.setBackgroundResource(R.drawable.main_more);
		mAbTitleBar.getRightLayout().setOnClickListener(
				new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						MainMenuPop mainMenPop = new MainMenuPop(
								UnifyWebViewActivity.this, personBt,
								Config.selectCustomerC,
								UnifyWebViewActivity.class.getSimpleName());
						mainMenPop.show();
					}
				});

		mWebChromeClient = new MyWebChromeClient();
		mWebView.setWebChromeClient(mWebChromeClient);
		mAbTitleBar.getLogoView().setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				onBack();
			}
		});
		if (from.equals("ActiEPP")) {
			mWebView.addJavascriptInterface(new wdClient(), "wdClient");
		}
		mWebView.addJavascriptInterface(new wdClient(), "Greenland");
		setWebView();
	}

	class wdClient {
		@JavascriptInterface
		public void closeView() {
			AbLogUtil.d("webViewActivity", "wdClient:closeView()");
			Intent intent = new Intent();
			setResult(RESULT_OK, intent);
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		}

		/**
		 * js调用 跳转购物车
		 * 
		 * @param content
		 */
		@JavascriptInterface
		public void toCart(String content) {
			if (Config.ORDER_TYPE.equals(OrderType.SALES_ORDER_O2O_SERVICE
					.toString())) {
				AbToastUtil.showToast(UnifyWebViewActivity.this,
						"当前应用配置的订单类型有误");
				return;
			}
			AbLogUtil.d("wcz->toCart", content);
			try {
				List<DbProduct> mlist = (List<DbProduct>) AbJsonUtil.fromJson(
						content, new TypeToken<List<DbProduct>>() {
						});
				if (mlist != null) {
					for (DbProduct dp : mlist) {
						if (OrderType.SALES_ORDER_B2C.toString().equals(
								Config.ORDER_TYPE)) {// b2c商品全部归为绿地一类
							dp.catalogId = Config.B2C_CATEGORYID;
							dp.categoryId = Config.B2C_CATEGORYID;
							dp.orderTypeId = Config.ORDER_TYPE;
						} else {
							dp.catalogId = Config.CATALOGID;
							dp.orderTypeId = Config.ORDER_TYPE;
						}
					}
					CartManager.getInstance().insert(mlist);
					startActivity(new Intent(UnifyWebViewActivity.this,
							ShopCartActivity.class));

				}
				;
			} catch (Exception e) {
				AbLogUtil.e("wcz-web", e.toString());
			}
		};

		/**
		 * js调用 立即下单
		 * 
		 * @param content
		 */
		@JavascriptInterface
		public void toOrder(String content) {
			AbLogUtil.d("wcz->toOrder", content);
			if (!isLogin()) {
				startLoginActivity();
				return;
			}
			if (Config.ORDER_TYPE.equals(OrderType.SALES_ORDER_O2O_SERVICE
					.toString())) {
				AbToastUtil.showToast(UnifyWebViewActivity.this,
						"当前应用配置的订单类型有误");
				return;
			}
			try {
				mDbProductlist = (ArrayList<DbProduct>) AbJsonUtil.fromJson(
						content, new TypeToken<List<DbProduct>>() {
						});
				if (mDbProductlist != null) {
					for (DbProduct dp : mDbProductlist) {
						dp.catalogId = Config.CATALOGID;
						dp.orderTypeId = Config.ORDER_TYPE;
					}
					AbDialogUtil.showProgressDialog(UnifyWebViewActivity.this,
							0, "查询数据中...");
					GetShopCartsRequest req = new GetShopCartsRequest();
					req.sendRequest(mHandler, new Gson().toJson(CartManager
							.convert(mDbProductlist)));
					// startActivity(new Intent(UnifyWebViewActivity.this,
					// ConfirmOrderActivity.class).putExtra(
					// "DbProductList", mDbProductlist));
				}
				;
			} catch (Exception e) {
				AbLogUtil.e("wcz-web", e.toString());
			}
		};

		/**
		 * js调用 预约下单
		 * 
		 * @param content
		 */
		@JavascriptInterface
		public void toPoint(String content) {
			AbLogUtil.d("wcz->toPoint", content);
			if (!isLogin()) {
				startLoginActivity();
				return;
			}
			if (!Config.ORDER_TYPE.equals(OrderType.SALES_ORDER_O2O_SERVICE
					.toString())) {
				AbToastUtil.showToast(UnifyWebViewActivity.this,
						"当前应用配置的订单类型有误");
				return;
			}
			try {
				ArrayList<DbProduct> mlist = (ArrayList<DbProduct>) AbJsonUtil
						.fromJson(content, new TypeToken<List<DbProduct>>() {
						});
				if (mlist != null) {
					for (DbProduct dp : mlist) {
						dp.catalogId = Config.CATALOGID;
						dp.orderTypeId = Config.ORDER_TYPE;
					}
					startActivity(new Intent(UnifyWebViewActivity.this,
							ConfirmO2OServiceActivity.class).putExtra(
							"DbProductList", mlist));
				}
				;
			} catch (Exception e) {
				AbLogUtil.e("wcz-web", e.toString());
			}
		};

		/**
		 * 缓存购物车数据
		 * 
		 * @param jsonStr
		 */
		@JavascriptInterface
		public void toProInfo(String jsonStr) {
			tempWebData = jsonStr;
		}

		@JavascriptInterface
		public String getCartInfo(String type) {
			if ("cartInfo".equals(type)) {
				return tempWebData;
			}
			return "";
		}

	}

	private String tempWebData = "";

	/**
	 * 从商品详情到商品列表手动调js方法
	 */
	public void cartInfoFromPhone() {
		mWebView.loadUrl("javascript:cartInfoFromPhone()");
	}

	/**
	 * 获取Intent数据
	 */
	private String getIntentString(String key) {
		String value = getIntent().getStringExtra(key) != null ? getIntent()
				.getStringExtra(key) : "";
		return value;
	}

	/**
	 * 
	 * 功能描述: <br>
	 * 设置WebView缩放 ApI9以上版本
	 */
	@SuppressLint("NewApi")
	private void setWebView() {
		// 去除手机系统自带ScrollView滑动效果
		mWebView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		// 设置WebView加载超时处理
		mWebView.postDelayed(run, LOADING_TIMEOUT);
		if (!TextUtils.isEmpty(mLoadUrl)) {
			// 将token插入cookie中，避免二次登录
			mWebView.setWebViewClient(myWebViewClient);
			mWebView.loadUrl(mLoadUrl);
		}

		// 设置Web视图
		this.registerForContextMenu(mWebView);
	}

	/**
	 * 根据WebView的Title,进行Web页面标题处理 处理照片打印机调用系统上传文件功能
	 */
	private class MyWebChromeClient extends WebChromeClient {
		@Override
		public void onReceivedTitle(WebView view, String title) {
			if (appName.equals("快递查询")) {
				mOutTitle = appName;
				mAbTitleBar.setTitleText(appName);
			} else if (appName.equals("寄快递")) {
				mOutTitle = appName;
				mAbTitleBar.setTitleText(appName);
			} else {
				mOutTitle = title;
				mAbTitleBar.setTitleText(title);
			}
		}

		// 支持网页定位功能
		@Override
		public void onGeolocationPermissionsShowPrompt(String origin,
				GeolocationPermissions.Callback callback) {
			callback.invoke(origin, true, false);
			super.onGeolocationPermissionsShowPrompt(origin, callback);
		}

		@Override
		public void onProgressChanged(WebView view, int newProgress) {
			super.onProgressChanged(view, newProgress);
			if (newProgress == 100) {
				mWebView.hideLoadProgress();
			} else {
				mWebView.setLoadProgress(newProgress);
			}
		}
	}

	/**
	 * WebView配置类
	 */
	WebViewClient myWebViewClient = new WebViewClient() {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// view.loadUrl(url);
			// AbLogUtil.d(this.toString(), "shouldOverrideUrlLoading=" + url);
			// return true;
			return false;
		}

		/**
		 * WebView安全认证，默认直接允许 //handler.cancel(); 默认的处理方式，WebView变成空白页
		 * //handler.process();接受证书 //handleMessage(Message msg); 其他处理
		 */
		@Override
		public void onReceivedSslError(WebView view, SslErrorHandler handler,
				SslError error) {
			super.onReceivedSslError(view, handler, error);
			handler.proceed();
		}

		@Override
		public void onPageStarted(WebView view, String url, Bitmap favicon) {
			mWebView.showLoadProgress();
			super.onPageStarted(view, url, favicon);
		}

		@Override
		public void onPageFinished(WebView view, final String url) {
			mWebView.hideLoadProgress();
			// 移除延时计时功能
			mWebView.removeCallbacks(run);
			if (AbWifiUtil.isConnectivity(UnifyWebViewActivity.this)) {
				if (!TextUtils.isEmpty(mOutTitle)
						&& (mOutTitle.equals("找不到网页"))) {
					mWebView.showErrorPageView(true);
				} else {
					mWebView.showErrorPageView(false);
				}
			} else {
				mWebView.showErrorNetView(true);
			}

			AbLogUtil.d(this.toString(), "onPageFinished=" + url);
			if (url.contains(URL_PRODUCT_LIST)
					|| url.contains(URL_PRODUCT_DETAIL)) {
				cartInfoFromPhone();
			}
			super.onPageFinished(view, url);
		}

		@Override
		public void onScaleChanged(WebView view, float oldScale, float newScale) {
			super.onScaleChanged(view, oldScale, newScale);
		}

		/**
		 * 加载WebView异常处理
		 */
		@Override
		public void onReceivedError(WebView view, int errorCode,
				String description, String failingUrl) {
			super.onReceivedError(view, errorCode, description, failingUrl);
			if (!TextUtils.isEmpty(mLoadUrl) && !mLoadUrl.equals(failingUrl)) {
				return;
			}
			if (AbWifiUtil.isConnectivity(UnifyWebViewActivity.this)) {
				mWebView.showErrorPageView(true);
			} else {
				mWebView.showErrorNetView(true);
			}
		}
	};

	// /**
	// * 提供方法供不同页面传值使用
	// *
	// * @param loadUrl
	// * @return
	// */
	// public static Intent toWebPage(Context context, String loadUrl) {
	// Intent intent = new Intent();
	// intent.setClass(context, UnifyWebViewActivity.class);
	// intent.putExtra("loadUrl", loadUrl);
	// return intent;
	// }

	public static Intent toWebPage(Context context, String loadUrl,
			String from, String appName) {
		Intent intent = new Intent();
		intent.setClass(context, UnifyWebViewActivity.class);
		intent.putExtra("loadUrl", loadUrl);
		intent.putExtra("from", from);
		intent.putExtra("appName", appName);
		return intent;
	}

	//
	// public static Intent toWebPage(Context context, String loadUrl, String
	// from) {
	// Intent intent = new Intent();
	// intent.setClass(context, UnifyWebViewActivity.class);
	// intent.putExtra("loadUrl", loadUrl);
	// intent.putExtra("from", from);
	// return intent;
	// }

	/**
	 * 调用系统上传文件回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		if (requestCode == FILECHOOSER_RESULTCODE) {
			if (null == mUploadMessage)
				return;
			Uri result = intent == null || resultCode != RESULT_OK ? null
					: intent.getData();
			mUploadMessage.onReceiveValue(result);
			mUploadMessage = null;
		}
	}

	@Override
	public void onPostExecute(boolean succeed) {
	}

	@Override
	public void onPreExecute() {
	}

	@Override
	public void onProgressUpdate(long progress, long max, int precent) {
	}

	@Override
	public void onException(Exception exception) {
	}

	/**
	 * WebView超时处理
	 */
	Runnable run = new Runnable() {

		@Override
		public void run() {
			if (mWebView.getContentHeight() == 0) {
				Message msg = new Message();
				msg.what = WEBVIEW_LOAD_OVERTIME;
				mHandler.sendMessage(msg);
			}
		}
	};

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			onBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 返回上一级
	 */
	private void onBack() {
		// if (!TextUtils.isEmpty(appName) && mFinishWebApps.contains(appName))
		// {
		// finish();
		// overridePendingTransition(android.R.anim.slide_in_left,
		// android.R.anim.slide_out_right);
		// } else {
		// if (mWebView.canGoBack()) {
		// mWebView.goBack();
		// } else {
		// finish();
		// overridePendingTransition(android.R.anim.slide_in_left,
		// android.R.anim.slide_out_right);
		// }
		// }

		if (mWebView.canGoBack()) {
			// while (mWebView.canGoBack()) {
			// mWebView.goBack();
			// }
			mWebView = new HTML5WebView(this);
			mFrameLayout = mWebView.getLayout();
			setAbContentView(mFrameLayout);
			initDatas();
			initViews();
		} else {
			finish();
			overridePendingTransition(android.R.anim.slide_in_left,
					android.R.anim.slide_out_right);
		}
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		mWebView.onPause();
		// JPushInterface.onPause(this);
		MobclickAgent.onPageEnd(getClass().getSimpleName() + appName); // （仅有Activity的应用中SDK自动调用，不需要单独写）保证
		// onPageEnd 在onPause
		// 之前调用,因为 onPause
		// 中会保存信息。"SplashScreen"为页面名称，可自定义
		MobclickAgent.onPause(this);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		mWebView.onResume();
		// JPushInterface.onResume(this);
		MobclickAgent.onPageStart(getClass().getSimpleName() + appName); // 统计页面(仅有Activity的应用中SDK自动调用，不需要单独写。"SplashScreen"为页面名称，可自定义)
		MobclickAgent.onResume(this);

	}

}
