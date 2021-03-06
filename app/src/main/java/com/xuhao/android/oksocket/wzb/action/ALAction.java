package com.xuhao.android.oksocket.wzb.action;

import android.util.Log;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LocationMulti;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.util.Date;

/**
*
* @Author: ZhiBin.Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/24 15:42
*/

public class ALAction {

    public static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb","ALAction upload executed at "+new Date().toString());
                String msg= Cmd.encode(Cmd.CS+Cmd.SPLIT+Cmd.IMEI+Cmd.SPLIT+Cmd.AL+","+packALInfo());
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

    private static String packALInfo(){
        String alData="";
        alData+= LocationMulti.getInstance(MyApplication.CONTEXT).getLastLocation();
        alData+=",";
        alData+=LocationMulti.getInstance(MyApplication.CONTEXT).getCellInfo();
        alData+=LocationMulti.getInstance(MyApplication.CONTEXT).getWifiInfo();
        return alData;
    }

}
