package com.chuangba.homeinn.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.countdown.CountdownTime;
import com.chuangba.homeinn.countdown.CountdownView;
import com.chuangba.homeinn.view.CountDownListener;
import com.chuangba.homeinn.view.PayDialog;

import K720_Package.K720_Serial;

/**
 * Created by jinyh on 2018/3/5.
 */

public class CheckOutActivity extends BaseActivity implements CountDownListener{
    private static String TAG = CheckOutActivity.class.getSimpleName();
    ImageView imageViewCheckOut;
    private int cycleCount = 3;
    CountdownView countdownView;
    CountDownTimer countDownTimer;
    TextView textViewCount;
    ImageView imageViewPre;
    ImageView imageViewExit;
    TextView textViewCheckOut;
    TextView textViewRecycle;
    private  int TIME_OUT = 4;
    private static int Port = 2;
    private static byte MacAddr = 0;
    private Runnable runnableGet;
    Thread threadGetCard;
    AlertDialog dialogOut;

    private boolean getCard = false;
    private void returnToBox(){
        Log.e(TAG,Thread.currentThread().getName());
        int nRet;
        byte[] SendBuf=new byte[3];
        String[] RecordInfo=new String[2];
        SendBuf[0] = 0x44;
        SendBuf[1] = 0x42;
        nRet = K720_Serial.K720_SendCmd(MacAddr, SendBuf, 2, RecordInfo);
        if(nRet == 0){
            Log.e("回到卡箱","success");

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    textViewCheckOut.setText("退房成功");
                    //countDownTimer.cancel();
                    CheckOutActivity.this.finish();
                }
            });

        }

            //ShowMessage("回收到卡箱命令执行成功");
        else
            Log.e("回到卡箱","fail");
            //ShowMessage("回收到卡箱命令执行失败");
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (getCard == false)
        getCard = true;
        if (threadGetCard!=null){
            threadGetCard.interrupt();
            try {
                threadGetCard.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            //reset();//复位吐卡机
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out);
        CountdownTime.onTimeCountdownOverListener = new CountdownTime.OnTimeCountdownOverListener() {
            @Override
            public void onTimeCountdownOver() {
                Log.e("Blin QueueMangerOver","回调了");
                CheckOutActivity.this.finish();
            }
        };
        countdownView = (CountdownView) findViewById(R.id.cv_check_out);
        countdownView.setCountdownTime(70,"1");
        textViewCheckOut = (TextView) findViewById(R.id.tv_checkOut);
        imageViewCheckOut = (ImageView) findViewById(R.id.iv_check_out_card);
        //textViewCount = (TextView) findViewById(R.id.tv_count_check_out);
        //imageViewPre = (ImageView) findViewById(R.id.tv_pre_step);
        imageViewExit = (ImageView) findViewById(R.id.iv_exit);
        textViewRecycle = (TextView) findViewById(R.id.tv_recycle);//模拟收卡按钮
        textViewRecycle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               Intent intent =  getIntent();
                boolean timeIn = intent.getBooleanExtra("time_in",true);
                final int remain = intent.getIntExtra("remain",0);
                if (timeIn){
                    if (remain==0)
                    textViewCheckOut.setText("退房成功");
                    else if (remain==1){
                        textViewCheckOut.setText("退房成功，请持凭条至前台退还余额");
                    }else if (remain==-1){
                        PayDialog payDialog = new PayDialog(CheckOutActivity.this);
                        payDialog.show();
                    }
                }else {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(CheckOutActivity.this);
                    builder.setCancelable(false);
                    View layout = LayoutInflater.from(CheckOutActivity.this).inflate(R.layout.dialog_select_out,null);
                    builder.setView(layout);
                    TextView textExtend = (TextView) layout.findViewById(R.id.tv_select_extend);
                    TextView textOut = (TextView) layout.findViewById(R.id.tv_select_out);
                    textExtend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //跳到续住
                            Intent intent1 = new Intent(CheckOutActivity.this,ExtendStayInfoActivity.class);
                            startActivity(intent1);
                        }
                    });


                    textOut.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (remain == -1 ){
                                dialogOut.dismiss();
                                PayDialog payDialog = new PayDialog(CheckOutActivity.this);
                                payDialog.show();
                            }
                        }
                    });
                    dialogOut = builder.create();
                    dialogOut.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
                    dialogOut.show();
                }
            }
        });
        runnableGet = new Runnable() {
            @Override
            public void run() {
                while (!getCard){
                    int nRet;
                    byte[] StateInfo=new byte[4];
                    String[] RecordInfo=new String[2];
                    nRet = K720_Serial.K720_SensorQuery(MacAddr, StateInfo, RecordInfo);
                    Log.e(TAG, "获取传感器状态 " );
                    if(nRet == 0){
                        String code = Integer.toHexString(StateInfo[3] & 0xFF).toUpperCase();
                        Log.e(TAG, code );
                        if ((code.equals("33"))){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    imageViewExit.setVisibility(View.INVISIBLE);
                                    imageViewPre.setVisibility(View.INVISIBLE);
                                    textViewCheckOut.setText("正在退房");
                                }
                            });
                            Log.e(TAG, "getCard" );
                            returnToBox();
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


        //inPutCard();




        imageViewExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOutActivity.this.finish();
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
                //textViewCount.setText(message);
            }

            @Override
            public void onFinish() {
                CheckOutActivity.this.finish();
            }
        };
        //countDownTimer.start();
    }

    private void reset(){

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

    //前端进卡
    private void inPutCard() {
        Log.e(TAG, "开始前端收卡" );
        int nRet;
        byte[] SendBuf = new byte[3];
        String[] RecordInf=new String[2];
        SendBuf[0] = 0x46;
        SendBuf[1] = 0x43;
        SendBuf[2] = 0x38;
        nRet = K720_Serial.K720_SendCmd(MacAddr, SendBuf, 3, RecordInf);
        if(nRet == 0){
            Log.e(TAG, "前端进卡命令执行成功" );


            threadGetCard = new Thread(runnableGet);
            threadGetCard.start();
          //  threadGetCard = new Thread(runnableGet);
          //  threadGetCard.start();
        }
           // ShowMessage("前端进卡命令执行成功");
        else{
            Log.e(TAG, "前端进卡命令执行失败，重新进卡" );
            inPutCard();
        }
    }

    @Override
    public void onFinish(boolean finish) {
        if (finish)
            this.finish();
    }
}
