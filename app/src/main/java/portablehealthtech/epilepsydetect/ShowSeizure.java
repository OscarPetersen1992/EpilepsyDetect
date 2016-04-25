package portablehealthtech.epilepsydetect;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
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




public class ShowSeizure extends Activity {


    private static int seizureId ;
    private String seizureString;
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

        TextView headerText1 =(TextView)findViewById(R.id.headerText);
        headerText1.setText(String.valueOf(seizureId));

        // LineChart is initialized from xml
        Utils.init(this);
        LineChart mLineChart = (LineChart) findViewById(R.id.chart);

        DBHandler db = new DBHandler(this);

        Cursor cursor = db.getSeizure(seizureId);

        cursor.moveToFirst();

        seizureString = cursor.getString(3); // Column 'data'
        seizureString = seizureString.replaceAll("\\[", "").replaceAll("\\]", "");
        cursor.close();
        String[] seizureStringSplitted = seizureString.split(",");
        lengthOfSeizure = seizureStringSplitted.length;

        for (int i= 0; i<lengthOfSeizure; i++) {
            double d = Double.parseDouble(seizureStringSplitted[i]);
            Entry eeg = new Entry((float)d,i);
            EEG.add(eeg); //get Double from Array (String)
        }

        LineDataSet setComp1 = new LineDataSet(EEG, "EEG");
        setComp1.setAxisDependency(YAxis.AxisDependency.LEFT);
        setComp1.setColor(R.color.colorPrimary);
        setComp1.setDrawCircles(false);

        ArrayList<String> xVals = new ArrayList<String>();
        for (int i= 0; i<lengthOfSeizure; i++) {
            xVals.add(Integer.toString(i/fs));
        }

        LineData data = new LineData(xVals, setComp1);
        mLineChart.setData(data);
        mLineChart.animateX(2500);
        mLineChart.setDragEnabled(true);
        mLineChart.setScaleXEnabled(true);
        mLineChart.setPinchZoom(true);
        mLineChart.invalidate(); // refresh


/*
        Double[] array1 = new Double[lengthOfSeizure];
        EEG.toArray(array1); // fill    the array

        array1d = ArrayUtils.toPrimitive(array1); //convert to double from Double

        // Convert to class DataPoints for GraphView
        DataPoint[] values = new DataPoint[lengthOfSeizure];

        for(int j=0; j<lengthOfSeizure; j++){
            DataPoint valueTemp = new DataPoint((double)j/fs,array1d[j]);
            values[j]=valueTemp;
        }

        // Find maximum and minimum value for plot
        Arrays.sort(array1d);
        minValue = array1d[0];
        maxValue = array1d[lengthOfSeizure - 1];

        // Generate line plot
        GraphView graphView = (GraphView) findViewById(R.id.graph);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        // Manual bounds of x
        //graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(lengthOfSeizure / fs);

        // Manual bounds of y
        //graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(minValue);
        graphView.getViewport().setMaxY(maxValue);

        // Axis labels
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Time (s)");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Amplitude (\u03BCV)");
        //graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(values);
        graphView.addSeries(series);*/

    }


}
