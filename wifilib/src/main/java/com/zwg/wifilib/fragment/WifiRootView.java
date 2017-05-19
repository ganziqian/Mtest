package com.zwg.wifilib.fragment;


import android.content.Context;
import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zwg.wifilib.R;
import com.zwg.wifilib.WifiHelper;
import com.zwg.wifilib.adapter.WifiItemAdapter;
import com.zwg.wifilib.bean.MyWifiBean;
import com.zwg.wifilib.dialog.AddNetDialog;
import com.zwg.wifilib.dialog.OperaDialog;
import com.zwg.wifilib.dialog.WifiSettingsDialog;
import com.zwg.wifilib.myinterface.AddNetCallBack;
import com.zwg.wifilib.myinterface.WifiConnetSuccesCallBack;
import com.zwg.wifilib.utils.SaveUtils;
import com.zwg.wifilib.utils.ViewBaseAction;
import com.zwg.wifilib.utils.WifiOperator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 */
public class WifiRootView extends RelativeLayout implements AdapterView.OnItemClickListener,DialogInterface.OnDismissListener,AdapterView.OnItemLongClickListener,View.OnClickListener,ViewBaseAction{

    private List<MyWifiBean> myWifiBeens = new ArrayList<>();
    private WifiItemAdapter adapter;

    private WifiHelper wifiHelper;
    private boolean flag = false;
    private TextView wifiSearchButton;

    WifiItemAdapter.ViewHolder vh;
    private TextView saoTv;
    private ProgressBar progressBar;

    private boolean isfresh=true;

    private int count=0;//线程计时标记

    private Context context;
    private ListView wifiListView=null;
    private ImageView wifiStateButton=null;
    private WifiHelper.WifiHelperListener mWifiHelperListener;

    private WifiConnetSuccesCallBack wifiViewCallBack;

    public WifiConnetSuccesCallBack getWifiViewCallBack() {
        return wifiViewCallBack;
    }

    public void setWifiViewCallBack(WifiConnetSuccesCallBack wifiViewCallBack) {
        this.wifiViewCallBack = wifiViewCallBack;
    }

    public WifiRootView(Context context) {
        super(context);
        this.context=context;
        init();
    }



