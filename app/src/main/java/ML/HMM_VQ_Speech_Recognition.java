package ML;

import java.io.File;
import java.util.Arrays;
import java.util.List;


import ML.audio.JSoundCapture;
import ML.db.TrainingTestingWaveFiles;


/**
 * Main application- contains GUI and main method - train / test / data collection all can be done from here
 *
 * @author Ganesh Tiwari
 */
public class HMM_VQ_Speech_Recognition {

    private static final long	serialVersionUID		= 1L;
    private JSoundCapture		soundCapture			= null;
    //private JTabbedPane			jTabbedPane				= null;
    //private JPanel				verifyPanel				= null;
    //private JPanel				trainPanel				= null;
    //private JPanel				runTrainingPanel		= null;
    //private JButton				getWordButton			= null;
    //private JButton				btnVerify				= null;
    //private JComboBox			wordsComboBoxVerify		= null;
    //private JComboBox			wordsComboBoxAddWord	= null;

    //private JButton				getWordButton1			= null;

    private final Operations opr						= new Operations( );
    //private JLabel				aboutLBL;
    //private JLabel				statusLBLRecognize;
   // private JTextField			addWordToCombo			= null;
    //private JButton				addWordToComboBtn		= null;
    //private JButton				addTrainSampleBtn		= null;
    //private JLabel				lblChooseAWord;
    //private JLabel				lblAddANew;

    //private JButton				generateCodeBookBtn;
    //private JButton				btnNewButton_2;

    /**
     * This is the default constructor
     */
    public HMM_VQ_Speech_Recognition( ) {
        super( );
        //initialize( );
        //ErrorManager.setMessageLbl( getStatusLblRecognize( ) );
    }

    /*
     * This method initializes this
     *
     * @return void

    private void initialize( ) {
        this.setSize( 485, 335 );
        this.setContentPane( getJContentPane( ) );
        this.setTitle( "HMM/VQ Speech Recognition - by GT" );
    }
    */
    /**
     * create a setboard

    
    private JTabbedPane getJTabbedPane( ) {
        if ( jTabbedPane == null ) {
            jTabbedPane = new JTabbedPane( );
            jTabbedPane.setBounds( new Rectangle( 10, 94, 449, 178 ) );
            jTabbedPane.addTab( "Verify Word", null, getVerifyWordPanel( ), null );
            jTabbedPane.addTab( "Add Sample", null, getAddSamplePanel( ), null );
            jTabbedPane.addTab( "Run HMM Train", null, getRunTrainingPanel( ), null );
            jTabbedPane.addChangeListener(e -> {
                System.out.println( "state changed" );
                if ( jTabbedPane.getSelectedIndex( ) == 0 ) {
                    soundCapture.setSaveFileName( null );
                } else if ( jTabbedPane.getSelectedIndex( ) == 1 ) {
                    soundCapture.setSaveFileName( "TrainWav" + File.separator + getWordsComboBoxAddWord( ).getSelectedItem( ) + File.separator + getWordsComboBoxAddWord( ).getSelectedItem( ) );
                }

            });
        }
        return jTabbedPane;
    }
    */
    /**
    private File getTestFile( ) {
        JFileChooser jfc = new JFileChooser( "Select WAVE File to Verify" );
        jfc.setFileSelectionMode( JFileChooser.FILES_AND_DIRECTORIES );
        jfc.setSize( new Dimension( 541, 326 ) );
        jfc.setFileFilter( new javax.swing.filechooser.FileFilter( ) {

            @Override
            public String getDescription( ) {
                return ".WAV & .WAVE Files";
            }

            @Override
            public boolean accept( File f ) {
                return ( f.getName( ).toLowerCase( ).endsWith( "wav" ) || f.getName( ).toLowerCase( ).endsWith( "wave" ) || f.isDirectory( ) );
            }
        } );
        int chooseOpt = jfc.showOpenDialog( this );
        if ( chooseOpt == JFileChooser.APPROVE_OPTION ) {
            File file = jfc.getSelectedFile( );
            System.out.println( "selected File " + file );
            return file;
        }
        return null;
    }
     */
    /**

    private JPanel getVerifyWordPanel( ) {
        if ( verifyPanel == null ) {
            JLabel jLabel = new JLabel( );
            jLabel.setBounds( new Rectangle( 13, 55, 245, 20 ) );
            jLabel.setText( "Or... Select a Word From the List to Verify" );
            verifyPanel = new JPanel( );
            verifyPanel.setLayout( null );
            verifyPanel.add( getGetWordButton( ), null );
            verifyPanel.add( getWordsComboBoxVerify( ), null );
            verifyPanel.add( jLabel, null );
            verifyPanel.add( getGetWordButton1( ), null );
            verifyPanel.add( getBtnVerify( ) );
            verifyPanel.add( getStatusLblRecognize( ) );
        }
        return verifyPanel;
    } */
    /* 確認程式 */
/*
    private JButton getBtnVerify( ) {
        if ( btnVerify == null ) {
            btnVerify = new JButton( "Verify" );
            btnVerify.addActionListener(e -> {
                if ( soundCapture.isSoundDataAvailable( ) && getWordsComboBoxVerify( ).getItemCount( ) > 0 ) {

                    try {

                        String recWord = opr.hmmGetWordFromAmplitureArray( soundCapture.getAudioData( ) );
                        if ( recWord.equalsIgnoreCase( getWordsComboBoxVerify( ).getSelectedItem( ).toString( ) ) ) {
                            getStatusLblRecognize( ).setText( "Verified" );
                        } else {
                            getStatusLblRecognize( ).setText( "Not Verified" );
                        }
                    } catch ( Exception e2 ) {
                        e2.printStackTrace( );
                    }
                }
            });
            btnVerify.setBounds( 126, 111, 89, 24 );
        }
        return btnVerify;
    }

 */

