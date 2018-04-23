package com.chuangba.homeinn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.chuangba.homeinn.R;

/**
 * Created by jinyh on 2018/4/4.
 */

public class CheckOutSetActivity extends BaseActivity {

    RadioGroup radioGroupTimeOut;
    RadioGroup radioGroupRemain;
    RadioGroup radioGroupVipLevel;
    TextView textSubmit;
    TextView textCancel;
    RadioButton radioButtonTimeIn;
    RadioButton radioButtonTimeOut;
    RadioButton radioButtonRemain;
    RadioButton radioButtonNotRemain;
    private boolean timeIn;
    private int remain;//是否有余额  -1，需要加收 0，可退房 1，需退余额
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_out_set);
        textSubmit = (TextView) findViewById(R.id.tv_set_out_submit);
        textCancel = (TextView) findViewById(R.id.tv_set_out_cancel);
        radioGroupTimeOut = (RadioGroup) findViewById(R.id.rg_time_out);
        radioGroupRemain = (RadioGroup) findViewById(R.id.rg_remain);
        radioGroupVipLevel = (RadioGroup) findViewById(R.id.rg_vip_level);
        radioButtonTimeIn = (RadioButton) findViewById(R.id.rb_time_in);
        radioButtonTimeOut = (RadioButton) findViewById(R.id.rb_time_out);
        radioButtonNotRemain = (RadioButton) findViewById(R.id.rb_not_remain);
        radioButtonRemain = (RadioButton) findViewById(R.id.rb_remain);
        radioButtonTimeIn.setChecked(true);
        radioButtonRemain.setChecked(true);
        timeIn = true;

        textSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckOutSetActivity.this,CheckOutActivity.class);
                intent.putExtra("time_in",timeIn);
                intent.putExtra("remain",remain);
                startActivity(intent);
                CheckOutSetActivity.this.finish();
            }
        });

        textCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckOutSetActivity.this.finish();
            }
        });

        radioGroupTimeOut.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId==R.id.rb_time_in){
                    timeIn = true;
                    //未超时，判断余额
                }   else {
                    timeIn = false;
                    //超时，加收房费或者提示续住
                }
            }
        });

        radioGroupRemain.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if(checkedId == R.id.rb_remain)
                {
                    remain = 1;
                    //有余额打凭条，前台退余额
                }else if (checkedId == R.id.rb_remain_0){
                    remain = 0;
                    //余额为0，可退房
                }else {
                    //加收
                    remain = -1;
                }


            }
        });

        radioGroupVipLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                //根据会员等级判断是否超时
            }
        });

    }
}
