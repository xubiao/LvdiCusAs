package com.lvdi.ruitianxia_cus.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import com.ab.activity.AbActivity;
import com.ab.view.ioc.AbIocView;
import com.ab.view.sliding.AbSlidingPlayView;
import com.ab.view.sliding.AbSlidingPlayView.AbOnChangeListener;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.MyApplication;

public class GuideActivity extends AbActivity {
	AbSlidingPlayView mSlidingPlayView;
	@AbIocView(id = R.id.point1)
	ImageView point1;
	@AbIocView(id = R.id.point2)
	ImageView point2;
	@AbIocView(id = R.id.point3)
	ImageView point3;
	@AbIocView(id = R.id.point4)
	ImageView point4;
	@AbIocView(id = R.id.backBt)
	ImageView backBg;

	private ImageView[] points;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_guide);
		points = new ImageView[] { point1, point2, point3, point4 };
		mSlidingPlayView = (AbSlidingPlayView) findViewById(R.id.mAbSlidingPlayView);

		final View mPlayView = mInflater.inflate(R.layout.play_view_item, null);
		ImageView mPlayImage = (ImageView) mPlayView
				.findViewById(R.id.mPlayImage);
		mPlayImage.setBackgroundResource(R.drawable.launcher1);

		final View mPlayView1 = mInflater
				.inflate(R.layout.play_view_item, null);
		ImageView mPlayImage1 = (ImageView) mPlayView1
				.findViewById(R.id.mPlayImage);
		mPlayImage1.setBackgroundResource(R.drawable.launcher2);

		final View mPlayView2 = mInflater
				.inflate(R.layout.play_view_item, null);
		ImageView mPlayImage2 = (ImageView) mPlayView2
				.findViewById(R.id.mPlayImage);
		mPlayImage2.setBackgroundResource(R.drawable.launcher3);

		final View mPlayView3 = mInflater
				.inflate(R.layout.play_view_item, null);
		ImageView mPlayImage3 = (ImageView) mPlayView3
				.findViewById(R.id.mPlayImage);
		mPlayImage3.setBackgroundResource(R.drawable.launcher4);

		mSlidingPlayView.setNavHorizontalGravity(Gravity.RIGHT);
		mSlidingPlayView.addView(mPlayView);
		mSlidingPlayView.addView(mPlayView1);
		mSlidingPlayView.addView(mPlayView2);
		mSlidingPlayView.addView(mPlayView3);
		final Button enterBt = (Button) findViewById(R.id.enterBt);
		enterBt.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (getIntent().getStringExtra("from").equals(
						SettingActivity.class.getSimpleName())) {
					finish();
					return;
				}
				MyApplication.getInstance().resetFirstStart();
				Intent intent = new Intent(GuideActivity.this,
						MainActivity.class);
				startActivity(intent);
				finish();
			}
		});
		backBg.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}
		});
		enterBt.setVisibility(View.GONE);
		backBg.setVisibility(View.GONE);
		mSlidingPlayView.setOnPageChangeListener(new AbOnChangeListener() {

			@Override
			public void onChange(int position) {
				// TODO Auto-generated method stub
				if (position == 3) {
					if (getIntent().getStringExtra("from").equals(
							SettingActivity.class.getSimpleName())) {
						backBg.setVisibility(View.VISIBLE);
					} else {
						enterBt.setVisibility(View.VISIBLE);
					}
				} else {
					enterBt.setVisibility(View.GONE);
					backBg.setVisibility(View.GONE);
				}
				updatePoint(position);
			}
		});

	}

	private void updatePoint(int position) {
		for (int i = 0; i < 4; i++) {
			if (position == i) {
				points[i].setBackgroundResource(R.drawable.white_round_icon);
			} else {
				points[i].setBackgroundResource(R.drawable.gray_round_icon);
			}
		}
	}
}
