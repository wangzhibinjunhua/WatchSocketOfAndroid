package com.xuhao.android.oksocket.wzb.action;


import android.content.Context;
import android.content.Intent;

import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.util.Date;

/**
*
* @Author: Zhibin.Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/24 16:40
*/
public class RebootAction {

        public static void execute(Context context){
                //notify system reboot
            upload();
            context.sendBroadcast(new Intent("com.wzb.custom.reboot"));
        }

    private static void upload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb", "RebootAction upload executed at " + new Date().toString());
                String msg = Cmd.encode(Cmd.CS + Cmd.SPLIT + Cmd.IMEI + Cmd.SPLIT + Cmd.RESET);
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

}
