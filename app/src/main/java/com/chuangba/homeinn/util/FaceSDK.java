package com.chuangba.homeinn.util;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;

import com.chuangba.homeinn.ui.ConStant;
import com.chuangba.homeinn.bean.VerifyRst;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import cn.face.sdk.FaceDetTrack;
import cn.face.sdk.FaceInfo;
import cn.face.sdk.FaceInterface;
import cn.face.sdk.FaceParam;
import cn.face.sdk.FaceRecog;

import static cn.face.sdk.FaceDetTrack.cwFaceDetection;

/**
 * Created by jinyh on 2017/4/6.
 *
 * 用于采集相机人脸特征，身份证人脸特征，比对
 */

public class FaceSDK {
    private static final String TAG = FaceSDK.class.getSimpleName();
    private String pLicence;
    private String sModelPath;
    private static FaceSDK faceSDK;
    private FaceDetTrack faceDetTrack; //检测
    private FaceRecog faceRecog;  //识别，比对都是这个
    int iDetHandle = -1;      // 比对句柄
    int iRecogHandle = -1;    // 识别句柄
    int iFeaLen = 0;          // 特征长度，不会变

    byte[] btFeaFiled = null; //画面人脸特征
    byte[] btFeaProbe = null; //身份证人脸特征

    private FaceSDK(){

    }

    public static FaceSDK getInstance(){
        if (faceSDK == null)
            faceSDK = new FaceSDK();
        return faceSDK;
    }


    public void createHandles() {
        faceDetTrack = new FaceDetTrack();
        faceRecog = new FaceRecog();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String sdPath = Environment.getExternalStorageDirectory().getAbsolutePath();
        ConStant.publicFilePath = new StringBuilder(sdPath).append(File.separator).append("cwFaceSDK")
                .append(File.separator).append(sdf.format(new Date())).toString();
        FileUtil.mkDir(ConStant.publicFilePath);
        // String sModelPath = new StringBuilder(sdPath).append(File.separator).append("CWModels").append(File.separator).append("CWR_Config_1_1.xml").toString();
        String sModelPath = new StringBuilder(sdPath).append(File.separator).append("mycwpath").append(File.separator).append("CWR_Config_1_1.xml").toString();

        // 需要注意的是，创建句柄操作比较耗时间，使用时可以将该操作放到线程里面
        // sharedPreferences = getSharedPreferences(getResources().getString(R.string.storage_key),MODE_PRIVATE);
        //sharedPreferences = faceApplication.getShare();
        //editor = sharedPreferences.edit();
        //String licen = sharedPreferences.getString("licence","11");
        //iDetHandle =  faceDetTrack.cwCreateDetHandleFromMem(licen);
        //iRecogHandle = faceRecog.cwCreateRecogHandle(sModelPath, licen , 0, -1);
        iDetHandle =  faceDetTrack.cwCreateDetHandleFromMem(ConStant.sLicence);
        iRecogHandle = faceRecog.cwCreateRecogHandle(sModelPath, ConStant.sLicence , 0, -1);

        // 人脸检测参数，最小人脸默认100，最大默认400
        FaceParam param = new FaceParam();
        if (FaceInterface.cw_errcode_t.CW_OK == faceDetTrack.cwGetFaceParam(iDetHandle, param))
        {
            param.minSize = 20;
            param.maxSize = 600;
            param.maxFaceNumPerImg = 2;
            param.globleDetFreq = 60;
            faceDetTrack.cwSetFaceParam(iDetHandle, param);
        }

        iFeaLen = faceRecog.cwGetFeatureLength(iRecogHandle);
    }


    public void destroyHandles(){
        if (iDetHandle!=-1){
            faceDetTrack.cwReleaseDetHandle(iDetHandle);
            faceRecog.cwReleaseRecogHandle(iRecogHandle);
        }

    }

    //从画面提取特征
    public FaceInfo getFeatureFromSurface(byte[] data,int width,int height ) throws IOException
    {
        btFeaFiled = new byte[iFeaLen];
        btFeaProbe = new byte[iFeaLen];

        FaceInfo[] pFaceBuffer = new FaceInfo[1];

        pFaceBuffer[0] = new FaceInfo();

        int iRet = cwFaceDetection(iDetHandle, data,width, height, FaceInterface.cw_img_form_t.CW_IMAGE_YUV420P, 0, 0,
                FaceInterface.cw_op_t.CW_OP_DET | FaceInterface.cw_op_t.CW_OP_ALIGN, pFaceBuffer);

        if (iRet >= FaceInterface.cw_errcode_t.CW_EMPTY_FRAME_ERR)
        {
            Log.e(TAG, "getFeatureFromSurface:   "+iRet );

            return null;
        }
        if (iRet < 1)//无人脸
        {
            return null;
        }
        // 提取filed特征
        // 提取检测到的第一张人脸，为最大人脸
        Log.d(TAG, "cwGetFiledFeature" );
        iRet = faceRecog.cwGetFiledFeature(iRecogHandle, pFaceBuffer[0].alignedData, pFaceBuffer[0].alignedW,
                pFaceBuffer[0].alignedH, pFaceBuffer[0].nChannels, 1, btFeaFiled, iFeaLen);

        return pFaceBuffer[0];
    }

        //获取身份证人脸特征

    public int getFeatureFromCard(Bitmap bitmap) throws IOException
    {

        byte[] imgData = ImgUtil.bitmapToByte(bitmap, Bitmap.CompressFormat.JPEG,100);
        FaceInfo[] pFaceBuffer = new FaceInfo[1];
        for (int i = 0; i < 1; i++)
        {
            pFaceBuffer[0] = new FaceInfo();
        }
        int iRet = cwFaceDetection(iDetHandle, imgData, 0, 0, FaceInterface.cw_img_form_t.CW_IMAGE_BINARY, 0, 0,
                FaceInterface.cw_op_t.CW_OP_DET | FaceInterface.cw_op_t.CW_OP_ALIGN, pFaceBuffer);
        if (iRet >= FaceInterface.cw_errcode_t.CW_EMPTY_FRAME_ERR)
        {
            return -1;
        }
        if (iRet < 1)
        {
            return -1;
        }
        if (iRet == 1){
            // 提取读卡器里的特征，为最大人脸
            faceRecog.cwGetProbeFeature(iRecogHandle, pFaceBuffer[0].alignedData, pFaceBuffer[0].alignedW,
                    pFaceBuffer[0].alignedH, pFaceBuffer[0].nChannels, 1, btFeaProbe, iFeaLen);
            Log.d(TAG, "GetFeatureFromPath: " );
            return 0;
        }else
            return -1;

    }

    public VerifyRst compare(float threshold){
        float score ;
        VerifyRst rst = new VerifyRst();
        float[] pScores = new float[1];

        int iRet = faceRecog.cwComputeMatchScore(iRecogHandle, btFeaFiled, iFeaLen, 1, btFeaProbe, iFeaLen, 1, pScores);
        if (iRet == FaceInterface.cw_errcode_t.CW_OK)
        {
            Log.d(TAG, "compareFace: "+pScores[0] );
             score = pScores[0];
            if (score > 0.979  ){ //相似度过大是云从bug
                Random run = new Random();
                score =  ((run.nextFloat())*0.2f+0.3f);
            }
            rst.score = score;

        }else {
            Log.e(TAG, "compare: failed" );
            rst.success = false;
            rst.score = 0f;
            return rst;
        }
        btFeaFiled = null;
        btFeaProbe = null;
        if (score >= threshold)
            rst.success = true;
        else
            rst.success = false;
        return rst;
    }


}
