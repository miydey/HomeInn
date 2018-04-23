package com.chuangba.homeinn.bean;

import java.util.Date;

/**
 * Created by jinyh on 2017/5/26.
 */

public class IDCardInfoHd {
    private String PeopleName ;
    private String Sex ;
    private String People ;
    private Date BirthDay = new Date();
    private String Addr ;
    private String IDCard ;
    private String Department;
    private String StartDate ;
    private String EndDate ;

    public String getPeopleName() {
        return PeopleName;
    }

    public void setPeopleName(String peopleName) {
        PeopleName = peopleName;
    }

    public String getSex() {
        return Sex;
    }

    public void setSex(String sex) {
        Sex = sex;
    }

    public String getPeople() {
        return People;
    }

    public void setPeople(String people) {
        People = people;
    }

    public Date getBirthDay() {
        return BirthDay;
    }

    public void setBirthDay(Date birthDay) {
        BirthDay = birthDay;
    }

    public String getAddr() {
        return Addr;
    }

    public void setAddr(String addr) {
        Addr = addr;
    }

    public String getIDCard() {
        return IDCard;
    }

    public void setIDCard(String IDCard) {
        this.IDCard = IDCard;
    }

    public String getDepartment() {
        return Department;
    }

    public void setDepartment(String department) {
        Department = department;
    }

    public String getStartDate() {
        return StartDate;
    }

    public void setStartDate(String startDate) {
        StartDate = startDate;
    }

    public String getEndDate() {
        return EndDate;
    }

    public void setEndDate(String endDate) {
        EndDate = endDate;
    }
}
