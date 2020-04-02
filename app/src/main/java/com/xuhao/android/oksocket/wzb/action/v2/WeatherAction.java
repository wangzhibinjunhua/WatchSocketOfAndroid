package com.xuhao.android.oksocket.wzb.action.v2;

import android.util.Log;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.v2.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LocationMultiv2;

import java.util.Date;

public class WeatherAction {

    public static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("wzb","Weather upload executed at "+new Date().toString());
                String msg= Cmd.encode(Cmd.CS+Cmd.SPLIT+Cmd.IMEI+Cmd.SPLIT+Cmd.WEATHER+","+packWeatherInfo());
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

    private static String packWeatherInfo(){
        String weatherData="";
        weatherData+= LocationMultiv2.getInstance(MyApplication.CONTEXT).getLastLocation();
        weatherData+=";";
        weatherData+=LocationMultiv2.getInstance(MyApplication.CONTEXT).getCellInfo();
        weatherData+=LocationMultiv2.getInstance(MyApplication.CONTEXT).getWifiInfo();
        return weatherData;
    }
}
