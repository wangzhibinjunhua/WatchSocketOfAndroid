package com.xuhao.android.oksocket.wzb.action;


import android.content.Context;
import android.content.Intent;
/**
*
* @Author: ZhiBin.Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/24 15:43
*/
public class FactoryAction {

        public static void execute(Context context){
                //notice system factoryreset
            context.sendBroadcast(new Intent("com.wzb.custom.factory_reset"));
        }

}