    public WifiRootView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
       // init();
    }

    public WifiRootView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
    //    init();
    }


    private void init() {
        LayoutInflater.from(context).inflate(R.layout.fragment_wifi,this, true);
        wifiHelper = WifiHelper.getInstance(context);
        initConfig();
        SaveUtils.saveInfo(context,"66");

        initUI2();
    }




    private void initUI2(){

        initConparto();


        wifiListView=(ListView)findViewById(R.id.wifi_listview);

        adapter = new WifiItemAdapter(context, myWifiBeens);
        wifiListView.setAdapter(adapter);
        wifiListView.setOnItemClickListener(this);
        wifiListView.setOnItemLongClickListener(this);
        progressBar= (ProgressBar) findViewById(R.id.pross);

        Button addNetBtn= (Button) findViewById(R.id.add_wifi_net);
        addNetBtn.setOnClickListener(this);

        wifiSearchButton = (TextView) findViewById(R.id.wifi_fresh_tv);

        wifiSearchButton.setOnClickListener(this);


        // wifiHelper.getScanner().start();

        saoTv = (TextView) findViewById(R.id.head_TextView_WifiConnecion);
        saoTv.setText("正在搜索附近wifi...");



        wifiStateButton=(ImageView)findViewById(R.id.wifiState_Button_WifiConnection);


       flag=wifiHelper.getWifiManager().isWifiEnabled();

        if(!flag){
            wifiHelper.openWifi();
            flag=true;
        }

        if(!flag){
            saoTv.setText("wifi已关闭");
            progressBar.setVisibility(GONE);
        }

        setWifiButtonState(flag);


       // getList(1);
        wifiStateButton.setOnClickListener(this);
    }


    private void initConfig(){
        mWifiHelperListener=new WifiHelper.WifiHelperListener() {

            @Override
            public void onWifiState(WifiHelper.WifiState wifiState,String SSID) {
                try {
                    if (wifiState .equals(WifiHelper.WifiState.WIFI_STATE_CONNECTED)) {
                        adapter.sta=6;
                        count =30;
                        adapter.notifyDataSetChanged();
                        getList(1);
                        if(wifiViewCallBack!=null){
                            wifiViewCallBack.connetSuccess();
                        }
                    } else if (wifiState == WifiHelper.WifiState.WIFI_STATE_CONNECT_FAILED) {
                        adapter.sta = 3;
                        count = 20;
                        isfresh = true;
                        changeState(SSID.replaceAll("\"", ""));
                        adapter.notifyDataSetChanged();
                    }
                    Log.e("-----zzz----", SSID + "-" + wifiState.toString());
                } catch (Exception e) {}
            }

            @Override
            public void onSupplicantState(SupplicantState SupplicantState1, int error) {
                try {
                    if (SupplicantState1 != null) {
                        if(error== WifiManager.ERROR_AUTHENTICATING){

                            count =30;
                            adapter.sta=7;
                            adapter.notifyDataSetChanged();
                            getList(1);
                        }

                        if (SupplicantState1.toString() .equals("COMPLETED")) {
                            adapter.sta=6;
                            count =30;
                            adapter.notifyDataSetChanged();
                            getList(1);
                            if(wifiViewCallBack!=null){
                                wifiViewCallBack.connetSuccess();
                            }
                        }
                        // Log.e("--ww---onSupplicantState----", SupplicantState + "-" + error);
                    }
                } catch (Exception e) {}
            }

            @Override
            public void onScanSuccess(List<ScanResult> wifiList) {
                try {
                    if (!isfresh) {
                        return;
                    }


                    saoTv.setText("选取WIFI");
                    progressBar.setVisibility(View.GONE);
                    myWifiBeens.clear();
                    getList(0);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };



    }


    private void setWifiButtonState(boolean state){


        if(state){
            wifiStateButton.setImageResource(R.drawable.wifi_on);
        }else{
            wifiStateButton.setImageResource(R.drawable.wifi_off);
        }
    }

    private Handler myHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            boolean isshua=false;

            List<ScanResult> mWifiList = wifiHelper.getWifiManager().getScanResults();
            for (int i = 0; i <mWifiList.size(); i++) {

                if (wifiHelper.isConnected2(mWifiList.get(i).BSSID)) {
                    ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                    if (mWifi.isConnected()) {
                        isshua = true;
                        getList(1);
                        break;
                    }
                }
            }

            if(!isshua){
                adapter.sta=1;
            }
            isfresh=true;
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void hide() {

    }

    @Override
    public void show() {

    }


    private class MyThread extends  Thread{
        @Override
        public void run() {
            super.run();

            while (count<13){
                ++count;
                Log.e("=====th==",count+"");
                try {
                    sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(count==13){
                    myHandler.sendEmptyMessage(1);
                }
            }
        }
    }



    //注册wifi监听
    public void registerWifi(){
        wifiHelper.registBroadcast(mWifiHelperListener);
        wifiHelper.getScanner().start();
    }

    public void unRegister(){
        if(SaveUtils.getIsLian(context).equals("bu")){//判断是否是手动断开标记

        }else{
            SaveUtils.deletInfo(context);
        }

        wifiHelper.getScanner().stop();
        try{
            wifiHelper.unRegistBroadcast();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        if(!adapter.MyBSSID.equals("")){
            isfresh=false;
            count=0;
            adapter.sta=0;
            new MyThread().start();
        }
        getList(1);
    }

    @Override
    public void onClick(View v) {
        int aid = v.getId();


        if(aid==R.id.wifiState_Button_WifiConnection){
            myWifiBeens.clear();
            adapter.notifyDataSetChanged();

            setWifiButtonState(!flag);
            wifiStateButton.setClickable(false);
            if (flag == false) {
                flag = true;
                wifiHelper.openWifi();
                saoTv.setText("正在搜索附近wifi...");
                progressBar.setVisibility(View.VISIBLE);

                adapter.MyBSSID="";
                adapter.sta=0;
                isfresh=true;
                new MyAsynTask().execute();
                wifiSearchButton.setVisibility(View.VISIBLE);
            } else {
                saoTv.setText("wifi已关闭");
                flag = false;
                wifiSearchButton.setVisibility(View.GONE);
                wifiHelper.closeWifi();
                new Thread(){
                    @Override
                    public void run() {
                        super.run();
                        try {
                            sleep(500);
                            btnHander.sendEmptyMessage(1);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
        }else if(aid==R.id.wifi_fresh_tv){
            saoTv.setText("正在刷新...");
            progressBar.setVisibility(View.VISIBLE);

            adapter.MyBSSID="";
            adapter.sta=0;
            isfresh=true;

            new MyAsynTask().execute();
        }else if(aid==R.id.add_wifi_net) {
            AddNetDialog addNetDialog = new AddNetDialog(context, R.style.NobackDialog, new AddNetCallBack() {


                @Override
                public void inputBack(String ssid, String psw, com.zwg.wifilib.dialog.WifiOperator.WifiCipherType inType) {
                    for (int i = 0; i < myWifiBeens.size(); i++) {
                        myWifiBeens.get(i).setStattag(0);
                        if (myWifiBeens.get(i).isContent()) {
                            if (myWifiBeens.get(i).getWifiConfiguration() != null) {
                                wifiHelper.getWifiManager().disableNetwork(myWifiBeens.get(i).getWifiConfiguration().networkId);
                                wifiHelper.getWifiManager().disconnect();
                                myWifiBeens.get(i).setContent(false);
                                myWifiBeens.get(i).setState(" 已断开");
                                break;
                            }
                        }
                    }
                    WifiOperator.setContext(context);
                    WifiOperator.getInstance().addNetWorkAndConnect(ssid, psw, inType);
                    new MyAsynTask().execute();

                }
            }) {

            };
            addNetDialog.show();




        }

    }

    private Handler btnHander=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int wat=msg.what;
            switch (wat){
                case 1:
                    if(wifiStateButton!=null) {
                        wifiStateButton.setClickable(true);
                    }
                    break;
                case 2:
                    break;
            }
        }
    };



    @Override
    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
        SaveUtils.saveInfo(context,"66");
        Log.e("=======dian","1");
        adapter.MyBSSID="";
        vh = (WifiItemAdapter.ViewHolder) view.getTag();
        if (myWifiBeens.get(position).isContent()) {
            OperaDialog operaDialog = new OperaDialog(context, R.style.NobackDialog, myWifiBeens.get(position),null);
            operaDialog.show();
            operaDialog.setOnDismissListener(this);
            //已经连接
        } else {
            if (myWifiBeens.get(position).isSave()) {


                if(myWifiBeens.get(position).getWifiConfiguration()!=null){


                    if(myWifiBeens.get(position).getWifiConfiguration().BSSID==null) {
                        //未保存未输密码连接
                        MyWifiBean myWifiBeandia = myWifiBeens.get(position);
                        WifiSettingsDialog dialog = new WifiSettingsDialog(context, R.style.NobackDialog, myWifiBeandia, new WifiSettingsDialog.PswCallBack() {
                            @Override
                            public void inputBack(String psw) {
                                for (int i = 0; i < myWifiBeens.size(); i++) {
                                    myWifiBeens.get(i).setStattag(0);
                                    if (myWifiBeens.get(i).isContent()) {
                                        if (myWifiBeens.get(i).getWifiConfiguration() != null) {
                                            wifiHelper.getWifiManager().disableNetwork(myWifiBeens.get(i).getWifiConfiguration().networkId);
                                            wifiHelper.getWifiManager().disconnect();
                                            myWifiBeens.get(i).setContent(false);
                                            myWifiBeens.get(i).setState(" 已断开");
                                        }
                                    }
                                }
                                adapter.MyBSSID = psw;
                            }
                        });
                        dialog.setOnDismissListener(this);
                        dialog.show();
                        return;
                    }
                }


                for (int i = 0; i < myWifiBeens.size(); i++) {
                    myWifiBeens.get(i).setStattag(0);
                    if (myWifiBeens.get(i).isContent()) {
                        if (myWifiBeens.get(i).getWifiConfiguration() != null) {
                            wifiHelper.getWifiManager().disableNetwork(myWifiBeens.get(i).getWifiConfiguration().networkId);
                            wifiHelper.getWifiManager().disconnect();
                            myWifiBeens.get(i).setContent(false);
                            myWifiBeens.get(i).setState(" 已断开");
                        }
                    }
                }
                adapter.MyBSSID=myWifiBeens.get(position).getScanResult().BSSID;
                //  vh.stateTv.setText("正在连接");
                isfresh=false;
                adapter.sta=0;
                count=0;
                myWifiBeens.get(position).setStattag(2);


                //  wifiHelper.connectWifiByReflectMethod(myWifiBeens.get(position).getWifiConfiguration().networkId);
                //已经保存未连接
                if(myWifiBeens.get(position).getWifiConfiguration()!=null) {
                    wifiHelper.connect(myWifiBeens.get(position).getWifiConfiguration(), false);
                }
                Collections.sort(myWifiBeens,comparator);
                adapter.notifyDataSetChanged();
                new MyThread().start();
            } else {
                String capabilities = myWifiBeens.get(position).getScanResult().capabilities.trim();
                if (capabilities != null && (capabilities.equals("") || capabilities.equals("[ESS]"))) {//没密码连接
                    for (int i = 0; i < myWifiBeens.size(); i++) {
                        myWifiBeens.get(i).setStattag(0);
                        if (myWifiBeens.get(i).isContent()) {
                            if (myWifiBeens.get(i).getWifiConfiguration() != null) {
                                wifiHelper.getWifiManager().disableNetwork(myWifiBeens.get(i).getWifiConfiguration().networkId);
                                wifiHelper.getWifiManager().disconnect();
                                myWifiBeens.get(i).setContent(false);
                                myWifiBeens.get(i).setState(" 已断开");
                            }
                        }
                    }
                    WifiConfiguration config = new WifiConfiguration();
                    config.allowedAuthAlgorithms.clear();
                    config.allowedGroupCiphers.clear();
                    config.allowedKeyManagement.clear();
                    config.allowedPairwiseCiphers.clear();
                    config.allowedProtocols.clear();
                    config.SSID = "\"" + myWifiBeens.get(position).getScanResult().SSID + "\"";
                    // 没有密码
                    //  config.wepKeys[0] = "";
                    config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                    //     config.wepTxKeyIndex = 0;

                    WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    wifiManager.enableNetwork(wifiManager.addNetwork(config), true);

                    adapter.MyBSSID=myWifiBeens.get(position).getScanResult().BSSID;
                    // vh.stateTv.setText("正在连接");
                    isfresh=false;
                    adapter.sta=0;
                    count=0;

                    myWifiBeens.get(position).setStattag(2);

                    Collections.sort(myWifiBeens,comparator);
                    adapter.notifyDataSetChanged();
                    new MyThread().start();

                }else {
                    //未保存未输密码连接
                    MyWifiBean myWifiBeandia = myWifiBeens.get(position);
                    WifiSettingsDialog dialog = new WifiSettingsDialog(context, R.style.NobackDialog, myWifiBeandia, new WifiSettingsDialog.PswCallBack() {
                        @Override
                        public void inputBack(String psw) {
                            for (int i = 0; i < myWifiBeens.size(); i++) {
                                myWifiBeens.get(i).setStattag(0);
                                if (myWifiBeens.get(i).isContent()) {
                                    if (myWifiBeens.get(i).getWifiConfiguration() != null) {
                                        wifiHelper.getWifiManager().disableNetwork(myWifiBeens.get(i).getWifiConfiguration().networkId);
                                        wifiHelper.getWifiManager().disconnect();
                                        myWifiBeens.get(i).setContent(false);
                                        myWifiBeens.get(i).setState(" 已断开");
                                    }
                                }
                            }
                            adapter.MyBSSID=psw;
                        }
                    });
                    dialog.setOnDismissListener(this);
                    dialog.show();
                }
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        SaveUtils.saveInfo(context,"66");
        MyWifiBean myWifiBean=null;
        adapter.MyBSSID="";
        try {
            myWifiBean=myWifiBeens.get(position);

        } catch (Exception e) {
            e.printStackTrace();
        }

        if(myWifiBean!=null){
            if(myWifiBean.isContent()) {
                OperaDialog operaDialog = new OperaDialog(context, R.style.NobackDialog, myWifiBean,null);
                operaDialog.show();
                operaDialog.setOnDismissListener(this);
            }else {
                if(myWifiBean.isSave()){
                    OperaDialog operaDialog = new OperaDialog(context, R.style.NobackDialog, myWifiBean, new OperaDialog.OperaCallBack() {
                        @Override
                        public void opera(String ssid, String type) {
                            adapter.MyBSSID=ssid;
                        }
                    });
                    operaDialog.show();
                    operaDialog.setOnDismissListener(this);
                }else {
                    WifiSettingsDialog dialog = new WifiSettingsDialog(context, R.style.NobackDialog, myWifiBean, new WifiSettingsDialog.PswCallBack() {
                        @Override
                        public void inputBack(String psw) {
                            adapter.MyBSSID=psw;
                        }
                    });
                    dialog.show();
                }
            }
        }
        return true;
    }



    private class MyAsynTask extends AsyncTask<String ,String,String> {

        @Override
        protected String doInBackground(String... params) {

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            saoTv.setText("选取WIFI");
            progressBar.setVisibility(View.GONE);
            getList(1);
        }
    }

    private void getList(int ttyy){
        btnHander.sendEmptyMessage(1);
        if(!flag){
            return;
        }

        myWifiBeens.clear();
        //adapter.notifyDataSetChanged();

        if(ttyy==1) {
            wifiHelper.getWifiManager().startScan();
        }
        saoTv.setText("选取WIFI");
        progressBar.setVisibility(View.GONE);
        List<ScanResult> mWifiList = wifiHelper.getWifiManager().getScanResults();
        for (int i = 0; i <mWifiList.size(); i++) {
            MyWifiBean myWifiBean = new MyWifiBean();
            myWifiBean.setScanResult(mWifiList.get(i));
            if (wifiHelper.isConnected2(mWifiList.get(i).BSSID)) {
                ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
                if(mWifi.isConnected()) {
                    myWifiBean.setContent(true);
                    myWifiBean.setState("已连接");
                    myWifiBean.setStattag(1);

                    adapter.MyBSSID = "";
                    adapter.sta = 0;
                    count = 20;
                    isfresh = true;
                    SaveUtils.deletInfo(context);
                }else {
                    myWifiBean.setContent(false);
                    myWifiBean.setState("");
                    myWifiBean.setStattag(0);
                }
            } else {
                myWifiBean.setContent(false);
                myWifiBean.setState("");
                myWifiBean.setStattag(0);
            }

            if(adapter.MyBSSID.equals(mWifiList.get(i).BSSID)){
                myWifiBean.setStattag(2);
            }

            WifiConfiguration wifiConfiguration = wifiHelper.getConfiguredNetwork(mWifiList.get(i).BSSID);

            if(wifiConfiguration==null){
                wifiConfiguration=wifiHelper.getConfiguredNetworkSSID(mWifiList.get(i).SSID);
            }

            if (wifiConfiguration != null) {
                myWifiBean.setWifiConfiguration(wifiConfiguration);
                myWifiBean.setSave(true);
                //  Log.e(mWifiList.get(i).SSID, "======" + wifiConfiguration.toString());
            } else {
                myWifiBean.setSave(false);
            }
            myWifiBean.setLeve(mWifiList.get(i).level);

            // Log.e("--------->",mWifiList.get(i).toString());
            if(mWifiList.get(i).SSID!=null){
                if(!mWifiList.get(i).SSID.equals("")){
                    myWifiBeens.add(myWifiBean);
                }
            }


        }
        Collections.sort(myWifiBeens,comparator);
        adapter.notifyDataSetChanged();
    }
    Comparator<MyWifiBean> comparator;
    private void initConparto(){
        comparator = new Comparator<MyWifiBean>() {
            public int compare(MyWifiBean s1, MyWifiBean s2) {
                // 先排年龄
                if (s1.getStattag() != s2.getStattag()) {
                    return s2.getStattag() - s1.getStattag();
                }else {
                    return s2.getLeve()-s1.getLeve();
                }
            }
        };
    }


    private void changeState(String ssid){
        for (int i=0;i<myWifiBeens.size();i++){
            if(myWifiBeens.get(i).getScanResult().SSID.equals(ssid)){
                myWifiBeens.get(i).setState("已连接");
            }
        }
        adapter.notifyDataSetChanged();

    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int childCount = getChildCount();
        for(int i = 0 ; i < childCount ; i ++){
            View children = getChildAt(i);
            measureChild(children,widthMeasureSpec,heightMeasureSpec);
        }
    }
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int childCount = getChildCount();
        int preHeight = 0;
        for(int i = 0 ; i < childCount ; i ++){
            View children = getChildAt(i);
            int cHeight = children.getMeasuredHeight();
            if(children.getVisibility() != View.GONE){
                children.layout(l, preHeight, r,preHeight += cHeight);
            }
        }
    }












}
