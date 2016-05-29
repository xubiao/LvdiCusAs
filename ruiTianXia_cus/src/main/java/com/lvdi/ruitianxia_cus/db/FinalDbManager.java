package com.lvdi.ruitianxia_cus.db;

import net.tsz.afinal.FinalDb;
import net.tsz.afinal.FinalDb.DbUpdateListener;
import android.database.sqlite.SQLiteDatabase;

import com.lvdi.ruitianxia_cus.global.MyApplication;

public class FinalDbManager {
	private static final int DATABASE_VERSION = 7;
	public static final String TAB_NAME = "lvdi_fdb";
	private FinalDb fdb;
	private static FinalDbManager fdbManager;

	private boolean isUpdate = false;
	public FinalDbManager() {
		
		fdb = FinalDb.create(MyApplication.getInstance(), TAB_NAME, true,
				DATABASE_VERSION, new DbUpdateListener() {
					
					@Override
					public void onUpgrade(SQLiteDatabase arg0, int arg1, int arg2) {
						// TODO Auto-generated method stub
						isUpdate = true;
					}
				});
		if(isUpdate){
			fdb.dropDb();
		}
	}

	public FinalDb getFdb() {
		return fdb;
	}

	public static FinalDb getFinalDb() {
		if (fdbManager == null) {
			fdbManager = new FinalDbManager();
		}
		return fdbManager.getFdb();
	}
}
