package com.zwg.socketdemo;

import com.google.gson.Gson;

public class SimpleUtils {

//	public static HttpUtils httpUtils;
	public static Gson gson;
//	public static HttpUtils getHttpUtils()
//	{
//		if(httpUtils==null)
//		{
//			httpUtils=new HttpUtils();
//		}
//		httpUtils.configTimeout(10000);
//		httpUtils.configSoTimeout(10000);
//		return httpUtils;
//	}
	public static Gson getgson()
	{
		if(gson==null)
		{
			gson=new Gson();
		}
		return gson;
	}
}
