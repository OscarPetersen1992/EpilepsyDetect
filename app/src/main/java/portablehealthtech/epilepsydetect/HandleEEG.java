package portablehealthtech.epilepsydetect;

import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.os.Environment;
import java.io.File;

public class HandleEEG extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_handle_eeg);

        File folder = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/CAEdetector/");
    }

}
