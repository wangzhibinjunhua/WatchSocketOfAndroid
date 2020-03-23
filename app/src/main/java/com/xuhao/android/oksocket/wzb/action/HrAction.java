package com.xuhao.android.oksocket.wzb.action;

import android.util.Log;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LocationMulti;

import java.util.Date;

public class HrAction {

    public static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("wzb","HrAction upload executed at "+new Date().toString());
                String msg= Cmd.encode(Cmd.CS+Cmd.SPLIT+Cmd.IMEI+Cmd.SPLIT+Cmd.HR+","+packHrInfo());
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();

        CoreService.mCoreServiceHandler.postDelayed(hrTimeOut,5*1000);
    }

    public static Runnable hrTimeOut=new Runnable() {
        @Override
        public void run() {
            CoreService.mCoreServiceHandler.sendEmptyMessage(1000);
        }
    };

    private static String packHrInfo(){
        String data="";
        data+= ""+getHr();
        data+=",";
        data+=""+System.currentTimeMillis();

        return data;
    }

    private static int getHr(){
        int hr=60;
        return hr;

    }


}
