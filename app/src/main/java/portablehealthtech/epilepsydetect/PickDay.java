package portablehealthtech.epilepsydetect;

import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Toast;
import java.lang.reflect.Field;

import java.util.Calendar;

public class PickDay extends AppCompatActivity {

    private String dateString;
    private static String patientName ;

    DBHandler db = new DBHandler(this);
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_day);
        patientName = getIntent().getExtras().getString("PatientName");


        Calendar calendar = Calendar.getInstance();
        DatePicker start = (DatePicker) findViewById(R.id.datePicker);
        //setDatePickerTextColor(start,Color.WHITE);
        MyOnDateChangeListener onDateChangeListener = new MyOnDateChangeListener();
        dateString = Integer.toString(calendar.get(Calendar.YEAR)) + "/" + String.format("%02d", calendar.get(Calendar.MONTH)+1) + "/" + String.format("%02d", calendar.get(Calendar.DAY_OF_MONTH));
        start.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), onDateChangeListener);
    }

    public class MyOnDateChangeListener implements DatePicker.OnDateChangedListener {
        @Override
        public void onDateChanged(DatePicker view, int year, int mon, int day) {
            int month =mon+1; // the month numbers are 0-based

            dateString = Integer.toString(year) + "/" + String.format("%02d", month) + "/" + String.format("%02d", day);

        }
    }


    public void ChooseDate(View view) {

        System.out.println(dateString);

        cursor = db.getStats(dateString);

        cursor.moveToFirst();

        if (cursor.getCount() != 0) {
                Intent barActivity = new Intent(this, BarStats.class);
                barActivity.putExtra("DateString", dateString);
                barActivity.putExtra("PatientName", patientName);
                startActivity(barActivity);

        }else {

            Toast.makeText(getApplicationContext(), "No seizures detected this day", Toast.LENGTH_SHORT).show();
        }
    }

    public void SumStat(View view) {

        Intent statsActivity = new Intent(this, StatisticsActivity.class);
        statsActivity.putExtra("PatientName", patientName);
        startActivity(statsActivity);

    }

    /*
    public static boolean setDatePickerTextColor(DatePicker datePicker, int color)
    {
        final int count = datePicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = datePicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = datePicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(datePicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    datePicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalAccessException e){
                    Log.w("setNumberPickerTextColor", e);
                }
                catch(IllegalArgumentException e){
                    Log.w("setNumberPickerTextColor", e);
                }
            }
        }
        return false;
    } */

}
