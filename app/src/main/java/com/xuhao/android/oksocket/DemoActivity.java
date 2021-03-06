package com.xuhao.android.oksocket;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.xuhao.android.oksocket.wzb.camera.CameraWindow;
import com.xuhao.android.oksocket.wzb.util.Cmd;
import com.xuhao.android.oksocket.wzb.service.v2.CoreService;
import com.xuhao.android.oksocket.wzb.util.PermissionUtils;

/**
 * Created by didi on 2018/4/20.
 */

public class DemoActivity extends AppCompatActivity {

    private Button mSimpleBtn;

    private Button mComplexBtn;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity);
        mSimpleBtn = findViewById(R.id.btn1);
        mComplexBtn = findViewById(R.id.btn2);


        mSimpleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, SimpleDemoActivity.class);
                startActivity(intent);
            }
        });
        mComplexBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DemoActivity.this, ComplexDemoActivity.class);
                startActivity(intent);
            }
        });
        initPermission();
        dialogOpenPower();
       // CameraWindow.show(MyApplication.CONTEXT);
        test();
    }

    public void dialogOpenPower(){
        if(Build.VERSION.SDK_INT >= 23)
            if (! Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent,10);
            }
    }

    void test(){
        //Log.e("wzb","battery info:"+Cmd.getBatteryLevel(true));
        String imei= Cmd.IMEI;
        Log.e("wzb","test imei="+imei);
        startService(new Intent(this, CoreService.class));
        //startService(new Intent(this, LkLongRunningService.class));
       // startService(new Intent(this, CameraService.class));
        //UdLongRunningService.packUdInfo("A,-23.22,11.11");
       // String testStr="34123456,feff72387238,22344,feff8bbe5907,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,,";
       // PhbAction.savePhb(testStr);
    }

    private void initPermission(){
        PermissionUtils.requestPermissionsResult(this, 1, new String[]{    //权限提醒
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.WAKE_LOCK,
                Manifest.permission.WRITE_CONTACTS,
                Manifest.permission.READ_CONTACTS,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.SYSTEM_ALERT_WINDOW,
                        Manifest.permission.READ_PHONE_STATE}
                , new PermissionUtils.OnPermissionListener() {
                    @Override
                    public void onPermissionGranted() {

                    }

                    @Override
                    public void onPermissionDenied() {
                        // PermissionUtils.showTipsDialog(CONTEXT);
                    }
                }
        );
    }
}
