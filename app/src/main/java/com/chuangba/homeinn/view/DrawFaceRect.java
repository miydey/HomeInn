package com.chuangba.homeinn.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

import com.chuangba.homeinn.R;

/**
 * Created by jinyh on 2017/3/6.
 * Modified by jinyh on 2017/3/8.
 */

public class DrawFaceRect extends View {
    private final static String TAG = "DrawFaceRect";
    private final static int len = 40;
    private final static int distance = 15;
    private int mColor;//人脸框颜色
    //图片大小
    private int pWidth;
    private int pHeight;
    //view的大小
    private int vWidth;
    private int vHeight;
    private float left;
    private float top;
    private float bottom;
    private float right;
    private float wRate;
    private float hRate;
    //屏幕大小
    private int sWidth = 1366;
    private int sHeight = 768;
    private Bitmap bitmap;
    private int bitmapHeight;
    private int bitmapWidth;
    Matrix matrix ;
    private int[] colors = new int[] { 0xffede4a6,0xffffffff };
    private static Paint paint_outer = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static Paint paint_main = new Paint(Paint.ANTI_ALIAS_FLAG);
    public DrawFaceRect(Context context,int color) {
        super(context);
        this.mColor = color;
        this.pWidth = 0;
        this.pHeight = 0;
        this.vHeight = 0;
        this.vWidth = 0;
        this.bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.rect);
        this.bitmapHeight = bitmap.getHeight();
        this.bitmapWidth = bitmap.getWidth();
        matrix = new Matrix();
        //initPaint();
    }

    private void initPaint() {
        paint_outer.setColor(mColor);
        paint_outer.setStyle(Paint.Style.STROKE);
        paint_outer.setStrokeWidth(5.0f);
        paint_main.setColor(mColor);
        paint_main.setStyle(Paint.Style.STROKE);
        paint_main.setStrokeWidth(3.0f);
//        paint_main.setShader(new LinearGradient(0, 0, 100, 100, colors, null,
//                Shader.TileMode.MIRROR));
    }



    //设置框的位置
    public void setPosition(float left, float top, float right, float bottom){

            this.wRate = vWidth;
            this.wRate = wRate / pWidth;
            this.hRate = vHeight;
            this.hRate = hRate / pHeight;
            this.left = wRate * left;
            this.right = wRate * right;
            this.top = hRate * top;
            this.bottom = hRate * bottom;
            this.invalidate();

    }

    public void setPosition(Rect rect){
        this.wRate = vWidth;
        this.wRate = wRate / pWidth;
        this.hRate = vHeight;
        this.hRate = hRate / pHeight;

        this.left = wRate * rect.left;
        this.right = wRate * rect.right;
        this.top = hRate * rect.top;
        this.bottom = hRate * rect.bottom;
        this.invalidate();
    }

    //设置控件大小
    public void setViewSize(int width, int height){
        this.vWidth = width;
        this.vHeight = height;
        sWidth = width;
        sHeight = height;
    }

    //设置图片大小
    public void setImageSize(int width, int height){
        this.pWidth = width;
        this.pHeight = height;
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        // 设置想要的大小
        long startTime = System.currentTimeMillis();
        if (right!=0 && left != 0){
            float newWidth = right - left;
            float newHeight = bottom - top;
            // 计算缩放比例
            float scaleWidth = (newWidth) / bitmapWidth;
            float scaleHeight = ( newHeight) / bitmapHeight;
            // 取得想要缩放的matrix参数
            Matrix matrix = new Matrix();
            matrix.postScale(scaleWidth, scaleHeight);
            // 得到新的图片
            Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, bitmapWidth, bitmapHeight, matrix,
                    true);
            canvas.drawBitmap(newbm,left,top,paint_main);
            //long finishTime = System.currentTimeMillis();
            //Log.e(TAG, String.valueOf(finishTime-startTime)+"ms 耗时");
        }

        //paint_outer.setMaskFilter(new BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID)); 高版本无效


       // paint_main.setMaskFilter(new BlurMaskFilter(10f, BlurMaskFilter.Blur.SOLID));
        ////画面分为五个矩形，中间人脸框不绘制灰色，周围绘制灰色
//        Paint greyPaint;
//        greyPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
//        greyPaint.setColor(getResources().getColor(R.color.backGrey));
//        greyPaint.setStyle(Paint.Style.FILL);
//        greyPaint.setAlpha(180);

//        canvas.drawRect(0,0,sWidth,top,greyPaint);
//        canvas.drawRect(0,bottom,sWidth,sHeight,greyPaint);
//        canvas.drawRect(0,top,left,bottom,greyPaint);
//        canvas.drawRect(right,top,sWidth,bottom,greyPaint);

       // canvas.drawRect(left, top, right, bottom, paint_main);


        /** 双重框框的绘制 **/

//            canvas.drawRect(new RectF(left+distance, top+distance, right-distance, bottom-distance), paint_main);
//
//            canvas.drawLine(left, top, left + len, top, paint_outer);  //y不变，左上角横线
//            canvas.drawLine(left, top, left, top + len, paint_outer);  //x不变 ，左上角竖线
//            canvas.drawLine(left, bottom, left + len, bottom, paint_outer);
//            canvas.drawLine(left, bottom, left , bottom - len, paint_outer);
//
//            canvas.drawLine(right-len, top, right, top, paint_outer);
//            canvas.drawLine(right, top, right, top + len, paint_outer);
//            canvas.drawLine(right -len, bottom, right , bottom, paint_outer);
//            canvas.drawLine(right, bottom, right, bottom - len, paint_outer);
    }
}
