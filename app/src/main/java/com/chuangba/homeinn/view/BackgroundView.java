package com.chuangba.homeinn.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by jinyh on 2017/9/19.
 */

public class BackgroundView extends View {


    int top;
    int left;
    int bottom;
    int right;
    int WIDTH;
    int HEIGHT;
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public BackgroundView(Context context) {
        super(context);
    }

    public BackgroundView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BackgroundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setSize(int realWidth,int realHeight){
        WIDTH = realWidth;
        HEIGHT = realHeight;
    }
    public float [] getPosition(){
        float hRate = this.getHeight()/HEIGHT;
        float wRate = this.getWidth()/WIDTH;

            Log.e("Rate", "getPosition: "+wRate );
            //return new float[]{left/wRate,top/hRate,this.getWidth()/2/wRate,this.getHeight()*2/3/hRate};
            return new float[]{left/wRate, top, this.getWidth()/2/wRate, this.getHeight()/hRate};
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (this.getHeight()<this.getWidth()){
            top = 0;
            Log.e("top", String.valueOf(top));
            left = this.getWidth()/4;
            Log.e("left", String.valueOf(left));
            //bottom = top+this.getHeight()*2/3;
            bottom = this.getHeight();
            Log.e("bottom", String.valueOf(bottom));
            right = left+this.getWidth()/2;
            Log.e("right", String.valueOf(bottom));
            paint.setColor(Color.argb(200,66,66,66));
            canvas.drawRect(0,0,this.getWidth(),top,paint);
            canvas.drawRect(0,bottom,this.getWidth(),this.getHeight(),paint);
            canvas.drawRect(0,top,left,bottom,paint);
            canvas.drawRect(right,top,this.getWidth(),bottom,paint);
            //canvas.drawLine(left,0,left,bottom,);
        } else {
            top = getHeight()/6;
            Log.e("top", String.valueOf(top));
            left = 0;
            Log.e("left", String.valueOf(left));
            //bottom = top+this.getHeight()*2/3;
            bottom = this.getHeight()*5/6;
            Log.e("bottom", String.valueOf(bottom));
            right = this.getWidth();
            Log.e("right", String.valueOf(bottom));
            paint.setColor(Color.argb(100,66,66,66));
            canvas.drawRect(0,0,this.getWidth(),top,paint);
            canvas.drawRect(0,bottom,this.getWidth(),this.getHeight(),paint);
            canvas.drawRect(0,top,left,bottom,paint);
            canvas.drawRect(right,top,this.getWidth(),bottom,paint);
        }

    }
}
