package com.xuhao.android.oksocket.wzb.util;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.telephony.CellInfo;
import android.telephony.CellInfoGsm;
import android.telephony.CellInfoWcdma;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.v2.CoreService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.WIFI_SERVICE;

/**
*
* @Author: ZhiBin.Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/24 17:04
*/

public class LocationMultiv2 {

    private volatile static LocationMultiv2 uniqueInstance;
    private LocationManager locationManager;
    private String locationProvider;
    private Location location;
    private Context mContext;

    //gps data
    private double latitude = 0.0;
    private double longitude = 0.0;
    private float speed=0.0f;
    private double altitude=0.0f;//海拔
    private float bearing=0.0f;//方位
    private float accuracy=0.0f;//精度
    private int gpsStateNum=0;

    public final int LBS_GPS=10001;
    public final int LBS_WIFI=10002;


    private WifiManager mWifiManager;

    private static int udType=0;//0:定时上报,1:指令查询

    private LocationMultiv2(Context context) {
        mContext = context;
    }


    public static LocationMultiv2 getInstance(Context context) {
        if (uniqueInstance == null) {
            synchronized (LocationMultiv2.class) {
                if (uniqueInstance == null) {
                    uniqueInstance = new LocationMultiv2(context);
                }
            }
        }
        return uniqueInstance;
    }


    public String getLastLocation() {
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("wzb","location no permission");
            return "V,0.0,0.0";

        }
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);

        String provider=null;
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            provider=LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            provider=LocationManager.NETWORK_PROVIDER;
        }else{
            provider=LocationManager.GPS_PROVIDER;
        }
        Location location = locationManager.getLastKnownLocation(provider);
        Log.e("wzb","getLastKnownLocation location="+location);

        String info="";

        Calendar now=Calendar.getInstance();
        String dateYear=String.format("%04d",now.get(Calendar.YEAR));
        String dateMonth=String.format("%02d",now.get(Calendar.MONTH)+1);
        String dateDay=String.format("%02d",now.get(Calendar.DAY_OF_MONTH));
        String date=dateYear+dateMonth+dateDay;
        String time=String.format("%02d",now.get(Calendar.HOUR_OF_DAY))+String.format("%02d",now.get(Calendar.MINUTE))
                +String.format("%02d",now.get(Calendar.SECOND));
        Log.e("wzb","date="+date+" time="+time);
        info+=date;

        info+=time;
        info+=";";

        String gpsStatus=location!=null? "A":"V";
        info+=gpsStatus;
        info+=",";
        String lat=location!=null? ""+location.getLatitude():"0.0";
        if(lat.substring(0,1).equals("-")){
            lat=lat+",S";
        }else{
            lat=lat+",N";
        }
        info+=lat;
        info+=",";
        String lon=location!=null? ""+location.getLongitude():"0.0";
        if(lon.substring(0,1).equals("-")){
            lon=lon+",W";
        }else{
            lon=lon+",E";
        }
        info+=lon;
        info+=",";
        // Log.e("wzb","info="+info);
        String spee=location!=null? ""+location.getSpeed():"0.0";
        info+=spee;
        info+=",";

        String direction=location!=null? ""+location.getBearing():"0.0";
        info+=direction;
        info+=",";

        String height=location!=null? ""+location.getAltitude():"0.0";
        info+=height;
        info+=",";

        String statelliteNum=location!=null? "5":"0";
        info+=statelliteNum;
        info+=",";

        String gsmDbm="0";
        info+=gsmDbm;



        return info;
    }

    public void StartLocation(int type){
        udType=type;
        LogUtil.logMessage("wzb","StartLocation +");
        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("wzb","location no permission");
            return ;

        }
        enableWifi();
        locationManager = (LocationManager) mContext.getSystemService(Context.LOCATION_SERVICE);

        List<String> providers = locationManager.getProviders(true);

        String provider=null;
        if(providers.contains(LocationManager.GPS_PROVIDER)){
            Log.e("wzb","GPS_PROVIDER");
            provider=LocationManager.GPS_PROVIDER;
        }else if(providers.contains(LocationManager.NETWORK_PROVIDER)){
            provider=LocationManager.NETWORK_PROVIDER;
            Log.e("wzb","NETWORK_PROVIDER");
        }else{
            // return ;
            provider=LocationManager.GPS_PROVIDER;
            Log.e("wzb","no PROVIDER");
        }
        locationManager.requestLocationUpdates(provider, 1000, 0, locationListener);
        mHandler.postDelayed(gpsTimeOut,20*1000);
        LogUtil.logMessage("wzb","StartLocation -");
    }

    private void enableWifi(){

        mWifiManager=(WifiManager) MyApplication.CONTEXT.getSystemService(WIFI_SERVICE);
        if(mWifiManager.isWifiEnabled()){
           // mWifiManager.startScan();
        }else{
            mWifiManager.setWifiEnabled(true);
            //mWifiManager.startScan();
        }


    }

    private List<ScanResult> getWifiList() {
        WifiManager wifiManager = (WifiManager) MyApplication.CONTEXT.getSystemService(WIFI_SERVICE);
        List<ScanResult> scanWifiList = wifiManager.getScanResults();
        Log.e("wzb","scanWifiList size="+scanWifiList.size());
        List<ScanResult> wifiList = new ArrayList<>();
        if (scanWifiList != null && scanWifiList.size() > 0) {
            HashMap<String, Integer> signalStrength = new HashMap<String, Integer>();
            for (int i = 0; i < scanWifiList.size(); i++) {
                ScanResult scanResult = scanWifiList.get(i);
                if (!scanResult.SSID.isEmpty()) {
                    String key = scanResult.SSID + " " + scanResult.capabilities;
                    if (!signalStrength.containsKey(key)) {
                        signalStrength.put(key, i);
                        wifiList.add(scanResult);
                    }
                }
            }
        }
        return wifiList;
    }

    public String getWifiInfo(){
        String wifiInfo="";
        List<ScanResult> wifiList=getWifiList();
        int wifiApNum=wifiList.size();

        wifiInfo+=""+(wifiApNum>4?4:wifiApNum);
        for(int i=0;i<(wifiApNum>4?4:wifiApNum);i++){
            wifiInfo+=",";
            wifiInfo+=wifiList.get(i).SSID;
            wifiInfo+=",";
            wifiInfo+=wifiList.get(i).BSSID;
            wifiInfo+=",";
            wifiInfo+=""+wifiList.get(i).level;
        }

        LogUtil.logMessage("wzb","wifiInfo="+wifiInfo);
        return wifiInfo;
    }

