package com.chuangba.homeinn.ui;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.application.FaceApplication;
import com.chuangba.homeinn.card.HsCardReader;
import com.chuangba.homeinn.handler.CheckVipHandler;
import com.chuangba.homeinn.util.ToastUtil;
import com.huashi.otg.sdk.IDCardInfo;

import java.text.SimpleDateFormat;

/**
 * Created by jinyh on 2018/4/2.
 */

public class CheckVipActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = CheckVipActivity.class.getSimpleName();
    public static final int GET_TIME     = 0;
    public static final int CONNECTED    = GET_TIME + 1;
    public static final int SHOW_CARD    = CONNECTED + 1;
    public static final int TIME_OUT     = SHOW_CARD + 1;
    public static final int DETECT_FACE  = TIME_OUT + 1;
    public static final int NO_FACE      = DETECT_FACE + 1;
    public static final int FACE_CAMERA  = NO_FACE + 1;
    public static final int CLEAR_NO_FACE = FACE_CAMERA + 1;
    public static final int SWIPE_AGAIN   = CLEAR_NO_FACE +1;

    // 华视读卡器
    private HsCardReader hsCardReader;
    //private FaceHandler faceHandler;
    private IDCardInfo IDinfoHs; //华视读卡器的信息
    private CheckVipHandler checkVipHandler;

    //UI
    private LinearLayout linearLayoutInfo;
    private TextView textViewVipName;
    private TextView textViewVipPhone;
    private TextView textViewVipNumber;
    private TextView textViewPayStatus;
    private TextView textViewOrderNumber;
    private TextView textViewVipSubmit;
    private TextView textViewVipCancel;
    ImageView imageIDTip;
    AlertDialog dialogNumber;
    AlertDialog dialogPhone;
    AlertDialog dialogMsg;
    AlertDialog dialogPolice;
    boolean orderExist;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_vip);
        linearLayoutInfo = (LinearLayout) findViewById(R.id.ll_vip_info);
        imageIDTip = (ImageView) findViewById(R.id.iv_id_tip);
        textViewVipName = (TextView) findViewById(R.id.tv_vip_name);
        textViewVipPhone = (TextView) findViewById(R.id.tv_vip_phone);
        textViewVipNumber = (TextView) findViewById(R.id.tv_vip_number);
        textViewPayStatus = (TextView) findViewById(R.id.tv_pay_status);
        textViewOrderNumber = (TextView) findViewById(R.id.tv_order_number);
        textViewVipSubmit = (TextView) findViewById(R.id.tv_vip_submit);
        textViewVipCancel = (TextView) findViewById(R.id.tv_vip_cancel);
        textViewVipSubmit.setOnClickListener(this);
        textViewVipCancel.setOnClickListener(this);
        checkVipHandler = new CheckVipHandler(this);
        initCardReader();
        setIDCardAnimator();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: " );

    }

    private void initCardReader() {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            hsCardReader = new HsCardReader(this);
            hsCardReader.openDevice();
        }

    public void setIDCardAnimator(){
            ObjectAnimator  objectAnimator = ObjectAnimator.ofFloat(imageIDTip,"alpha",1f,0f,1f);
            imageIDTip.setVisibility(View.VISIBLE);
            objectAnimator.setRepeatCount(Animation.INFINITE);
            objectAnimator.setRepeatMode(ValueAnimator.RESTART);
            objectAnimator.setDuration(1500).start();

    }
    public void sendMessageWithData(int message,Object object){
        Message msg = checkVipHandler.obtainMessage();
        msg.what = message;
        msg.obj = object;
        checkVipHandler.sendMessage(msg);
    }

    public void sendMessage(int message){
        checkVipHandler.sendEmptyMessage(message);
    }
    private void showNumberSelect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_check_number,null);
        Button button1 = (Button) layout.findViewById(R.id.bt_check_1);
        Button button2 = (Button) layout.findViewById(R.id.bt_check_2);
        Button button3 = (Button) layout.findViewById(R.id.bt_check_3);
        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        dialogNumber = builder.create();
        dialogNumber.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialogNumber.setView(layout);
        dialogNumber.show();
    }
    public void showCard(IDCardInfo idCardInfo){
        linearLayoutInfo.setVisibility(View.VISIBLE);
        textViewVipName.setText(idCardInfo.getPeopleName());
        textViewVipNumber.setText(idCardInfo.getIDCard());
        textViewVipPhone.setText("1862222222");
        //然后发起请求验证身份证号
        FaceApplication faceApplication = FaceApplication.getInstance();
        SharedPreferences sharedPreferences = faceApplication.getShare();
        orderExist = sharedPreferences.getBoolean("order_status",false);
        if (orderExist){
            textViewOrderNumber.setText("31002158");
            boolean payStatus = sharedPreferences.getBoolean("pay_status",false);
            if (payStatus){
                textViewPayStatus.setText("已支付");
            }else {
                textViewPayStatus.setText("未支付");
            }

        }else {
            //如果没有订单
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            View layout = LayoutInflater.from(this).inflate(R.layout.dialog_vip_message,null);
            builder.setCancelable(false);
            builder.setView(layout);
            TextView textYes = (TextView) layout.findViewById(R.id.tv_msg_submit);
            TextView textNo = (TextView) layout.findViewById(R.id.tv_msg_cancel);
            dialogMsg = builder.create();
            dialogMsg.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
            textYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkPhone();
                    dialogMsg.dismiss();
                }
            });

            textNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialogMsg.dismiss();
                }
            });

            dialogMsg.show();


        }

    }

    private void checkPhone() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_vip_phone,null);
        TextView submit = (TextView) layout.findViewById(R.id.tv_phone_submit);
        TextView cancel = (TextView) layout.findViewById(R.id.tv_phone_cancel);
        final EditText editPhone = (EditText) layout.findViewById(R.id.et_order_phone);
        final EditText editName = (EditText) layout.findViewById(R.id.et_order_name);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogPhone.dismiss();
            }
        });

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = editPhone.getText().toString();
                String name = editName.getText().toString();
                if (phone.length()==11 && name.length()>2){
                    dialogPhone.dismiss();
                    showNumberSelect();
                }else {
                    ToastUtil.showToast(CheckVipActivity.this,"请正确输入预订人信息");
                }

            }
        });
        builder.setCancelable(false);
        dialogPhone = builder.create();
        dialogPhone.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialogPhone.setView(layout);
        dialogPhone.show();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(CheckVipActivity.this,CheckActivity.class);
        int checkNumber;
        switch (v.getId()){
            case R.id.tv_vip_submit:

                showPoliceMsg();

                break;
            case R.id.tv_vip_cancel:
                finish();
                hsCardReader.closeDevice();
                break;
            case R.id.bt_check_1:
                checkNumber = 1;
                intent.putExtra("check_number",checkNumber);
                intent.putExtra("order_exist",false);
                startActivity(intent);
                dialogNumber.dismiss();
                hsCardReader.closeDevice();
                finish();
                break;
            case R.id.bt_check_2:
                checkNumber = 2;
                intent.putExtra("check_number",checkNumber);
                intent.putExtra("order_exist",false);
                startActivity(intent);
                dialogNumber.dismiss();
                hsCardReader.closeDevice();
                finish();
                break;
            case R.id.bt_check_3:
                checkNumber = 3;
                intent.putExtra("check_number",checkNumber);
                intent.putExtra("order_exist",false);
                startActivity(intent);
                dialogNumber.dismiss();
                hsCardReader.closeDevice();
               finish();
                break;
        }

    }

    private void showPoliceMsg() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_police_message,null);
        builder.setView(layout);
        TextView textViewSubmit = (TextView) layout.findViewById(R.id.tv_police_submit);
        dialogPolice = builder.create();
        dialogPolice.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hsCardReader.closeDevice();
                Intent intent = new Intent(CheckVipActivity.this,CheckActivity.class);
                intent.putExtra("check_number",1);
                intent.putExtra("order_exist",orderExist);
                dialogPolice.dismiss();
                startActivity(intent);
                finish();
            }
        });
        dialogPolice.show();
    }
}
