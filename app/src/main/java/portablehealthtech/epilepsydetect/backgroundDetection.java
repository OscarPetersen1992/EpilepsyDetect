package portablehealthtech.epilepsydetect;

import android.app.IntentService;
import android.content.Intent;
import android.os.Environment;

import java.io.BufferedReader;
import java.io.File;
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
    String path = "test2.csv";
        try {
            // Load data signal
            allEEG = load_csv(path);
            // Load SVM model
            /*InputStreamReader svm_file = new InputStreamReader(getAssets().open("svmAndroid.model"));

            File svmFile = new File(getApplicationInfo().sourceDir, "svmAndroid.model");
            FileReader svm_file = new FileReader(svmFile);
            BufferedReader bufferedReader = new BufferedReader(svm_file);
            svmModel = svm.svm_load_model(bufferedReader);
            int k = svmModel.l;*/
        } catch (IOException e) {
            e.printStackTrace();
        }

        int numOfWindows = allEEG.length;
        predictedLabels = new int[numOfWindows];

        int index = 0;
        // Detect seizures
        while (index < numOfWindows) {

            window = allEEG[index];

            wavelets[0] = window[1];
            wavelets[1] = window[2];
            wavelets[2] = window[3];
            wavelets[3] = window[4];
            shannon = window[0];

            double[] feat_test = new double[5];
            feat_test[0] = shannon;
            feat_test[1] = wavelets[0];
            feat_test[2] = wavelets[1];
            feat_test[3] = wavelets[2];
            feat_test[4] = wavelets[3];

            //create svm node
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
            /*
            wavelets = Features.wavelet_logsum(window);
            shannon = Features.shannon(window);

            double[] feat_test = new double[5];
            feat_test[0] = shannon;
            feat_test[1] = wavelets[0];
            feat_test[2] = wavelets[1];
            feat_test[3] = wavelets[2];
            feat_test[4] = wavelets[3];

            //create svm node
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
            */

            firstTempPredictedLabels = (int) svm.svm_predict(svmModel, featuresVec);
            System.out.println(index);
            System.out.println(Arrays.toString(feat_test));
            System.out.println(firstTempPredictedLabels);
/*
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
            */

            index++;
        }


            // Preprocess

        // Store a seizure

        if (index == 2) {

            String current_date = dateFormat.format(new Date());
            db.addSeizure(new Seizure(1234, current_date, 10.4));


        }

    }



    public double[][] load_csv(String path) throws IOException{
        List<Double> EEG = new ArrayList<>();
        double[] array1d;
        double[][] array2d = new double[275][5]; //[number of observations][number of samples]

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
