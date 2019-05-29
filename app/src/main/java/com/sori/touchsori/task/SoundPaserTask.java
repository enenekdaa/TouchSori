package com.sori.touchsori.task;


import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.AsyncTask;
import android.os.Build;

import com.sori.touchsori.SoriApplication;
import com.sori.touchsori.callback.CallbackSound;
import com.sori.touchsori.utill.Define;
import com.sori.touchsori.utill.EtcUtil;
import com.sori.touchsori.utill.LogUtil;
import com.sori.touchsori.utill.NFD2;


/**
 * Created by Dongnam on 2017. 4. 28..
 */

public class SoundPaserTask extends AsyncTask<Integer, Void, Integer> {
    private static final String TAG = SoundPaserTask.class.getSimpleName();     // 디버그 태그

    private Context mContext;                                                   // 콘텍스트
    private SoriApplication mApp;                                          // 전역 (Application) 변수

    private AudioRecord audioRecord;                                            // 오디오 레코드

    // 오디오 세팅
    private static final int AUDIO_SOURCE = MediaRecorder.AudioSource.DEFAULT;
    private static final int SAMPLE_RATE = 44100;
    private static final int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
    private static final int CHANNEL_MASK = AudioFormat.CHANNEL_IN_MONO;
    private static final int BUFFER_SIZE = AudioRecord.getMinBufferSize(SAMPLE_RATE, CHANNEL_MASK, ENCODING);   // 버퍼

    private CallbackSound callbackSound;            // 사운드 리시버

    private NFD2 trf;                               // 음성분석기
    private static int rDat;                        // 오디오 레코드 결과 (1024)
    private static short[] bf = new short[1024];    // 소리 버퍼
    private static double[] ttf = new double[1024]; // 소리 주파수 버퍼

    private static int freq1 = 0;
    private static int freq2 = 0;
    private static int delayTime = 135;

    // 버튼별 주파수 설정
    private void checkFrequency(int type) {
        LogUtil.d(TAG, "checkFrequency() -> buttonTpye : " + type);

        switch (type) {
            case -1: //17-17
                freq1=1000;
                freq2=1000;
                break;
            case 0: //16-17
                freq1=722;
                freq2=768;
                break;

            case 1: //16-18
                freq1=722;
                freq2=815;
                break;
            case 2: //16-19
                freq1=722;
                freq2=861;
                break;
            case 3: //16-20
                freq1=722;
                freq2=907;
                break;
            case 4: //17-18
                freq1=764;
                freq2=811;
                break;
            case 5: //17-19
                freq1=764;
                freq2=857;
                break;
            case 6: //17-20
                freq1=764;
                freq2=904;
                break;
            case 7: //18-19
                freq1=811;
                freq2=857;
                break;
            case 8: //18-20
                freq1=811;
                freq2=904;
                break;
            case 9: //19-20
                freq1=857;
                freq2=904;
                break;
        }
    }




    public SoundPaserTask(CallbackSound callbackSound, Context context) {
        this.mContext = context;
        this.callbackSound = callbackSound;
    }

    @Override
    protected void onPreExecute() { super.onPreExecute(); }

    @Override
    protected Integer doInBackground(Integer... params) {
        int parseType = params[0];
        LogUtil.d(TAG, "doInBackground() -> parseType : " + parseType);
        if (parseType == -1) return Define.SOUND_PARSE_RESULT_NONE;

        if (mApp == null) mApp = (SoriApplication) mContext;


        LogUtil.d(TAG, "doInBackground() -> callbackSound : " + callbackSound);
        if (null == callbackSound) {

            return Define.SOUND_PARSE_RESULT_NONE;
        }

        LogUtil.d(TAG, "doInBackground() -> mApp.getIsSoundParserStop() : " + mApp.getIsSoundParserStop());
        LogUtil.d(TAG, "doInBackground() -> mApp.getIsServiceStop() : " + mApp.getIsServiceStop());

        if (mApp.getIsSoundParserStop() == true) return Define.SOUND_PARSE_RESULT_NONE;
        if (mApp.getIsServiceStop() == true) return Define.SOUND_PARSE_RESULT_NONE;


        // 주파수 확인
        checkFrequency(parseType);

        // 음성분석기 초기화
        if (trf == null) trf = new NFD2();

        try {
            // 오디오 객체 생성
            if (audioRecord == null) {
                if (Build.MODEL.contains("LG")) {
                    if(Build.MODEL.contains("LG-F4330")) {
                        audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_MASK, ENCODING, BUFFER_SIZE);
                    }else {
                        audioRecord = new AudioRecord(5, SAMPLE_RATE, CHANNEL_MASK, 1, BUFFER_SIZE);
                    }
                } else {
                    audioRecord = new AudioRecord(AUDIO_SOURCE, SAMPLE_RATE, CHANNEL_MASK, ENCODING, BUFFER_SIZE);
                }


                if (audioRecord == null) {
                    LogUtil.e(TAG, "audioRecord is null");
                    return Define.SOUND_PARSE_RESULT_NONE;
                }
            }
        } catch (Exception e) {
            LogUtil.d(TAG, e.getMessage());
//            LogUtil.e(TAG, "getState() -> initialized : " + String.valueOf(audioRecord.getState()));
            return Define.SOUND_PARSE_RESULT_NOISE;
        }