    public void verify()
    {
        if ( soundCapture.isSoundDataAvailable( )  ) {
//&& getWordsComboBoxVerify( ).getItemCount( ) > 0 and 下拉式選單可選選項>0個
            try {

                String recWord = opr.hmmGetWordFromAmplitureArray( soundCapture.getAudioData( ) );
                if ( recWord.equalsIgnoreCase( "this should be 下拉式選單中的文字" ) ) {
                    //settext verified
                } else {
                   //settext not verified
                }
            } catch ( Exception e2 ) {
                e2.printStackTrace( );
            }
        }
    }



    /**
     * This method initializes jPanel1
     *
     * @return javax.swing.JPanel
     */
    /**
    private JPanel getAddSamplePanel( ) {
        if ( trainPanel == null ) {
            trainPanel = new JPanel( );
            trainPanel.setLayout( null );
            trainPanel.add( getWordsComboBoxAddWord( ), null );
            trainPanel.add( getAddWordToCombo( ), null );
            trainPanel.add( getAddWordToComboBtn( ), null );
            trainPanel.add( getLblChooseAWord( ) );
            trainPanel.add( getLblAddANew( ) );
            // trainPanel.add(getAddTrainSampleBtn(), null);
        }
        return trainPanel;
    }*/

    /**
     * This method initializes runTrainingPanel
     *
     * @return javax.swing.JPanel
     */
    /**
    private JPanel getRunTrainingPanel( ) {
        if ( runTrainingPanel == null ) {
            runTrainingPanel = new JPanel( );
            runTrainingPanel.setLayout( null );
            runTrainingPanel.add( getGenerateCodeBookBtn( ) );
            runTrainingPanel.add( getBtnNewButton_2( ) );
        }
        return runTrainingPanel;
    }*/

    /**
     * This method initializes getWordButton
     *
     * @return javax.swing.JButton
     */
    /*
    private JButton getGetWordButton( ) {
        if ( getWordButton == null ) {
            getWordButton = new JButton( "Recognize With Just Recorded" );
            getWordButton.addActionListener(arg0 -> {
                if ( soundCapture.isSoundDataAvailable( ) && getWordsComboBoxVerify( ).getItemCount( ) > 0 ) {

                    try {

                        getStatusLblRecognize( ).setText( opr.hmmGetWordFromAmplitureArray( soundCapture.getAudioData( ) ) );
                    } catch ( Exception e ) {
                        e.printStackTrace( );
                    }

                }
            });
            getWordButton.setBounds( new Rectangle( 13, 8, 202, 24 ) );
        }
        return getWordButton;
    }


     */
    public void getword()
    {

        if ( soundCapture.isSoundDataAvailable( )) {
// && getWordsComboBoxVerify( ).getItemCount( ) > 0
            try {

                //setText( opr.hmmGetWordFromAmplitureArray( soundCapture.getAudioData( ) ) );
            } catch ( Exception e ) {
                e.printStackTrace( );
            }

        }

    }




