package com.chuangba.homeinn.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangba.homeinn.ui.CheckActivity;
import com.chuangba.homeinn.ui.ConStant;
import com.chuangba.homeinn.R;
import com.chuangba.homeinn.bean.OrderInfo;
import com.chuangba.homeinn.task.KeepAliveWatchDog;
import com.chuangba.homeinn.task.SocketListener;
import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;
import com.uuzuche.lib_zxing.activity.CodeUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static com.chuangba.homeinn.R.mipmap.alipay;
import static com.chuangba.homeinn.R.mipmap.wechat2;

/**
 * Created by jinyh on 2018/3/2.
 */

public class PayFragment extends Fragment {
    private static String TAG = PayFragment.class.getSimpleName();
    CountDownTimer countdown;
    private Button buttonAliPay;
    private Button buttonWechatPay;
    private TextView textViewPayType;
    private TextView textViewPayCount;
    private TextView textViewRoomType;
    private TextView textViewPrice;
    private TextView textViewDay;
    private TextView textViewName;
    private ImageView imageQrcode;
    CheckActivity activity;
    KeepAliveWatchDog watchDog;
    private boolean keepDeviceAlive;
    SocketListener socketListner;
    Thread threadAlive;
    OkHttpClient okHttpClient;
    int httpTimeOutCount = 5;
    OrderInfo orderInfo;
    String URL_WECHAT = "http://www.chuangber.cn/pay/weroomorder.do";
    String URL_ALI = "http://www.chuangber.cn/pay/aliroomorder.do";

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        shutdownWatchDog();
        countdown.cancel();
    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (hidden){
            countdown.cancel();
        } else {
            Log.e(TAG, "onHiddenChanged: is show" );
            countdown = new CountDownTimer(180*1000,1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    String count = activity.getResources().getString(R.string.count);
                    String time = String.valueOf(millisUntilFinished/1000);
                    String message = String.format(count,time);
                    textViewPayCount.setText(message);
                }

                @Override
                public void onFinish() {
                    activity.finish();
                }
            };
            countdown.start();
        }

    }



    @Override
    public void onStart() {
        super.onStart();
        countdown = new CountDownTimer(180*1000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                String count = activity.getResources().getString(R.string.count);
                String time = String.valueOf(millisUntilFinished/1000);
                String message = String.format(count,time);
                textViewPayCount.setText(message);
            }

            @Override
            public void onFinish() {
                activity.finish();
            }
        };
        countdown.start();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = inflater.inflate(R.layout.fragment_pay,container,false);
        activity = (CheckActivity) getActivity();
        textViewName = (TextView) view.findViewById(R.id.tv_pay_room_name);
        buttonAliPay = (Button) view.findViewById(R.id.bt_ali_pay);
        buttonWechatPay = (Button) view.findViewById(R.id.bt_wechat_pay);
        textViewPayType = (TextView) view.findViewById(R.id.tv_pay_type);
        textViewPayCount = (TextView) view.findViewById(R.id.tv_pay_count);
        textViewPrice = (TextView) view.findViewById(R.id.tv_pay_room_price);
        imageQrcode = (ImageView) view.findViewById(R.id.iv_pay);
        textViewRoomType = (TextView) view.findViewById(R.id.tv_pay_room_type);
        textViewDay = (TextView) view.findViewById(R.id.tv_pay_room_day);
        String name = activity.getName();
        if (name!=null)
        textViewName.setText(name);

        orderInfo = activity.getOrderInfo();
        if (orderInfo!=null){
            textViewPrice.setText("￥"+String.valueOf(orderInfo.getPriceTotal()));
            textViewRoomType.setText(String.valueOf(orderInfo.getRoomTypeInfo().getRoomType()));
            textViewDay.setText(orderInfo.getRoomTypeInfo().getDay()+"晚");
        }else {
            textViewPrice.setText("￥399");
            textViewRoomType.setText("商务房");
            textViewDay.setText("1晚");
        }

        socketListner = new SocketListener() {
            @Override
            public void onMessege(int status) {
                if (status == 2){
                    Log.e(TAG, "onMessege: " );
                    activity.setTab(4);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
//                    View view  = LayoutInflater.from(activity).inflate(R.layout.dialog_check_success,null);
//                    builder.setView(view);
//                    AlertDialog dialog = builder.create();
//                    dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
//                    dialog.show();
                }
            }
        };


        buttonWechatPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewPayType.setText("请打开微信");
                buttonAliPay.setEnabled(true);
                try {
                    sendWeChatTrade("6ff566e1a8b7b73c8716761c43cdbc43","92b07aa138d5f660439493c6084632f8");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });
        buttonAliPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    sendAliTrade("6ff566e1a8b7b73c8716761c43cdbc43","92b07aa138d5f660439493c6084632f8");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                textViewPayType.setText("请打开支付宝钱包");
                buttonWechatPay.setEnabled(true);
                buttonAliPay.setEnabled(false);
            }
        });

        initWatchDog();

        textViewPayType.setText("请打开微信");
        try {
            sendWeChatTrade("6ff566e1a8b7b73c8716761c43cdbc43","92b07aa138d5f660439493c6084632f8");
        } catch (Exception e) {
            e.printStackTrace();
        }
        buttonWechatPay.setEnabled(false);
        return view;
    }
    public void shutdownWatchDog(){
        if (watchDog!= null){
            watchDog.setRun(false);
            watchDog.closeWatchDog();
            threadAlive.interrupt();
            try {
                threadAlive.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            watchDog = null;
            keepDeviceAlive = false;
            Log.e(TAG, "shutdownWatchDog: ");
        }

    }
    public void initWatchDog(){  //向服务端发送心跳
        Log.e(TAG, "initWatchDog: " );
        //为了防止打错字 以后的sharePre直接在application获取
       // sharedPreferences = getSharedPreferences(getResources().getString(R.string.storage_key),MODE_PRIVATE);
        String machineId = "6ff566e1a8b7b73c8716761c43cdbc43";
        String machineName = "测试2";
        String hotelName = "测试酒店";
        if (watchDog == null && (!machineId.equals("default"))){
            watchDog = new KeepAliveWatchDog(machineId,
                    "92b07aa138d5f660439493c6084632f8",machineName,hotelName);
            watchDog.setRun(true);
            watchDog.setSocketListener(socketListner);
            threadAlive =  new Thread(watchDog);
            threadAlive.start();
            keepDeviceAlive = true;
        }else {
            keepDeviceAlive = false;
        }

    }


    /**
     * 发起微信交易 获取二维码
     **/
    private void sendWeChatTrade(final String machineId,final String machineToken) throws Exception {
        String url = URL_WECHAT+"?machine_id="+machineId+"&machine_token="+machineToken;
        Request request = new Request.Builder()
                .url(url)
                //.post(formBody)
                .build();
        Log.e(TAG, url );
        okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("get wechat qrcode", "onFailure: ");
              //  FileUtil.writeStringToFile("---get weChat QRCode fail,get again---\r\n",fileName);
                if (httpTimeOutCount < ConStant.HTTP_FAIL_MAX_COUNT){
                    httpTimeOutCount++;
                    try {
                        sendWeChatTrade(machineId,machineToken);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }else {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(activity,"网络错误",Toast.LENGTH_SHORT).show();
                        }
                    });
                    httpTimeOutCount = 0;
                    //FileUtil.writeStringToFile("---get weChat QRCode fail---\r\n",fileName);
                }

                //showDialog(SHOW_SOCKET_FAIL);

            }
            @Override
            public void onResponse(Response response) throws IOException {
                httpTimeOutCount = 0;
                String result = response.body().string();
                try {
                    Log.e("wechat", result );
                    JSONObject jsonObject = new JSONObject(result);
                    String trade = jsonObject.getString("resultMsg");
                    JSONObject jsobj = jsonObject.getJSONObject("data");
                 final String  weChatQrCode = jsobj.getString("qrcode");
                    Log.e(TAG, weChatQrCode );
                    //udpPort = Integer.valueOf(jsobj.getString("udpPort"));
                    if (weChatQrCode != null ){
                        Log.e(TAG, weChatQrCode );
                        //faceHandler.sendEmptyMessage(97);
                        String trade_no = jsobj.getString("out_trade_no");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap mBitmap = CodeUtils.createImage(weChatQrCode, 400, 400,
                                        BitmapFactory.decodeResource(getResources(), wechat2));
//                            Bitmap mBitmap = CodeUtils.createImage("http://192.168.0.234/hhh.html", 400, 400,
//                                   null);
                                imageQrcode.setImageBitmap(mBitmap);
                            }
                        });
