package com.xuhao.android.oksocket.wzb.action;

import android.content.Context;

import com.xuhao.android.oksocket.MyApplication;

import java.util.Arrays;
import java.util.List;

public class SOSAction {

        public static void execute(Context context, String sosNumber){
            List<String> msgArr= Arrays.asList(sosNumber.split(","));
            if(msgArr.size()==3){ //valid sos number
                MyApplication.sp.set("sos_number_1",msgArr.get(0));
                MyApplication.sp.set("sos_number_1",msgArr.get(1));
                MyApplication.sp.set("sos_number_1",msgArr.get(2));
            }
        }

}
