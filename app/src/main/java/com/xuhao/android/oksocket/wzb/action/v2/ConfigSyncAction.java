package com.xuhao.android.oksocket.wzb.action.v2;

import android.content.Context;
import android.content.Intent;

import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.service.v2.CoreService;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.util.Date;

public class ConfigSyncAction {

    public static void upload(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb","ConfigSyncAction upload executed at "+new Date().toString());
                String msg=Cmd.encode2(Cmd.SYNC_NUM,packSyncInfo());
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

    public static void ack(Context context,String msg){

    }

    private static String packSyncInfo(){
        String info="";
        info+="9";
        info+=";";
        info+=Cmd.getFamilyPhoneNumChecksum();
        info+=",";
        info+="na";
        info+=",";
        info+=Cmd.getSoundSettings();
        info+=",";
        info+=Cmd.getUdIntervalMin();
        info+=",";
        info+=Cmd.getPedoIntervalMin();
        info+=",";
        info+=Cmd.getLowBatLevel();
        info+=",";
        info+=Cmd.getTrackOn();
        info+=",";
        info+=Cmd.getPedoOn();
        info+=",";
        info+=Cmd.getGpsOn();
        return info;
    }
}
