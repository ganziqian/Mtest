package com.zwg.wifilib.adapter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.zwg.wifilib.R;
import com.zwg.wifilib.WifiHelper;
import com.zwg.wifilib.bean.MyWifiBean;

import java.util.List;

/**
 * Created by Administrator on 2016/8/13.
 */
public class WifiItemAdapter extends BaseAdapter{
    private List<MyWifiBean>  list;
    private Context context;
    public String MyBSSID="";
    public int sta=0;

    public WifiItemAdapter(Context context, List<MyWifiBean> list){
        this.list=list;
        this.context=context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder vh=null;
        if(convertView==null){
            convertView= LayoutInflater.from(context).inflate(R.layout.wifi_item_layout,null);
            vh=new ViewHolder(convertView);
            convertView.setTag(vh);
        }else {
            vh= (ViewHolder) convertView.getTag();
        }


        vh.nameTv.setText(list.get(position).getScanResult().SSID);
       // vh.stateTv.setText(list.get(position).);
        int level = list.get(position).getScanResult().level;
        //根据获得的信号强度发送信息
        if (level <= 0 && level >= -50) {
            vh.levTv.setText("信号强");
            vh.iv.setImageResource(R.drawable.wifi_level_4);
        } else if (level < -50 && level >= -70) {
            vh.levTv.setText("信号较强");
            vh.iv.setImageResource(R.drawable.wifi_level_3);
        } else if (level < -70 && level >= -80) {
            vh.levTv.setText("信号一般");
            vh.iv.setImageResource(R.drawable.wifi_level_2);
        } else if (level < -80 && level >= -100) {
            vh.levTv.setText("信号差");
            vh.iv.setImageResource(R.drawable.wifi_level_1);
        } else {
            vh.levTv.setText("无信号");
            vh.iv.setImageResource(R.drawable.wifi_level_0);
        }
/*
         if (list.get(position).getState() != null ) {
             if( list.get(position).getState().equals("已连接")){
                 vh.stateTv.setText("已连接");
             }
        }*/
        vh.lokiv.setVisibility(View.VISIBLE);
        if(MyBSSID.equals(list.get(position).getScanResult().BSSID)){
            if(sta==0) {
                vh.stateTv.setText("正在连接...");
            }else if(sta==1){
                vh.stateTv.setText("连接超时");
            }else if(sta==6){
                vh.stateTv.setText("已连接");
            }else if(sta==7){
                vh.stateTv.setText("密码验证错误");
            }else if(sta==3){
                vh.stateTv.setText("连接失败");
            }
        }else {
            if (WifiHelper.getInstance(context).isConnected2(list.get(position).getScanResult().BSSID)) {
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if (mWifi.isConnected()) {
                    vh.stateTv.setText("已连接");
                } /*else {
                    vh.stateTv.setText("已保存，" + "通过WPA/WPA2进行保护");
                }*/
            }  else {
                if (list.get(position).isSave()) {
                    if (list.get(position).getScanResult().capabilities.contains("WPA") && list.get(position).getScanResult().capabilities.contains("WPA2")) {
                        vh.stateTv.setText("已保存，" + "通过WPA/WPA2进行保护");
                    } else if (list.get(position).getScanResult().capabilities.contains("WPA")) {
                        vh.stateTv.setText("已保存，" + "通过WPA进行保护");
                    } else if (list.get(position).getScanResult().capabilities.contains("WPA2")) {
                        vh.stateTv.setText("已保存，" + "通过WPA2进行保护");
                    } else if(list.get(position).getScanResult().capabilities.contains("WEP")){
                        vh.stateTv.setText("已保存，" + "通过WEP进行保护");
                    }else {
                        vh.stateTv.setText("已保存");
                        vh.lokiv.setVisibility(View.GONE);
                    }
                } else {
                    if (list.get(position).getScanResult().capabilities.contains("WPA") && list.get(position).getScanResult().capabilities.contains("WPA2")) {
                        vh.stateTv.setText("通过WPA/WPA2进行保护");
                    } else if (list.get(position).getScanResult().capabilities.contains("WPA")) {
                        vh.stateTv.setText("通过WPA进行保护");
                    } else if (list.get(position).getScanResult().capabilities.contains("WPA2")) {
                        vh.stateTv.setText("通过WPA2进行保护");
                    } else if(list.get(position).getScanResult().capabilities.contains("WEP")){
                        vh.stateTv.setText("通过WEP进行保护");
                    }else if (list.get(position).getScanResult().capabilities.contains("[ESS]")) {
                        vh.stateTv.setText("免费WIFI、可连接");
                        vh.lokiv.setVisibility(View.GONE);
                    } else {
                        vh.stateTv.setText("未连接");
                        vh.lokiv.setVisibility(View.GONE);
                    }
                }
            }
        }

       // vh.stateTv.setText(list.get(position).getScanResult().BSSID);
        return convertView;
    }

    public class ViewHolder{
        private TextView nameTv;
        public TextView stateTv;
        private TextView levTv;
        private ImageView iv;
        private ImageView lokiv;
        private RelativeLayout itemlayout;

        public ViewHolder(View view){
            nameTv= (TextView) view.findViewById(R.id.wifi_name_tv);
            stateTv= (TextView) view.findViewById(R.id.wifi_state_tv);
            levTv= (TextView) view.findViewById(R.id.xinhao_tv);
            iv= (ImageView) view.findViewById(R.id.check_iv);
            lokiv= (ImageView) view.findViewById(R.id.wifiLock_item_iv);
            itemlayout= (RelativeLayout) view.findViewById(R.id.itwm_lauyout);
        }
    }

}
