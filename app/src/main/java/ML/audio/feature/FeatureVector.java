package ML.audio.feature;

import java.io.Serializable;

/**
 *
 * @author Ganesh Tiwari for storing all coeffs of spectral features<br>
 *         include mfcc + delta mfcc + delta delta mfcc include engergy + delta
 *         energy+ delta delta energy
 */
public class FeatureVector implements Serializable {

	/**
	 * 2d array of feature vector, dimension=noOfFrame*noOfFeatures
	 */
	private double[][] mfccFeature;
	private double[][] featureVector;// all

	public FeatureVector() {
	}

	public double[][] getMfccFeature() {
		return mfccFeature;
	}

	public void setMfccFeature(double[][] mfccFeature) {
		this.mfccFeature = mfccFeature;
	}

	public int getNoOfFrames() {
		return featureVector.length;
	}

	public void setNoOfFrames(int noOfFrames) {
	}

	public int getNoOfFeatures() {
		return featureVector[0].length;
	}

	public void setNoOfFeatures(int noOfFeatures) {
	}

	/**
	 * returns feature vector
	 *
	 * @return
	 */
	public double[][] getFeatureVector() {
		return featureVector;
	}

	/**
	 * sets the feature vector array
	 *
	 * @param featureVector
	 */
	public void setFeatureVector(double[][] featureVector) {
		this.featureVector = featureVector;
	}
}