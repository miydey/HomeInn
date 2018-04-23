package com.chuangba.homeinn.bean;

/**
 * Created by jinyh on 2018/3/1.
 * 记录不同房型的信息
 */

public class RoomTypeInfo {

    private int priceVIP;
    private int priceNormal;
    private String roomType;
    private int day = 1;

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getPriceVIP() {
        return priceVIP;
    }

    public void setPriceVIP(int priceVIP) {
        this.priceVIP = priceVIP;
    }

    public int getPriceNormal() {
        return priceNormal;
    }

    public void setPriceNormal(int priceNormal) {
        this.priceNormal = priceNormal;
    }

    public String getRoomType() {
        return roomType;
    }

    public void setRoomType(String roomType) {
        this.roomType = roomType;
    }
}
