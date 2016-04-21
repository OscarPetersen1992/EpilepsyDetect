package portablehealthtech.epilepsydetect;

import android.app.IntentService;
import android.content.Intent;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Date;
import java.text.DateFormat;
import java.math.*;
import libsvm.*;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class backgroundDetection extends IntentService {

    DBHandler db = new DBHandler(this,"Seizure List",null,1);

    private static double [][] allEEG;
    private static double [] logsum;
    private static double [] window;
    private static final int refractoryPeriod= 30;
    private static int numDetail = 4;
    private static svm_node[] featuresVec = new svm_node[numDetail];
    public static int[] predictedLabels;
    private static int firstTempPredictedLabels;
    private static int tempPredictedLabels;
    private static svm_model svmModel = new svm_model();


    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //Defining date format


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

        int numOfWindows = allEEG.length;
        predictedLabels = new int[numOfWindows];

        int index = 0;

        // Detect seizures

        while (index < numOfWindows) {

            window = allEEG[index];

            featuresVec = Features.wavelet_logsum(window);
            firstTempPredictedLabels = (int) svm.svm_predict(svmModel, featuresVec);

            if (firstTempPredictedLabels == 1) {
                index++;
                featuresVec = Features.wavelet_logsum(window);
                tempPredictedLabels = (int) svm.svm_predict(svmModel, featuresVec);

                if (tempPredictedLabels == 1) {
                    predictedLabels[index - 1] = firstTempPredictedLabels;
                    predictedLabels[index] = tempPredictedLabels;

                    while (index < numOfWindows && (firstTempPredictedLabels + tempPredictedLabels) >= 1) {
                        if ((firstTempPredictedLabels + tempPredictedLabels) == 1) {
                            index++;
                            featuresVec = Features.wavelet_logsum(window);
                            tempPredictedLabels = (int) svm.svm_predict(svmModel, featuresVec);

                            if (tempPredictedLabels == 1) {
                                predictedLabels[index - 1] = tempPredictedLabels;
                                predictedLabels[index] = tempPredictedLabels;
                            } else {
                                predictedLabels[index - 1] = tempPredictedLabels;
                                predictedLabels[index] = tempPredictedLabels;
                                index = index + refractoryPeriod;
                            }
                        } else {
                            index++;
                            featuresVec = Features.wavelet_logsum(window);
                            tempPredictedLabels = (int) svm.svm_predict(svmModel, featuresVec);
                            predictedLabels[index] = tempPredictedLabels;
                        }
                    }

                } else {
                    predictedLabels[index - 1] = tempPredictedLabels;
                    predictedLabels[index] = tempPredictedLabels;
                }

            } else {
                predictedLabels[index] = firstTempPredictedLabels;
            }

            index++;
        }



            // Preprocess

            // Store a seizure

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

            String[] splitted = line.split(",");

            for (int i= 0; i<splitted.length; i++){
                EEG.add(Double.parseDouble(splitted[i])); //get Double from Array (String)
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
