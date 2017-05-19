package com.zwg.socketdemo;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;


import com.google.gson.Gson;

import org.xutils.common.Callback.Cancelable;
import org.xutils.common.Callback.CommonCallback;
import org.xutils.http.RequestParams;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {
	private int SYSTEMTYPE=1;//系统类型，android系统
	private String LOGIN="0";//登录类型，0表示正常登陆
	private EditText phoneNumber,loginPassword;
	private SharedPreferences sp;
	private Button bt_sure,bt_cancal,loginBTLogin;
	private String headpic="";
	private Cancelable post;

	public static final String URL="http://api.xjkb.com:8090/";

	public static final String URLLOGIN="api/login_parent.do?systemtype=1";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
		sp = getSharedPreferences("config", MODE_PRIVATE);
		initUI();
		initData();
	}
	private void initUI() {
		// TODO Auto-generated method stub
		phoneNumber=(EditText) findViewById(R.id.phoneNumber);
		loginPassword=(EditText) findViewById(R.id.loginPassword);
		loginBTLogin=(Button) findViewById(R.id.loginBTLogin);

		ListView lv= (ListView) findViewById(R.id.lv);
		final List<String> list=new ArrayList<>();
		list.add("18122384421");
		list.add("13751634713");
		list.add("15897489589");
		list.add("18220686040");
		ArrayAdapter<String> arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,list);
		lv.setAdapter(arrayAdapter);
		lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				phoneNumber.setText(list.get(position));
			}
		});
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(phoneNumber.getWindowToken(),0);
	}

	private void initData()
	{


		loginBTLogin.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v){
				onLogin();
			}
		});
	}
	/**
	 * 登录按钮的点击事件
	 * @param
	 */
	public void onLogin()
	{
		TelephonyManager telephonyManager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		//String deviceId = telephonyManager.getDeviceId();
		final String phoneNum = phoneNumber.getText().toString().trim();
		String loginPsw = loginPassword.getText().toString().trim();
		String loginEncodePsw=MD5Utils.encode(loginPsw);
		sp.edit().putString("phone", phoneNum).commit();
		sp.edit().putString("password",loginPsw).commit();
		if(!TextUtils.isEmpty(phoneNum) && !TextUtils.isEmpty(loginPsw))
		{
			RequestParams params=new RequestParams(URL+URLLOGIN);
			params.addBodyParameter("loginname", phoneNum);

			params.addBodyParameter("loginpwd", loginEncodePsw);
			params.addBodyParameter("deviceid", "2222");
			params.addBodyParameter("login", LOGIN);
			post = x.http().post(params, new CommonCallback<String>() {

				@Override
				public void onCancelled(CancelledException arg0) {
				}
				@Override
				public void onError(Throwable arg0, boolean arg1) {
					Log.e("=======",arg0.toString());
					LOGIN="1";
					//UIUtils.loadonFailure(arg0.toString(), dialog);
				}
				@Override
				public void onFinished() {
				}
				@Override
				public void onSuccess(String arg0) {
					// TODO Auto-generated method stub
					Log.e("======",arg0);
					System.out.println("登录返回结果:"+arg0);
					MessageCode jsonresult = solverJson(arg0);

				//	{"code":"1003","data":[{}],"msg":"账号已在其他设备登录","request_url":"api/login_parent"}

					if(jsonresult.data.size()!=0)
					{
						MessageCode.Message message = jsonresult.data.get(0);
						sp.edit().putString("userid",message.userid).commit();//userid保存到sp中

						Log.e("--------",message.userid);
						Log.e("--------",message.sessionid);

						sp.edit().putString("nickname",message.nickname).commit();//nickname保存到sp中
						sp.edit().putString("sessionid",message.sessionid).commit();//sessionid保存到sp中
						headpic = jsonresult.data.get(0).headpic;
						sp.edit().putString(phoneNum, headpic).commit();

						Intent intent=new Intent(LoginActivity.this,MainActivity.class);
						intent.putExtra("aa","xjkb.api.login."+message.userid+"."+message.sessionid);
						startActivity(intent);
					}
					//openHomeActivity(jsonresult);
				}
			} );
			if(post!=null)
			{
			/*	dialog = new Mydialogconnect(this, post);
				dialog.show();*/
			}
		}else{
			//MyToast.toast("用户名或者密码不能为空");
		}




	}
	/**
	 * 登录打开homeactivity
	 * @param
	 * @param jsonresult
	 *//*
	public void openHomeActivity(final MessageCode jsonresult)
	{

		if (jsonresult.code == 1) {//登录成功

			String userid=jsonresult.data.get(0).userid;
			String huanxinPassword=jsonresult.data.get(0).huanxinpwd;

			if (!TextUtils.isEmpty(userid)&&!TextUtils.isEmpty(huanxinPassword)) {
				//环信id和环信密码都不能为空
				System.out.println("环信账号和密码"+userid+"---"+huanxinPassword);
				LoginFromEM(userid,huanxinPassword);
				sp.edit().putBoolean("islogin", true).commit();
				UIUtils.getHandler().postDelayed(new Runnable() {
					@Override
					public void run() {
						Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
						startActivity(intent);
						dialog.dismiss();
						finish();
					}
				}, 1000);
			}else{
				MyToast.toast("环信账号或者密码异常,环信登录失败");
			}
		} else if (jsonresult.code == 3 || jsonresult.code == 2) {
			MyToast.toast(jsonresult.msg);//账户或者密码错误
			dialog.dismiss();
		} else if (jsonresult.code == 1003) {//强制登录
			dialog.dismiss();
			final Dialog dialog0=new Dialog(this, R.style.dialog_white);
			View view = View.inflate(this, R.layout.add_equepment_dialog, null);
			dialog0.setContentView(view);
			TextView tv_content=(TextView) view.findViewById(R.id.tv_content);
			tv_content.setText(jsonresult.msg+",是否强制登录");
			Button bt_sure=(Button) view.findViewById(R.id.bt_sure);
			Button bt_cancal=(Button) view.findViewById(R.id.bt_cancal);
			bt_sure.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					LOGIN="1";
					onLogin();
					dialog0.dismiss();
				}
			});
			bt_cancal.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					dialog0.dismiss();
				}
			});
			dialog0.show();
		}
	}
	*//**
	 * 环信登陆
	 * @param userid
	 * @param huanxinPassword
	 *//*
	private void LoginFromEM(String userid, String huanxinPassword) {
		EMClientOperation.getInstance(UIUtils.getContext()).login(userid, huanxinPassword, new EMClientOperation.WSHInterFace() {
			@Override
			public void onSuccess() {
//	                WSHToast.ShowAtUiThread(LoginActivity.this,"登陆成功！");
//	            	sp.edit().putBoolean("islogin", true).commit();
//	                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
//	                dialog.dismiss();
//	                finish();
			}

			@Override
			public void onProgress(int progress, String status) {
				WSHToast.ShowAtUiThread(LoginActivity.this, "正在登陆...！");
			}

			@Override
			public void onError(int code, String message) {
				WSHToast.ShowAtUiThread(LoginActivity.this, "登陆失败,请检查网络是否连接！");
			}
		});
	}
//	*//**
//	 * 登录对话框
//	 *//*
//	public void showLoginDialog()
//	{
//		dialog = new Dialog(this, R.style.dialog);
//		View view = View.inflate(this, R.layout.dialog_login, null);
//		dialog.setCanceledOnTouchOutside(false);
//		dialog.setContentView(view);
//		dialog.show();
//	}*/
	/**
	 * 解析json
	 * @param
	 */
	public MessageCode solverJson(String json)
	{
		Gson gson = SimpleUtils.getgson();
		MessageCode jsonresult = gson.fromJson(json, MessageCode.class);
		return jsonresult;
	}


}
