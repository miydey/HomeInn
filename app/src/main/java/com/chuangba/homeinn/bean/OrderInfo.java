package com.chuangba.homeinn.bean;

import java.util.ArrayList;

/**
 * Created by jinyh on 2018/3/12.
 * 记录订单信息
 */

public class OrderInfo {

    private ArrayList<String> names;//入住人
    private int priceTotal;//总价
    private RoomTypeInfo roomTypeInfo;//房型信息

    public ArrayList<String> getNames() {
        return names;
    }

    public void setNames(ArrayList<String> names) {
        this.names = names;
    }

    public RoomTypeInfo getRoomTypeInfo() {
        return roomTypeInfo;
    }

    public void setRoomTypeInfo(RoomTypeInfo roomTypeInfo) {
        this.roomTypeInfo = roomTypeInfo;
    }

    public int getPriceTotal() {
        return priceTotal;
    }

    public void setPriceTotal(int priceTotal) {
        this.priceTotal = priceTotal;
    }
}
