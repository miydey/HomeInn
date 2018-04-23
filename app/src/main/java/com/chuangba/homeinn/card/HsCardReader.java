package com.chuangba.homeinn.card;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import com.chuangba.homeinn.ui.CheckVipActivity;
import com.chuangba.homeinn.bean.HistoryInfo;
import com.chuangba.homeinn.fragment.VerifyFragment;
import com.huashi.otg.sdk.HSInterface;
import com.huashi.otg.sdk.HsOtgService;
import com.huashi.otg.sdk.IDCardInfo;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.chuangba.homeinn.fragment.VerifyFragment.CLEAR_NO_FACE;


/**
 * Created by jinyh on 2017/9/8.
 * 华视读卡器
 */

public class HsCardReader implements FaceExistCallback {
    private static final String TAG = HsCardReader.class.getSimpleName();
    private CardConnection conn;
    private HSInterface hSinterface;
    private IDCardInfo IDInfoHs; //华视的信息
    private Intent service;
    private String filepath = "";
    VerifyFragment verifyFragment;
    Context activity;
    private int haveID ;  //读卡器读到照片
    private int waitTime; //等待的秒数
    private long firstScan;
    private boolean connect ;
    Thread findCardThread;
    boolean isReading;
    boolean faceExist;
    public HsCardReader(VerifyFragment verifyFragment){
        this.verifyFragment = verifyFragment;
        activity = verifyFragment.getActivity();
    }


    public HsCardReader(Context context){
        activity = context;
    }

    @Override
    public void getFaceNumber(boolean faceExist) {
        this.faceExist = faceExist;
    }

