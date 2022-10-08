package com.example.soundkeyboard;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by HaoGuibao
 * Date 2016/8/26.
 * Description :pcm格式的音訊轉換為wav格式的工具類

 */

public class PcmToWavUtil
{
    private int mBufferSize=4096; //快取的音訊大小
    private int mSampleRate = 48000;// 8000|16000
    private int mChannel = AudioFormat.CHANNEL_IN_MONO; //立體聲
    private int mEncoding = AudioFormat.ENCODING_PCM_16BIT;

    public PcmToWavUtil() {
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mEncoding);
    };

    public PcmToWavUtil(int sampleRate, int channel, int encoding) {
        this.mSampleRate = sampleRate;
        this.mChannel = channel;
        this.mEncoding = encoding;
        this.mBufferSize = AudioRecord.getMinBufferSize(mSampleRate, mChannel, mEncoding);
    }
    public void pcmToWav(String inFilename, String outFilename) {
        long starttime,endtime;
        starttime=System.currentTimeMillis();
        FileInputStream in;
        FileOutputStream out;
        long totalAudioLen;
        long totalDataLen;
        long longSampleRate = mSampleRate;
        int channels = 1;
        int len=0;
        long byteRate = 16 * mSampleRate * channels / 8;
        byte[] data = new byte[mBufferSize];
        try {
            in = new FileInputStream(inFilename);
            out = new FileOutputStream(outFilename);
            totalAudioLen = in.getChannel().size();
            totalDataLen = totalAudioLen + 36;
            writeWaveFileHeader(out, totalAudioLen, totalDataLen,
                    longSampleRate, channels, byteRate);
            while ((len=in.read(data)) != -1) {
                /*
                ByteBuffer bb=ByteBuffer.wrap(data);
                bb.order(ByteOrder.LITTLE_ENDIAN);

                Log.i("endian test","data="+data[0]+"bb="+bb.array()[0]);

                out.write(bb.array());
                */
                 for (int i=0;i<len;i+=2)
                 {
                     byte tmp=data[i];
                     data[i]=data[i+1];
                     data[i+1]=tmp;
                 }
                 out.write(data);

            }
            in.close();
            out.close();
        } catch (IOException e) {
            Log.i("exception",e.toString());
            e.printStackTrace();
        }
        endtime=System.currentTimeMillis();
        Log.i("TO WAV TIME",""+(endtime-starttime));
    }
    /**
     * 加入wav檔案頭
     */
    private void writeWaveFileHeader(FileOutputStream out, long totalAudioLen,
                                     long totalDataLen, long longSampleRate, int channels, long byteRate)
            throws IOException {
        byte[] header = new byte[44];
        header[0] = 'R'; // RIFF/WAVE header
        header[1] = 'I';
        header[2] = 'F';
        header[3] = 'F';
        header[4] = (byte) (totalDataLen & 0xff);
        header[5] = (byte) ((totalDataLen >> 8) & 0xff);
        header[6] = (byte) ((totalDataLen >> 16) & 0xff);
        header[7] = (byte) ((totalDataLen >> 24) & 0xff);
        header[8] = 'W'; //WAVE
        header[9] = 'A';
        header[10] = 'V';
        header[11] = 'E';
        header[12] = 'f'; // 'fmt ' chunk
        header[13] = 'm';
        header[14] = 't';
        header[15] = ' ';
        header[16] = 16; // 4 bytes: size of 'fmt ' chunk
        header[17] = 0;
        header[18] = 0;
        header[19] = 0;
        header[20] = 1; // format = 1
        header[21] = 0;
        header[22] = (byte) channels;
        header[23] = 0;
        header[24] = (byte) (longSampleRate & 0xff);
        header[25] = (byte) ((longSampleRate >> 8) & 0xff);
        header[26] = (byte) ((longSampleRate >> 16) & 0xff);
        header[27] = (byte) ((longSampleRate >> 24) & 0xff);
        header[28] = (byte) (byteRate & 0xff);
        header[29] = (byte) ((byteRate >> 8) & 0xff);
        header[30] = (byte) ((byteRate >> 16) & 0xff);
        header[31] = (byte) ((byteRate >> 24) & 0xff);
        header[32] = (byte) (1 * 16 / 8); // block align
        header[33] = 0;
        header[34] = 16; // bits per sample
        header[35] = 0;
        header[36] = 'd'; //data
        header[37] = 'a';
        header[38] = 't';
        header[39] = 'a';
        header[40] = (byte) (totalAudioLen & 0xff);
        header[41] = (byte) ((totalAudioLen >> 8) & 0xff);
        header[42] = (byte) ((totalAudioLen >> 16) & 0xff);
        header[43] = (byte) ((totalAudioLen >> 24) & 0xff);
        out.write(header, 0, 44);
    }

}