        // 오디오 레코드 시작
        try {
            audioRecord.startRecording();
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
//            LogUtil.e(TAG, "getState() -> startRecording : " + String.valueOf(audioRecord.getState()));
            return Define.SOUND_PARSE_RESULT_NOISE;
        }
        boolean isRunning = true;   // 음성 레코딩 플래그

        int cntDetect = 0;          // 터치소리 주파수 디텍트 카운트
        int circularIdx = 0;        // 반복 인텍스
        int startIdx = 0;           // 시작 인텍스
        int parseCount = Define.SOUND_PARSING_COUNT_7;        // 파싱 카운트

        if(EtcUtil.isSoundParserCountDownDevice()) {                        // 24시인 경우
            parseCount = Define.SOUND_PARSING_COUNT_5;
        }

//        if(mApp.getConfig().getTimeStart() == 0 && mApp.getConfig().getTimeEnd() == 0 ) {           // 24시인 경우
//            if(EtcUtil.isSoundParserCountDownDevice()) {
//                parseCount = Define.SOUND_PARSING_COUNT_5;
//            }
//        }else  {
//            parseCount = Define.SOUND_PARSING_COUNT_10;
//        }

        boolean chkN1 = false;
        boolean chkN2 = false;
        boolean isRetry = false;

        while (isRunning) {
            timeSleep(delayTime);
            try {
                rDat = audioRecord.read(bf, 0, 1024);
            } catch (Exception e) {
                LogUtil.d(TAG, e.getMessage());
//                LogUtil.e(TAG, "getState() -> startRecording : " + String.valueOf(audioRecord.getState()));
                return Define.SOUND_PARSE_RESULT_NOISE;
            }

            for (int i = 0; i < 1024 && i < rDat; i++) {
                ttf[i] = (double) bf[i] / 32768;
            }

            trf.ft(ttf);
            int isNullValue = Double.compare(ttf[0], 0.0);
            if (isNullValue == 0) continue;

            circularIdx++;

            for (int i = 0; i < 1024; i++) {
                ttf[i] = ttf[i] * ttf[i];
            }

            int i;
            int peakBase1=0;
            int peakBase2=0;
            int cntPeak1=0;
            int cntPeak2=0;
            double peak1=0.;
            double peak2=0.;

            for (i=700; i<1024; i++) {
                if (i>=(freq1-10) && i<=(freq1+10)) {
                    if (peak1<ttf[i] && ttf[i]>0.1) {
                        peak1=ttf[i];
                        peakBase1=i;
                    }
                }
                if (i>=(freq2-10) && i<=(freq2+10)) {
                    if (peak2<ttf[i] && ttf[i]>0.1) {
                        peak2=ttf[i];
                        peakBase2=i;
                    }
                }
            }

//            if (peak1>0 && peak2>0) LogUtil.d(TAG,"--------index of peak1 is " + peakBase1 + ":" + peak1 + ", index of peak2 is " + peakBase2 + peakBase2":" + peak2 );

            if (peakBase1>(freq1-10) && peakBase2>(freq2-10)) {
//                String sOut = "";
//                for(i=20;i>0;i--){
//                    sOut += ttf[peakBase2-i]/peak2+",";
//                }
//                for(i=0;i<20;i++){
//                    sOut += ttf[peakBase2+i]/peak2+",";
//                }
//                sOut += ttf[peakBase2+i]/peak2;
//
//                LogUtil.d(TAG, "doInBackground() -> " + sOut);

                int tmpCnt=0;

                if (!chkN1) {
                    if (ttf[peakBase1 - 2] > peak1 * 0.00 && ttf[peakBase1 - 2] < peak1 * 0.95)
                        tmpCnt++;
                    if (ttf[peakBase1 - 1] > peak1 * 0.00 && ttf[peakBase1 - 1] < peak1 * 0.95)
                        tmpCnt++;
                    if (ttf[peakBase1 + 1] > peak1 * 0.00 && ttf[peakBase1 + 1] < peak1 * 0.95)
                        tmpCnt++;
                    if (ttf[peakBase1 + 2] > peak1 * 0.00 && ttf[peakBase1 + 2] < peak1 * 0.95)
                        tmpCnt++;

                    if (tmpCnt == 4) cntPeak1++;

                    tmpCnt = 0;
                    if (ttf[peakBase1 - 4] > peak1 * 0.00 && ttf[peakBase1 - 4] < peak1 * 0.70)
                        tmpCnt++;
                    if (ttf[peakBase1 - 3] > peak1 * 0.00 && ttf[peakBase1 - 3] < peak1 * 0.70)
                        tmpCnt++;
                    if (ttf[peakBase1 + 3] > peak1 * 0.00 && ttf[peakBase1 + 3] < peak1 * 0.70)
                        tmpCnt++;
                    if (ttf[peakBase1 + 4] > peak1 * 0.00 && ttf[peakBase1 + 4] < peak1 * 0.70)
                        tmpCnt++;

                    if (tmpCnt == 4) cntPeak1++;

                    tmpCnt = 0;
                    if (ttf[peakBase1 - 8] > peak1 * 0.00 && ttf[peakBase1 - 8] < peak1 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase1 - 7] > peak1 * 0.00 && ttf[peakBase1 - 7] < peak1 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase1 - 6] > peak1 * 0.00 && ttf[peakBase1 - 6] < peak1 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase1 - 5] > peak1 * 0.00 && ttf[peakBase1 - 5] < peak1 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase1 + 5] > peak1 * 0.00 && ttf[peakBase1 + 5] < peak1 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase1 + 6] > peak1 * 0.00 && ttf[peakBase1 + 6] < peak1 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase1 + 7] > peak1 * 0.00 && ttf[peakBase1 + 7] < peak1 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase1 + 8] > peak1 * 0.00 && ttf[peakBase1 + 8] < peak1 * 0.33)
                        tmpCnt++;

                    if (tmpCnt == 8) cntPeak1++;

                    tmpCnt = 0;
                    if (ttf[peakBase1 - 10] > peak1 * 0.00 && ttf[peakBase1 - 10] < peak1 * 0.07)
                        tmpCnt++;
                    if (ttf[peakBase1 - 9] > peak1 * 0.00 && ttf[peakBase1 - 9] < peak1 * 0.07)
                        tmpCnt++;
                    if (ttf[peakBase1 + 9] > peak1 * 0.00 && ttf[peakBase1 + 9] < peak1 * 0.07)
                        tmpCnt++;
                    if (ttf[peakBase1 + 10] > peak1 * 0.00 && ttf[peakBase1 + 10] < peak1 * 0.07)
                        tmpCnt++;

                    if (tmpCnt == 4) cntPeak1++;

                    if (cntPeak1 == 4) {
                        chkN1 = true;
                        LogUtil.d(TAG, "***************************** Peak1 Passed *****************************");

                    }
                }

                if (!chkN2) {
                    tmpCnt = 0;
                    if (ttf[peakBase2 - 2] > peak2 * 0.00 && ttf[peakBase2 - 2] < peak2 * 0.95)
                        tmpCnt++;
                    if (ttf[peakBase2 - 1] > peak2 * 0.00 && ttf[peakBase2 - 1] < peak2 * 0.95)
                        tmpCnt++;
                    if (ttf[peakBase2 + 1] > peak2 * 0.00 && ttf[peakBase2 + 1] < peak2 * 0.95)
                        tmpCnt++;
                    if (ttf[peakBase2 + 2] > peak2 * 0.00 && ttf[peakBase2 + 2] < peak2 * 0.95)
                        tmpCnt++;

                    if (tmpCnt == 4) cntPeak2++;

                    tmpCnt = 0;
                    if (ttf[peakBase2 - 4] > peak2 * 0.00 && ttf[peakBase2 - 4] < peak2 * 0.70)
                        tmpCnt++;
                    if (ttf[peakBase2 - 3] > peak2 * 0.00 && ttf[peakBase2 - 3] < peak2 * 0.70)
                        tmpCnt++;
                    if (ttf[peakBase2 + 3] > peak2 * 0.00 && ttf[peakBase2 + 3] < peak2 * 0.70)
                        tmpCnt++;
                    if (ttf[peakBase2 + 4] > peak2 * 0.00 && ttf[peakBase2 + 4] < peak2 * 0.70)
                        tmpCnt++;

                    if (tmpCnt == 4) cntPeak2++;

                    tmpCnt = 0;
                    if (ttf[peakBase2 - 8] > peak2 * 0.00 && ttf[peakBase2 - 8] < peak2 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase2 - 7] > peak2 * 0.00 && ttf[peakBase2 - 7] < peak2 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase2 - 6] > peak2 * 0.00 && ttf[peakBase2 - 6] < peak2 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase2 - 5] > peak2 * 0.00 && ttf[peakBase2 - 5] < peak2 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase2 + 5] > peak2 * 0.00 && ttf[peakBase2 + 5] < peak2 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase2 + 6] > peak2 * 0.00 && ttf[peakBase2 + 6] < peak2 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase2 + 7] > peak2 * 0.00 && ttf[peakBase2 + 7] < peak2 * 0.33)
                        tmpCnt++;
                    if (ttf[peakBase2 + 8] > peak2 * 0.00 && ttf[peakBase2 + 8] < peak2 * 0.33)
                        tmpCnt++;

                    if (tmpCnt == 8) cntPeak2++;

                    tmpCnt = 0;
                    if (ttf[peakBase2 - 10] > peak2 * 0.00 && ttf[peakBase2 - 10] < peak2 * 0.07)
                        tmpCnt++;
                    if (ttf[peakBase2 - 9] > peak2 * 0.00 && ttf[peakBase2 - 9] < peak2 * 0.07)
                        tmpCnt++;
                    if (ttf[peakBase2 + 9] > peak2 * 0.00 && ttf[peakBase2 + 9] < peak2 * 0.07)
                        tmpCnt++;
                    if (ttf[peakBase2 + 10] > peak2 * 0.00 && ttf[peakBase2 + 10] < peak2 * 0.07)
                        tmpCnt++;

                    if (tmpCnt == 4) cntPeak2++;

                    if (cntPeak2 == 4) {
                        LogUtil.d(TAG, "***************************** Peak2 Passed *****************************");
                        chkN2 = true;

                    }
                }
            }
