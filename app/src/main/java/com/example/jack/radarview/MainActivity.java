package com.example.jack.radarview;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.github.mikephil.charting.charts.RadarChart;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.RadarData;
import com.github.mikephil.charting.data.RadarDataSet;
import com.github.mikephil.charting.data.RadarEntry;
import com.github.mikephil.charting.interfaces.datasets.IRadarDataSet;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    RadarChart mChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        setData();

        RadarView radarView=findViewById(R.id.radarView);
        List<String> listString=new ArrayList<>();
        listString.add("输出");
        listString.add("KDA");
        listString.add("发育");
        listString.add("团战");
        listString.add("生存");
        radarView.setCornerName(listString);

        List<Float> listData=new ArrayList<>();
        listData.add(2f);
        listData.add(4f);
        listData.add(6f);
        listData.add(8f);
        listData.add(5f);
        radarView.setData(listData);

        radarView.setMaxValue(10f);

    }

    public void setData() {
//        mChart = findViewById(R.id.chart1);
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

}
