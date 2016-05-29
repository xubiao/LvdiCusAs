package com.lvdi.ruitianxia_cus.model;

import java.io.Serializable;
import java.util.ArrayList;

import com.lvdi.ruitianxia_cus.model.ApplicationEntity.Application;

/**
 * C端首页获取首页模板 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月9日 上午12:05:32
 */
public class CustomerC extends BaseObject {
	public String layout;// 页面信息数据的JSON组合 c端模板
	public ProjectForC selectedProject;// 定位的组织机构
	public ArrayList<Application> layouts;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2565080426195736280L;

	public static class CustomerCLayout implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -8911923438382634345L;
		public ArrayList<Application> layout;
	}

}
