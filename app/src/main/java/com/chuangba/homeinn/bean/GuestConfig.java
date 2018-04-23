package com.chuangba.homeinn.bean;

/**
 * Created by jinyh on 2018/4/4.
 *
 * 顾客信息设置
 */

public class GuestConfig {
    private boolean orderExist;
    private boolean paid;
    private int vipLevel; //0,普通、青春，1，家宾 2,金 3，铂金 4，钻石

    public boolean isOrderExist() {
        return orderExist;
    }

    public void setOrderExist(boolean orderExist) {
        this.orderExist = orderExist;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public int getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(int vipLevel) {
        this.vipLevel = vipLevel;
    }
}
