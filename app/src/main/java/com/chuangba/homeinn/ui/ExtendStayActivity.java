package com.chuangba.homeinn.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.card.SerialCarder;
import com.chuangba.homeinn.countdown.CountdownTime;
import com.chuangba.homeinn.countdown.CountdownView;

import K720_Package.K720_Serial;

import static com.chuangba.homeinn.util.FileUtil.TAG;

/**
 * Created by jinyh on 2018/3/5.
 */

public class ExtendStayActivity extends BaseActivity {
        SerialCarder serialCarder;
        ImageView imageViewCheckOut;
        private static final String[] StrComPort = {"COM1", "COM1", };
        CountdownView countdownView;
        CountDownTimer countDownTimer;
        TextView textViewCount;
        ImageView imageViewExit;
        private  int TIME_OUT = 4;
        private int time;
        private static int Port = 2;
        private static byte MacAddr = 0;
        private Runnable runnableGet;
        Thread threadGetCard;
        private boolean getCard = false;
    private void returnToBox(){
        int nRet;
        byte[] SendBuf=new byte[3];
        String[] RecordInfo=new String[2];
        SendBuf[0] = 0x44;
        SendBuf[1] = 0x42;
        nRet = K720_Serial.K720_SendCmd(MacAddr, SendBuf, 2, RecordInfo);
        if(nRet == 0){
            Log.e("回到卡箱","success");

        }
            //ShowMessage("回收到卡箱命令执行成功");
        else
            Log.e("回到卡箱","fail");
        //ShowMessage("回收到卡箱命令执行失败");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
       // countDownTimer.cancel();
        getCard = true;
        if (threadGetCard!=null){
            threadGetCard.interrupt();
            try {
                threadGetCard.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        serialCarder.reset();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend);

        CountdownTime.onTimeCountdownOverListener = new CountdownTime.OnTimeCountdownOverListener() {
            @Override
            public void onTimeCountdownOver() {
                ExtendStayActivity.this.finish();
            }
        };
        serialCarder = SerialCarder.getInstance(this);
        runnableGet = new Runnable() {
            @Override
            public void run() {
                while (!getCard){
                    int nRet;
                    byte[] StateInfo=new byte[4];
                    String[] RecordInfo=new String[2];
                    nRet = K720_Serial.K720_SensorQuery(MacAddr, StateInfo, RecordInfo);
                    if(nRet == 0){
                        String code = Integer.toHexString(StateInfo[3] & 0xFF).toUpperCase();
                        Log.e(TAG, code );
                        if ((code.equals("33"))){
                            //这里应有写卡，然后返回卡箱
                            if (serialCarder.returnToBox()){
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Intent intent = new Intent(ExtendStayActivity.this,ExtendStayInfoActivity.class);
                                        startActivity(intent);
                                    }
                                });
                            }
                            getCard = true;
                        }
                        else{
                            try {
                                Thread.sleep(300);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            Log.e("fail",K720_Serial.ErrorCode(nRet, 0));
                        }
                    }

                }

            }
        };


        inputCard();
        //ShowMessage("前端进卡命令执行失败");
        threadGetCard = new Thread(runnableGet);
        threadGetCard.start();
        countdownView = (CountdownView) findViewById(R.id.cv_check_out);
        countdownView.setCountdownTime(60,"3");
        imageViewCheckOut = (ImageView) findViewById(R.id.iv_check_out_card);
        textViewCount = (TextView) findViewById(R.id.tv_count_check_out);
        imageViewExit = (ImageView) findViewById(R.id.iv_exit);

        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ExtendStayActivity.this.finish();
            }
        });

        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageViewCheckOut,"translationY",-120f);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setDuration(2500).start();

        countDownTimer = new CountDownTimer(30*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                final String count = getResources().getString(R.string.count);
                String time = String.valueOf(millisUntilFinished/1000);
                String message = String.format(count,time);
               // textViewCount.setText(message);
            }

            @Override
            public void onFinish() {
                ExtendStayActivity.this.finish();
            }
        };
        //countDownTimer.start();
    }

    private void inputCard() {
        int nRet;
        byte[] SendBuf = new byte[3];
        String[] RecordInf=new String[2];
        SendBuf[0] = 0x46;
        SendBuf[1] = 0x43;
        SendBuf[2] = 0x38;
        nRet = K720_Serial.K720_SendCmd(MacAddr, SendBuf, 3, RecordInf);
        if(nRet == 0){
            Log.e(TAG, "前端进卡命令执行成功" );
            //  threadGetCard = new Thread(runnableGet);
            //  threadGetCard.start();
        }
        // ShowMessage("前端进卡命令执行成功");
        else{

        }
    }

}
