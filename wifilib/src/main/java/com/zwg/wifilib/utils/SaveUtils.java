package com.zwg.wifilib.utils;

import android.content.Context;
import android.content.SharedPreferences;


/**
 * 作用：
 * 时间：2015/9/22
 */
public class SaveUtils {


    /**
     * 登录是否是手动存储标记
     * @param uid
     */
    public static void saveInfo(Context context,String uid){
        SharedPreferences preferences=context.getSharedPreferences("islian",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putString("uid",uid);

        editor.commit();
    }

    public static String getIsLian(Context context){
        SharedPreferences preferences= context.getSharedPreferences("islian",Context.MODE_PRIVATE);
        String str=preferences.getString("uid","");
        return str;
    }

    /**
     * 删除信息
     */
    public static void deletInfo(Context context){
        SharedPreferences preferences= context.getSharedPreferences("islian", Context.MODE_PRIVATE);
        preferences.edit().clear().commit();

    }


}
