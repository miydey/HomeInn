package com.chuangba.homeinn.ui;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.application.FaceApplication;
import com.chuangba.homeinn.bean.GuestConfig;

/**
 * Created by jinyh on 2018/4/3.
 */

public class CheckInSetActivity extends Activity {

    RadioGroup radioGroupOrder;//是否有订单
    RadioButton radioButtonOrderExist;
    RadioButton radioButtonOrderNotExist;
    RadioGroup radioGroupVipLevel;//会员等级
    RadioGroup radioGroupPay;//支付状态

    RadioButton radioButtonPaid;
    RadioButton radioButtonNotPaid;
    GuestConfig guestConfig;
    TextView textSubmit;
    TextView textCancel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check_in_set);
        radioGroupOrder = (RadioGroup) findViewById(R.id.rg_order);
        radioButtonOrderExist = (RadioButton) findViewById(R.id.rb_order_exist);
        radioButtonOrderNotExist = (RadioButton) findViewById(R.id.rb_order_no);
        radioGroupPay = (RadioGroup) findViewById(R.id.rg_pay);
        radioButtonPaid = (RadioButton) findViewById(R.id.rb_paid);
        radioButtonNotPaid = (RadioButton) findViewById(R.id.rb_not_paid);
        radioGroupVipLevel = (RadioGroup) findViewById(R.id.rg_vip_level);
        textSubmit = (TextView) findViewById(R.id.tv_set_submit);
        textCancel = (TextView) findViewById(R.id.tv_set_cancel);
        textSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckInSetActivity.this,CheckVipActivity.class);
                startActivity(intent);
                FaceApplication faceApplication = FaceApplication.getInstance();
                SharedPreferences sharedPreferences = faceApplication.getShare();
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putBoolean("order_status",guestConfig.isOrderExist());
                editor.putBoolean("pay_status",guestConfig.isPaid());
                editor.commit();
            }
        });
        radioButtonOrderExist.setChecked(true);
        guestConfig = new GuestConfig();
        guestConfig.setOrderExist(true);
        radioGroupOrder.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_order_exist){
                    radioButtonPaid.setEnabled(true);
                    radioButtonNotPaid.setEnabled(true);
                    radioButtonNotPaid.setTextColor(getResources().getColor(R.color.black));
                    radioButtonPaid.setTextColor(getResources().getColor(R.color.black));
                    guestConfig.setOrderExist(true);

                }else {

                    radioButtonPaid.setEnabled(false);
                    radioButtonNotPaid.setEnabled(false);
                    radioButtonPaid.setChecked(false);
                    radioButtonNotPaid.setChecked(false);
                    radioButtonNotPaid.setTextColor(getResources().getColor(R.color.grgray));
                    radioButtonPaid.setTextColor(getResources().getColor(R.color.grgray));
                    guestConfig.setOrderExist(false);
                }
            }
        });

        radioGroupPay.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_not_paid){
                    guestConfig.setPaid(false);
                }else {
                    guestConfig.setPaid(true);
                }
            }
        });
        radioGroupVipLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
                    case R.id.rb_not_vip:
                        guestConfig.setVipLevel(0);
                        break;
                    case R.id.rb_vip_home:
                        guestConfig.setVipLevel(1);
                        break;
                    case R.id.rb_vip_gold:
                        guestConfig.setVipLevel(2);
                        break;
                    case R.id.rb_vip_platinum:
                        guestConfig.setVipLevel(3);
                        break;
                    case R.id.rb_vip_diamond:
                        guestConfig.setVipLevel(4);
                        break;

                }
            }
        });

    }
}
