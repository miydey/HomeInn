package com.chuangba.homeinn.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.view.PayDialog;
import com.chuangba.homeinn.view.RoomTypeDialog;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by jinyh on 2018/3/13.
 */

public class ExtendStayInfoActivity extends BaseActivity implements View.OnClickListener{

    private ImageView imageExit;
    AlertDialog dialogNumber;
    AppCompatSpinner spinner;
    Button buttonChangeNumber;
    Button buttonSubmit;
    Button buttonDate;
    Button buttonChangeType;
    private int checkNumber;//入住人数
    TextView textViewEnd;
    TextView textViewExType;
    Intent intent;
    WindowManager m;
    int year;
    int month;
    int day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extend_info);
        m = getWindowManager();
        buttonDate = (Button) findViewById(R.id.bt_extend_date);
        imageExit = (ImageView) findViewById(R.id.iv_exit);
        textViewEnd = (TextView) findViewById(R.id.tv_extend_day);
       // buttonChangeNumber = (Button) findViewById(R.id.bt_change_number);
        buttonChangeType = (Button) findViewById(R.id.bt_change_type);
        buttonSubmit = (Button) findViewById(R.id.bt_extend_pay);
        textViewExType = (TextView) findViewById(R.id.tv_extend_type);
        imageExit.setOnClickListener(this);
        buttonChangeType.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
        buttonDate.setOnClickListener(this);
        Calendar   calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        intent = new Intent(this,CheckActivity.class);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_exit:
                this.finish();
                break;
            case R.id.bt_change_type:
                RoomTypeDialog roomTypeDialog = new RoomTypeDialog(this);
                roomTypeDialog.show();
                //showNumberSelect();
                break;
            case R.id.bt_extend_date:
                DatePickerDialog dp = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int iyear, int monthOfYear, int dayOfMonth) {
                        // long maxDate = datePicker.getMaxDate();//日历最大能设置的时间的毫秒值
                        int year1= datePicker.getYear();//年
                        int month1 = datePicker.getMonth();//月-1
                        int dayOfMonth1 = datePicker.getDayOfMonth();//日*
                        //iyear:年，monthOfYear:月-1，dayOfMonth:日
                        StringBuilder builder = new StringBuilder(String.valueOf(year));
                        builder.append("-")
                                .append(month1+1)
                                .append("-")
                                .append(dayOfMonth1);

                        Date today = new Date(year,month,day);
                        Date extend = new Date(year1,month,dayOfMonth);
                        String rstDays = getDatePoor(extend,today);
                        textViewEnd.setText(rstDays);
                        //ToastUtil.showToast(context, iyear + ":" + (monthOfYear + 1) + ":" + dayOfMonth);
                    }
                }, year, month, day+1);//2013:初始年份，2：初始月份-1 ，1：初始日期
                DatePicker picker = dp.getDatePicker();
                picker.setMinDate(System.currentTimeMillis());
                dp.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
                dp.show();

                break;
            case R.id.bt_extend_pay:
                PayDialog payDialog = new PayDialog(this);
                payDialog.show();

                break;
//            case R.id.bt_check_1:
//                checkNumber = 1;
//                intent.putExtra("check_number",checkNumber);
//                intent.putExtra("order_exist",false);
//                startActivity(intent);
//                dialogNumber.dismiss();
//                break;
//            case R.id.bt_check_2:
//                checkNumber = 2;
//                intent.putExtra("check_number",checkNumber);
//                intent.putExtra("order_exist",false);
//                startActivity(intent);
//                dialogNumber.dismiss();
//                break;
//            case R.id.bt_check_3:
//                checkNumber = 3;
//                intent.putExtra("check_number",checkNumber);
//                intent.putExtra("order_exist",false);
//                startActivity(intent);
//                dialogNumber.dismiss();
//                break;
        }
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

    private void showNumberSelect() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.dialog_check_number,null);
       // spinner = (AppCompatSpinner)layout.findViewById(R.id.sp_number);
        Button button1 = (Button) layout.findViewById(R.id.bt_check_1);
        Button button2 = (Button) layout.findViewById(R.id.bt_check_2);
        Button button3 = (Button) layout.findViewById(R.id.bt_check_3);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);

        String[] spin_arry = getResources().getStringArray(R.array.number_array);

        ArrayAdapter adapter= new ArrayAdapter(this,R.layout.spinner_item,spin_arry);
        //adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item ); //设置的是展开的时候下拉菜单的样式
       // spinner.setAdapter(adapter);
        // builder.setCancelable(false);
        dialogNumber = builder.create();
        dialogNumber.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialogNumber.setView(layout);
        dialogNumber.show();
    }


    public void setType(int i){
        switch (i){
            case 0:
                textViewExType.setText("标准房");
                break;
            case 1:
                textViewExType.setText("大床房");
                break;
            case 2:
                textViewExType.setText("商务房");
                break;
            case 3:
                textViewExType.setText("总统房");
        }


    }
}
