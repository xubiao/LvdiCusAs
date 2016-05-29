package com.lvdi.ruitianxia_cus.activity.shopcart;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbToastUtil;
import com.ab.view.ioc.AbIocView;
import com.google.gson.Gson;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.activity.LvDiActivity;
import com.lvdi.ruitianxia_cus.constants.OrderType;
import com.lvdi.ruitianxia_cus.db.FinalDbManager;
import com.lvdi.ruitianxia_cus.global.Cache;
import com.lvdi.ruitianxia_cus.global.Config;
import com.lvdi.ruitianxia_cus.global.HandleAction;
import com.lvdi.ruitianxia_cus.model.AccountInfo;
import com.lvdi.ruitianxia_cus.model.shopcart.CategoryInfo;
import com.lvdi.ruitianxia_cus.model.shopcart.DbCategory;
import com.lvdi.ruitianxia_cus.model.shopcart.DbProduct;
import com.lvdi.ruitianxia_cus.model.shopcart.ProductInfo;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqConfirmOrder;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqProductItem;
import com.lvdi.ruitianxia_cus.model.shopcart.RspCategory;
import com.lvdi.ruitianxia_cus.model.shopcart.RspConfrimOrder;
import com.lvdi.ruitianxia_cus.request.CheckInventoryRequest;
import com.lvdi.ruitianxia_cus.request.GetShopCartsRequest;
import com.lvdi.ruitianxia_cus.request.order.ConfirmOrderRequest;
import com.lvdi.ruitianxia_cus.util.ImageLoaderHelper;
import com.lvdi.ruitianxia_cus.util.MathUtil;
import com.lvdi.ruitianxia_cus.view.dialog.DialogBase;
import com.lvdi.ruitianxia_cus.view.dialog.DialogBase.OnButtonClickListener;

public class ShopCartActivity extends LvDiActivity {
	TextView registerBt;// 注册点击
	/** 总价 */
	@AbIocView(id = R.id.tv_total_price)
	TextView mTvTotalPrice;
	/** 去结算 */
	@AbIocView(click = "btnClick", id = R.id.tv_submit)
	TextView mTvSubmit;
	/** 清除按钮 */
	@AbIocView(click = "btnClick", id = R.id.btn_clear)
	Button mBtnClear;
	/** 列表 */
	@AbIocView(id = R.id.expandlistview)
	ExpandableListView mListView;
	/** */
	private MyAdapter mAdapter;
	/** */
	private LayoutInflater inflater;
	/** 购物车数据 */
	private List<CategoryInfo> mDataList;

	/** 当前选择的店铺*/
	private int mSelectCategoryIndex = -1;
	/** 当前店铺下选中的商品id列表 */
	private ArrayList<String> mSelectProductIds = new ArrayList<String>();
	
	/**
	 * 数据库中保存的商品信息
	 */
	private List<DbCategory> mDbCateInfo;
	private ReqConfirmOrder content;
	/***是否在请求商品数量 */
	private boolean isReqNum = false;
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			AbDialogUtil.removeDialog(ShopCartActivity.this);
			switch (msg.what) {
			case HandleAction.HttpType.HTTP_CHECK_GET_SHOP_CARTS_SUCC:// 4.22
																		// 购物车商品信息查询接口
				if (msg.obj != null) {
					RspCategory rc = (RspCategory) msg.obj;
					if (rc.categoryVOList != null) {
						mDataList.clear();
						mDataList.addAll(rc.categoryVOList);
						mAdapter.notifyDataSetChanged();
						expandList();
					}
				}
				break;
			case HandleAction.HttpType.HTTP_CHECK_GET_SHOP_CARTS_FAIL:
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_CHECK_INVENTORY_SUCC:// 4.21
				isReqNum = false;													// 商品库存检查接口
				if (msg.obj != null && (Boolean) msg.obj) {// 有库存
					Bundle data = msg.getData();
					int groupPos = data.getInt("groupPos");
					int childPos = data.getInt("childPos");
					int quantity = data.getInt("quantity");

					mDataList.get(groupPos).prodList.get(childPos).quantity = quantity;
					mAdapter.notifyDataSetChanged();
					CartManager.getInstance().updateInventory(
							mDataList.get(groupPos).prodList.get(childPos),
							mDataList.get(groupPos).categoryId);
					calulateTotalPrice();
				}else{
					AbToastUtil.showToast(ShopCartActivity.this, "库存已达上限哦!");
				}
				break;
			case HandleAction.HttpType.HTTP_CHECK_INVENTORY_FAIL:
				isReqNum = false;
				AbToastUtil
						.showToast(getApplicationContext(), (String) msg.obj);
				break;
			case HandleAction.HttpType.HTTP_CHECK_POST_CONFIRM_ORDER_SUCC:
				if (msg.obj != null) {
					RspConfrimOrder mRspConfirmOrder = (RspConfrimOrder) msg.obj;
					
					startActivity(new Intent(ShopCartActivity.this, ConfirmOrderActivity.class).putExtra(
					"ReqConfirmOrder", content).putExtra("RspConfrimOrder", mRspConfirmOrder));
				}
				break;
			case HandleAction.HttpType.HTTP_CHECK_POST_CONFIRM_ORDER_FAIL:
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
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setAbContentView(R.layout.activity_shopcart);
		setAbTitle("购物车");
	}

