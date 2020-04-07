package com.xuhao.android.oksocket.wzb.receiver.v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xuhao.android.oksocket.wzb.service.v2.BatteryLongRunningService;


/**
*
* @Author: Zhibin Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/4/7 15:11
*/

public class BatteryAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context, BatteryLongRunningService.class);
        context.startService(i);
    }
}
