package portablehealthtech.epilepsydetect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.github.mikephil.charting.utils.Utils;

public class ShowSeizure extends AppCompatActivity {

    private static int seizureId ;
    private static String patientName ;
    private String seizureString;
    private String seizureDuration;
    private String seizureDate;
    private String seizureState;
    //private List<Double> EEG = new ArrayList<>();
    private ArrayList<Entry> EEG = new ArrayList<Entry>();
    private double[] array1d;
    private double maxValue;
    private double minValue;
    private int lengthOfSeizure;
    private int fs = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seizure);

        seizureId = getIntent().getExtras().getInt("seizure_id");
        patientName = getIntent().getExtras().getString("PatientName");

        // LineChart is initialized from xml
        Utils.init(this);
        LineChart mLineChart = (LineChart) findViewById(R.id.chart);

        final DBHandler db = new DBHandler(this);
        Cursor cursor = db.getSeizure(seizureId);
        cursor.moveToFirst();
        seizureDate= cursor.getString(1); // Column 'time'
        seizureDuration = cursor.getString(2); // Column 'Duration'
        seizureString = cursor.getString(3); // Column 'data'
        seizureState = cursor.getString(4); // Column 'Status'
        seizureString = seizureString.replaceAll("\\[", "").replaceAll("\\]", "");
        cursor.close();

        TextView seizureID =(TextView)findViewById(R.id.seizureID);
        seizureID.setText(String.valueOf(seizureId));
        TextView seizureDateText =(TextView)findViewById(R.id.seizureDATE);
        seizureDateText.setText(String.valueOf(seizureDate));
        TextView seizureDUR =(TextView)findViewById(R.id.seizureDUR);
        seizureDUR.setText(String.valueOf(seizureDuration));
        TextView seizureSTATUS =(TextView)findViewById(R.id.seizureSTATUS);
        if (Integer.parseInt(seizureState)==1){
            seizureSTATUS.setText("Accepted");
        }else{
            seizureSTATUS.setText("Rejected");
        }


        String[] seizureStringSplitted = seizureString.split(",");
        lengthOfSeizure = seizureStringSplitted.length;

        for (int i= 0; i<lengthOfSeizure; i++) {
            double d = Double.parseDouble(seizureStringSplitted[i])/10;
            Entry eeg = new Entry((float)d,i);
            EEG.add(eeg);
        }

        LineDataSet setComp1 = new LineDataSet(EEG, "EEG");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(Color.BLACK);
        setComp1.setDrawCircles(false);

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i= 0; i<lengthOfSeizure; i++) {
            xVals.add(Double.toString(i/fs));
        }

        LineData data = new LineData(xVals, setComp1);
        mLineChart.setData(data);
        mLineChart.animateX(2500);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleXEnabled(true);
        mLineChart.setScaleYEnabled(true);
        mLineChart.setPinchZoom(true);
        mLineChart.setBackgroundColor(Color.WHITE);
        mLineChart.setVerticalScrollBarEnabled(true);
        mLineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        mLineChart.getAxisLeft().setGranularityEnabled(true);
        mLineChart.getAxisRight().setEnabled(false);

        XAxis xAxis = mLineChart.getXAxis();
        float start = (1000*2/fs);
        float end = (lengthOfSeizure-400)/fs;
        LimitLine lstart = new LimitLine(400, "Seizure start");
        LimitLine lend = new LimitLine((lengthOfSeizure-200), "Seizure end");
        lstart.setLineColor(Color.RED);
        lstart.setLineWidth(4f);
        lstart.setTextColor(Color.BLACK);
        lstart.setTextSize(12f);
        lend.setLineColor(Color.BLUE);
        lend.setLineWidth(4f);
        lend.setTextColor(Color.BLACK);
        lend.setTextSize(12f);
        lend.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        xAxis.addLimitLine(lstart);
        xAxis.addLimitLine(lend);

        mLineChart.invalidate(); // refresh


        ImageButton accept = (ImageButton)findViewById(R.id.acceptButton);

        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Seizure accepted", Toast.LENGTH_SHORT).show();
                if (Integer.parseInt(seizureState)==0){
                    db.acceptSeizure(seizureId);
                }
                if (seizureId - 1 == 0) { //if at end of list
                    Toast.makeText(getApplicationContext(), "No more seizures", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ShowSeizure.class);
                    intent.putExtra("seizure_id", seizureId);
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(getApplicationContext(), ShowSeizure.class);
                    intent.putExtra("seizure_id", seizureId - 1);
                    startActivity(intent);
                }
            }
        });

        ImageButton reject = (ImageButton)findViewById(R.id.rejectButton);

        reject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                db.rejectSeizure(seizureId);
                Toast.makeText(getApplicationContext(), "Seizure rejected", Toast.LENGTH_SHORT).show();
                if (seizureId-1==0){ //if at end of list
                    Toast.makeText(getApplicationContext(), "No more seizures", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), ShowSeizure.class);
                    intent.putExtra("seizure_id", seizureId);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(getApplicationContext(), ShowSeizure.class);
                    intent.putExtra("seizure_id", seizureId-1);
                    startActivity(intent);
                }
            }
        });

        ImageButton list = (ImageButton)findViewById(R.id.listButton);

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent getSeizureActivity = new Intent(getApplicationContext(), SeizureList.class);
                getSeizureActivity.putExtra("PatientName", patientName);
                startActivity(getSeizureActivity);
            }
        });

    }
}
