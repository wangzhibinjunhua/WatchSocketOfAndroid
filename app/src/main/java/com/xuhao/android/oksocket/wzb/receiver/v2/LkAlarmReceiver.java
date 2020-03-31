package com.xuhao.android.oksocket.wzb.receiver.v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xuhao.android.oksocket.wzb.service.v2.LkLongRunningService;

/**
 * Created by Administrator on 2018-07-10.
 */

public class LkAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i=new Intent(context,LkLongRunningService.class);
        context.startService(i);
    }
}
