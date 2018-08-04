package com.example.jack.radarview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Scroller;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadarView extends View{
    private Paint mBroadPaint=new Paint();
    private Paint mMarkEasePaint =new Paint();
    private Paint mMarkPaint=new Paint();
    private Paint mCircleHoldPaint=new Paint();
    private Paint mDrawTextPaint=new Paint();

    private int broad_color=Color.parseColor("#d1d1d1");
    private int mark_color=Color.parseColor("#7cfc00");
    private int mark_broad_color=Color.parseColor("#7cfc00");

    private float mBroadStrokeWidth=1.5f;
    private float mMarkBroadStrokeWidth=1.5f;
    private int corner_textSize;
    private int mMarkEaseAlpha=70;
    private int mBroadAlpha=0;

    private List<String> cornerName=new ArrayList<>();
    private List<Float> listData=new ArrayList<>();

    private float maxValue=0f;
    private Float radius=0f;

    private float FLIP_DISTANCE = 50;
    private GestureDetector mDetector;
    private Display display =null;
    private Scroller scroller;

    private float[] listAngle;


    public RadarView(Context context) {
        this(context,null);
    }

    public RadarView(Context context, AttributeSet attrs){
        this(context,attrs,0);
    }

    public RadarView(Context context,AttributeSet attrs,int defStyleAttr){
        super(context,attrs,defStyleAttr);
        display=((WindowManager)getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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

    public void setData(List<Float> listData){
        this.listData.clear();
        this.listData.addAll(listData);
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void setmBroadStrokeWidth(float mBroadStrokeWidth) {
        this.mBroadStrokeWidth = mBroadStrokeWidth;
    }
    public void setmMarkBroadStrokeWidth(float mMarkBroadStrokeWidth) {
        this.mMarkBroadStrokeWidth = mMarkBroadStrokeWidth;
    }

    public void setMarkEaseAlpha(int markEaseAlpha) {
        this.mMarkEaseAlpha = markEaseAlpha;
    }

    public void setCorneTextSize(int cornerTextSize){
        this.corner_textSize=cornerTextSize;
    }

    public void setBroadColor(int broadColor){
        this.broad_color=broadColor;
    }

    public void setMarkColor(int markColor){
        this.mark_color=markColor;
    }

    public void setMarkBroadColor(int markBroadColor){
        this.mark_broad_color=mark_broad_color;
    }

    public void setCornerName(List<String> cornerList) {
        if(this.cornerName.size()==0){
            this.cornerName.addAll(cornerList);
        }
    }

    public void initValue(){
        scroller=new Scroller(getContext());
        mDetector=new GestureDetector(getContext(),mGestureListener);
        corner_textSize=(int)TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP,16,getResources().getDisplayMetrics());

        mBroadPaint.setColor(broad_color);
        mBroadPaint.setStyle(Paint.Style.STROKE);
        mBroadPaint.setStrokeWidth(mBroadStrokeWidth);
        mBroadPaint.setAntiAlias(true);

        mMarkPaint.setColor(mark_broad_color);
        mMarkPaint.setStyle(Paint.Style.STROKE);
        mMarkPaint.setStrokeWidth(mMarkBroadStrokeWidth);
        mMarkPaint.setAntiAlias(true);

        mMarkEasePaint.setAntiAlias(true);
        mMarkEasePaint.setColor(mark_color);
        mMarkEasePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mMarkEasePaint.setAlpha(mMarkEaseAlpha);

        mCircleHoldPaint=new Paint();
        mCircleHoldPaint.setAntiAlias(true);
        mCircleHoldPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mDrawTextPaint=new Paint();
        mDrawTextPaint.setTextSize(corner_textSize);
        mDrawTextPaint.setColor(broad_color);
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
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        radius=(float)Math.min(width,height)/3;
        float tempRedius=(float)360/listData.size();
        listAngle=new float[listData.size()];
        for(int i=0;i<listData.size();i++){
            listAngle[i]=tempRedius*(i+1);
        }
        loadStartAnimator();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(getWidth()/2,getHeight()/2);
        canvas.rotate(180);
        canvas.save();
        //画雷达图各个边
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

    /**
     * 画出雷达图的边
     * @param canvas
     * @param radius
     * @param angle
     */
    public void drawRadarBroad(Canvas canvas,float radius){
        Path path=new Path();
        for(int i=0;i<listAngle.length;i++){
            float[] temp=getAngle(radius,listAngle[i]);
            if(i==0){
                path.moveTo(temp[0],temp[1]);
            }else{
                path.lineTo(temp[0],temp[1]);
            }
        }
        float[] lastPoint=getAngle(radius,listAngle[0]);
        path.lineTo(lastPoint[0],lastPoint[1]);
        canvas.drawPath(path,mBroadPaint);
    }

    /**
     * 画出五条中心点对边
     * @param canvas
     * @param radius
     */
    public void drawPointLine(Canvas canvas,float radius){
        for(int i=0;i<listAngle.length;i++){
            Path path=new Path();
            path.moveTo(0,0);
            float[] temp=getAngle(radius,listAngle[i]);
            path.lineTo(temp[0],temp[1]);
            mBroadPaint.setAlpha(mBroadAlpha);
            canvas.drawPath(path, mBroadPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
    }

    private GestureDetector.SimpleOnGestureListener mGestureListener=new GestureDetector.SimpleOnGestureListener(){
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float tempRadius=0;
            String type="123";
            if (e1.getX()-e2.getX() > FLIP_DISTANCE) {//向左滑
                type="向左滑";
                tempRadius=-(360*((e1.getX()-e2.getX())/display.getWidth()));
            }else if (e2.getX()-e1.getX() > FLIP_DISTANCE) {//向右滑
                type="向右滑";
                tempRadius=360*((e2.getX()-e1.getX())/display.getWidth());
            }else if (e1.getY()-e2.getY() > FLIP_DISTANCE) {//向上滑
                type="向上滑";
                tempRadius=-(360*((e1.getY()-e2.getY())/display.getHeight()));
            }else if (e2.getY()-e1.getY() > FLIP_DISTANCE) {//向下滑
                type="向下滑";
                tempRadius=360*((e2.getY()-e1.getY())/display.getWidth());
            }
            if(tempRadius==0){
                return true;
            }
            for(int i=0;i<listAngle.length;i++){
                listAngle[i]-=(tempRadius/20);
            }
            Log.e("type",type+"");
            Log.e("type2",tempRadius+"");
            postInvalidate();
            return true;
        }
        @Override
        public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float velocityY) {
            int mini_width=120;
            int mini_speed=0;
            float distance_right=arg1.getX()-arg0.getX();
            float distance_left=arg0.getX()-arg1.getX();
            float distance_down=arg1.getY()-arg0.getY();
            float distance_up=arg0.getY()-arg1.getY();
            if(distance_right>mini_width && Math.abs(arg2)>mini_speed){
                Log.e("", "onFling-"+"向右滑动");
            } else if(distance_left>mini_width && Math.abs(arg2)>mini_speed){
                Log.e("", "onFling-"+"向左滑动");
            } else if(distance_down>mini_width && Math.abs(arg2)>mini_speed){
                Log.e("", "onFling-"+"向下滑动");
            } else if(distance_up>mini_width && Math.abs(arg2)>mini_speed){
                Log.e("", "onFling-"+"向上滑动");
            }

            return true;
        }
    };

    /**
     * 根据距离差判断 滑动方向
     * @param dx X轴的距离差
     * @param dy Y轴的距离差
     * @return 滑动的方向
     */
    private int getOrientation(float dx, float dy) {
        Log.e("Tag","========X轴距离差："+dx);
        Log.e("Tag","========Y轴距离差："+dy);
        if (Math.abs(dx)>Math.abs(dy)){
            //X轴移动
            return dx>0?'r':'l';
        }else{
            //Y轴移动
            return dy>0?'b':'t';
        }
    }

    /**
     * 画出数值区域
     * @param canvas
     * @param radius
     */
    public void drawData(Canvas canvas,float radius){
        if(maxValue==0){
            maxValue=Collections.max(listData);
        }
        Path path=new Path();
        for(int i=0;i<listAngle.length;i++){
            float tempRadius=(listData.get(i)/maxValue)*radius;
            float[] tempAngle=getAngle(tempRadius,listAngle[i]);
            if(i==0){
                path.moveTo(tempAngle[0],tempAngle[1]);
            }else{
                path.lineTo(tempAngle[0],tempAngle[1]);
            }
        }
        path.close();
        canvas.drawPath(path,mMarkEasePaint);
        canvas.drawPath(path,mMarkPaint);
    }

    /**
     * 画出各个点
     * @param canvas
     * @param radius
     */
    public void circleHoldPaint(Canvas canvas,float radius){
        if(maxValue==0){
            maxValue=Collections.max(listData);
        }
        for(int i=0;i<listAngle.length;i++){
            float tempRadius= (listData.get(i)/maxValue)*radius;
            float[] tempAngle=getAngle(tempRadius,listAngle[i]);
            canvas.drawCircle(tempAngle[0],tempAngle[1],5,mCircleHoldPaint);
        }
    }

    /**
     * 通过各个边得到各个点
     * @param radius
     * @param angle
     * @return
     */
    public float[] getAngle(float radius,float angle){
        float[] param=new float[2];
        param[0]=(float) Math.sin(Math.toRadians(angle))*radius;
        param[1]=(float) Math.cos(Math.toRadians(angle))*radius;
        return param;
    }

    /**
     * 画出雷达图的各个角的提示
     * @param canvas
     * @param radius
     */
    public void drawText(Canvas canvas,float radius){
       for(int i=0;i<listAngle.length;i++){
           canvas.save();
           float[] temp=getAngle(radius,listAngle[i]);
           canvas.translate(temp[0],temp[1]);
           float textWidth = mDrawTextPaint.measureText(cornerName.get(i));
           float baseLineY = Math.abs(mDrawTextPaint.ascent()+mDrawTextPaint.descent())/2;
           canvas.rotate(-180);
           if(((int)temp[0])==0){
               canvas.drawText(cornerName.get(i),-textWidth/2,-baseLineY,mDrawTextPaint);
           }else{
               canvas.drawText(cornerName.get(i),temp[0]>0?-textWidth:0,temp[1]>0?-baseLineY:baseLineY*2,mDrawTextPaint);
           }
           canvas.restore();
       }
    }

    /**
     * 数字转化为dp
     * @param value
     * @return
     */
    public int convertDpToPixel(float value){
        return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                value,getResources().getDisplayMetrics());
    }

    /**
     * 创建动画
     */
    public void loadStartAnimator(){
        ValueAnimator alphaAnimator=ValueAnimator.ofInt(0,225);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mBroadAlpha=(int)valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        final ValueAnimator radiusAnimator=ValueAnimator.ofFloat(0,radius);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                radius=(Float)radiusAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        alphaAnimator.setDuration(3000);
        radiusAnimator.setDuration(3000);
        alphaAnimator.start();
        radiusAnimator.start();
    }

}
