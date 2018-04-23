package com.chuangba.homeinn.application;

import android.app.Application;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.task.KeepAliveWatchDog;
import com.squareup.okhttp.Cache;
import com.squareup.okhttp.OkHttpClient;
import com.uuzuche.lib_zxing.activity.ZXingLibrary;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static java.lang.System.currentTimeMillis;

/**
 * Created by jinyh on 2017/5/8.
 */

public class FaceApplication extends Application  {
    static FaceApplication faceApplication;
    private static final String TAG = FaceApplication.class.getSimpleName();
    private String licence;
    OkHttpClient okHttpClient;
    private String customName = "ysck";
    private String customCode = "ysck20170428";
    private SharedPreferences sharedPreferences;
    private static final String LOG_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath() + "/verify/log/";
    private static final String LOG_NAME = getCurrentDateString() + ".txt";
    KeepAliveWatchDog watchDog;
    private boolean keepDeviceAlive;
    @Override
    public void onTerminate() {

        super.onTerminate();
        shutdownWatchDog();
    }

    public static FaceApplication getInstance(){
        return faceApplication;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        ZXingLibrary.initDisplayOpinion(this);
        faceApplication = this;
        okHttpClient = defaultOkHttpClient();
        sharedPreferences = getSharedPreferences(getResources().getString(R.string.storage_key),MODE_PRIVATE);
        //Thread.setDefaultUncaughtExceptionHandler(handler);
        // Intent intent = new Intent(this, RestartService.class); //定时重启
       // bindService(intent, conn, Context.BIND_AUTO_CREATE);

        //发心跳，这个版本去掉
        //initWatchDog();
    }


    public static OkHttpClient defaultOkHttpClient() {
        OkHttpClient client = new OkHttpClient();
       // OkHttpClient.Builder client = new OkHttpClient.Builder();
        client.setConnectTimeout(30 * 1000, TimeUnit.MILLISECONDS);
        client.setReadTimeout(20 * 1000, TimeUnit.MILLISECONDS);

        //设置缓存路径
        File httpCacheDirectory = new File(faceApplication.getCacheDir(), "okhttpCache");
        //设置缓存 10M
        Cache cache = new Cache(httpCacheDirectory, 10 * 1024 * 1024);
        client.setCache(cache);
        //设置拦截器

        //client.addInterceptor(LoggingInterceptor);
        //client.addInterceptor(new ChuckInterceptor(application));
        //client.addNetworkInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        //client.addInterceptor(REWRITE_CACHE_CONTROL_INTERCEPTOR);
        return client;
    }


    Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {

        @Override
        public void uncaughtException(Thread thread, Throwable ex) {
            writeErrorLog(ex);
            Intent intent = getPackageManager().getLaunchIntentForPackage(
                    FaceApplication.this.getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                    Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            System.exit(1);
        }
    };

    public boolean getKeepAlive(){
        return keepDeviceAlive;
    }

    public OkHttpClient getOkHttpClient(){
        return this.okHttpClient;
    }


    public SharedPreferences getShare(){
        return sharedPreferences;
    }

    private void shutdownWatchDog(){
        watchDog.setRun(false);
        watchDog.closeWatchDog();
    }
    protected void writeErrorLog(Throwable ex) {
        String info = null;
        ByteArrayOutputStream baos = null;
        PrintStream printStream = null;
        try {
            baos = new ByteArrayOutputStream();
            printStream = new PrintStream(baos);
            ex.printStackTrace(printStream);
            byte[] data = baos.toByteArray();
            info = new String(data);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            String time = simpleDateFormat.format(currentTimeMillis());
            info = time +"\n"+ info;
            data = null;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (printStream != null) {
                    printStream.close();
                }
                if (baos != null) {
                    baos.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File dir = new File(LOG_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File file = new File(dir, LOG_NAME);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file, true);
            fileOutputStream.write(info.getBytes());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    /**
     * 获取当前日期
     *
     */
    private static String getCurrentDateString() {
        String result = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",
                Locale.getDefault());
        Date nowDate = new Date();
        result = sdf.format(nowDate);
        return result;
    }
}
