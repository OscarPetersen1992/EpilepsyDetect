package portablehealthtech.epilepsydetect;

import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.GridLabelRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class ShowSeizure extends AppCompatActivity {


    private static int seizureId ;
    private String seizureString;
    List<Double> EEG = new ArrayList<>();
    double[] array1d;
    double maxValue;
    double minValue;
    int lengthOfSeizure;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seizure);

        seizureId = getIntent().getExtras().getInt("seizure_id");

        TextView headerText1 =(TextView)findViewById(R.id.headerText);
        headerText1.setText(String.valueOf(seizureId));
        // Load in EEG data


        DBHandler db = new DBHandler(this);

        Cursor cursor = db.getSeizure(seizureId);

        cursor.moveToFirst();

        seizureString = cursor.getString(3); // Column 'data'

        seizureString = seizureString.replaceAll("\\[", "").replaceAll("\\]", "");

        cursor.close();

        String[] seizureStringSplitted = seizureString.split(",");

        lengthOfSeizure = seizureStringSplitted.length;

        for (int i= 0; i<lengthOfSeizure; i++) {
            EEG.add(Double.parseDouble(seizureStringSplitted[i])); //get Double from Array (String)
        }

        Double[] array1 = new Double[lengthOfSeizure];
        EEG.toArray(array1); // fill the array

        array1d = ArrayUtils.toPrimitive(array1); //convert to double from Double

        // Convert to class DataPoints for GraphView
        DataPoint[] values = new DataPoint[lengthOfSeizure];

        for(int j=0; j<lengthOfSeizure; j++){
            DataPoint valueTemp = new DataPoint(j,array1d[j]);
            values[j]=valueTemp;
        }

        // Find maximum and minimum value for plot
        Arrays.sort(array1d);
        minValue = array1d[0];
        maxValue = array1d[lengthOfSeizure - 1];

        /*
        // Manual bounds of x
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(lengthOfSeizure / fs);

        // Manual bounds of y
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(minVal);
        graphView.getViewport().setMaxY(maxVal);

        // Axis labels
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Time (s)");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Amplitude (\u03BCV)");
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        */

        // Generate line plot
        GraphView graphView = (GraphView) findViewById(R.id.graph);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(values);
        graphView.addSeries(series);

    }

}
