package com.ab.http;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class AbRequestGetJsonParams extends AbRequestParams {

	/**
	 * 获取参数字符串.
	 * 
	 * @return the param string
	 */
	public String getParamString() {
		 List<BasicNameValuePair> paramsList = new
		 LinkedList<BasicNameValuePair>();
		 for (ConcurrentHashMap.Entry<String, String> entry :
		 urlParams.entrySet()) {
		 paramsList.add(new BasicNameValuePair(entry.getKey(),
		 entry.getValue()));
		 }
		 return "?"+URLEncodedUtils.format(paramsList, HTTP.UTF_8);

//		try {
//			return URLEncoder.encode(paramBuilder.toString(), HTTP.UTF_8);
//		} catch (UnsupportedEncodingException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//			return paramBuilder.toString();
//		}
//		return paramBuilder.toString();
	}
}
