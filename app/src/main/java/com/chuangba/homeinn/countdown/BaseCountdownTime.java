package com.chuangba.homeinn.countdown;

/**
 * Created by zhaocheng on 2017/3/27.
 */

public abstract class BaseCountdownTime {
    protected int hour;
    protected int minute;
    protected int second;
    abstract String getTimeText();
}
