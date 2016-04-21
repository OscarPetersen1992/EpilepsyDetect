package portablehealthtech.epilepsydetect;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;


import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SeizureList extends AppCompatActivity {

    private ListView listView;
    DBHandler db;
    public static ArrayList<String> ArrayofName = new ArrayList<>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seizure_list);

        db = new DBHandler(this,"Seizure List",null,1);



        final Cursor cursor = db.getAllSeizures1();

        String [] columns = new String[] {
                DBHandler.COLUMN_ID,
                DBHandler.COLUMN_DATE,
                DBHandler.COLUMN_DURATION
        };
        int [] widgets = new int[] {
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.activity_seizure_list,
                cursor, columns, widgets, 0);
        listView = (ListView)findViewById(R.id.listSeizure);
        listView.setAdapter(cursorAdapter);
       // listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {



        /*

        // Inserting Seizures

        db.addSeizure(new Seizure(1234, "04. april 2016", 10.4));
        db.addSeizure(new Seizure(4321, "06. april 2016", 20.3));
        db.addSeizure(new Seizure(2222, "08. april 2016", 17.4));



        // db.removeAll();



        db.getAllSeizures();

        listView = (ListView) findViewById(R.id.listSeizure);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, ArrayofName);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });

        */



    }

    public void backNow(View view) {

        super.onBackPressed();
    }

}
