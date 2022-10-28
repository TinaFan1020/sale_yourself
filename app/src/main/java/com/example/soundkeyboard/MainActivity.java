package com.example.soundkeyboard;


import static android.os.Environment.DIRECTORY_MUSIC;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.LinkedList;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import java.io.FileReader;
import java.util.Locale;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.content.Context;
import android.widget.TextView;

import com.karlotoy.perfectune.instance.PerfectTune;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.time.LocalDateTime;

import Catalano.Math.ComplexNumber;
import Catalano.Math.Transforms.FourierTransform;
import ML.HMM_VQ_Speech_Recognition;
import ML.Operations;
import ML.audio.JSoundCapture;
import jfftpack.RealDoubleFFT;

import  Catalano.Math.Transforms.HilbertTransform;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
//VAD ZONE
import com.konovalov.vad.Vad;
import com.konovalov.vad.VadConfig;
import com.konovalov.vad.example.recorder.VoiceRecorder;
//VAD ZONE
import com.karlotoy.perfectune.instance.PerfectTune;


public class MainActivity extends AppCompatActivity {
    private boolean hasmic = false, isRecording, haswrite = false, hasread = false,hasshake=false,hasallfile=false;
    Button btn_toggle_draw, btn_audio_record, btn_audio_stop, btn_audio_play, btn_toggle_window, btn_play_frequency, btn_stop_frequency, btn_cal_sd, btn_llap_start, btn_llap_stop,btn_train,btn_generate,btn_crazy,btn_force_train;
    ImageView imageView;//最上面畫畫ㄉ
    ImageView imageView2;//最下面顯示路徑的
    TextView txt_out,texDistance_x,texDistance_y,absolute_disx,absolute_disy,predict_text;//中間顯示字ㄉ
    Bitmap bitmap;//最上面畫畫ㄉ
    Canvas canvas;//最上面畫畫ㄉ
    Paint paint;//最上面畫畫ㄉ
    Bitmap bitmap2;//最下面路徑
    Canvas canvas2;//最下面路徑
    Paint paint2;//最下面路徑
    EditText frequency_text;//命名撞到 我改了變數名
    private String tmpfile;
    private final static String TAG = "MyTag";
    boolean stroke_detected=false;
    boolean stroke_flag=false;
    boolean gravity_flag=false;//true=detected stroke
    boolean too_much_flag=false;//true = too much
    boolean triggered_flag=false;
    boolean protago_flag=false;
    boolean shake_triggered=false;
    boolean stabled_flag=true;
    int retrigger_flag=0;
    boolean firststart_flag=false;//看是否為剛開啟llap按鍵
    boolean firststroke_flag=true;//輸入一維距離實驗上下格的範圍
    int firststroke_cnt=0;
    int initstroke_cnt=0;
    int error_cnt=0;
    boolean initstroke_flag=true;
    int stroke_cnt=0;
    double upper_section=0.0;
    double lower_section=0.0;
    double left_section=0.0;
    double right_section=0.0;
    int[] left_up_section = new int[3];
    int[] left_down_section = new int[3];
    int[] right_up_section = new int[3];
    int[] right_down_section = new int[3];
    //short[] stroke_data_tmp = new short[48000/2];//samplerate/2
    short[][] stroke_data_tmp_2ch=new short[2][48000];//TODO CIRCLE TMP SIZE
    int stroke_data_ptr=0;
    int last_pointx=0;
    int last_pointy=0;
    double lastdisx=0.0;
    SensorManager sensorManager;
    Sensor sensor;
    Sensor sensor_gravity;
    HMM_VQ_Speech_Recognition hsr;
    private final Operations opr = new Operations();
    private JSoundCapture soundCapture = null;
    //llap zone





    private int frameSize = 512;
    int recBufSize = 0;
    double temperature = 20;
    int numfreq = 16;

    private AudioRecord llap_audioRecord;
    private double freqinter = 350;

    private double[] wavefreqs = new double[numfreq];

    private double[] wavelength = new double[numfreq];

    private double[] phasechange = new double[numfreq * 2];

    private double[] freqpower = new double[numfreq * 2];

    private double[] dischange = new double[2];

    private double[] idftdis = new double[2];

    private double startfreq = 15050;//17150

    private double soundspeed = 0;
    private boolean blnPlayRecord = false;
    int coscycle = 1920;

    private int sampleRateInHz = 48000;

    int cicdec = 16;
    int cicsec = 3;
    int cicdelay = cicdec * 17;

    private double[] baseband = new double[2 * numfreq * 2 * frameSize / cicdec];

    private double[] baseband_nodc = new double[2 * numfreq * 2 * frameSize / cicdec];

    private short[] dcvalue = new short[4 * numfreq];


    private int[] trace_x = new int[1000];
    private int[] trace_y = new int[1000];
    private int trace[][]=new int[1000][3];
    private double first_train[][]= new double[100][3];
    private double second_train[][]= new double[100][3];
    private double third_train[][]= new double[100][3];
    private double mean_train[][]= new double[100][3];
    private double stroke_training_set[][] = new double[100][100];
    private double stroke_training_result[][]=new double[9][];
    private int training_set_cnt=0; //偶數為x 奇數為y
    double tmp_sectionx=0.0;//用於section實驗的暫存器
    double tmp_sectiony=0.0;//用於section實驗的暫存器
    private double[] neighbor =new double[100];
    private int tracecount = 0;
    private int twodimsioncount = 0;
    private int playBufSize = 0;
    private boolean isCalibrated = false;
    private int now;
    private int lastcalibration;

    private double distrend = 0.05;

    private double micdis1 = 5;
    private double micdis2 = 115;
    private double dischangehist = 0;

    private double disx, disy;//note: x is upper mic y is lower mic on phone
    private double disy_cp;//複製disy
    private double tmpx=0,tmpy=0,dischangex=0,dischangey=0;//相對距離計算用
    private double lastx=0,lasty,changex=0,changey=0;//避免距離突然增加，計算與前次的相對距離

    private double displaydis = 0;
    boolean write_dis=true;
    boolean force_train=false;
    FileWriter fdiswriter0 = null;
    FileWriter fdiswriter1 = null;
    FileWriter fdiswriter2 = null;
    FileWriter fdiswriter3 = null;
    FileWriter fdiswriter4 = null;
    FileWriter fdiswriter5 = null;
    FileWriter fdiswriter6 = null;
    FileWriter fdiswriter7 = null;
    FileWriter fdiswriter8 = null;

    //llap zone

    int cnt = 0;
    double[] toTransform,toTransform_2ch,toTransform_shake_only,toTransform_2ch_shake_only;//用來放要拿去fftㄉdata
    double most_freq = 0.0;//fft出來最大ㄉ頻率
    //////
    int quite_avg = 90;//todo:暫時用強行設定 等開始寫預先訓練步驟時要求使用者安靜5秒來測定背景音量
    int stroke_power_max = 600;//todo:暫時用強行設定 之後寫預先訓練步驟時測定按鍵按下強度 用以壓制比按鍵大的聲音
    int stroke_power_min = 30;//todo:暫時用強行設定 之後寫預先訓練步驟時測定按鍵按下強度 用以偵測按鍵發生的最下限
    //todo:之後測試標準差以及變異數對於偵測的效用
    /////
    File file,file_org,file_org_2ch,file_2ch,file_baseband_txt,file_dis0,file_dis1,file_dis2,file_dis3,file_dis4,file_dis5,file_dis6,file_dis7,file_dis8;
    int dowindow=1;//用來開關window func 1是開 0是關
    int dodraw=0; //用來切換畫畫模式 0是spectrum 1是原data 2是i/q signal
    PerfectTune perfectTune = new PerfectTune();
    Path path = new Path();
    int drawcount=1;//確認是不是第一筆畫的，1是第一筆0不是
    int prevx=0;
    int prevy=0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //requestWindowFeature(Window.FEATURE_NO_TITLE);

        //setContentView(R.layout.activity_main);
        Log.i(TAG, "onCreate:123");
        setContentView(R.layout.activity_main);
        checkPermission();
        //按鈕的宣告
        btn_audio_record = findViewById(R.id.btn_audio_record);
        btn_audio_stop = findViewById(R.id.btn_audio_end);
        btn_audio_play = findViewById(R.id.btn_audio_play);
        btn_crazy= findViewById(R.id.btn_crazy);
        btn_toggle_window = findViewById(R.id.btn_toggle_window);
        btn_play_frequency = findViewById(R.id.btn_play_frequency);
        btn_stop_frequency = findViewById(R.id.btn_stop_frequency);
        btn_toggle_draw = findViewById(R.id.btn_toggle_draw);
        btn_cal_sd = findViewById(R.id.btn_cal_sd);
        btn_llap_start = findViewById(R.id.btn_llap_start);
        btn_llap_stop = findViewById(R.id.btn_llap_stop);
        btn_train=findViewById(R.id.btn_train);
        btn_generate=findViewById(R.id.btn_generate);
        btn_force_train=findViewById(R.id.btn_force_train);
        frequency_text = findViewById(R.id.frequency_num);
        txt_out = findViewById(R.id.txt_out);
        predict_text=findViewById(R.id.predict_text);
        texDistance_x=findViewById(R.id.text_disatnce_x);
        texDistance_y=findViewById(R.id.text_distance_y);
        absolute_disx=findViewById(R.id.text_absolute_disx);
        absolute_disy=findViewById(R.id.text_absolute_disy);
        btn_crazy.setOnClickListener(v -> onclick_crazy_button());
        btn_audio_record.setOnClickListener(v -> onclick_audio_start());
        btn_audio_stop.setOnClickListener(v -> onclick_audio_stop());
        btn_audio_play.setOnClickListener(v -> onclick_audio_play());
        btn_toggle_window.setOnClickListener(v -> toggle());
        btn_toggle_draw.setOnClickListener(v -> toggledarw());
        btn_play_frequency.setOnClickListener(v -> onlick_frequency_play());
        btn_stop_frequency.setOnClickListener(v -> onlick_frequency_stop());
        btn_cal_sd.setOnClickListener(v -> cal_sd());
        hsr= new HMM_VQ_Speech_Recognition();
        btn_generate.setOnClickListener(v -> onclick_generate());
        btn_train.setOnClickListener(v->onclick_train());
        //畫布大小 寬=變數1 高=變數2 最左上角是0 0 右下角是 (寬,高)
        bitmap = Bitmap.createBitmap((int) 4096, (int) 1400, Bitmap.Config.ARGB_8888);
        bitmap2 = Bitmap.createBitmap((int) 150, (int) 150, Bitmap.Config.ARGB_8888);
        canvas2= new Canvas(bitmap2);
        canvas = new Canvas(bitmap);

        paint = new Paint();
        paint2= new Paint();
        paint.setColor(Color.GREEN);
        paint2.setColor(Color.GREEN);
        imageView = (ImageView) this.findViewById(R.id.ImageView01);
        imageView2=(ImageView)this.findViewById(R.id.imageView02);
        imageView.setImageBitmap(bitmap);
        imageView2.setImageBitmap(bitmap2);
        //path.moveTo(100, 100);
        tmpfile = getExternalCacheDir().getAbsolutePath();
        Log.i(TAG, "pid of main thread= " + Thread.currentThread().getId());
        imageView.invalidate();
        perfectTune.setTuneFreq(15000);
        perfectTune.setTuneAmplitude(50000);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        sensor_gravity = sensorManager.getDefaultSensor(Sensor.TYPE_GRAVITY);
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        btn_toggle_window.setVisibility(View.INVISIBLE);
        btn_audio_play.setVisibility(View.INVISIBLE);
        btn_toggle_draw.setVisibility(View.INVISIBLE);
        txt_out.setVisibility(View.INVISIBLE);
        imageView.setVisibility(View.INVISIBLE);
        imageView2.setVisibility(View.INVISIBLE);
        texDistance_x.setVisibility(View.INVISIBLE);
        texDistance_y.setVisibility(View.INVISIBLE);
        HMM_VQ_Speech_Recognition HMM = new HMM_VQ_Speech_Recognition( );

        //String testc=testcc('c');
        //Log.i(TAG,testc);
        //llap zone
        soundspeed = 331.3 + 0.606 * temperature;


        for (int i = 0; i < numfreq; i++) {
            wavefreqs[i] = startfreq + i * freqinter;
            wavelength[i] = soundspeed / wavefreqs[i] * 1000;
        }


        //disx = 0;
        //disy = 250;
        now = 0;
        lastcalibration = 0;

        tracecount = 0;

