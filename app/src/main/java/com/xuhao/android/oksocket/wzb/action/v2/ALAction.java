package com.xuhao.android.oksocket.wzb.action.v2;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.v2.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LocationMultiv2;
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
                String msg= Cmd.encode2(Cmd.AL_NUM,packALInfo());
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

    private static String packALInfo(){
        String alData="";
        alData+="0*";
        alData+= LocationMultiv2.getInstance(MyApplication.CONTEXT).getLastLocation();
        alData+=";";
        alData+=LocationMultiv2.getInstance(MyApplication.CONTEXT).getCellInfo();
        alData+=LocationMultiv2.getInstance(MyApplication.CONTEXT).getWifiInfo();
        return alData;
    }

}
