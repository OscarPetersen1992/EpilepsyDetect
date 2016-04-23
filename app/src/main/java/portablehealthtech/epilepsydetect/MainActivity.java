package portablehealthtech.epilepsydetect;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.content.Intent;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button loginButton;
    private EditText usernameEd, passwordEd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.button);
        usernameEd =(EditText)findViewById(R.id.username);
        passwordEd =(EditText)findViewById(R.id.password);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
/*                if(usernameEd.getText().toString().equals("Oscar") &&

                        passwordEd.getText().toString().equals("123")) {
                    Toast.makeText(getApplicationContext(), "Login successful",Toast.LENGTH_SHORT).show();
*/
                    Intent getOverviewActivity = new Intent(MainActivity.this,Overview.class);
                    getOverviewActivity.putExtra("PatientName",usernameEd.getText().toString());
                    startActivity(getOverviewActivity);
                }
/*                else{
                    Toast.makeText(getApplicationContext(), "Wrong username or password",Toast.LENGTH_SHORT).show();

                }

           }
*/        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
