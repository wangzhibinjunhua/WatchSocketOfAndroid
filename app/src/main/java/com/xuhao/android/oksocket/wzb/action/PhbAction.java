package com.xuhao.android.oksocket.wzb.action;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

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
* @Time: 2020/3/26 15:35
*/
public class PhbAction {

        public static void execute(Context context, String phb){
            upload();
            savePhb(phb);
        }

    private static void upload() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                LogUtil.logMessage("wzb", "PhbAction upload executed at " + new Date().toString());
                String msg = Cmd.encode(Cmd.CS + Cmd.SPLIT + Cmd.IMEI + Cmd.SPLIT + Cmd.PHB);
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();
    }

    public static void savePhb(final String phb){

        new Thread(new Runnable() {
            @Override
            public void run() {
                List<String> msgArr= Arrays.asList(phb.split(","));
                LogUtil.logMessage("wzb","phb size:"+msgArr.size());
               // if(msgArr.size()==50){ //valid phb number 25
                if(true){
                    Uri uri = Uri.parse("content://com.android.contacts/raw_contacts");
                    MyApplication.CONTEXT.getContentResolver().delete(uri,"_id!=-1", null);
                    for(int i=0;i<msgArr.size();i+=2){

                        String name=Cmd.unicodeToStr(msgArr.get(i+1));
                        String phone=msgArr.get(i);
                        LogUtil.logMessage("wzb","name="+name+";phone="+phone);
                        if(!TextUtils.isEmpty(name) && !TextUtils.isEmpty(phone)){
                            Cmd.addContact(MyApplication.CONTEXT,name,phone);
                        }
                    }
                }
            }
        }).start();


    }

}
