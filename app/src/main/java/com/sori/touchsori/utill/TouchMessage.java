//package com.sori.touchsori.utill;
//
//
//import android.app.AlarmManager;
//import android.app.Notification;
//import android.app.NotificationChannel;
//import android.app.NotificationManager;
//import android.app.PendingIntent;
//import android.content.Context;
//import android.content.Intent;
//import android.location.Address;
//import android.location.Geocoder;
//import android.location.Location;
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//import android.os.AsyncTask;
//import android.os.Build;
//import android.os.Handler;
//import android.os.Message;
//import android.support.v4.app.NotificationCompat;
//import android.telephony.TelephonyManager;
//import android.text.TextUtils;
//import android.view.Gravity;
//
//import com.sori.touchsori.SoriApplication;
//import com.sori.touchsori.receiver.EmergencyRecevier;
//
//import org.json.JSONObject;
//
//
//import java.io.BufferedReader;
//import java.io.DataOutputStream;
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStreamReader;
//import java.io.OutputStream;
//import java.net.HttpURLConnection;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.Iterator;
//import java.util.List;
//import java.util.Locale;
//
//import static android.content.Context.ALARM_SERVICE;
//
///**
// * Created by sgkim on 2017-03-24.
// */
//
//public class TouchMessage {
//    private static final String TAG = TouchMessage.class.getSimpleName();   // 디버그 태그
//    private Context mContext;                                               // 콘텍스트
//
//    private final int TOUCH_MESSAGE_TYPE_EMERGENCY = 1;                     // 메시지 타입 안심귀가
//    private final int TOUCH_MESSAGE_TYPE_MEDIA = 2;                         // 메시지 타입 영상메시지
//
//    private AudioRecord audioRecord;                                        // 오디오 레코드
//    private LocationInfo mLocation;                                         // 위치정보
//    private RecordThread mThread;                                           // 레코드 스레드
//
//    private String audioFile;                                               // 오디오 파일 경로
//    private String mediaFile;                                               // 이미지, 동영상 파일 경로
//    private int msg_type = 0;
//    private ArrayList<String> toNumbers = new ArrayList<>();                // 안심귀가 수신자
////    private DBHelper dbHelper;                                              // 데이터베이스
//
//    private SoriApplication mApp;                                      // 전역 (Application) 변수
//
//    public TouchMessage(Context context, int msgType, String media_file) {
//        // 콘텍스트
//        mContext = context.getApplicationContext();
//        // 전역 (Application) 변수
//        if (mApp == null) mApp = (SoriApplication) mContext;
//        // 메시지 타입
//        msg_type = msgType;
//        // 이미지 동영상 파일 경로
//        mediaFile = media_file;
//        // 위치정보
//        mLocation = LocationInfo.getInstance(mContext);
//
//        // 오디오 세팅 및 오디오 레코드 최기화
//        int SAMPLE_RATE = 44100;
//        int ENCODING = AudioFormat.ENCODING_PCM_16BIT;
//        int CHANNEL_MASK_STEREO = AudioFormat.CHANNEL_IN_STEREO;
//        int CHANNEL_MASK_MONO = AudioFormat.CHANNEL_IN_MONO;
//        int BUFFER_SIZE = 7168;
//
//        try {
//            if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
//                audioRecord = new AudioRecord(
//                        MediaRecorder.AudioSource.DEFAULT,
//                        SAMPLE_RATE, CHANNEL_MASK_MONO,
//                        ENCODING,
//                        BUFFER_SIZE);
//                LogUtil.d(TAG, " Build.VERSION_CODES.LOLLIPOP under Aduio Channel MONO!!!!!!!!!!!!!!");
//            }else {
//                int audioSource;
//
//                if( Build.MODEL.contains("SHV-E")) {
//                    audioSource =  MediaRecorder.AudioSource.DEFAULT;
//                } else if (Build.MODEL.contains("SH")) {
//                    audioSource = MediaRecorder.AudioSource.VOICE_CALL;
//                } else if (Build.MODEL.contains("SM-G") || Build.MODEL.contains("LG-F350") || Build.MODEL.contains("SHV-E")){
//                    audioSource =  MediaRecorder.AudioSource.MIC;
//                } else {
//                    audioSource = MediaRecorder.AudioSource.DEFAULT;
//                }
//
//                audioRecord = new AudioRecord(
//                        audioSource,
//                        SAMPLE_RATE,
//                        CHANNEL_MASK_STEREO,
//                        ENCODING,
//                        BUFFER_SIZE);
//
//                LogUtil.d(TAG, " Build.VERSION_CODES.LOLLIPOP under Aduio Channel STEREO!!!!!!!!!!!!!!");
//            }
//        } catch (Exception e) {
//            LogUtil.e(TAG, e.getMessage());
//        }
//
//        // TODO 오디오 레코드 정보 가져오기
////        audioRecord = findAudioRecord();
//
//    }
//
//    public TouchMessage(Context context, int msgType, String media_file, ArrayList<String> numbers) {
//        this(context, msgType, media_file);
//        toNumbers.addAll(numbers);
//    }
//
//    private final int RECORD_EVENT_START = 1;
//    private final int RECORD_EVENT_STOP = 3;
//    /**
//     * 메시지 생성
//     */
//    public void makeMessage() {
//        if (msg_type == TOUCH_MESSAGE_TYPE_MEDIA && mediaFile.contains(".mp4")) {
//            audioFile = "";
//            mRecordHandler.sendEmptyMessage(RECORD_EVENT_STOP);
//        } else {
//            mLocation.updateLocationInfo();
//            mRecordHandler.sendEmptyMessage(RECORD_EVENT_START);
//        }
//    }
//    private int time_count = 11;
//    private Handler mRecordHandler = new Handler() {
//        @Override
//        public void handleMessage(Message msg) {
//            switch (msg.what) {
//                case RECORD_EVENT_START:
//                    time_count = 10 + 1;
//                    audioRecord.startRecording();
//                    mThread.recordStart();
//                    this.sendEmptyMessage(RECORD_EVENT_UPDATE);
//                    break;
//                case RECORD_EVENT_UPDATE:
//                    time_count--;
//                    mApp.onToast(mContext.getString(R.string.msg_on_recording) + " " + time_count);
//                    if (time_count > 0) {
//                        this.sendEmptyMessageDelayed(RECORD_EVENT_UPDATE, 1000);
//                    } else {
//                        mThread.recordStop();
//                        mThread = null;
//                        mLocation.stopLocationInfo();
//                        this.sendEmptyMessage(RECORD_EVENT_STOP);
//                    }
//                    break;
//                case RECORD_EVENT_STOP: {
//                    if (msg_type == TOUCH_MESSAGE_TYPE_MEDIA && mediaFile.contains(".mp4")) {
//                        executeServerTask();
//                    }else {
//                        this.sendEmptyMessageDelayed(LOCATION_EVENT_STOP, 5000);
//                    }
//                    mApp.onToast(mContext.getString(R.string.progress_dialog_wait));
//                    break;
//                }
//                case LOCATION_EVENT_STOP: {
//                    executeServerTask();
//                    break;
//                }
//            }
//        }
//    };
//
//
//
//    /**
//     * 파일 업로드
//     *
//     * @param fileName
//     * @param paramName
//     */
//    public boolean writeFile(String fileName, String paramName, HashMap params) {
//        boolean isSuccess = false;
//        int FileIdx = fileName.lastIndexOf("/");
//        String strfileName = fileName.substring(FileIdx + 1, fileName.length());
//        FileInputStream mFileInputStream = null;
//        String lineEnd = "\r\n";
//        String twoHyphens = "--";
//        String boundary = "*****";
//
//        try {
//            mFileInputStream = new FileInputStream(fileName);
//        } catch (FileNotFoundException e) {
//            LogUtil.d(TAG, e.getMessage());
//        }
//
//        try {
//            dosvm.writeBytes(twoHyphens + boundary + lineEnd);
//            dosvm.writeBytes("Content-Disposition: form-data; name=\"" + paramName + "\";filename=\"" + strfileName + "\"" + lineEnd);
//            dosvm.writeBytes(lineEnd);
//            int bytesAvailable = mFileInputStream.available();
//            int maxBufferSize = 4096;
//
//            int bufferSize = Math.min(bytesAvailable, maxBufferSize);
//            byte[] buffer = new byte[bufferSize];
//            int bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//            while (bytesRead > 0) {
//                dosvm.write(buffer, 0, bufferSize);
//                bytesAvailable = mFileInputStream.available();
//                bufferSize = Math.min(bytesAvailable, maxBufferSize);
//                bytesRead = mFileInputStream.read(buffer, 0, bufferSize);
//            }
//            dosvm.writeBytes(lineEnd);
//            isSuccess = true;
//        } catch (IOException e) {
//            LogUtil.e(TAG, e.getMessage());
//        }
//
//        if("audio_file".equalsIgnoreCase(paramName)) {
//            if(null != params) {
//                Iterator<String> keys = params.keySet().iterator();
//                while (keys.hasNext()) {
//                    String key = keys.next();
//                    String value = (String) params.get(key);
//
//                    try {
//                        dosvm.writeBytes(twoHyphens + boundary + lineEnd);
//                        dosvm.writeBytes("Content-Disposition: form-data; name=\"" + key + "\"" + lineEnd);
//                        dosvm.writeBytes("Content-Type: text/plain" + lineEnd);
//                        dosvm.writeBytes(lineEnd);
//                        dosvm.writeBytes(value);
//                        dosvm.writeBytes(lineEnd);
//                    } catch (IOException e) {
//                        e.printStackTrace();
//                    }
//                }
//            }
//        }
//        return isSuccess;
//    }
//
//    /**
//     * 오디오 파일 삭제
//     *
//     * @param fileName
//     */
//    private void deletAudioFile(String fileName) {
//        if (TextUtils.isEmpty(fileName)) {
//            return;
//        }
//        File file = new File(fileName);
//        if (file.exists()) {
//            file.delete();
//        }
//    }
//
//    /**
//     * 주소 가져오기
//     *
//     * @param location
//     * @return
//     */
//    private String getAddress(Location location) {
//        String address = "";
//        if (location == null) {
//            return "";
//        }
//
//        double lat = location.getLatitude();
//        double lon = location.getLongitude();
//
//        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
//        try {
//            List<Address> addrs = geocoder.getFromLocation(lat, lon, 1);
//            String addr = addrs.get(0).getAddressLine(0);
//            if (!TextUtils.isEmpty(addr)) {
//                String rAddr = addr.replace("남한", "");
//                address = rAddr.replace("대한민국", "");
//            }
//        } catch (Exception e) {
//            address = "";
//        }
//        return address;
//    }
//
//
//    /**
//     * 오디오 레코드 설정
//     */
//    private static int[] mSampleRates = new int[] { 8000, 11025, 22050, 44100 };
//    public AudioRecord findAudioRecord() {
//        for (int sampleRate : mSampleRates) {
//            for (short encoding : new short[] { AudioFormat.ENCODING_PCM_8BIT, AudioFormat.ENCODING_PCM_16BIT }) {
//                for (short channelMask : new short[] { AudioFormat.CHANNEL_IN_MONO, AudioFormat.CHANNEL_IN_STEREO }) {
//                    try {
//                        int bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelMask, encoding);
//
//                        if (bufferSize != AudioRecord.ERROR_BAD_VALUE) {
//                            LogUtil.d(TAG, "findAudioRecord() -> sampleRate : " + sampleRate + ", encoding : " + encoding + ", channelMask : " + channelMask);
//                            LogUtil.d(TAG, "findAudioRecord() -> bufferSize : " + bufferSize);
//
//                            AudioRecord recorder = new AudioRecord(0, sampleRate, channelMask, encoding, bufferSize);
//
//                            if (recorder.getState() == AudioRecord.STATE_INITIALIZED) return recorder;
//                        }
//                    } catch (Exception e) {
//                        LogUtil.e(TAG, e.getMessage());
//                    }
//                }
//            }
//        }
//        return null;
//    }
//
//    /**
//     * 위치전송 알람 등록
//     */
//    private void registerLocationAlarm() {
//        LogUtil.i(TAG, "registerLocationAlarm() -> Start !!!");
//
//        Intent intent = new Intent(mContext, EmergencyRecevier.class);
//        intent.setAction(Define.ACTION_SEND_LOCATION);
//        PendingIntent pendingIntent = PendingIntent.getBroadcast(
//                mContext,
//                Define.ALARM_ID_SEND_LOCATION,
//                intent,
//                0);
//        long time = System.currentTimeMillis();
//        time += Define.LOCATION_SEND_TIME;
//
//        AlarmManager alarmManager = (AlarmManager) mContext.getSystemService(ALARM_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {               // 마시멜로우 (Ver.6.0 이상)
//            long triggerTime = System.currentTimeMillis() + Define.LOCATION_SEND_TIME;
//            AlarmManager.AlarmClockInfo info = new AlarmManager.AlarmClockInfo(triggerTime, pendingIntent);
//            alarmManager.setAlarmClock(info, pendingIntent);
//        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {   // 키켓 (Ver.4.4 이상)
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, time, pendingIntent);
//        } else {                                                            // 기타
//            alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);
//        }
//    }
//
//
//    /**
//     * LG U+인 경우 125바이트 이상을 SKT로 보내는 경우 문자 2개로 나뉘어 보내져
//     * 주소,위치정보등이 나뉘어져 전달되어 링크가 잘못되는 경우가 있음
//     * 125바이트 기준으로 두개의 메시지로 분리되도록 함
//     */
//    private StringBuilder UplusDivideLMS(StringBuilder sb) {
//        StringBuilder tmpSb = sb;
//
//        TelephonyManager tm = (TelephonyManager)mContext.getSystemService(Context.TELEPHONY_SERVICE);
//        String networkoper = tm.getNetworkOperatorName();
//        LogUtil.d(TAG, "networkoper = " + networkoper);
//
//        if(networkoper.equals("LG U+")) {                           // LGU+에서 SKT로 전송시 125바이트가 넘어가면, 문자가 2개로 보내져서 강제로 주소 다음문자에 map url 오도록 함
//            int length = Math.abs(sb.toString().getBytes().length - 125);
//            for(int i=0; i < length; i++) {
//                tmpSb.append(" ");
//            }
//        }
//
//        return tmpSb;
//    }
//}
