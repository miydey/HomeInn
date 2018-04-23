package com.chuangba.homeinn.task;

import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * Created by jinyh on 2017/11/23.
 */


public class KeepAliveWatchDog implements Runnable {
    private static String TAG = KeepAliveWatchDog.class.getSimpleName();
    private long delay = 20 * 1000 * 60 ;  //发送间隔,20分钟
    private  String manageAddress = "ws://www.chuangber.cn/machine_online/";
    //private  String manageAddress = "ws://192.168.0.234/machine_online/";
    // private  int alivePort = 7859;
    private String machineID = "default";
    private String machineToken = "default";
    WebSocketClient webSocketClient;
   // MainActivity activity;
    SocketListener socketListener;
    Fragment fragment;
    private boolean isRun = false;

    public KeepAliveWatchDog(String machineID,String machineToken,String machineName,String hotelName){
        this.machineToken = machineToken;
        this.machineID = machineID;
        String machineNameToBase = Base64.encodeToString(machineName.getBytes(),Base64.NO_WRAP);
        String hotelNameToBase = Base64.encodeToString(hotelName.getBytes(),Base64.NO_WRAP);
        try {
            webSocketClient = new WebSocketClient(new URI(manageAddress+machineID+"/"
                    +machineToken+"?machine_name="+machineNameToBase+"&hotel_name="+hotelNameToBase)) {
                @Override
                public void onOpen(ServerHandshake serverHandshake) {

                }

                @Override
                public void onMessage(String s) {
                    Log.e(TAG, s );
                    //activity.writeLog(s);
                    JSONObject jsonPayResult = null;
                    try {
                        jsonPayResult = new JSONObject(s);
                        int payResult = jsonPayResult.getInt("statu");
                        if (payResult == 2){
                            Log.e(TAG, "pay success" );
                            socketListener.onMessege(2);
                            //activity.disMiss();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onClose(int i, String s, boolean b) {
                    Log.e(TAG, "onClose: "+"int"+i +"Sting"+b );
                    //activity.writeLog("webSocket status onClose\r\n"+"int: "+i+" String: "+s+" boolean: "+b);
                    if (i == 1000 && s.length() > 1 ){
                        try {
                            JSONObject json = new JSONObject(s);
                            int statu = json.getInt("statu");
                            if (statu == -2){
                                Thread.sleep(1000 * 60);
                                this.connect();
                            }else
                                webSocketClient =null;

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }else if (i == 1001){
                        try {
                            Thread.sleep(1000 * 60);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        this.connect();
                    }

                }

                @Override
                public void onError(Exception e) {
                    Log.e(TAG, "onError: " );
                    //activity.writeLog("webSocket status onError:"
                      //      +"message:" +e.getMessage());
                }
            };
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        webSocketClient.connect();

    };

    public void setContext(Fragment fragment){
        this.fragment = fragment;
    }

    public void setSocketListener(SocketListener socketListener){
        this.socketListener = socketListener;
    }

    public void setRun(boolean isRun){
        this.isRun = isRun;
    }
    public void closeWatchDog(){
        if (!isRun && webSocketClient!=null){

                webSocketClient.close();
                webSocketClient = null;
                Log.e(TAG, "closeWatchDog: " );


        }
    }
    @Override
    public void run() {
        while (isRun && webSocketClient!=null){
            synchronized (this){
                while (!webSocketClient.getReadyState().equals(WebSocket.READYSTATE.OPEN)){
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                webSocketClient.send(machineID + machineToken );
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

    }
}