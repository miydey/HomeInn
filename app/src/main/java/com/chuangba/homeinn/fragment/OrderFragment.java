package com.chuangba.homeinn.fragment;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;

import com.chuangba.homeinn.ui.CheckActivity;
import com.chuangba.homeinn.R;
import com.chuangba.homeinn.bean.OrderInfo;
import com.chuangba.homeinn.bean.RoomTypeInfo;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by jinyh on 2018/3/1.
 * 确认订单页面
 */

public class OrderFragment extends Fragment {
    private static final String TAG = OrderFragment.class.getSimpleName();
    Context context;
    RoomTypeInfo roomTypeInfo;
    // ListView listViewOrder;
    ArrayList<RoomTypeInfo> roomTypeInfos = new ArrayList<>();
    TextView textViewDateEnd;
    TextView textSelectDateEnd;
    TextView textTypeSubmit;
    TextView textViewBigBed;
    TextView textRoomType;
    TextView textStandRoom;
    TextView textOrderDay;
    TextView textDateStart;
    TextView textDateEnd;
    Calendar calendar;
    int year;
    int month;
    int day;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view =  inflater.inflate(R.layout.fragment_order,container,false);
        context = getActivity();
        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,5);
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        Log.e(TAG, "month "+month );
        day = calendar.get(Calendar.DAY_OF_MONTH);
        //listViewOrder = (ListView) view.findViewById(R.id.lv_order);
        //listViewOrder.addHeaderView(LayoutInflater.from(context).inflate(R.layout.order_top, null));
        textTypeSubmit = (TextView) view.findViewById(R.id.tv_order_submit);
        textViewBigBed = (TextView) view.findViewById(R.id.tv_type_big);
        textStandRoom = (TextView) view.findViewById(R.id.tv_type_stand);
        textOrderDay = (TextView) view.findViewById(R.id.tv_order_days);
        textDateStart = (TextView) view.findViewById(R.id.tv_date_start);
        textDateEnd = (TextView) view.findViewById(R.id.tv_date_end);
       // textRoomType = (TextView) view.findViewById(R.id.tv_selected_type);
        textSelectDateEnd = (TextView) view.findViewById(R.id.tv_selected_date_end);
        StringBuilder builderStart = new StringBuilder(String.valueOf(year));
        builderStart.append("-")
                    .append(month+1)
                    .append("-")
                    .append(day);
        textDateStart.setText(builderStart.toString());

        StringBuilder builderEnd = new StringBuilder(String.valueOf(year));
        builderEnd.append("-")
                    .append(month+1)
                    .append("-")
                    .append(day+1);
        textDateEnd.setText(builderEnd.toString());


        textStandRoom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textStandRoom.setBackground(getActivity().getResources().getDrawable(R.drawable.shape_type_pressedl));
                textStandRoom.setTextColor(getResources().getColor(R.color.white));
                textViewBigBed.setTextColor(getResources().getColor(R.color.grgray));
            }
        });


        textViewBigBed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textViewBigBed.setTextColor(getResources().getColor(R.color.white));
                textStandRoom.setTextColor(getResources().getColor(R.color.grgray));
            }
        });

        textTypeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                roomTypeInfo = new RoomTypeInfo();
                roomTypeInfo.setDay(1);
                roomTypeInfo.setRoomType("标准房");
                OrderInfo orderInfo = new OrderInfo();
                orderInfo.setPriceTotal(999);
                orderInfo.setRoomTypeInfo(roomTypeInfo);
                CheckActivity activity = (CheckActivity) context;
                activity.setOrderInfo(orderInfo);
                activity.setTab(2);
            }
        });

        textSelectDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dp = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int iyear, int monthOfYear, int dayOfMonth) {
                        // long maxDate = datePicker.getMaxDate();//日历最大能设置的时间的毫秒值
                        int year1 = datePicker.getYear();//年
                        int month1 = datePicker.getMonth();//月-1
                        int dayOfMonth1 = datePicker.getDayOfMonth();//日*
                        //iyear:年，monthOfYear:月-1，dayOfMonth:日
                        StringBuilder builder = new StringBuilder(String.valueOf(year1));
                        builder.append("-")
                                .append(month1+1)
                                .append("-")
                                .append(dayOfMonth1);
                        textDateEnd.setText(builder.toString());
                        Date today = new Date(year,month,day);
                        Date extend = new Date(year1,month1,dayOfMonth);
                        String rstDays = getDatePoor(extend,today);
                        textOrderDay.setText(rstDays);
                        //ToastUtil.showToast(context, iyear + ":" + (monthOfYear + 1) + ":" + dayOfMonth);
                    }
                }, year, month, day+1);//2013:初始年份，2：初始月份-1 ，1：初始日期
                DatePicker picker = dp.getDatePicker();
                picker.setMinDate(System.currentTimeMillis());
                dp.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
                dp.show();
            }
        });




        RoomTypeInfo roomTypeInfo = new RoomTypeInfo();
        roomTypeInfo.setPriceNormal(449);
        roomTypeInfo.setPriceVIP(419);
        roomTypeInfo.setRoomType("大床房");
        RoomTypeInfo roomTypeInfo1 = new RoomTypeInfo();
        roomTypeInfo1.setPriceNormal(1088);
        roomTypeInfo1.setPriceVIP(998);
        roomTypeInfo1.setRoomType("总统房");
        RoomTypeInfo roomTypeInfo2 = new RoomTypeInfo();
        roomTypeInfo2.setPriceNormal(499);
        roomTypeInfo2.setPriceVIP(459);
        roomTypeInfo2.setRoomType("商务房");
        RoomTypeInfo roomTypeInfo3 = new RoomTypeInfo();
        roomTypeInfo3.setPriceNormal(619);
        roomTypeInfo3.setPriceVIP(559);
        roomTypeInfo3.setRoomType("家庭房");
            roomTypeInfos.add(roomTypeInfo);
            roomTypeInfos.add(roomTypeInfo1);
            roomTypeInfos.add(roomTypeInfo2);
            roomTypeInfos.add(roomTypeInfo3);
       // OrderAdapter orderAdapter = new OrderAdapter(context, roomTypeInfos);
        //listViewOrder.setAdapter(orderAdapter);
        return view;
    }



    public static String getDatePoor(Date endDate, Date nowDate) {

        long nd = 1000 * 24 * 60 * 60;
        long nh = 1000 * 60 * 60;
        long nm = 1000 * 60;
        // long ns = 1000;
        // 获得两个时间的毫秒时间差异
        long diff = endDate.getTime() - nowDate.getTime();
        // 计算差多少天
        long day = diff / nd;
        // 计算差多少小时
        long hour = diff % nd / nh;
        // 计算差多少分钟
        long min = diff % nd % nh / nm;
        // 计算差多少秒//输出结果
        // long sec = diff % nd % nh % nm / ns;
        // return day + "天" + hour + "小时" + min + "分钟";
        return day + "天";
    }
}
