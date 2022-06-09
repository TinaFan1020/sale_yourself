package com.example.soundkeyboard;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import android.graphics.Bitmap;
import android.widget.ImageView;

import Catalano.Math.ComplexNumber;
import Catalano.Math.Transforms.FourierTransform;
import jfftpack.RealDoubleFFT;

import  Catalano.Math.Transforms.HilbertTransform;



import com.karlotoy.perfectune.instance.PerfectTune;


public class MainActivity extends AppCompatActivity {
    private boolean hasmic = false, isRecording, haswrite = false, hasread = false;
    Button btn_toggle_draw,btn_audio_record,btn_audio_stop,btn_audio_play,btn_toggle_window,btn_play_frequency,btn_stop_frequency,btn_cal_sd;
    ImageView imageView;//最上面畫畫ㄉ
    TextView txt_out;//中間顯示字ㄉ
    Bitmap bitmap;//最上面畫畫ㄉ
    Canvas canvas;//最上面畫畫ㄉ
    Paint paint;//最上面畫畫ㄉ
    EditText frequency_text;//命名撞到 我改了變數名
    private String tmpfile;
    private final static String TAG = "MyTag";


    int cnt = 0;
    double[] toTransform;//用來放要拿去fftㄉdata
    double most_freq=0.0;//fft出來最大ㄉ頻率
    //////
    int quite_avg=90;//todo:暫時用強行設定 等開始寫預先訓練步驟時要求使用者安靜5秒來測定背景音量
    int stroke_power_max=500;//todo:暫時用強行設定 之後寫預先訓練步驟時測定按鍵按下強度 用以壓制比按鍵大的聲音
    int stroke_power_min=150;//todo:暫時用強行設定 之後寫預先訓練步驟時測定按鍵按下強度 用以偵測按鍵發生的最下限
    //todo:之後測試標準差以及變異數對於偵測的效用
    /////
    File file;
    int dowindow=1;//用來開關window func 1是開 0是關
    int dodraw=0; //用來切換畫畫模式 0是spectrum 1是原data 2是i/q signal
    PerfectTune perfectTune = new PerfectTune();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "onCreate:123");
        setContentView(R.layout.activity_main);
        checkPermission();
        //按鈕的宣告
        btn_audio_record=findViewById(R.id.btn_audio_record);
        btn_audio_stop=findViewById(R.id.btn_audio_end);
        btn_audio_play=findViewById(R.id.btn_audio_play);
        btn_toggle_window=findViewById(R.id.btn_toggle_window);
        btn_play_frequency=findViewById(R.id.btn_play_frequency);
        btn_stop_frequency=findViewById(R.id.btn_stop_frequency);
        btn_toggle_draw=findViewById(R.id.btn_toggle_draw);
        btn_cal_sd=findViewById(R.id.btn_cal_sd);
        frequency_text=findViewById(R.id.frequency_num);
        txt_out = findViewById(R.id.txt_out);
        btn_audio_record.setOnClickListener(v->onclick_audio_start());
        btn_audio_stop.setOnClickListener(v->onclick_audio_stop());
        btn_audio_play.setOnClickListener(v->onclick_audio_play());
        btn_toggle_window.setOnClickListener(v->toggle());
        btn_toggle_draw.setOnClickListener(v->toggledarw());
        btn_play_frequency.setOnClickListener(v->onlick_frequency_play());
        btn_stop_frequency.setOnClickListener(v->onlick_frequency_stop());
        btn_cal_sd.setOnClickListener(v->cal_sd());
        //畫布大小 寬=變數1 高=變數2 最左上角是0 0 右下角是 (寬,高)
        bitmap = Bitmap.createBitmap((int)4096,(int)1000,Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
        paint= new Paint();
        paint.setColor(Color.GREEN);
        imageView = (ImageView) this.findViewById(R.id.ImageView01);
        imageView.setImageBitmap(bitmap);
        tmpfile = getExternalCacheDir().getAbsolutePath();
        Log.i(TAG,"pid of main thread= "+Thread.currentThread().getId());
        imageView.invalidate();
        perfectTune.setTuneFreq(15000);
        perfectTune.setTuneAmplitude(50000);

        String testc=testcc('c');
        Log.i(TAG,testc);

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

                        int downy = (int) (1000 - (tmp[i] * 100));
                        int upy = 1000;

                        canvas.drawLine(x, downy, x, upy, paint);
                    }
                        imageView.invalidate();

                    break;
                case 3:
                    short tmp3[]=(short[]) msg.obj;
                    canvas.drawColor(Color.BLACK);

                    for(int i=0;i<tmp3.length;i++)
                    {
                        canvas.drawCircle(i,500-tmp3[i]/10,6,paint);//用畫多個圓的方式得到更好效能
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
                        canvas.drawCircle(i,500-(float)tmp4[i].real/10,6,paint);
                        paint.setColor(Color.RED);
                        canvas.drawCircle(i,500-(float)tmp4[i].imaginary/20,6,paint);

                    }
                    imageView.invalidate();
                    break;

            }
            super.handleMessage(msg);

        }
    };


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
        int stroke_cnt=0;
        int stroke_state=0;
        ////////////////////////////
        //for fft
        RealDoubleFFT transformer;


