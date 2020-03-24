package com.xuhao.android.oksocket.wzb.action;

import android.util.Log;

import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.util.Date;

public class BpAction {

    public static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb","BpAction upload executed at "+new Date().toString());
                String msg= Cmd.encode(Cmd.CS+Cmd.SPLIT+Cmd.IMEI+Cmd.SPLIT+Cmd.BP+","+packBpInfo());
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();

       // CoreService.mCoreServiceHandler.postDelayed(bpTimeOut,5*1000);
    }

    public static Runnable bpTimeOut=new Runnable() {
        @Override
        public void run() {
           // CoreService.mCoreServiceHandler.sendEmptyMessage(1000);
        }
    };

    private static String packBpInfo(){
        String data="";
        data+= getBp();
        data+=",";
        data+=""+System.currentTimeMillis();

        return data;
    }

    private static String getBp(){
        String info="";
        return info;

    }


}
