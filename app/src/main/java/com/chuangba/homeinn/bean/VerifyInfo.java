package com.chuangba.homeinn.bean;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by jinyh on 2017/10/26.
 */

public class VerifyInfo implements Parcelable {
    private String name;
    private String IDNumber;
    private Bitmap picCamera;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIDNumber() {
        return IDNumber;
    }

    public void setIDNumber(String IDNumber) {
        this.IDNumber = IDNumber;
    }

    public Bitmap getPicCamera() {
        return picCamera;
    }

    public void setPicCamera(Bitmap picCamera) {
        this.picCamera = picCamera;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(IDNumber);
        dest.writeParcelable(picCamera, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<VerifyInfo> CREATOR = new Creator<VerifyInfo>() {
        @Override
        public VerifyInfo createFromParcel(Parcel in) {
            VerifyInfo verifyInfo = new VerifyInfo();
            verifyInfo.name = in.readString();
            verifyInfo.IDNumber = in.readString();
            verifyInfo.picCamera = in.readParcelable(Bitmap.class.getClassLoader());
            return verifyInfo;
        }

        @Override
        public VerifyInfo[] newArray(int size) {
            return new VerifyInfo[size];
        }
    };
}
