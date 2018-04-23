package com.chuangba.homeinn.view;

import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
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

import com.chuangba.homeinn.R;

/**
 * Created by jinyh on 2018/4/10.
 */

public class PayDialog extends AlertDialog {

    Context context;
    TextView textViewCancel;
    ImageView imageViewQr;
    public PayDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }

    protected PayDialog(@NonNull Context context, @StyleRes int themeResId) {
        super(context, themeResId);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_pay);
        textViewCancel = (TextView) findViewById(R.id.tv_pay_cancel);
        imageViewQr = (ImageView) findViewById(R.id.iv_pay);
        Activity activity = (Activity) context;
        WindowManager m = activity.getWindowManager();
        setCancelable(false);
        Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
        WindowManager.LayoutParams p = getWindow().getAttributes(); // 获取对话框当前的参数值
        p.height = (int) (d.getHeight() * 0.9);
        p.width = (int) (d.getWidth() * 0.8);
        getWindow().setAttributes(p);
        getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        imageViewQr.setImageBitmap(BitmapFactory.decodeResource(context.getResources(),R.mipmap.qr));
        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PayDialog.this.dismiss();

            }
        });

    }
}
