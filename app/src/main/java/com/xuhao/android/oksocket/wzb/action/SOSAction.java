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
* @Author: ZhiBin.Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/24 16:39
*/
public class SOSAction {

        public static void execute(Context context, String sosNumber){
            upload();
            List<String> msgArr= Arrays.asList(sosNumber.split(","));
            if(msgArr.size()==3){ //valid sos number
                MyApplication.sp.set("sos_number_1",msgArr.get(0));
                MyApplication.sp.set("sos_number_1",msgArr.get(1));
                MyApplication.sp.set("sos_number_1",msgArr.get(2));
            }
        }

    private static void upload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb", "SOSAction upload executed at " + new Date().toString());
                String msg = Cmd.encode(Cmd.CS + Cmd.SPLIT + Cmd.IMEI + Cmd.SPLIT + Cmd.SOS);
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

}