	public void btnClick(View view) {
		switch (view.getId()) {
		case R.id.btn_clear:// 清空
			new DialogBase(this).defSetCancelBtn("取消", null).defSetConfirmBtn("确定", new OnButtonClickListener() {
				
				@Override
				public void onClick(DialogBase dialog) {
					// TODO Auto-generated method stub
					clearCart();
				}
			}).defSetContentTxt("确定清空购物车").show();
			break;
		case R.id.tv_submit:// 去结算
			confirmSubmit();
			break;

		default:
			break;
		}
	}
	/**
	 * 去结算
	 */
	private void confirmSubmit(){
		if(mDbCateInfo== null){
			AbToastUtil.showToast(this,"请先添加商品");
			return;
		}
		if(mSelectCategoryIndex == -1 || mSelectProductIds.size()==0){
			AbToastUtil.showToast(this, "请选择要去结算的商品");
			return;
		}
		if(!isLogin()){
			startLoginActivity();
			return;
		}
		
		content = new ReqConfirmOrder();
		content.categoryId= mDataList.get(mSelectCategoryIndex).categoryId;
		content.orderTypeId = OrderType.SALES_ORDER_B2C.toString();
		content.promoCode = "";
		
		for(DbCategory dc :mDbCateInfo ){
			if(dc.categoryId .equals(content.categoryId) ){
				content.orderTypeId = dc.orderTypeId;
				content.catalogId = dc.catalogId;
				break;
			}
		}
		AccountInfo accountInfo = Cache.getAccountInfo();

		if(accountInfo != null){
			content.userLoginId = Cache.getUser().userName;
		}
		content.productItems = new ArrayList<ReqProductItem>();
		
		ReqProductItem item;
		for(ProductInfo pi: mDataList.get(mSelectCategoryIndex).prodList){
			if(mSelectProductIds.contains(pi.productId)){
				item = new ReqProductItem();
				item.productId = pi.productId;
				item.quantity = pi.quantity+"";
				content.productItems.add(item);
			}
		}
		
		 
		AbDialogUtil.showProgressDialog(this, 0, "请稍等...");
		ConfirmOrderRequest req = new ConfirmOrderRequest();
		req.sendRequest(mHandler, new Gson().toJson(content));
		 

	}

	public void initData() {
		super.initData();
		inflater = LayoutInflater.from(this);

		mDataList = new ArrayList<CategoryInfo>();
		mAdapter = new MyAdapter();
		mListView.setGroupIndicator(null);
		mListView.setVerticalFadingEdgeEnabled(false);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setAdapter(mAdapter);

		sendCartDataReq();
	}