//                        FileUtil.writeStringToFile("---trade no:"
//                                + trade_no+"---",fileName);
                    }else {
                        Log.e(TAG, "onResponse:  send fail" );
                        //faceHandler.sendEmptyMessage(SEND_FAIL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * 发起支付宝交易 获取二维码
     **/
    private void sendAliTrade(final String machineId,final String machineToken) throws Exception {
        String url = URL_ALI+"?machine_id="+machineId+"&machine_token="+machineToken;
        Request request = new Request.Builder()
                .url(url)
                //.post(formBody)
                .build();
        Log.e(TAG, url );
        okHttpClient = new OkHttpClient();
        Call call = okHttpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                Log.e("get wechat qrcode", "onFailure: ");
                //  FileUtil.writeStringToFile("---get weChat QRCode fail,get again---\r\n",fileName);
                if (httpTimeOutCount < ConStant.HTTP_FAIL_MAX_COUNT){
                    httpTimeOutCount++;
                    try {
                        sendAliTrade(machineId,machineToken);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }else {
                    httpTimeOutCount = 0;
                    //FileUtil.writeStringToFile("---get weChat QRCode fail---\r\n",fileName);
                }

            }
            @Override
            public void onResponse(Response response) throws IOException {
                httpTimeOutCount = 0;
                String result = response.body().string();
                try {
                    Log.e("wechat", result );
                    JSONObject jsonObject = new JSONObject(result);
                    String trade = jsonObject.getString("resultMsg");
                    JSONObject jsobj = jsonObject.getJSONObject("data");
                    final String  weChatQrCode = jsobj.getString("qrcode");
                    Log.e(TAG, weChatQrCode );
                    //udpPort = Integer.valueOf(jsobj.getString("udpPort"));
                    if (weChatQrCode != null ){
                        Log.e(TAG, weChatQrCode );
                        //faceHandler.sendEmptyMessage(97);
                        String trade_no = jsobj.getString("out_trade_no");
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Bitmap mBitmap = CodeUtils.createImage(weChatQrCode, 400, 400,
                                        BitmapFactory.decodeResource(getResources(), alipay));
                                imageQrcode.setImageBitmap(mBitmap);
                            }
                        });
//                        FileUtil.writeStringToFile("---trade no:"
//                                + trade_no+"---",fileName);
                    }else {
                        Log.e(TAG, "onResponse:  send fail" );
                        //faceHandler.sendEmptyMessage(SEND_FAIL);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
