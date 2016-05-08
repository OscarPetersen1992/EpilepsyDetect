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

public class StatisticsActivity extends AppCompatActivity {

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
        setContentView(R.layout.activity_statistics);

        // LineChart is initialized from xml
        Utils.init(this);
        PieChart mPieChart = (PieChart) findViewById(R.id.chartPie);

        patientName = getIntent().getExtras().getString("PatientName");

        DBHandler db = new DBHandler(this);

        TextView meanDuration = (TextView) findViewById(R.id.meanText);
        TextView totalSeizure = (TextView) findViewById(R.id.SeizureTotalText);
        TextView avgSeizPerDayText = (TextView) findViewById(R.id.AvgSeizPerDayText);

        Cursor seiz = db.getAllAcceptedSeizures();
        int morning = 0;
        int day = 0;
        int evening = 0;
        int night = 0;
        seiz.moveToLast();
        try {
            firstDate = dateFormat.parse(seiz.getString(1)); // first date of detected seizures
        } catch (ParseException e) {
            e.printStackTrace();
        }
        seiz.moveToFirst();
        try {
            LastDate = dateFormat.parse(seiz.getString(1)); //Last date of detected seizures
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
            if (h > 7 && h <= 12) {
                morning++;
            } else if (h > 12 && h <= 17) {
                day++;
            } else if (h > 17 && h <= 24) {
                evening++;
            } else if (h > 0 && h <= 7) {
                night++;
            }
        }
        final int sumSeiz = SeizureCount;

        double diff = LastDate.getTime() - firstDate.getTime();
        double diffDays = diff / (24 * 60 * 60 * 1000);
        double mean = getAverage();
        double avgSeizPerDay = round((SeizureCount / Math.ceil(diffDays)), 1);

        meanDuration.setText(Double.toString(round(mean, 1)));
        totalSeizure.setText(Integer.toString(SeizureCount));
        avgSeizPerDayText.setText(Double.toString(avgSeizPerDay));

        // CREATE PIE CHART
        ArrayList<Entry> yVals1 = new ArrayList<Entry>();
        float[] yPieData = new float[4];
        yPieData[0] = morning;
        yPieData[1] = day;
        yPieData[2] = evening;
        yPieData[3] = night;
        final String[] xPieData = {"Morning", "Day", "Evening", "Night"};

        for (int i = 0; i < yPieData.length; i++)
            if (yPieData[i] > 0) {
                yVals1.add(new Entry(yPieData[i], i));
            }

        ArrayList<String> xVals = new ArrayList<String>();

        for (int i = 0; i < 4; i++)
            if (yPieData[i] > 0) {
                xVals.add(xPieData[i]);
            }

        // create pie data set
        PieDataSet dataSet = new PieDataSet(yVals1, " ");

        dataSet.setColors(ColorTemplate.JOYFUL_COLORS);

        // instantiate pie data object now
        PieData dataPie = new PieData(xVals, dataSet);
        //dataPie.setValueFormatter(new PercentFormatter());
        dataPie.setValueTextSize(11f);
        dataPie.setValueTextColor(Color.WHITE);

        // configure pie chart
        mPieChart.setUsePercentValues(true);
        mPieChart.setBackgroundColor(Color.TRANSPARENT);

        // enable hole and configure
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setHoleColor(Color.TRANSPARENT);
        mPieChart.setHoleRadius(30);
        mPieChart.setTransparentCircleRadius(10);
        mPieChart.animateY(1000);
        mPieChart.setDescription("Seizure occurance");
        mPieChart.setDescriptionColor(Color.WHITE);
        mPieChart.setDragDecelerationEnabled(true);
        mPieChart.setCenterTextColor(Color.WHITE);

        // enable rotation of the chart by touch
        mPieChart.setRotationAngle(0);
        mPieChart.setRotationEnabled(true);

        // set a chart value selected listener
        mPieChart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {

            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                // display msg when value selected
                if (e == null)
                    return;

                Toast.makeText(getApplicationContext(),
                        xPieData[e.getXIndex()] + " = " + round(e.getVal() / sumSeiz * 100, 2) + "%", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected() {

            }
        });

        mPieChart.setData(dataPie);
        // update pie chart
        mPieChart.invalidate();
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
