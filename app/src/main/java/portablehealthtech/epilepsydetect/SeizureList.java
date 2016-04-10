package portablehealthtech.epilepsydetect;

import android.content.ContentValues;
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

    private ListView listView;
    public static ArrayList<String> ArrayofName = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seizure_list);


        DBHandler db = new DBHandler(this,"Seizure List",null,1);

        // Inserting Seizures
        Log.d("Insert: ", "Inserting ..");
        db.addSeizure(new Seizure(1234, "4. april 2016", 10.4));
        db.addSeizure(new Seizure(4321, "6. april 2016", 20.3));
        db.addSeizure(new Seizure(2222, "8. april 2016", 17.4));

        // Reading all contacts
        Log.d("Reading: ", "Reading all contacts..");
        List<Seizure> seizures = db.getAllSeizures();

        for (Seizure sz : seizures) {
            String log = "Id: "+sz.getSeizure_id()+" ,Date: " + sz.getSeizure_date() + " ,Duration: "
                    + sz.getSeizure_duration();
            // Writing Contacts to log
            Log.d("Name: ", log);

        }

        db.getAllSeizures();

        listView = (ListView) findViewById(R.id.listSeizure);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, ArrayofName);

        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),
                        ((TextView) v).getText(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
