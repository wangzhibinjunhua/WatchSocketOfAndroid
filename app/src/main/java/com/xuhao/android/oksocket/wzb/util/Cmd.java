package com.xuhao.android.oksocket.wzb.util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.R;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;
import java.util.List;

import android.provider.ContactsContract.RawContacts;
import android.provider.ContactsContract.Data;

import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.provider.ContactsContract.CommonDataKinds.StructuredName;

/**
*
* @Author: Zhibin.Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/24 16:39
*/

public class Cmd {

    public static final String IP_ADDR_DEFAULT="192.168.1.134";
    public static final String IP_PORT_DEFAULT="8282";

    public static final int DATA_CMD_HEADER_LEN=2+1+15+1;

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
    public static final String BP="BP";
    public static final String ECG="ECG";
    public static final String SLEEP="SLEEP";
    public static final String PHOTO="PHOTO";
    public static final String CALL="CALL";
    public static final String SOS="SOS";
    public static final String FACTORY="FACTORY";
    public static final String VERNO="VERNO";
    public static final String RESET="RESET";
    public static final String FIND="FIND";
    public static final String POWEROFF="POWEROFF";
    public static final String REMIND="REMIND";
    public static final String PHB="PHB";
    public static final String PHB2="PHB2";
    public static final String TK="TK";
    public static final String SILENCETIME="SILENCETIME";
    public static final String VOLUME="VOLUME";
    public static final String BRIGHTNESS="BRIGHTNESS";
    public static final String CXYELL="CXYELL";
    public static final String PEDO="PEDO";
    public static final String PING="PING";
    public static final String MESSAGE="MESSAGE";
    public static final String IP="IP";
    public static final String LANGUAGE="LANGUAGE";
    public static final String ALERT="ALERT";


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

    public static String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class, String.class);
            value = (String)(get.invoke(c, key, defaultValue));
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logMessage("wzb","getProperty Exception="+e.toString());
        }finally {
            return value;
        }
    }

    public static void setProperty(String key, String value) {
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method set = c.getMethod("set", String.class, String.class);
            set.invoke(c, key, value);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.logMessage("wzb","setProperty Exception="+e.toString());
        }
    }

    public static boolean checkAddress(String s) {
        return s.matches("((25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))\\.){3}(25[0-5]|2[0-4]\\d|((1\\d{2})|([1-9]?\\d)))");
    }

    public static boolean checkPort(String s) {
        return s.matches("^[1-9]$|(^[1-9][0-9]$)|(^[1-9][0-9][0-9]$)|(^[1-9][0-9][0-9][0-9]$)|(^[1-6][0-5][0-5][0-3][0-5]$)");
    }


    // unicode feff开头 不含.  举例：feff72387238 就是爸爸
    public static String unicodeToStr(String unicode) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i+4 <= unicode.length(); i+=4) {
            String info=unicode.substring(i,i+4);

            int index = Integer.parseInt(info, 16);
            sb.append((char) index);
        }
        return sb.toString();
    }


    public static void addContact(Context context,String name, String phoneNumber) {
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();

        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = context.getContentResolver().insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();

        values.put(Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(StructuredName.GIVEN_NAME, name);
        // 向联系人URI添加联系人名字
        context.getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();

        values.put(Data.RAW_CONTACT_ID, rawContactId);
        values.put(Data.MIMETYPE, Phone.CONTENT_ITEM_TYPE);
        // 联系人的电话号码
        values.put(Phone.NUMBER, phoneNumber);
        // 电话类型
        values.put(Phone.TYPE, Phone.TYPE_MOBILE);
        // 向联系人电话号码URI添加电话号码
        context.getContentResolver().insert(Data.CONTENT_URI, values);
        values.clear();

        //values.put(Data.RAW_CONTACT_ID, rawContactId);
        //values.put(Data.MIMETYPE, Email.CONTENT_ITEM_TYPE);
        // 联系人的Email地址
        //values.put(Email.DATA, "zhangphil@xxx.com");
        // 电子邮件的类型
       // values.put(Email.TYPE, Email.TYPE_WORK);
        // 向联系人Email URI添加Email数据
       // context.getContentResolver().insert(Data.CONTENT_URI, values);

    }




}
