package com.xuhao.android.oksocket.wzb.action;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.WindowManager;

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
public class AlertAction {

        public static void execute(Context context,String info){
            upload();
            showDialog(MyApplication.CONTEXT,info);
        }

    private static void upload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb", "AlertAction upload executed at " + new Date().toString());
                String msg = Cmd.encode(Cmd.CS + Cmd.SPLIT + Cmd.IMEI + Cmd.SPLIT + Cmd.ALERT);
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }



    private static void showDialog(Context context,String info){
            AlertDialog.Builder builder=new AlertDialog.Builder(context);
            builder.setTitle("Remind");
            builder.setMessage(info);
            final AlertDialog dialog = builder.create();
            dialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));
            dialog.show();
    }




}
