package com.lvdi.ruitianxia_cus.model;

public class AddressInfo extends BaseObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5531662642380323198L;

	public String contactMechId;// 收货地址的
	public String recipient;// 收货人
	public String contactNumber;// 收货人联系电话
	public String address;// 收货人地址
	public String zipCode;// 邮政编码
	/**
	 * Y 默认  N非默认
	 */
	public String isDefault;// 是否默认

	public AddressInfo(String contactMechId, String recipient,
			String contactNumber, String address, String zipCode,
			String isDefault) {
		super();
		this.contactMechId = contactMechId;
		this.recipient = recipient;
		this.contactNumber = contactNumber;
		this.address = address;
		this.zipCode = zipCode;
		this.isDefault = isDefault;
	}

}
