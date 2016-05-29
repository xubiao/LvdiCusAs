package com.lvdi.ruitianxia_cus.view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import android.content.Context;
import android.content.res.AssetManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;

import com.ab.util.AbDialogUtil;
import com.ab.util.AbLogUtil;
import com.lvdi.ruitianxia_cus.R;
import com.lvdi.ruitianxia_cus.model.city.CityModel;
import com.lvdi.ruitianxia_cus.model.city.DistrictModel;
import com.lvdi.ruitianxia_cus.model.city.ProvinceModel;
import com.lvdi.ruitianxia_cus.util.XmlParserHandler;
import com.lvdi.ruitianxia_cus.view.ScrollerNumberPicker.OnSelectListener;

public class CitySelectView {
	public interface CitySelectCallback {
		public void onSaveCity(String province, String city, String district,
				String zipcode);
	}

	private CitySelectCallback mListener;
	/**
	 * 所有省
	 */
	protected ArrayList<String> mProvinceDatas = new ArrayList<String>();
	/**
	 * key - 省 value - 市
	 */
	protected Map<String, ArrayList<String>> mCitisDatasMap = new HashMap<String, ArrayList<String>>();
	/**
	 * key - 市 values - 区
	 */
	protected Map<String, ArrayList<String>> mDistrictDatasMap = new HashMap<String, ArrayList<String>>();

	/**
	 * key - 区 values - 邮编
	 */
	protected Map<String, String> mZipcodeDatasMap = new HashMap<String, String>();

	/**
	 * 当前省的名称
	 */
	protected String mCurrentProviceName;
	/**
	 * 当前市的名称
	 */
	protected String mCurrentCityName;
	/**
	 * 当前区的名称
	 */
	protected String mCurrentDistrictName = "";

	/**
	 * 当前区的邮政编码
	 */
	protected String mCurrentZipCode = "";

	private Context mContext;
	private LayoutInflater mInflater;
	private View mView;
	/** 滑动控件 */
	private ScrollerNumberPicker provincePicker;
	private ScrollerNumberPicker cityPicker;
	private ScrollerNumberPicker districtPicker;

	public CitySelectView(Context context, CitySelectCallback listener) {
		mContext = context;
		mListener = listener;
		initView();
	}

	/**
	 * 显示
	 * 
	 * @author Xubiao
	 */
	public void showView() {
		AbDialogUtil.showFullScreenDialog(mView);
	}

	/**
	 * 消失
	 * 
	 * @author Xubiao
	 */
	public void dismiss() {
		AbDialogUtil.removeDialog(mView);
	}

