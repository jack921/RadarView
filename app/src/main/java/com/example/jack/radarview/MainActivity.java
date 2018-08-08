package com.example.jack.radarview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//import rorbin.q.radarview.RadarData;
//import rorbin.q.radarview.RadarView;

public class MainActivity extends AppCompatActivity {

    RadarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setData();
        setData2();

        RadarView radarView=findViewById(R.id.radarView);
        List<String> listString=new ArrayList<>();
        listString.add("输出");
        listString.add("KDA");
        listString.add("发育");
        listString.add("团战");
        listString.add("生存");
        listString.add("测试");
        radarView.setCornerName(listString);

        List<Float> listData=new ArrayList<>();
        listData.add(1f);
        listData.add(4f);
        listData.add(6f);
        listData.add(8f);
        listData.add(5f);
        listData.add(3f);
        radarView.setData(listData);
        radarView.setMaxValue(10f);
        radarView.setDrawText(true);
        radarView.setOpenDuration(true);

    }

    public void setData() {
        mChart = findViewById(R.id.chart1);
        mChart.setBackgroundColor(Color.rgb(60, 65, 82));
        mChart.getDescription().setEnabled(false);
        mChart.setWebLineWidth(1f);
        mChart.setWebColor(Color.LTGRAY);
        mChart.setWebLineWidthInner(1f);
        mChart.setWebColorInner(Color.LTGRAY);
        mChart.setWebAlpha(100);
        ArrayList<RadarEntry> entries1 = new ArrayList<RadarEntry>();
        entries1.add(new RadarEntry(121));
        entries1.add(new RadarEntry(200));
        entries1.add(new RadarEntry(312));
        entries1.add(new RadarEntry(400));
        entries1.add(new RadarEntry(510));
        entries1.add(new RadarEntry(270));
        RadarDataSet set1 = new RadarDataSet(entries1, "Last Week");
        set1.setColor(Color.rgb(103, 110, 129));
        set1.setFillColor(Color.rgb(103, 110, 129));
        set1.setDrawFilled(true);
        set1.setFillAlpha(180);
        set1.setLineWidth(2f);
        set1.setDrawHighlightCircleEnabled(true);
        set1.setDrawHighlightIndicators(false);
        ArrayList<IRadarDataSet> sets = new ArrayList<IRadarDataSet>();
        sets.add(set1);
        RadarData data = new RadarData(sets);
        data.setValueTextSize(8f);
        data.setDrawValues(false);
        data.setValueTextColor(Color.WHITE);
        mChart.setData(data);
        mChart.invalidate();
    }

    public void setData2(){
        RadarView2 radarView1=findViewById(R.id.chart2);
        List<Float> values2 = new ArrayList<>();
        Collections.addAll(values2, 7f, 1f, 4f, 2f, 8f, 3f, 4f, 6f, 5f, 3f);
        rorbin.q.radarview.RadarData data2 = new rorbin.q.radarview.RadarData(values2);
        data2.setValueTextEnable(true);
        data2.setVauleTextColor(Color.WHITE);
        radarView1.addData(data2);

    }

}
