package com.chuangba.homeinn.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chuangba.homeinn.ui.CheckActivity;
import com.chuangba.homeinn.ui.ConStant;
import com.chuangba.homeinn.R;
import com.chuangba.homeinn.bean.HistoryInfo;
import com.chuangba.homeinn.bean.VerifyRst;
import com.chuangba.homeinn.camera.FaceInfoCallback;
import com.chuangba.homeinn.camera.PreviewCamera;
import com.chuangba.homeinn.card.CardCallback;
import com.chuangba.homeinn.card.HsCardReader;
import com.chuangba.homeinn.db.HisDatabaseHelper;
import com.chuangba.homeinn.task.FindCardReaderRun;
import com.chuangba.homeinn.util.FaceSDK;
import com.chuangba.homeinn.util.FileUtil;
import com.chuangba.homeinn.view.DrawFaceRect;
import com.chuangba.homeinn.view.GuestInfoDialog;
import com.huashi.otg.sdk.IDCardInfo;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import cn.face.sdk.FaceInfo;

import static com.chuangba.homeinn.R.drawable.fail;

/**
 * Created by jinyh on 2018/2/28.
 */

public class VerifyFragment extends Fragment implements FaceInfoCallback,CardCallback {
    private final static String TAG = VerifyFragment.class.getSimpleName();
    public static final int GET_TIME     = 0;
    public static final int CONNECTED    = GET_TIME + 1;
    public static final int SHOW_CARD    = CONNECTED + 1;
    public static final int TIME_OUT     = SHOW_CARD + 1;
    public static final int DETECT_FACE  = TIME_OUT + 1;
    public static final int NO_FACE      = DETECT_FACE + 1;
    public static final int FACE_CAMERA  = NO_FACE + 1;
    public static final int CLEAR_NO_FACE = FACE_CAMERA + 1;
    public static final int SWIPE_AGAIN   = CLEAR_NO_FACE +1;
    public static final int SHOW_HISTORY  = SWIPE_AGAIN +1;
    public static final int NO_CARD_READER = SHOW_HISTORY +1 ;
    public static final int RECONNECT    = NO_CARD_READER +1 ;
    public static final int DETACHED     = RECONNECT + 1;
    public static final int QR_DISMISS   = DETACHED + 1;
    public static final int CHECK_SUCCESS = QR_DISMISS + 1 ;
    public static final int CHECK_FAIL   = CHECK_SUCCESS +1;
    public static final int SEND_SUCCESS = CHECK_FAIL +1;
    public static final int SEND_FAIL   = SEND_SUCCESS +1;
    public static final int UPDATE_VERSION = SEND_FAIL +1;
    private int TIME_TOTAL = 30*1000;
    private FrameLayout frame;
    private PreviewCamera surfaceView;
    private static DrawFaceRect faceRectFirst = null; //两个人脸框
    private static DrawFaceRect faceRectSecond = null;
    private TextView textViewCheckNumber;
    private ImageView imageId;
    private ImageView imageShot;
    private ImageView imageResult;
    private ImageView imageArrow;
    private ImageView imageIDTip;
    private TextView textViewTip;
    private TextView textViewCount;
    CountDownTimer countDownTimer;
    private LinearLayout cardview;
    private TextView textInfo;
    private TextView textTimeOut;
    ObjectAnimator objectAnimator;
    // 华视读卡器
    private HsCardReader hsCardReader;
    private IDCardInfo IDinfoHs; //华视读卡器的信息
    private ArrayList<IDCardInfo> idCardInfos = new ArrayList<>();
    ScheduledExecutorService scheduledExecutorService ;//线程池
    private CheckActivity checkActivity;
    private int checkNumber;//总验证人数
    private ArrayList<String> arrayListId;
    int checkWaited;//待验证人数
    //识别
    FaceInfo[] faceInfos;
    int faceNum; //抓到的人脸数量
    byte[] dataFrame;
    private Bitmap  bitmapShot;
    private Bitmap bmpCamera;
    private Bitmap bmpCut;
    private Bitmap bmpId;
    private FaceSDK faceSDK;

