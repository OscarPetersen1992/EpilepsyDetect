package portablehealthtech.epilepsydetect;

import android.app.IntentService;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Date;
import java.text.DateFormat;
import java.math.*;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class backgroundDetection extends IntentService {

    DBHandler db = new DBHandler(this,"Seizure List",null,1);

    private double [][] allEEG;

    public backgroundDetection() {
        super("backgroundDetection");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    String path = "finaltestset_eeg.csv";
        try {
            allEEG = load_csv(path);
        } catch (IOException e) {
            e.printStackTrace();
        }

        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //Defining date format

        // Detect seizures
        for(int row = 0; row < allEEG.length; row++) {  // System.out.println(allEEG[0].length); number of columns




            // Preprocess

            // Feature extraction

            // Classification

            // Store a seizure

            if (row == 2) {

                String current_date = dateFormat.format(new Date());
                db.addSeizure(new Seizure(1234, current_date, 10.4));
                System.out.println(current_date);


            }


        }




    }

    public double[][] load_csv(String path) throws IOException{
        List<Double> EEG = new ArrayList<>();
        double[] array1d;
        double[][] array2d = new double[4447][512]; //[number of observations][number of samples]

        InputStreamReader is = new InputStreamReader(getAssets().open(path));

        //  FileReader testFileReader = new FileReader(path);
        BufferedReader bf = new BufferedReader(is);
        String line;
        int row = 0;
        while((line = bf.readLine())!=null){

            String[] splited = line.split(",");

            for (int i= 0; i<splited.length; i++){
                EEG.add(Double.parseDouble(splited[i])); //get Double from Array (String)
            }

            Double[] array1 = new Double[EEG.size()];
            EEG.toArray(array1); // fill the array

            array1d = ArrayUtils.toPrimitive(array1); //convert to double from Double

            for(int col = 0; col < array1d.length; col++) {
                array2d[row][col] =  array1d[col];
            }
            row++;
            EEG.clear();
        }

        return array2d;

    }



}
