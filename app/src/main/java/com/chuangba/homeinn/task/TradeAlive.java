package com.chuangba.homeinn.task;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by jinyh on 2017/11/23.
 */

public class TradeAlive implements Runnable {

    private static final String TAG = TradeAlive.class.getSimpleName();
    private  String manageAddress ;
    private  int alivePort ;
    private static final String MSG = "keepAlive" ;
     DatagramSocket socket ;


    public TradeAlive(DatagramSocket socket,String address,int alivePort){
        this.socket = socket;
        this.manageAddress = address;
        this.alivePort = alivePort;
        Log.e(TAG,Thread.currentThread().getName());
    }

    @Override
    public void run() {
        InetAddress hostAddress = null;
        try {
            hostAddress = InetAddress.getByName(manageAddress);
        } catch (UnknownHostException e) {
            Log.e("udpClient", "no server");
            e.printStackTrace();
        }
        DatagramPacket packetSend = new DatagramPacket(MSG.getBytes(), MSG.getBytes().length,
                hostAddress, alivePort); //发送消息
        try {
            synchronized (this){
                socket.send(packetSend);
                Log.e(TAG, "send alive" );
            }
        } catch (IOException e) {
            Log.e(TAG, e.getCause().toString() );
            e.printStackTrace();
        }
    }
}
