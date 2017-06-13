package com.kirliavc.myapplication;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements OnChartValueSelectedListener,SensorEventListener {

    ScatterChart chart;
    private SensorManager mSensorManager;
    float value[];
    Timer timer;
    List<Entry> entries = new ArrayList<Entry>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        timer=new Timer(true);
        timer.schedule(task,1000,1000);
        value=new float[3];
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        chart = (ScatterChart) findViewById(R.id.chart);

        for(int i=0;i<2;i++)
            entries.add(new Entry(i,i));
        ScatterDataSet dataSet = new ScatterDataSet(entries, "Label");
        ScatterData scatterData = new ScatterData(dataSet);
        chart.getDescription().setEnabled(false);
        chart.setData(scatterData);
        chart.invalidate(); // refresh
        Intent intent = new Intent(MainActivity.this,Chart2Activity.class);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dynamical, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(MainActivity.this,Chart2Activity.class);
        startActivity(intent);

        return true;
    }
    public void addEntryToChart(float value1,float value2){
        entries.add(new Entry(value1, value2));
        Collections.sort(entries,new Comparator<Entry>() {
            @Override
            public int compare(Entry entry, Entry t1) {
                if(entry.getX()>t1.getX())
                    return 1;
                return -1;
            }
        });

        ScatterDataSet dataSet = new ScatterDataSet(entries, "Label");
        ScatterData scatterData = new ScatterData(dataSet);
        chart.getDescription().setEnabled(false);
        chart.setData(scatterData);
        chart.invalidate(); // refresh
    }
    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
    }
    @Override
    public void onNothingSelected() {

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.sensor.getType()== Sensor.TYPE_ACCELEROMETER){
            value[0]=sensorEvent.values[0];
            value[1]=sensorEvent.values[1];
            value[2]=sensorEvent.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }

    protected void onResume() {
        super.onResume();
        //注册加速度传感器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),//传感器TYPE类型
                SensorManager.SENSOR_DELAY_UI);//采集频率
        //注册重力传感器
        mSensorManager.registerListener(this,
                mSensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY),
                SensorManager.SENSOR_DELAY_FASTEST);
    }
    TimerTask task = new TimerTask() {
        public void run() {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Log.i(String.valueOf(value[0]),String.valueOf(value[1]));
                    addEntryToChart(Math.abs(value[0]*10),Math.abs(value[1]*10));
                }
            });

        }
    };
}
