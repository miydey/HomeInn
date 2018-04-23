package com.chuangba.homeinn.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.widget.Toast;

import com.chuangba.homeinn.R;

/**
 * Created by jinyh on 2017/10/24.
 */

public class NetworkReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager connectivityManager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
       // NetworkInfo networkInfo = connectivityManager.getNetworkInfo()
        NetworkInfo mNetworkInfo = connectivityManager
                .getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        if (networkInfo!= null&&networkInfo.isAvailable()){
            return;
        }else {
//            AlertDialog.Builder builder = new AlertDialog.Builder(context);
//            builder.setCancelable(false)
//                    .setTitle("网络断开或不可用")
//                    .setMessage("请检查网络状况并重启软件")
//                    .setIcon(R.mipmap.message)
//                    .setNegativeButton("确定",null)
//                    .show();
            Toast.makeText(context, R.string.net_status_changed,Toast.LENGTH_LONG).show();
        }
    }
}
