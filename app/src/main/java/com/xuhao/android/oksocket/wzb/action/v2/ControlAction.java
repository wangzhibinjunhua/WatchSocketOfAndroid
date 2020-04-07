package com.xuhao.android.oksocket.wzb.action.v2;


import android.content.Context;
import android.content.Intent;

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
* @Time: 2020/3/24 15:43
*/
public class ControlAction {

        public static void execute(Context context,String msg){
                //notify system
            upload();
            if(msg.equals("1")){//立即定位
                LocationMultiv2.getInstance(MyApplication.CONTEXT).StartLocation(1);

            }else if(msg.equals("2")){ //立即重启
                context.sendBroadcast(new Intent("com.wzb.custom.reboot"));

            }else if(msg.equals("3")){ //立即关机
                context.sendBroadcast(new Intent("com.wzb.custom.poweroff"));
            }
            //context.sendBroadcast(new Intent("com.wzb.custom.factory_reset"));
        }

    private static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb","ControlAction upload executed at "+new Date().toString());
                String msg= Cmd.encode(Cmd.CS+Cmd.SPLIT+Cmd.IMEI+Cmd.SPLIT+Cmd.FACTORY);
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

}