    FaceHandler faceHandler;
    HisDatabaseHelper databaseHelper;
    SoundPool soundPool;
    Calendar calendar;
    //裁剪参数
    int cutX ;
    int cutY ;
    int cutWidth ;
    int cutHeight ;
    private final static int WIDTH = 1280;
    private final static int HEIGHT = 720;
    private static final int RATE = 4; //缩放比率
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_verify, container,false);
        checkActivity = (CheckActivity) getActivity();
        frame = (FrameLayout) view.findViewById(R.id.frame_main);
        surfaceView = (PreviewCamera) view.findViewById(R.id.surface_main);
        imageId = (ImageView) view.findViewById(R.id.image_ID);
        imageShot = (ImageView) view.findViewById(R.id.image_shot);
        cardview = (LinearLayout) view.findViewById(R.id.layout_card_info);
        textInfo = (TextView) view.findViewById(R.id.text_info);
        imageResult = (ImageView) view.findViewById(R.id.image_result);
        imageArrow = (ImageView) view.findViewById(R.id.iv_arrow);
        imageIDTip = (ImageView) view.findViewById(R.id.iv_id_tip);
        textTimeOut = (TextView) view.findViewById(R.id.text_time_out);
        textViewTip = (TextView) view.findViewById(R.id.tv_warn);
        textViewCount = (TextView) view.findViewById(R.id.tv_count);
        textViewCheckNumber = (TextView) view.findViewById(R.id.tv_check_number);
        String count = getResources().getString(R.string.check_number);
        String number = String.valueOf(checkNumber);
        String message = String.format(count,number,number);
        //textViewCheckNumber.setText(message);

        faceRectFirst = new DrawFaceRect(checkActivity, getResources().getColor(R.color.FaceColor));
        frame.addView(faceRectFirst, new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        faceRectSecond = new DrawFaceRect(checkActivity, getResources().getColor(R.color.FaceColor));
        frame.addView(faceRectSecond, new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        surfaceView.setKeepScreenOn(true);
        surfaceView.setCardCallback(this);
        surfaceView.setFragment(this);
        hsCardReader = new HsCardReader(this);
        faceHandler = new FaceHandler();
        setIDCardAnimator(true);
        initData();
        initHandle();
        countDownTimer = new CountDownTimer(checkNumber*TIME_TOTAL,1000) {  //验证倒计时
            @Override
            public void onTick(long millisUntilFinished) {
                Log.e(TAG, "onTick: " );
                String count = getResources().getString(R.string.count);
                String time = String.valueOf(millisUntilFinished/1000);
                String message = String.format(count,time);
                textViewCount.setText(message);
            }

            @Override
            public void onFinish() {
                checkActivity.finish();
            }
        };
        countDownTimer.start();
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e(TAG, "onStart: " );
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        surfaceView.stopCameraPreview();
        scheduledExecutorService.shutdown();
        hsCardReader.closeDevice();
    }

    @Override
    public void onStop() {
        super.onStop();
        surfaceView.stopCamera();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        faceSDK.destroyHandles();
        countDownTimer.cancel();
        hsCardReader.closeDevice();
        scheduledExecutorService.shutdown();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e(TAG, "onResume: " );
        surfaceView.setFaceInfoCallback(this);
        surfaceView.setFaceExistCallback(hsCardReader);
        surfaceView.startCamera();
        faceRectFirst.setImageSize(WIDTH/RATE,HEIGHT/RATE);
        faceRectSecond.setImageSize(WIDTH/RATE,HEIGHT/RATE);
    }

    public void setCheckNumber(int checkNumber){
        this.checkNumber = checkNumber;
        checkWaited = checkNumber;
        arrayListId = new ArrayList<>();
    }

    private void initData() {
        //读卡
        scheduledExecutorService = Executors.newScheduledThreadPool(2);
        //声音
       initSound();
        //数据库
        databaseHelper = HisDatabaseHelper.getInstance(checkActivity);

        //时间
        initTime();

        //读卡
        initCardReader();
//        if (!faceApplication.getKeepAlive()){
//            faceApplication.initWatchDog();
//        }
    }

    private void initTime() {
        calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR,5);
        //IntentFilter filter = new IntentFilter();
        //filter.addAction(Intent.ACTION_TIME_TICK);
        // registerReceiver(timeReceiver, filter);
    }

    private void initCardReader() {

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd");
            String path = Environment.getExternalStorageDirectory().getAbsolutePath();
            String filepath = path+"/FaceLog/"+sdf.format(new Date())+"/";
            FileUtil.mkDir(filepath);
            String fileName = filepath+"log.txt";
            hsCardReader = new HsCardReader(this);
            FindCardReaderRun findCardReaderRun = new FindCardReaderRun(this);
            scheduledExecutorService.scheduleAtFixedRate(findCardReaderRun,20,3000, TimeUnit.MILLISECONDS);
    }

    private void initHandle() {
        faceSDK = FaceSDK.getInstance();
        faceSDK.createHandles();
    }
    public void setIDCardAnimator(boolean offline){
        objectAnimator = ObjectAnimator.ofFloat(imageIDTip,"alpha",1f,0f,1f);
        if (offline){
            imageIDTip.setVisibility(View.VISIBLE);
            objectAnimator.setRepeatCount(Animation.INFINITE);
            objectAnimator.setRepeatMode(ValueAnimator.RESTART);
            objectAnimator.setDuration(1500).start();
        }else {
            objectAnimator.pause();
            imageIDTip.setVisibility(View.INVISIBLE);
        }
    }
    public void sendMessageWithData(int message,Object object){
        Message msg = faceHandler.obtainMessage();
        msg.what = message;
        msg.obj = object;
        faceHandler.sendMessage(msg);
    }

    public void sendMessage(int message){
        faceHandler.sendEmptyMessage(message);
    }

    private void initSound() {
        //声音
        soundPool = new SoundPool(4, AudioManager.RINGER_MODE_NORMAL, 10);
        soundPool.load(checkActivity, R.raw.sound_success, ConStant.BEEP.SUCCESS);
        soundPool.load(checkActivity, R.raw.sound_fail, ConStant.BEEP.FAIL);
        soundPool.load(checkActivity, R.raw.sound_timeout, ConStant.BEEP.TIME_OUT);
        soundPool.load(checkActivity, R.raw.sound_found, ConStant.BEEP.BEEP);
    }
    public Bitmap  takePicture(byte[] data) {
        YuvImage img = new YuvImage(data, ImageFormat.NV21
                , ConStant.CAMERA_SET.PREVIEW_WIDTH, ConStant.CAMERA_SET.PREVIEW_HEIGHT, null);
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        //Matrix matrix = new Matrix();
        //matrix.postScale(-1,1);
        img.compressToJpeg(new Rect(0, 0, ConStant.CAMERA_SET.PREVIEW_WIDTH, ConStant.CAMERA_SET.PREVIEW_HEIGHT), 100, output);
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;

        Bitmap bmp = BitmapFactory.decodeByteArray(output.toByteArray(), 0, output.size(), opt);
        //Bitmap  bmpCamera = bmp.copy(Bitmap.Config.RGB_565,true);
        return bmp;
    }

    public void setViewSize(int width,int height){
        faceRectFirst.setViewSize(width,height);
        faceRectSecond.setViewSize(width,height);
    }

    //播放指定声音
    public void beep(int flag){
        soundPool.play(flag, 1, 1, 0, 0, 1);
    }
    @Override
    public void detectFaceInfo(FaceInfo[] faceInfos, int faceNum) {
        if (faceNum > 0) {
            this.faceInfos = faceInfos;
            this.faceNum = faceNum;
            if (faceNum == 1){
                faceRectFirst.setVisibility(View.VISIBLE);
                faceRectSecond.setVisibility(View.GONE);
                FaceInfo faceInfo = faceInfos[0];
                faceRectFirst.setPosition(faceInfo.x,faceInfo.y,
                        faceInfo.x + faceInfo.width,faceInfo.y+faceInfo.height);
            }
            if (faceNum == 2){
                faceRectFirst.setVisibility(View.VISIBLE);
                FaceInfo faceInfo1 = faceInfos[0];
                faceRectFirst.setPosition(faceInfo1.x,faceInfo1.y,
                        faceInfo1.x + faceInfo1.width,faceInfo1.y+faceInfo1.height);
                faceRectSecond.setVisibility(View.VISIBLE);
                FaceInfo faceInfo = faceInfos[1];
                faceRectSecond.setPosition(faceInfo.x,faceInfo.y,
                        faceInfo.x+faceInfo.width,faceInfo.y + faceInfo.height);
            }
        } else {
            this.faceInfos = null;
            this.faceNum = 0;
            if (faceRectFirst.getVisibility()== View.VISIBLE){
                faceRectFirst.setVisibility(View.GONE);
                faceRectSecond.setVisibility(View.GONE);
            }

        }
    }


    @Override
    public void getPic(byte[] buffer) {
        setIDCardAnimator(false);
        textViewTip.setVisibility(View.INVISIBLE);
        textViewCount.setVisibility(View.INVISIBLE);
        dataFrame = new byte[buffer.length];
        System.arraycopy(buffer,0,dataFrame,0,buffer.length);
        bmpCamera = takePicture(dataFrame);
        try {
            FaceInfo face =   faceSDK.getFeatureFromSurface(dataFrame,WIDTH,HEIGHT);
            if (face!=null){
                cutX = face.x;
                cutY = face.y;
                cutHeight = face.height;
                cutWidth = face.width;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        //开始比对
        try {
            if (faceSDK.getFeatureFromCard(bmpId)==0)
            {
                VerifyRst rst =  faceSDK.compare(0.55f);
                if (bmpCamera != null){
                    String filepath = Environment.getExternalStorageDirectory().getAbsolutePath() + "/wltlib";// 读卡器授权目录
                    if (cutWidth > 0 && cutX > 0){
                        if (cutY + cutHeight < HEIGHT ){
                            if (cutY - cutHeight*2/5 > 0 && (cutY+ cutHeight*6/5 < HEIGHT ) && (cutWidth*6/5 < WIDTH ) && (cutX - cutWidth/5 > 0))
                            {
                                bmpCut = Bitmap.createBitmap(bmpCamera,cutX-cutWidth/5,cutY-cutHeight/5,cutWidth*6/5,cutWidth*7/5);
//
                            }
                            else if(cutX > 20 && cutY > 30){
                                if (cutY-25 + cutHeight+40 > bmpCamera.getHeight()){
                                    throw new Exception("height must be < = source");
                                }
                                bmpCut = Bitmap.createBitmap(bmpCamera,cutX-20,cutY-25,cutWidth+40,cutHeight+40);

                            } else  {
                                bmpCut = Bitmap.createBitmap(bmpCamera,cutX,cutY,cutWidth,cutHeight);

                            }
                        }
                    } else {
                        bmpCut = bmpCamera.copy(Bitmap.Config.RGB_565,true);
                    }
                }
                if (rst.success ){
                    soundPool.play(1, 1, 1, 0, 0, 1);
                        faceHandler.showCard(true,IDinfoHs,bmpId,bmpCut);
                    String name = IDinfoHs.getIDCard();
                    arrayListId.add(name);
                    idCardInfos.add(IDinfoHs);
                    Log.e(TAG, "getPic: "+ arrayListId.size() );
                    String count = getResources().getString(R.string.check_number);
                    checkWaited = checkWaited - 1;
                    String message = String.format(count,String.valueOf(checkNumber),String.valueOf(checkWaited));
                    //textViewCheckNumber.setText(message);
                        if (checkWaited < 1){
                            CountDownTimer countDownTimer2 = new CountDownTimer(4*1000,1000) {
                                @Override
                                public void onTick(long millisUntilFinished) {

                                }

                                @Override
                                public void onFinish() {

                                    GuestInfoDialog guestInfoDialog = new GuestInfoDialog(checkActivity);
                                    guestInfoDialog.initData(idCardInfos);
                                    guestInfoDialog.show();
                                    faceHandler.clearCardInfo();
                                }
                            };
                            countDownTimer.cancel();
                            countDownTimer2.start();//验证结束，等待倒计时

                        }


                }else  {
                    soundPool.play(2, 1, 1, 0, 0, 1);
                        faceHandler.showCard(false,IDinfoHs,bmpId,bmpCut);

                    bmpCut = null;
                    bmpId = null;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private class FaceHandler extends Handler {

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                clearCardInfo();
            }
        };
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case GET_TIME:
                    //获取实时时间改用textClock来代替
                    break;
                case CONNECTED:
                    //隐去信息，表示读卡器正常
                    reConnetCardReader();
                    break;
                case TIME_OUT:
                    //身份证过期
                    showTimeOut();
                    break;
                case SHOW_CARD:
                    HistoryInfo historyInfo = (HistoryInfo) msg.obj;
                    bmpId = historyInfo.getBitmap();
                    IDinfoHs = historyInfo.getIdCardInfo();

                    if (checkRepeat(IDinfoHs.getIDCard(), arrayListId)){
                        textInfo.setText("请不要重复验证");
                        imageResult.setImageDrawable(getResources().getDrawable(R.drawable.fail));
                    }else {
                        surfaceView.setCardExist(true);
                    }
                    break;
                case DETECT_FACE:
                    //faceRectFirst.setVisibility(View.VISIBLE);
                    //  faceRectFirst.setPosition(left,top,width+left,top+height);
                    break;
                case NO_FACE: //没读卡，没人脸
                    faceRectFirst.setPosition(0,0,0,0);
                    break;
                case FACE_CAMERA:
                    showNoFace();//读到卡，还没有人脸的时候
                    break;
                case CLEAR_NO_FACE:
                    clearNoFace();
                    break;
                case SWIPE_AGAIN:
                    swipeAgain();
                    break;
                case SHOW_HISTORY:

                    break;
                case NO_CARD_READER:
                    showNoCardReader();
                    break;
                case DETACHED:
                    hsCardReader.closeDevice();
                    showNoCardReader();
                    break;
                case RECONNECT :
                    hsCardReader.openDevice();
                    break;
            }
        }

        private boolean checkRepeat(String newName,ArrayList<String> names){
           if (names.contains(newName))
           {
               Log.e(TAG, "checkRepeat: 名字已存在" );
               return true;
           }

            else
               return false;
        }

        private void showNoCardReader(){
            Log.e(TAG, "showNoCardReader: " );
            cardview.setVisibility(View.VISIBLE);
            textInfo.setText("请插入读卡器");
        }

        private void reConnetCardReader(){
            cardview.setVisibility(View.INVISIBLE);
            textInfo.setText("");
        }

        private void showNoFace(){
            cardview.setVisibility(View.VISIBLE);
            imageId.setImageBitmap(bmpId);
            textInfo.setText("请正视摄像头");

        }

        private void clearNoFace(){
            cardview.setVisibility(View.INVISIBLE);
            imageId.setImageBitmap(null);
            textInfo.setText("");
        }


        private void showCard (boolean checkRst, IDCardInfo idCardInfo, Bitmap bitmapID, Bitmap bitmapCut){
            cardview.setVisibility(View.VISIBLE);
            textTimeOut.setVisibility(View.INVISIBLE);
            imageId.setImageBitmap(bitmapID);
            imageShot.setImageBitmap(bitmapCut);
            String name_after = "姓　名："+idCardInfo.getPeopleName()+"\n"+"性　别："+idCardInfo.getSex();
            textInfo.setText(name_after);
            imageArrow.setVisibility(View.VISIBLE);
            //验证结果
            if (checkRst){
                imageResult.setImageDrawable(getResources().getDrawable(R.drawable.success));
            }else {
                imageResult.setImageDrawable(getResources().getDrawable(R.drawable.fail));
            }
        }



        private void swipeAgain() {
            cardview.setVisibility(View.VISIBLE);
            textInfo.setVisibility(View.VISIBLE);
            imageIDTip.setVisibility(View.INVISIBLE);
            textViewCount.setVisibility(View.INVISIBLE);
            textInfo.setText(R.string.swipe_card);
            faceHandler.postDelayed(runnable, 3000);

        }

        private void showTimeOut(){
            soundPool.play(3, 1, 1, 0, 0, 1);
            cardview.setVisibility(View.VISIBLE);
            //textTimeOut.setVisibility(View.VISIBLE);
            textTimeOut.setText(R.string.time_out);
            textInfo.setText("");
            imageResult.setImageDrawable(getResources().getDrawable(fail));
            faceHandler.postDelayed(runnable, 3000);
        }

        private void clearCardInfo() {
            cardview.setVisibility(View.GONE);
            imageId.setImageBitmap(null);
            imageShot.setImageBitmap(null);
            imageResult.setImageBitmap(null);
            textTimeOut.setText("");
        }
    }

}
