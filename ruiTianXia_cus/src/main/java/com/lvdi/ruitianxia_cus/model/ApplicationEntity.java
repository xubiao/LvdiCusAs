package com.lvdi.ruitianxia_cus.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.ab.util.AbJsonUtil;

/**
 * 4.18客户B端首页，获取请求时的项目ID可用的B端应用 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年11月8日 下午11:32:59
 */
public class ApplicationEntity extends BaseObject {
	public List<Application> applications = new ArrayList<ApplicationEntity.Application>();// 应用列表
	public boolean hasNext;// 是否有下一页 true有 false 没有
	public int count;// 总数
	/**
	 * 
	 */
	private static final long serialVersionUID = 2918569603950542257L;

	public String toJson() {
		// TODO Auto-generated method stub
		return AbJsonUtil.toJson(this);
	}

	/**
	 * B端应用 类的详细描述：
	 * 
	 * @author XuBiao
	 * @version 1.0.1
	 * @time 2015年11月8日 下午11:33:16
	 */
	public static class Application implements Serializable {
		/**
		 * 
		 */
		private static final long serialVersionUID = -5215706848734268537L;
		public String appIcon;// 应用ICON的URL下载地址
		public String appName;// 应用名称
		public String catalogId;// 如果是电商性质，对应的catalogId
		public long createTime;
		public int id;// 应用ID
		public String orderType;// 如果是订购预约类型，区分订购还是预约
		public String visitkeyword;// H5：对应H5的地址NATIVE：对应和前段约束好的关键字 URL路径=
									// protocol+visitkeyword+？
		public String visitType;// 访问方式：H5或者NATIVE
		public String appIconKey;// 应用ICON的匹配字
		public String protocol;// 如果是H5，访问协议
		public String appIntroduction;// 描述
		public String timeNode;

		public Application(String appIcon, String appName, String catalogId,
				long createTime, int id, String orderType, String visitkeyword,
				String visitType, String appIconKey, String protocol,
				String appIntroduction, String timeNode) {
			super();
			this.appIcon = appIcon;
			this.appName = appName;
			this.catalogId = catalogId;
			this.createTime = createTime;
			this.id = id;
			this.orderType = orderType;
			this.visitkeyword = visitkeyword;
			this.visitType = visitType;
			this.appIconKey = appIconKey;
			this.protocol = protocol;
			this.appIntroduction = appIntroduction;
			this.timeNode = timeNode;
		}

		public void update(Application application) {
			this.appIcon = application.appIcon;
			this.appName = application.appName;
			this.catalogId = application.catalogId;
			this.createTime = application.createTime;
			this.id = application.id;
			this.orderType = application.orderType;
			this.visitkeyword = application.visitkeyword;
			this.visitType = application.visitType;
			this.appIconKey = application.appIconKey;
			this.protocol = application.protocol;
			this.appIntroduction = application.appIntroduction;
		}

		public void removeApplication() {
			this.appIcon = "";
			this.appName = "";
			this.catalogId = "";
			this.createTime = 0L;
			this.id = 0;
			this.orderType = "";
			this.visitkeyword = "";
			this.visitType = "";
			this.appIconKey = "";
			this.protocol = "";
			this.appIntroduction = "";
		}

	}
}
