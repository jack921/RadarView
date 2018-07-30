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
import java.util.Collections;
import java.util.List;

public class RadarView extends View{
    private Paint mBroadPaint=new Paint();
    private Paint mMarkEasePaint =new Paint();
    private Paint mMarkPaint=new Paint();
    private Paint mCircleHoldPaint=new Paint();

    private List<String> cornerName=new ArrayList<>();
    private List<Float> listData=new ArrayList<>();
    private int broad_color=Color.parseColor("#d1d1d1");
    private int mark_color=Color.parseColor("#7cfc00");
    private int mark_broad_color=Color.parseColor("#7cfc00");

    private int corner_textSize=(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
            16,getResources().getDisplayMetrics());

    private float maxValue=0f;

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
                case R.styleable.RadarView_mark_color:
                    broad_color=typedArray.getColor(attr, Color.parseColor("#7cfc00"));
                    break;
                case R.styleable.RadarView_mark_broad_color:
                    mark_broad_color=typedArray.getColor(attr,Color.parseColor("#7cfc00"));
                    break;

            }
        }
        typedArray.recycle();
        initValue();
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void setData(List<Float> listData){
        this.listData.clear();
        this.listData.addAll(listData);
    }

    public void initValue(){
        mBroadPaint.setColor(broad_color);
        mBroadPaint.setStyle(Paint.Style.STROKE);
        mBroadPaint.setStrokeWidth(1.5f);
        mBroadPaint.setAntiAlias(true);

        mMarkPaint.setColor(mark_broad_color);
        mMarkPaint.setStyle(Paint.Style.STROKE);
        mMarkPaint.setStrokeWidth(1.5f);
        mMarkPaint.setAntiAlias(true);

        mMarkEasePaint.setAntiAlias(true);
        mMarkEasePaint.setColor(mark_color);
        mMarkEasePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mMarkEasePaint.setAlpha(70);

        mCircleHoldPaint=new Paint();
        mCircleHoldPaint.setAntiAlias(true);
        mCircleHoldPaint.setStyle(Paint.Style.FILL_AND_STROKE);
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
        //画雷达图的边
        drawRadarBroad(canvas,radius);
        drawRadarBroad(canvas,radius*((float)3/4));
        drawRadarBroad(canvas,radius*((float)1/2));
        drawRadarBroad(canvas,radius*((float)1/4));
        //画雷达图各个边的连线
        drawPointLine(canvas,radius);
        //画雷达图的角的文字
        drawText(canvas,radius);
        //画出数值区域
        drawData(canvas,radius);
        //画出各个点
        circleHoldPaint(canvas,radius);
    }

    public void drawData(Canvas canvas,float radius){
        if(maxValue==0){
           maxValue=Collections.max(listData);
        }
        Path path=new Path();
        for(int i=0;i<listData.size();i++){
           float tempRadius= (listData.get(i)/maxValue)*radius;
           if(i==0){
               path.moveTo(0,-tempRadius);
               canvas.drawCircle(0,-tempRadius,5,mCircleHoldPaint);
           }else if(i==1){
               double[] rightTop=getTopAngle(tempRadius);
               path.lineTo(Double.valueOf(rightTop[0]).floatValue(),-Double.valueOf(rightTop[1]).floatValue());
               canvas.drawCircle(Double.valueOf(rightTop[0]).floatValue(),
                       -Double.valueOf(rightTop[1]).floatValue(),5,mCircleHoldPaint);
           }else if(i==2){
               double[] rightBottom=getBottomAngle(tempRadius);
               path.lineTo(Double.valueOf(rightBottom[0]).floatValue(),Double.valueOf(rightBottom[1]).floatValue());
               canvas.drawCircle(Double.valueOf(rightBottom[0]).floatValue(),
                       Double.valueOf(rightBottom[1]).floatValue(),5,mCircleHoldPaint);
           }else if(i==3){
               double[] leftBottom=getBottomAngle(tempRadius);
               path.lineTo(-Double.valueOf(leftBottom[0]).floatValue(),Double.valueOf(leftBottom[1]).floatValue());
               canvas.drawCircle(-Double.valueOf(leftBottom[0]).floatValue(),
                       Double.valueOf(leftBottom[1]).floatValue(),5,mCircleHoldPaint);
           }else if(i==4){
               double[] leftTop=getTopAngle(tempRadius);
               path.lineTo(-Double.valueOf(leftTop[0]).floatValue(),-Double.valueOf(leftTop[1]).floatValue());
               canvas.drawCircle(-Double.valueOf(leftTop[0]).floatValue(),
                       -Double.valueOf(leftTop[1]).floatValue(),5,mCircleHoldPaint);
               path.close();
           }
        }
        canvas.drawPath(path,mMarkEasePaint);
        canvas.drawPath(path,mMarkPaint);
    }

    public void circleHoldPaint(Canvas canvas,float radius){
        if(maxValue==0){
            maxValue=Collections.max(listData);
        }
        for(int i=0;i<listData.size();i++){
            float tempRadius= (listData.get(i)/maxValue)*radius;
            if(i==0){
                canvas.drawCircle(0,-tempRadius,5,mCircleHoldPaint);
            }else if(i==1){
                double[] rightTop=getTopAngle(tempRadius);
                canvas.drawCircle(Double.valueOf(rightTop[0]).floatValue(),
                        -Double.valueOf(rightTop[1]).floatValue(),5,mCircleHoldPaint);
            }else if(i==2){
                double[] rightBottom=getBottomAngle(tempRadius);
                canvas.drawCircle(Double.valueOf(rightBottom[0]).floatValue(),
                        Double.valueOf(rightBottom[1]).floatValue(),5,mCircleHoldPaint);
            }else if(i==3){
                double[] leftBottom=getBottomAngle(tempRadius);
                canvas.drawCircle(-Double.valueOf(leftBottom[0]).floatValue(),
                        Double.valueOf(leftBottom[1]).floatValue(),5,mCircleHoldPaint);
            }else if(i==4){
                double[] leftTop=getTopAngle(tempRadius);
                canvas.drawCircle(-Double.valueOf(leftTop[0]).floatValue(),
                        -Double.valueOf(leftTop[1]).floatValue(),5,mCircleHoldPaint);
            }
        }
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
        path5.close();

        canvas.drawPath(path5, mBroadPaint);
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



}
