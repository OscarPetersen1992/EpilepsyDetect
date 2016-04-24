package portablehealthtech.epilepsydetect;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;


import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

public class StatisticsActivity extends AppCompatActivity {

    private static int numberOfSeizures = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        DBHandler db = new DBHandler(this);


        GraphView graphView = (GraphView) findViewById(R.id.graph);
        graphView.getViewport().setScrollable(true);
        graphView.getViewport().setScalable(true);

        /*
        // Manual bounds of x
        graphView.getViewport().setXAxisBoundsManual(true);
        graphView.getViewport().setMinX(0);
        graphView.getViewport().setMaxX(data.length / fs);

        // Manual bounds of y
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setMinY(1.1 * minVal);
        graphView.getViewport().setMaxY(1.1 * maxVal);

        // Axis labels
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Time (s)");
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Amplitude (\u03BCV)");
        graphView.getGridLabelRenderer().setGridStyle(GridLabelRenderer.GridStyle.NONE);
        */

        LineGraphSeries<DataPoint> series = new LineGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 1),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });

        graphView.addSeries(series);

        TextView numbSeizures =(TextView)findViewById(R.id.numSeizuresText);
        numbSeizures.setText(String.valueOf(numberOfSeizures));


    }



}
