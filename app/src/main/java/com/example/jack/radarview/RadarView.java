package com.example.jack.radarview;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RadarView extends View {
    private Paint mBroadPaint = new Paint();//边
    private Paint mMarkEasePaint = new Paint();//数值面积
    private Paint mMarkPaint = new Paint();//数值边
    private Paint mCircleHoldPaint = new Paint();//各个数值点
    private Paint mDrawTextPaint = new Paint();//各个角文字
    private Paint mHoldTextPaint = new Paint();//数值点文字
    private Paint mIntervalTextPaint = new Paint();//区间点

    public static final double CIRCLE_ANGLE = 2 * Math.PI;
    private int broad_color = Color.parseColor("#585858");//边的颜色
    private int broad_color_text = Color.parseColor("#88001B");//角的字体颜色
    private int mark_color = Color.parseColor("#FDECA6");//数值区域颜色
    private int mark_broad_color = Color.parseColor("#FFCA18");//数值边的颜色
    private int corner_hold_color = Color.parseColor("#EC1C24");//数组提示字体的颜色
    private int circle_hold_color = Color.parseColor("#008B8B");//数值区域点的颜色
    private int interval_text_color = Color.parseColor("#2F4F4F");//区间点的颜色

    private float mBroadStrokeWidth = 1.5f;//边的粗细
    private float mMarkBroadStrokeWidth = 1.5f;//数值区域边的粗细
    private int corner_textSize;//边角的字体的大小
    private int circle_hold_textSize;//数组提示字体的大小
    private int mMarkEaseAlpha = 70;//数值区域的透明度
    private int mBroadAlpha = 225;//各个边的连线的透明度
    private int mIntervalTextSize;//区间点的大小

    private List<String> cornerName = new ArrayList<>();//角的名字的集合
    private List<Float> listData = new ArrayList<>();
    private int angleStatus = 0;//角的状态
    private float maxValue = 0f;//最大值
    private Float radius = 0f;//画图的半径

    private float[] listAngle;//所有角的集合
    private boolean drawText = false;//画不画数组提示
    private long duration = 3000;//动画时间
    private boolean openDuration = true;//是否开启动画
    private boolean openDataEasePoint = true;//是否开启区域数值提示

    private int marginNum = 4;
    private float margin;

    private double mPerimeter;
    private float mFlingPoint;

    private GestureDetector mDetector;
    private Scroller scroller;

    public RadarView(Context context) {
        this(context, null);
    }

    public RadarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RadarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(attrs, R.styleable.RadarView, defStyleAttr, 0);
        int numCount = typedArray.getIndexCount();
        for (int i = 0; i < numCount; i++) {
            int attr = typedArray.getIndex(i);
            switch (attr) {
                case R.styleable.RadarView_broad_text_size:
                    corner_textSize = typedArray.getDimensionPixelSize(attr, (int)TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP,15,getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RadarView_circle_hold_textSize:
                    circle_hold_textSize = typedArray.getDimensionPixelSize(attr,(int)TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP,12,getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RadarView_interval_text_size:
                    mIntervalTextSize = typedArray.getDimensionPixelSize(attr, (int)TypedValue.applyDimension(
                            TypedValue.COMPLEX_UNIT_SP,10,getResources().getDisplayMetrics()));
                    break;
                case R.styleable.RadarView_mark_color:
                    mark_color = typedArray.getColor(attr, Color.parseColor("#FDECA6"));
                    break;
                case R.styleable.RadarView_mark_broad_color:
                    mark_broad_color = typedArray.getColor(attr, Color.parseColor("#FFCA18"));
                    break;
                case R.styleable.RadarView_corner_hold_color:
                    corner_hold_color = typedArray.getColor(attr, Color.parseColor("#EC1C24"));
                    break;
                case R.styleable.RadarView_broad_color_text:
                    broad_color_text = typedArray.getColor(attr, Color.parseColor("#88001B"));
                    break;
                case R.styleable.RadarView_circle_hold_color:
                    circle_hold_color = typedArray.getColor(attr, Color.parseColor("#008B8B"));
                    break;
                case R.styleable.RadarView_broad_color:
                    broad_color = typedArray.getColor(attr, Color.parseColor("#585858"));
                    break;
                case R.styleable.RadarView_interval_text_color:
                    interval_text_color = typedArray.getColor(attr, Color.parseColor("#2F4F4F"));
                    break;
            }
        }
        typedArray.recycle();
        initValue();
    }

    public void setDrawText(boolean drawText) {
        this.drawText = drawText;
    }

    public void setData(List<Float> listData) {
        this.listData.clear();
        this.listData.addAll(listData);
    }

    public void setMaxValue(float maxValue) {
        this.maxValue = maxValue;
    }

    public void setBroadColor(int broad_color) {
        this.broad_color = broad_color;
    }

    public void setBroadColorText(int broad_color_text) {
        this.broad_color_text = broad_color_text;
    }

    public void setMarkColor(int mark_color) {
        this.mark_color = mark_color;
    }

    public void setMarkBroadColor(int mark_broad_color) {
        this.mark_broad_color = mark_broad_color;
    }

    public void setCornerHoldColor(int corner_hold_color) {
        this.corner_hold_color = corner_hold_color;
    }

    public void setCircleHoldColor(int circle_hold_color) {
        this.circle_hold_color = circle_hold_color;
    }

    public void setmBroadStrokeWidth(float mBroadStrokeWidth) {
        this.mBroadStrokeWidth = mBroadStrokeWidth;
    }

    public void setmMarkBroadStrokeWidth(float mMarkBroadStrokeWidth) {
        this.mMarkBroadStrokeWidth = mMarkBroadStrokeWidth;
    }

    public void setCornerTextSize(int corner_textSize) {
        this.corner_textSize = convertDpToPixel(corner_textSize);
    }

    public void setCircleHoldTextSize(int circle_hold_textSize) {
        this.circle_hold_textSize = convertDpToPixel(circle_hold_textSize);
    }

    public void setmMarkEaseAlpha(int mMarkEaseAlpha) {
        this.mMarkEaseAlpha = mMarkEaseAlpha;
    }

    public void setmBroadAlpha(int mBroadAlpha) {
        this.mBroadAlpha = mBroadAlpha;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public void setOpenDuration(boolean open) {
        this.openDuration = open;
    }

    public void setIntervalTextColor(int interval_text_color) {
        this.interval_text_color = interval_text_color;
    }

    public void setIntervalTextSize(int mIntervalTextSize) {
        this.mIntervalTextSize = mIntervalTextSize;
    }

    public void setCornerName(List<String> cornerList) {
        if (this.cornerName.size() == 0) {
            this.cornerName.addAll(cornerList);
        }
    }

    public void initValue() {
        scroller = new Scroller(getContext());
        mDetector = new GestureDetector(getContext(), mGestureListener);
        corner_textSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 15, getResources().getDisplayMetrics());
        circle_hold_textSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        mIntervalTextSize = (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics());

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

        mCircleHoldPaint.setAntiAlias(true);
        mCircleHoldPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mCircleHoldPaint.setColor(circle_hold_color);

        mDrawTextPaint.setTextSize(corner_textSize);
        mDrawTextPaint.setColor(broad_color_text);
        mDrawTextPaint.setStyle(Paint.Style.STROKE);
        mDrawTextPaint.setTextAlign(Paint.Align.CENTER);
        mDrawTextPaint.setAntiAlias(true);

        mHoldTextPaint.setTextSize(circle_hold_textSize);
        mHoldTextPaint.setColor(corner_hold_color);
        mDrawTextPaint.setStyle(Paint.Style.STROKE);
        mDrawTextPaint.setAntiAlias(true);

        mIntervalTextPaint.setTextSize(mIntervalTextSize);
        mIntervalTextPaint.setStyle(Paint.Style.STROKE);
        mIntervalTextPaint.setColor(interval_text_color);
        mIntervalTextPaint.setAntiAlias(true);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthModel = MeasureSpec.getMode(widthMeasureSpec);
        int heightModel = MeasureSpec.getMode(heightMeasureSpec);
        int measureWidth = MeasureSpec.getSize(widthMeasureSpec);
        int measureHeight = MeasureSpec.getSize(heightMeasureSpec);
        int width;
        int height;
        if (widthModel == MeasureSpec.EXACTLY) {
            width = measureWidth;
        } else {
            width = getPaddingLeft() + getPaddingRight() + measureWidth;
        }
        if (heightModel == MeasureSpec.EXACTLY) {
            height = measureHeight;
        } else {
            height = (getPaddingTop() + getPaddingBottom() + measureHeight) / 2;
        }
        setMeasuredDimension(width, height);
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldw, int oldh) {
        super.onSizeChanged(width, height, oldw, oldh);
        radius = (float) Math.min(width, height) / 3;
        margin = radius / 4;
        mPerimeter = 2 * Math.PI * radius;
        float tempRedius = (float) 360 / listData.size();
        listAngle = new float[listData.size()];
        for (int i = 0; i < listData.size(); i++) {
            listAngle[i] = tempRedius * (i + 1);
        }
        if (this.openDuration) {
            loadStartAnimator();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (listAngle.length == 0) {
            return;
        }
        canvas.translate(getWidth() / 2, getHeight() / 2);
        canvas.rotate(180);
        canvas.save();
        //画雷达图各个边
        for (int i = 1; i <= marginNum; i++) {
            drawRadarBroad(canvas, margin * i);
        }
        //画雷达图各个边的连线
        drawPointLine(canvas, radius);
        //画雷达图的角的文字
        drawText(canvas, radius);
        //画出数值区域
        drawData(canvas, radius);
        //画出各个点
        circleHoldPaint(canvas, radius);
        //画各个区间数值提示
        drawDataEasePoint(canvas);
    }

    /**
     * 画各个区间数值提示
     * @param canvas
     * @param radius
     */
    public void drawDataEasePoint(Canvas canvas) {
        if (!openDataEasePoint && listAngle.length != 0) {
            return;
        }
        float marginData = maxValue / marginNum;
        for (int i = 1; i <= marginNum; i++) {
            float[] temp = getAngle(margin * i, listAngle[0]);
            canvas.save();
            canvas.translate(temp[0], temp[1]);
            canvas.rotate(-180);
            float data = marginData * i;
            Rect mCenterRect=new Rect();
            mIntervalTextPaint.getTextBounds(data+"",0,(data+"").length(),mCenterRect);
            canvas.drawText(data+"",0, temp[1]>0?mCenterRect.height():-mCenterRect.height(), mIntervalTextPaint);
            canvas.restore();
        }
    }

    /**
     * 画出雷达图的边
     * @param canvas
     * @param radius
     * @param angle
     */
    public void drawRadarBroad(Canvas canvas, float radius) {
        Path path = new Path();
        for (int i = 0; i < listAngle.length; i++) {
            float[] temp = getAngle(radius, listAngle[i]);
            if (i == 0) {
                path.moveTo(temp[0], temp[1]);
            } else {
                path.lineTo(temp[0], temp[1]);
            }
        }
        float[] lastPoint = getAngle(radius, listAngle[0]);
        path.lineTo(lastPoint[0], lastPoint[1]);
        mBroadPaint.setAlpha(mBroadAlpha);
        canvas.drawPath(path, mBroadPaint);
    }

    /**
     * 画出五条中心点对边
     * @param canvas
     * @param radius
     */
    public void drawPointLine(Canvas canvas, float radius) {
        for (int i = 0; i < listAngle.length; i++) {
            Path path = new Path();
            path.moveTo(0, 0);
            float[] temp = getAngle(radius, listAngle[i]);
            path.lineTo(temp[0], temp[1]);
            mBroadPaint.setAlpha(mBroadAlpha);
            canvas.drawPath(path, mBroadPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return true;
    }

    private GestureDetector.SimpleOnGestureListener mGestureListener = new GestureDetector.SimpleOnGestureListener() {
        @Override
        public boolean onDown(MotionEvent e) {
            if (!scroller.isFinished()) {
                scroller.forceFinished(true);
            }
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            calculationAngle(e1.getX(), e1.getY(), e2.getX(), e2.getY(), distanceX / 5, distanceY / 5);
            postInvalidate();
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX) > Math.abs(velocityY)) {
                mFlingPoint = e2.getX();
                scroller.fling((int) e2.getX(), 0, (int) (velocityX), 0,
                        (int) (-50 + e2.getX()),
                        (int) (50 + e2.getX()), 0, 0);
            } else if (Math.abs(velocityX) < Math.abs(velocityY)) {
                mFlingPoint = e2.getX();
                scroller.fling(0, (int) e2.getY(), 0, (int) (velocityY), 0, 0,
                        (int) -(50 + e2.getY()),
                        (int) (50 + e2.getY()));
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }
    };

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();
            double tempRadius = 0;
            int max = Math.max(Math.abs(x), Math.abs(y));
            double rotateDis = CIRCLE_ANGLE * (Math.abs(max - mFlingPoint) / mPerimeter);
            if (angleStatus == 0) {
                tempRadius = rotateDis;
            } else if (angleStatus == 1) {
                tempRadius = -rotateDis;
            }
            for (int i = 0; i < listAngle.length; i++) {
                listAngle[i] += (tempRadius);
            }
            postInvalidate();
        }
    }

    /**
     * 旋转计算角度
     * @param startX
     * @param startY
     * @param endX
     * @param endY
     * @param distanceX
     * @param distanceY
     */
    public void calculationAngle(float startX, float startY, float endX, float endY, float distanceX, float distanceY) {
        float tempRadius = 0;
        int action = detectDicr(startX, startY, endX, endY);
        if (action == 1 || action == 2) {//上下
            if (startX > (getWidth() / 2)) {
                tempRadius = distanceY; //右
            } else {
                tempRadius = -distanceY;//左
            }
        } else if (action == 3 || action == 4) {//左右
            if (startY > (getHeight() / 2)) {
                tempRadius = -distanceX;//下
            } else {
                tempRadius = distanceX;//上
            }
        }
        if (tempRadius > 0) {
            angleStatus = 0;
        } else {
            angleStatus = 1;
        }
        for (int i = 0; i < listAngle.length; i++) {
            listAngle[i] += (tempRadius);
        }
    }

    //通过手势来移动方块：1,2,3,4对应上下左右
    private int detectDicr(float start_x, float start_y, float end_x, float end_y) {
        boolean isLeftOrRight = Math.abs(start_x - end_x) > Math.abs(start_y - end_y) ? true : false;
        if (isLeftOrRight) {
            if (start_x - end_x > 0) {
                return 3;
            } else if (start_x - end_x < 0) {
                return 4;
            }
        } else {
            if (start_y - end_y > 0) {
                return 1;
            } else if (start_y - end_y < 0) {
                return 2;
            }
        }
        return 0;
    }

    /**
     * 画出数值区域
     * @param canvas
     * @param radius
     */
    public void drawData(Canvas canvas, float radius) {
        if (maxValue == 0) {
            maxValue = Collections.max(listData);
        }
        Path path = new Path();
        for (int i = 0; i < listAngle.length; i++) {
            float tempRadius = (listData.get(i) / maxValue) * radius;
            float[] tempAngle = getAngle(tempRadius, listAngle[i]);
            if (i == 0) {
                path.moveTo(tempAngle[0], tempAngle[1]);
            } else {
                path.lineTo(tempAngle[0], tempAngle[1]);
            }
        }
        path.close();
        canvas.drawPath(path, mMarkEasePaint);
        canvas.drawPath(path, mMarkPaint);
    }

    /**
     * 画出各个点
     * @param canvas
     * @param radius
     */
    public void circleHoldPaint(Canvas canvas, float radius) {
        if (maxValue == 0) {
            maxValue = Collections.max(listData);
        }
        for (int i = 0; i < listAngle.length; i++) {
            float tempRadius = (listData.get(i) / maxValue) * radius;
            float[] tempAngle = getAngle(tempRadius, listAngle[i]);
            if (drawText) {
                canvas.save();
                canvas.translate(tempAngle[0], tempAngle[1]);
                float textWidth = mHoldTextPaint.measureText(listData.get(i) + "");
                float baseLineY = Math.abs(mHoldTextPaint.ascent() + mHoldTextPaint.descent()) / 2;
                canvas.rotate(-180);
                if (((int) tempAngle[0]) == 0) {
                    canvas.drawText(listData.get(i) + "", -textWidth / 2, -baseLineY, mHoldTextPaint);
                } else {
                    canvas.drawText(listData.get(i) + "", tempAngle[0] > 0 ? -textWidth : 0,
                            tempAngle[1] > 0 ? -baseLineY : baseLineY * 2, mHoldTextPaint);
                }
                canvas.restore();
            }
            canvas.drawCircle(tempAngle[0], tempAngle[1], 5, mCircleHoldPaint);
        }
    }

    /**
     * 通过各个边得到各个点
     * @param radius
     * @param angle
     * @return
     */
    public float[] getAngle(float radius, float angle) {
        float[] param = new float[2];
        param[0] = (float) Math.sin(Math.toRadians(angle)) * radius;
        param[1] = (float) Math.cos(Math.toRadians(angle)) * radius;
        return param;
    }

    /**
     * 画出雷达图的各个角的提示
     * @param canvas
     * @param radius
     */
    public void drawText(Canvas canvas, float radius) {
        if (cornerName.size() == 0) {
            return;
        }
        for (int i = 0; i < listAngle.length; i++) {
            canvas.save();
            float[] temp = getAngle(radius, listAngle[i]);
            canvas.translate(temp[0], temp[1]);
            canvas.rotate(-180);
            Rect mCenterRect=new Rect();
            mIntervalTextPaint.getTextBounds(cornerName.get(i)+"",0,cornerName.get(i).length(),mCenterRect);
            if (-0.6<((int)temp[0])&&((int)temp[0])<=0.6) {
                canvas.drawText(cornerName.get(i),0,
                        temp[1]>0?-(mCenterRect.height()/2)
                        :mCenterRect.height()*2,mDrawTextPaint);
            } else {
                canvas.drawText(cornerName.get(i),
                        temp[0]>0?-(mCenterRect.width()):(mCenterRect.width()),
                        temp[1]>0?-(mCenterRect.height()/2):mCenterRect.height(),
                        mDrawTextPaint);
            }
            canvas.restore();
        }
    }

    /**
     * 数字转化为dp
     * @param value
     * @return
     */
    public int convertDpToPixel(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                value, getResources().getDisplayMetrics());
    }

    /**
     * 创建动画
     */
    public void loadStartAnimator() {
        ValueAnimator alphaAnimator = ValueAnimator.ofInt(0, 225);
        alphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mBroadAlpha = (int) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        final ValueAnimator radiusAnimator = ValueAnimator.ofFloat(0, radius);
        radiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                radius = (Float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        final ValueAnimator marginAnimator = ValueAnimator.ofFloat(0, margin);
        marginAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                margin = (Float) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        alphaAnimator.setDuration(duration);
        radiusAnimator.setDuration(duration);
        marginAnimator.setDuration(duration);
        alphaAnimator.start();
        radiusAnimator.start();
        marginAnimator.start();
    }

}
