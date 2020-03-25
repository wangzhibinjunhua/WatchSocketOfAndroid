package com.xuhao.android.oksocket.wzb.action;

import android.content.Context;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
/**
*
* @Author: Zhibin Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/25 11:37
*/
public class IpAction {

        public static int execute(Context context, String ip){
            upload();

            List<String> msgArr= Arrays.asList(ip.split(","));
            if(msgArr.size()==2){ //valid ip
                String ipaddr=msgArr.get(0);
                String port=msgArr.get(1);
                if(Cmd.checkAddress(ipaddr)&&Cmd.checkPort(port)){
                    MyApplication.sp.set("ip_addr",ipaddr);
                    MyApplication.sp.set("ip_port",port);
                    return 0;
                }

            }
            return -1;
        }

    private static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb","IpAction upload executed at "+new Date().toString());
                String msg= Cmd.encode(Cmd.CS+Cmd.SPLIT+Cmd.IMEI+Cmd.SPLIT+Cmd.IP);
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }




}
