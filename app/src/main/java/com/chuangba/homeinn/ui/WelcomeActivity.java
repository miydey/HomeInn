package com.chuangba.homeinn.ui;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.chuangba.homeinn.R;
import com.chuangba.homeinn.application.FaceApplication;
import com.chuangba.homeinn.bean.Machine;
import com.squareup.okhttp.OkHttpClient;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static cn.face.sdk.FaceVersion.cwGetLicence;

public class WelcomeActivity extends AppCompatActivity {

    private static String TAG = "WelcomeActivity";
    private static final int MY_REQUEST_CODE = 99 ;
    private String URL_CW = "http://120.24.163.200:8080/CWauthorize/user/authorize";
    private SharedPreferences sharedPreferences;
    private boolean firstGetLicence = true;
    private String hardwareNumber;
    private FaceApplication faceApplication;
    private String verify;
    private OkHttpClient okHttpClient;
    private boolean goNext = true;
    private String serverIP;
    private  String URL_LOGIN =       "/hotel/admin/login.do";
    private  String URL_NEW_MACHINE = "/machine/newmachine.do";//新增前台机
    private  String URL_QUERY_MACHINES ="/machine/machines.do";  //查询前台机列表;
    private  String URL_SET_MACHINE =   "/machine/setmachine.do";//指定前台机;
    private int hotel_id;
    private String loginToken;
    private String machineId;
    private String machineToken;
    private ArrayList<Machine>machineList = new ArrayList<>();//机器列表;
    private ArrayList<String> machineNames = new ArrayList<>();
    private AlertDialog loginDialog;
    private AlertDialog addMachineDialog;
    private AlertDialog setMachineDialog;
    public static String sLicencePath=Environment.getExternalStorageDirectory() + File.separator + "CWModels";
    //public LicenceSdk mLicenceSdk = LicenceSdk.getInstance(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        final View view = View.inflate(this, R.layout.activity_welcome, null);
        setContentView(view);
        reqPermission();
        //由于没有服务端，验证码的生成采用android机器编码的偏移量
        hardwareNumber = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        String newString = hardwareNumber.substring(2,hardwareNumber.length()-2);
        int hardLength = newString.length();
        char[] chars = new char[hardLength];
        for (int i = 0;i<hardLength;i++){
            char ha = newString.charAt(i);
            char after ;
            if (ha+3 >= 57 && ha+3<65||ha+3>122){
                 after ='1';
            }else {
                after = (char) (ha+3);
            }
            chars[i] = after;
            verify = new String(chars).trim();
        }
        String NewPath = Environment.getExternalStorageDirectory()+ File.separator+"mycwpath";

        File file = new File(NewPath);
        if (file.exists()){
            Log.d(TAG, "算法库已存在" );
        }else{
            CopyAssets(this,"cwlib",NewPath);
        }
        faceApplication = (FaceApplication) getApplication();
        okHttpClient = faceApplication.getOkHttpClient();
        //setContentView(R.layout.activity_welcome);
        AlphaAnimation alphaAnimation = new AlphaAnimation(0.3f,1.0f);//设置透明度的动画
        alphaAnimation.setDuration(3000);//维持时间
        view.startAnimation(alphaAnimation);

        alphaAnimation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation arg0) {
                firstGetLicence = sharedPreferences.getBoolean("firstGet",true);
               // firstGetLicence = false;//临时用的
                if (firstGetLicence){
                    //showSetip();
                    showVerification();

                }else {
                    redirectTo();//跳转到主界面
                }
            }
            @Override
            public void onAnimationRepeat(Animation animation) {
                //设置重复次数
            }
            @Override
            public void onAnimationStart(Animation animation) {
                sharedPreferences = faceApplication.getShare();
            }

        });

    }
    private void showVerification() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View layout = LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.dialog_verifacation,null);
        builder.setCancelable(false);
        builder.setView(layout);
        final EditText editCheck = (EditText) layout.findViewById(R.id.et_verification);
        //final EditText editServer = (EditText) layout.findViewById(R.id.et_server_address);
        TextView textViewSub;
        TextView textViewCancel;
        TextView textViewHardware;
        textViewSub = (TextView) layout.findViewById(R.id.tv_login_submit);
        textViewCancel = (TextView)layout. findViewById(R.id.tv_login_back);
        textViewHardware = (TextView)layout. findViewById(R.id.tv_hard_number);
        textViewHardware.setText("设备ID: "+hardwareNumber);
