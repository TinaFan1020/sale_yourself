package ML.audio.feature;
/**
 * calculates energy from given PCM of a frame
 *
 * @author Madhav Pandey
 * @reference Spectral Features for Automatic Text-Independent Speaker
 *            Recognition @author Tomi Kinnunen, @fromPage ##
 */
public class Energy {

	/**
	 *
	 */
	private final int samplePerFrame;

	/**
	 *
	 * @param samplePerFrame
	 */
	public Energy(int samplePerFrame) {
		this.samplePerFrame = samplePerFrame;
	}

	/**
	 *
	 * @param framedSignal
	 * @return energy of given PCM frame
	 */
	public double[] calcEnergy(float[][] framedSignal) {
		double[] energyValue = new double[framedSignal.length];
		for (int i = 0; i < framedSignal.length; i++) {
			float sum = 0;
			for (int j = 0; j < samplePerFrame; j++) {
				// sum the square
				sum += Math.pow(framedSignal[i][j], 2);
			}
			// find log
			energyValue[i] = Math.log(sum);
		}
		return energyValue;
	}
}