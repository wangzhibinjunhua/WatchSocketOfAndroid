package com.xuhao.android.oksocket.wzb.receiver.v2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xuhao.android.oksocket.wzb.service.v2.CoreService;

/**
 * Created by Administrator on 2018-07-12.
 */

public class ReConnectAlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("wzb","ReConnectAlarmReceiver");
        CoreService.connect();
    }
}
