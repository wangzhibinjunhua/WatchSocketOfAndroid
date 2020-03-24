package com.xuhao.android.oksocket.wzb.action;

import android.util.Log;

import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;

import java.util.Date;
/**
*
* @Author: ZhiBin.Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/24 16:38
*/
public class SleepAction {

    public static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("wzb","BpAction upload executed at "+new Date().toString());
                String msg= Cmd.encode(Cmd.CS+Cmd.SPLIT+Cmd.IMEI+Cmd.SPLIT+Cmd.SLEEP+","+packSleepInfo());
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

    private static String packSleepInfo(){
        String data="";
        data+= getSleep();
        //data+=",";
       // data+=""+System.currentTimeMillis();

        return data;
    }

    private static String getSleep(){
        String info="";
        return info;

    }


}
