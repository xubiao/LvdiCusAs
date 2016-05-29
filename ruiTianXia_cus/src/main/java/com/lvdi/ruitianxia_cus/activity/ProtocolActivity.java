package com.lvdi.ruitianxia_cus.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.webkit.WebView;

import com.ab.view.ioc.AbIocView;
import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;

public class ProtocolActivity extends LvDiActivity {
	private AbTitleBar mAbTitleBar = null;
	@AbIocView(id = R.id.webview)
	WebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_protocol);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setLogo(R.drawable.button_selector_back);
		mAbTitleBar.setTitleBarBackground(R.drawable.top_bg);
		mAbTitleBar.setTitleBarGravity(Gravity.CENTER, Gravity.CENTER);
		initView();
	}

	private void initView() {
		String title = getIntent().getStringExtra("title");
		String url = getIntent().getStringExtra("url");
		mAbTitleBar.setTitleText(title);
		mWebView.loadUrl(url);
	}

}