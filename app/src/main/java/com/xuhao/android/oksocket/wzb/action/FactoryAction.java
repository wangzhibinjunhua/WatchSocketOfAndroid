package com.xuhao.android.oksocket.wzb.action;


import android.content.Context;
import android.content.Intent;

public class FactoryAction {

        public static void execute(Context context){
                //notice system factoryreset
            context.sendBroadcast(new Intent("com.wzb.custom.factory_reset"));
        }

}
