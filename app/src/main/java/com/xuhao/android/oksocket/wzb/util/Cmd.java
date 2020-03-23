package com.xuhao.android.oksocket.wzb.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;

/**
 * Created by Administrator on 2018-07-11.
 */

public class Cmd {

    public static final String LK="LK";
    public static final String CS="CS";
    public static final String UD="UD";
    public static final String UPLOAD="UPLOAD";
    public static final String CR="CR";
    public static final String SPLIT="*";
    public static final String WEATHER="WEATHER";
    public static final String AL="AL";
    public static final String SYSTEMTIME="SYSTEMTIME";
    public static final String HR="HR";

    public static final String IMEI=getImei();









    public static String encode(String data){
        return String.format("%04x",data.length())+data;
    }

    public static String decode(String data){
        if(data.length()<=4)return null;
        return data.substring(4);
    }

    public static String getImei() {
        String defaultImei="123456789012346";
        TelephonyManager telephonyManager = (TelephonyManager) MyApplication.CONTEXT.getSystemService(MyApplication.CONTEXT.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MyApplication.CONTEXT, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return defaultImei;
        }
        String imei = telephonyManager.getDeviceId();
        Log.e("wzb","get imei:"+imei);
        return imei!=null? imei:defaultImei;
    }


    public static int getBatteryLevel(){
        BatteryManager manager=(BatteryManager)MyApplication.CONTEXT.getSystemService(Context.BATTERY_SERVICE);
        int level= 0;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            level = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        }
        return level;
    }

    public static void send(String msg){
        CoreService.mManager.send(new MsgDataBean(encode(msg)));
    }
}
