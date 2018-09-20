package com.example.sunny.up_education_board;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.Random;

/**
 * Created by Sunny on 4/5/2017.
 */

public class PieGraph extends View {
    private Paint piePaint; // to draw pie graph
    private RectF rectF;    // view within which pie graph will be drawn
    private float[] data;      // data set to be used to draw slices of pie graph

    public PieGraph(Context context, AttributeSet attrs){
            super(context,attrs);
            piePaint = new Paint();
                piePaint.setAntiAlias(true);
                piePaint.setDither(true);
                piePaint.setStyle(Paint.Style.FILL);
    }
    public void setData(float[] data){
            this.data = data;
            invalidate();
    }
    private float[] pieSegment(){
           float[] segValues = new float[this.data.length];
        float Total = getTotal();
           for (int i = 0; i < this.data.length; i++){
                segValues[i] = (this.data[i]/Total) * 360;
        }
        return segValues;
    }
    private float getTotal(){
            float total = 0;
        for (float val : this.data){
                total +=val;
        }
        return total;
    }
    @Override
    protected void onDraw(Canvas canvas){
           if (data != null){
                 int top = 0;
            int left = 0;
            int endBottom = getHeight();
            int endRight = endBottom;
                    rectF = new RectF(left, top, endRight, endBottom);
            float[] segment = pieSegment();
            float segStartPoint = 0;
                for (int i = 0; i < segment.length; i++){

                        Random rnd = new Random();
                int color = Color.argb(255, (int)segment[i], 200, 211);
                        piePaint.setColor(color);
                        canvas.drawArc(rectF, segStartPoint, segment[i], true, piePaint);
                        segStartPoint += segment[i];
            }
        }
    }
}