//            LogUtil.d(TAG,"#############freq1:" + freq1 + " freq2:" + freq2);

//            if (circularIdx-startIdx>countParser) {
//            if (circularIdx-startIdx>20) {
            if (circularIdx-startIdx>parseCount) {
                LogUtil.d(TAG,"############# Reset Values -> circularIdx : " + circularIdx + ", startIdx : " + circularIdx + ", parseCount : " + parseCount);

                circularIdx=0;
                startIdx=0;
                cntDetect=0;
            }

            if (chkN1 && chkN2) {
                chkN1 = false;
                chkN2 = false;
                cntDetect++;
//                FileUtil.writeLog(mContext, TAG, "cntDetect : " + cntDetect);
                if (startIdx == 0) startIdx = circularIdx;

                // TODO 주파수 디텍트 카운트
                if (cntDetect>=3) {
                    LogUtil.d(TAG, "############# cntDetect : " + cntDetect + " #############");
                    LogUtil.d(TAG, "############# circularIdx : " + circularIdx + " #############");

                    return Define.SOUND_PARSE_RESULT_BUTTON;
                }
            }

//            if (circularIdx % countParser == 0) {
//            if (circularIdx%20==0 && cntDetect == 0) {
            if (circularIdx%parseCount==0) {
                LogUtil.d(TAG, "############# circularIdx : " + circularIdx + ", parseCount : " + parseCount + " #############");

                if (cntDetect == 0) {
                    return Define.SOUND_PARSE_RESULT_NONE;
                } else {
                    LogUtil.d(TAG, "############# cntDetect : " + cntDetect + " #############");

                    if (!isRetry) {
                        parseCount = parseCount*3;
                        isRetry = true;
                    } else {
                        return Define.SOUND_PARSE_RESULT_BUTTON_READY;
                    }
                }
            }
        }

        // 버튼 일부 인식
        if (cntDetect > 0) {
            LogUtil.d(TAG, "############# circularIdx : " + circularIdx + " #############");
            LogUtil.d(TAG, "############# cntDetect : " + cntDetect + " #############");

            return Define.SOUND_PARSE_RESULT_BUTTON_READY;
        }
        LogUtil.d(TAG, "############# cntDetect : " + cntDetect + " #############");
        LogUtil.d(TAG, "############# circularIdx : " + circularIdx + " #############");
        return Define.SOUND_PARSE_RESULT_NOISE;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        LogUtil.d(TAG, "onPostExecute() -> result : " + result);

        // 오디오 객체 해제
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }

        // 사운드 파싱 결과 콜백
        if (callbackSound != null) callbackSound.OnSoundParseComplete(result);
    }

    @Override
    protected void onCancelled() {
        // 오디오 객체 해제
        if (audioRecord != null) {
            audioRecord.stop();
            audioRecord.release();
            audioRecord = null;
        }
        // 콜백 초기화
        if (callbackSound != null) callbackSound = null;
        super.onCancelled();
    }

    /**
     * 슬림타임
     *
     * @param milliseconds
     */
    private void timeSleep(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
        } catch (Exception e) {
            LogUtil.e(TAG, e.getMessage());
        }
    }
}