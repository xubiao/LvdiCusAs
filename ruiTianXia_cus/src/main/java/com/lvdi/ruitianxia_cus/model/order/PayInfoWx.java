/*** */
package com.lvdi.ruitianxia_cus.model.order;

import com.lvdi.ruitianxia_cus.model.BaseObject;

/**
 * @author wangcz
 * 
 */
public class PayInfoWx extends BaseObject {
	public WxPayBean payInfo;
	
	public  static class WxPayBean{
		/*** 公众账号ID */
		public String appid = "";
		/*** 商户号 */
		public String partnerid = "";
		/*** 预支付交易会话ID */
		public String prepayid = "";
//		/*** 扩展字段 (同package)*/
//		public String extend = "";
		/*** 随机字符串 */
		public String noncestr = "";
		/*** 时间戳 */
		public String timestamp = "";
		/*** 签名 */
		public String sign = "";
		
		public String packageValue="";
	}

	 

	 

}
