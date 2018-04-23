package com.chuangba.homeinn.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangba.homeinn.ui.CheckActivity;
import com.chuangba.homeinn.R;

import K720_Package.K720_Serial;

/**
 * Created by jinyhon 2018/3/9.
 * 领取放卡
 */

public class CardFragment extends Fragment {
    private String TAG = CardFragment.class.getSimpleName();
    private static byte MacAddr = 0;
    TextView textGetCard;
    ImageView imageViewGetCard;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.from(getContext()).inflate(R.layout.fragment_check_int,container,false);
        textGetCard = (TextView) view.findViewById(R.id.tv_get_card);
        imageViewGetCard = (ImageView) view.findViewById(R.id.iv_get_card);
        ObjectAnimator objectAnimator = ObjectAnimator.ofFloat(imageViewGetCard,"translationY",120f);
        objectAnimator.setRepeatCount(Animation.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.RESTART);
        objectAnimator.setDuration(2500).start();
        textGetCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getCard();
            }
        });
        return view;
    }



    @Override
    public void onResume() {
        super.onResume();

    }

    private void getCard(){

        int nRet;
        byte[] SendBuf=new byte[3];
        String[] RecordIn=new String[2];
        SendBuf[0] = 0x46;
        SendBuf[1] = 0x43;
        SendBuf[2] = 0x34;
        nRet = K720_Serial.K720_SendCmd(MacAddr, SendBuf, 3, RecordIn);
        if(nRet == 0){
            Log.e(TAG, "getCard: " );
            CheckActivity activity = (CheckActivity) getActivity();
            activity.finish();
        }

            //ShowMessage("卡片移动到取卡位置成功");
        else
            Log.e(TAG, "getCard: fail " );
            //ShowMessage("卡片移动到取卡位置失败");
    }

}
