package com.lvdi.ruitianxia_cus.model;

import com.ab.util.AbJsonUtil;

/**
 * 
 * 类的详细描述：登陆用户
 * 
 * @author XuBiao
 * @version 1.0.1
 * @time 2015年10月24日 下午4:08:30
 */
public class LoginInfo {

	public String userName;
	public String passWord;
	/**
	 * 
	 */
	private static final long serialVersionUID = -5229045440621971454L;

	public String toJson() {
		// TODO Auto-generated method stub
		return AbJsonUtil.toJson(this);
	}

}
