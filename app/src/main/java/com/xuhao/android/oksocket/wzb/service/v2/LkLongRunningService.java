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
import com.xuhao.android.oksocket.wzb.receiver.v2.LkAlarmReceiver;
import com.xuhao.android.oksocket.wzb.util.Cmd;

import java.util.Date;

/**
 * Created by Administrator on 2018-07-10.
 */

public class LkLongRunningService extends Service {

    public static final int LK_INTERVAL=30*1000;//60 seconds

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
                Log.e("wzb","LkLongRunningService executed at "+new Date().toString());
                String msg= Cmd.encode2(Cmd.LK_NUM,Cmd.getBatteryLevel(true));
                CoreService.mManager.send(new MsgDataBean(msg));
            }
        }).start();

        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime() + LK_INTERVAL;
        Intent i = new Intent(this, LkAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