	private void initView() {
		initProvinceDatas();
		mInflater = LayoutInflater.from(mContext);
		mView = mInflater.inflate(R.layout.address_city_select_view, null);
		mView.findViewById(R.id.closeTv).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						AbDialogUtil.removeDialog(mView);
					}
				});
		mView.findViewById(R.id.saveTv).setOnClickListener(
				new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mListener.onSaveCity(mCurrentProviceName,
								mCurrentCityName, mCurrentDistrictName,
								mCurrentZipCode);
						AbDialogUtil.removeDialog(mView);
					}
				});

		provincePicker = (ScrollerNumberPicker) mView
				.findViewById(R.id.province);
		cityPicker = (ScrollerNumberPicker) mView.findViewById(R.id.city);
		districtPicker = (ScrollerNumberPicker) mView.findViewById(R.id.couny);

		provincePicker.setData(mProvinceDatas);
		provincePicker.setDefault(0);

		cityPicker.setData(mCitisDatasMap.get(mProvinceDatas.get(0)));
		cityPicker.setDefault(0);

		districtPicker.setData(mDistrictDatasMap.get(mCitisDatasMap.get(
				mProvinceDatas.get(0)).get(0)));
		districtPicker.setDefault(0);

		provincePicker.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endSelect(int id, String text) {
				// TODO Auto-generated method stub
				AbLogUtil.d(getClass(), "province-" + "id-" + id + "text-"
						+ text);
				mCurrentProviceName = text;
				mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName).get(
						0);
				mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)
						.get(0);
				mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);

				cityPicker.setData(mCitisDatasMap.get(mCurrentProviceName));
				cityPicker.setDefault(0);

				districtPicker.setData(mDistrictDatasMap.get(mCurrentCityName));
				districtPicker.setDefault(0);

			}
		});
		cityPicker.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endSelect(int id, String text) {
				// TODO Auto-generated method stub
				AbLogUtil.d(getClass(), "city-" + "id-" + id + "text-" + text);
				mCurrentCityName = text;
				mCurrentDistrictName = mDistrictDatasMap.get(mCurrentCityName)
						.get(0);
				mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);

				districtPicker.setData(mDistrictDatasMap.get(mCurrentCityName));
				districtPicker.setDefault(0);
			}
		});
		districtPicker.setOnSelectListener(new OnSelectListener() {

			@Override
			public void selecting(int id, String text) {
				// TODO Auto-generated method stub

			}

			@Override
			public void endSelect(int id, String text) {
				// TODO Auto-generated method stub
				AbLogUtil.d(getClass(), "count-" + "id-" + id + "text-" + text);
				mCurrentDistrictName = text;
				mCurrentZipCode = mZipcodeDatasMap.get(mCurrentDistrictName);
			}
		});
	}

	/**
	 * 解析省市区的XML数据
	 */

	protected void initProvinceDatas() {
		List<ProvinceModel> provinceList = null;
		AssetManager asset = mContext.getAssets();
		try {
			InputStream input = asset.open("province_data.xml");
			// 创建一个解析xml的工厂对象
			SAXParserFactory spf = SAXParserFactory.newInstance();
			// 解析xml
			SAXParser parser = spf.newSAXParser();
			XmlParserHandler handler = new XmlParserHandler();
			parser.parse(input, handler);
			input.close();
			// 获取解析出来的数据
			provinceList = handler.getDataList();
			// */ 初始化默认选中的省、市、区
			if (provinceList != null && !provinceList.isEmpty()) {
				mCurrentProviceName = provinceList.get(0).getName();
				List<CityModel> cityList = provinceList.get(0).getCityList();
				if (cityList != null && !cityList.isEmpty()) {
					mCurrentCityName = cityList.get(0).getName();
					List<DistrictModel> districtList = cityList.get(0)
							.getDistrictList();
					mCurrentDistrictName = districtList.get(0).getName();
					mCurrentZipCode = districtList.get(0).getZipcode();
				}
			}
			// */

			for (int i = 0; i < provinceList.size(); i++) {
				// 遍历所有省的数据
				mProvinceDatas.add(provinceList.get(i).getName());
				List<CityModel> cityList = provinceList.get(i).getCityList();
				ArrayList<String> cityNames = new ArrayList<String>();
				for (int j = 0; j < cityList.size(); j++) {
					// 遍历省下面的所有市的数据
					cityNames.add(cityList.get(j).getName());
					List<DistrictModel> districtList = cityList.get(j)
							.getDistrictList();
					ArrayList<String> distrinctNameArray = new ArrayList<String>();
					DistrictModel[] distrinctArray = new DistrictModel[districtList
							.size()];
					for (int k = 0; k < districtList.size(); k++) {
						// 遍历市下面所有区/县的数据
						DistrictModel districtModel = new DistrictModel(
								districtList.get(k).getName(), districtList
										.get(k).getZipcode());
						// 区/县对于的邮编，保存到mZipcodeDatasMap
						mZipcodeDatasMap.put(districtList.get(k).getName(),
								districtList.get(k).getZipcode());
						distrinctArray[k] = districtModel;
						distrinctNameArray.add(districtModel.getName());
					}
					// 市-区/县的数据，保存到mDistrictDatasMap
					mDistrictDatasMap.put(cityNames.get(j), distrinctNameArray);
				}
				// 省-市的数据，保存到mCitisDatasMap
				mCitisDatasMap.put(provinceList.get(i).getName(), cityNames);
			}
		} catch (Throwable e) {
			e.printStackTrace();
		} finally {

		}
	}

}
