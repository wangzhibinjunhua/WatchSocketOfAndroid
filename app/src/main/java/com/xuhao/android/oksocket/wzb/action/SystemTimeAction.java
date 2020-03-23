package com.xuhao.android.oksocket.wzb.action;

import android.util.Log;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LocationMulti;

import java.util.Date;

public class SystemTimeAction {

    public static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("wzb","SystemTimeAction upload executed at "+new Date().toString());
                String msg= Cmd.encode(Cmd.CS+Cmd.SPLIT+Cmd.IMEI+Cmd.SPLIT+Cmd.SYSTEMTIME);
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }



}
