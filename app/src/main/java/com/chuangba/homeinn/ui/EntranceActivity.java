package com.chuangba.homeinn.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextClock;
import android.widget.TextView;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.card.SerialCarder;

import java.util.Calendar;

/**
 * Created by jinyh on 2018/2/28.
 */

public class EntranceActivity extends BaseActivity implements View.OnClickListener {
    SerialCarder serialCarder;
    TextView textViewDate;
    TextClock textClock;
    Button buttonCheckIn;
    Button buttonCheckOut;
    Button buttonExtend;
    byte MacAddr = 0;
    Intent intent;
    public static String TAG = EntranceActivity.class.getSimpleName();
    AlertDialog dialogNumber;
    AlertDialog dialogType;
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        //serialCarder.closeDevice();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // serialCarder = SerialCarder.getInstance(this);
       // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_extrance);

        //serialCarder.openSerialDevice();


        textViewDate = (TextView) findViewById(R.id.tv_date);
        textClock = (TextClock) findViewById(R.id.tc_main);
        buttonCheckIn = (Button) findViewById(R.id.bt_check_in);
        buttonCheckIn.setOnClickListener(this);
        buttonCheckOut = (Button) findViewById(R.id.bt_check_out);
        buttonExtend = (Button) findViewById(R.id.bt_extend_stay);
        buttonExtend.setOnClickListener(this);
        buttonCheckOut.setOnClickListener(this);
        textClock.setFormat24Hour("hh:mm EEEE");
        intent = new Intent(this,CheckActivity.class);
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,5);
        String year = calendar.get(Calendar.YEAR)+"";
        String month = calendar.get(Calendar.MONTH)+1+"";
        String day = calendar.get(Calendar.DATE)+"";
        String week = calendar.get(Calendar.DAY_OF_WEEK)+"";
        Log.e(TAG, "onCreate: "+week );
        textViewDate.setText(year+"-"+month+"-"+day);

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.e(TAG, "onRestart: " );

        buttonCheckOut.setEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.bt_check_in:
                //showNumberSelect();
                //showOrdered();
                Intent intent0 = new Intent(this,CheckInSetActivity.class);
                startActivity(intent0);
                break;
            case R.id.bt_extend_stay:
                Intent intent2 = new Intent(this,ExtendStayActivity.class);
                startActivity(intent2);
                break;
            case R.id.bt_check_out:
                Intent intent1 = new Intent(this,CheckOutSetActivity.class);
                startActivity(intent1);
                buttonCheckOut.setEnabled(false);
                break;
        }
    }

    private void showOrdered() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_select,null);
        builder.setView(view);
        TextView checkNow = (TextView) view.findViewById(R.id.tv_check_now);
        TextView orderAlready = (TextView) view.findViewById(R.id.tv_order_already);
        dialogType = builder.create();
        dialogType.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);

        checkNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showNumberSelect();
                dialogType.dismiss();
            }
        });

        orderAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EntranceActivity.this,CheckActivity.class);
                intent.putExtra("order_exist",true);
                startActivity(intent);
                dialogType.dismiss();
            }
        });
        dialogType.show();
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
        //spinner.setAdapter(adapter);
        // builder.setCancelable(false);
        dialogNumber = builder.create();
        dialogNumber.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialogNumber.setView(layout);
        dialogNumber.show();
    }
}
