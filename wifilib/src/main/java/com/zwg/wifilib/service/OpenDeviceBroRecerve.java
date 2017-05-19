package com.zwg.wifilib.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.List;

/**
 * Created by Administrator on 2016/11/11.
 */
public class OpenDeviceBroRecerve extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Intent ser=new Intent(context,ContentRecever.class);
            context.startService(ser);
            Log.e("--------->","2222222222");
        }
    }
}
