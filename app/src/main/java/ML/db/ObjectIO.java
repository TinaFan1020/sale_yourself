package ML.db;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.io.*;

/**
 * This Class works for both any object <code><T></code>, which implements the Model interface
 *
 * @author Ganesh Tiwari
 * @param <T>
 */
public class ObjectIO< T > {

	T							model;

	/**
	 * default constructor of modelDB
	 */
	public ObjectIO( ) {
	}

	/**
	 * sets the model to save to db
	 *
	 * @param model
	 *            model of current type to save into db
	 */
	public void setModel( T model ) {
		this.model = model;
	}

	/**
	 * saves the model to {@code filePath} of type T
	 *
	 * @param filePath
	 */
	public void saveModel( String filePath ) throws Exception {

		File file = new File( filePath );
		if ( !file.exists( ) ) {
			file.getParentFile( ).mkdirs( );
		}


		// open file
		ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
		// save model
		output.writeObject( model );
		output.close( );
	}

	/**
	 * read the model from {@code filePath} of type T
	 *
	 * @param filePath
	 * @return the model of type T
	 */
	public T readModel( String filePath ) throws Exception {
		// open file
		ObjectInputStream input = new ObjectInputStream(new FileInputStream(filePath));
		// read
		model = ( T ) input.readObject( );
		input.close( );
		return model;
	}
}