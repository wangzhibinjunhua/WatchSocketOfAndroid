package com.xuhao.android.oksocket.wzb.action;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.util.Log;


public class CallAction {
   public static void execute(Context context, String phoneNumber){
       if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED )  {
           // TODO: Consider calling
           //    ActivityCompat#requestPermissions
           // here to request the missing permissions, and then overriding
           //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
           //                                          int[] grantResults)
           // to handle the case where the user grants the permission. See the documentation
           // for ActivityCompat#requestPermissions for more details.
           Log.e("wzb","ACTION_CALL no permission");
           return ;

       }
       Intent intent = new Intent(Intent.ACTION_CALL);
       Uri data = Uri.parse("tel:" + phoneNumber);
       intent.setData(data);

       context.startActivity(intent);


   }
}
