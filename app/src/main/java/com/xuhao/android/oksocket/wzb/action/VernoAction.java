package com.xuhao.android.oksocket.wzb.action;


import android.content.Context;

import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.util.Date;

/**
*
* @Author: ZhiBin.Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/24 16:40
*/
public class VernoAction {

        public static void execute(Context context){

           // String version= Cmd.getProperty("ro.hardware","unknow");
            upload();
        }


        private static void upload(){
            new Thread(new Runnable() {
                @Override
                public void run() {
                    LogUtil.logMessage("wzb","VernoAction upload executed at "+new Date().toString());
                    String msg= Cmd.encode(Cmd.CS+Cmd.SPLIT+Cmd.IMEI+Cmd.SPLIT+Cmd.VERNO+","+packVernoInfo());
                    CoreService.mManager.send(new MsgDataBean(msg));
                }
            }).start();
        }

        private static String packVernoInfo(){
            String data="";
            String version= Cmd.getProperty("ro.hardware","unknow");
            data+=version;
            return data;
        }

}
