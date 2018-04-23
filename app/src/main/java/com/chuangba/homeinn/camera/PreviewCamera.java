package com.chuangba.homeinn.camera;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.chuangba.homeinn.ui.ConStant;
import com.chuangba.homeinn.card.CardCallback;
import com.chuangba.homeinn.card.FaceExistCallback;
import com.chuangba.homeinn.fragment.VerifyFragment;
import com.chuangba.homeinn.util.ImgUtil;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import cn.face.sdk.FaceInfo;
import cn.face.sdk.FaceInterface;
import hdx.HdxUtil;

import static cn.face.sdk.FaceDetTrack.cwFaceDetection;

/**
 * Created by jinyh on 2017/12/19.
 */

public class PreviewCamera extends SurfaceView implements SurfaceHolder.Callback, Camera.PreviewCallback,OnCaptureCallback {
    private int cardType;
    private Camera mCamera;
    private boolean mPreviewing = true;
    private CameraConfigurationManager mCameraConfigurationManager;
    int caremaId = Camera.CameraInfo.CAMERA_FACING_BACK;
    final String TAG = PreviewCamera.class.getSimpleName();
    final int WIDTH = ConStant.CAMERA_SET.PREVIEW_WIDTH;
    final int HEIGHT = ConStant.CAMERA_SET.PREVIEW_HEIGHT;
    private int surfaceHeight;
    private int surfaceWidth;
    private static final int RATE = 4;
    int noFaceCount = 0;
    int faceNum;
    private boolean stopGetFeature = false;
    int iDetHandle;
    private CardCallback cardCallback;
    private boolean cardExist;
    //相机参数
    private Context context;
    VerifyFragment verifyFragment;
    private byte[] buffer = new byte[WIDTH * HEIGHT];
    FaceInfoCallback faceInfoCallback;
    FaceExistCallback faceExistCallback;
    public PreviewCamera(Context context) {
        super(context);
        this.context = context;
    }

