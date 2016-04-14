package portablehealthtech.epilepsydetect;

import android.content.ContentValues;
import android.content.Intent;
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
import android.widget.TextView;
import android.widget.Toast;

public class SeizureList extends AppCompatActivity {

    public static ArrayList<String> ArrayofName = new ArrayList<>();
    DBHandler db = new DBHandler(this,"Seizure List",null,1);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seizure_list);


        DBHandler db = new DBHandler(this,"Seizure List",null,1);
        /*
        // Inserting Seizures

        db.addSeizure(new Seizure(1234, "04. april 2016", 10.4));
        db.addSeizure(new Seizure(4321, "06. april 2016", 20.3));
        db.addSeizure(new Seizure(2222, "08. april 2016", 17.4));


        // db.removeAll();
        */
        db.getAllSeizures();


        ListView listView = (ListView) findViewById(R.id.listSeizure);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ArrayofName);

        listView.setAdapter(adapter);
        listView.setTextFilterEnabled(true);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void backNow(View view) {

        super.onBackPressed();
    }

}
