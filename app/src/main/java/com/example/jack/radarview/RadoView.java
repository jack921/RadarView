package com.example.jack.radarview;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by Administrator on 2016/2/26.
 * 自定义雷达图控件
 */
public class RadoView extends View {

    private int count = 6;

    private int[] radius = new int[]{100, 200, 300, 400, 500};

    private int maxRadius = radius[radius.length - 1];

    private int[] marks = new int[count];
    private String[] keys = new String[count];

    LinkedHashMap<String, Integer> map = new LinkedHashMap<>();

    private Paint paintLine;

    private Paint paintMarkLine;
    private Paint paintMarkPoint;

    private Paint paintText;

    private double x;
    private double y;
    private double lastX;
    private double lastY;

    public RadoView(Context context) {
        super(context);
    }

    public RadoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RadoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        double littleAngle = 360 / count;

        map.put("语文", 200);
        map.put("数学", 300);
        map.put("外语", 320);
        map.put("文科", 450);
        map.put("理科", 270);
        map.put("其他", 1020);

        Iterator iterator = map.entrySet().iterator();
        int j = 0;
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            String key = (String) entry.getKey();
            Integer value = (Integer) entry.getValue();
            marks[j] = value;
            keys[j] = key;
            j++;
        }

        paintLine = new Paint();
        paintLine.setColor(Color.BLACK);
        paintLine.setStyle(Paint.Style.STROKE);
        paintLine.setStrokeWidth(3);

        paintText = new Paint();
        paintText.setColor(Color.BLACK);
        paintText.setTextSize(25);

        for (int i = 0; i < radius.length; i++) {
            drawStroke(canvas, littleAngle, radius[i]);
        }

        //分数点
        paintMarkPoint = new Paint();
        paintMarkPoint.setColor(Color.parseColor("#3F51B5"));
        paintMarkPoint.setStyle(Paint.Style.FILL);

        //评分点的连线
        paintMarkLine = new Paint();
        paintMarkLine.setAntiAlias(true);
        paintMarkLine.setColor(Color.parseColor("#3F51B5"));
        paintMarkLine.setStyle(Paint.Style.FILL_AND_STROKE);
        paintMarkLine.setAlpha(80);

        Path path = new Path();
        path.reset();
        for (int i = 0; i < marks.length; i++) {
            x = getPointX(littleAngle * i, marks[i]);
            y = getPointY(littleAngle * i, marks[i]);
            canvas.drawCircle((float) x, (float) y, 10, paintMarkPoint);
            if (i == 0) {
                path.moveTo((float) x, (float) y);
            } else {
                path.lineTo((float) x, (float) y);
            }
            lastX = x;
            lastY = y;
        }
        canvas.drawPath(path, paintMarkLine);
    }

    /**
     * 绘制多边形，radius为其外接圆半径
     *
     * @param canvas
     * @param littleAngle
     * @param radius
     */
    private void drawStroke(Canvas canvas, double littleAngle, double radius) {
        for (int i = 0; i < count; i++) {
            Paint paint = new Paint();
            paint.setColor(Color.BLACK);
            paint.setStyle(Paint.Style.FILL);
            x = getPointX(littleAngle * i, radius);
            y = getPointY(littleAngle * i, radius);
            canvas.drawPoint((float) x, (float) y, paint);
            canvas.drawLine((float) maxRadius, (float) maxRadius, (float) x, (float) y, paintLine);
            if (i > 0) {
                canvas.drawLine((float) lastX, (float) lastY, (float) x, (float) y, paintLine);
            }
            if (i == (count - 1)) {
                canvas.drawLine((float) x, (float) y, (float) getPointX(0, radius), (float) getPointY(0, radius), paintLine);
            }
            lastX = x;
            lastY = y;
            if (radius == maxRadius) {
                //如果是最外层的园，则加上文字
                canvas.drawText(keys[i], (float) (lastX - 20), (float) (lastY + 20), paintText);
            }
        }
    }


    /**
     * 得到需要计算的角度
     *
     * @param angle 角度，例：30.60.90
     * @return res
     */
    private double getNewAngle(double angle) {
        double res = angle;
        if (angle >= 0 && angle <= 90) {
            res = 90 - angle;
        } else if (angle > 90 && angle <= 180) {
            res = angle - 90;
        } else if (angle > 180 && angle <= 270) {
            res = 270 - angle;
        } else if (angle > 270 && angle <= 360) {
            res = angle - 270;
        }
        return res;
    }


    /**
     * 若以圆心为原点，返回该角度顶点的所在象限
     *
     * @param angle
     * @return
     */
    private int getQr(double angle) {
        int res = 0;
        if (angle >= 0 && angle <= 90) {
            res = 1;
        } else if (angle > 90 && angle <= 180) {
            res = 2;
        } else if (angle > 180 && angle <= 270) {
            res = 3;
        } else if (angle > 270 && angle <= 360) {
            res = 4;
        }
        return res;
    }

    /**
     * 返回多边形顶点X坐标
     *
     * @param angle
     * @return
     */
    private double getPointX(double angle, double radius) {
        double newAngle = getNewAngle(angle);
        double res = 0;
        double width = radius * Math.cos(newAngle / 180 * Math.PI);
        int qr = getQr(angle);
        switch (qr) {
            case 1:
            case 2:
                res = maxRadius + width;
                break;
            case 3:
            case 4:
                res = maxRadius - width;
                break;
            default:
                break;
        }
        return res;
    }


    /**
     * 返回多边形顶点Y坐标
     */
    private double getPointY(double angle, double radius) {
        double newAngle = getNewAngle(angle);
        double height = radius * Math.sin(newAngle / 180 * Math.PI);
        double res = 0;
        int qr = getQr(angle);
        switch (qr) {
            case 1:
            case 4:
                res = maxRadius - height;
                break;
            case 2:
            case 3:
                res = maxRadius + height;
                break;
            default:
                break;
        }
        return res;
    }

    public void setMap(LinkedHashMap<String, Integer> map) {
        this.map = map;
    }

    public LinkedHashMap<String, Integer> getMap() {
        return map;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}
