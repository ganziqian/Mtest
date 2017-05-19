package com.zwg.socketdemo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MessageCode implements Serializable{

	@Override
	public String toString() {
		return "MessageCode [code=" + code + ", msg=" + msg + ", request_url="
				+ request_url + ", data=" + data + "]";
	}
	public int code;
	public String msg;
	public String request_url;
	public ArrayList<Message> data;	
	public class Message implements Serializable
	{
		@Override
		public String toString() {
			return "Message [message_code=" + message_code + ", msg=" + msg
					+ ", relatename=" + relatename + ", loginname=" + loginname
					+ ", phone=" + phone + ", username=" + username
					+ ", sessionid=" + sessionid + ", relateid=" + relateid
					+ ", result=" + result + ", usertype=" + usertype
					+ ", familyid=" + familyid + ", userid=" + userid
					+ ", huanxinpwd=" + huanxinpwd + ", huanxinname="
					+ huanxinname + ", nickname=" + nickname + ", birthday="
					+ birthday + ", machineid=" + machineid + ", ownuserid="
					+ ownuserid + ", deviceid=" + deviceid + ", headpic="
					+ headpic + ", address=" + address + ", bluetooth="
					+ bluetooth + ", child=" + child + "]";
		}
		public String message_code;
		public String msg;
		public String relatename;
		public String loginname;
		public String phone;
		public String username;
		public String childname;
		public String sessionid;
		public int relateid;
		public int result;
		public int usertype;
		public int familyid;
		public String userid;
		public String huanxinpwd;
		public String huanxinname;
		public String nickname;
		public String birthday;
		public String machineid;
		public String ownuserid;
		public String deviceid;
		public String headpic;
		public String thumbpic;
		public String address;
		public String bluetooth;
		public String describe;
		public String updatedescribe;
		public String url;
		public String childid;
		public int ismain;
		public String sex;
		public String deviceidname;
		public String parentid;
		public List<Childinfo> child; 
		public List<Machines> device;
	}
	public class Childinfo{
		public String birthday;
		public String headpic;
		public String sex;
		public String username;
		public String userid;
		public String deviceidname;
		public String parentid;
	}
	public class Machines implements Serializable{
		public String devicename;
	}
}
