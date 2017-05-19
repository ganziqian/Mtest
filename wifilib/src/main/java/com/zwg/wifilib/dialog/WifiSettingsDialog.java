package com.zwg.wifilib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.zwg.wifilib.R;
import com.zwg.wifilib.WifiHelper;
import com.zwg.wifilib.bean.MyWifiBean;


/**
 * Created by Administrator on 2016/10/31.
 */
public class WifiSettingsDialog extends Dialog {

    private MyWifiBean myWifiBean;
    private PswCallBack pswCallBack;
    private Context context;
    public WifiSettingsDialog(Context context, int themeResId, MyWifiBean myWifiBean, PswCallBack pswCallBack) {
        super(context, themeResId);
        this.context=context;
       this.pswCallBack=pswCallBack;
        this.myWifiBean=myWifiBean;
    }
    public WifiSettingsDialog(Context context) {
        super(context);
    }

    public interface PswCallBack{
        abstract void inputBack(String psw);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_input_dialog);
        final EditText ed= (EditText) findViewById(R.id.wifi_psw_ed);
        TextView quedingTv= (TextView) findViewById(R.id.queding_btn);
        TextView quxiaoBtn= (TextView) findViewById(R.id.haha_tttv3);
        CheckBox checkBox= (CheckBox) findViewById(R.id.in_wifi_pws_checbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    ed.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    ed.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        quxiaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        quedingTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ed.getText().toString().toString().length()>2) {
                    pswCallBack.inputBack(myWifiBean.getScanResult().BSSID);
                    boolean iscon = WifiHelper.getInstance(context).connect(myWifiBean.getScanResult(), ed.getText().toString());
                    dismiss();
                }else {
                    Toast.makeText(context,"请输入完整的密码",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
