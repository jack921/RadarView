package com.example.jack.radarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class RadarView extends View{
    public Paint mBroadPaint=new Paint();
    public List<String> cornerName=new ArrayList<>();

    private int broad_color=0;
    public int corner_textSize=(int) TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP,16,getResources().getDisplayMetrics());

    public RadarView(Context context) {
        this(context,null);
    }

    public RadarView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public RadarView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        TypedArray typedArray=context.getTheme().obtainStyledAttributes(attrs,R.styleable.RadarView,defStyleAttr,0);
        int numCount=typedArray.getIndexCount();

        for(int i=0;i<numCount;i++){
            int attr=typedArray.getIndex(i);
            switch(attr){
                case R.styleable.RadarView_broad_color:
                    broad_color=typedArray.getColor(attr, Color.parseColor("#d1d1d1"));
                    break;
                case R.styleable.RadarView_broad_text_size:
                    corner_textSize=typedArray.getDimensionPixelSize(attr,(int) TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP,16,getResources().getDisplayMetrics()));
                    break;
            }
        }
        typedArray.recycle();
        initValue();
    }

    public void initValue(){
        mBroadPaint.setColor(broad_color);
        mBroadPaint.setStyle(Paint.Style.STROKE);
        mBroadPaint.setStrokeWidth(6f);
        mBroadPaint.setAntiAlias(true);
    }

    public void setCornerName(List<String> cornerList) {
        if(this.cornerName.size()==0){
            this.cornerName.addAll(cornerList);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthModel=MeasureSpec.getMode(widthMeasureSpec);
        int heightModel=MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth=MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight=MeasureSpec.getSize(heightMeasureSpec);
        int width; int height;
        if(widthModel==MeasureSpec.EXACTLY){
            width=measureWidth;
        }else{
            width=getPaddingLeft()+getPaddingRight()+measureWidth;
        }
        if(heightModel==MeasureSpec.EXACTLY){
            height=measureHeight;
        }else{
            height=(getPaddingTop()+getPaddingBottom()+measureHeight)/2;
        }
        setMeasuredDimension(width,height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth()/2,getHeight()/2);
        canvas.save();
        float radius=getHeight()/3;
        drawRadarBroad(canvas,radius);
        drawRadarBroad(canvas,radius*((float)3/4));
        drawRadarBroad(canvas,radius*((float)1/2));
        drawRadarBroad(canvas,radius*((float)1/4));
        drawPointLine(canvas,radius);
        drawText(canvas,radius);

    }

    /**
     * 画出五条中心点对边
     * @param canvas
     * @param radius
     */
    public void drawPointLine(Canvas canvas,float radius){
        Path path=new Path();
        path.moveTo(0,0);
        path.lineTo(0,-radius);
        canvas.drawPath(path, mBroadPaint);

        double[] rightTop=getTopAngle(radius);
        Path path2=new Path();
        path2.moveTo(0,0);
        path2.lineTo(Double.valueOf(rightTop[0]).floatValue(),-Double.valueOf(rightTop[1]).floatValue());
        canvas.drawPath(path2, mBroadPaint);

        Path path3=new Path();
        path3.moveTo(0,0);
        path3.lineTo(-Double.valueOf(rightTop[0]).floatValue(),-Double.valueOf(rightTop[1]).floatValue());
        canvas.drawPath(path3, mBroadPaint);

        double[] rightBottom=getBottomAngle(radius);
        Path path4=new Path();
        path4.moveTo(0,0);
        path4.lineTo(-Double.valueOf(rightBottom[0]).floatValue(),Double.valueOf(rightBottom[1]).floatValue());
        canvas.drawPath(path4, mBroadPaint);

        Path path5=new Path();
        path5.moveTo(0,0);
        path5.lineTo(-Double.valueOf(-rightBottom[0]).floatValue(),Double.valueOf(rightBottom[1]).floatValue());
        canvas.drawPath(path5, mBroadPaint);
    }

    /**
     * 画出雷达图的边
     * @param canvas
     * @param radius
     */
    public void drawRadarBroad(Canvas canvas,float radius){
        Path path=new Path();
        //上
        path.moveTo(0,-radius);
        //右上
        double[] rightTop=getTopAngle(radius);
        path.lineTo(Double.valueOf(rightTop[0]).floatValue(),-Double.valueOf(rightTop[1]).floatValue());
        //右下
        double[] rightBottom=getBottomAngle(radius);
        path.lineTo(Double.valueOf(rightBottom[0]).floatValue(),Double.valueOf(rightBottom[1]).floatValue());
        //左下
        path.lineTo(-Double.valueOf(rightBottom[0]).floatValue(),Double.valueOf(rightBottom[1]).floatValue());
        //左上
        path.lineTo(-Double.valueOf(rightTop[0]).floatValue(),-Double.valueOf(rightTop[1]).floatValue());
        //上
        path.lineTo(0,-radius+2);
        canvas.drawPath(path, mBroadPaint);
    }

    /**
     * 画出雷达图的各个角的提示
     * @param canvas
     * @param radius
     */
    public void drawText(Canvas canvas,float radius){
        Paint paint=new Paint();
        paint.setTextSize(corner_textSize);
        paint.setColor(broad_color);
        double[] rightTop=getTopAngle(radius);
        double[] rightBottom=getBottomAngle(radius);
        for(int i=0;i<cornerName.size();i++){
            if(i==0){
                canvas.save();
                canvas.translate(0,-radius);
                float textWidth = paint.measureText(cornerName.get(i));
                float baseLineY = Math.abs(paint.ascent() + paint.descent())/2;
                canvas.drawText(cornerName.get(i),-textWidth/2,-baseLineY,paint);
                canvas.restore();
            }else if(i==1){
                canvas.save();
                canvas.translate(Double.valueOf(rightTop[0]).floatValue(),-Double.valueOf(rightTop[1]).floatValue());
                float textWidth2=paint.measureText(cornerName.get(i));
                float baseLiney2=Math.abs(paint.ascent()+paint.descent())/2;
                canvas.drawText(cornerName.get(i),textWidth2/5,baseLiney2,paint);
                canvas.restore();
            }else if(i==2){
                canvas.save();
                canvas.translate(Double.valueOf(rightBottom[0]).floatValue(),Double.valueOf(rightBottom[1]).floatValue());
                float textWidth3=paint.measureText(cornerName.get(i));
                float baseLiney3=Math.abs(paint.ascent()+paint.descent());
                canvas.drawText(cornerName.get(i),-textWidth3/3,baseLiney3+15,paint);
                canvas.restore();
            }else if(i==3){
                canvas.save();
                canvas.translate(Double.valueOf(-rightBottom[0]).floatValue(),Double.valueOf(rightBottom[1]).floatValue());
                float textWidth4=paint.measureText(cornerName.get(i));
                float baseLiney4=Math.abs(paint.ascent()+paint.descent());
                canvas.drawText(cornerName.get(i),-textWidth4/2,baseLiney4+15,paint);
                canvas.restore();
            }else if(i==4){
                canvas.save();
                canvas.translate(-Double.valueOf(rightTop[0]).floatValue(),-Double.valueOf(rightTop[1]).floatValue());
                float textWidth5=paint.measureText(cornerName.get(i));
                float baseLiney5=Math.abs(paint.ascent()+paint.descent())/2;
                canvas.drawText(cornerName.get(i),-textWidth5-10,baseLiney5,paint);
                canvas.restore();
            }
        }
    }

    /**
     * 计算左右上角的坐标
     * @param radius
     * @return
     */
    public double[] getTopAngle(float radius){
        double[] param=new double[2];
        param[0]=Math.sin(Math.toRadians(72))*radius;
        param[1]=Math.sin(Math.toRadians(18))*radius;
        return param;
    }

    /**
     * 计算左右下角的坐标
     * @param radius
     * @return
     */
    public double[] getBottomAngle(float radius){
        double[] param=new double[2];
        param[0]=Math.sin(Math.toRadians(36))*radius;
        param[1]=Math.sin(Math.toRadians(54))*radius;
        return param;
    }

    /**
     * 将dp转换为与之相等的px
     * @param context
     * @param dipValue
     * @return
     */
    public static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     *  将sp转换为px
     * @param context
     * @param spValue
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }


}