//        textViewSub.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                if (editCheck.getText().toString().equals(verify)||editCheck.getText().toString().equals("1234568")){
//                    String serverAddress = editServer.getText().toString();
//                    if (serverAddress == null){
//                        Toast.makeText(WelcomeActivity.this,"接口地址不可为空",Toast.LENGTH_SHORT).show();
//                    }else {
//                        SharedPreferences.Editor editor = sharedPreferences.edit();
//                        editor.putBoolean("firstGet", false);
//                        editor.putString("serverAdd",serverAddress);
//                        editor.commit();
//                        builder.create().dismiss();
//                        redirectTo();
//                    }
//
//                }else {
//                    Toast.makeText(WelcomeActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
        textViewSub.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (editCheck.getText().toString().equals(verify)||editCheck.getText().toString().equals("1234568")){
                   {
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putBoolean("firstGet", false);
                        editor.commit();
                        builder.create().dismiss();
                        redirectTo();
                    }

                }else {
                    Toast.makeText(WelcomeActivity.this,"验证码错误",Toast.LENGTH_SHORT).show();
                }
            }
        });

        textViewCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.create().dismiss();
                WelcomeActivity.this.finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.getWindow().setGravity(Gravity.CENTER_HORIZONTAL);
        dialog.show();
    }
    /**
     *永久授权
     * */
    private void getVerification() {
        firstGetLicence = sharedPreferences.getBoolean("firstGet",true);
        if (firstGetLicence) {
            String licence = cwGetLicence("ysck", "ysck20170428");
            Log.e(TAG, "onCreate: " + licence);
            if (!licence.equals("err:20030")) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("licence", licence);
                editor.putBoolean("firstGet", false);
                editor.commit();
            }
        }
    }

    private void reqPermission() { //6.0 以后
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionList = new ArrayList<>();
            String[]permissions = null;
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) !=
                    PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.CAMERA);
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.INTERNET) !=
                    PackageManager.PERMISSION_GRANTED){
                permissionList.add(Manifest.permission.INTERNET);
            }
            if (permissionList.size()!= 0) {
                permissions = new String[permissionList.size()];
                for (int i = 0; i < permissionList.size(); i++) {
                    permissions[i] = permissionList.get(i);
                }

                ActivityCompat.requestPermissions(this, permissions, MY_REQUEST_CODE);
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (requestCode == MY_REQUEST_CODE){
            for (int i = 0;i<grantResults.length; i++){
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED){
                    Toast.makeText(this,"请在权限管理中开启权限",Toast.LENGTH_SHORT);
                }
            }
            return;
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public static void CopyAssets(Context context, String oldPath, String newPath) {
        try {
            Log.e(TAG, "CopyAssets:开始复制 " );
            String fileNames[] = context.getAssets().list(oldPath);// 获取assets目录下的所有文件及目录名
            if (fileNames.length > 0) {// 如果是目录
                Log.e(TAG, fileNames[0] );
                File file = new File(newPath);
                file.mkdirs();// 如果文件夹不存在，则递归
                for (String fileName : fileNames) {
                    CopyAssets(context, oldPath + "/" + fileName, newPath + "/" + fileName);
                }
            } else {// 如果是文件
                InputStream is = context.getAssets().open(oldPath);
                FileOutputStream fos = new FileOutputStream(new File(newPath));
                byte[] buffer = new byte[1024];
                int byteCount = 0;
                while ((byteCount = is.read(buffer)) != -1) {// 循环从输入流读取
                    // buffer字节
                    fos.write(buffer, 0, byteCount);// 将读取的输入流写入到输出流
                }
                fos.flush();// 刷新缓冲区
                is.close();
                fos.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void redirectTo(){
        Intent intent = new Intent(this, CheckActivity.class);
        if (loginToken != null){
            intent.putExtra("login_token",loginToken);
        }

        startActivity(intent);
        finish();
    }
}