	private void expandList() {
		int groupCount = mDataList.size();
		for (int i = 0; i < groupCount; i++) {
			mListView.expandGroup(i);
		}
		;
	}
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if(CartManager.cartNeedUpdate){//
			mSelectCategoryIndex = -1;
			mDataList.clear();
			calulateTotalPrice();
			sendCartDataReq();
		}
	}
	private void clearCart(){
		mSelectCategoryIndex = -1;
		mDataList.clear();
		mSelectProductIds.clear();
		mAdapter.notifyDataSetChanged();
		FinalDbManager.getFinalDb().deleteByWhere(DbProduct.class, "_id > '0'");
	}

	/**
	 * 刷新购物车所有的商品
	 */
	private void sendCartDataReq() {
		CartManager.cartNeedUpdate = false;
		 mDbCateInfo = CartManager.getInstance()
				.getAllProducts();
		if (mDbCateInfo != null && mDbCateInfo.size() > 0) {
			AbDialogUtil.showProgressDialog(this, 0, "查询数据中...");
			GetShopCartsRequest req = new GetShopCartsRequest();
			req.sendRequest(mHandler, new Gson().toJson(mDbCateInfo));
		} else {
			mAdapter.notifyDataSetChanged();
			AbToastUtil.showToast(this, "购物车信息为空");
		}
	}
	
	/**
	 * 计算当前购物车选中商品的价格
	 */
	private void calulateTotalPrice(){
		double total = 0;
		if(mSelectCategoryIndex != -1){
			CategoryInfo cinfo = mDataList.get(mSelectCategoryIndex);
			if(mSelectProductIds.size() > 0){
				for(ProductInfo pinfo:cinfo.prodList){
					if(mSelectProductIds.contains(pinfo.productId)){
						try {
							total += (pinfo.quantity * Double.parseDouble(pinfo.unitPrice));
						} catch (Exception e) {
							// TODO: handle exception
						}
					}
				}
			}
		}
		mTvTotalPrice.setText("合计："+MathUtil.formatPrice(total+""));
	}

	class MyAdapter extends BaseExpandableListAdapter {

		@Override
		public int getGroupCount() {
			// TODO Auto-generated method stub
			return mDataList != null ? mDataList.size() : 0;
		}

		@Override
		public int getChildrenCount(int groupPosition) {
			// TODO Auto-generated method stub
			return mDataList.get(groupPosition).prodList != null ? mDataList
					.get(groupPosition).prodList.size() : 0;
		}

		@Override
		public Object getGroup(int groupPosition) {
			// TODO Auto-generated method stub
			return mDataList.get(groupPosition);
		}

		@Override
		public Object getChild(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return mDataList.get(groupPosition).prodList;
		}

		@Override
		public long getGroupId(int groupPosition) {
			// TODO Auto-generated method stub
			return groupPosition;
		}

		@Override
		public long getChildId(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return childPosition;
		}

		@Override
		public boolean hasStableIds() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public View getGroupView(final int groupPosition, boolean isExpanded,
				View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			final GroupHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_shopcart_shop,
						null);
				holder = new GroupHolder();
				holder.mCbShop = (CheckBox) convertView
						.findViewById(R.id.cb_shop);
				holder.mTvShopName = (TextView) convertView
						.findViewById(R.id.tv_shop_name);
				holder.mLayoutCb = (LinearLayout) convertView
						.findViewById(R.id.layout_cb);
				convertView.setTag(holder);
			} else {
				holder = (GroupHolder) convertView.getTag();
			}
			if(mDataList.size()<=groupPosition){
				return convertView;
			}
			final CategoryInfo shop = mDataList.get(groupPosition);
			holder.mTvShopName
					.setText(TextUtils.isEmpty(shop.categoryName) ? ""
							: shop.categoryName);

			// 判断这商店里的商品是否全部选中
			if (mSelectCategoryIndex == groupPosition) {//
				boolean isAllSelect = true;
				if(shop.prodList != null){
					for(ProductInfo pi:shop.prodList){
						if(!mSelectProductIds.contains(pi.productId)){
							isAllSelect = false;
						}
					}
					
				}else{
					isAllSelect = false;
				}
				
				holder.mCbShop.setChecked(isAllSelect);
			} else {
				holder.mCbShop.setChecked(false);
			}
			holder.mLayoutCb.setOnClickListener(new OnClickListener() {// 商店是否选中

						@Override
						public void onClick(View v) {
							if (holder.mCbShop.isChecked()) {// 当前商店里商品已全部选中
								mSelectCategoryIndex = -1;
								mSelectProductIds.clear();
								holder.mCbShop.setChecked(false);
							} else {
								if (mSelectCategoryIndex == -1
										|| mSelectCategoryIndex == groupPosition) {// 当前没有商店产品选中或者 当前商店里商品部分选中
									mSelectCategoryIndex = groupPosition;//
									if (shop.prodList != null) {
										mSelectProductIds.clear();
										for (ProductInfo pinfo : shop.prodList) {
											mSelectProductIds
													.add(pinfo.productId);
										}
									}
									holder.mCbShop.setChecked(true);
								} else {// 其他商店里已有商品选中
									AbToastUtil.showToast(
											ShopCartActivity.this,
											"不能同时选择两个店铺里的商品");
								}
							}
							notifyDataSetChanged();
							calulateTotalPrice();
						}
					});
			return convertView;
		}

		@Override
		public View getChildView(final int groupPosition,
				final int childPosition, boolean isLastChild, View convertView,
				ViewGroup parent) {
			// TODO Auto-generated method stub
			final ChildHolder holder;
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.item_shopcart_product,
						null);
				holder = new ChildHolder();
				holder.mBtnAdd = (Button) convertView
						.findViewById(R.id.btn_add);
				holder.mBtnReduce = (Button) convertView
						.findViewById(R.id.btn_reduce);
				holder.mCbProduct = (CheckBox) convertView
						.findViewById(R.id.cb_product);
				holder.mLayoutCb = (RelativeLayout) convertView
						.findViewById(R.id.layout_cb_product);
				holder.mIvProductPic = (ImageView) convertView
						.findViewById(R.id.iv_pic);
				holder.mTvProductName = (TextView) convertView
						.findViewById(R.id.tv_product_name);
				holder.mTvProductPrice = (TextView) convertView
						.findViewById(R.id.tv_product_price);
				holder.mTvProductNum = (TextView) convertView
						.findViewById(R.id.tv_product_num);
				convertView.setTag(holder);
			} else {
				holder = (ChildHolder) convertView.getTag();
			}
			final ProductInfo product = mDataList.get(groupPosition).prodList
					.get(childPosition);

			holder.mTvProductName.setText(TextUtils
					.isEmpty(product.productName) ? "" : product.productName);
			holder.mTvProductPrice
					.setText(TextUtils.isEmpty(product.unitPrice) ? ""
							: product.unitPrice);
			holder.mTvProductNum.setText(product.quantity + "");

			ImageLoaderHelper.displayImage(Config.HttpURLPrefix3+product.smallImageUrl, holder.mIvProductPic);
			holder.mBtnAdd.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					sendCheckInventoryReq(groupPosition, childPosition, true);
				}
			});
			holder.mBtnReduce.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					sendCheckInventoryReq(groupPosition, childPosition, false);
				}
			});
			if(mSelectProductIds.contains(product.productId)){
				holder.mCbProduct.setChecked(true);
			}else{
				holder.mCbProduct.setChecked(false);
			}
			holder.mLayoutCb.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					if(holder.mCbProduct.isChecked()){//当前商品已选中
						holder.mCbProduct.setChecked(false);
						mSelectProductIds.remove(product.productId);
						if(mSelectProductIds.size()== 0){
							mSelectCategoryIndex = -1;
						}
					}else{
						if (mSelectCategoryIndex == groupPosition
								|| mSelectCategoryIndex == -1) {// 当前没有商店产品选中或者 当前商店里商品部分选中
							mSelectCategoryIndex = groupPosition;
							holder.mCbProduct.setChecked(true);
							mSelectProductIds.add(product.productId);
						}else{
							AbToastUtil.showToast(
									ShopCartActivity.this,
									"不能同时选择两个店铺里的商品");
						}
					}
					notifyDataSetChanged();
					calulateTotalPrice();
				}
			});
			return convertView;
		}

		/**
		 * 发送检查库存请求
		 */
		private synchronized void sendCheckInventoryReq(int groupPos,
				int childPos, boolean isAdd) {
			if(isReqNum){
				return;
			}
			isReqNum = true;
			int tempQuantity = mDataList.get(groupPos).prodList.get(childPos).quantity;
			String productId = mDataList.get(groupPos).prodList.get(childPos).productId;
			if (isAdd) {
				tempQuantity++;
			} else {
				tempQuantity--;
			}
			if (tempQuantity < 0) {
				tempQuantity = 0;
			}
			Bundle data = new Bundle();
			data.putInt("groupPos", groupPos);
			data.putInt("childPos", childPos);
			data.putInt("quantity", tempQuantity);
			CheckInventoryRequest.getInstance().sendRequest(mHandler, data,
					productId, tempQuantity + "");
			
//			AbDialogUtil.showProgressDialog(ShopCartActivity.this, 0,
//					"查询数据中...");
		}

		@Override
		public boolean isChildSelectable(int groupPosition, int childPosition) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void notifyDataSetChanged() {

			super.notifyDataSetChanged();

		}

		class GroupHolder {
			/**
			 * 店铺名称
			 */
			TextView mTvShopName;
			/**
			 * 店铺选择框
			 */
			CheckBox mCbShop;
			/**
			 *
			 */
			LinearLayout mLayoutCb;
		}

		class ChildHolder {
			/**
			 * 产品名称
			 */
			TextView mTvProductName;
			/**
			 * 产品选择框
			 */
			CheckBox mCbProduct;
			/** 复选点击区域 */
			RelativeLayout mLayoutCb;
			/**
			 * 商品图片
			 */
			ImageView mIvProductPic;
			/**
			 * 减少按钮
			 */
			Button mBtnReduce;
			/**
			 * 增加按钮
			 */
			Button mBtnAdd;
			/**
			 * 产品名称
			 */
			TextView mTvProductPrice;
			/**
			 * 数量
			 */
			TextView mTvProductNum;
		}

	}
}
