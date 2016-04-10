package portablehealthtech.epilepsydetect;

import java.util.Date;
import android.app.Activity;
import javax.xml.datatype.Duration;

public class Seizure extends Activity {

    private int seizure_id;
    private String seizure_date;
    private double seizure_duration;

    // Empty constructor
    public Seizure(){}

    // constructor
    public Seizure(String seizuredate, double seizureduration){
        this.seizure_date = seizuredate;
        this.seizure_duration = seizureduration;
    }

    // constructor
    public Seizure(int seizureid, String seizuredate, double seizureduration){
        this.seizure_id = seizureid;
        this.seizure_date = seizuredate;
        this.seizure_duration = seizureduration;
    }

    public int getSeizure_id() {
        return seizure_id;
    }

    public void setSeizure_id(int seizure_id) {
        this.seizure_id = seizure_id;
    }

    public String getSeizure_date() {
        return seizure_date;
    }

    public void setSeizure_date(String seizure_date) {
        this.seizure_date = seizure_date;
    }

    public double getSeizure_duration() {
        return seizure_duration;
    }

    public void setSeizure_duration(double seizure_duration) {
        this.seizure_duration = seizure_duration;
    }
}