    /**
     * This method initializes wordsComboBox
     *
     * @return javax.swing.JComboBox
     */
    //下拉式選單
    /*
    private JComboBox getWordsComboBoxVerify( ) {
        if ( wordsComboBoxVerify == null ) {
            DataBase db = new ObjectIODataBase( );
            db.setType( "hmm" );
            wordsComboBoxVerify = new JComboBox( );
            try {
                List< String > regs = Arrays.asList(db.readRegistered());
                for ( String string: regs ) {
                    wordsComboBoxVerify.addItem( string );
                }
            } catch ( Exception ignored) {
            }
            wordsComboBoxVerify.setBounds( new Rectangle( 13, 75, 202, 24 ) );
        }
        return wordsComboBoxVerify;
    }

     */
//選訓練檔資料的名子的東西
    //private 選單
    /*private void getWordsComboBoxAddWord( ) {
        if ( true ) {
            //if wordsComboBoxAddWord == null if選單不存在
            TrainingTestingWaveFiles ttwf = new TrainingTestingWaveFiles( "train" );
            //wordsComboBoxAddWord = new JComboBox( ); 選單=new 選單()
            try {
                List< String > regs = Arrays.asList(ttwf.readWordWavFolder());
                for (String reg : regs) {
                    //wordsComboBoxAddWord.addItem(reg);
                    //for each string in training files , add to 選單
                }
            } catch ( Exception ignored) {
            }
            //wordsComboBoxAddWord.addItemListener(e -> soundCapture.setSaveFileName( "TrainWav" + File.separator + getWordsComboBoxAddWord( ).getSelectedItem( ) + File.separator + getWordsComboBoxAddWord( ).getSelectedItem( ) ));
        }
        return ;
    }*/

    /**
     * This method initializes getWordButton1
     *
     * @return javax.swing.JButton
     */
    /*
    private JButton getGetWordButton1( ) {
        if ( getWordButton1 == null ) {
            getWordButton1 = new JButton( "Recognize a Saved WAV File" );
            getWordButton1.addActionListener(e -> {
                System.out.println( "getting word file totest" );
                File f = getTestFile( );
                if ( f != null ) {

                    try {

                        getStatusLblRecognize( ).setText( opr.hmmGetWordFromFile( f ) );
                    } catch ( Exception e2 ) {
                        e2.printStackTrace( );
                    }

                }
            });
            getWordButton1.setBounds( new Rectangle( 225, 8, 189, 24 ) );
        }
        return getWordButton1;
    }

     */

    public void cohoose_file()
    {
        System.out.println( "getting word file totest" );
        File f = null;
        //File f = getTestFile( );
        //todo 做好選檔案的東西
        if ( f != null ) {

            try {
                String res=opr.hmmGetWordFromFile( f );
                //getStatusLblRecognize( ).setText( opr.hmmGetWordFromFile( f ) );
            } catch ( Exception e2 ) {
                e2.printStackTrace( );
            }

        }
    }


    /**
     * This method initializes jContentPane
     *
     * @return javax.swing.JPanel
     */
    /*
    private JPanel getJContentPane( ) {
        if ( jContentPane == null ) {
            jContentPane = new JPanel( );
            jContentPane.setLayout( null );
            jContentPane.add( getJTabbedPane( ) );
            jContentPane.add( getSoundCapture( ) );
            jContentPane.add( getAboutLBL( ) );
        }
        return jContentPane;
    }

     */

    private JSoundCapture getSoundCapture( ) {
        if ( soundCapture == null ) {
            soundCapture = new JSoundCapture( true, true );
            //soundCapture.setBounds( 10, 10, 431, 74 );
        }
        return soundCapture;
    }

    /*
    private JLabel getAboutLBL( ) {
        if ( aboutLBL == null ) {
            aboutLBL = new JLabel( "Developer: Ganesh Tiwari,Visit ganeshtiwaridotcomdotnp.blogspot.com For MORE " );
            aboutLBL.setHorizontalAlignment( SwingConstants.CENTER );
            aboutLBL.setFont( new Font( "Tahoma", Font.PLAIN, 11 ) );
            aboutLBL.setBounds( 10, 275, 449, 16 );
        }
        return aboutLBL;
    }

     */
/*
    private JLabel getStatusLblRecognize( ) {
        if ( statusLBLRecognize == null ) {
            statusLBLRecognize = new JLabel( "" );
            statusLBLRecognize.setHorizontalAlignment( SwingConstants.CENTER );
            statusLBLRecognize.setBounds( 225, 71, 189, 68 );
        }
        return statusLBLRecognize;
    }

 */

    /**
     * This method initializes addWordToCombo
     *
     * @return javax.swing.JTextField
     */
    //弄出畫畫/寫字的版面
    /*
    private JTextField getAddWordToCombo( ) {
        if ( addWordToCombo == null ) {
            addWordToCombo = new JTextField( );
            addWordToCombo.setBounds( new Rectangle( 10, 42, 202, 24 ) );
        }
        return addWordToCombo;
    }

     */

