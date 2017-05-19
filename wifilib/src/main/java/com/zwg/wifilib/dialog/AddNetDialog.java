package com.zwg.wifilib.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.zwg.wifilib.R;
import com.zwg.wifilib.myinterface.AddNetCallBack;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/10/31.
 */
public class AddNetDialog extends Dialog {

    private Context context;
    private AddNetCallBack addNetCallBack;
    private ArrayList<String> list = new ArrayList<String>();

    private RelativeLayout layout;

    private int type=1;
    private EditText nameEd,pswEd;
    public AddNetDialog(Context context, int themeResId,AddNetCallBack addNetCallBack) {
        super(context, themeResId);
        this.context=context;
       this.addNetCallBack=addNetCallBack;
    }
    public AddNetDialog(Context context) {
        super(context);
    }



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.addnet_dialog);

        nameEd= (EditText) findViewById(R.id.add_wifi_name_ed);
        pswEd= (EditText) findViewById(R.id.add_wifi_psw_ed);

        layout= (RelativeLayout) findViewById(R.id.addnet_ps_layout);
        CheckBox checkBox= (CheckBox) findViewById(R.id.add_wifi_pws_checbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    pswEd.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }else {
                    pswEd.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });
        layout.setVisibility(View.GONE);
        Spinner spinner= (Spinner) findViewById(R.id.add_net_wifistate_spiner);



        list.add("无");
       // list.add("WEP");
        list.add("WPA PSK/TKIP");
        list.add("WPA2 PSK/TKIP");

        /*
         * 第二个参数是显示的布局
         * 第三个参数是在布局显示的位置id
         * 第四个参数是将要显示的数据
         */
        ArrayAdapter adapter2 = new ArrayAdapter(context, R.layout.wifi_spinner_item_layout, R.id.spinner_txt,list);
        //adapter2.setDropDownViewResource(android.R.layout.simple_list_item_1);
        spinner.setAdapter(adapter2);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if(list.get(position).equals("无")){
                    layout.setVisibility(View.GONE);
                    type=1;
                }else if(list.get(position).equals("WEP")){
                    layout.setVisibility(View.VISIBLE);
                    type=2;
                }else if(list.get(position).equals("WPA2 PSK/TKIP")){
                    layout.setVisibility(View.VISIBLE);
                    type=3;
                }else if(list.get(position).equals("WPA PSK/TKIP")){
                    layout.setVisibility(View.VISIBLE);
                    type=4;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button btn= (Button) findViewById(R.id.addnet_queding_btn);
        Button quxiaoBtn= (Button) findViewById(R.id.addnet_quxiao_btn);

        quxiaoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ssidname=nameEd.getText().toString();
                String psw=pswEd.getText().toString();
                if(ssidname.equals("")){
                    Toast.makeText(context,"请输入网络名称（SSID）",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(type==1){
                    addNetCallBack.inputBack(ssidname,"", WifiOperator.WifiCipherType.NONE);
                }else if(type==2){

                    if(psw.equals("")){
                        Toast.makeText(context,"请输入密码",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addNetCallBack.inputBack(ssidname,psw, WifiOperator.WifiCipherType.WEP);

                }else if(type==3){
                    if(psw.equals("")){
                        Toast.makeText(context,"请输入密码",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addNetCallBack.inputBack(ssidname,psw, WifiOperator.WifiCipherType.WPA2);
                }else if(type==4){
                    if(psw.equals("")){
                        Toast.makeText(context,"请输入密码",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    addNetCallBack.inputBack(ssidname,psw, WifiOperator.WifiCipherType.WPA);
                }

                dismiss();
            }
        });




    }

    @Override
    protected void onStop() {
        super.onStop();

    }
}
