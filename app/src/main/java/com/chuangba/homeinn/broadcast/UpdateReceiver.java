package com.chuangba.homeinn.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;

import com.chuangba.homeinn.R;

import java.io.File;

/**
 * Created by jinyh on 2017/11/22.
 */

public class UpdateReceiver extends BroadcastReceiver {

    Context context;
    String version;
    public UpdateReceiver(Context context){
        this.context = context;
    }

    public void setVersion(String version){
        this.version = version;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this.context);
        builder.setNegativeButton(R.string.cancel,null)
                .setTitle(R.string.download_finished)
                .setMessage(R.string.install_right_now)
                .setCancelable(false)
                .setPositiveButton(R.string.submit, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        install();
                    }
                })
                .show();
    }
    private  void install() {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        String appName = "update"+version+".apk";

        intent.setDataAndType(Uri.fromFile(new File(Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getPath(), appName)),
                "application/vnd.android.package-archive");
        context.startActivity(intent);

        //发送定时广播 安装之后重启
//        Intent ite = new Intent(context, StartReceiver.class);
//        ite.setAction(Intent.ACTION_PACKAGE_ADDED);
//        PendingIntent SENDER = PendingIntent.getBroadcast(context, 0, ite,
//                PendingIntent.FLAG_CANCEL_CURRENT);
//        Log.e("---------", "install: " );
//        AlarmManager ALARM = (AlarmManager) context.getSystemService(ALARM_SERVICE);
//        ALARM.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 5000,
//                SENDER);

    }

}
