package com.ab.http;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.http.message.BasicNameValuePair;

public class AbRequestPostJsonParams extends AbRequestParams {

	@Override
	public List<BasicNameValuePair> getParamsList() {
		List<BasicNameValuePair> paramsList = new LinkedList<BasicNameValuePair>();
		for (ConcurrentHashMap.Entry<String, String> entry : urlParams
				.entrySet()) {
			paramsList.add(new BasicNameValuePair(entry.getKey(), entry
					.getValue()));
		}
		return paramsList;
	}
}
