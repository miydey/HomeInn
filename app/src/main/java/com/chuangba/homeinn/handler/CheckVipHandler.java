package com.chuangba.homeinn.handler;

import android.os.Handler;
import android.os.Message;

import com.chuangba.homeinn.ui.CheckVipActivity;
import com.huashi.otg.sdk.IDCardInfo;

import java.lang.ref.WeakReference;

/**
 * Created by jinyh on 2018/4/2.
 */

public class CheckVipHandler extends Handler {

    WeakReference<CheckVipActivity> checkOutActivityWeakReference;

    public CheckVipHandler(CheckVipActivity checkVipActivity) {
        this.checkOutActivityWeakReference = new WeakReference<CheckVipActivity>(checkVipActivity);
    }

    @Override
    public void handleMessage(Message msg) {
        CheckVipActivity checkVipActivity = checkOutActivityWeakReference.get();
        switch (msg.what){
            case CheckVipActivity.SHOW_CARD:
               IDCardInfo idCardInfo = (IDCardInfo) msg.obj;
                checkVipActivity.showCard(idCardInfo);
                break;
        }
    }
}
