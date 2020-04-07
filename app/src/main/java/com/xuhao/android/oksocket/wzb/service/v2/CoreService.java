package com.xuhao.android.oksocket.wzb.service.v2;


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
import com.xuhao.android.oksocket.wzb.action.v2.ControlAction;
import com.xuhao.android.oksocket.wzb.action.v2.IpAction;
import com.xuhao.android.oksocket.wzb.action.v2.InitAction;
import com.xuhao.android.oksocket.wzb.receiver.v2.ReConnectAlarmReceiver;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.util.LogUtil;

import java.nio.ByteOrder;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import static android.widget.Toast.LENGTH_SHORT;
import static com.xuhao.android.libsocket.sdk.OkSocket.open;

/**
*
* @Author: Zhibin Wang
* @Email: wangzhibin_x@qq.com
* @Time: 2020/3/31 15:12
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
            //context.startService(new Intent(context, LkLongRunningService.class));
            //context.startService(new Intent(context, UdLongRunningService.class));
            InitAction.upload();
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
            String strHead=new String(data.getHeadBytes(), Charset.forName("utf-8"));
            String downNum=strHead.substring(4,7);
            String strBody = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            //logRece(str);
            Log.e("wzb","CoreService/onSocketReadResponse rece:"+strBody);
            parseData(downNum,strBody);
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
        //@B#@002*000002*80@E#@
        //包头(4位)+编号(3位)+内容长度(6位)+内容+包尾(4位)
        @Override
        public int getHeaderLength() {
            return 15;

        }

        @Override
        public int getBodyLength(byte[] header, ByteOrder byteOrder) {
            String headerStr=new String(header);
            LogUtil.logMessage("wzb","headerStr="+headerStr+" length="+headerStr.length());

            if(!Cmd.DATA_START.equals(headerStr.substring(0,4))){
                return 0; //如果包头标识不符合，返回0即可丢弃此次错误数据
             }
            String hexBodyLen=headerStr.substring(8,14);
            LogUtil.logMessage("wzb","hexBodyLen="+hexBodyLen);

            return Integer.valueOf(hexBodyLen,16)+4;

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
                .setHeaderProtocol(mIHeaderProtocolB)
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
        if(mManager == null) {
            LogUtil.logMessage("wzb","v2 connect : mManager is null");
            return;
        }
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

    private void parseData(String downNum,String msg){
        List<String> msgArr= Arrays.asList(msg.split("\\*|,|;",-1));

        switch(downNum){
            case Cmd.SERVER_ACK_NUM:
            {
                String reNum=msgArr.get(0);
                switch (reNum){
                    case Cmd.INIT_NUM:
                        InitAction.ack(mContext,null);
                        break;
                    default:
                        break;
                }
            }
            break;
            case Cmd.IP_NUM:
            {
                if(msgArr.size()==2){
                    if(IpAction.execute(mContext,msgArr.get(0),msgArr.get(1))==0){
                        reconnectNewIp();
                    }
                }
            }
                break;
            case Cmd.CONTROL_NUM:
            {
                ControlAction.execute(mContext,msgArr.get(0));
            }
            break;
            default:
                break;
        }


    }
}
