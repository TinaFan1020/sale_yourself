package ML.db;

import static android.os.Environment.DIRECTORY_MUSIC;

import android.os.Environment;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * various operations relating to reading train/testing wav folders<br>
 * works according to the filePath supplied in constructor arguement
 *
 * @author Ganesh Tiwari
 */
public class TrainingTestingWaveFiles {

	protected List< String >	folderNames;
	protected File[][]			waveFiles;
	protected File baseFile;

	/**
	 * constructor, sets the wavFile path according to the args supplied
	 *
	 * @param testOrTrain
	 */
	public TrainingTestingWaveFiles( String testOrTrain ) {
		if ( testOrTrain.equalsIgnoreCase( "test" ) ) {
			setWavPath( new File( "TestWav" ) );
		} else if ( testOrTrain.equalsIgnoreCase( "train" ) ) {
			setWavPath( new File( "TrainWav" ) );
		}

	}

	private void readFolder() {
		System.out.println("++++++find it+++++");
		baseFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()
				+ "//SoundKeyboard_TrainWav");
		if (!baseFile.exists()){
			baseFile.mkdir();
		}
		System.out.println(baseFile.getAbsolutePath());
		if(getWavPath().list().length != 0) {
			folderNames = Arrays.asList(new String[getWavPath().list().length]);
			folderNames = Arrays.asList(getWavPath().list());// must return only folders
		}
		else {
			System.out.println("++++++null array++++");
		}

	}

	public List< String > readWordWavFolder( ) {
		readFolder( );
		return folderNames;
	}

	public File[][] readWaveFilesList( ) {
		readFolder( );
		waveFiles = new File[ folderNames.size( ) ][];
		for ( int i = 0; i < folderNames.size( ); i++ ) {

			System.out.println( folderNames.get( i ) );
			File wordDir = new File( getWavPath( ) + File.separator + folderNames.get( i ) + File.separator );
			waveFiles[ i ] = wordDir.listFiles( );
		}
		System.out.println( "++++++Folder's Content+++++" );
		for (File[] waveFile : waveFiles) {
			for (int j = 0; j < waveFile.length; j++) {
				System.out.print(waveFile[j].getName() + "\t\t");
			}
			System.out.println();
		}
		return waveFiles;

	}

	public File getWavPath() {
		return baseFile;
	}

	public void setWavPath(File baseFile) {
		this.baseFile = baseFile;
		System.out.println("Current wav file Path   :" + this.baseFile.getName());
	}
}
