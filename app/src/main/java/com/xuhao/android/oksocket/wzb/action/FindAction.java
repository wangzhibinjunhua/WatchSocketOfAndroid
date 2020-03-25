package com.xuhao.android.oksocket.wzb.action;


import android.content.Context;
import android.content.Intent;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;

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
public class FindAction {
    private static int playAudioFlag=0;

        public static void execute(Context context){

            upload();
            if(playAudioFlag==0){
                playAudio(context);
            }
        }

    private static void upload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb", "FindAction upload executed at " + new Date().toString());
                String msg = Cmd.encode(Cmd.CS + Cmd.SPLIT + Cmd.IMEI + Cmd.SPLIT + Cmd.FIND);
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

    private static void playAudio(Context context){
        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        final Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();
        playAudioFlag=1;
        CoreService.mCoreServiceHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                r.stop();
                playAudioFlag=0;
            }
        },60*1000);
    }



}
