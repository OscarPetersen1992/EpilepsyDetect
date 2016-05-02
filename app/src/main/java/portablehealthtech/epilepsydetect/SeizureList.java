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
import android.widget.CursorAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class SeizureList extends AppCompatActivity {

    public final static String SEIZURE_ID = "seizure_id";
    private ListView listView;
    DBHandler db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seizure_list);

        db = new DBHandler(this);

        final Cursor cursor = db.getAllSeizures();

        String [] columns = new String[] {
             //   DBHandler.COLUMN_ID,
                DBHandler.COLUMN_DATE,
                DBHandler.COLUMN_DURATION
        };
        int [] widgets = new int[] {
             //   R.id.idShow,
                R.id.dateShow,
                R.id.durationShow
        };

        SimpleCursorAdapter cursorAdapter = new SimpleCursorAdapter(this, R.layout.seizure_info,
                cursor, columns, widgets, 2);
        listView = (ListView)findViewById(R.id.listSeizure);
        listView.setAdapter(cursorAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView listView, View view,
                                    int position, long id) {

                Cursor itemCursor = (Cursor) SeizureList.this.listView.getItemAtPosition(position);
                int personID = itemCursor.getInt(itemCursor.getColumnIndex(DBHandler.COLUMN_ID));
                Intent intent = new Intent(getApplicationContext(), ShowSeizure.class);
                intent.putExtra(SEIZURE_ID, personID);
                startActivity(intent);
            }
        });

    }

    public void updateClick(View view) {
        finish();
        startActivity(getIntent());
    }

}
