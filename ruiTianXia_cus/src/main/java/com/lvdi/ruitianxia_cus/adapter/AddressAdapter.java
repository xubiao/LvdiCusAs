/*
 * Copyright (C), 2002-201
5, 苏宁易购电子商务有限公司
 * FileName: AddressAdapter.java
 * Author:   14070414
 * Date:     2015年10月29日 下午3:39:35
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.lvdi.ruitianxia_cus.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.util.AbWifiUtil;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.AddAddressActivity;
import com.lvdi.ruitianxia_cus.activity.LoginActivity;
import com.lvdi.ruitianxia_cus.activity.MainActivity;
import com.lvdi.ruitianxia_cus.activity.MyAddressActivity;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.model.AddressInfo;
import com.lvdi.ruitianxia_cus.request.DeleteAddressRequest;
import com.lvdi.ruitianxia_cus.request.SetDefaultAddressRequest;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉
 * 
 * @author 14070414
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class AddressAdapter extends BaseAdapter {
	private LayoutInflater mInflater;
	private Context mContext;
	private List<AddressInfo> mAddressInfos = new ArrayList<AddressInfo>();
	private Handler mHandler;

	public AddressAdapter(Context context, Handler handler) {
		mHandler = handler;
		mContext = context;
		mInflater = LayoutInflater.from(mContext);
	}

	public void setData(List<AddressInfo> addressInfos) {
		mAddressInfos.clear();
		mAddressInfos.addAll(addressInfos);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mAddressInfos.size();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItem(int)
	 */
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getItemId(int)
	 */
	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.widget.Adapter#getView(int, android.view.View,
	 * android.view.ViewGroup)
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder = null;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.view_my_address_item,
					parent, false);
			holder.mNameTv = (TextView) convertView.findViewById(R.id.nameTv);
			holder.mPhoneTv = (TextView) convertView.findViewById(R.id.phoneTv);
			holder.mAddressTv = (TextView) convertView
					.findViewById(R.id.addressTv);
			holder.mDefalutCb = (TextView) convertView
					.findViewById(R.id.checkCb);
			holder.mEditTv = (TextView) convertView.findViewById(R.id.editTv);
			holder.mDeleteTv = (TextView) convertView
					.findViewById(R.id.deleteTv);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final AddressInfo addressInfo = mAddressInfos.get(position);
		holder.mNameTv.setText(addressInfo.recipient);
		holder.mPhoneTv.setText(addressInfo.contactNumber);
		holder.mAddressTv.setText(addressInfo.zipCode + addressInfo.address);
		if (addressInfo.isDefault.equals("Y")) {
			holder.mDeleteTv.setVisibility(View.GONE);
			setDefaultCheck(true, holder.mDefalutCb);
			holder.mDefalutCb.setEnabled(false);
		} else {
			setDefaultCheck(false, holder.mDefalutCb);
			holder.mDeleteTv.setVisibility(View.VISIBLE);
			holder.mDefalutCb.setEnabled(true);
		}
		final OnClickListener clickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				switch (v.getId()) {
				case R.id.checkCb:
					if (AbWifiUtil.isConnectivity(mContext)) {
						AbDialogUtil.showProgressDialog(
								(MyAddressActivity) mContext, 0, "设置默认地址中...");
						SetDefaultAddressRequest.getInstance().sendRequest(
								mHandler, Cache.getAccountInfo().partyId,
								addressInfo.contactMechId);
					} else {
						AbToastUtil.showToast(mContext,
								R.string.please_check_network);
					}
					break;
				case R.id.editTv:

					Intent intent = new Intent(mContext,
							AddAddressActivity.class);
					intent.putExtra("addressInfo", addressInfo);
					intent.putExtra("addressMode",
							AddAddressActivity.EDIT_ADDRESS);
					((MyAddressActivity) mContext).startActivityForResult(
							intent, 1);
					break;
				case R.id.deleteTv:

					final View mView = mInflater.inflate(
							R.layout.dialog_confirm_view, null);
					AbDialogUtil.showAlertDialog(mView);
					((TextView) mView.findViewById(R.id.messageTv))
							.setText("确定删除?");

					OnClickListener dialogClickListener = new OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							AbDialogUtil.removeDialog(mView);
							if (v.getId() == R.id.cancelBt) {

							} else if (v.getId() == R.id.okBt) {
								if (AbWifiUtil.isConnectivity(mContext)) {
									AbDialogUtil.showProgressDialog(
											(MyAddressActivity) mContext, 0,
											"删除地址中...");
									DeleteAddressRequest
											.getInstance()
											.sendRequest(
													mHandler,
													Cache.getAccountInfo().partyId,
													addressInfo.contactMechId);
								} else {
									AbToastUtil.showToast(mContext,
											R.string.please_check_network);
								}
							}

						}
					};
					mView.findViewById(R.id.cancelBt).setOnClickListener(
							dialogClickListener);
					mView.findViewById(R.id.okBt).setOnClickListener(
							dialogClickListener);

					break;
				default:
					break;
				}
			}
		};
		holder.mDefalutCb.setOnClickListener(clickListener);
		holder.mDeleteTv.setOnClickListener(clickListener);
		holder.mEditTv.setOnClickListener(clickListener);
		return convertView;
	}

	private void setDefaultCheck(boolean check, TextView defalutCb) {
		Resources res = mContext.getResources();
		Drawable titleIcon = res
				.getDrawable(check ? R.drawable.address_check_pre
						: R.drawable.address_check_nor);
		// 调用setCompoundDrawables时，必须调用Drawable.setBounds()方法,否则图片不显示
		titleIcon.setBounds(0, 0, titleIcon.getMinimumWidth(),
				titleIcon.getMinimumHeight());
		defalutCb.setCompoundDrawables(titleIcon, null, null, null);
	}

	private class ViewHolder {
		TextView mNameTv;
		TextView mPhoneTv;
		TextView mAddressTv;
		TextView mDefalutCb;
		TextView mEditTv;
		TextView mDeleteTv;
	}
}
