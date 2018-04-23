package com.chuangba.homeinn.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

public class CountDownAnimatorView extends View {
    final float radius = 80f;
    float progress = 0;
    RectF arcRectF = new RectF();
    Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    int degree;//刻度
    int graduation = 135;//自定义开始格
    float textSize = 40f;
    float roundSize = 15f;
    int secondCount = 5;
    public CountDownAnimatorView(Context context) {
        super(context);
    }

    public CountDownAnimatorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public CountDownAnimatorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    {
        paint.setTextSize(textSize);
        paint.setTextAlign(Paint.Align.CENTER);
    }
    public void setTextSize(float size){
        textSize = size;
    }
   public void setGraduation(int graduation){
       this.graduation = graduation;
   }
    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
        invalidate();
    }
    public void setSecondCount(int secondCount){
        this.secondCount = secondCount;
    }
    public void setRoundSize(float round){
        this.roundSize = round;
        invalidate();
    }
    public void setDegree( int degree){
        this.degree = degree;
    }
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;
        paint.setColor(Color.rgb(255,10,10));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        paint.setStrokeWidth(roundSize);
        arcRectF.set(centerX - radius, centerY - radius, centerX + radius, centerY + radius);
        canvas.drawArc(arcRectF, graduation, progress * 89f, false, paint);
        paint.setColor(Color.WHITE);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(String.valueOf(secondCount - (int)progress) , centerX, centerY - (paint.ascent() + paint.descent()) / 2, paint);
    }
}
