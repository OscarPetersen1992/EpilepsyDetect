package portablehealthtech.epilepsydetect;

import android.app.IntentService;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.apache.commons.lang3.ArrayUtils;
import java.util.Date;
import java.text.DateFormat;
import java.math.*;
import java.util.logging.FileHandler;

import libsvm.*;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class backgroundDetection extends IntentService {

    DBHandler db = new DBHandler(this);// ,"Seizure List",null,1);

    private static double [][] allEEG;
    private static double [] logsum;
    private static double [] window;
    private static final int refractoryPeriod= 30;
    public static int[] predictedLabels;
    private static int firstTempPredictedLabels;
    private static int tempPredictedLabels;
    private static svm_model svmModel = new svm_model();
    private static int numFeat = 5;
    private static svm_node[] featuresVec = new svm_node[numFeat];
    private static double[] wavelets = new double[4];
    private static double shannon;

    DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss"); //Defining date format


    public backgroundDetection() {
        super("backgroundDetection");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
    String path = "test1.csv";
        try {
            // Load data signal
            allEEG = load_csv(path,1792,400);
            // Load SVM model
            InputStreamReader svm_file = new InputStreamReader(getAssets().open("svmAndroid.model"));
            BufferedReader bufferedReader = new BufferedReader(svm_file);
            svmModel = svm.svm_load_model(bufferedReader); // Load from Assets
            //svmModel = svm.svm_load_model(new BufferedReader(new FileReader(getFilesDir()+"/svmAndroid.model"))); // Load from internal memory
            int k = svmModel.l;
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Initialize number of windows
        int numOfWindows = allEEG.length;
        predictedLabels = new int[numOfWindows];
        int index = 0;

        // Detect seizures
        while (index < numOfWindows) {
            int[] buffer = new int[2];
            int seizure_count = 0;
            // Get window
            window = allEEG[index];
            // Calculate features
            wavelets = Features.wavelet_logsum(window);
            shannon = Features.shannon(window);
            // Create svm node
            featuresVec = get_svm_node(shannon,wavelets);
            // Classify window
            firstTempPredictedLabels = (int) svm.svm_predict(svmModel, featuresVec);
            predictedLabels[index] = firstTempPredictedLabels;

            if (firstTempPredictedLabels == 1) {
                index++;
                // Get window
                window = allEEG[index];
                // Calculate features
                wavelets = Features.wavelet_logsum(window);
                shannon = Features.shannon(window);
                // Create svm node
                featuresVec = get_svm_node(shannon,wavelets);
                // Classify window
                tempPredictedLabels = (int) svm.svm_predict(svmModel, featuresVec);

                if (tempPredictedLabels == 1) {
                    predictedLabels[index - 1] = firstTempPredictedLabels;
                    predictedLabels[index] = tempPredictedLabels;

                    buffer[0]=index-1;

                    while (index < numOfWindows && (predictedLabels[index - 1] + predictedLabels[index]) >= 1) {
                        if ((predictedLabels[index - 1] + predictedLabels[index]) == 1) {
                            index++;
                            // Get window
                            window = allEEG[index];
                            // Calculate features
                            wavelets = Features.wavelet_logsum(window);
                            shannon = Features.shannon(window);
                            // Create svm node
                            featuresVec = get_svm_node(shannon,wavelets);
                            // Classify window
                            tempPredictedLabels = (int) svm.svm_predict(svmModel, featuresVec);

                            if (tempPredictedLabels == 1) {
                                predictedLabels[index - 1] = tempPredictedLabels;
                                predictedLabels[index] = tempPredictedLabels;
                            } else {
                                predictedLabels[index - 1] = tempPredictedLabels;
                                predictedLabels[index] = tempPredictedLabels;
                                buffer[1]=index-2;
                                index = index + refractoryPeriod;
                            }
                        } else {
                            index++;
                            // Get window
                            window = allEEG[index];
                            // Calculate features
                            wavelets = Features.wavelet_logsum(window);
                            shannon = Features.shannon(window);
                            // Create svm node
                            featuresVec = get_svm_node(shannon,wavelets);
                            // Classify window
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

            if(buffer[0]>0) {
                int seizurelength = 2;//buffer[1] - buffer[0];
                double[] seizureEEG = new double[seizurelength];
                int count = 0;
                for(int j = buffer[0]; j < seizurelength; j++) {
                    for (int k = 0; k < allEEG[j].length; k++) {
                          seizureEEG[k+j*allEEG[j].length] = allEEG[j][k];
                    }
                    count++;
                }
                //for (int j = buffer[0]; j < seizurelength; j++) {
                //    seizureEEG = ArrayUtils.addAll(seizureEEG, allEEG[j]);
                //}

                //System.out.println(Arrays.toString(seizureEEG));
                String current_date = dateFormat.format(new Date());
                db.addSeizure(new Seizure(current_date, seizurelength, Arrays.toString(allEEG[buffer[0]])));
            }
            index++;
        }

        //System.out.println(Arrays.toString(predictedLabels));
        //for(int j=0; j<predictedLabels.length; j++) {
        //    System.out.println(predictedLabels[j]);
        //}
        // Preprocess

        // Store a seizure
        /*
        if (index == 2) {

            String current_date = dateFormat.format(new Date());
            db.addSeizure(new Seizure(1234, current_date, 10.4));


        }
        */

    }

    public svm_node[] get_svm_node(double shannon, double[] wavelets) {
        for(int j=0; j<numFeat; j++) {
            svm_node node = new svm_node();
            if(j==0){
                node.index = j+1;
                node.value = shannon;
            }else{
                node.index = j+1;
                node.value = wavelets[j-1];
            }
            featuresVec[j] = node;
        }
        return featuresVec;
    }

    public double[][] load_csv(String path, int n, int v) throws IOException{
        List<Double> EEG = new ArrayList<>();
        double[] array1d;
        double[][] array2d = new double[n][v]; //[number of observations][number of samples]

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
