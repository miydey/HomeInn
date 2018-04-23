package com.chuangba.homeinn.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.bean.OrderInfo;
import com.chuangba.homeinn.fragment.CardFragment;
import com.chuangba.homeinn.fragment.OrderFragment;
import com.chuangba.homeinn.fragment.PayFragment;
import com.chuangba.homeinn.fragment.RoomFragment;
import com.chuangba.homeinn.fragment.VerifyFragment;

/**
 * Created by jinyh on 2018/2/28.
 */

public class CheckActivity extends FragmentActivity {
    private static String TAG = CheckActivity.class.getSimpleName();
    private Fragment[] fragments;
    private TextView[] textviewSteps;
    private TextView[] textViewDetails;
    private String name;
    private OrderInfo orderInfo;
    private FrameLayout frameLayout;
    VerifyFragment verifyFragment;
    OrderFragment orderFragment;
    RoomFragment roomFragment;
    PayFragment payFragment;
    CardFragment cardFragment;
    ImageView textPreStep;
    ImageView textExit;
    TextView textView1;
    TextView textView2;
    TextView textView3;
    TextView textView4;
    TextView textView5;
    TextView textViewDetail1;
    TextView textViewDetail2;
    TextView textViewDetail3;
    TextView textViewDetail4;
    TextView textViewDetail5;
    boolean orderExist;
    private int index;// 表示步骤的编号
    private int currentTabIndex;//当前编号

    @Override
    protected void onDestroy() {
        super.onDestroy();
       // K720_Serial.K720_CommClose();
    }

    @Override

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_check);
        frameLayout = (FrameLayout) findViewById(R.id.framelayout_main);
        textPreStep = (ImageView) findViewById(R.id.tv_pre_step);
        int checkNumber = getIntent().getIntExtra("check_number",1);
        orderExist = getIntent().getBooleanExtra("order_exist",false);
        textView1 = (TextView) findViewById(R.id.tv_step1);
        textView2 = (TextView) findViewById(R.id.tv_step2);
        textView3 = (TextView) findViewById(R.id.tv_step3);
        textView4 = (TextView) findViewById(R.id.tv_step4);
        textView5 = (TextView) findViewById(R.id.tv_step5);

        textViewDetail1 = (TextView) findViewById(R.id.tv_tab_verify);
        textViewDetail2 = (TextView) findViewById(R.id.tv_tab_order);
        textViewDetail3 = (TextView) findViewById(R.id.tv_tab_pay);
        textViewDetail4 = (TextView) findViewById(R.id.tv_tab_check);
        textViewDetail5 = (TextView) findViewById(R.id.tv_tab_card);

        textviewSteps = new TextView[5];
        textviewSteps[0] = textView1;
        textviewSteps[1] = textView2;
        textviewSteps[2] = textView3;
        textviewSteps[3] = textView4;
        textviewSteps[4] = textView5;
        textViewDetails = new TextView[5];
        textViewDetails[0] = textViewDetail1;
        textViewDetails[1] = textViewDetail2;
        textViewDetails[2] = textViewDetail3;
        textViewDetails[3] = textViewDetail4;
        textViewDetails[4] = textViewDetail5;

        textExit = (ImageView) findViewById(R.id.iv_exit);
        textExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckActivity.this.finish();
            }
        });
        textPreStep.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               int index = currentTabIndex - 1;
                if (currentTabIndex <= 0){
                    CheckActivity.this.finish();
                }
                else {
                    setTab(index);
                }


            }
        });
        verifyFragment = new VerifyFragment();
        verifyFragment.setCheckNumber(checkNumber);
        orderFragment = new OrderFragment();
        roomFragment = new RoomFragment();
        payFragment = new PayFragment();
        cardFragment = new CardFragment();
        fragments = new Fragment[5];
        fragments[0] = verifyFragment;
        fragments[1] = orderFragment;
        fragments[2] = roomFragment;
        fragments[3] = payFragment;
        fragments[4] = cardFragment;
        getSupportFragmentManager().beginTransaction()
                .add(R.id.framelayout_main, verifyFragment)
                //.add(R.id.framelayout_main, orderFragment)
                //.add(R.id.framelayout_main, roomFragment)
                //.add(R.id.framelayout_main, payFragment)
                //.add(R.id.framelayout_main, cardFragment)
                //.hide(cardFragment)
                //.hide(orderFragment)
                //.hide(roomFragment)
                //.hide(payFragment)
                .show(verifyFragment)
                .commit();
        currentTabIndex = 0;
        textviewSteps[currentTabIndex].setTextColor(Color.parseColor("#f5831b"));
        textViewDetails[currentTabIndex].setTextColor(Color.parseColor("#f5831b"));
    }

    public boolean getOrderExist(){
        return this.orderExist;
    }

public void setTab(int index){
    Log.e(TAG, "setTab:index "+ index );
    Log.e(TAG, "setTab:index "+ currentTabIndex );
    if (index ==1){
        textPreStep.setVisibility(View.INVISIBLE);
    }else {
        textPreStep.setVisibility(View.VISIBLE);
    }
    if (currentTabIndex != index) {
        FragmentTransaction trx = getSupportFragmentManager()
                .beginTransaction();
        trx.hide(fragments[currentTabIndex]);
        //trx.remove(fragments[currentTabIndex]);
        if (!fragments[index].isAdded()) {
            trx.add(R.id.framelayout_main, fragments[index]);
        }
        trx.show(fragments[index]).commit();
        for (int i = 0;i<5;i++){
            textviewSteps[i].setTextColor(Color.parseColor("#ffffff"));
            textViewDetails[i].setTextColor(Color.parseColor("#ffffff"));
            if (i==index){
                textviewSteps[i].setTextColor(Color.parseColor("#f5831b"));
                textViewDetails[i].setTextColor(Color.parseColor("#f5831b"));
            }

        }
        currentTabIndex = index;
    }

}

 public void setOrderInfo(OrderInfo orderInfo){
     this.orderInfo = orderInfo;
 }

 public OrderInfo getOrderInfo(){
     return this.orderInfo;
 }

    public void setName(String name){
        this.name = name;
    }
    public String getName(){
        return this.name;
    }
}