    /**
     * This method initializes addWordToComboBtn
     *
     * @return javax.swing.JButton
     */
    //新增一個label
    /*
    private JButton getAddWordToComboBtn( ) {
        if ( addWordToComboBtn == null ) {
            addWordToComboBtn = new JButton( "Add Word" );
            addWordToComboBtn.addActionListener(e -> {
                String newWord = Utils.clean( getAddWordToCombo( ).getText( ) );
                boolean isAlreadyRegistered = false;
                if ( !newWord.isEmpty( ) ) {
                    // already in combo box
                    for ( int i = 0; i < getWordsComboBoxAddWord( ).getItemCount( ); i++ ) {
                        if ( getWordsComboBoxAddWord( ).getItemAt( i ).toString( ).equalsIgnoreCase( newWord ) ) {
                            isAlreadyRegistered = true;
                            break;
                        }
                    }
                    // if not add
                    if ( !isAlreadyRegistered ) {
                        getWordsComboBoxAddWord( ).addItem( getAddWordToCombo( ).getText( ) );
                        getWordsComboBoxAddWord( ).repaint( );
                        getAddWordToCombo( ).setText( "" );
                    }
                }
            });
            addWordToComboBtn.setBounds( new Rectangle( 222, 42, 142, 24 ) );
        }
        return addWordToComboBtn;
    }

     */

    /**
     * This method initializes addTrainSample
     *
     * @return javax.swing.JButton
     */
    //錄音的按鈕 把這一段音檔儲存
    /*
    private JButton getAddTrainSampleBtn( ) {
        if ( addTrainSampleBtn == null ) {
            addTrainSampleBtn = new JButton( "Record" );
            addTrainSampleBtn.setBounds( new Rectangle( 223, 103, 141, 24 ) );
            addTrainSampleBtn.addActionListener(e -> {
                if ( addTrainSampleBtn.getText( ).startsWith( "Record" ) ) {
                    soundCapture.startRecord( );
                    addTrainSampleBtn.setText( "Save Captured" );
                } else if ( addTrainSampleBtn.getText( ).startsWith( "Save" ) ) {
                    // TODO: decouple path, may be singleton conf for path
                    soundCapture.setSaveFileName( "TrainWav" + File.separator + getWordsComboBoxAddWord( ).getSelectedItem( ) + File.separator + getWordsComboBoxAddWord( ).getSelectedItem( ) );

                    try {

                        soundCapture.getFileNameAndSaveFile( );
                        addTrainSampleBtn.setText( "Record" );

                    } catch ( Exception e2 ) {
                        e2.printStackTrace( );
                    }
                }
            });
        }
        return addTrainSampleBtn;
    }

     */


    //TODO 呼叫HMM
    /*
    public static void main( String[] args ) {
        try {
            UIManager.setLookAndFeel( UIManager.getSystemLookAndFeelClassName( ) );
        } catch ( Exception e ) {
            System.out.println( e.toString( ) );
        }
        SwingUtilities.invokeLater(() -> {
            HMM_VQ_Speech_Recognition test = new HMM_VQ_Speech_Recognition( );
            test.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

            test.setResizable( false );
            test.setVisible( true );
        });
    }

     */
/*
    private JLabel getLblChooseAWord( ) {
        if ( lblChooseAWord == null ) {
            lblChooseAWord = new JLabel( "Choose a word to record sound and save to corresponding folder" );
            lblChooseAWord.setBounds( 11, 77, 325, 14 );
        }
        return lblChooseAWord;
    }

    private JLabel getLblAddANew( ) {
        if ( lblAddANew == null ) {
            lblAddANew = new JLabel( "Add a new Word" );
            lblAddANew.setBounds( 11, 11, 126, 14 );
        }
        return lblAddANew;
    }


 */
    public void generate()
    {
        try {
            opr.generateCodebook( );
        } catch ( Exception e2 ) {
            e2.printStackTrace( );
        }
        return;
    }


/*
    private JButton getGenerateCodeBookBtn( ) {
        if ( generateCodeBookBtn == null ) {
            generateCodeBookBtn = new JButton( "Generate CodeBook" );
            generateCodeBookBtn.addActionListener(e -> {
                try {
                    opr.generateCodebook( );
                } catch ( Exception e2 ) {
                    e2.printStackTrace( );
                }
            });
            generateCodeBookBtn.setBounds( 10, 32, 167, 23 );
        }
        return generateCodeBookBtn;
    }
*/

    public void train()
    {
        try {
            opr.hmmTrain( );
        } catch ( Exception e2 ) {
            e2.printStackTrace( );
    }

    /*
    private JButton getBtnNewButton_2( ) {
        if ( btnNewButton_2 == null ) {
            btnNewButton_2 = new JButton( "Train HMM" );
            btnNewButton_2.addActionListener(e -> {
                try {
                    opr.hmmTrain( );
                } catch ( Exception e2 ) {
                    e2.printStackTrace( );

                }
            });
            btnNewButton_2.setBounds( 10, 74, 167, 23 );
        }
        return btnNewButton_2;
    }
     */
}
}