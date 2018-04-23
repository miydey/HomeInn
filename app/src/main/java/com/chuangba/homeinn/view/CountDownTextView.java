package com.chuangba.homeinn.view;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.widget.TextView;

/**
 * Created by jinyh on 2017/11/1.
 */

public class CountDownTextView extends CountDownTimer{

    /**
     * 倒计时控件
     */
    public static final int TIME_COUNT = 180*1000;//倒计时总时间为180S
    private TextView textView;
    private Fragment fragment;
    Context context;
    CountDownListener countDownListener;
    public static String TAG = CountDownTextView.class.getSimpleName();

    /**
     * @param millisInFuture    The number of millis in the future from the call
     *                          to {@link #start()} until the countdown is done and {@link #onFinish()}
     *                          is called.
     * @param countDownInterval The interval along the way to receive
     *                          {@link #onTick(long)} callbacks.
     */
    public CountDownTextView(long millisInFuture, long countDownInterval, TextView textView, Fragment fragment) {
        super(millisInFuture, countDownInterval);
        this.fragment = fragment;
        this.textView = textView;
    }


    public void setCountDownListener(CountDownListener countDownListener){
        this.countDownListener = countDownListener;
    }

    public CountDownTextView(long millisInFuture, long countDownInterval, TextView textView, Context context) {
        super(millisInFuture, countDownInterval);
        this.context = context;
        this.textView = textView;
    }
    @Override
    public void onTick(long millisUntilFinished) {
        textView.setEnabled(false);
        textView.setText(String.valueOf(millisUntilFinished/1000)+"秒后自动退出");
//        if (millisUntilFinished/1000==10){
//            MainActivity mainActivity = (MainActivity) context;
//            mainActivity.sendQueryAli();
//        }
    }

    @Override
    public void onFinish() {
        textView.setEnabled(true);
        //MainActivity mainActivity = (MainActivity) context;
       // mainActivity.cancelPay();
        this.cancel();
        countDownListener.onFinish(true);
        Log.e(TAG, "onFinish: " );
        //textView.setText("取消");
    }


}
