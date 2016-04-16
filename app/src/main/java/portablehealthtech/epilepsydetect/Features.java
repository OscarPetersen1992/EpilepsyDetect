package portablehealthtech.epilepsydetect;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.transform.DftNormalization;
import org.apache.commons.math3.transform.FastFourierTransformer;
import org.apache.commons.math3.transform.TransformType;

import jwave.Transform;
import jwave.transforms.FastWaveletTransform;
import jwave.transforms.wavelets.daubechies.Daubechies4;

public class Features {

    public static double power_deltaband(double [] input)
    {
        // calculate band power of delta band
        double[] PSD = psd(input);

        int fs = 200;
        int N = 512;
        double f1 = 0.5; // 0.5 Hz
        double f2 = 4.0;   // 4.0 Hz
        int f1_delta_N = (int) Math.floor((f1/fs*(N-1)));
        int f2_delta_N = (int) Math.ceil((f2*(N-1))/fs);
        int count=0;

        double[] psd_band = new double[f2_delta_N-f1_delta_N+1];
        for(int i =f1_delta_N;i<f2_delta_N+1;i++){
            psd_band[count] = PSD[i];
            count++;
        }

        int N_1 = 200/2; // fs/2
        double[] freq = new double[(N_1-1)];
        for(int i=0;i<(N_1-1);i++) {
            freq[i]=i*(200.0/512);
        }

        return freq[1]*sum(psd_band); // bandpower

    }


    public static double shannon(double [] input)
    {
        // calculate Shannon's Entropy
        double[] p1 = new double[input.length];
        double[] p2 = new double[input.length];
        double entropy;

        try {
            for(int i=0;i<input.length;i++) {
                p1[i]=Math.abs(input[i]+1E-6);
            }
            double s = sum(p1);

            for(int i=0;i<input.length;i++) {
                p2[i]=-(p1[i]/s*Math.log(p1[i]/s) / Math.log(2));
            }


        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }

        entropy  = sum(p2);

        return entropy;

    }

    public static double[] psd(double [] input)
    {
        int N = input.length;
        double[] tempConversion = new double[N/2+1];

        FastFourierTransformer fft = new FastFourierTransformer(DftNormalization.STANDARD);
        try {
            Complex[] complx = fft.transform(input, TransformType.FORWARD);

            for (int i = 0; i < (N/2+1); i++) {
                double rr = (complx[i].getReal());
                double ri = (complx[i].getImaginary());
                if(i==0 || i==(N/2)) {
                    tempConversion[i] = Math.pow(Math.sqrt((rr * rr) + (ri * ri)), 2) / (N * 200); // Power Spectrum Density

                }else {
                    tempConversion[i] = 2*Math.pow(Math.sqrt((rr * rr) + (ri * ri)), 2) / (N * 200); // Power Spectrum Density

                }
            }

        } catch (IllegalArgumentException e) {
            System.out.println(e);
        }

        return tempConversion;

    }

    public static double sum(double...values) {
        double result = 0;
        for (double value:values)
            result += value;
        return result;
    }

    public static double[] wavelet_logsum(double[] window) {
        Transform wavelet = new Transform (new FastWaveletTransform( new Daubechies4( ) ) );

        double[ ] details = wavelet.forward( window );

        //System.out.println(Arrays.toString(details));

        // Initialize length of detail levels
        int nFeatures =4;
        double[] features = new double[nFeatures];
        double[] detail1 = new double[window.length/2];
        double[] detail2 = new double[window.length/4];
        double[] detail3 = new double[window.length/8];
        double[] detail4 = new double[window.length/16];
        double[] sum_detail = new double[nFeatures];


        // Get the abs of detail levels

        for(int j=0; j<window.length/2; j++){
            detail1[j] = details[window.length/2+j];
            sum_detail[0] += Math.abs(detail1[j]);

            if(j<window.length/4) {
                detail2[j] = details[window.length / 4 + j];
                sum_detail[1] += Math.abs(detail2[j]);
            }

            if(j<window.length/8) {
                detail3[j] = details[window.length/8+j];
                sum_detail[2] += Math.abs(detail3[j]);
            }

            if(j<window.length/16) {
                detail4[j] = details[window.length/16+j];
                sum_detail[3] += Math.abs(detail4[j]);
            }

        }

        // Calculate the features
        for(int j=0; j<sum_detail.length; j++) {
            features[j] = Math.log(sum_detail[j]);
            //svm_node node = new svm_node();
            //node.index = j+1;
            //node.value = features[j];
        }

        return features;
    }
}
