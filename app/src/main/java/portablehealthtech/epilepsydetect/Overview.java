package portablehealthtech.epilepsydetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.graphics.Color;
import android.widget.ImageView;

public class Overview extends AppCompatActivity {

    private static String patientName ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        TextView userText =(TextView)findViewById(R.id.usernameText);

        if (userText.getText().toString().isEmpty()) {
            patientName = getIntent().getExtras().getString("PatientName");
            userText.setText(patientName);
        }

    }

    public void seizureList(View view) {
        Intent getSeizureActivity = new Intent(this,SeizureList.class);
        startActivity(getSeizureActivity);
    }

    public void aboutClick(View view) {
        Intent getAboutActivity = new Intent(this,About.class);
        startActivity(getAboutActivity);
    }

    public void startStopRecord(View view) {

        Button startStopRecordButton = (Button)findViewById(R.id.startStopRecordButton);

        if (startStopRecordButton.getText().toString().equalsIgnoreCase("Start recording")) {
            startStopRecordButton.setText("Stop recording");
            startStopRecordButton.setTextColor(Color.RED);

        Intent backgroundDetectionActivity = new Intent(this,backgroundDetection.class);
        this.startService(backgroundDetectionActivity);


        }else {
            startStopRecordButton.setText("Start recording");
            startStopRecordButton.setTextColor(Color.parseColor("#ffffff"));

            stopService(new Intent(getApplicationContext(), backgroundDetection.class));
        }

    }

}
