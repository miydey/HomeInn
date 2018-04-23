package com.chuangba.homeinn.card;

import android.content.Context;
import android.util.Log;

import com.chuangba.homeinn.util.ToastUtil;

import K720_Package.K720_Serial;

/**
 * Created by jinyh on 2018/3/13.
 * 串口发卡器
 */

public class SerialCarder {

    private boolean getCard = false;
    private Runnable runnableGet;
    Thread threadGetCard;
    private static String TAG = SerialCarder.class.getSimpleName();
    byte MacAddr = 0;
    Context context;
    private static SerialCarder instance;
    boolean deviceExist;
    private SerialCarder(Context context) {
        this.context = context;
    }

    public static SerialCarder getInstance(Context context) {
        if (instance == null) {
            instance = new SerialCarder(context);
            return instance;
        }
        return instance;
    }


    private void showMessage(String message) {
        ToastUtil.showToast(this.context, message);
    }


    public void closeDevice(){
        if (deviceExist){
            K720_Serial.K720_CommClose();
            Log.e(TAG,"device is closed");
        }

    }


    public void openSerialDevice() {
        String StrPort = "/dev/ttyS1";//这个每个不同的测试机产品，文件位置不一样
        int re = 0;
        byte i;
        String[] RecordInfo = new String[2];
        int Baudate = 9600;
        re = K720_Serial.K720_CommOpen(StrPort);
        if (re == 0) {
            for (i = 0; i < 16; i++) {
                re = K720_Serial.K720_AutoTestMac(i, RecordInfo);
                if (re == 0) {
                    MacAddr = i;
                    Log.e(TAG, "macaddr" + MacAddr);
                    break;
                }
            }
            if (i == 16 && MacAddr == 0) {
                Log.e(TAG, "onCreate:连接失败 ");
                showMessage("设备连接失败");
                deviceExist = false;
            } else {
                Log.e(TAG, "onCreate:设备连接成功 ");
                 deviceExist = true;
            }
        } else {
            deviceExist = false;
            showMessage("串口打开错误");

        }
    }


    //卡片移动到取卡位置
    private boolean getCard() {

        int nRet;
        byte[] SendBuf = new byte[3];
        String[] RecordIn = new String[2];
        SendBuf[0] = 0x46;
        SendBuf[1] = 0x43;
        SendBuf[2] = 0x34;
        nRet = K720_Serial.K720_SendCmd(MacAddr, SendBuf, 3, RecordIn);
        if (nRet == 0) {
            Log.e(TAG, "getCard: ");
//            CheckActivity activity = (CheckActivity) getActivity();
//            activity.finish();
            return true;
        } else
            Log.e(TAG, "getCard: fail ");
        return false;

    }


    private void inPutCard() {
        Log.e(TAG, "开始前端收卡");
        int nRet;
        byte[] SendBuf = new byte[3];
        String[] RecordInf = new String[2];
        SendBuf[0] = 0x46;
        SendBuf[1] = 0x43;
        SendBuf[2] = 0x38;
        nRet = K720_Serial.K720_SendCmd(MacAddr, SendBuf, 3, RecordInf);
        if (nRet == 0) {
            Log.e(TAG, "前端进卡命令执行成功");


            threadGetCard = new Thread(runnableGet);
            threadGetCard.start();
            //  threadGetCard = new Thread(runnableGet);
            //  threadGetCard.start();
        }
        // ShowMessage("前端进卡命令执行成功");
        else {
            Log.e(TAG, "前端进卡命令执行失败，重新进卡");
            inPutCard();
        }
    }


    private void getSensorStatus() {
        int nRet;
        byte[] StateInfo = new byte[4];
        String[] RecordInfo = new String[2];
        nRet = K720_Serial.K720_SensorQuery(MacAddr, StateInfo, RecordInfo);
        Log.e(TAG, "获取传感器状态 ");
        if (nRet == 0) {
            String code = Integer.toHexString(StateInfo[3] & 0xFF).toUpperCase();
            Log.e(TAG, code);
            if ((code.equals("33"))) {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        imageViewExit.setVisibility(View.INVISIBLE);
//                        imageViewPre.setVisibility(View.INVISIBLE);
//                        textViewCheckOut.setText("正在退房");
//                    }
//                });
                Log.e(TAG, "getCard");
                returnToBox();
                getCard = true;
            } else {
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.e("fail", K720_Serial.ErrorCode(nRet, 0));
            }
        }
    }

    public boolean returnToBox() {
        Log.e(TAG, Thread.currentThread().getName());
        int nRet;
        byte[] SendBuf = new byte[3];
        String[] RecordInfo = new String[2];
        SendBuf[0] = 0x44;
        SendBuf[1] = 0x42;
        nRet = K720_Serial.K720_SendCmd(MacAddr, SendBuf, 2, RecordInfo);
        if (nRet == 0) {
            Log.e(TAG, "回到卡箱：success");
            return true;
        } else {
            Log.e(TAG, "回到卡箱：fail");
            return false;
        }
    }


    private void stopGetStatusThread(){
        if (getCard == false)
            getCard = true;
        if (threadGetCard!=null){
            threadGetCard.interrupt();
            try {
                threadGetCard.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }
    }


    public void reset(){ //发卡机复位，用于开启收卡但长时间没有插入卡片的情况

        int nRet;
        byte[] SendBuf=new byte[3];
        String[] RecordInfo=new String[2];
        SendBuf[0] = 0x52;
        SendBuf[1] = 0x53;
        nRet = K720_Serial.K720_SendCmd(MacAddr, SendBuf, 2, RecordInfo);
        if(nRet == 0)
            Log.e(TAG,"复位成功");
        else
            Log.e(TAG,"复位失败");;
    }
}


