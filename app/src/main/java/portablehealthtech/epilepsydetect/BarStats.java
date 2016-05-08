package portablehealthtech.epilepsydetect;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.Utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BarStats extends AppCompatActivity {

    private String dateString;
    private List<BarEntry> HourCount = new ArrayList<>();
    ArrayList<String> xVals = new ArrayList<String>();
    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //Defining date format
    private Date date;
    private Date firstDate;
    private Date LastDate;
    private Calendar cal = Calendar.getInstance();
    private static String patientName;
    public List<Integer> SeizureDUR = new ArrayList<Integer>();
    double meanSeizureDUR;


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bar_stats);

        // LineChart is initialized from xml
        Utils.init(this);
        BarChart mBarChart = (BarChart) findViewById(R.id.chartBar);

        dateString = getIntent().getExtras().getString("DateString");
        patientName = getIntent().getExtras().getString("PatientName");

        DBHandler db = new DBHandler(this);
        /*

        TextView meanDuration =(TextView)findViewById(R.id.meanText);
        TextView totalSeizure =(TextView)findViewById(R.id.SeizureTotalText);
        TextView avgSeizPerDayText =(TextView)findViewById(R.id.AvgSeizPerDayText);

        Cursor seiz = db.getAllAcceptedSeizures();
        int morning = 0;
        int day = 0;
        int evening =0;
        int night = 0;
        seiz.moveToLast();
        try {
             firstDate  = dateFormat.parse(seiz.getString(1)); // first date of detected seizures
        } catch (ParseException e) {
            e.printStackTrace();
        }
        seiz.moveToFirst();
        try {
             LastDate  = dateFormat.parse(seiz.getString(1)); //Last date of detected seizures
        } catch (ParseException e) {
            e.printStackTrace();
        }


        Calendar calendar = Calendar.getInstance();
        int SeizureCount = 0;


        while (seiz.moveToNext()) {
            try {
                date = dateFormat.parse(seiz.getString(1)); //date
                Integer DUR;
                DUR = seiz.getInt(2);
                SeizureDUR.add(DUR);
                SeizureCount++;
            } catch (ParseException e) {
                e.printStackTrace();
            }
            calendar.setTime(date);
            int h = calendar.get(Calendar.HOUR_OF_DAY);
            if (h>7 && h<=12) {
                morning++;
            } else if (h>12 && h<=17){
                day++;
            } else if (h>17 && h<=24) {
                evening++;
            } else if (h>0 && h <=7) {
                night++;
            }
        }
        final int sumSeiz = SeizureCount;

        double diff = LastDate.getTime() - firstDate.getTime();
        double diffDays = diff / (24 * 60 * 60 * 1000);
        double mean = getAverage();
        double avgSeizPerDay = round((SeizureCount / Math.ceil(diffDays)), 1);

        meanDuration.setText(Double.toString(round(mean,1)));
        totalSeizure.setText(Integer.toString(SeizureCount));
        avgSeizPerDayText.setText(Double.toString(avgSeizPerDay));
        */

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
        mBarChart.animateY(1000);
        mBarChart.setDragEnabled(true);
        mBarChart.setScaleXEnabled(true);
        mBarChart.setPinchZoom(true);
        mBarChart.invalidate(); // refresh
        mBarChart.setDescription("Seizure Distribution for 24 hours");
    }

    public void ChangeDate(View view) {
        Intent ChangeDateActivity = new Intent(this,PickDay.class);
        ChangeDateActivity.putExtra("PatientName", patientName);

        startActivity(ChangeDateActivity);
    }

    public void backToMain(View view) {
        Intent BackToMainActivity = new Intent(this,Overview.class);
        BackToMainActivity.putExtra("PatientName", patientName);
        startActivity(BackToMainActivity);
    }

    public double getAverage(){
        long sum = 0;
        int n = SeizureDUR.size();
        for (int i = 0; i < n; i++)
            sum += SeizureDUR.get(i);
        meanSeizureDUR =((double) sum) / n;
        return meanSeizureDUR;
    }

    private static double round (double value, int precision) {
        int scale = (int) Math.pow(10, precision);
        return (double) Math.round(value * scale) / scale;
    }

}
