package com.lvdi.ruitianxia_cus.activity.shopcart;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;

import com.lvdi.ruitianxia_cus.db.FinalDbManager;
import com.lvdi.ruitianxia_cus.model.shopcart.DbCategory;
import com.lvdi.ruitianxia_cus.model.shopcart.DbProduct;
import com.lvdi.ruitianxia_cus.model.shopcart.ProductInfo;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqConfirmOrder;
import com.lvdi.ruitianxia_cus.model.shopcart.ReqProductItem;

public class CartManager {

	private static CartManager manager;
	private Context context;

	public static boolean cartNeedUpdate = false;

	public static CartManager getInstance() {
		if (manager == null) {
			manager = new CartManager();
		}
		return manager;
	}

	/**
	 * @return 返回所有商品信息
	 */
	public List<DbCategory> getAllProducts() {
		List<DbCategory> shopList = new ArrayList<DbCategory>();
		List<DbProduct> products = FinalDbManager.getFinalDb().findAll(
				DbProduct.class, "categoryId");
		if (products != null) {
			DbCategory cs = null;
			String catId = null;
			for (DbProduct product : products) {
				if (!TextUtils.isEmpty(product.getCategoryId())) {
					// product.productId="10004";//测试用
					if (!product.getCategoryId().equals(catId)) {
						if (cs != null) {
							shopList.add(cs);
						}
						catId = product.getCategoryId();
						cs = new DbCategory();
						cs.categoryId = catId;
						cs.catalogId = product.catalogId;
						cs.orderTypeId = product.orderTypeId;
						cs.prodList = new ArrayList<DbProduct>();
						cs.prodList.add(product);
					} else if (cs != null) {
						if (cs.prodList == null) {
							cs.prodList = new ArrayList<DbProduct>();
						}
						cs.prodList.add(product);
					}
				}
			}
			if (cs != null) {// 最后一个店铺要手动添加
				shopList.add(cs);
			}
			return shopList;
		}
		return shopList;
	}

	public static List<DbCategory> convert(List<DbProduct> products) {
		List<DbCategory> shopList = new ArrayList<DbCategory>();
		if (products != null) {
			DbCategory cs = null;
			String catId = null;
			for (DbProduct product : products) {
				if (!TextUtils.isEmpty(product.getCategoryId())) {
					// product.productId="10004";//测试用
					if (!product.getCategoryId().equals(catId)) {
						if (cs != null) {
							shopList.add(cs);
						}
						catId = product.getCategoryId();
						cs = new DbCategory();
						cs.categoryId = catId;
						cs.catalogId = product.catalogId;
						cs.orderTypeId = product.orderTypeId;
						cs.prodList = new ArrayList<DbProduct>();
						cs.prodList.add(product);
					} else if (cs != null) {
						if (cs.prodList == null) {
							cs.prodList = new ArrayList<DbProduct>();
						}
						cs.prodList.add(product);
					}
				}
			}
			if (cs != null) {// 最后一个店铺要手动添加
				shopList.add(cs);
			}
		}
		return shopList;
	}

	public void insert(List<DbProduct> plist) {

		for (DbProduct dp : plist) {
			// List<DbProduct> tp = FinalDbManager.getFinalDb().findAllByWhere(
			// DbProduct.class, "productId='" + dp.productId + "'");
			// if (tp != null && tp.size() > 0) {
			// tp.get(0).quantity += dp.quantity;
			// FinalDbManager.getFinalDb().update(tp.get(0));
			// } else {
			// FinalDbManager.getFinalDb().save(dp);
			// }
			FinalDbManager.getFinalDb().deleteByWhere(DbProduct.class,
					"productId='" + dp.productId + "'");
			FinalDbManager.getFinalDb().save(dp);
			// FinalDbManager.getFinalDb().deleteByWhere(DbProduct.class
			// , "categoryId='"+dp.categoryId+"'");
		}
		// for (DbProduct dp : plist) {
		// FinalDbManager.getFinalDb().save(dp);
		// }
	}

	public void delCarts(ReqConfirmOrder data) {
		cartNeedUpdate = true;
		for (ReqProductItem pi : data.productItems) {
			FinalDbManager.getFinalDb().deleteByWhere(
					DbProduct.class,
					"categoryId='" + data.categoryId + "'and productId='"
							+ pi.productId + "'");
		}
	}

	/**
	 * 更新数据库库存信息
	 */
	public void updateInventory(ProductInfo product, String categoryId) {
		DbProduct dp = new DbProduct();
		dp.productId = product.productId;
		dp.quantity = product.quantity;
		dp.categoryId = categoryId;
		FinalDbManager.getFinalDb().update(dp,
				"productId='" + dp.productId + "'");
	}
}
