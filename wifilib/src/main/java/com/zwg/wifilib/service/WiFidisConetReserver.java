package com.zwg.wifilib.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.zwg.wifilib.utils.SaveUtils;


/**
 * Created by Administrator on 2016/12/13.
 */
public class WiFidisConetReserver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {


        if(intent.getAction().equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)){//wifi连接上与否
            // System.out.println("网络状态改变");
            NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if(info.getState().equals(NetworkInfo.State.DISCONNECTED)){

                Log.e("--------->",ContentRecever.isy+"");
                if(ContentRecever.isy==0){
                    if(SaveUtils.getIsLian(context).equals("")) {  //如果信息被清空则可以自动重连
                        Log.e("--------->", "网络断开,开启服务重连");
                        Intent ser=new Intent(context,ContentRecever.class);
                        context.startService(ser);
                    }else {
                        Log.e("--------->", "限制重连");
                    }
                }


            }


        }

    }
}
