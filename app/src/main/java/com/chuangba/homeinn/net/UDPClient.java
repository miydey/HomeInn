package com.chuangba.homeinn.net;

import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Administrator on 2017/10/23.
 */

public class UDPClient  implements Runnable {
    static final int udpPort = 7854;
    static final String hostIp = "118.31.38.73";
    private static DatagramSocket socket = null;
    private static DatagramPacket packetSend, packetRcv;
    private boolean udpLife = true; //udp生命线程
    private byte[] msgRcv = new byte[512]; //接收消息

    public UDPClient() {
        super();
    }

    //返回udp生命线程因子是否存活
    public boolean isUdpLife() {
        if (udpLife) {
            return true;
        }

        return false;
    }

    //更改UDP生命线程因子
    public void setUdpLife(boolean b) {
        udpLife = b;
    }

    //发送消息
    public String send(String msgSend) {
        InetAddress hostAddress = null;

        try {
            hostAddress = InetAddress.getByName(hostIp);
        } catch (UnknownHostException e) {
            Log.e("udpClient", "未找到服务器");
            e.printStackTrace();
        }

/*        try {
            socket = new DatagramSocket();
        } catch (SocketException e) {
            Log.i("udpClient","建立发送数据报失败");
            e.printStackTrace();
        }*/

        packetSend = new DatagramPacket(msgSend.getBytes(), msgSend.getBytes().length, hostAddress, udpPort);

        try {
                if (socket!=null)
                socket.send(packetSend);
        } catch (IOException e) {
            e.printStackTrace();
            Log.i("udpClient", "发送失败");
        }
        //   socket.close();
        return msgSend;
    }

    @Override
    public void run() {
        try {
            socket = new DatagramSocket();
            socket.setSoTimeout(180*1000);//设置超时为3min
        } catch (SocketException e) {
            Log.e("udpClient", "建立接收数据报失败");
            e.printStackTrace();
        }
        packetRcv = new DatagramPacket(msgRcv, msgRcv.length);
        while (udpLife) {
            try {
                Log.e("-----udpClient-----", "UDP监听");
                socket.receive(packetRcv);
                String RcvMsg = new String(packetRcv.getData(), packetRcv.getOffset(), packetRcv.getLength(),"gb2312");
                //将收到的消息发给主界面
                //Intent RcvIntent = new Intent();
                //RcvIntent.setAction("udpRcvMsg");
               // RcvIntent.putExtra("udpRcvMsg", RcvMsg);
                // MainActivity.context.sendBroadcast(RcvIntent);

                Log.e("--------Rcv-------", RcvMsg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        Log.e("udpClient", "UDP监听关闭");
        socket.close();
    }

    public void pullSend(){
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                send("{states:alive}");
            }
        },3000,3000);
    }

}
