package com.chuangba.homeinn.bean;

import android.graphics.Bitmap;

import com.huashi.otg.sdk.IDCardInfo;

/**
 * Created by jinyh on 2017/4/20.
 */

public class HistoryInfo {
    private int id ;
    private IDCardInfo idCardInfo;
    private long date;//到店日期
    private byte [] picture_camera;
    private byte [] picture_id;
    private float sim;//相似度
    private boolean isChecked;//是否被查看
    private Bitmap bitmap;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getSim() {
        return sim;
    }

    public void setSim(float sim) {
        this.sim = sim;
    }

    public byte[] getPicture_camera() {
        return picture_camera;
    }

    public void setPicture_camera(byte[] picture_camera) {
        this.picture_camera = picture_camera;
    }

    public byte[] getPicture_id() {
        return picture_id;
    }

    public void setPicture_id(byte[] picture_id) {
        this.picture_id = picture_id;
    }

    public IDCardInfo getIdCardInfo() {
        return idCardInfo;
    }

    public void setIdCardInfo(IDCardInfo idCardInfo) {
        this.idCardInfo = idCardInfo;
    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }
}