    //读卡器连接
    private class CardConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            hSinterface = (HSInterface) service;
            if (hSinterface == null){
                Log.e(TAG, "onServiceConnected: bind fail " );
            }
            int i = 5;
            while (i > 0) {
                i--;
                int ret = hSinterface.init();
                Log.e(TAG, "HSinterface init: "+ret );
                if (ret == 1) {
                    i = 0;
                    String SAM_ID =  hSinterface.GetSAM();
                    connect = true;
                    isReading = true;
                    findCard();
                    if (verifyFragment!=null)
                    verifyFragment.sendMessage(VerifyFragment.CONNECTED);
                    return;
                } else {
                    try {
                        Thread.sleep(400);
                        i++;
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    continue;
                }
            }

        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            conn = null;
            hSinterface = null;
        }

}
    public void openDevice(){
        filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 读卡器授权目录
        service = new Intent(activity, HsOtgService.class);


        conn = new CardConnection();
        activity.bindService(service, conn, Service.BIND_AUTO_CREATE);
    }

    public void closeDevice(){
        if (connect){
            Log.e(TAG, "closeDevice: " );
            if (hSinterface!= null){
                isReading = false;
                hSinterface.unInit();
                activity.stopService(service);
                activity.unbindService(conn);
                conn = null;
                findCardThread.interrupt();

                try {
                    findCardThread.join();
                    hSinterface = null;
                } catch (InterruptedException e) {
                    Log.e(TAG, e.toString() );
                }
            }

        }
    }

    private void readCard(){
        if (hSinterface == null)
            return;
        long time = System.currentTimeMillis();
        int ret = hSinterface.ReadCard();
        if (ret == 1){
            IDInfoHs = HsOtgService.ic;
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd" );

            String endDate = IDInfoHs.getEndDate();
            try {
                Date date_end = sdf.parse(endDate);
                Calendar calendar = Calendar.getInstance();
                if (date_end.getTime() - calendar.getTimeInMillis() < 0 &&(!endDate.substring(0,1).equals("长"))){
                        verifyFragment.sendMessage(VerifyFragment.TIME_OUT);
                    haveID = 0;
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // String endDate = "2016.03.22"; //证件过期测试

            try {
                ret = hSinterface.Unpack();// 照片解码
                if (ret != 0) {// 读卡失败
                    Log.e(TAG, "readCard: unpack fail" );
                    return;
                }
                FileInputStream fis = new FileInputStream(filepath + "/zp.bmp");
                Bitmap bmpId = BitmapFactory.decodeStream(fis);
                fis.close();
                haveID = 1;
                while (haveID == 1){
                    if (faceExist){
                        HistoryInfo historyInfo = new HistoryInfo();
                        historyInfo.setBitmap(bmpId);
                        historyInfo.setIdCardInfo(IDInfoHs);
                        verifyFragment.sendMessageWithData(VerifyFragment.SHOW_CARD,historyInfo);
                        waitTime = 0;
                        haveID = 0;
                    }else {
                        verifyFragment.sendMessage(VerifyFragment.FACE_CAMERA);
                        while (!faceExist){  //无人脸的时候等待
                            waitTime++;
                            Thread.sleep(500);
                            if (waitTime >= 10){
                                haveID = 0;
                                verifyFragment.sendMessage(CLEAR_NO_FACE);
                                waitTime = 0;
                                break;
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                Toast.makeText(activity, "头像不存在！", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(activity, "头像读取错误", Toast.LENGTH_SHORT).show();
            }catch (Exception e)
            {
                Toast.makeText(activity, "头像解码失败", Toast.LENGTH_SHORT).show();
            }finally {
                HsOtgService.ic = null;
            }
        }else {
            Log.e("readCard fail", String.valueOf(System.currentTimeMillis()-time) );

            verifyFragment.sendMessage(VerifyFragment.SWIPE_AGAIN);
        }
    }

    private void readCardForVip(){
        CheckVipActivity checkVipActivity = (CheckVipActivity) activity;
        if (hSinterface == null)
            return;
        long time = System.currentTimeMillis();
        int ret = hSinterface.ReadCard();
        if (ret == 1){
           IDCardInfo IDInfoHs = HsOtgService.ic;
            SimpleDateFormat sdf = new SimpleDateFormat( "yyyy.MM.dd" );
            String endDate = IDInfoHs.getEndDate();
            try {
                Date date_end = sdf.parse(endDate);
                Calendar calendar = Calendar.getInstance();
                if (date_end.getTime() - calendar.getTimeInMillis() < 0 &&(!endDate.substring(0,1).equals("长"))){
                    checkVipActivity.sendMessage(CheckVipActivity.TIME_OUT);
                    haveID = 0;
                    return;
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // String endDate = "2016.03.22"; //证件过期测试

            try {
                ret = hSinterface.Unpack();// 照片解码
                if (ret != 0) {// 读卡失败
                    Log.e(TAG, "readCard: unpack fail" );
                    return;
                }
                FileInputStream fis = new FileInputStream(filepath + "/zp.bmp");
                Bitmap bmpId = BitmapFactory.decodeStream(fis);
                fis.close();

//                HistoryInfo historyInfo = new HistoryInfo();
//                historyInfo.setBitmap(bmpId);
//                historyInfo.setIdCardInfo(IDInfoHs);
                checkVipActivity.sendMessageWithData(CheckVipActivity.SHOW_CARD,IDInfoHs);

            } catch (FileNotFoundException e) {
                Toast.makeText(activity, "头像不存在！", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                Toast.makeText(activity, "头像读取错误", Toast.LENGTH_SHORT).show();
            }catch (Exception e)
            {
                Toast.makeText(activity, "头像解码失败", Toast.LENGTH_SHORT).show();
            }finally {
                HsOtgService.ic = null;
            }
        }else {
            Log.e("readCard fail", String.valueOf(System.currentTimeMillis()-time) );

            checkVipActivity.sendMessage(CheckVipActivity.SWIPE_AGAIN);
        }
    }


    private void findCard(){

        //开启读卡线程，轮巡扫卡
        findCardThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (isReading){
                    if (hSinterface == null)
                        return;
                    int ret = hSinterface.Authenticate();
                    if (ret == 1){
                        if (System.currentTimeMillis() - firstScan > 1500){ //防抖动
                            // soundPool.play(4, 1, 1, 0, 0, 1);
                            if (verifyFragment!=null){
                                readCard();//人脸比对
                            }else
                                readCardForVip();//只验证身份证号码

                            firstScan = System.currentTimeMillis();
                        } else{
                            firstScan = System.currentTimeMillis();
                        }

                    }else if (ret == 2){
                        Log.d(TAG, " no IDCard" );
                    }else if (ret == 0){
                        Log.d(TAG, "no reader connect" );
                    }
                    try {
                        Thread.sleep(400);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        findCardThread.start();

    }
}
