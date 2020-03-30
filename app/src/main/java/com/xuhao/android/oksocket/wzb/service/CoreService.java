package com.xuhao.android.oksocket.wzb.service;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import com.xuhao.android.libsocket.sdk.ConnectionInfo;
import com.xuhao.android.libsocket.sdk.OkSocketOptions;
import com.xuhao.android.libsocket.sdk.SocketActionAdapter;
import com.xuhao.android.libsocket.sdk.bean.IPulseSendable;
import com.xuhao.android.libsocket.sdk.bean.ISendable;
import com.xuhao.android.libsocket.sdk.bean.OriginalData;
import com.xuhao.android.libsocket.sdk.connection.IConnectionManager;
import com.xuhao.android.libsocket.sdk.connection.NoneReconnect;
import com.xuhao.android.libsocket.sdk.protocol.IHeaderProtocol;
import com.xuhao.android.libsocket.utils.BytesUtils;
import com.xuhao.android.oksocket.MyApplication;
import com.xuhao.android.oksocket.wzb.action.AlertAction;
import com.xuhao.android.oksocket.wzb.action.FindAction;
import com.xuhao.android.oksocket.wzb.action.HrAction;
import com.xuhao.android.oksocket.wzb.action.IpAction;
import com.xuhao.android.oksocket.wzb.action.VernoAction;
import com.xuhao.android.oksocket.wzb.action.WeatherAction;
import com.xuhao.android.oksocket.wzb.camera.CameraService;
import com.xuhao.android.oksocket.wzb.receiver.LkAlarmReceiver;
import com.xuhao.android.oksocket.wzb.receiver.ReConnectAlarmReceiver;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static com.xuhao.android.libsocket.sdk.OkSocket.open;

/**
 * @author wzb<wangzhibin_x@qq.com>
 * @date 2018-07-02	16:43
 */
public class CoreService extends Service{

    private ConnectionInfo mInfo;
    public static  IConnectionManager mManager;
    private OkSocketOptions mOkOptions;
    private Context mContext;



    private SocketActionAdapter adapter = new SocketActionAdapter() {

        @Override
        public void onSocketConnectionSuccess(Context context, ConnectionInfo info, String action) {
            Log.e("wzb","onSocketConnectionSuccess ");
            context.startService(new Intent(context,LkLongRunningService.class));
            context.startService(new Intent(context,UdLongRunningService.class));
        }

        @Override
        public void onSocketDisconnection(Context context, ConnectionInfo info, String action, Exception e) {
            if (e != null) {
                Log.e("wzb","onSocketDisconnection exception"+e.getMessage());
            } else {
                Log.e("wzb","onSocketDisconnection normal");
            }
            sendReConnect();
        }

        @Override
        public void onSocketConnectionFailed(Context context, ConnectionInfo info, String action, Exception e) {
            Toast.makeText(context, "onSocketConnectionFailed" + e.getMessage(), LENGTH_SHORT).show();
            Log.e("wzb","onSocketConnectionFailed ");
            sendReConnect();
        }

        @Override
        public void onSocketReadResponse(Context context, ConnectionInfo info, String action, OriginalData data) {
            super.onSocketReadResponse(context, info, action, data);
            String str = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            //logRece(str);
            Log.e("wzb","CoreService/onSocketReadResponse rece:"+str);
            parseData(str);
            Log.e("wzb","parseData end");
        }

        @Override
        public void onSocketWriteResponse(Context context, ConnectionInfo info, String action, ISendable data) {
            super.onSocketWriteResponse(context, info, action, data);
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.e("wzb","CoreService/onSocketWriteResponse send:"+str);
            //logSend(str);
        }

        @Override
        public void onPulseSend(Context context, ConnectionInfo info, IPulseSendable data) {
            super.onPulseSend(context, info, data);
            String str = new String(data.parse(), Charset.forName("utf-8"));
            //logSend(str);
        }
    };

    IHeaderProtocol mIHeaderProtocolA=new IHeaderProtocol(){
        //协议A 包长+内容
        @Override
        public int getHeaderLength() {

            return 4;
        }

        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {

            String hexHeaderStr= BytesUtils.convertHexToString(BytesUtils.bytesToHexString(header));
            return Integer.valueOf(hexHeaderStr,16);


        }
    };

