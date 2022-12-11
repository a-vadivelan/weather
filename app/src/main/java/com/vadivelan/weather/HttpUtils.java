package com.vadivelan.weather;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

public class HttpUtils {
	private static final AsyncHttpClient client = new AsyncHttpClient();
	/*public static void get(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
		client.get(getAbsoluteUrl(url),params,responseHandler);
	}
	public static void post(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
		client.post(getAbsoluteUrl(url),params,responseHandler);
	}*/
	public static void getByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
		client.get(url,params,responseHandler);
	}
	/*8public static void postByUrl(String url, RequestParams params, AsyncHttpResponseHandler responseHandler){
		client.post(url,params,responseHandler);
	}
	private static String getAbsoluteUrl(String relativeUrl){
		return baseUrl+relativeUrl;
	}*/
}
