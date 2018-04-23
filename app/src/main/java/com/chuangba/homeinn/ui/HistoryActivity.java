package com.chuangba.homeinn.ui;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.adapter.HistoryAdapter;
import com.chuangba.homeinn.adapter.MyItemClickListener;
import com.chuangba.homeinn.bean.HistoryInfo;
import com.chuangba.homeinn.db.HisDatabaseHelper;
import com.chuangba.homeinn.util.FileUtil;
import com.chuangba.homeinn.util.ImgUtil;
import com.huashi.otg.sdk.IDCardInfo;

import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static java.lang.System.currentTimeMillis;

/**
 * Created by jinyh on 2017/8/16.
 */

public class HistoryActivity extends Activity implements View.OnClickListener {
    private static final int FRESH_LIST = 1 ;
    private static final int DELETE_INFO = FRESH_LIST + 1 ;
    private static final int ILLEGAL_DATE = DELETE_INFO +1 ;
    private static final int INIT_HISTORY = ILLEGAL_DATE +1;
    private static final int PER_PAGE = 50;
    public static final String TAG = HistoryActivity.class.getSimpleName();

    SharedPreferences.Editor editor;
    Calendar calendar;
    private HistoryAdapter historyAdaper;
    private List<HistoryInfo> historyInfos;
    private long flipTime;
    private int totalNumber;
    private int totalPage;
    private boolean isSearch = false;
    private SharedPreferences sharedPreferences;
    private HistoryHandler historyHandler;

    //UI
    final Bitmap[] bmpID = new Bitmap[1];
    final Bitmap[] bmpCamera = new Bitmap[1];
    private RecyclerView recycleHis ;
    private CheckBox checkBoxAll;
    private TextView text_name;
    private TextView text_sex ;
    private TextView text_number ;
    private TextView text_check_date ;
    private TextView text_sim ;
    private TextView text_result ;
    private ImageView image_search;
    private EditText edit_year ;
    private EditText edit_month ;
    private EditText edit_day ;
    private ImageView image_camera;
    private ImageView image_id;
    private ImageView image_delete;
    private ImageView image_save ;
    private LinearLayoutManager manager ;
    private TextView textView_page_up ;
    private TextView textView_page_down;
    private TextView textView_last ;
    private TextView textView_threshold ;
    private SeekBar seekBar ;
    private TextView textPageNumber;
    int pageNumber = 0;
    final int[] clickPosition = new int[1];
    final int[] id = {0};
    boolean itemClicked = false;

    //db
    private float threshold = 0.68f;
    HisDatabaseHelper databaseHelper;
    SQLiteDatabase hisData;


