package com.lvdi.ruitianxia_cus.activity;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.ShakeListener.OnShakeListener;
import com.lvdi.ruitianxia_cus.constants.OrderType;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.RandomProduct;
import com.lvdi.ruitianxia_cus.request.RandomProductRequest;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.lvdi.ruitianxia_cus.view.MainMenuPop;
import com.umeng.analytics.MobclickAgent;

/**
 * 摇一摇界面 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月13日 上午12:10:13
 */
public class RandomProductActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(id = R.id.shakeImgDown)
	RelativeLayout mImgDn;
	@AbIocView(id = R.id.shakeImgUp)
	RelativeLayout mImgUp;
	@AbIocView(id = R.id.succRl)
	RelativeLayout mSuccRl;
	Vibrator mVibrator;
	@AbIocView(id = R.id.iconIv)
	ImageView mIconIv;// 优惠券图片
	@AbIocView(id = R.id.nameTv)
	TextView mNameTv;// 优惠券名称
	@AbIocView(id = R.id.priceTv)
	TextView mPriceTv;// 优惠券价格
	@AbIocView(id = R.id.detailTv)
	TextView mDetailTv;// 优惠券描述

	ShakeListener mShakeListener = null;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_RANDOM_PRODUCT_SUCC:
				RandomProduct randomProduct = (RandomProduct) msg.obj;
				getDataSuccess(randomProduct);
				break;
			case HandleAction.HttpType.HTTP_RANDOM_PRODUCT_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				getDataFail();
				break;
			default:
				break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_randomproduct);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("摇一摇");
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		initView();
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
	}

	public void btnClick(View v) {
		switch (v.getId()) {

		default:
			break;
		}
	}

	private void initView() {
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
								RandomProductActivity.this, personBt,
								Config.selectCustomerC,
								RandomProductActivity.class.getSimpleName());
						mainMenPop.show();
					}
				});

		mVibrator = (Vibrator) getApplication().getSystemService(
				VIBRATOR_SERVICE);
		mShakeListener = new ShakeListener(RandomProductActivity.this);
		mShakeListener.setOnShakeListener(new OnShakeListener() {
			public void onShake() {
				if (AbWifiUtil.isConnectivity(RandomProductActivity.this)) {
					mSuccRl.setVisibility(View.GONE);
					startAnim(); // 开始 摇一摇手掌动画
					mShakeListener.stop();
					startVibrato(); // 开始 震动
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							requestData();
						}
					}, 100);
				} else {
					AbToastUtil.showToast(RandomProductActivity.this,
							R.string.please_check_network);
				}

			}
		});
	}

	private void requestData() {
		RandomProductRequest.getInstance().sendRequest(mHandler, "");
	}

	private void getDataSuccess(final RandomProduct randomProduct) {
		mSuccRl.setVisibility(View.VISIBLE);
		mVibrator.cancel();
		mShakeListener.start();
		ImageLoaderHelper.displayImage(randomProduct.largeImageUrl, mIconIv,
				R.drawable.pic_default_bg);

		mNameTv.setText(randomProduct.productName);
		mPriceTv.setText("￥" + randomProduct.promoPrice);
		mDetailTv.setText(randomProduct.description);
		findViewById(R.id.yhqLl).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Config.CATALOGID = "Gshop";
				Config.ORDER_TYPE = OrderType.SALES_ORDER_B2C.toString();
				startActivity(UnifyWebViewActivity.toWebPage(
						RandomProductActivity.this, randomProduct.detailUrl,
						RandomProductActivity.class.getSimpleName(), "摇一摇"));

			}
		});
	}

	private void getDataFail() {
		mVibrator.cancel();
		mShakeListener.start();
	}

	public void startAnim() { // 定义摇一摇动画动画
		AnimationSet animup = new AnimationSet(true);
		TranslateAnimation mytranslateanimup0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimup0.setDuration(1000);
		TranslateAnimation mytranslateanimup1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimup1.setDuration(1000);
		mytranslateanimup1.setStartOffset(1000);
		animup.addAnimation(mytranslateanimup0);
		animup.addAnimation(mytranslateanimup1);
		mImgUp.startAnimation(animup);

		AnimationSet animdn = new AnimationSet(true);
		TranslateAnimation mytranslateanimdn0 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				+0.5f);
		mytranslateanimdn0.setDuration(1000);
		TranslateAnimation mytranslateanimdn1 = new TranslateAnimation(
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF, 0f,
				Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
				-0.5f);
		mytranslateanimdn1.setDuration(1000);
		mytranslateanimdn1.setStartOffset(1000);
		animdn.addAnimation(mytranslateanimdn0);
		animdn.addAnimation(mytranslateanimdn1);
		mImgDn.startAnimation(animdn);
	}

	public void startVibrato() {
		MediaPlayer player;
		player = MediaPlayer.create(this, R.raw.awe);
		player.setLooping(false);
		player.start();

		// 定义震动
		mVibrator.vibrate(new long[] { 500, 200, 500, 200 }, -1); // 第一个｛｝里面是节奏数组，
																	// 第二个参数是重复次数，-1为不重复，非-1俄日从pattern的指定下标开始重复
	}

	public void shake_activity_back(View v) { // 标题栏 返回按钮
		this.finish();
	}

	public void linshi(View v) { // 标题栏
		startAnim();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (mShakeListener != null) {
			mShakeListener.stop();
		}
	}
}
