package com.zwg.wifilib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


import com.zwg.wifilib.R;
import com.zwg.wifilib.WifiHelper;
import com.zwg.wifilib.bean.MyWifiBean;
import com.zwg.wifilib.utils.SaveUtils;

import java.util.List;

/**
 * Created by Administrator on 2016/11/2.
 */
public class OperaDialog extends Dialog implements View.OnClickListener{


    private OperaCallBack operaCallBack;
    private Context context;
    private MyWifiBean myWifiBean;
    public OperaDialog(Context context,MyWifiBean myWifiBean) {
        super(context);
        this.myWifiBean=myWifiBean;
    }

    public OperaDialog(Context context, int themeResId,MyWifiBean myWifiBean,OperaCallBack operaCallBack) {
        super(context, themeResId);
        this.myWifiBean=myWifiBean;
        this.context=context;
        this.operaCallBack=operaCallBack;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_opera_dialog);

        TextView titleTv= (TextView) findViewById(R.id.opera_title_tv);

        LinearLayout layout1= (LinearLayout) findViewById(R.id.opera_layout1);
        LinearLayout layout2= (LinearLayout) findViewById(R.id.opera_layout2);

        if(myWifiBean.isContent()){
            layout1.setVisibility(View.GONE);
        }else {
            layout2.setVisibility(View.GONE);
        }

        TextView lianjieTv= (TextView) findViewById(R.id.opera_lianjie_tv);
        TextView calSaveTv= (TextView) findViewById(R.id.opera_calsave_tv);
        TextView quxiao2= (TextView) findViewById(R.id.opera_quxiao2_tv);
        quxiao2.setOnClickListener(this);

        TextView hulieTv= (TextView) findViewById(R.id.opera_hulie2_tv);
        hulieTv.setOnClickListener(this);

        Button quxiaoBtn= (Button) findViewById(R.id.opera_quxiao_btn);
        Button duankai= (Button) findViewById(R.id.opera_duankai_btn);

        lianjieTv.setOnClickListener(this);
        calSaveTv.setOnClickListener(this);
        quxiaoBtn.setOnClickListener(this);
        duankai.setOnClickListener(this);



        TextView levelTv= (TextView) findViewById(R.id.opera_level_tv);
        TextView anquanTv= (TextView) findViewById(R.id.opera_anquan_tv);

        int level =myWifiBean.getScanResult().level;
        //根据获得的信号强度发送信息
        if (level <= 0 && level >= -50) {
            levelTv.setText("信号强");
        } else if (level < -50 && level >= -70) {
            levelTv.setText("信号较强");
        } else if (level < -70 && level >= -80) {
            levelTv.setText("信号一般");
        } else if (level < -80 && level >= -100) {
            levelTv.setText("信号差");
        } else {
            levelTv.setText("无信号");
        }


        if (myWifiBean.getScanResult().capabilities.contains("WPA") && myWifiBean.getScanResult().capabilities.contains("WPA2")) {
            anquanTv.setText("通过WPA/WPA2进行保护");
        } else if (myWifiBean.getScanResult().capabilities.contains("WPA")) {
            anquanTv.setText("通过WPA进行保护");
        } else if (myWifiBean.getScanResult().capabilities.contains("WPA2")) {
            anquanTv.setText( "通过WPA2进行保护");
        } else {
            anquanTv.setText("未知");
        }

        titleTv.setText(myWifiBean.getScanResult().SSID);


    }

    @Override
    public void onClick(View v) {
        int id=v.getId();


        if(id==R.id.opera_calsave_tv){//取消保存
            WifiHelper.getInstance(context).getWifiManager().removeNetwork(myWifiBean.getWifiConfiguration().networkId);
            WifiHelper.getInstance(context).getWifiManager().saveConfiguration();
            dismiss();
        }else if(id==R.id.opera_lianjie_tv){//连接
            List<ScanResult> scanResults=WifiHelper.getInstance(context).getWifiManager().getScanResults();
            for (int i = 0; i < scanResults.size(); i++) {
                if (WifiHelper.getInstance(context).isConnected2(scanResults.get(i).BSSID)) {
                    WifiConfiguration wifiConfiguration = WifiHelper.getInstance(context).getConfiguredNetwork(scanResults.get(i).SSID);
                    WifiHelper.getInstance(context).getWifiManager().disableNetwork(wifiConfiguration.networkId);
                    WifiHelper.getInstance(context).getWifiManager().disconnect();
                }
            }
            WifiHelper.getInstance(getContext()).connect(myWifiBean.getScanResult(), false);
            operaCallBack.opera(myWifiBean.getScanResult().BSSID,"1");
            dismiss();
        }else if(id==R.id.opera_quxiao_btn){//取消
            dismiss();
        }else if(id==R.id.opera_duankai_btn){//断开
            SaveUtils.saveInfo(context,"bu");//保存标记，限制自动重连
            WifiHelper.getInstance(context).getWifiManager().disableNetwork(myWifiBean.getWifiConfiguration().networkId);
            WifiHelper.getInstance(context).getWifiManager().disconnect();
            dismiss();
        }else if(id==R.id.opera_quxiao2_tv){
            dismiss();
        }else if(id==R.id.opera_hulie2_tv){
            try {
                WifiHelper.getInstance(context).getWifiManager().removeNetwork(myWifiBean.getWifiConfiguration().networkId);
                WifiHelper.getInstance(context).getWifiManager().saveConfiguration();
                dismiss();
            }catch (Exception e){
                e.printStackTrace();
            }

        }

    }

    public interface OperaCallBack{
        abstract void opera(String ssid, String type);
    }
}
