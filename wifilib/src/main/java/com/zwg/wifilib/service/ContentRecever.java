package com.zwg.wifilib.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zwg.wifilib.WifiHelper;
import com.zwg.wifilib.utils.SaveUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/14.
 */
public class ContentRecever extends Service{

    private  List<ScanResult> mWifiList;
    private int islianjie=0;

    private WifiHelper wifiHelper;
    private List<String> ssids=new ArrayList<>();

    public static int isy=0;

    private WifiHelper.WifiHelperListener wifiHelperListener;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        SaveUtils.deletInfo(getBaseContext());
        isy=1;
        wifiHelper=WifiHelper.getInstance(getBaseContext());

        if (wifiHelper.checkState() == 1) {
            //未打开
            wifiHelper.openWifi();
        } else if (wifiHelper.checkState() == 3) {
            //已打开
        }
        //尝试连接已经连接过得wifi


        wifiHelper.getWifiManager().startScan();
        mWifiList = wifiHelper.getWifiManager().getScanResults();
        wifiHelper.tryAutoConnect(mWifiList);
        Log.e("--------->","z自动重连");
        // Toast.makeText(getBaseContext(),"尝试连接wifi",Toast.LENGTH_LONG).show();


        wifiHelperListener=new WifiHelper.WifiHelperListener() {
            @Override
            public void onWifiState(WifiHelper.WifiState wifiState,String SSID) {
                try {
                    if (wifiState == WifiHelper.WifiState.WIFI_STATE_CONNECTED) {
                        stopSelf();
                    } else if (wifiState == WifiHelper.WifiState.WIFI_STATE_CONNECT_FAILED) {

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onSupplicantState(SupplicantState SupplicantState, int error) {
                try {
                    if (SupplicantState != null) {
                        if (SupplicantState == SupplicantState.COMPLETED) {
                            stopSelf();
                        }
                        // Log.e("--ww---onSupplicantState----", SupplicantState + "-" + error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onScanSuccess(List<ScanResult> wifiList) {
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        new NyThread().start();
        return super.onStartCommand(intent, flags, startId);
    }



    private void contengWifi(){
        for (int i = 0; i <mWifiList.size(); i++) {
            if(ssids.size()>0){
                for (int j=0;j<ssids.size();j++){
                    if(ssids.get(j).equals(mWifiList.get(i).SSID)){
                        return;
                    }
                }
            }

            WifiConfiguration wifiConfiguration = wifiHelper.getConfiguredNetwork(mWifiList.get(i).SSID);

            if (wifiConfiguration != null) {
                Log.e("--------->尝试连接wifi----",mWifiList.get(i).SSID);
                ssids.add(mWifiList.get(i).SSID);
                wifiHelper.connect(wifiConfiguration, false);
                break;
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isy=0;
        Log.e("--------->自动重连----","服务结束");
        try {
            wifiHelper.unRegistBroadcast();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private class NyThread extends Thread{
        @Override
        public void run() {
            super.run();

            while (islianjie==0){
                ConnectivityManager connManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWifi.isConnected()) {
                    islianjie=1;
                    break;
                }else {
                    contengWifi();
                }

                try {
                    sleep(12000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            stopSelf();

        }
    }
}
