package com.xuhao.android.oksocket.wzb.action.v2;

import android.content.Context;
import android.content.Intent;

import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.v2.CoreService;
import com.xuhao.android.oksocket.wzb.service.v2.LkLongRunningService;
import com.xuhao.android.oksocket.wzb.service.v2.UdLongRunningService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.util.Date;

public class InitAction {

    public static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb","InitAction upload executed at "+new Date().toString());
                String msg=Cmd.encode2(Cmd.INIT_NUM,packInitInfo());
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

    public static void ack(Context context,String msg){
       context.startService(new Intent(context, LkLongRunningService.class));
       context.startService(new Intent(context, UdLongRunningService.class));
       ConfigSyncAction.upload();

       //below is test
       ALAction.upload();
       WeatherAction.upload();
       //test end
    }

    private static String packInitInfo(){
        String info="";
        info+=Cmd.PHONENUMBER;
        info+=",";
        info+=Cmd.IMSI;
        info+=",";
        info+=Cmd.OPTYPE;
        info+=",";
        info+=Cmd.DEVICE_MODEL;
        info+=",";
        info+=Cmd.SW_VERSION;
        info+=",";
        info+=Cmd.getBatteryLevel(false);
        return info;
    }
}