//採集率
        int frequency = 44100;
//格式
        int channelConfiguration = AudioFormat.CHANNEL_IN_MONO;
//16Bit
        int audioEncoding = AudioFormat.ENCODING_PCM_16BIT;
//生成PCM檔案
        file = new File(getExternalCacheDir().getAbsolutePath()+"/reverseme.pcm");
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
        try {//輸出流
            OutputStream os = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(os);
            DataOutputStream dos = new DataOutputStream(bos);
            //int bufferSize = AudioRecord.getMinBufferSize(frequency, channelConfiguration, audioEncoding);//這會得到最小所需buffersize 但要用2的次方大小
            int bufferSize=4096;// set as power of 2 for fft 這是每個迴圈取的資料點 每個迴圈約等於0.09秒
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

            AudioRecord audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, frequency, channelConfiguration, audioEncoding, bufferSize);
            short[] buffer = new short[bufferSize];//用來儲存原始音訊資料
            toTransform = new double[bufferSize*2];//用來儲存要放進fft的資料
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
                int bufferReadResult = audioRecord.read(buffer, 0, bufferSize);

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
                }
                //read data from mic and predict strokes
                for (int i = 0; i < bufferReadResult; i++  ) {
                    //double db = 20 * (Math.log10(Math.abs(buffer[i])));
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

                    //if(i==2000) Log.i(TAG,"buffreadreult="+bufferReadResult+"i= "+i+" buffer[i]="+buffer[i]);
                    /*
                    put data into fft array
                     */
                    toTransform[i] = (double) buffer[i] / 32768.0;
                    //put data into complex array
                    complexBuffer[i].real=buffer[i];
                    complexBuffer[i].imaginary=0;
                };
                pos_avg_local=pos_total_local/pos_cnt_local;
                pos_avg_global=pos_total_global/pos_cnt_global;
                neg_avg_local=neg_total_local/audio_cnt_local;
                /*
                apply window func before fft
                */
                if(dowindow>0) {
                    double window[] = new double[bufferSize * 2];
                    window = hanning(bufferSize * 2);//產生適當的window
                    toTransform = applyWindowFunc(toTransform, window);
                }
                /*
                do fft
                 */
                transformer.ft(toTransform);

                //do hilbert transform
                HilbertTransform.FHT(complexBuffer, FourierTransform.Direction.Forward);
                int itotal=0;
                int qtotal=0;
                for(int i=0;i<complexBuffer.length;i++)
                {
                    //Log.i(TAG,"real part["+i+"]= "+complexBuffer[i].real);
                    //Log.i(TAG,"imag part["+i+"]= "+complexBuffer[i].imaginary);
                    itotal+=Math.abs(complexBuffer[i].real);
                    qtotal+=Math.abs(complexBuffer[i].imaginary);
                }
                Log.i(TAG,"real part average= "+itotal/complexBuffer.length);

                //after hilbert transform the real part is i signal and imaginary part is q signal

                /*
                do cut off frequency cut off frequency lower than lower and bigger than upper
                 */
                //todo:這邊要做調整截斷頻率的東西
                //int lower=3000;
                //int upper=19000;
               // toTransform=to_transform_cut_frequency(toTransform,lower,upper,frequency,fftSize);

                /*

                 */
                double[] spectrum;
                //to calculate spectrum
                if (toTransform.length % 2 != 0) {// if odd
                    spectrum = new double[(toTransform.length + 1) / 2];
                    re=new double[(toTransform.length + 1) / 2];
                    im=new double[(toTransform.length + 1) / 2];
                    magnitude=new double[(toTransform.length + 1) / 2];
                    re[0]=toTransform[0];//real part of first complex fft coeffient is x[0]
                    im[0]=0;
                    magnitude[0]=re[0]*re[0];
                    spectrum[0] = Math.pow(toTransform[0] * toTransform[0], 0.5);// dc component
                    for (int index = 1; index < toTransform.length; index = index + 2) {//i=1 3 5...
                        // magnitude =re*re + im*im
                        double mag = toTransform[index] * toTransform[index]
                                + toTransform[index + 1] * toTransform[index + 1];
                        re[(index + 1) / 2]=toTransform[index];//when index=1 3 5... i=1 2 3..
                        im[(index+1)/2]=toTransform[index+1];//index=2 4 6...i=1 2 3..
                        magnitude[(index+1)/2]=Math.sqrt(mag);
                        spectrum[(index + 1) / 2] = Math.pow(mag, 0.5);
                    }
                } else {// if even
                    spectrum = new double[toTransform.length / 2 + 1];
                    re=new double[toTransform.length / 2 + 1];
                    im=new double[toTransform.length / 2 + 1];
                    magnitude=new double[toTransform.length / 2 + 1];
                    re[0]=toTransform[0];//real part of first complex fft coeffient is x[0]
                    im[0]=0;
                    magnitude[0]=re[0]*re[0];
                    spectrum[0] = Math.pow(toTransform[0] * toTransform[0], 0.5);// dc component real only
                    for (int index = 1; index < toTransform.length - 1; index = index + 2) {//index=1 3 5.. i=1 2 3...
                        // magnitude =re*re + im*im
                        double mag = toTransform[index] * toTransform[index]
                                + toTransform[index + 1] * toTransform[index + 1];
                        re[(index + 1) / 2]=toTransform[index];//index=1 3 5..i=1 2 3
                        im[(index+1)/2]=toTransform[index+1];//index=2 4 6..i=1 2 3
                        magnitude[(index+1)/2]=Math.sqrt(mag);
                        spectrum[(index + 1) / 2] = Math.pow(mag, 0.5);
                    }
                    spectrum[spectrum.length - 1] = Math.pow(toTransform[toTransform.length - 1]
                            * toTransform[toTransform.length - 1], 0.5);
                    re[re.length - 1]=toTransform[toTransform.length - 1];
                    im[im.length-1]=0;
                    magnitude[magnitude.length-1]=Math.sqrt(re[re.length - 1]*re[re.length - 1]);
                }
                //after fft the real part is stored in index 1,3,5....(n/2)-1
                // Calculate the Real and imaginary and Magnitude.

                //do inverse fft
                transformer.bt(toTransform);

                int tmp_stat=0;
                /* debug
                for(int i=0;i<toTransform.length;i++)
                {
                    if(i==2000)Log.i(TAG,"coeff "+i+ " after inv fft="+toTransform[i]);
                    //if(i>4096&&toTransform[i]>0.5)Log.i(TAG,"coeff "+i+ " !!!this should not >1"+toTransform[i]);
                }
                */

                /*
                trying to write transformed data into pcm file 音訊檔的內容是經過fft和反fft轉換的 會損壞音質
                 */
                for(int i=0;i<bufferSize;i++)
                {
                    short tmp=(short)Math.round(toTransform[i]);
                    dos.writeShort(tmp);
                }

                //end of do inverse fft
                double peak = -1.0;
                int peak_location=-1;
                int len=magnitude.length;
                // Get the largest magnitude peak
                for(int i = 0; i <len; i++){
                    if(peak < Math.abs(spectrum[i])) {
                        peak = Math.abs(spectrum[i]);
                        peak_location = i;
                    }
                    //if(spectrum[i]>=1)Log.i(TAG,"tmp "+i+ " spectrum=="+spectrum[i]);

                }
                most_freq = (double)((double)frequency * (double)peak_location)/(double)(bufferSize*2);
                //Log.i(TAG,"Most freq="+most_freq);

                //three state counter if recently detected block for a moment to prevent error
                if(stroke_state==0)
                {
                    if(pos_avg_local>=stroke_power_min&&pos_avg_local<stroke_power_max&&stroke_state>=0)
                    {
                        stroke_cnt++;
                        Log.i(TAG+"stroke detected","strokes= " +stroke_cnt+"looptimes="+looptimes);
                        stroke_state-=2;
                    }
                }
                else if(stroke_state==-1)
                {
                    stroke_state=0;
                }
                else if(stroke_state==-2)
                {
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
            dos.close();
        } catch (Throwable t) {
            Log.e(TAG, "錄音失敗"+t);
            t.printStackTrace();
        }
    }
    private void onclick_audio_start()//用來撥放音檔
    {
        Thread thread = new Thread(new Runnable() {
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
        if(file == null){
            Log.i(TAG,"Null file");
            return;
        }
//讀取檔案
        int musicLength = (int) (file.length() / 2);//過長的檔案可能會導致出錯
        Log.i(TAG,"music len="+musicLength);
        short[] music = new short[musicLength];
        try {
            InputStream is = new FileInputStream(file);
            BufferedInputStream bis = new BufferedInputStream(is);
            DataInputStream dis = new DataInputStream(bis);
            int i = 0;
            while (dis.available() > 0) {
                music[i] = dis.readShort();

                i++;
            }
            dis.close();
            AudioTrack audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    44100, AudioFormat.CHANNEL_OUT_MONO,
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
    private void toggledarw()////用來切換畫畫模式 0是spectrum 1是原data 2是i/q signal
    {

        if(dodraw==2) {dodraw=0;txt_out.setText("draw spectrum"); return;}
        if(dodraw==1) {dodraw=2;txt_out.setText("draw i/q signal"); return;}
        if(dodraw==0) {dodraw=1;txt_out.setText("draw input data");return;}

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
            }
            return toTransform;
    }
    private void cal_sd()
    {
        int collected=0;
        short[] collected_data=new short[131072];
        //採集率
        int frequency = 44100;
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