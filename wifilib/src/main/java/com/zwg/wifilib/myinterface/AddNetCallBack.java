package com.zwg.wifilib.myinterface;

import com.zwg.wifilib.dialog.WifiOperator;

/**
 * Created by Administrator on 2017/4/2.
 */

public interface AddNetCallBack {
    public abstract void inputBack(String ssid, String psw, WifiOperator.WifiCipherType inType);
}
