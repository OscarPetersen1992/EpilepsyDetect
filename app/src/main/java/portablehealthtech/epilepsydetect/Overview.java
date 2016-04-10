package portablehealthtech.epilepsydetect;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Overview extends Activity {

    private static String patientName ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        patientName = getIntent().getExtras().getString("PatientName");

        TextView userText =(TextView)findViewById(R.id.usernameText);
        userText.setText(patientName);

    }

    public void seizureList(View view) {
        Intent getPatientActivity = new Intent(Overview.this,SeizureList.class);
        startActivity(getPatientActivity);
    }

}
