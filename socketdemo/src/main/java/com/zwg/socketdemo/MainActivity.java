package com.zwg.socketdemo;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.im.client.CallbackType;
import com.im.client.ClientCallbackImpl;
import com.im.client.LocationType;
import com.im.client.callback.ClientCallback;
import com.im.client.callback.DefaultClientCallback;
import com.im.client.compoment.ConnectionParameter;
import com.im.client.core.ChatManager;
import com.im.client.core.IMClient;
import com.im.client.struct.IMMessage;
import com.im.client.struct.IMMessageProtos;
import com.im.client.util.UUID;
import com.wsh.aidl.ActionCMDAidlConnect;

import org.json.JSONException;
import org.json.JSONObject;

import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.nio.NioEventLoopGroup;

public class MainActivity extends AppCompatActivity implements View.OnTouchListener,View.OnLongClickListener{
    private static final String TAG = "TAG";
    public static int MSG_REC = 0xabc;
    private static Context context;
    private EditText ed;
    private TextView tv;
    private String aa;
    ActionCMDAidlConnect actionCMDAidlConnect;
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_REC) {
                Toast.makeText(context, msg.obj.toString(), Toast.LENGTH_LONG).show();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;
      //  connected();

         aa=getIntent().getStringExtra("aa");


        new Thread(){
            @Override
            public void run() {
                super.run();
                dd();
            }
        }.start();





        tv = (TextView) findViewById(R.id.tttvvv);
        Button btn1 = (Button) findViewById(R.id.btn1);
        Button btn2 = (Button) findViewById(R.id.btn2);
        Button btn3 = (Button) findViewById(R.id.btn3);
        Button btn4 = (Button) findViewById(R.id.btn4);

        ed= (EditText) findViewById(R.id.ii);

        btn1.setOnLongClickListener(this);
        btn2.setOnLongClickListener(this);
        btn3.setOnLongClickListener(this);
        btn4.setOnLongClickListener(this);
        btn1.setOnTouchListener(this);
        btn2.setOnTouchListener(this);
        btn3.setOnTouchListener(this);
        btn4.setOnTouchListener(this);

       /* btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
               // sendMessage();
              sen();
            }
        });*/
        registerHardware();
        actionCMDAidlConnect.register(this.getPackageName(), 1);

    }


    public  void dd() {
        try {


            IMClient client = IMClient.getClient();
            ConnectionParameter parameter = ConnectionParameter.getInstance();
            Log.e("aaaaaaa----",aa);
            parameter.setFrom(aa);
            parameter.setPassword("123");

     /*       parameter.setFrom("xjkb.api.login.11406.619C65D1AC0FDD509958B3F87504B28E");
            parameter.setPassword("123456");*/
            parameter.setSocketType(LocationType.ANDROID.value());

            DefaultClientCallback defaultClientCallback = DefaultClientCallback.getInstance();
            //defaultClientCallback.registerCallback(CallbackType.LOGIN_CALLBACK, new ClientCallbackImpl());
            defaultClientCallback.registerCallback(CallbackType.LOGIN_CALLBACK,  new ClientCallback(){

                @Override
                public void process(IMMessage imMessage) {
                    Log.e("=======",imMessage.getBody());
                }

                @Override
                public void resetCount() {
                    Log.e("=======","---------");
                }
            });



          //  defaultClientCallback.registerCallback(CallbackType.PUSH_CALLBACK, new ClientCallbackImpl());
            defaultClientCallback.registerCallback(CallbackType.ANSWER_CALLBACK, new ClientCallbackImpl());
            defaultClientCallback.registerCallback(CallbackType.ERROR_CALLBACK, new ClientCallback(){

                @Override
                public void process(IMMessage imMessage) {
                    Log.e("=======22",imMessage.getBody());

                }

                @Override
                public void resetCount() {
                    Log.e("=======22","---------");
                }
            });




            defaultClientCallback.registerCallback(CallbackType.SINGLE_CHAT_CALLBACK, new ClientCallback() {
                @Override
                public void process(IMMessage imMessage) {

                    String stii[]=imMessage.getBody().split("_");

                    String bhn=stii[0];
                    String hjh=stii[1];
                    if(bhn.equals("1")){
                        actionCMDAidlConnect.jsonCMD(Front(18,hjh));
                      handler2.sendEmptyMessage(1);
                    }else if(bhn.equals("2")){
                        actionCMDAidlConnect.jsonCMD(Front(19,hjh));
                        handler2.sendEmptyMessage(2);
                    }else if(bhn.equals("3")){
                        actionCMDAidlConnect.jsonCMD(Front(20,hjh));
                        handler2.sendEmptyMessage(3);
                    }else if(bhn.equals("4")){
                        actionCMDAidlConnect.jsonCMD(Front(21,hjh));
                        handler2.sendEmptyMessage(4);
                    }else if(bhn.equals("5")){
                        actionCMDAidlConnect.jsonCMD(Front(17,"0"));
                        handler2.sendEmptyMessage(5);
                        Log.e("----","55");
                    }

                    Log.e("=======",imMessage.getBody());
                }

                @Override
                public void resetCount() {

                }
            });

            defaultClientCallback.registerCallback(CallbackType.PUSH_CALLBACK, new ClientCallback() {
                @Override
                public void process(IMMessage imMessage) {


                }

                @Override
                public void resetCount() {

                }
            });




            defaultClientCallback.registerCallback(CallbackType.GROUP_CHAT_CALLBACK, new ClientCallbackImpl());
         // defaultClientCallback.r

            defaultClientCallback.registerCallback(CallbackType.OFFLINE_CALLBACK, new ClientCallbackImpl());
            client.connect();
        } catch (Exception e) {
            Log.e("========","222222222");
            System.out.println("chat....exception,..");
            e.printStackTrace();
        }
    }

    Handler handler2=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            int jk=msg.what;
            switch (jk){



                case 1:
                    launchWithPkgAndCls("com.zwg.newalldemo","com.zwg.newalldemo.MainActivity");
                   // Toast.makeText(MainActivity.this,"起步   走",Toast.LENGTH_SHORT).show();
                    tv.setText("起步   走");
                    break;
                case 2:
                    launchWithPkgAndCls("com.zwg.newalldemo","com.zwg.newalldemo.Main2Activity");
                    //Toast.makeText(MainActivity.this,"向后  退",Toast.LENGTH_SHORT).show();
                    tv.setText("向后  退");
                    break;
                case 3:
                    tv.setText("向右   转");
                   // Toast.makeText(MainActivity.this,"向右   转",Toast.LENGTH_SHORT).show();
                    break;
                case 4:
                    tv.setText("向左 转");
                  //  Toast.makeText(MainActivity.this,"向左 转",Toast.LENGTH_SHORT).show();
                    break;
                case 5:
                    tv.setText("立 定");
                   // Toast.makeText(MainActivity.this,"立 定",Toast.LENGTH_SHORT).show();
                    break;


            }

        }
    };





    /**
     * 根据app包名自动对应的app和Activity
     * context注意要传applicationContext
     */
    public  void launchWithPkgAndCls (String pkg, String cls) {
        try {



            Intent intent=new Intent();
           // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.setComponent(new ComponentName(pkg, cls));
            startActivity(intent);

        }catch (Exception e){
            e.printStackTrace();
            Log.e("=========",e.toString());
        }

    }



    @Override
    public boolean onTouchEvent(MotionEvent event) {


        return super.onTouchEvent(event);
    }

    private void sen(final String hhu){


        new Thread(new Runnable() {
            @Override
            public void run() {
             /*   try (
                        InputStreamReader reader = new InputStreamReader(System.in);
                     BufferedReader in = new BufferedReader(reader)) {
                    TimeUnit.MILLISECONDS.sleep(2000);*/
                //18220686040
                String[] ssa={"10995","10245","12050","10243"};

                for (int i=0;i<ssa.length;i++) {
                    ChatManager manager = ChatManager.getInstance();

                    IMMessageProtos.IMMessage message = null;
                    try {
                        message = manager.buildSingleChatReq(ssa[i], hhu,
                                UUID.next());
                        Log.e("========", "111111");
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        Log.e("========", e1.toString());
                    }
                    if(manager!=null){
                        manager.sendMessage(message);
                    }

                }
/*
                    for (;;) {
                        String line = in.readLine();
                        if (null == line) {
                            continue;
                        }

                        Log.e("========","111111");
                    }*/

            }
        }).start();

    }




















    public void headUp1(){
        actionCMDAidlConnect.controlCMDwithValue("1",100);
    }
    public void headDown() {
        actionCMDAidlConnect.controlCMDwithValue("1",75);
    }
    public void headNormal() {
        actionCMDAidlConnect.controlCMDwithValue("1",90);
    }



    private void registerHardware () {
        actionCMDAidlConnect = new ActionCMDAidlConnect();
        actionCMDAidlConnect.setActionListener(new ActionCMDAidlConnect.ActionListener() {

            @Override
            public void receive(String result) {
                try {
                    JSONObject j_root = new JSONObject(result);
                    int id = j_root.getInt("id");
                    String info = j_root.getString("info");
                    Log.i("cccc", "info is " + info);
                    if (com.shuanghua.utils.tools.SystemUtils.UnitUtils.isInteger(info)) {
                        int state = Integer.parseInt(info);
                        Message message = handler.obtainMessage();
                        message.what = id;
                        message.arg1 = state;
                        handler.sendMessage(message);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void output(String output) {

            }
        });
        actionCMDAidlConnect.init(this);
        //actionCMDAidlConnect.register(getActivity().getPackageName(), 1); //放到onResume中

    }


    public Handler handler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            switch (msg.what) {

                case 40:
                case 41:
                    if (msg.arg1 == 1) {
                        //actionCMDAidlConnect.controlCMDwithValue("1",75);
                        headDown();
                        //  headNormal();
                    }
                    break;
            }


        }

    };




    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if(event.getAction() == MotionEvent.ACTION_DOWN) {
//            if(longClicked) {
//                //快进
//
//            }

        } else if(event.getAction() == MotionEvent.ACTION_UP) {
            String ssh=ed.getText().toString();
            sen("5_" + ssh);
        }

        return false;
    }

    @Override
    public boolean onLongClick(View v) {
        String ssh=ed.getText().toString();
        int id=v.getId();

        switch (id) {
            case R.id.btn1:

                sen("1_" + ssh);
                //actionCMDAidlConnect.syncJsonCMD(Front(18,hh));
                break;
            case R.id.btn2:
                sen("2_" + ssh);
//                actionCMDAidlConnect.syncJsonCMD(Front(19,hh));
                break;
            case R.id.btn3:
                sen("3_" + ssh);
                // actionCMDAidlConnect.syncJsonCMD(Front(20,hh));
                break;
            case R.id.btn4:
                sen("4_" + ssh);
                //actionCMDAidlConnect.syncJsonCMD(Front(21,hh));
                break;

        }


        return false;
    }







    // 前进
    public static final String Front (int aa,String hh) {
        return MakeActionJSON(aa, hh);
    }

    protected static final String MakeActionJSON (int info, String gid) {
        JSONObject j_root = new JSONObject();
        try {
            int hhhj=Integer.parseInt(gid);

            j_root.put("id", info);
            j_root.put("value", hhhj);
            // j_root.put("state", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return j_root.toString();
    }

















}
