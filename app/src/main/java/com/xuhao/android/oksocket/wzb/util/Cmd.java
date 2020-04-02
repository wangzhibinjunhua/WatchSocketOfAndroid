package com.xuhao.android.oksocket.wzb.util;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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


    //cmd number
    public static final String DATA_START="@B#@";
    public static final String DATA_END="@E#@";
    //up cmd
    public static final String LK_NUM="003";
    public static final String INIT_NUM="001";
    public static final String SYNC_NUM="002";
    public static final String WEATHER_NUM="004";
    public static final String UD_NUM="005";
    public static final String AL_NUM="006";
    public static final String BAT_NUM="007";

    //down cmd
    public static final String SERVER_ACK_NUM="100";
    public static final String IP_NUM="101";
    public static final String FAMILY_PHONE_NUM="102";
    public static final String CONFIG_NUM="103";
    public static final String CONTROL_NUM="104";


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
    public static final String IMSI=getImsi();
    public static final String PHONENUMBER=getPhoneNum();
    public static final int OPTYPE=getOperatorType();
    public static final String DEVICE_MODEL="ZC01";
    public static final String SW_VERSION=getProperty("ro.hardware","unknow");

    public static String encode(String data){
        return String.format("%04x",data.length())+data;
    }

    public static String encode2(String cmdNum,String data){

        return DATA_START+cmdNum+"*"+IMEI+"*"+String.format("%06x",data.length())+"*"+data+DATA_END;
    }

    public static String decode(String data){
        if(data.length()<=4)return null;
        return data.substring(4);
    }

    public static String getImei() {
        String defaultImei="123456789012346";
        TelephonyManager telephonyManager = (TelephonyManager) MyApplication.CONTEXT.getSystemService(Context.TELEPHONY_SERVICE);
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

    public static String getImsi(){
        TelephonyManager mTelephonyMgr = (TelephonyManager) MyApplication.CONTEXT.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MyApplication.CONTEXT, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        String imsi = mTelephonyMgr.getSubscriberId()!=null? mTelephonyMgr.getSubscriberId():"";

        return imsi ;


    }

    public static String getPhoneNum(){
        TelephonyManager mTelephonyMgr = (TelephonyManager) MyApplication.CONTEXT.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MyApplication.CONTEXT, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return "";
        }
        String phone = mTelephonyMgr.getLine1Number()!=null? mTelephonyMgr.getLine1Number():"";

        return phone ;

    }


    //1 移动;2 联通;3 电信
    public static int getOperatorType(){
        int operType=0;
        TelephonyManager mTelephonyMgr = (TelephonyManager) MyApplication.CONTEXT.getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(MyApplication.CONTEXT, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return 0;
        }
        String type=mTelephonyMgr.getNetworkOperator();
        if (type.equals("46000") || type.equals("46002")) {
            operType=1;//中国移动
        } else if(type.equals("46001")) {
            operType = 2;//中国联通
        } else if (type.equals("46003")) {
            operType = 3;//中国电信
        }

        return operType;
    }


    public static String getBatteryLevel(boolean hasChargStatus){
        //BatteryManager manager=(BatteryManager)MyApplication.CONTEXT.getSystemService(Context.BATTERY_SERVICE);
        int level= 0;
        int chargStatus=0;
        //if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
        //    level = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
        //}else{
            Intent intent = MyApplication.CONTEXT.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            //LogUtil.logMessage("wzb","getBatteryLevel intent="+intent);
            if(intent != null){
                level = intent.getIntExtra("level", 0);
                int status=intent.getIntExtra("status",BatteryManager.BATTERY_STATUS_UNKNOWN);
                if(status==BatteryManager.BATTERY_STATUS_CHARGING) {
                    chargStatus=1;
                }
                else {
                    chargStatus=0;
                }

            }

        //}

        if(hasChargStatus){
            return ""+level+","+chargStatus;
        }
        return ""+level;


    }

    public static void send(String msg){
        CoreService.mManager.send(new MsgDataBean(encode(msg)));
    }

    public static void send2(String cmdNum,String msg){
        CoreService.mManager.send(new MsgDataBean(encode2(cmdNum,msg)));
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

    public static String getFamilyPhoneNumChecksum(){
        return MyApplication.sp.get("family_cs","");
    }


    //0:铃声+震动;1:仅震动;2:仅铃声;3:无震无声
    public static String getSoundSettings(){
        return MyApplication.sp.get("sound_settings","2");
    }

    public static int getUdIntervalMin(){
       return MyApplication.sp.get("upload",60);
    }

    public static int getPedoIntervalMin(){
        return MyApplication.sp.get("pedo",60);
    }

    public static int getLowBatLevel(){
        return MyApplication.sp.get("lowbat",15);
    }


    public static int getTrackOn(){
        return MyApplication.sp.get("track_on",0);
    }

    public static int getPedoOn(){
        return MyApplication.sp.get("pedo_on",0);
    }

    public static int getGpsOn(){
        return MyApplication.sp.get("gps_on",0);
    }

}