    public PreviewCamera(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public PreviewCamera(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    public void setFragment(VerifyFragment verifyFragment){
        this.verifyFragment = verifyFragment;
    }

public void setFaceInfoCallback(FaceInfoCallback faceInfoCallback){
    this.faceInfoCallback = faceInfoCallback;
}

    public void setFaceExistCallback(FaceExistCallback faceExistCallback){
        this.faceExistCallback = faceExistCallback;
    }

    public void setCardExist(boolean exist){
        this.cardExist = exist;
    }
    public void setCardCallback(CardCallback cardCallback){
        this.cardCallback = cardCallback;
    }
    public void setCardType(int type){
        cardType = type;
    }

    private int getFaceRect(Bitmap bitmap ) throws IOException  //提取人脸框，基础信息
    {
        byte[] data = ImgUtil.bitmapToByte(bitmap, Bitmap.CompressFormat.JPEG,90);

        FaceInfo[] faceInfos = new FaceInfo[2];
        for (int i = 0; i < 2; i++)
        {
            faceInfos[i] = new FaceInfo();
        }
//
        int iRet = cwFaceDetection(iDetHandle, data, WIDTH, HEIGHT, FaceInterface.cw_img_form_t.CW_IMAGE_BINARY,
                FaceInterface.cw_img_mirror_t.CW_IMAGE_MIRROR_NONE, 0,
                FaceInterface.cw_op_t.CW_OP_DET , faceInfos);
        if (iRet >= FaceInterface.cw_errcode_t.CW_EMPTY_FRAME_ERR)
        {
            return -1;

        }
        if (iRet < 1)//无人脸

        {
            if (cardType == ConStant.CARD_READER.TYPE_HD){
                noFaceCount++;
                //连续5帧画面无人脸 则关闭补光灯
                if (noFaceCount > 5){
                    HdxUtil.SetCameraBacklightness(0);
                }
            }
            faceExistCallback.getFaceNumber(false);
            faceInfoCallback.detectFaceInfo(null, 0);
            faceInfoCallback.detectFaceInfo(faceInfos, 0);
            return -1;
        }

        if (cardType == ConStant.CARD_READER.TYPE_HD){
            noFaceCount = 0;
            HdxUtil.SetCameraBacklightness(1);
        }

        FaceInfo faceInfo;
        FaceInfo faceInfo2;
        // 提取filed特征
        // 提取检测到的第一张人脸，为最大人脸
        faceInfo = faceInfos[0];
        faceInfo2 = faceInfos[1];
        if (faceInfo !=null && faceInfo.x!= 0)
        {
            faceNum = 1;
            if (faceInfo2.x != 0 && faceInfo2.y != 0){
                faceNum = 2;
            }
        }

        if (faceNum > 0 ) {
            faceInfoCallback.detectFaceInfo(faceInfos, faceNum);
            faceExistCallback.getFaceNumber(true);
        } else {
            faceInfoCallback.detectFaceInfo(null, 0);
            faceInfoCallback.detectFaceInfo(faceInfos, 0);
            faceExistCallback.getFaceNumber(false);
        }
        return iRet;
    }


    public void stopGetFeature(boolean stop){
        this.stopGetFeature = stop;
    }



    private void Mirror(byte[] src, int w, int h) { //src是原始yuv数组
        int i;
        int index;
        byte temp;
        int a, b;
        //mirror y
        for (i = 0; i < h; i++) {
            a = i * w;
            b = (i + 1) * w - 1;
            while (a < b) {
                temp = src[a];
                src[a] = src[b];
                src[b] = temp;
                a++;
                b--;
            }
        }

        // mirror u and v
        index = w * h;
        for (i = 0; i < h / 2; i++) {
            a = i * w;
            b = (i + 1) * w - 2;
            while (a < b) {
                temp = src[a + index];
                src[a + index] = src[b + index];
                src[b + index] = temp;

                temp = src[a + index + 1];
                src[a + index + 1] = src[b + index + 1];
                src[b + index + 1] = temp;
                a+=2;
                b-=2;
            }
        }
    }

    @Override
    public void onPreviewFrame(byte[] data, Camera camera) {

        if (cardExist){
            cardCallback.getPic(data);
            cardExist = false;
        }
        if ( camera!= null){
            Camera.Parameters param = camera.getParameters();
            Camera.Size sz = param.getPreviewSize();//为了改变预览的大小
            if (!stopGetFeature){
                //Mirror(data,sz.width,sz.height);
                //这个编码以及检测的过程可以放在另一个线程中
                YuvImage img = new YuvImage(data, param.getPreviewFormat(), sz.width, sz.height, null);
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                Matrix matrix = new Matrix();
                matrix.postScale(-1,1);
                img.compressToJpeg(new Rect(0, 0, sz.width, sz.height), 90, output);
                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inPreferredConfig = Bitmap.Config.RGB_565;
                //为了加速框脸，适当缩放
                opt.inSampleSize = RATE;
                Bitmap bmp = BitmapFactory.decodeByteArray(output.toByteArray(), 0, output.size(), opt);
                // final Bitmap detBmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //由于没有购买授权码，检测通道数受到限制，检测人脸和比对等功能都只能放到主线程
                try {
                    getFaceRect(bmp);
                    bmp = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                Log.d(TAG, "stop onPreviewFrame:" );
            }

        }
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {

    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (holder.getSurface() == null) {
            return;
        }
        stopCameraPreview();
        showCameraPreview();
        //MainActivity activity = (MainActivity) context;
        //activity.setViewSize(width,height);
        verifyFragment.setViewSize(width,height);
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        stopCameraPreview();
    }

    /**
     * 打开摄像头开始预览，但是并未开始识别
     */
    public void startCamera() {
        if (mCamera != null) {
            return;
        }

        try {
            mCamera = Camera.open(caremaId);
        } catch (Exception e) {
            Log.e(TAG, "StartCamera: fail" );
        }
        setCamera(mCamera);
    }

    /**
     * 关闭摄像头预览，并且隐藏扫描框
     */
    public void stopCamera() {
        if (mCamera != null) {
            stopCameraPreview();

            mCamera.release();
            mCamera = null;
        }
    }



    public void stopCameraPreview() {
        if (mCamera != null) {
            try {

                mPreviewing = false;
                mCamera.cancelAutoFocus();
                mCamera.setPreviewCallback(null);
                mCamera.stopPreview();
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
    }

    public void setCamera(Camera camera) {
        mCamera = camera;
        if (mCamera != null) {
            mCameraConfigurationManager = new CameraConfigurationManager(getContext());

            getHolder().addCallback(this);
            if (mPreviewing) {
                requestLayout();
            } else {
                showCameraPreview();
            }
        }
    }

    public void showCameraPreview() {
        if (mCamera != null) {
            try {
                Log.e("","showCamera ");
                mPreviewing = true;
                mCamera.setPreviewDisplay(getHolder());
                int reqPrevW =ConStant.CAMERA_SET.PREVIEW_WIDTH;
                        int reqPrevH = ConStant.CAMERA_SET.PREVIEW_HEIGHT;
                mCameraConfigurationManager.setCameraParametersForPreviewCallBack(mCamera, caremaId, reqPrevW,
                        reqPrevH);
                mCamera.startPreview();
                mCamera.setPreviewCallback(this);
            } catch (Exception e) {
                Log.e("","showCamera fail");
            }
        }
    }

    public void takePic(){
        try {
            mCameraConfigurationManager.tackPicture(mCamera,this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onCapture(byte[] jpgdata) {

    }
}
