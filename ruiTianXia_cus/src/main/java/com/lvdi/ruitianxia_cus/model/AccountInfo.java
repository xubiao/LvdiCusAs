package com.lvdi.ruitianxia_cus.model;

import com.ab.util.AbJsonUtil;

/**
 * 账户信息 类的详细描述：
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月30日 下午7:43:17
 */
public class AccountInfo extends BaseObject {
	public String partyId;// 客户的partyId
	public String realName;// 客户的真是姓名
	public String gender;// 客户的性别
	public String nickName;// 客户的昵称
	public String headIconPath;// 头像的相对路径
	public String companyName;// 客户隶属入住企业
	public String companyAddress;// 客户隶属入住企业的地址
	public String positionInCompany;// 客户隶属入驻企业的职称

	/**
	 * 
	 */
	private static final long serialVersionUID = -5229045440621971454L;

	public AccountInfo(String resultCode, String errorMessage, String partyId,
			String realName, String gender, String nickName,
			String headIconPath, String companyName, String companyAddress,
			String positionInCompany) {
		super();
		this.resultCode = resultCode;
		this.errorMessage = errorMessage;
		this.partyId = partyId;
		this.realName = realName;
		this.gender = gender;
		this.nickName = nickName;
		this.headIconPath = headIconPath;
		this.companyName = companyName;
		this.companyAddress = companyAddress;
		this.positionInCompany = positionInCompany;
	}

	public String toJson() {
		// TODO Auto-generated method stub
		return AbJsonUtil.toJson(this);
	}

}
