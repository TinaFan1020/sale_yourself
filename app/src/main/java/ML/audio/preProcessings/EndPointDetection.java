package ML.audio.preProcessings;

/**
 * @author Madhav Pandey, Ganesh Tiwari
 * @reference 'A New Silence Removal and Endpoint Detection Algorithm for Speech
 *            and Speaker Recognition Applications' by IIT, Khragpur
 */
public class EndPointDetection {

    private final float[] originalSignal; // input
    private final int firstSamples;
    private final int samplePerFrame;

    public EndPointDetection(float[] originalSignal, int samplingRate) {
        this.originalSignal = originalSignal;
        samplePerFrame = samplingRate / 1000;

        firstSamples = samplePerFrame * 200;// according to formula
    }

    public float[] doEndPointDetection() {
        float[] voiced = new float[originalSignal.length];// for identifying
        // each
        // sample whether it is
        // voiced or unvoiced
        float sum = 0;
        double sd = 0.0;
        double m = 0.0;

        // 1. calculation of mean
        for (int i = 0; i < firstSamples; i++) {
            sum += originalSignal[i];
        }
        // System.err.println("total sum :" + sum);
        m = sum / firstSamples;// mean
        sum = 0;// reuse var for S.D.

        // 2. calculation of Standard Deviation
        for (int i = 0; i < firstSamples; i++) {
            sum += Math.pow((originalSignal[i] - m), 2);
        }
        sd = Math.sqrt(sum / firstSamples);
        // System.err.println("summm sum :" + sum);
        // System.err.println("mew :" + m);
        // System.err.println("sigma :" + sd);

        // 3. identifying whether one-dimensional Mahalanobis distance function
        // i.e. |x-u|/s greater than ####3 or not,
        for (int i = 0; i < originalSignal.length; i++) {
            // System.out.println("x-u/SD  ="+(Math.abs(originalSignal[i] -u ) /
            // sd));
            if ((Math.abs(originalSignal[i] - m) / sd) > 2) {
                voiced[i] = 1;
            }
            else {
                voiced[i] = 0;
            }
        }

        // 4. calculation of voiced and unvoiced signals
        // mark each frame to be voiced or unvoiced frame
        int frameCount = 0;
        int usefulFramesCount = 1;
        int count_voiced = 0;
        int count_unvoiced = 0;
        int[] voicedFrame = new int[originalSignal.length / samplePerFrame];
        int loopCount = originalSignal.length - (originalSignal.length % samplePerFrame);// skip
        // the
        // last
        for (int i = 0; i < loopCount; i += samplePerFrame) {
            count_voiced = 0;
            count_unvoiced = 0;
            for (int j = i; j < i + samplePerFrame; j++) {
                if (voiced[j] == 1) {
                    count_voiced++;
                }
                else {
                    count_unvoiced++;
                }
            }
            if (count_voiced > count_unvoiced) {
                usefulFramesCount++;
                voicedFrame[frameCount++] = 1;
            }
            else {
                voicedFrame[frameCount++] = 0;
            }
        }

        // 5. silence removal
        // output
        float[] silenceRemovedSignal = new float[usefulFramesCount * samplePerFrame];
        int k = 0;
        for (int i = 0; i < frameCount; i++) {
            if (voicedFrame[i] == 1) {
                for (int j = i * samplePerFrame; j < i * samplePerFrame + samplePerFrame; j++) {
                    silenceRemovedSignal[k++] = originalSignal[j];
                }
            }
        }
        // end
        return silenceRemovedSignal;
    }
}