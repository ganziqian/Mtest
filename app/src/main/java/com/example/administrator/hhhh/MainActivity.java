package com.example.administrator.hhhh;

        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;
        import android.util.Log;
        import android.widget.RelativeLayout;

        import com.zwg.wifilib.fragment.WifiRootView;
        import com.zwg.wifilib.myinterface.WifiConnetSuccesCallBack;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);


        RelativeLayout layout= (RelativeLayout) findViewById(R.id.activity_main);

        WifiRootView wifiRootView=new WifiRootView(this);


        wifiRootView.setWifiViewCallBack(new WifiConnetSuccesCallBack() {//设置联网成功回调
            @Override
            public void connetSuccess() {
                Log.e("--------","9917781");
            }
        });

        wifiRootView.registerWifi();   //注册开启wifi
      //  wifiRootView.unRegister();  //取消监听


        layout.addView(wifiRootView);

     /*   Intent intent=new Intent();
        // 0 代表逻辑数学  1 艺术创想   2情景英语  3阶梯阅读  4安全礼仪
        intent.putExtra("teachingType",4);
        intent.setAction("com.cnst.wisdomforparents");
        intent.setDataAndType(Uri.parse("teaching.garden:asdwd"), "vnd.android.cursor.item/teaching.garden");
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        startActivity(intent);*/

    }
}
