package com.chuangba.homeinn.view;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.chuangba.homeinn.ui.ExtendStayInfoActivity;
import com.chuangba.homeinn.R;

/**
 * Created by jinyh on 2018/4/10.
 */

public class RoomTypeDialog extends AlertDialog {
    TextView textViewSubmit;
    TextView textViewCancel;
    TextView textViewStand;
    int index;
    Context context;
    public RoomTypeDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_room_type);
        textViewCancel = (TextView) findViewById(R.id.bt_type_cancel);
        textViewSubmit = (TextView) findViewById(R.id.bt_type_submit);
        textViewStand = (TextView) findViewById(R.id.tv_type_stand);
        final ExtendStayInfoActivity extendStayInfoActivity = (ExtendStayInfoActivity) context;
        WindowManager m = extendStayInfoActivity.getWindowManager();
        setCancelable(false);
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.8);
        p.width = (int) (d.getWidth() * 0.8);
        getWindow().setAttributes(p);
        getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomTypeDialog.this.dismiss();
            }
        });
        textViewSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                RoomTypeDialog.this.dismiss();
            }
        });
        textViewStand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                extendStayInfoActivity.setType(0);
            }
        });
    }
}
