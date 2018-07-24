package com.example.jack.radarview;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RadarView radarView=findViewById(R.id.radarView);
        List<String> listString=new ArrayList<>();
        listString.add("输出");
        listString.add("KDA");
        listString.add("发育");
        listString.add("团战");
        listString.add("生存");
        radarView.setCornerName(listString);



    }
}
