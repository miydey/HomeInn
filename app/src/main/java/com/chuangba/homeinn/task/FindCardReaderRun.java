package com.chuangba.homeinn.task;

import android.content.Context;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.util.Log;

import com.chuangba.homeinn.fragment.VerifyFragment;

import static com.chuangba.homeinn.fragment.VerifyFragment.DETACHED;
import static com.chuangba.homeinn.fragment.VerifyFragment.NO_CARD_READER;
import static com.chuangba.homeinn.fragment.VerifyFragment.RECONNECT;

/**
 * Created by jinyh on 2017/5/21.
 *
 * 实时检测读卡器插拔情况
 */

public class FindCardReaderRun implements Runnable {

    private String TAG = FindCardReaderRun.class.getSimpleName();
    private Context context;
    private VerifyFragment verifyFragment;
    private boolean connect = false;
    private boolean sendAgain = true;
    public FindCardReaderRun(VerifyFragment verifyFragment){
        this.verifyFragment = verifyFragment;
        this.context = verifyFragment.getActivity();
    }

    public boolean getReaderStatus(){
        return  connect;
    }

    @Override
    public void run() {
        if (getCardReader()){
            if (!connect)
            {
                Log.e(TAG, "card reader reconnect" );
                verifyFragment.sendMessage(RECONNECT);
                connect = true;
                sendAgain = true;
            }
        }else{
            if (connect){
                verifyFragment.sendMessage(DETACHED);
                Log.e(TAG, "DETACHED " );
                connect = false;
                sendAgain = false;
            } else {
                if (sendAgain){
                    verifyFragment.sendMessage(NO_CARD_READER);
                }
                sendAgain = false;
            }
        }
    }


    private boolean getCardReader() {
        UsbManager usbManager = (UsbManager)context.getSystemService(Context.USB_SERVICE);
        for (UsbDevice device : usbManager.getDeviceList().values()) {
            if( device.getVendorId() == 1024 && device.getProductId() == 50010 ) {
                Log.d(TAG, "CardReader exist" );
                return true;
            }
        }
        return false;
    }
}