    private static class HistoryHandler extends Handler{
        WeakReference<HistoryActivity> weakReference;
        public HistoryHandler(HistoryActivity historyActivity){
            this.weakReference = new WeakReference<HistoryActivity>(historyActivity);
        }
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            HistoryActivity historyActivity = this.weakReference.get();
            switch (msg.what){
                case FRESH_LIST:
                    historyActivity.freshList();
                    break;
                case INIT_HISTORY:
                    //historyActivity.initHistoryList(0);
                    break;
                case ILLEGAL_DATE:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        initView();
        initData();
        initHistoryList();
        setViews();
        historyHandler = new HistoryHandler(this);
        manager = new LinearLayoutManager(this);
        recycleHis.setLayoutManager(manager);
       // manager.setOrientation(LinearLayoutManager.HORIZONTAL);
        historyAdaper = new HistoryAdapter(historyInfos,this);
        recycleHis.setAdapter(historyAdaper);
        historyAdaper.setOnItemClickListener(new MyItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                itemClicked = true;
                clickPosition[0] = position;
                id[0] = historyInfos.get(position).getId();
                text_name.setText(historyInfos.get(position).getIdCardInfo().getPeopleName());
                text_sex.setText(historyInfos.get(position).getIdCardInfo().getSex());
                text_number.setText(historyInfos.get(position).getIdCardInfo().getIDCard());
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                Date date = new Date(historyInfos.get(position).getDate());
                text_check_date.setText(simpleDateFormat.format(date));
                int similar = (int) (historyInfos.get(position).getSim()*100);
                text_sim.setText(similar+"%");
                if (similar >= threshold*100){
                    text_result.setText("验证成功");
                }else {
                    text_result.setText("验证失败");
                }
                byte[] inputID = historyInfos.get(position).getPicture_id();
                bmpID[0] = BitmapFactory.decodeByteArray(inputID, 0, inputID.length);
                image_id.setImageBitmap(bmpID[0]);
                byte[] inputCamera = historyInfos.get(position).getPicture_camera();
                bmpCamera[0] = BitmapFactory.decodeByteArray(inputCamera,0 ,inputCamera.length);
                image_camera.setImageBitmap(bmpCamera[0]);

            }
        });

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                textView_threshold.setText(String.valueOf(progress)+"%");
                threshold = progress/100f;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });




    }

    private void initData() {
        databaseHelper = HisDatabaseHelper.getInstance(this);
        hisData = databaseHelper.getWritableDatabase();

        sharedPreferences = getSharedPreferences("password",MODE_PRIVATE);
        threshold = sharedPreferences.getFloat("threshold",0.65f);
        seekBar.setProgress((int) (threshold*100));
    }

    private void initView() {
        recycleHis = (RecyclerView) findViewById(R.id.recycle_his);
        checkBoxAll = (CheckBox) findViewById(R.id.checkBox_all);
        text_name = (TextView) findViewById(R.id.text_his_name);
        text_sex = (TextView) findViewById(R.id.text_his_sex);
        text_number = (TextView) findViewById(R.id.text_his_card_number);
        text_check_date = (TextView) findViewById(R.id.text_his_date);
        text_sim = (TextView) findViewById(R.id.text_his_sim);
        text_result = (TextView) findViewById(R.id.text_his_result);
        image_search = (ImageView) findViewById(R.id.text_his_search);
        edit_year = (EditText) findViewById(R.id.edit_year);
        edit_month = (EditText) findViewById(R.id.edit_month);
        edit_day = (EditText) findViewById(R.id.edit_day);
        image_camera = (ImageView) findViewById(R.id.image_his_camera);
        image_id = (ImageView) findViewById(R.id.image_his_id);
        image_delete = (ImageView) findViewById(R.id.image_his_delete);
        image_save = (ImageView) findViewById(R.id.image_his_save);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        textView_page_up = (TextView)findViewById(R.id.text_page_up);
        textView_page_down = (TextView)findViewById(R.id.text_page_down);
        textView_last = (TextView)findViewById(R.id.text_page_last);
        textView_threshold = (TextView) findViewById(R.id.text_threshold);
        textPageNumber = (TextView) findViewById(R.id.text_page_number);
        seekBar = (SeekBar) findViewById(R.id.seek_the);
    }


    private void setViews(){
        textView_threshold.setText(String.valueOf((int)(threshold*100))+"%" );
        textPageNumber.setText("第"+String.valueOf(pageNumber+1)+"/"+String.valueOf(totalNumber/PER_PAGE+1)+"页");
        checkBoxAll.setOnClickListener(this);
        image_save.setOnClickListener(this);
        image_delete.setOnClickListener(this);
        image_search.setOnClickListener(this);
        textView_page_up.setOnClickListener(this);
        textView_page_down.setOnClickListener(this);
        textView_last.setOnClickListener(this);

        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,5);
        int year_default =  calendar.get(Calendar.YEAR);
        int month_default = calendar.get(Calendar.MONTH) + 1; // 一月是0
        int day_default = calendar.get(Calendar.DAY_OF_MONTH);
        edit_year.setText(String.valueOf(year_default));
        edit_month.setText(String.valueOf(month_default));
        edit_day.setText(String.valueOf(day_default));

    }

    private void deleteRecord(ArrayList<HistoryInfo> deleteList,int deleteCount) {

        String[] delete = new String[1];
        for (int aa = 0; aa<deleteCount; aa++){
            delete[0] = String.valueOf(deleteList.get(aa).getId());
            hisData.delete("face","id=?",delete);
        }

        //hisData.delete("face","id=?",new String[]{String.valueOf(id[])});
        flipPage(pageNumber);
        historyAdaper.refresh(historyInfos);
        Toast.makeText(HistoryActivity.this,"删除成功",Toast.LENGTH_SHORT).show();
        //initHistoryList(pageNumber);
        if (historyInfos.size()==0||clickPosition[0] >= historyInfos.size()){
            text_name.setText("");
            text_sex.setText("");
            text_number.setText("");
            text_check_date.setText("");
            text_sim.setText("");
            text_result.setText("");
            image_camera.setImageBitmap(null);
            image_id.setImageBitmap(null);
            return;
        }else {
            text_name.setText(historyInfos.get(clickPosition[0]).getIdCardInfo().getPeopleName());
            text_sex.setText(historyInfos.get(clickPosition[0]).getIdCardInfo().getSex());
            text_number.setText(historyInfos.get(clickPosition[0]).getIdCardInfo().getIDCard());
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            Date date = new Date(historyInfos.get(clickPosition[0]).getDate());
            text_check_date.setText(simpleDateFormat.format(date));
            int similar = (int) (historyInfos.get(clickPosition[0]).getSim()*100);
            text_sim.setText(similar+"%");
            if (similar >= threshold*100){
                text_result.setText("验证成功");
            }else {
                text_result.setText("验证失败");
            }
            byte[] inputID = historyInfos.get(clickPosition[0]).getPicture_id();
            bmpID[0] = BitmapFactory.decodeByteArray(inputID, 0, inputID.length);
            image_id.setImageBitmap(bmpID[0]);
            byte[] inputCamera = historyInfos.get(clickPosition[0]).getPicture_camera();
            bmpCamera[0] = BitmapFactory.decodeByteArray(inputCamera,0 ,inputCamera.length);
            image_camera.setImageBitmap(bmpCamera[0]);

        }

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        hisData.close();
        historyInfos.clear();
        editor = sharedPreferences.edit();
        editor.putFloat("threshold",threshold);
        editor.commit();
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.checkBox_all:
                if(((CheckBox) v).isChecked()){
                    historyAdaper.selectAll();
                }else {
                    historyAdaper.unSelectAll();
                }

                break;
            case R.id.image_his_delete:
                showDelete();
                break;
            case R.id.image_his_save:
                showSave();
                break;
            case R.id.text_his_search:
                isSearch = true;
                pageNumber = 0;
                final String year = edit_year.getText().toString().trim();
                final String month = edit_month.getText().toString().trim();
                final String day = edit_day.getText().toString().trim();
                if (year == null|| month == null|| day == null){
                    Toast.makeText(this,"请填写完整日期",Toast.LENGTH_SHORT);
                }else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            queryByDate(year, month, day);
                        }
                    }).start();

                }

                break;
            case R.id.text_page_last:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        pageNumber = totalNumber/PER_PAGE;
                        flipPage(pageNumber);
                    }
                }).start();
                break;
            case R.id.text_page_down:
                pageDown();
                break;
            case R.id.text_page_up:
                pageUp();
                break;
        }
    }

    private void pageUp() {
        if (System.currentTimeMillis() - flipTime >500){
            if ((pageNumber <= totalPage) && pageNumber >=1){
                pageNumber = pageNumber -1;
                flipTime = currentTimeMillis();
                if (isSearch){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String start = String.valueOf(pageNumber*50);
                            String year = edit_year.getText().toString().trim();
                            String month = edit_month.getText().toString().trim();
                            String day = edit_day.getText().toString().trim();
                            queryByDate(start,year,month,day);
                        }
                    }).start();
                }else {
                    if (pageNumber >= 0)
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                flipPage(pageNumber);
                            }
                        }).start();
                }
            }
        } else
            flipTime = System.currentTimeMillis();
    }

    private void pageDown() {
        if (System.currentTimeMillis() - flipTime >500){
            if (pageNumber < totalPage-1){
                pageNumber = pageNumber + 1;
                flipTime = System.currentTimeMillis();
                if (isSearch){
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String start = String.valueOf(pageNumber*50);
                            String year = edit_year.getText().toString().trim();
                            String month = edit_month.getText().toString().trim();
                            String day = edit_day.getText().toString().trim();
                            queryByDate(start,year,month,day);
                        }
                    }).start();

                } else {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            flipPage(pageNumber);
                        }
                    }).start();
                }

            }
        }else
            flipTime = System.currentTimeMillis();
    }

    private void queryByDate(String start,String year, String month, String day) {
        String input = year+"-"+month+"-"+day;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long input_time = simpleDateFormat.parse(input).getTime();
            String SQL_TIME = "SELECT * FROM face WHERE date >"+input_time+" limit "+start+",50";
            Cursor cursor1 = hisData.rawQuery(SQL_TIME,null);
            historyInfos.clear();
            cursor1.moveToFirst();
            int result = cursor1.getCount();
            for (int j = 0; j < result ; j++){
                HistoryInfo history = new HistoryInfo();
                IDCardInfo id = new IDCardInfo();
                id.setPeopleName(cursor1.getString(1));
                id.setSex(cursor1.getString(3));
                id.setIDCard(cursor1.getString(4));
                history.setIdCardInfo(id);
                history.setId(cursor1.getInt(0));
                history.setDate(cursor1.getLong(2));
                history.setPicture_camera(cursor1.getBlob(6));
                history.setPicture_id(cursor1.getBlob(7));
                history.setSim(cursor1.getFloat(8));
                historyInfos.add(history);
                cursor1.moveToNext();
            }
            cursor1.close();
            historyHandler.sendEmptyMessage(FRESH_LIST);
        } catch (ParseException e) {
           historyHandler.sendEmptyMessage(ILLEGAL_DATE);

            e.printStackTrace();
        }
    }

    private void initHistoryList() {
        historyInfos = new ArrayList<>();
        int pageId = (pageNumber)*50;

        String SQL_PAGE = "SELECT * FROM face limit 50";
        //String SQL_PAGE = "SELECT * FROM face where id >="+pageId+" and id < "+ pageEnd;
        String SQL_COUNT = "SELECT COUNT(*) as num FROM face";
        String SQL_DELETE = "delete from face where id < (select id from face limit 10,1)";
        if (!hisData.isOpen()){
            hisData = databaseHelper.getWritableDatabase();
        }

        final Cursor cursor = hisData.rawQuery(SQL_PAGE,null);
        Cursor cursor1 = hisData.rawQuery(SQL_COUNT,null);
        cursor1.moveToFirst();
        totalNumber = (int) cursor1.getLong(0);
        if (totalNumber > 20000){
            //hisData.delete("face","id < ",new String[]{"select id from face limit 10,1"});
            hisData.execSQL(SQL_DELETE);
        }
        totalPage = totalNumber/PER_PAGE+1;
        cursor1.close();
        cursor.moveToFirst();
        int count = cursor.getCount();
        if(count < PER_PAGE){
            for (int i = 0; i < count; i++){
                HistoryInfo history = new HistoryInfo();
                IDCardInfo id = new IDCardInfo();
                id.setPeopleName(cursor.getString(1));
                id.setSex(cursor.getString(3));
                id.setIDCard(cursor.getString(4));
                history.setIdCardInfo(id);
                history.setId(cursor.getInt(0));
                history.setDate(cursor.getLong(2));
                history.setPicture_camera(cursor.getBlob(6));
                history.setPicture_id(cursor.getBlob(7));
                history.setSim(cursor.getFloat(8));
                historyInfos.add(history);
                cursor.moveToNext();
            }
        }else {
            for (int i = 0; i < PER_PAGE; i++) {
                HistoryInfo history = new HistoryInfo();
                IDCardInfo id = new IDCardInfo();
                id.setPeopleName(cursor.getString(1));
                id.setSex(cursor.getString(3));
                id.setIDCard(cursor.getString(4));
                history.setIdCardInfo(id);
                history.setId(cursor.getInt(0));
                history.setDate(cursor.getLong(2));
                history.setPicture_camera(cursor.getBlob(6));
                history.setPicture_id(cursor.getBlob(7));
                history.setSim(cursor.getFloat(8));
                historyInfos.add(history);
                cursor.moveToNext();
            }
        }
        Log.e(TAG, "initHistoryList: "+historyInfos.size() );
        cursor.close();
    }


    private void queryByDate(String year, String month, String day) {
        String input = year+"-"+month+"-"+day;
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        try {
            long input_time = simpleDateFormat.parse(input).getTime();
            String SQL_TIME = "SELECT * FROM face WHERE date >"+input_time;
            Cursor cursor1 = hisData.rawQuery(SQL_TIME,null);
            historyInfos.clear();
            cursor1.moveToFirst();
            int result = cursor1.getCount();
            for (int j = 0; j < result ; j++){
                HistoryInfo history = new HistoryInfo();
                IDCardInfo id = new IDCardInfo();
                id.setPeopleName(cursor1.getString(1));
                id.setSex(cursor1.getString(3));
                id.setIDCard(cursor1.getString(4));
                history.setIdCardInfo(id);
                history.setId(cursor1.getInt(0));
                history.setDate(cursor1.getLong(2));
                history.setPicture_camera(cursor1.getBlob(6));
                history.setPicture_id(cursor1.getBlob(7));
                history.setSim(cursor1.getFloat(8));
                historyInfos.add(history);
                cursor1.moveToNext();
            }
            cursor1.close();
            historyHandler.sendEmptyMessage(FRESH_LIST);
        } catch (ParseException e) {
            historyHandler.sendEmptyMessage(ILLEGAL_DATE);

            e.printStackTrace();
        }
    }


    private void showSave() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        final int saveCount = historyAdaper.getSelectedItem().size();
        if (saveCount < 1){
            builder.setTitle("请先勾选要导出的记录")
                    .setIcon(R.mipmap.message)
                    .setNegativeButton("确定",null);

        }else{
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ArrayList<HistoryInfo> checkList=  historyAdaper.getSelectedItem();
                    String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/saveFace";
                    FileUtil.mkDir(path);
                    ProgressBar progressBar = new ProgressBar(HistoryActivity.this);
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd-HH时mm分");
                    for (int i = 0; i<saveCount;i++){
                        String nameID =
                                checkList.get(i).getIdCardInfo().getPeopleName()
                                +(simpleDateFormat.format(checkList.get(i).getDate()))
                                +("-身份证.jpg");
                        String nameShot = (checkList.get(i).getIdCardInfo().getPeopleName())
                                +(i+1+"-")
                                +(simpleDateFormat.format(checkList.get(i).getDate()))
                                +("-相似度："+(int)(checkList.get(i).getSim()*100)+"%"+"-")
                                +("-抓拍.jpg");
                        String information =
                                "姓　名："+checkList.get(i).getIdCardInfo().getPeopleName()
                                +"\n" +"性　别："+checkList.get(i).getIdCardInfo().getSex()
                                +"\n"+checkList.get(i).getIdCardInfo().getIDCard()
                                +"\n"+simpleDateFormat.format(checkList.get(i).getDate())
                                +"\n"+"相似度："+(int)(checkList.get(i).getSim()*100)+"%";
                        byte[]bytesID = checkList.get(i).getPicture_id();
                        Bitmap bmpId = BitmapFactory.decodeByteArray(bytesID, 0, bytesID.length);
                       // ImgUtil.saveJPGE_After(bmpId,path,nameID,100);
                        byte[]bytesCamera = checkList.get(i).getPicture_camera();
                        Bitmap bitmapCamera = BitmapFactory.decodeByteArray(bytesCamera,0,bytesCamera.length);
                        //ImgUtil.saveJPGE_After(bitmapCamera,path,nameShot,100);
                        Bitmap bmpCompose= ImgUtil.mergeBitmap(bitmapCamera,bmpId,information);
                        String nameCompose =
                                checkList.get(i).getIdCardInfo().getPeopleName()
                                +"-"+checkList.get(i).getId()+".jpg";
                        ImgUtil.saveJPGE_After(bmpCompose,path,nameCompose,100);

                    }
                    Toast.makeText(HistoryActivity.this,"导出完毕",Toast.LENGTH_LONG).show();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            });
            builder.setTitle("是否要导出"+saveCount+"条记录")
                    .setIcon(R.mipmap.message)
                    .setNegativeButton("取消",null);
        }

        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.Dialog_Anim_Style);
        dialog.show();
    }

    private void flipPage(int pageNumber) {
        historyInfos.clear();
        String pageId = String.valueOf(pageNumber*PER_PAGE);
        String pageEnd = String.valueOf((pageNumber +1)*PER_PAGE);
        // String SQL_PAGE = "SELECT * FROM face where id >= "+pageId +" and id < "+ pageEnd;
        String SQL_PAGE = "SELECT * FROM face limit "+pageId+",50";
        if (!hisData.isOpen()){
            hisData = databaseHelper.getWritableDatabase();
        }

        final Cursor cursor = hisData.rawQuery(SQL_PAGE,null);
        cursor.moveToFirst();
        int count = cursor.getCount();
        for (int i = 0; i < count; i++) {
            HistoryInfo history = new HistoryInfo();
            IDCardInfo id = new IDCardInfo();
            id.setPeopleName(cursor.getString(1));
            id.setSex(cursor.getString(3));
            id.setIDCard(cursor.getString(4));
            history.setIdCardInfo(id);
            history.setId(cursor.getInt(0));
            history.setDate(cursor.getLong(2));
            history.setPicture_camera(cursor.getBlob(6));
            history.setPicture_id(cursor.getBlob(7));
            history.setSim(cursor.getFloat(8));
            historyInfos.add(history);
            cursor.moveToNext();

        }
        cursor.close();
        historyHandler.sendEmptyMessage(FRESH_LIST);
    }


    private void deleteInfo(){
        historyAdaper.refresh(historyInfos);
        // Toast.makeText(MainActivity.this,"删除成功",Toast.LENGTH_SHORT);
    }


    private void freshList(){
        //initHistoryList(pageNumber);
        historyAdaper.refresh(historyInfos);
        totalPage = totalNumber/PER_PAGE+1;
        textPageNumber.setText("第"+String.valueOf(pageNumber+1)+"/"+String.valueOf(totalPage)+"页");
        if (historyInfos.size() == 0)
            Toast.makeText(HistoryActivity.this,"查询结果为空",Toast.LENGTH_SHORT);
    }

    private void showDelete() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("是否要删除记录")
                .setIcon(R.mipmap.message)
                .setNegativeButton("取消",null);
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ArrayList<HistoryInfo> deleteList = historyAdaper.getSelectedItem();
                int deleteCount = deleteList.size();
                deleteRecord(deleteList, deleteCount);
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setWindowAnimations(R.style.Dialog_Anim_Style);
        dialog.show();
    }
}
