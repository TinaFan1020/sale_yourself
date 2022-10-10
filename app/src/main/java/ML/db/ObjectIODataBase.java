package ML.db;

import static android.os.Environment.DIRECTORY_MUSIC;

import android.os.Environment;

import ML.classify.speech.CodeBookDictionary;
import ML.classify.speech.HMMModel;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author Ganesh Tiwari
 */
public class ObjectIODataBase implements DataBase {

    /**
     * type of current model,,gmm,hmm,cbk, which is extension ofsaved file
     */
    String			type;
    /**
     *
     */
    List< String >	modelFiles;
    /**
     *
     */
    String[]		userNames;
    String			CURRENTFOLDER;
    /**
     * the file name to same codebook, adds .cbk extension automatically
     */
    final String			CODEBOOKFILENAME	= "codebook";
    String			currentModelType;

    /**
     * MAKE SURE THAT Files are/will be in this folder structure the folder structure for training : (Selected)DBROOTFOLDER\ \speechTrainWav\\apple\\apple01.wav
     * \speechTrainWav\\apple\\apple02.wav \speechTestWav\\cat\\cat01.wav \speechTestWav\\cat\\cat01.wav \speechTestWav\\cat\\cat01.wav \codeBook\\codeBook.cbk
     * \models\\HMM\\apple.hmm \models\\HMM\\cat.hmm \models\\GMM\\ram.gmm \models\\GMM\\shyam.gmm
     */
    public ObjectIODataBase( ) {
    }

    /**
     * @param type
     *            type of the model, valid entry are either gmm, hmm, or cbk
     */
    public void setType( String type ) {
        this.type = type;
        File ModelFile = new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()
                    + "//SoundKeyboard_ModelFile");
        if (!ModelFile.exists()){
            ModelFile.mkdir();
        }
        if ( this.type.equalsIgnoreCase( "hmm" ) ) {
            CURRENTFOLDER = ModelFile.getPath() + File.separator + "HMM";
            File ModelFile_check1 = new File(CURRENTFOLDER);
            if (!ModelFile_check1.exists()){
                ModelFile_check1.mkdir();
            }
        }
        if ( this.type.equalsIgnoreCase( "cbk" ) ) {
            CURRENTFOLDER = ModelFile.getPath() + File.separator + "codeBook";
            File ModelFile_check2 = new File(CURRENTFOLDER);
            if (!ModelFile_check2.exists()){
                ModelFile_check2.mkdir();
            }
        }
    }

    /**
     *
     */
    @Override
    public Model readModel( String name ) throws Exception {
        Model model = null;
        if ( type.equalsIgnoreCase( "hmm" ) ) {
            ObjectIO< HMMModel > oio = new ObjectIO<>();
            model = new HMMModel( );
            model = oio.readModel( CURRENTFOLDER + File.separator + name + "." + type );
            //            System.out.println("Type " + type);
            //            System.out.println("Read ::::: " + DBROOTFOLDER + "\\" + CURRENTFOLDER + "\\" + name + "." + type);
            // System.out.println(model);
        }
        if ( type.equalsIgnoreCase( "cbk" ) ) {
            ObjectIO< CodeBookDictionary > oio = new ObjectIO<>();
            model = new CodeBookDictionary( );
            model = oio.readModel( CURRENTFOLDER + File.separator + CODEBOOKFILENAME + "." + type );
            //            System.out.println("Read ::::: " + DBROOTFOLDER + "\\" + CURRENTFOLDER + "\\" + CODEBOOKFILENAME + "." + type);
        }
        return model;
    }

    /**
     *
     */
    @Override
    public List< String > readRegistered( ) {

        modelFiles = readRegisteredWithExtension( );
        System.out.println( "modelFiles length (Oiodb) :" + modelFiles.size( ) );
        return removeExtension( modelFiles );
    }

    /**
     *
     */
    @Override
    public void saveModel( Model model, String name ) throws Exception{

        if ( type.equalsIgnoreCase( "hmm" ) ) {
            ObjectIO< HMMModel > oio = new ObjectIO<>();
            oio.setModel( ( HMMModel ) model );
            oio.saveModel( CURRENTFOLDER + File.separator + name + "." + type );
        }
        if ( type.equalsIgnoreCase( "cbk" ) ) {
            ObjectIO< CodeBookDictionary > oio = new ObjectIO<>();
            oio.setModel( ( CodeBookDictionary ) model );
            oio.saveModel( CURRENTFOLDER + File.separator + CODEBOOKFILENAME + "." + type );

        }

    }

    private List< String > readRegisteredWithExtension( ) {
        File modelPath = new File( CURRENTFOLDER );

        modelFiles = Arrays.asList( modelPath.list( ) );// must return only folders

        return modelFiles;
    }

    private String removeExtension( String fileName ) {

        return fileName.substring( 0, fileName.lastIndexOf( '.' ) );

    }

    private List< String > removeExtension( List< String > modelFiles ) {
        // remove the ext i.e., type
        List< String > noExtension = new ArrayList< >( );
        for ( String fileName: modelFiles ) {
            noExtension.add( removeExtension( fileName ) );// TODO:check the lengths
        }

        return noExtension;
    }
}