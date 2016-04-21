package portablehealthtech.epilepsydetect;

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


public class ShowSeizure extends AppCompatActivity {


    private static int seizure_id ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_seizure);

        seizure_id = getIntent().getExtras().getInt("seizure_id");

        TextView headerText1 =(TextView)findViewById(R.id.headerText);
        headerText1.setText(String.valueOf(seizure_id));
        // Load in EEG data


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

    }

}
