package com.chuangba.homeinn.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.chuangba.homeinn.ui.ConStant;

/**
 * Created by jinyh on 2017/11/30.
 */

public class StartReceiver extends BroadcastReceiver {

    // 软件更新后的重启广播

    private static final String TAG = StartReceiver.class.getSimpleName();

    @Override
    public void onReceive(Context context, Intent intent) {
        //Log.e(TAG,intent.getAction());
        //Log.e(TAG,intent.getData().getSchemeSpecificPart());
       // if (intent.getAction().equalsIgnoreCase(Intent.ACTION_PACKAGE_ADDED)) {
            if (intent.getData().getSchemeSpecificPart().equals(ConStant.PACKAGE_NAME)){

                Intent intent_boot = context.getPackageManager().getLaunchIntentForPackage(ConStant.PACKAGE_NAME);
                context.startActivity(intent_boot);

            }else {
                Log.d(TAG, "add other app " );
            }

            //Intent intent2 = new Intent(context, WelcomeActivity.class);
           // intent2.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);//注意,不能少
           // context.startActivity(intent2);
       // }
    }
}
