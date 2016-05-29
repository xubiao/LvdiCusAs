package com.lvdi.ruitianxia_cus.activity;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import com.ab.view.titlebar.AbTitleBar;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.view.MainMenuPop;

/**
 * 快递查询 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月20日 下午12:43:01
 */
public class ExpressQueryActivity extends LvDiActivity {

	private AbTitleBar mAbTitleBar = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_express_query);
		mAbTitleBar = this.getTitleBar();
		mAbTitleBar.setTitleText("快递查询");
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
		mAbTitleBar.getRightLayout().setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				MainMenuPop mainMenPop = new MainMenuPop(
						ExpressQueryActivity.this, personBt,
						Config.selectCustomerC, ExpressQueryActivity.class
								.getSimpleName());
				mainMenPop.show();
			}
		});
	}

}
