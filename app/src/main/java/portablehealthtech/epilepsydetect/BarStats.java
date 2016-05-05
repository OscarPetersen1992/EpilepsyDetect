package portablehealthtech.epilepsydetect;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;

public class BarStats extends AppCompatActivity {

    private String dateString;
    private List<BarEntry> HourCount = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<String>();
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //Defining date format
    private Date date;
    private Calendar cal = Calendar.getInstance();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_stats);

        // LineChart is initialized from xml
        Utils.init(this);
        BarChart mBarChart = (BarChart) findViewById(R.id.chartBar);


        dateString = getIntent().getExtras().getString("DateString");

        DBHandler db = new DBHandler(this);


        Cursor cursor = db.getStats(dateString);


        for (int i= 0; i<24; i++) {

            cursor.moveToFirst();

            int count = 0;

            while (cursor.moveToNext()) {

                try {
                    cal.setTime(dateFormat.parse(cursor.getString(0)));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (cal.get(Calendar.HOUR_OF_DAY) == i) {
                    count++;
                }
            }

            BarEntry hourCount = new BarEntry((float) count, i);
            HourCount.add(hourCount); //get Double from Array (String)
            xVals.add(String.format("%02d", i));
        }


        BarDataSet setCompHourCount = new BarDataSet(HourCount, "HourCount");
        setCompHourCount.setAxisDependency(YAxis.AxisDependency.LEFT);
        setCompHourCount.setColors(ColorTemplate.COLORFUL_COLORS);
        //setCompHourCount.setColor(R.color.colorPrimary);

        BarData data = new BarData(xVals, setCompHourCount);
        mBarChart.setData(data);
        mBarChart.animateX(1000);
        mBarChart.setDragEnabled(true);
        mBarChart.setScaleXEnabled(true);
        mBarChart.setPinchZoom(true);
        mBarChart.invalidate(); // refresh
        mBarChart.setDescription("Seizure Distribution for 24 hours");
    }

    public void ChangeDate(View view) {
        Intent ChangeDateActivity = new Intent(this,PickDay.class);
        startActivity(ChangeDateActivity);
    }

    public void backToMain(View view) {
        Intent BackToMainActivity = new Intent(this,Overview.class);
        startActivity(BackToMainActivity);
    }

}
