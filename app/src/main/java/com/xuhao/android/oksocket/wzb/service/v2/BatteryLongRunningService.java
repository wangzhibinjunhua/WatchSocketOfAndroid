package com.xuhao.android.oksocket.wzb.service.v2;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;

import com.xuhao.android.oksocket.data.MsgDataBean;
import com.xuhao.android.oksocket.wzb.receiver.v2.BatteryAlarmReceiver;
import com.xuhao.android.oksocket.wzb.util.Cmd;

import java.util.Date;

/**
*
* @Author: Zhibin Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/4/7 15:12
*/

public class BatteryLongRunningService extends Service {

    public static final int BATTERY_INTERVAL=60*60*1000;//60 minutes

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        doSomeThing();
        return super.onStartCommand(intent, flags, startId);
    }

    private void doSomeThing(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.e("wzb","BatteryLongRunningService executed at "+new Date().toString());
                String msg= Cmd.encode2(Cmd.BAT_NUM,Cmd.getBatteryLevel(true));
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();

        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime() + BATTERY_INTERVAL;
        Intent i = new Intent(this, BatteryAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
       // manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
        Cmd.setAlarmTime(manager,AlarmManager.ELAPSED_REALTIME_WAKEUP,triggerAtTime,pi);


    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