//android6.0
public String getCellInfoNew(){
    String cellInfo="";
    LogUtil.logMessage("wzb","getCellInfo +");
    TelephonyManager tm=(TelephonyManager) MyApplication.CONTEXT.getSystemService(Context.TELEPHONY_SERVICE);

    if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.
        Log.e("wzb","location no permission");
        return "0;";

    }
    try {
        List<CellInfo> infoLists=tm.getAllCellInfo();
        if (infoLists.size() < 1) {
            String stationNum = "0";
            cellInfo += stationNum;
            cellInfo += ";";


        } else {
            String stationNum = "" + (infoLists.size() > 5 ? 5 : infoLists.size());
            cellInfo += stationNum;
            cellInfo += ",";

            String gsmDelay = "1";
            cellInfo += gsmDelay;
            cellInfo += ",";

            String mcc = "460";
            cellInfo += mcc;
            cellInfo += ",";

            String mnc="00";
            if(infoLists.get(0).toString().contains("CellInfoWcdma")){
                CellInfoWcdma cw=(CellInfoWcdma)infoLists.get(0);
                mnc=""+cw.getCellIdentity().getMnc();
            }else {
                CellInfoGsm cg = (CellInfoGsm) infoLists.get(0);
                mnc = "" + cg.getCellIdentity().getMnc();
            }

            cellInfo += mnc;
            cellInfo += ",";
            for (int i = 0; i < (infoLists.size() > 5 ? 5 : infoLists.size()); i++) {
               if(infoLists.get(i).toString().contains("CellInfoWcdma")){
                   CellInfoWcdma cg2 = (CellInfoWcdma) infoLists.get(i);
                   String lac = "" + cg2.getCellIdentity().getLac();//基站位置区域码
                   cellInfo += lac;
                   cellInfo += ",";

                   String cellid = "" + cg2.getCellIdentity().getCid();//基站编号
                   cellInfo += cellid;
                   cellInfo += ",";

                   String stationDbm = "" + cg2.getCellSignalStrength().getDbm();
                   cellInfo += stationDbm;
                   cellInfo += ",";
               }else {

                   CellInfoGsm cg2 = (CellInfoGsm) infoLists.get(i);
                   String lac = "" + cg2.getCellIdentity().getLac();//基站位置区域码
                   cellInfo += lac;
                   cellInfo += ",";

                   String cellid = "" + cg2.getCellIdentity().getCid();//基站编号
                   cellInfo += cellid;
                   cellInfo += ",";

                   String stationDbm = "" + cg2.getCellSignalStrength().getDbm();
                   cellInfo += stationDbm;
                   cellInfo += ",";
               }

            }
        }
    }catch(Exception e){
        LogUtil.logMessage("wzb","getCellInfo exception e:"+e.toString());
        cellInfo += 0;
        cellInfo += ";";

    }
    cellInfo=cellInfo.substring(0,cellInfo.length()-1);
    cellInfo+=";";

    LogUtil.logMessage("wzb","cellInfo="+cellInfo);
    LogUtil.logMessage("wzb","getCellInfo -");
    return cellInfo;
}


    //android10.0 此函数获取基站信息ok;6.0上不行
    public String getCellInfo(){
        String cellInfo="";
        LogUtil.logMessage("wzb","getCellInfo +");
        TelephonyManager tm=(TelephonyManager) MyApplication.CONTEXT.getSystemService(Context.TELEPHONY_SERVICE);

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.e("wzb","location no permission");
            return "0;";

        }
        try {
            List<CellInfo> infoLists=tm.getAllCellInfo();
            if (infoLists.size() < 1) {
                String stationNum = "0";
                cellInfo += stationNum;
                cellInfo += ",";


            } else {
                String stationNum = "" + (infoLists.size() > 5 ? 5 : infoLists.size());
                cellInfo += stationNum;
                cellInfo += ",";

                String gsmDelay = "1";
                cellInfo += gsmDelay;
                cellInfo += ",";

                String mcc = "460";
                cellInfo += mcc;
                cellInfo += ",";

                CellInfoGsm cg = (CellInfoGsm) infoLists.get(0);
                String mnc = "" + cg.getCellIdentity().getMnc();

                cellInfo += mnc;
                cellInfo += ",";
                for (int i = 0; i < (infoLists.size() > 5 ? 5 : infoLists.size()); i++) {
                    CellInfoGsm cg2 = (CellInfoGsm) infoLists.get(i);
                    String lac = "" + cg2.getCellIdentity().getLac();//基站位置区域码
                    cellInfo += lac;
                    cellInfo += ",";

                    String cellid = "" + cg2.getCellIdentity().getCid();//基站编号
                    cellInfo += cellid;
                    cellInfo += ",";

                    String stationDbm = "" + cg2.getCellSignalStrength().getDbm();
                    cellInfo += stationDbm;
                    cellInfo += ",";

                }
            }
        }catch(Exception e){
            LogUtil.logMessage("wzb","getCellInfo exception e:"+e.toString());
            cellInfo += 0;
            cellInfo += ";";

        }

        cellInfo=cellInfo.substring(0,cellInfo.length()-1);
        cellInfo+=";";
        LogUtil.logMessage("wzb","cellInfo="+cellInfo);
        LogUtil.logMessage("wzb","getCellInfo -");
        return cellInfo;
    }

    Runnable gpsTimeOut=new Runnable() {
        @Override
        public void run() {
            LogUtil.logMessage("wzb","gpsTimeOut +");
            mHandler.sendEmptyMessage(LBS_WIFI);
        }
    };

    public Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case LBS_GPS:
                    uploadLbsGpsData();
                    break;
                case LBS_WIFI:
                    uploadLbsWifiData();
                    break;
                default:
                    break;
            }
        }
    };

    private void uploadLbsGpsData(){

        String info="";

        Calendar now=Calendar.getInstance();
        String dateYear=String.format("%04d",now.get(Calendar.YEAR));
        String dateMonth=String.format("%02d",now.get(Calendar.MONTH)+1);
        String dateDay=String.format("%02d",now.get(Calendar.DAY_OF_MONTH));
        String date=dateYear+dateMonth+dateDay;
        String time=String.format("%02d",now.get(Calendar.HOUR_OF_DAY))+String.format("%02d",now.get(Calendar.MINUTE))
                +String.format("%02d",now.get(Calendar.SECOND));
        Log.e("wzb","date="+date+" time="+time);
        info+=date;

        info+=time;
        info+=";";

        String gpsStatus="A";
        info+=gpsStatus;
        info+=",";
        String lat=""+latitude;
        if(lat.substring(0,1).equals("-")){
            lat=lat+",S";
        }else{
            lat=lat+",N";
        }
        info+=lat;
        info+=",";
        String lon=""+longitude;
        if(lon.substring(0,1).equals("-")){
            lon=lon+",W";
        }else{
            lon=lon+",E";
        }
        info+=lon;
        info+=",";
        // Log.e("wzb","info="+info);
        String spee=""+speed;
        info+=spee;
        info+=",";

        String direction=""+bearing;
        info+=direction;
        info+=",";

        String height=""+altitude;
        info+=height;
        info+=",";

        String statelliteNum=""+gpsStateNum;
        info+=statelliteNum;
        info+=",";

        String gsmDbm="0";
        info+=gsmDbm;
        info+=";";


        info+=getCellInfoNew();

        info+=getWifiInfo();


        LogUtil.logMessage("wzb","uploadLbsGpsData info="+info);
        final String udInfo=udType+"*"+info;
        new Thread(new Runnable() {
                @Override
                public void run() {

                    String msg= Cmd.encode2(Cmd.UD_NUM,udInfo);
                    CoreService.mManager.send(new MsgDataBean(msg));
                }
            }).start();

    }

    private void uploadLbsWifiData(){

        String info="";

        Calendar now=Calendar.getInstance();
        String dateYear=String.format("%04d",now.get(Calendar.YEAR));
        String dateMonth=String.format("%02d",now.get(Calendar.MONTH)+1);
        String dateDay=String.format("%02d",now.get(Calendar.DAY_OF_MONTH));
        String date=dateYear+dateMonth+dateDay;
        String time=String.format("%02d",now.get(Calendar.HOUR_OF_DAY))+String.format("%02d",now.get(Calendar.MINUTE))
                +String.format("%02d",now.get(Calendar.SECOND));
        Log.e("wzb","date="+date+" time="+time);
        info+=date;

        info+=time;
        info+=";";

        String gpsStatus="V";
        info+=gpsStatus;
        info+=",";
        String lat=""+latitude;
        if(lat.substring(0,1).equals("-")){
            lat=lat+",S";
        }else{
            lat=lat+",N";
        }
        info+=lat;
        info+=",";
        String lon=""+longitude;
        if(lon.substring(0,1).equals("-")){
            lon=lon+",W";
        }else{
            lon=lon+",E";
        }
        info+=lon;
        info+=",";
        // Log.e("wzb","info="+info);
        String spee=""+speed;
        info+=spee;
        info+=",";

        String direction=""+bearing;
        info+=direction;
        info+=",";

        String height=""+altitude;
        info+=height;
        info+=",";

        String statelliteNum=""+gpsStateNum;
        info+=statelliteNum;
        info+=",";

        String gsmDbm="0";
        info+=gsmDbm;
        info+=";";


        info+=getCellInfoNew();


        info+=getWifiInfo();

        LogUtil.logMessage("wzb","uploadLbsWifiData info="+info);
        final String udInfo=udType+"*"+info;
        new Thread(new Runnable() {
            @Override
            public void run() {

                String msg= Cmd.encode2(Cmd.UD_NUM,udInfo);
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }



    LocationListener locationListener = new LocationListener() {
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        @Override
        public void onProviderEnabled(String provider) {
            //Log.e(TAG, provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
           // Log.e(TAG, provider);
        }

        @Override
        public void onLocationChanged(Location location) {
            if (location != null) {
                Log.e("wzb", "Location changed : Lat: " + location.getLatitude() + " Lng: " + location.getLongitude()+" location="+location);
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                speed=location.getSpeed();
                altitude=location.getAltitude();
                bearing=location.getBearing();
                accuracy=location.getAccuracy();
                LogUtil.logMessage("wzb","accuracy="+accuracy);
                if(accuracy<10){
                    gpsStateNum=10;
                }else{
                    gpsStateNum=4;
                }

                mHandler.sendEmptyMessage(LBS_GPS);
                locationManager.removeUpdates(locationListener);
                locationManager=null;
                mHandler.removeCallbacks(gpsTimeOut);
            }
        }
    };



}
