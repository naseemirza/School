package com.lead.infosystems.schooldiary.Progress;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.lead.infosystems.schooldiary.R;

import java.util.ArrayList;
import java.util.List;

public class SinlGraph extends AppCompatActivity {

    List<String> s = new ArrayList<>();
    String sub_name;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sinl_graph);
        Intent intent = getIntent();
        sub_name = intent.getStringExtra("sub_name");
        LineChart chart=(LineChart)findViewById(R.id.chart);
        List<MarksData> data = new ArrayList<MarksData>();
        List<Entry> entries = new ArrayList<Entry>();
        data = Marks.items;
        if (!data.isEmpty()){
            for (int i = 0; i < data.size(); i++) {
                MarksData currentItem = data.get(i);
                    entries.add(new Entry(Float.parseFloat(currentItem.getPercentage()), i));
                    s.add(currentItem.getExam_name() + "");
            }
            LineDataSet dataset = new LineDataSet(entries, sub_name);
            dataset.disableDashedLine();
            dataset.setValueTextSize(12);
            dataset.setValueTextColor(Color.RED);
            dataset.setCircleSize(8);
            dataset.setCircleColorHole(Color.BLUE);
            dataset.getValueTypeface();
            dataset.setColor(Color.BLACK);
            dataset.setDrawFilled(true);
            dataset.setLineWidth(2f);
            dataset.setDrawCircleHole(true);
            dataset.setFillColor(Color.GREEN);
            dataset.setFillAlpha(70);
            LineData linedata = new LineData(s, dataset);
            chart.setData(linedata);
            chart.animateXY(00, 1500);
            chart.invalidate();

        }
    }
}
