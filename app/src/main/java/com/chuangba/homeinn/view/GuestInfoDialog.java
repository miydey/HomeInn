package com.chuangba.homeinn.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StyleRes;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangba.homeinn.ui.CheckActivity;
import com.chuangba.homeinn.R;
import com.huashi.otg.sdk.IDCardInfo;

import java.util.ArrayList;

/**
 * Created by jinyh on 2018/4/10.
 */

public class GuestInfoDialog extends AlertDialog {
    Context context;
    TextView textViewSubmit;
    TextView textViewId1 ;
    TextView textViewId2 ;
    TextView textViewName1 ;
    TextView textViewName2 ;
    ImageView imageViewRst2;
    ImageView imageViewRst3;
    ArrayList<IDCardInfo> idCardInfos = new ArrayList<>();
    TextView textViewAddGuest2;
    TextView textViewVip2;


    public GuestInfoDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    protected GuestInfoDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }


    public void initData(ArrayList<IDCardInfo> idCardInfos){
        this.idCardInfos = idCardInfos;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_guest);
        CheckActivity checkActivity = (CheckActivity) context;
        WindowManager m = checkActivity.getWindowManager();
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.95);
        p.width = (int) (d.getWidth() * 0.9);

        getWindow().setAttributes(p);
        getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        textViewSubmit = (TextView) findViewById(R.id.tv_guest_submit);
        textViewId1 = (TextView) findViewById(R.id.tv_id1);
        textViewVip2 = (TextView) findViewById(R.id.tv_vip2);
        textViewName1 = (TextView) findViewById(R.id.tv_name1);
        textViewId2 = (TextView) findViewById(R.id.tv_id2);
        textViewName2 = (TextView) findViewById(R.id.tv_name2);
        imageViewRst2 = (ImageView) findViewById(R.id.iv_rst2);
        textViewAddGuest2 = (TextView) findViewById(R.id.tv_add_guest2);
        textViewId1.setText(idCardInfos.get(0).getIDCard());
        textViewName1.setText("姓名："+idCardInfos.get(0).getPeopleName());

        if (idCardInfos.size()==2){
            imageViewRst2.setVisibility(View.VISIBLE);
            textViewId2.setText(idCardInfos.get(1).getIDCard());
            textViewAddGuest2.setText("清除信息");
            textViewVip2.setText("钻石会员");
            textViewName2.setText("姓名："+idCardInfos.get(1).getPeopleName());
        }

        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CheckActivity checkActivity = (CheckActivity) context;
                boolean orderExist = checkActivity.getOrderExist();
                if (orderExist){
                    checkActivity.setTab(3);

                }else {
                    checkActivity.setTab(1);
                    checkActivity.setName(idCardInfos.get(0).getPeopleName());
                }
                GuestInfoDialog.this.dismiss();
            }
        });

        textViewAddGuest2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                GuestInfoDialog.this.dismiss();
            }
        });

    }
}