        Log.i(TAG, "initialization start at time: " + System.currentTimeMillis());
        Log.i(TAG, initdownconvert(sampleRateInHz, numfreq, wavefreqs));
        Log.i(TAG, "" + wavefreqs[0]);
        Log.i(TAG, "initialization finished at time: " + System.currentTimeMillis());
        int maxvolume=0;
        if(write_dis)
        {
            try {//try to create txt
                file_dis0=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data0.txt");
                fdiswriter0=new FileWriter(file_dis0,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {//try to create txt
                file_dis1=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data1.txt");
                fdiswriter1=new FileWriter(file_dis1,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {//try to create txt
                file_dis2=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data2.txt");
                fdiswriter2=new FileWriter(file_dis2,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {//try to create txt
                file_dis3=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data3.txt");
                fdiswriter3=new FileWriter(file_dis3,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {//try to create txt
                file_dis4=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data4.txt");
                fdiswriter4=new FileWriter(file_dis4,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {//try to create txt
                file_dis5=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data5.txt");
                fdiswriter5=new FileWriter(file_dis5,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {//try to create txt
                file_dis6=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data6.txt");
                fdiswriter6=new FileWriter(file_dis6,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {//try to create txt
                file_dis7=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data7.txt");
                fdiswriter7=new FileWriter(file_dis7,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {//try to create txt
                file_dis8=new File(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data8.txt");
                fdiswriter8=new FileWriter(file_dis8,true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        btn_llap_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btn_llap_start.setEnabled(false);
                btn_llap_stop.setEnabled(true);
                btn_force_train.setEnabled(false);
                //AudioManager llap_audioManager = (AudioManager)getSystemService(Context.AUDIO_SERVICE);
                //llap_audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, llap_audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC), 0);
                recBufSize = AudioRecord.getMinBufferSize(sampleRateInHz,
                        AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

                Log.i(TAG, "recbuffersize:" + recBufSize);

                playBufSize = AudioTrack.getMinBufferSize(sampleRateInHz,
                        AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT);

                if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                    checkPermission();
                }
                llap_audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
                        sampleRateInHz, AudioFormat.CHANNEL_IN_STEREO, AudioFormat.ENCODING_PCM_16BIT, recBufSize);


                Log.i(TAG,"channels:" + llap_audioRecord.getChannelConfiguration());

                new ThreadInstantPlay().start();
                new ThreadInstantRecord().start();

            }

        });
        //
        btn_llap_stop.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                btn_llap_start.setEnabled(true);
                btn_llap_stop.setEnabled(false);
                btn_force_train.setEnabled(true);
                isCalibrated=false;
                blnPlayRecord=false;
                isCalibrated=false;
                firststroke_cnt=0;
                error_cnt=0;
                initstroke_flag=true;
                initstroke_cnt=0;
                firststroke_flag=true;
                upper_section=0.0;
                lower_section=0.0;
                left_section=0.0;
                right_section=0.0;
                firststart_flag=false;
                Arrays.fill(trace_x,0);
                Arrays.fill(trace_y,0);
                Arrays.fill(left_down_section,0);
                Arrays.fill(left_up_section,0);
                Arrays.fill(right_down_section,0);
                Arrays.fill(right_up_section,0);
                //Arrays.fill(section,0);
                //Arrays.fill(neighbor,0);
                canvas2.drawColor(Color.TRANSPARENT, PorterDuff.Mode.CLEAR);
                canvas2.drawColor(Color.WHITE);
                path.reset();
                drawcount=1;
                disx=micdis1;
                disy=micdis2;
            }
        });

        btn_force_train.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
               force_train=true;
               predict_text.setText("請按LLAP做Training");
            }
        });

        //llap zone

    }
    private void stroke_training(int position,int training_time,double dis1,double dis2){ //position為知道現在training第幾個按鍵 training_time為目前敲擊該按鍵第幾次
        Log.i("TAG","stroke dis1= "+dis1+" dis2= "+dis2);
        int real_position=position+1;
        /* 四宮格
        if(training_time==0){
            first_train[position][1]=dis1;
            first_train[position][2]=dis2;
            tmp_sectionx +=dis1;
            tmp_sectiony +=dis2;
            firststroke_cnt++;
            Log.i("TAG","請繼續敲擊第"+real_position+"按鍵");
        }
        else if(training_time==1){
            second_train[position][1]=dis1;
            second_train[position][2]=dis2;
            tmp_sectionx +=dis1;
            tmp_sectiony +=dis2;
            firststroke_cnt++;
            Log.i("TAG","請繼續敲擊第"+real_position+"按鍵");
        }
        else if(training_time==2){
            firststroke_cnt=0;
            initstroke_cnt++;
            tmp_sectionx +=dis1;
            tmp_sectiony +=dis2;
            mean_train[position][1] = tmp_sectionx/3;
            mean_train[position][2] = tmp_sectiony/3;
            third_train[position][1] = dis1;
            third_train[position][2] =dis2;
            tmp_sectionx=0;
            tmp_sectiony=0;
            real_position++;
            if(position==0 || position==1 ||position==3){
                disx=micdis1;
                disy=micdis2;
            }
            if(position==3){
                initstroke_flag=false;
                Log.i("TAG","訓練完畢，請敲擊任意按鍵");
                Log.i("TAG","stroke 按鍵1為 x= "+mean_train[0][1]+"y= "+mean_train[0][2]);
                Log.i("TAG","stroke 按鍵2為 x= "+mean_train[1][1]+"y= "+mean_train[1][2]);
                Log.i("TAG","stroke 按鍵3為 x= "+mean_train[2][1]+"y= "+mean_train[2][2]);
                Log.i("TAG","stroke 按鍵4為 x= "+mean_train[3][1]+"y= "+mean_train[3][2]);
            }
            else{
                Log.i("TAG","敲擊完畢，請換第"+real_position+"按鍵");
            }
        }

         */
        /*九宮格*/
        if(training_time<2){
            stroke_training_set[position][training_set_cnt]=dis1;
            training_set_cnt++;
            stroke_training_set[position][training_set_cnt]=dis2;
            training_set_cnt++;
            tmp_sectionx +=dis1;
            tmp_sectiony +=dis2;
            firststroke_cnt++;
            Log.i("TAG","請繼續敲擊第"+real_position+"按鍵");
            Message hint=new Message();
            hint.what=2;
            hint.obj=real_position;
            updateviews.sendMessage(hint);
            if(write_dis) {
                if (position == 0) {
                    try {
                        fdiswriter0.write(""+dis1+" "+dis2+"\n");
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 1) {
                    try {
                        fdiswriter1.write(""+dis1+" "+dis2+"\n");
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 2) {
                    try {
                        fdiswriter2.write(""+dis1+" "+dis2+"\n");
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 3) {
                    try {
                        fdiswriter3.write(""+dis1+" "+dis2+"\n");
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 4) {
                    try {
                        fdiswriter4.write(""+dis1+" "+dis2+"\n");
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 5) {
                    try {
                        fdiswriter5.write(""+dis1+" "+dis2+"\n");
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 6) {
                    try {
                        fdiswriter6.write(""+dis1+" "+dis2+"\n");
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 7) {
                    try {
                        fdiswriter7.write(""+dis1+" "+dis2+"\n");
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 8) {
                    try {
                        fdiswriter8.write(""+dis1+" "+dis2+"\n");
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }











        }
        else if(training_time==2){
            firststroke_cnt=0;
            initstroke_cnt++;
            tmp_sectionx +=dis1;
            tmp_sectiony +=dis2;
            mean_train[position][1] = tmp_sectionx/3;
            mean_train[position][2] = tmp_sectiony/3;
            stroke_training_set[position][training_set_cnt]=dis1;
            training_set_cnt++;
            stroke_training_set[position][training_set_cnt]=dis2;
            training_set_cnt++;
            tmp_sectionx=0;
            tmp_sectiony=0;
            training_set_cnt=0;
            real_position++;
            Message hint=new Message();
            hint.what=2;
            hint.obj=real_position;
            updateviews.sendMessage(hint);

            if(write_dis) {
                if (position == 0) {
                    try {
                        fdiswriter0.write(""+dis1+" "+dis2+"\n");
                        fdiswriter0.flush();
                        fdiswriter0.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 1) {
                    try {
                        fdiswriter1.write(""+dis1+" "+dis2+"\n");
                        fdiswriter1.flush();
                        fdiswriter1.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 2) {
                    try {
                        fdiswriter2.write(""+dis1+" "+dis2+"\n");
                        fdiswriter2.flush();
                        fdiswriter2.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 3) {
                    try {
                        fdiswriter3.write(""+dis1+" "+dis2+"\n");
                        fdiswriter3.flush();
                        fdiswriter3.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 4) {
                    try {
                        fdiswriter4.write(""+dis1+" "+dis2+"\n");
                        fdiswriter4.flush();
                        fdiswriter4.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 5) {
                    try {
                        fdiswriter5.write(""+dis1+" "+dis2+"\n");
                        fdiswriter5.flush();
                        fdiswriter5.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 6) {
                    try {
                        fdiswriter6.write(""+dis1+" "+dis2+"\n");
                        fdiswriter6.flush();
                        fdiswriter6.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 7) {
                    try {
                        fdiswriter7.write(""+dis1+" "+dis2+"\n");
                        fdiswriter7.flush();
                        fdiswriter7.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
                else if (position == 8) {
                    try {
                        fdiswriter8.write(""+dis1+" "+dis2+"\n");
                        fdiswriter8.flush();
                        fdiswriter8.close();
                    }catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                }
            }




            if(position==8){
                initstroke_flag=false;
                Log.i("TAG","訓練完畢，請敲擊任意按鍵");
                Log.i("TAG","stroke 按鍵1為 x= "+mean_train[0][1]+"y= "+mean_train[0][2]);
                Log.i("TAG","stroke 按鍵2為 x= "+mean_train[1][1]+"y= "+mean_train[1][2]);
                Log.i("TAG","stroke 按鍵3為 x= "+mean_train[2][1]+"y= "+mean_train[2][2]);
                Log.i("TAG","stroke 按鍵4為 x= "+mean_train[3][1]+"y= "+mean_train[3][2]);
                Log.i("TAG","stroke 按鍵5為 x= "+mean_train[4][1]+"y= "+mean_train[4][2]);
                Log.i("TAG","stroke 按鍵6為 x= "+mean_train[5][1]+"y= "+mean_train[5][2]);
                Log.i("TAG","stroke 按鍵7為 x= "+mean_train[6][1]+"y= "+mean_train[6][2]);
                Log.i("TAG","stroke 按鍵8為 x= "+mean_train[7][1]+"y= "+mean_train[7][2]);
                Log.i("TAG","stroke 按鍵9為 x= "+mean_train[8][1]+"y= "+mean_train[8][2]);
            }
            else{
                Log.i("TAG","敲擊完畢，請換第"+real_position+"按鍵");
            }
            if(position==1 || position==2 ||position==4|| position==5||position==7||position==8){
                disx=micdis1;
                disy=micdis2;
            }
        }

    }

    private double[] stroke_reading(double[] data,int position) throws IOException {

        BufferedReader bf=new BufferedReader(new FileReader(Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath()+"/dis_data"+position+".txt"));

        String textline;
        String str="";
        while ((textline=bf.readLine())!=null)
        {
            str+=" "+textline;
        }
        String[] num=str.split(" ");
        if(num.length<=1)
        {
            return  null;
        }
        data=new double[num.length-1];

        Log.i("stroke_test",""+num.length+" "+data.length);
        for(int i=0;i<data.length;i++)
        {
            data[i]=Double.parseDouble(num[i+1]);
            Log.i("stroke_test",num[i+1]);
        }

        return data;

    }
    long tmp_time1,tmp_time2;
    private void onclick_crazy_button() {
        disx=micdis1;
        disy=micdis2;
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return;
    }

    private void onclick_train() {
        hsr.train();
        return;
    }

    private void onclick_generate() {
        System.out.println("++++++generate+++++");
        hsr.generate();
        System.out.println("++++++generate succeed+++++");
        return;
    }

    private void onlick_frequency_stop() {
        perfectTune.stopTune();
    }

    private void onlick_frequency_play() {
        String num = frequency_text.getText().toString();
        if(TextUtils.isEmpty(num)){
            num= "15000";
        }
        perfectTune.setTuneFreq(Integer.valueOf(num));
        perfectTune.playTune();//stops the tune
    }

    /**
     * 確認是否有麥克風使用權限
     */
    private void checkPermission() {
        hasmic = ActivityCompat.checkSelfPermission(this
                , Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED;
        haswrite = ActivityCompat.checkSelfPermission(this
                , Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        hasread = ActivityCompat.checkSelfPermission(this
                , Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
        hasshake=ActivityCompat.checkSelfPermission(this
                , Manifest.permission.HIGH_SAMPLING_RATE_SENSORS) == PackageManager.PERMISSION_GRANTED;
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R)
        {
            if(Environment.isExternalStorageManager())
            {
                hasallfile=true;
                Log.i("mytag","hasallfile");
            }else
            {
                hasallfile=false;
                Log.i("mytag","nohasallfile");
            }
        }else
        {
            hasallfile=true;
        }
        //Log.i("per",""+hasshake);
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.R&&hasallfile==false)
        {
            //Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
            //intent.setData(Uri.parse("package:"+getPackageName()));
            //startActivity(intent);
            //this.startActivityForResult(intent,1024);
            try {
                Uri uri = Uri.parse("package:" + BuildConfig.APPLICATION_ID);
                Intent intent = new Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION, uri);
                startActivity(intent);
            } catch (Exception ex){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                startActivity(intent);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!hasmic) {
                this.requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1);
            }
            if (!haswrite) {
                this.requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 2);
            }
            if (!hasread) {
                this.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 3);
            }
            if (!hasshake) {
                this.requestPermissions(new String[]{Manifest.permission.HIGH_SAMPLING_RATE_SENSORS}, 3);
            }
        }


    }

    /**取得權限回傳*/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasmic = true;
            Log.i(TAG, "get mic");
        }
        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            haswrite = true;
            Log.i(TAG, "get write");
        }
        if (requestCode == 3 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            hasread = true;
            Log.i(TAG, "get read");
        }
        if (!(grantResults.length > 0)) {
            Log.i(TAG, "request error");
        }
    }


    //這邊是用來更新ui的內容 因位子thread不能更新主ui
    @SuppressLint("HandlerLeak")
    private Handler handlerMeasure = new Handler() {
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what) {
                case 1://不再使用
                    /*
                    int amp = recorder.getMaxAmplitude();
                    //公式：Gdb = 20log10(V1/V0)
                    double db = 20 * (Math.log10(Math.abs(amp)));
                    cnt++;
                    //media_total+=amp;
                    //media_avg2=media_total/cnt;
                    if(cnt==1) media_avg=amp;
                    else{
                        //media_avg=(media_avg*(cnt-1)+amp)/cnt;
                        media_avg+=(amp-media_avg)/cnt;
                    }
                    if((double)amp>1.25*(double)media_avg) media_strokes++;

                    //if -Infinity
                    if (Math.round(db) == -9223372036854775808.0) txt_out.setText("0 db");
                    else txt_out.setText(amp + "amp, avg=" +media_avg+"strokes"+media_strokes);
                    //else txt_out.setText(Math.round(db) + " db, " + cnt);
                    //Log.i(TAG, "handler thread id = " +

                            //Thread.currentThread().getId());


                     */
                    Log.i(TAG,"THIS PART IS NO LONGER USED");
                    break;
                case 2:
                    //for drawing;
                    double tmp[]= (double[]) msg.obj;
                    canvas.drawColor(Color.BLACK);
                    for (int i = 0; i < tmp.length; i++) {
                        int x = i;

                        int downy = (int) (1400 - (tmp[i] * 100));
                        int upy = 1400;

                        canvas.drawLine(x, downy, x, upy, paint);
                    }
                    imageView.invalidate();

                    break;
                case 3:
                    short tmp3[]=(short[]) msg.obj;
                    canvas.drawColor(Color.BLACK);

                    for(int i=0;i<tmp3.length;i++)
                    {
                        canvas.drawCircle(i,700-tmp3[i]/10,6,paint);//用畫多個圓的方式得到更好效能
                        //canvas.drawLine(i, 500-(int)tmp3[i]/10, i-1,500- (int)tmp3[i-1]/10, paint);

                    }
                    imageView.invalidate();

                    break;

                case 4:
                    ComplexNumber tmp4[]=(ComplexNumber[]) msg.obj;
                    canvas.drawColor(Color.BLACK);
                    for(int i=0;i<tmp4.length;i++)
                    {
                        //i is green q is red
                        paint.setColor(Color.GREEN);
                        canvas.drawCircle(i,700-(float)tmp4[i].real/10,6,paint);
                        paint.setColor(Color.RED);
                        canvas.drawCircle(i,700-(float)tmp4[i].imaginary/20,6,paint);

                    }
                    imageView.invalidate();
                    break;

                case 5:
                    double tmp5[]=(double[]) msg.obj;
                    canvas.drawColor(Color.BLACK);
                    //Log.i(TAG,"tmplen="+tmp5.length);
                    for(int i=0;i<tmp5.length;i++)
                    {
                        short tmpp=(short)(Math.round(tmp5[i]));
                        canvas.drawCircle(i,700-tmpp/2,6,paint);//用畫多個圓的方式得到更好效能
                        //canvas.drawLine(i, 500-(int)tmp3[i]/10, i-1,500- (int)tmp3[i-1]/10, paint);

                    }
                    imageView.invalidate();

                    break;
                case 6:
                    double tmp6[]=(double[]) msg.obj;
                    canvas.drawColor(Color.BLACK);
                    Log.i(TAG,"draw shake");
                    for(int i=0;i<tmp6.length;i++)
                    {
                        short tmpp=(short)(Math.ceil(tmp6[i]));
                        //canvas.drawCircle(i,500-tmpp/2,6,paint);//用畫多個圓的方式得到更好效能
                        for(int x=i*40;x<(i+1)*40;x++) canvas.drawLine(x,1400-(int)tmp6[i],x,1400, paint);

                    }
                    imageView.invalidate();

                    break;

                case 7://baseband
                    double[] tmp7 =(double[]) msg.obj;
                    canvas.drawColor(Color.BLACK);

                    for(int i=0;i<4095;i++)
                    {
                        //canvas.drawCircle(i, Math.round(500-tmp7[i/2]),6,paint);
                        Log.i("basedata","i= "+i+" data= "+tmp7[i/2]);
                        canvas.drawLine(i,700-Math.round(tmp7[i/2]*4),i+1,700-Math.round(tmp7[(i+1)/2]*4), paint);
                    }
                    imageView.invalidate();
                    //Log.i("draw_baseband",""+baseband[1024]);
                    break;

                case 8://baseband nodc
                    double[] tmp8 =(double[]) msg.obj;
                    canvas.drawColor(Color.BLACK);
                    for(int i=0;i<4096;i++)
                    {
                        canvas.drawCircle(i, Math.round(700-tmp8[i/2]*5),4,paint);
                    }
                    imageView.invalidate();
                    //Log.i("draw_baseband",""+baseband[1024]);
                    break;



            }
            super.handleMessage(msg);

        }
    };

    short[][] spiltChannel(short [] samples, int numChannels)
    {

        int numFrames  = samples.length / numChannels;

        short[][] result = new short[numChannels][];
        for (int ch = 0 ; ch < numChannels ; ch++)
        {
            result[ch] = new short[numFrames];
            for (int i = 0 ; i < numFrames ; i++)
            {
                result[ch][i] = samples[numChannels*i+ch];
            }
        }
        return result;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void StartAudioRecord() {//錄音的函式 還有處理資料也在這裡
        Log.i(TAG, "開始錄音");
        ////////////////////////////
        //此處都是關於按鍵聲偵測的變數 但是此處會有overflow的問題且準度不精確
        double audio_avg_global=0;
        int audio_cnt_global=0;
        double pos_avg_global=0;
        double neg_avg_global=0;
        int pos_total_global=0;
        int pos_cnt_global=0;
        stroke_cnt=0;
        protago_flag=false;
        int stroke_state=0;
        long last_stroke_time=0,current_time=0;

        int train_flag=0;//0 for starter 1 for grant access
        long start_record_time=System.currentTimeMillis();
        ////////////////////////////
        //for fft
        RealDoubleFFT transformer;


//採集率
        int frequency = 48000;
//格式
        int channelConfiguration = AudioFormat.CHANNEL_IN_STEREO;
//16Bit
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
//生成PCM檔案
        file = new File( getExternalCacheDir().getAbsolutePath()+"/audio_fft.pcm");
        file_2ch=new File( getExternalCacheDir().getAbsolutePath()+"/audio_fft_2ch.pcm");
        file_org = new File( getExternalCacheDir().getAbsolutePath()+"/audio_org.pcm");
        file_org_2ch=new File( getExternalCacheDir().getAbsolutePath()+"/audio_org_2ch.pcm");
        

        Log.i(TAG, "生成檔案"+file.getAbsolutePath());
//如果存在，就先刪除再建立
        if(!file.exists()){
            Log.i(TAG,"檔案不存在");
        }
        if (file.exists()) {
            file.delete();
            Log.i(TAG, "刪除檔案");

            try {
                file.createNewFile();
                Log.i(TAG, "建立檔案");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "未能建立");
                //throw new IllegalStateException("未能建立"+file.toString());
            }
        }
        if(!file_org.exists()){
            Log.i(TAG,"檔案不存在");
        }
        if (file_org.exists()) {
            file_org.delete();
            Log.i(TAG, "刪除檔案");

            try {
                file_org.createNewFile();
                Log.i(TAG, "建立檔案");
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "未能建立");
                //throw new IllegalStateException("未能建立"+file.toString());
            }
        }
        try {//輸出流
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            OutputStream os_2ch = new FileOutputStream(file_2ch);
            BufferedOutputStream bos_2ch = new BufferedOutputStream(os_2ch);
            DataOutputStream dos_2ch = new DataOutputStream(bos_2ch);
            OutputStream os_org = new FileOutputStream(file_org);
            BufferedOutputStream bos_org = new BufferedOutputStream(os_org);
            DataOutputStream dos_org = new DataOutputStream(bos_org);
            OutputStream os_org_2ch = new FileOutputStream(file_org_2ch);
            BufferedOutputStream bos_org_2ch = new BufferedOutputStream(os_org_2ch);
            DataOutputStream dos_org_2ch = new DataOutputStream(bos_org_2ch);

            

            int NOTbufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);//這會得到最小所需buffersize 但要用2的次方大小
            Log.i(TAG, "recbuffersize:" + NOTbufferSize);
            int bufferSize=4096;// set as power of 2 for fft 這是每個迴圈取的資料點 每個迴圈約等於0.09秒
            int bufferSize2channel=4096*2;//for 2 channel
            int fftSize=bufferSize*2;//丟進去做fft的大小 是buffer的兩倍 為了讓fft中的運作與buffer對齊
            transformer = new RealDoubleFFT(bufferSize*2);//!

            //這邊是必要的權限檢查 不能刪 不然java不讓過
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize2channel);
            short[] buffer = new short[bufferSize];//用來儲存原始音訊資料
            short[] buffer_second = new short[bufferSize];
            short[] buffer2channel = new short[bufferSize2channel];
            short[][] buffer_spilt;
            toTransform = new double[bufferSize*2];
            toTransform_2ch = new double[bufferSize*2];//用來儲存要放進fft的資料 且用來機器學習判斷
            toTransform_shake_only = new double[bufferSize*2];
            toTransform_2ch_shake_only = new double[bufferSize*2];//用來儲存要放進fft的資料 且用來機器學習判斷
            //to calculate data after fft
            double[] re;
            double[] im;
            double[] magnitude;
            ComplexNumber[] complexBuffer = new ComplexNumber[bufferSize];
            for (int i = 0; i < bufferSize; i++)//init of complex data array
                complexBuffer[i] = new ComplexNumber(0, 0);
            audioRecord.startRecording();
            Log.i(TAG, "開始錄音");
            isRecording = true;
            int looptimes=0;
            while (isRecording) {
                int audio_total=0;
                looptimes++;
                int bufferReadResult = audioRecord.read(buffer2channel, 0, bufferSize2channel);
                buffer_spilt=spiltChannel(buffer2channel,2);
                System.arraycopy(buffer_spilt[0],0,buffer,0,buffer_spilt[0].length);
                System.arraycopy(buffer_spilt[1],0,buffer_second,0,buffer_spilt[1].length);
               // Log.i(TAG, "READRESULT="+bufferReadResult);
                ////////////////////////////
                //此處都是關於按鍵聲偵測的變數 此處的變數都只用在一次迴圈中資料的統計 而不是從程式開始執行到現在的統計

                int localmin=0x3f3f3f3f;
                int localmax=-0x3f3f3f3f;
                int localzeros=0;
                int zerocross=0;
                int ispos=0;
                int lastpos=0;
                double audio_avg_local=0;
                int audio_cnt_local=0;
                int pos_total_local=0;
                int pos_cnt_local=0;
                int neg_total_local=0;
                double pos_avg_local=0;
                double neg_avg_local=0;
                //////////////////////////

                //zero out data of previous loop and init
                for(int i=0;i<bufferSize*2;i++)
                {
                    toTransform[i]=0;
                    toTransform_2ch[i]=0;
                    toTransform_shake_only[i]=0;
                    toTransform_2ch_shake_only[i]=0;
                }
                //read data from mic and predict strokes
                for (int i = 0; i < bufferSize; i++  ) {
                    //double db = 20 * (Math.log10(Math.abs(buffer[i])));
                    /* //this original stroke detect
                    lastpos=ispos;
                    if(buffer[i]>0) {ispos=1;pos_total_local+=buffer[i];pos_cnt_local++;pos_cnt_global++;pos_total_global+=buffer[i];}
                    if(buffer[i]==0) ispos=ispos;
                    if(buffer[i]<0) {ispos=-1;neg_total_local+=buffer[i];}

                    if(lastpos!=ispos) zerocross++;

                    audio_cnt_global++;
                    audio_cnt_local++;
                    audio_avg_global+=(buffer[i]-audio_avg_global)/audio_cnt_global;
                    audio_avg_local+=(buffer[i]-audio_avg_local)/audio_cnt_local;
                    audio_total+=buffer[i];
                    if(buffer[i]==0) localzeros++;
                    if(localmax<buffer[i]) localmax=buffer[i];
                    if(localmin>buffer[i]) localmin=buffer[i];
*/

                    /*
                    put data into fft array
                     */
                    toTransform[i] = (double) buffer[i] / 32768.0;
                    toTransform_2ch[i] = (double) buffer_second[i] / 32768.0;
                    //put data into complex array
                    complexBuffer[i].real=buffer[i];
                    complexBuffer[i].imaginary=0;



                    //write original data into pcm2
                    dos_org.writeShort(buffer[i]);

                };
                for(int i=0;i<bufferSize2channel;i++)
                {
                    dos_org_2ch.writeShort(buffer2channel[i]);
                }
                /*
                pos_avg_local=(double)pos_total_local/(double)pos_cnt_local;
                pos_avg_global=(double)pos_total_global/(double)pos_cnt_global;
                neg_avg_local=(double)neg_total_local/(double)audio_cnt_local;
                */

                /*
                apply window func before fft
                */
                if(dowindow>0) {
                    double window[] = new double[bufferSize * 2];
                    window = hanning(bufferSize * 2);//產生適當的window
                    toTransform = applyWindowFunc(toTransform, window);
                    toTransform_2ch = applyWindowFunc(toTransform_2ch, window);
                }
                /*
                do fft
                 */
                transformer.ft(toTransform);
                transformer.ft(toTransform_2ch);
                System.arraycopy(toTransform,0,toTransform_shake_only,0,toTransform.length);
                System.arraycopy(toTransform_2ch,0,toTransform_2ch_shake_only,0,toTransform_2ch.length);

                //do hilbert transform
                HilbertTransform.FHT(complexBuffer, FourierTransform.Direction.Forward);
                int itotal=0;
                int qtotal=0;
                for(int i=0;i<complexBuffer.length;i++)
                {

                    itotal+=Math.abs(complexBuffer[i].real);
                    qtotal+=Math.abs(complexBuffer[i].imaginary);
                }


                //after hilbert transform the real part is i signal and imaginary part is q signal

                /*
                do cut off frequency cut off frequency lower than lower and bigger than upper
                 */
                //todo:這邊要做調整截斷頻率的東西
                int lower=800;
                int upper=15000;
                toTransform=to_transform_cut_frequency(toTransform,lower,upper,frequency,fftSize);
                toTransform_2ch=to_transform_cut_frequency(toTransform_2ch,lower,upper,frequency,fftSize);
                toTransform_shake_only=to_transform_cut_frequency(toTransform_shake_only,lower,23800,frequency,fftSize);
                toTransform_2ch_shake_only=to_transform_cut_frequency(toTransform_2ch_shake_only,lower,23800,frequency,fftSize);
                /*

                 */
                double[] spectrum;
                //to calculate spectrum
                if (toTransform_shake_only.length % 2 != 0) {// if odd
                    spectrum = new double[(toTransform_shake_only.length + 1) / 2];
                    re=new double[(toTransform_shake_only.length + 1) / 2];
                    im=new double[(toTransform_shake_only.length + 1) / 2];
                    magnitude=new double[(toTransform_shake_only.length + 1) / 2];
                    re[0]=toTransform_shake_only[0];//real part of first complex fft coeffient is x[0]
                    im[0]=0;
                    magnitude[0]=re[0]*re[0];
                    spectrum[0] = Math.pow(toTransform_shake_only[0] * toTransform_shake_only[0], 0.5);// dc component
                    for (int index = 1; index < toTransform_shake_only.length; index = index + 2) {//i=1 3 5...
                        // magnitude =re*re + im*im
                        double mag = toTransform_shake_only[index] * toTransform_shake_only[index]
                                + toTransform_shake_only[index + 1] * toTransform_shake_only[index + 1];
                        re[(index + 1) / 2]=toTransform_shake_only[index];//when index=1 3 5... i=1 2 3..
                        im[(index+1)/2]=toTransform_shake_only[index+1];//index=2 4 6...i=1 2 3..
                        magnitude[(index+1)/2]=Math.sqrt(mag);
                        spectrum[(index + 1) / 2] = Math.pow(mag, 0.5);
                    }
                } else {// if even
                    spectrum = new double[toTransform_shake_only.length / 2 + 1];
                    re=new double[toTransform_shake_only.length / 2 + 1];
                    im=new double[toTransform_shake_only.length / 2 + 1];
                    magnitude=new double[toTransform_shake_only.length / 2 + 1];
                    re[0]=toTransform_shake_only[0];//real part of first complex fft coeffient is x[0]
                    im[0]=0;
                    magnitude[0]=re[0]*re[0];
                    spectrum[0] = Math.pow(toTransform_shake_only[0] * toTransform_shake_only[0], 0.5);// dc component real only
                    for (int index = 1; index < toTransform_shake_only.length - 1; index = index + 2) {//index=1 3 5.. i=1 2 3...
                        // magnitude =re*re + im*im
                        double mag = toTransform_shake_only[index] * toTransform_shake_only[index]
                                + toTransform_shake_only[index + 1] * toTransform_shake_only[index + 1];
                        re[(index + 1) / 2]=toTransform_shake_only[index];//index=1 3 5..i=1 2 3
                        im[(index+1)/2]=toTransform_shake_only[index+1];//index=2 4 6..i=1 2 3
                        magnitude[(index+1)/2]=Math.sqrt(mag);
                        spectrum[(index + 1) / 2] = Math.pow(mag, 0.5);
                    }
                    spectrum[spectrum.length - 1] = Math.pow(toTransform_shake_only[toTransform_shake_only.length - 1]
                            * toTransform_shake_only[toTransform_shake_only.length - 1], 0.5);
                    re[re.length - 1]=toTransform_shake_only[toTransform_shake_only.length - 1];
                    im[im.length-1]=0;
                    magnitude[magnitude.length-1]=Math.sqrt(re[re.length - 1]*re[re.length - 1]);
                }
                //after fft the real part is stored in index 1,3,5....(n/2)-1
                // Calculate the Real and imaginary and Magnitude.

                //do inverse fft
                transformer.bt(toTransform);
                transformer.bt(toTransform_2ch);
                transformer.bt(toTransform_shake_only);
                transformer.bt(toTransform_2ch_shake_only);

                int tmp_stat=0;


                /*
                trying to write transformed data into pcm file 音訊檔的內容是經過fft和反fft轉換的 會損壞音質
                 */
                for(int i=0;i<bufferSize;i++)
                {
                    short tmp=(short)Math.round(toTransform[i]);
                    short tmp_2ch=(short)Math.round(toTransform_2ch[i]);

                    dos.writeShort(tmp*8);
                    dos_2ch.writeShort(tmp*8);
                    dos_2ch.writeShort(tmp_2ch*8);
                    //stroke_data_tmp[stroke_data_ptr]=tmp;
                    stroke_data_tmp_2ch[0][stroke_data_ptr]= (short) (tmp*8);
                    stroke_data_tmp_2ch[1][stroke_data_ptr]= (short) (tmp_2ch*8);
                    stroke_data_ptr++;
                    stroke_data_ptr%=48000;
                    //TODO CIRCLE TMP PTR
                }

                current_time=System.currentTimeMillis();
                if(current_time-start_record_time>=2000)protago_flag = true;
                if(stroke_detected&&train_flag==0)
                {
                    train_flag=1;
                }
                else if(train_flag==1)
                {
                    if(current_time-last_stroke_time>=300)//TODO CIRCLE TMP TIME
                    {

                        train_flag=0;
                        //store data finished onto next one
                        store_stroke_file(stroke_data_tmp_2ch,stroke_data_ptr);
                        //todo test file recognize

                        File test_stroke_file = new File(getExternalCacheDir().getAbsolutePath() + "/stroke_tmp_2ch.wav");
                        System.out.println("mytag"+test_stroke_file.getPath());
                        String test_result = opr.hmmGetWordFromFile(test_stroke_file);
                        if(test_result==null) {
                            Log.i("TAG", "continue");
                            continue;
                        }
                        //String test_label_path = Environment.getExternalStoragePublicDirectory(DIRECTORY_MUSIC).getPath() + "//SoundKeyboard_TrainWav//4" +"/cache2022-10-08T151829.394audio_fft.wav";
                        //File test_label = new File(test_label_path);
                        //String test_result = opr.hmmGetWordFromFile(test_label);
                        System.out.println("test_result = "+ test_result);
                        //txt_out.setText(test_result);
                        //Log.i("test_result",test_result);
                    }
                    else
                    {
                        //keep storing data
                    }
                }



                // detect stroke from inverse fft data
                for(int i=0;i<bufferSize;i++)
                {
                    lastpos=ispos;
                    if(toTransform_shake_only[i]>0) {ispos=1;pos_total_local+=toTransform_shake_only[i];pos_cnt_local++;pos_cnt_global++;pos_total_global+=toTransform_shake_only[i];}
                    if(toTransform_shake_only[i]==0) ispos=ispos;
                    if(toTransform_shake_only[i]<0) {ispos=-1;neg_total_local+=toTransform_shake_only[i];}

                    if(lastpos!=ispos) zerocross++;

                    audio_cnt_global++;
                    audio_cnt_local++;
                    audio_avg_global+=(toTransform_shake_only[i]-audio_avg_global)/audio_cnt_global;
                    audio_avg_local+=(toTransform_shake_only[i]-audio_avg_local)/audio_cnt_local;
                    audio_total+=toTransform_shake_only[i];
                    if(toTransform_shake_only[i]==0) localzeros++;
                    if(localmax<toTransform_shake_only[i]) localmax=(int)toTransform_shake_only[i];
                    if(localmin>toTransform_shake_only[i]) localmin=(int)toTransform_shake_only[i];

                }
                pos_avg_local=(double)pos_total_local/(double)pos_cnt_local;
                pos_avg_global=(double)pos_total_global/(double)pos_cnt_global;
                neg_avg_local=(double)neg_total_local/(double)audio_cnt_local;
                Log.i(TAG,"lmax="+localmax);

                //end of do inverse fft
                double peak = -1.0;
                int peak_location=-1;
                int zero_location=-1;
                int zero_flag=1;
                int len=magnitude.length;
                double spectrum_average=0;
                double spectrum_total=0;
                int data_nums=1024;
                double spectrum_sd=0;
                // Get the largest magnitude peak
                for(int i = 0; i <data_nums; i++){
                    if(peak < Math.abs(spectrum[i])) {
                        peak = Math.abs(spectrum[i]);
                        peak_location = i;
                    }
                    spectrum_total+=spectrum[i];
                }
                spectrum_average=spectrum_total/data_nums;
                for(int i = 0; i <data_nums; i++)
                {
                    spectrum_sd+=Math.abs(spectrum[i]-spectrum_average);
                }
                spectrum_sd/=data_nums;
                Log.i("MyTag","sd=" +spectrum_sd);

                /*
                if(peak_location<=32&&peak_location>=10&&peak>=17&&spectrum_total>=550)
                {
                    Log.i("MyTag","test stroke detect!");
                }
*/


                most_freq = (double)((double)frequency * (double)peak_location)/(double)(bufferSize*2);
                Log.i(TAG,"Most freq="+most_freq);

                //three state counter if recently detected block for a moment to prevent error

                //Log.i("temp test",""+pos_avg_local +"  "+gravity_flag+" "+"  "+triggered_flag );

                if(stroke_state==0)
                {

                    if(pos_avg_local>=stroke_power_min&&pos_avg_local<stroke_power_max&&stroke_state>=0&&triggered_flag==false&&gravity_flag==true&&too_much_flag==false)//push前要加回來&&shake_triggered==false&&retrigger_flag==0
                    {

                        stroke_cnt++;
                        stroke_detected=true;
                        triggered_flag=true;
                        shake_triggered=true;
                        last_stroke_time=System.currentTimeMillis();
                        retrigger_flag=1;
                       // stabled_flag=false;
                        Log.i(TAG+"stroke detected","strokes= " +stroke_cnt+"looptimes="+looptimes);

                        Log.i(TAG,"peak location"+peak_location);
                        stroke_state-=2;
                    }
                    else if(pos_avg_local>=stroke_power_min&&pos_avg_local<stroke_power_max&&stroke_state>=0&&gravity_flag==true&&too_much_flag==false&&triggered_flag==false&&retrigger_flag!=0) {stroke_detected=false; Log.i(TAG,"detect but not retrigger");}
                    else if(pos_avg_local>=stroke_power_min&&pos_avg_local<stroke_power_max&&stroke_state>=0&&gravity_flag==true&&too_much_flag==false&&triggered_flag==false&&shake_triggered==true)
                    {
                        stroke_detected=false;
                        Log.i(TAG,"fake stroke detected shake triggered already!");
                    }
                    else if(pos_avg_local>=stroke_power_min&&pos_avg_local<stroke_power_max&&stroke_state>=0&&gravity_flag==false&&too_much_flag==false&&triggered_flag==false&&shake_triggered==false)
                    {
                        stroke_detected=false;
                       // Log.i(TAG,"fake stroke detected no shake!!");
                    }
                    else if(pos_avg_local<=stroke_power_min&&gravity_flag==true &&triggered_flag==false)
                    {
                        stroke_detected=false;
                        //Log.i(TAG,"fake stroke detected too less sound"+pos_avg_local);

                    }
                    else if(pos_avg_local>stroke_power_max&&gravity_flag==true)
                    {
                        stroke_detected=false;
                        Log.i(TAG,"fake stroke detected too much sound"+pos_avg_local);
                    }
                    else
                    {
                        stroke_detected=false;
                        //Log.i(TAG,"what happened?"+pos_avg_local+"  "+gravity_flag);
                    }
                }
                else if(stroke_state==-1)
                {
                    stroke_state=0;
                }
                else if(stroke_state==-2)
                {
                    stroke_detected=false;
                    if(pos_avg_local<stroke_power_min)
                    {
                        stroke_state=0;
                    }
                    else stroke_state++;
                }




                //to draw using handler by sending msg
                Message msg = handlerMeasure.obtainMessage();
                if(dodraw==0)
                {//draw spectrum
                    msg.what = 2;
                    msg.obj = spectrum;
                    handlerMeasure.sendMessage(msg);
                }else if(dodraw==1)//draw input data
                {
                    msg.what = 3;

                    msg.obj = buffer;

                    handlerMeasure.sendMessage(msg);


                }else if(dodraw==2)//draw i/q signal
                {
                    msg.what = 4;
                    msg.obj = complexBuffer;
                    handlerMeasure.sendMessage(msg);
                }
                else if(dodraw==3)//draw inv fft signal
                {
                    msg.what = 5;
                    msg.obj = toTransform;
                    handlerMeasure.sendMessage(msg);
                }

                /*
                if(pos_avg_local>=stroke_power_min&&pos_avg_local<stroke_power_max&&stroke_state>=0)
                {
                    stroke_cnt++;
                    Log.i(TAG+"stroke detected","strokes= " +stroke_cnt+"looptimes="+looptimes);
                    stroke_state-=2;
                }
                else
                {
                    detected_already=false;
                }


                //Log.i(TAG,"looptimes="+looptimes+" max= "+localmax+"min= "+localmin);
                //Log.i(TAG,"looptimes"+looptimes+" loczlzeros="+localzeros+" zerocross= "+zerocross);
                //Log.i(TAG,"looptimes="+looptimes+"pos avg local="+pos_avg_local);
                */

            }
            audioRecord.stop();
            String currentTime = LocalDateTime.now().toString();
            boolean train_fft = false;
            if(train_fft){

                File des = new File(getExternalCacheDir()+currentTime+file.getName());
                copyFileUsingStream(file,des);
            }
            boolean train_fft_2ch = false;
            if(train_fft_2ch){

                File des = new File(getExternalCacheDir()+currentTime+file_2ch.getName());
                copyFileUsingStream(file_2ch,des);
            }
            boolean train_unfft = false;
            if(train_unfft){

                File des = new File(getExternalCacheDir()+currentTime+file_org.getName());
                copyFileUsingStream(file_org,des);
            }
            boolean train_unfft_2ch = false;
            if(train_unfft_2ch){

                File des = new File(getExternalCacheDir()+currentTime+file_org_2ch.getName());
                copyFileUsingStream(file_org_2ch,des);
            }
            boolean train_fft_towav_v1 = false;
            if(train_fft_towav_v1)
            {

                String path=getExternalCacheDir()+"/"+file.getName();
                String outpath = path.replace(".pcm", ".wav");
                File wavfile = new File(outpath);
                PCMToWAV(file,wavfile,1,48000,16);
                File des = new File(getExternalCacheDir()+currentTime+wavfile.getName());
                copyFileUsingStream(wavfile,des);

            }
            boolean train_unfft_towav_v1 = false;
            if(train_unfft_towav_v1)
            {

                String path=getExternalCacheDir()+"/"+file_org.getName();
                String outpath = path.replace(".pcm", ".wav");
                File wavfile = new File(outpath);
                PCMToWAV(file_org,wavfile,1,48000,16);
                File des = new File(getExternalCacheDir()+currentTime+wavfile.getName());
                copyFileUsingStream(wavfile,des);

            }
            boolean train_fft_towav_v2 = true;
            if(train_fft_towav_v2)
            {
                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil( 48000, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
                String path=getExternalCacheDir()+"/"+file.getName();
                String outpath = path.replace(".pcm", ".wav");
                pcmToWavUtil.pcmToWav(path, outpath);
                File souce = new File(outpath);
                File des = new File(getExternalCacheDir()+currentTime+"audio_fft.wav");
                copyFileUsingStream(souce,des);
            }
            boolean train_fft_towav_v2_2ch = true;
            if(train_fft_towav_v2_2ch)
            {
                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(48000, AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT);
                String path=getExternalCacheDir()+"/"+file_2ch.getName();
                String outpath = path.replace(".pcm", ".wav");
                pcmToWavUtil.pcmToWav(path, outpath);
                File souce = new File(outpath);
                File des = new File(getExternalCacheDir()+currentTime+"audio_fft_2ch.wav");
                copyFileUsingStream(souce,des);
            }
            boolean train_unfft_towav_v2 = false;
            if(train_unfft_towav_v2)
            {
                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil();
                String path=getExternalCacheDir()+"/"+file_org.getName();
                String outpath = path.replace(".pcm", ".wav");
                pcmToWavUtil.pcmToWav(path, outpath);
                File souce = new File(outpath);
                File des = new File(getExternalCacheDir()+currentTime+"audio_org.wav");
                copyFileUsingStream(souce,des);


            }
            boolean train_unfft_towav_v2_2ch = false;
            if(train_unfft_towav_v2_2ch)
            {
                PcmToWavUtil pcmToWavUtil = new PcmToWavUtil();
                String path=getExternalCacheDir()+"/"+file_org_2ch.getName();
                String outpath = path.replace(".pcm", ".wav");
                pcmToWavUtil.pcmToWav(path, outpath);
                File souce = new File(outpath);
                File des = new File(getExternalCacheDir()+currentTime+"audio_org_2ch.wav");
                copyFileUsingStream(souce,des);


            }

            dos.close();
            dos_2ch.close();
            dos_org.close();
            dos_org_2ch.close();
            
        } catch (Exception t) {
            Log.e(TAG, "錄音失敗"+t.getMessage());
            t.printStackTrace();

        }
    }

    private void store_stroke_file(short[][] stroke_data_tmp_2ch, int stroke_data_ptr) throws IOException
    {
        try {


            File f = new File(getExternalCacheDir().getAbsolutePath() + "/stroke_tmp.pcm");
            File f2ch = new File(getExternalCacheDir().getAbsolutePath() + "/stroke_tmp_2ch.pcm");
            OutputStream os = new FileOutputStream(f);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            OutputStream os_2ch = new FileOutputStream(f2ch);
            BufferedOutputStream bos_2ch = new BufferedOutputStream(os_2ch);
            DataOutputStream dos_2ch = new DataOutputStream(bos_2ch);

            for(int i=0;i<stroke_data_tmp_2ch[0].length;i++)
        {

            dos.writeShort(stroke_data_tmp_2ch[0][(i+stroke_data_ptr+1)%stroke_data_tmp_2ch[0].length]);
            dos_2ch.writeShort(stroke_data_tmp_2ch[0][(i+stroke_data_ptr+1)%stroke_data_tmp_2ch[0].length]);
            dos_2ch.writeShort(stroke_data_tmp_2ch[1][(i+stroke_data_ptr+1)%stroke_data_tmp_2ch[0].length]);
            if(i==0) Log.i("tmp file test start","ptr= "+stroke_data_ptr +"real ptr= "+(i+stroke_data_ptr+1)%stroke_data_tmp_2ch[0].length);
            if(i==stroke_data_tmp_2ch[0].length-1) Log.i("tmp file test end","ptr= "+stroke_data_ptr +"real ptr= "+(i+stroke_data_ptr+1)%stroke_data_tmp_2ch[0].length);

        }
            PcmToWavUtil pcmToWavUtil = new PcmToWavUtil(48000, AudioFormat.CHANNEL_IN_MONO,AudioFormat.ENCODING_PCM_16BIT);
            String path=getExternalCacheDir()+"/"+f.getName();
            String outpath = path.replace(".pcm", ".wav");
            pcmToWavUtil.pcmToWav(path, outpath);

            PcmToWavUtil pcmToWavUtil2ch = new PcmToWavUtil(48000, AudioFormat.CHANNEL_IN_STEREO,AudioFormat.ENCODING_PCM_16BIT);
            String path2ch=getExternalCacheDir()+"/"+f2ch.getName();
            String outpath2ch = path2ch.replace(".pcm", ".wav");
            pcmToWavUtil2ch.pcmToWav(path2ch, outpath2ch);


        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

    }

    /**
     * @param input         raw PCM data
     *                      limit of file size for wave file: < 2^(2*4) - 36 bytes (~4GB)
     * @param output        file to encode to in wav format
     * @param channelCount  number of channels: 1 for mono, 2 for stereo, etc.
     * @param sampleRate    sample rate of PCM audio
     * @param bitsPerSample bits per sample, i.e. 16 for PCM16
     * @throws IOException in event of an error between input/output files
     * @see <a href="http://soundfile.sapp.org/doc/WaveFormat/">soundfile.sapp.org/doc/WaveFormat</a>
     */
    static public void PCMToWAV(File input, File output, int channelCount, int sampleRate, int bitsPerSample) throws IOException {
        final int inputSize = (int) input.length();

        try (OutputStream encoded = new FileOutputStream(output)) {
            // WAVE RIFF header
            writeToOutput(encoded, "RIFF"); // chunk id
            writeToOutput(encoded, 36 + inputSize); // chunk size
            writeToOutput(encoded, "WAVE"); // format

            // SUB CHUNK 1 (FORMAT)
            writeToOutput(encoded, "fmt "); // subchunk 1 id
            writeToOutput(encoded, 16); // subchunk 1 size
            writeToOutput(encoded, (short) 1); // audio format (1 = PCM)
            writeToOutput(encoded, (short) channelCount); // number of channelCount
            writeToOutput(encoded, sampleRate); // sample rate
            writeToOutput(encoded, sampleRate * channelCount * bitsPerSample / 8); // byte rate
            writeToOutput(encoded, (short) (channelCount * bitsPerSample / 8)); // block align
            writeToOutput(encoded, (short) bitsPerSample); // bits per sample

            // SUB CHUNK 2 (AUDIO DATA)
            writeToOutput(encoded, "data"); // subchunk 2 id
            writeToOutput(encoded, inputSize); // subchunk 2 size
            copy(new FileInputStream(input), encoded);
        }
    }

    public static byte[] toByteArray(File f) throws IOException {


        if (!f.exists()) {
            throw new FileNotFoundException();
        }

        ByteArrayOutputStream bos = new ByteArrayOutputStream((int) f.length());
        BufferedInputStream in = null;
        try {
            in = new BufferedInputStream(new FileInputStream(f));
            int buf_size = 1024;
            byte[] buffer = new byte[buf_size];
            int len = 0;
            while (-1 != (len = in.read(buffer, 0, buf_size))) {
                bos.write(buffer, 0, len);
            }
            return bos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            throw e;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bos.close();
        }
    }


    /**
     * Size of buffer used for transfer, by default
     */
    private static final int TRANSFER_BUFFER_SIZE = 10 * 1024;

    /**
     * Writes string in big endian form to an output stream
     *
     * @param output stream
     * @param data   string
     * @throws IOException
     */
    public static void writeToOutput(OutputStream output, String data) throws IOException {
        for (int i = 0; i < data.length(); i++)
            output.write(data.charAt(i));
    }

    public static void writeToOutput(OutputStream output, int data) throws IOException {
        output.write(data >> 0);
        output.write(data >> 8);
        output.write(data >> 16);
        output.write(data >> 24);
    }

    public static void writeToOutput(OutputStream output, short data) throws IOException {
        output.write(data >> 0);
        output.write(data >> 8);
    }

    public static long copy(InputStream source, OutputStream output)
            throws IOException {
        return copy(source, output, TRANSFER_BUFFER_SIZE);
    }

    public static long copy(InputStream source, OutputStream output, int bufferSize) throws IOException {
        long read = 0L;
        byte[] buffer = new byte[bufferSize];
        for (int n; (n = source.read(buffer)) != -1; read += n) {
            output.write(buffer, 0, n);
        }
        return read;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            String currentTime = LocalDateTime.now().toString();
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        }
        catch (IOException e)
        {
            Log.i("exception",e.toString());
        }
        finally {
            is.close();
            os.close();
        }
    }

    private void onclick_audio_start()//用來撥放音檔
    {
        Thread thread = new Thread(new Runnable() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void run() {
                StartAudioRecord();
            }
        });
        thread.start();
        ButtonEnabled(false,true,false);
    }
    private void ButtonEnabled(boolean start, boolean stop, boolean play) {
        btn_audio_record.setEnabled(start);
        btn_audio_stop.setEnabled(stop);
        btn_audio_play.setEnabled(play);
    }
    private void onclick_audio_stop()//停止撥放
    {
        isRecording = false;
        ButtonEnabled(true, false, true);
        Log.i(TAG,"audio stop");
    }

    private void onclick_audio_play()
    {
        if(file_org == null){
            Log.i(TAG,"Null file");
            return;
        }
//讀取檔案
        int musicLength = (int) (file_org.length() / 2);//過長的檔案可能會導致出錯
        Log.i(TAG,"music len="+musicLength);
        short[] music = new short[musicLength];
        try {
            InputStream is = new FileInputStream(file_org);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);
            int i = 0;
            while (dis.available() > 0) {
                music[i] = dis.readShort();

                i++;
            }
            dis.close();
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    48000, AudioFormat.CHANNEL_OUT_MONO,
                    AudioFormat.ENCODING_PCM_16BIT,
                    musicLength * 2,
                    AudioTrack.MODE_STREAM);
            audioTrack.play();
            audioTrack.write(music, 0, musicLength);
            audioTrack.stop();
            Log.i(TAG,"成功播放");
        } catch (Throwable t) {
            Log.e(TAG, "播放失敗"+t);
            t.printStackTrace();
        }
    }

    /**
     * apply desired window function to the signal for fft
     *
     * @param signal
     *            : the signal intended to apply the window on
     * @param window
     *            : window function
     * @return the resulting signal after being applied the window
     */
    private static double[] applyWindowFunc(double[] signal, double[] window) {
        double[] result = new double[signal.length];
        int leng= window.length;
        for (int i = 0; i < leng; i++) {
            result[i] = signal[i] * window[i];
        }
        return result;
    }

    /**
     * generate hanning window function
     *
     * @param windowSize
     * @return the hanning window of a size "windowSize"
     */
    private double[] hanning(int windowSize) {//會根據從主界面條的window func產生對應的function 只要用這個產生就好 而參數大小應與fftsize相同
        double h_wnd[] = new double[windowSize]; // Hanning window
        if(dowindow==1)//hanning window
            for (int i = 0; i < windowSize; i++) { // calculate the hanning window
                h_wnd[i] = 0.5d * (1d - Math.cos(2.0 * Math.PI * i / (windowSize - 1)));

            }
        if(dowindow==2)//hamming
            for (int i = 0; i < windowSize; i++) { // calculate the hamming window
                h_wnd[i] = 0.54d - 0.46d*Math.cos(2.0 * Math.PI * i / (windowSize - 1));
            }
        return h_wnd;
    }

    private void toggle()//切換window func
    {

        if(dowindow==2) {dowindow=0;txt_out.setText("window close"); return;}
        if(dowindow==1) {dowindow=2;txt_out.setText("window hamming v2"); return;}
        if(dowindow==0) {dowindow=1;txt_out.setText("window hanning v1");return;}

    }
    private void toggledarw()////用來切換畫畫模式 0是spectrum 1是原data 2是i/q signal 3是inv fft 後的音訊數據 4是震動數據 5是??? 6是baseband 7是baseband沒dc
    {
        if(dodraw==7) {dodraw=0;txt_out.setText("draw spectrum");btn_toggle_draw.setText("draw spectrum");return;}
        if(dodraw==6) {dodraw=7;txt_out.setText("draw baseband_nodc");btn_toggle_draw.setText("draw baseband nodc");return;}
        if(dodraw==5) {dodraw=6;txt_out.setText("draw baseband");btn_toggle_draw.setText("draw baseband");return;}
        if(dodraw==4) {dodraw=5;txt_out.setText("draw location");btn_toggle_draw.setText("draw location"); return;}
        if(dodraw==3) {dodraw=4;txt_out.setText("draw shake");btn_toggle_draw.setText("draw shake"); return;}
        if(dodraw==2) {dodraw=3;txt_out.setText("draw inverse fft"); btn_toggle_draw.setText("inv fft");return;}
        if(dodraw==1) {dodraw=2;txt_out.setText("draw i/q signal");btn_toggle_draw.setText("draw i/q"); return;}
        if(dodraw==0) {dodraw=1;txt_out.setText("draw input data");btn_toggle_draw.setText("draw input");return;}

    }
    //given input spectrum set frequcncy between lower upper =0
    //most_freq = (double)((double)frequency * (double)peak_location)/(double)(bufferSize*2);
    //affect on only spectrum not fft data itself
    //目前不使用 因為只對spectrum有用 用to trans版的更完善
    private double[] cut_frequency(double[] spectrum ,int lower_bound,int upper_bound,int frequency,int fftsize)
    {
        int len=spectrum.length;
        for(int i=0;i<len;i++)
        {
            double freq = (double)((double)frequency * (double)i)/(double)(fftsize);
            if(freq>=lower_bound-10&&freq<=upper_bound+10)
            {
                spectrum[i]=0;
            }
        }
        return spectrum;
    }
//use it after fft
//affect on fft data itself
//會截斷某lower~upper的頻率範圍
    private double[] to_transform_cut_frequency(double[] toTransform ,int lower_bound,int upper_bound,int frequency,int fftsize) {
        int len = toTransform.length;


        for (int index = 1; index < len; index = index + 2)
        {//index=1 3 5... i=1 2 3(index+1)/2...
            int i = (index + 1) / 2;
            double freq = (double)((double)frequency * (double)i)/(double)(fftsize);
            if(freq>=lower_bound-10&&freq<=upper_bound+10)
            {
                toTransform[index]=0;
                toTransform[index+1]=0;
            }
            else if(freq>=upper_bound&&index<=len-2)
            {
                toTransform[index]*=0.5;
                toTransform[index+1]*=0.5;
            }
        }
        return toTransform;
    }
    private void cal_sd()
    {
        int collected=0;
        short[] collected_data=new short[131072];
        //採集率
        int frequency = 48000;
//格式
        int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
//16Bit
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
        int bufferSize=4096;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }

        AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
        short[] buffer = new short[bufferSize];//用來儲存原始音訊資料
        boolean dorecord=true;

        audioRecord.startRecording();
        Log.i(TAG,"start calculating sd");
        while(dorecord)
        {
            int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);
            for(int i=0;i<bufferReadResult;i++)
            {
                collected_data[collected]=buffer[i];
                collected++;
                if(collected==131071) break;
            }
            if(collected==131071) {dorecord=false;break;}
        }
        audioRecord.stop();
        Log.i(TAG,"stop calculating sd");
        double sum = 0.0, standardDeviation = 0.0;
        int length = 131072;

        for(double num : collected_data) {
            sum += num;
        }

        double mean = sum/length;

        for(double num: collected_data) {
            standardDeviation += Math.pow(num - mean, 2);
        }
        Log.i(TAG,"SD="+standardDeviation);

    }
    public void onResume() {
        super.onResume();
        //sensorManager.registerListener(gyroListener, sensor, SensorManager.SENSOR_DELAY_GAME);
        sensorManager.registerListener(gyroListener, sensor_gravity, SensorManager.SENSOR_DELAY_GAME);
    }

    public void onStop() {
        super.onStop();
        sensorManager.unregisterListener(gyroListener);
    }
    int contttt=0;
    long last_time=-1,cur_time=-1,too_much_time=-1;
    public SensorEventListener gyroListener = new SensorEventListener() {

        public void onAccuracyChanged(Sensor sensor, int acc) {
        }
        double lastx=0,lasty=0;
        int data_cnt=0;
        double[] shake_datas=new double[100];
        int[][] stable_data=new int[5][2];

        int stable_cnt;
        public void onSensorChanged(SensorEvent event) {
            if(event.sensor.getType()==Sensor.TYPE_GYROSCOPE) {
                float x = event.values[0];
                float y = event.values[1];
                float z = event.values[2];

                float movement = x + y + z;

                //if(Math.abs(x)>0.00115) Log.i(TAG,"x>0.0011");
                //if (Math.abs(y) > 0.00115) Log.i(TAG, "y>0.0011");
                //else if(Math.abs(y)>0.0011) Log.i(TAG,"else y>0.001");
            }
            if(event.sensor.getType()==Sensor.TYPE_GRAVITY) {
                float[] gravity = new float[3];
                float[] motion = new float[3];
                double ratio;
                double mAngle;
                for(int i=0; i<3; i++) {
                    gravity [i] = (float) (0.1 * event.values[i] + 0.9 * gravity[i]);
                    motion[i] = event.values[i] - gravity[i];
                }
                ratio = gravity[1]/SensorManager.GRAVITY_EARTH;
                if(ratio > 1.0) ratio = 1.0;
                if(ratio < -1.0) ratio = -1.0;
                mAngle = Math.toDegrees(Math.acos(ratio));
                if(gravity[2] < 0) {
                    mAngle = -mAngle;
                }
                double dx=0,dy=0;
                dx=lastx-event.values[0];
                dx*=100000;
                dy=lasty-event.values[1];
                dy*=100000;
                lastx=event.values[0];
                lasty=event.values[1];


                //Log.i("gravitytmp","dx "+Math.abs(dx)+"  dy  "+Math.abs(dy));
                if((Math.abs(dx)>0.00006*100000||Math.abs(dy)>0.00006*100000)&&Math.abs(dx)<0.002*100000&&Math.abs(dy)<0.002*100000)
                {
                    if(gravity_flag==true)
                    {
                        cur_time=System.currentTimeMillis();
                        if(cur_time-last_time>=350) {gravity_flag=false;triggered_flag=false;}
                        if(cur_time-too_much_time>=800) too_much_flag=false;
                    }
                    else
                    {
                        gravity_flag=true;

                        cur_time=System.currentTimeMillis();
                        last_time=System.currentTimeMillis();
                        if(cur_time-too_much_time>=800) too_much_flag=false;
                        //Log.i(TAG,"gravity detected"+contttt);

                    }
                    if(retrigger_flag==2) retrigger_flag=0;

                }
                else if(Math.abs(dx)<0.000005*100000&&Math.abs(dy)<0.000005*100000)
                {
                    if(retrigger_flag==1) retrigger_flag=2;
                    shake_triggered=false;
                    //Log.i(TAG,"small shake here!!");
                    cur_time=System.currentTimeMillis();
                    if(cur_time-last_time>=350) {gravity_flag=false;triggered_flag=false;}
                    if(cur_time-too_much_time>=800) too_much_flag=false;
                }
                else if(Math.abs(dx)>=0.002*100000&&Math.abs(dy)>=0.002*100000)
                {
                    //Log.i(TAG,"too much");
                    cur_time=System.currentTimeMillis();
                    too_much_time=System.currentTimeMillis();
                    too_much_flag=true;
                    if(cur_time-last_time>=350) {gravity_flag=false;triggered_flag=false;}
                }
                else
                {
                    cur_time=System.currentTimeMillis();
                    if(cur_time-last_time>=350) {gravity_flag=false;triggered_flag=false;}
                    if(cur_time-too_much_time>=800) too_much_flag=false;
                }
                contttt++;
                dx*=10;
                shake_datas[data_cnt]=dx;
                data_cnt++;
                if(data_cnt==100&&dodraw==4)
                {
                    Message msg = handlerMeasure.obtainMessage();
                    msg.what = 6;
                    msg.obj = shake_datas;
                    handlerMeasure.sendMessage(msg);
                    data_cnt=0;
                }
                else if(data_cnt==100) data_cnt=0;


                String shakevalue = String.format(
                        "Raw values\nX: %8.5f  Y: %8.5f\n" +
                                "Motion\nX: %8.5f  Y: %8.5f\n",
                        event.values[0], event.values[1],
                        motion[0], motion[1]);
               // Log.i("filt",shakevalue);

                //if(stroke_detected) txt_out.setText("detected! "+stroke_cnt);
                //else txt_out.setText("nothing");
                //txt_out.setText(shakevalue);
                //txt_out.bringToFront();
                //txt_out.invalidate();
                //btn_toggle_window.setVisibility(View.INVISIBLE);


            }

        }
    };
    Message msg = handlerMeasure.obtainMessage();

//llap zone
@SuppressLint("HandlerLeak")
private Handler updateviews =new Handler()
{
    @SuppressLint("HandlerLeak")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void handleMessage(Message msg)
    {
        if(msg.what== 0)
        {
            if(isCalibrated) {
                texDistance_x.setText(String.format("x=%04.2f", dischangex / 10) + "cm");
                texDistance_y.setText(String.format("y=%04.2f", dischangey / 10) + "cm");
                absolute_disx.setText(String.format("absolute x=%04.2f", disx/10) + "cm");
                absolute_disy.setText(String.format("absolute y=%04.2f", disy/10)+ "cm");

                int tmp[][] = (int[][]) msg.obj;//1為x 2為y
                //int tmp[][]={{4,1},{6,3},{8,2},{10,2},{10,1}};
                //tracecount=5;
                canvas2.drawColor(Color.WHITE);
                //canvas.scale(0.5f, 0.5f);

                paint2.setStyle(Paint.Style.STROKE);
                paint2.setStrokeWidth(5);
                paint2.setColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                //paint2.setAntiAlias(true);
                //Log.i(TAG, "IN the section try");
                //path.moveTo(100, 100);//原點
                //int eventx=100;
                //int eventy=100;
                for (int i=0;i<twodimsioncount;i++){
                    //Log.i(TAG, "IN the section try" + "x= " + (int)Math.round(tmp[i][1]*1) + "y= " + (int)Math.round(tmp[i][2]*1));
                    tmp[i][1]+=100;
                    tmp[i][2]+=50;
                    tmp[i][2]*=1.2;
                    Log.i(TAG, "IN the section" + "x= " + (int)Math.round(tmp[i][1]*1) + "y= " + (int)Math.round(tmp[i][2]*1));
                }
                for (int i = 0; i < twodimsioncount; i++) {
                    if(drawcount==1){
                        path.reset();
                        path.moveTo((int)Math.round(tmp[i][1]),(int)Math.round(tmp[i][2]));
                        drawcount=0;
                        //Log.i(TAG, "stroke: IN the section" + "x= " + (int)Math.round(tmp[i][1]*1) + "y= " + (int)Math.round(tmp[i][2]*1));
                    }
                    if(i==0){
                        path.moveTo((int)Math.round(tmp[i][1]*1),(int)Math.round(tmp[i][2]*1));
                        //Log.i(TAG, "IN the section" + "x= " + (int)Math.round(tmp[i][1]*1) + "y= " + (int)Math.round(tmp[i][2]*1));
                    }
                    else {
                        float midx = ((int)Math.round(tmp[i][1]*1) + prevx) / 2;
                        float midy = ((int)Math.round(tmp[i][2]*1) + prevy) / 2;
                        if(i==1){
                            path.lineTo(midx,midy);
                        }
                        else{
                            path.quadTo(prevx, prevy, midx, midy);
                        }
                        //Log.i(TAG, "IN the section two" + "x= " + (int)Math.round(tmp[i][1]*1) + "y= " + (int)Math.round(tmp[i][2]*1));
                    }
                    prevx = (int)Math.round(tmp[i][1]*1);
                    prevy = (int)Math.round(tmp[i][2]*1);
                    //Log.i(TAG, "IN the section try" + "x= " + (int)Math.round(tmp[i][1]*1) + "y= " + (int)Math.round(tmp[i][2]*1));
                    //Log.i(TAG,"In the section COUNT="+twodimsioncount);
                    //path.moveTo(eventx,eventy);
                }
                path.lineTo(prevx,prevy);
                canvas2.drawPath(path, paint2);
                //path.moveTo(prevx,prevy);
                imageView.invalidate();

                twodimsioncount=0;

            }
            else
            {
                texDistance_x.setText("Calibrating...");
                texDistance_y.setText("");
                absolute_disx.setText("");
                absolute_disy.setText("");

            }
            //Log.i(TAG,"count" + tracecount);
            tracecount=0;
        }
        else if(msg.what==1)
        {
            int result=(int)msg.obj;
            //Log.i("預測結果:",""+result);
            predict_text.setText("預測結果"+result);
        }
        else if(msg.what==2){
            int position=(int)msg.obj;
            predict_text.setText("敲擊第"+position+"按鍵");

        }
        boolean xydata = false;
        if(xydata){
            String text_x = texDistance_x.getText().toString();
            String text_y = texDistance_y.getText().toString();
            String abs_x = absolute_disx.getText().toString();
            String abs_y = absolute_disy.getText().toString();
            WriteStringFile(text_x,"test_x");
            WriteStringFile(text_y,"text_y");
            WriteStringFile(abs_x,"abs_x");
            WriteStringFile(abs_y,"abs_y");
        }
    }


};
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void WriteStringFile(String data, String filename) {
        String currentTime = LocalDateTime.now().toString();
        File road = new File(getExternalCacheDir() + file.getName(), filename+currentTime);
        FileOutputStream stream = null;
        try {
            stream = new FileOutputStream(road);
            stream.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    class ThreadInstantPlay extends Thread
    {
        @Override
        public void run()
        {
            SoundPlayer Player= new SoundPlayer(sampleRateInHz,numfreq,wavefreqs);
            blnPlayRecord=true;
            Player.play();
            while (blnPlayRecord==true){}
            Player.stop();

        }
    }

    class ThreadInstantRecord extends Thread {

        //private short [] bsRecord = new short[recBufSize];
        //

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        public void run() {
            short[] bsRecord = new short[recBufSize * 2];
            byte[] networkbuf = new byte[recBufSize * 4];
            int datacount = 0;
            int curpos = 0;
            long starttime,endtime;
            String c_result;
            int tmptracex[]= new int[100];
            int tmptracey[]= new int[100];
            boolean stroke_data_read=false,stroke_train=false;

            int tmpcount=0;
            long current_time=-1;
            long last_time=-1;
            FileWriter fwriter = null;

            try {//try to create txt
                file_baseband_txt=new File(getExternalCacheDir().getAbsolutePath()+"/baseband_data.txt");
                fwriter=new FileWriter(file_baseband_txt,false);
            } catch (IOException e) {
                e.printStackTrace();
            }



            while (blnPlayRecord == false) {
            }
            llap_audioRecord.startRecording();

            long lasttime=0,nowtime=0,stroke_detect_time=0,mystarttime=0,endttime=0;
            int loop_cnt=0;
            double stroke_dc_tot=0.0;
            double stroke_ndc_tot=0.0;
            int llap_stroke_flag=0;//0 for no 1 for yes
            int precatch_size=1000;//var to change the amount of precatched data
            double recent_dc_tot[]=new double[precatch_size];
            double recent_ndc_tot[]=new double[precatch_size];
            int recent_tot_ptr=0;
            while (blnPlayRecord) {


                if(stroke_data_read==false&&force_train==false )
                {
                    boolean error_flag=false;
                    for(int r=0;r<9;r++)
                    {
                        try {
                            stroke_training_result[r]=stroke_reading(stroke_training_result[r],r);
                            if(stroke_training_result[r]==null)
                            {
                                Log.i("error","please train");
                                error_flag=true;
                                break;
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                            Log.i("error","stroke read failed");
                            stroke_train=true;
                            break;

                        }
                    }
                    if(error_flag==true)
                    {
                        stroke_train=true;
                        stroke_data_read=true;
                        initstroke_flag=true;
                    }
                    else {
                        initstroke_flag = false;
                        stroke_data_read = true;
                    }
                }





                if(mystarttime==0) mystarttime=System.currentTimeMillis();

                int line = llap_audioRecord.read(bsRecord, 0, frameSize * 2);
                datacount = datacount + line / 2;
                now=now+1;

                //Log.i(TAG,"recevied data:" + line + " at time" + System.currentTimeMillis());
                if (line >= frameSize) {

                    //get baseband


                    getbaseband(bsRecord, baseband, line / 2);
                    //Log.i("BASEBAND", getbaseband(bsRecord, baseband, line / 2));//do cic

//TODO OBSERVE BASEBAND(length=2048)
                   // Log.i(TAG,"time used forbaseband:"+(endtime-starttime));

                    Double basebandtotal=0.0;
                    Double ndctotal=0.0;
                    removedc(baseband, baseband_nodc, dcvalue);
                   // Log.i("REMOVEDC", removedc(baseband, baseband_nodc, dcvalue)+baseband_nodc.length);
                    nowtime=System.currentTimeMillis();
                    for(int i=0;i<baseband.length;i++)
                    {
                        basebandtotal+=baseband[i];
                        ndctotal+=baseband_nodc[i];
                    }


                    recent_dc_tot[recent_tot_ptr]=basebandtotal;
                    recent_ndc_tot[recent_tot_ptr]=ndctotal;
                    recent_tot_ptr++;
                    recent_tot_ptr%=precatch_size;
/*
                    if(llap_stroke_flag==0&&stroke_detected==true)
                    {
                        llap_stroke_flag=1;
                        nowtime=System.currentTimeMillis();
                        stroke_detect_time=System.currentTimeMillis();
                        stroke_dc_tot=0.0;
                        stroke_ndc_tot=0.0;
                        //Log.i("debug","here!! stroke_dc_tot=" +stroke_dc_tot);
                        for(int i=0;i<precatch_size;i++)
                        {
                            stroke_dc_tot+=recent_dc_tot[i]/10;
                            stroke_ndc_tot+=recent_ndc_tot[i]/10;
                        }
                        loop_cnt=precatch_size;
                        //stroke_dc_tot+=basebandtotal/10;
                        //stroke_ndc_tot+=ndctotal/10;
                    }
                    else if(llap_stroke_flag==1&&nowtime-stroke_detect_time<350)
                    {
                        stroke_dc_tot+=basebandtotal/10;
                        stroke_ndc_tot+=ndctotal/10;
                        loop_cnt++;
                    }
                    else if(llap_stroke_flag==1&&nowtime-stroke_detect_time>=350)
                    {

                        llap_stroke_flag=0;
                        Log.i("stroke_dc_tot= ",""+stroke_dc_tot+"    avg=v"+stroke_dc_tot/loop_cnt);//end of calculation
                        Log.i("stroke_ndc_tot= ",""+stroke_ndc_tot+"    avg= "+stroke_ndc_tot/loop_cnt);
                        try {
                            fwriter.write(""+stroke_cnt+","+nowtime+","+stroke_dc_tot+","+stroke_dc_tot/loop_cnt+","+stroke_ndc_tot+","+stroke_ndc_tot/loop_cnt+"\n");

                            fwriter.flush();
                        } catch (IOException e) {
                            Log.i("TAG","fwrite erroe"+e);
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        //Log.i("????","check if anything wrong");
                    }
                    */

                    //Log.i(TAG,"basebandtot= "+basebandtotal +"baseavg= "+basebandtotal/baseband.length);
                    //Log.i(TAG,"ndctot= "+basebandtotal +"ndcavg= "+basebandtotal/baseband.length);
                    if(dodraw==6&&nowtime-lasttime>200)
                    {
                        Message msg = handlerMeasure.obtainMessage();
                        msg.what = 7;
                        msg.obj = baseband;
                        handlerMeasure.sendMessage(msg);
                        lasttime=System.currentTimeMillis();

                    }
                    if(dodraw==7&&nowtime-lasttime>200)
                    {
                        Message msg = handlerMeasure.obtainMessage();
                        msg.what = 8;
                        msg.obj = baseband_nodc;
                        handlerMeasure.sendMessage(msg);
                        lasttime=System.currentTimeMillis();

                    }

//TODO OBSERVE BASEBAND_NODC(length=2048) DCVALUE(length=64)
/*
                    for(int i=0;i<freqpower.length;i++)
                    {
                        if(freqpower[i]!=0) Log.i("freqpower0",""+i+" "+freqpower[i]);
                    }

 */
                    getdistance(baseband_nodc, phasechange, dischange, freqpower);
                   // Log.i("GETDISTANCE", getdistance(baseband_nodc, phasechange, dischange, freqpower)+" "+freqpower.length);
//TODO phasechange is hidden in c program and freqpower(useless) is always 0
                    /*
                    for(int i=0;i<freqpower.length;i++)
                    {
                        if(freqpower[i]!=0) Log.i("freqpower",""+i+" "+freqpower[i]);
                    }


                     */

                    //Log.i(TAG,"time used distance:"+(endtime-starttime));


                    if(!isCalibrated&&Math.abs(dischange[0])<0.05&&now-lastcalibration>10) {


                        c_result=calibrate(baseband);
                        //Log.i("CALIBRATE",c_result) ;
                        lastcalibration=now;
                        if(c_result.equals("calibrate OK")){
                            isCalibrated=true;
                        }

                    }
                    if(isCalibrated) {
                        getidftdistance(baseband_nodc, idftdis);
                        //Log.i("GETIDFT",getidftdistance(baseband_nodc, idftdis));
                        //idftdis is always 0
                        if(idftdis[0]!=0||idftdis[1]!=0)
                        {
                            Log.i("testidft","x= "+idftdis[0]+" y= "+idftdis[1]);
                        }

                        //keep difference stable;

                        double disdiff,dissum;
                        disdiff=dischange[0]-dischange[1];
                        dissum=dischange[0]+dischange[1];
                        dischangehist=dischangehist*0.5+disdiff*0.5;
                        dischange[0]=(dissum+dischangehist)/2;
                        dischange[1]=(dissum-dischangehist)/2;


                        disx=disx+dischange[0];

                        if(disx>1000)
                            disx=1000;
                        if(disx<0)
                            disx=0;
                        dischange[1]*=8;
                        disy=disy+dischange[1];
                        if(disy>1000)
                            disy=1000;
                        if(disy<0)
                            disy=0;
                        if(Math.abs(dischange[0])<0.05&&Math.abs(dischange[1])<0.05&&Math.abs(idftdis[0])>0.1&&Math.abs(idftdis[1])>0.1)
                        {
                            disx=disx*(1-distrend)+idftdis[0]*distrend;
                            disy=disy*(1-distrend)+idftdis[1]*distrend;
                        }
                        if(disx<micdis1)
                            disx=micdis1;
                        if(disy<micdis2)
                            disy=micdis2;
                        if(Math.abs(disx-disy)>(micdis1+micdis2))
                        {
                            double tempsum=disx+disy;
                            if(disx>disy)
                            {
                                disx=(tempsum+micdis1+micdis2)/2;
                                disy=(tempsum-micdis1-micdis2)/2;

                            }
                            else
                            {
                                disx=(tempsum-micdis1-micdis2)/2;
                                disy=(tempsum+micdis1+micdis2)/2;
                            }
                        }

                        /* 暫時無用的避免距離增加太多
                        if(firststart_flag==false){
                            lastx=disx;
                            lasty=disy;
                            firststart_flag=true;
                            last_time=System.currentTimeMillis();
                        }
                        current_time=System.currentTimeMillis();
                        if(current_time-last_time>1000){
                            last_time=System.currentTimeMillis();
                            changex=disx-lastx;
                            changey=disy-lasty;
                            if((Math.abs(changex/10)>0.3) || (Math.abs(changey/10)>0.3) ){
                                Log.i(TAG,"stroke change too much!");
                                disx=lastx;
                                disy=lasty;
                            }
                            else{
                                lastx=disx;
                                lasty=disy;
                            }
                        }*/
                        //計算相對距離，敲擊為trigger
                        /*if((stroke_detected==true)&&(stroke_detected!=stroke_flag))
                        {

                            dischangex=disx-tmpx;
                            dischangey=disy-tmpy;
                            tmpx=disx;
                            tmpy=disy;
                            stroke_flag=stroke_detected;
                        }
                        stroke_flag=stroke_detected;*/
                        trace_x[tracecount]= (int) Math.round((disy*micdis1*micdis1-disx*micdis2*micdis2+disx*disy*(disy-disx))/2/(disx*micdis2+disy*micdis1));
                        trace_y[tracecount]=(int) Math.round(Math.sqrt(Math.abs( (disx*disx-micdis1*micdis1)*(disy*disy-micdis2*micdis2)*((micdis1+micdis2)*(micdis1+micdis2)-(disx-disy)*(disx-disy))  )  )/2/(disx*micdis2+disy*micdis1) );
                        //trace_x[tracecount]= (int) Math.round(disx);
                        //Log.i("test","x= "+trace_x[tracecount]+" y= "+trace_y[tracecount]);
                        //section[0][1]=(trace_x[tracecount]+tmp_sectionx)/2;
                        //section[0][2]=(trace_y[tracecount]+tmp_sectiony)/2;
                        //tmp_sectionx=trace_x[tracecount];
                        //tmp_sectiony=trace_y[tracecount];
                        /*tmptracex[tmpcount]=trace_x[tracecount];
                        tmptracey[tmpcount]=trace_y[tracecount];
                        tmpcount++;
                        if(tmpcount==3){
                            int sumx=0;
                            int sumy=0;
                            for(int i=0;i<3;i++){
                                sumx+=tmptracex[i];
                                sumy+=tmptracey[i];
                            }
                            sumx/=3;
                            sumy/=3;
                            trace[twodimsioncount][1]=sumx;
                            trace[twodimsioncount][2]=sumy;
                            twodimsioncount++;
                            tmpcount=0;
                        }*/



                        //Log.i("test","x= "+trace_x[tracecount]+" y= "+trace_y[tracecount]);
                        tracecount++;


                        //敲擊實驗開始
                        /**/
                    //實驗五 : 敲擊+方向判斷，先敲擊四個頂點，順序為右下、右上、左上、左下(左邊為離手機較遠那側)
                    current_time=System.currentTimeMillis();
                    if(current_time-last_time>500) {
                        /* case1 四宮格
                        if(stroke_detected && initstroke_flag && initstroke_cnt < 4){//還沒初始化
                            stroke_training(initstroke_cnt,firststroke_cnt,disx/10,disy/10);
                            更改ui邏輯有誤之後修正
                            Message hint=new Message();
                            hint.what=2;
                            hint.obj=initstroke_cnt;
                            updateviews.sendMessage(hint);
                            last_time=System.currentTimeMillis();
                        }
                        */
                        /* case2九宮格 */
                        if(stroke_detected &&initstroke_flag &&initstroke_cnt<9&&(stroke_train==true||force_train==true||stroke_training_result[0]==null)){
                            stroke_training(initstroke_cnt,firststroke_cnt,disx/10,disy/10);
                            //更改ui邏輯有誤之後修正
                            stroke_data_read=false;
                            if(!initstroke_flag){ //初始化完成
                                stroke_train=false;
                                force_train=false;
                            }
                            last_time=System.currentTimeMillis();
                        }
                        else if(stroke_detected && !initstroke_flag){//初始化完成
                            //方法二:近鄰演算法
                            Log.i(TAG, "stroke input point x=" + disx/10 + "y= " + disy/10);
                            double input_pointx = disx/10;
                            double input_pointy = disy/10;
                            double sumProductx =0;
                            double sumProducty=0;
                            double sumAxSq = 0;//A為input B為train
                            double sumAySq = 0;//A為input B為train
                            double sumBxSq = 0;
                            double sumBySq = 0;
                            int nearest=0;
                        /*四宮格
                            for(int i=0;i<4;i++){
                                //歐幾里得距離
                                neighbor[i]+=Math.sqrt(Math.pow(input_pointx-first_train[i][1],2)+Math.pow(input_pointy-first_train[i][2],2));
                                neighbor[i]+=Math.sqrt(Math.pow(input_pointx-second_train[i][1],2)+Math.pow(input_pointy-second_train[i][2],2));
                                neighbor[i]+=Math.sqrt(Math.pow(input_pointx-third_train[i][1],2)+Math.pow(input_pointy-third_train[i][2],2));
                                neighbor[i]+=Math.sqrt(Math.pow(input_pointx-mean_train[i][1],2)+Math.pow(input_pointy-mean_train[i][2],2));



                                //曼哈頓距離
                                neighbor[i]+=Math.abs(input_pointx-first_train[i][1])+Math.abs(input_pointx-first_train[i][2]);
                                neighbor[i]+=Math.abs(input_pointx-second_train[i][1])+Math.abs(input_pointx-second_train[i][2]);
                                neighbor[i]+=Math.abs(input_pointx-third_train[i][1])+Math.abs(input_pointx-third_train[i][2]);
                                neighbor[i]+=Math.abs(input_pointx-mean_train[i][1])+Math.abs(input_pointx-mean_train[i][2]);



                                //SSD
                                neighbor[i]+=Math.pow(input_pointx-first_train[i][1],2)+Math.pow(input_pointx-first_train[i][2],2);
                                neighbor[i]+=Math.pow(input_pointx-second_train[i][1],2)+Math.pow(input_pointx-second_train[i][2],2);
                                neighbor[i]+=Math.pow(input_pointx-third_train[i][1],2)+Math.pow(input_pointx-third_train[i][2],2);
                                neighbor[i]+=Math.pow(input_pointx-mean_train[i][1],2)+Math.pow(input_pointx-mean_train[i][2],2);

                            }
                            for(int i=0;i<4;i++){
                                Log.i("TAG","Stroke training distance: "+neighbor[i]);
                                if(neighbor[nearest]>neighbor[i]){
                                    nearest=i;
                                }
                            }
                            for(int i=0;i<12;i++) {
                                neighbor[i] = 0;
                            }
                        */
                            for(int i=0;i<9;i++){
                                int position_length=stroke_training_result[i].length;
                                for(int j=0;j<position_length;j++){
                                    double tmp_distance_x=0.0,tmp_distance_y=0.0;
                                    if(j%2==0){
                                        tmp_distance_x=Math.pow(input_pointx-stroke_training_result[i][j],2);
                                        tmp_distance_x+=Math.abs(input_pointx-stroke_training_result[i][j]);
                                    }
                                    else{
                                        tmp_distance_y=Math.pow(input_pointy-stroke_training_result[i][j],2);
                                        tmp_distance_y=Math.abs(input_pointy-stroke_training_result[i][j]);
                                    }
                                    neighbor[i]+=Math.sqrt(tmp_distance_x+tmp_distance_y);
                                }
                            }
                            for(int i=0;i<9;i++){
                                Log.i("TAG","Stroke training distance: "+neighbor[i]);
                                if(neighbor[nearest]>neighbor[i]){
                                    nearest=i;
                                }
                            }
                            for(int i=0;i<9;i++) {
                                neighbor[i] = 0;
                            }
                            nearest+=1;
                            Log.i("TAG","Stroke 預測按鍵數字為: "+nearest);
                            //predict_text.setText("預測按鍵為: "+nearest);
                            Message result=new Message();
                            result.what=1;
                            result.obj=nearest;
                            updateviews.sendMessage(result);
                            last_time=System.currentTimeMillis();
                        }
                    }

                        //實驗結束
                    }

                    if(Math.abs(displaydis-disx)>2||(tracecount>10)) {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = trace;
                        displaydis=disx;
                        updateviews.sendMessage(msg);
                    }
                    if(!isCalibrated)
                    {
                        Message msg = new Message();
                        msg.what = 0;
                        msg.obj = trace;
                        updateviews.sendMessage(msg);
                    }




                    curpos = curpos + line / 2;
                    if (curpos > coscycle)
                        curpos = curpos - coscycle;



                }
                //Log.i(TAG,"endtime" + System.currentTimeMillis());


            }
            endttime=System.currentTimeMillis();
           if(loop_cnt!=0) Log.i("time","used time="+(endttime-mystarttime)+"loops=" +loop_cnt+"avg="+(endttime-mystarttime)/(loop_cnt));
           if(loop_cnt==0) Log.i("time","used time="+(endttime-mystarttime)+"loops=" +loop_cnt);
            try {
                fwriter.flush();
                fwriter.close();
            } catch (IOException e) {
                Log.i("TAG","fwrite error at end of llap"+e);
                e.printStackTrace();
            }
            String currentTime = LocalDateTime.now().toString();
            boolean train_txt = false;
            if(train_txt){

                File des = new File(getExternalCacheDir()+currentTime+file_baseband_txt.getName());
                try {
                    copyFileUsingStream(file_baseband_txt,des);
                } catch (IOException e) {
                    Log.i("TAG","fwrite error at end of llap copying"+e);
                    e.printStackTrace();
                }
            }
            llap_audioRecord.stop();

        }
    }

// C implementation of down converter

    public native String getbaseband(short[] data, double[] outdata, int numdata);

    //C implementation of LEVD

    public native String removedc(double[] data, double[] data_nodc, short[] outdata);

    //C implementation of distance

    public native String getdistance(double[] data, double[] outdata, double [] distance, double [] freqpower);

    // Initialize C down converter

    public native String initdownconvert(int samplerate, int numfreq, double [] wavfreqs);

    public native String getidftdistance(double[] data, double[] outdata);

    public native String calibrate(double[] data);

    public native String testcc(char c);
    static {
        System.loadLibrary("mainactivity");
    }


}

/* 不再使用
    private void endMediaRecording() {
        if (!hasmic || !isRecording) return;
        handlerMeasure.removeCallbacks(taskMeasure);
        try {
            recorder.stop();
            recorder.release();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
        isRecording = false;
    }
*/
/* 不再使用
    private void startMediaRecording() {
        if (!hasmic || isRecording) return;
        txt_out.setText("start");
        media_strokes=0;
        cnt=0;
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.setOutputFile(tmpfile);
            recorder.prepare();
            recorder.start();
            isRecording = true;
            handlerMeasure.post(taskMeasure);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
    /* 不再使用
    private Runnable taskMeasure = new Runnable() {
        @Override
        public void run() {
            handlerMeasure.sendEmptyMessage(1);
            //每100毫秒抓取一次檢測結果
            handlerMeasure.postDelayed(this, 100);
            //Log.i(TAG, "workRunnable thread id = " +

            //        Thread.currentThread().getId());
        }
    };

     */
/* 不再使用
    @Override
    protected void onStop() {
        super.onStop();
        endMediaRecording();
    }
*/
    /* 不再使用
    private void play() {//not using
        player = new MediaPlayer();
        try {
            player.setDataSource(tmpfile);
            player.prepare();
            player.start();
        } catch (IOException e) {
            Log.e(TAG, "prepare() failed");
        }
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer MP) {
                MP.stop();
                MP.release();
            }
        });

    }
*/