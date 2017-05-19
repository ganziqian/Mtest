package com.zwg.socketdemo;

import android.app.Application;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import org.xutils.x;

import java.util.HashMap;

public class BaseApplication extends MultiDexApplication {
	private static String TAG = "AppApplication";
	public static Context context;
	public static int REGISTER_PATH = 0;//启动注册页面的路径标示；
	public MessageCode jsonbean;
	public static HashMap<String, Boolean> bondstate = new HashMap<String, Boolean>();
	private Intent intent;
	public Boolean b = false;
	public Boolean b2 = false;
	public int jishuqi = 0;
	private static int mainThreadId;
	private NotificationCompat.Builder mBuilder;
	private NotificationManager mNotificationManager;

	public BaseApplication() {
		super();
		context = this;
		x.Ext.init(this);//初始化
		x.Ext.setDebug(true);//设置是否输出Debug.
	}


	@Override
	public void onCreate() {
		super.onCreate();
		//设置打印消息

		mainThreadId = android.os.Process.myTid();
		initData();

	}

	private void initData() {
		// TODO Auto-generated method stub

//		Intent startServiceIntent=new Intent(this, EMChatService.class);
//		startServiceIntent.putExtra("reason", "boot");
//		startService(startServiceIntent);


		String s = "{<type>:<notice>,<sub_type>:<binding>,<appkey>:<>,<option>:<>,< packages >:<>}";
		if (s.contains("{<type>:")) {
			String s1 = s.replaceAll("<", "\"");
			String s2 = s1.replaceAll(">", "\"");
			Log.e("______", s2);
		}

	}
}