    IHeaderProtocol mIHeaderProtocolB=new IHeaderProtocol(){
        //协议B 包头有其他内容，开始标识aa+包长， 包尾有结束标识aa.仅举例，aa可换其他字符
        @Override
        public int getHeaderLength() {
            return 6;

        }

        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {
            String headerStr=new String(header);
            if(!"aa".equals(headerStr.substring(0,2))){
                return 0; //如果包头标识不符合，返回0即可丢弃此次错误数据
             }
            LogUtil.logMessage("wzb","headerStr="+headerStr);

            return Integer.valueOf(headerStr.substring(2),16)+2;

        }
    };


    private void initSocket(){
        String ipAddr=MyApplication.sp.get("ip_addr",Cmd.IP_ADDR_DEFAULT);
        String ipPort=MyApplication.sp.get("ip_port",Cmd.IP_PORT_DEFAULT);
        mInfo = new ConnectionInfo(ipAddr, Integer.parseInt(ipPort));
        mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setWritePackageBytes(1024)
                .setMaxReadDataMB(5)
                .setHeaderProtocol(mIHeaderProtocolA)
                .build();
        mManager = open(mInfo).option(mOkOptions);
        Log.e("wzb","initSocket mManager="+mManager);
        if(mManager !=null) mManager.registerReceiver(adapter);
        connect();

    }

    private void releaseSocket(){
        if(mManager != null){
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
    }

    private void sendReConnect(){
        AlarmManager manager=(AlarmManager)getSystemService(ALARM_SERVICE);
        long triggerAtTime= SystemClock.elapsedRealtime() + 5*1000;
        Intent i = new Intent(this, ReConnectAlarmReceiver.class);
        PendingIntent pi = PendingIntent.getBroadcast(this, 0, i, 0);
        manager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtTime, pi);
    }

    public static void connect(){
        if(mManager == null) return;
        if(!mManager.isConnect()) mManager.connect();
    }

    public static void disconnect(){
        if(mManager == null) return;
        if(mManager.isConnect())mManager.disconnect();
    }

    private void reconnectNewIp(){
        releaseSocket();
        initSocket();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mContext=this;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("wzb","CoreService onStartCommand");
        initSocket();
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LogUtil.logMessage("wzb","CoreService onDestroy");
        releaseSocket();
        sendBroadcast(new Intent("com.android.custom.oksocket_reboot"));
    }

    public static Handler mCoreServiceHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 1000://test
                    LogUtil.logMessage("wzb","mCoreServiceHandler test");
                    break;

                default:
                    break;
            }
        }
    };

    private void parseData(String msg){
        List<String> msgArr= Arrays.asList(msg.split("\\*|,",-1));

        String imei=msgArr.get(1);
        String cmd=msgArr.get(2);
       // String info=msgArr.get(3).substring(cmd.length()+1);
        String info="";
       if(msgArr.size()>3) info=msg.substring(Cmd.DATA_CMD_HEADER_LEN+cmd.length()+1);
        Log.e("wzb","parseData imei="+imei+",cmd="+cmd+",info="+info);
        String rspMsg="";
        switch (cmd){
            case Cmd.PING:
                rspMsg = Cmd.CS + Cmd.SPLIT + imei + Cmd.SPLIT + Cmd.PING;
                Cmd.send(rspMsg);
                break;
            case "SOS1":
                break;
            case Cmd.CR://test
                //HrAction.upload();
               // FindAction.execute(mContext);
                AlertAction.execute(mContext,"warning test!");
                break;
            case Cmd.PHOTO:
                startService(new Intent(MyApplication.CONTEXT, CameraService.class));
                break;
            case Cmd.UPLOAD:
                if(msgArr.size()>3) {
                    int udInterval = Integer.parseInt(msgArr.get(3));
                    if(udInterval>=10 && udInterval<12*3600) {
                        MyApplication.sp.set("upload", udInterval);
                        rspMsg = Cmd.CS + Cmd.SPLIT + imei + Cmd.SPLIT + "UPLOAD";
                        Cmd.send(rspMsg);
                        Intent i = new Intent(MyApplication.CONTEXT, UdLongRunningService.class);
                        MyApplication.CONTEXT.startService(i);
                    }
                }
                break;
            case Cmd.HR:
                mCoreServiceHandler.removeCallbacks(HrAction.hrTimeOut);
                break;
            case Cmd.IP:
                if(IpAction.execute(mContext,info)==0){
                    reconnectNewIp();
                }
                break;
            default:
                break;
        }
    }
}
