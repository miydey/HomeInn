package com.chuangba.homeinn.util;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jinyh on 2018/1/17.
 */

public class ToastUtil {


    private static Toast toast;


    //防止一个toast多次弹出或者是上一个toast还没结束，下一个toast出现
    public static void showToast(Context context,
                                 String content) {
        if (toast == null) {
            toast = Toast.makeText(context,
                    content,
                    Toast.LENGTH_SHORT);
        } else {
            toast.setText(content);
        }
        toast.show();
    }
}
