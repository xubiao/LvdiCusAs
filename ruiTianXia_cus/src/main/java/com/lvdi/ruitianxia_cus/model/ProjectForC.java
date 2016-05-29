/*
 * Copyright (C), 2002-2015, 苏宁易购电子商务有限公司
 * FileName: ProjectForC.java
 * Author:   14070414
 * Date:     2015年11月2日 下午5:09:09
 * Description: //模块目的、功能描述      
 * History: //修改记录
 * <author>      <time>      <version>    <desc>
 * 修改人姓名             修改时间            版本号                  描述
 */
package com.lvdi.ruitianxia_cus.model;

import com.ab.util.AbJsonUtil;

/**
 * 〈一句话功能简述〉<br>
 * 〈功能详细描述〉 C端首页获取所有项目
 * 
 * @author 14070414
 * @see [相关类/方法]（可选）
 * @since [产品/模块版本] （可选）
 */
public class ProjectForC extends BaseObject {
	/**
	 */
	private static final long serialVersionUID = -8621681142366719387L;
	public String comments;
	public int companyId;
	public int countryId;
	public long createDate;
	public long modifiedDate;
	public String name;
	public int organizationId;
	public int parentOrganizationId;
	public boolean recursable;
	public int regionId;
	public int statusId;
	public String treePath;
	public String type;
	public int userId;
	public String userName;
	public String uuid;

	/**
	 * @param comments
	 * @param companyId
	 * @param countryId
	 * @param createDate
	 * @param modifiedDate
	 * @param name
	 * @param organizationId
	 * @param parentOrganizationId
	 * @param recursable
	 * @param regionId
	 * @param statusId
	 * @param treePath
	 * @param type
	 * @param userId
	 * @param userName
	 * @param uuid
	 */
	public ProjectForC(String comments, int companyId, int countryId,
			long createDate, long modifiedDate, String name,
			int organizationId, int parentOrganizationId, boolean recursable,
			int regionId, int statusId, String treePath, String type,
			int userId, String userName, String uuid) {
		super();
		this.comments = comments;
		this.companyId = companyId;
		this.countryId = countryId;
		this.createDate = createDate;
		this.modifiedDate = modifiedDate;
		this.name = name;
		this.organizationId = organizationId;
		this.parentOrganizationId = parentOrganizationId;
		this.recursable = recursable;
		this.regionId = regionId;
		this.statusId = statusId;
		this.treePath = treePath;
		this.type = type;
		this.userId = userId;
		this.userName = userName;
		this.uuid = uuid;
	}

	public String toJson() {
		// TODO Auto-generated method stub
		return AbJsonUtil.toJson(this);
	}
}
