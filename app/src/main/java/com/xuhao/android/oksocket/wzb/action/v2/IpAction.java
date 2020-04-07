package com.xuhao.android.oksocket.wzb.action.v2;

import android.content.Context;

import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.v2.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
*
* @Author: Zhibin Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/4/7 16:20
*/
public class IpAction {

        public static int execute(Context context, String ip,String port){
            upload();

            if(Cmd.checkAddress(ip)&&Cmd.checkPort(port)){

                //仅为测试,考虑到恢复出厂设置会清除data,会导致恢复出厂设置后的机器无法连接服务器,此ip信息
                //应该存储在恢复出厂设置也不会清掉的位置,比如/cache/recovery/last_
                    MyApplication.sp.set("ip_addr",ip);
                    MyApplication.sp.set("ip_port",port);
                    return 0;
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
