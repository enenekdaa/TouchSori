package com.sori.touchsori.utill;

/**
 * Created by sgkim on 2017-03-13.
 */

public class Define {
    public static final String ACTION_TOUCHSORI_SERVICE = "TouchService";  // 터치소리 서비스
    public static final String ACTION_CAMERA_NEW_PICTURE1 = "com.android.camera.NEW_PICTURE";                   // 이미지 추가
    public static final String ACTION_CAMERA_NEW_PICTURE2 = "android.hardware.action.NEW_PICTURE";              // 이미지 추가
    public static final String ACTION_CAMERA_NEW_VIDEO = "android.hardware.action.NEW_VIDEO";                   // 비디오 추가
    public static final String ACTION_WAKE_LOCK = "kr.co.innochal.ACTION_WAKE_LOCK";                            // WakeLock 리시버
    public static final String ACTION_PROVIDERS_CHANGED = "android.location.PROVIDERS_CHANGED";                 // GPS ON-OFF
    public static final String ACTION_EMERGENCY_TIME_START = "kr.co.innochal.ACTION_EMERGENCY_TIME_START";      // 안심귀가 시간 체크 시작
    public static final String ACTION_EMERGENCY_TIME_END = "kr.co.innochal.ACTION_EMERGENCY_TIME_END";          // 안심귀가 시간 체크 종료
    public static final String ACTION_SEND_LOCATION = "kr.co.innochal.ACTION_SEND_LOCATION";                    // 위치정보 전송
    public static final String ACTION_GYRO_SERVICE_STOP  = "kr.co.innochal.ACTION_GYRO_SERVICE_STOP";           // GYROSCOPE에 따른 서비스 종료
    public static final String ACTION_GYRO_EVENT_UPDATE = "kr.co.innochal.ACTION_GYRO_EVENT_UPDATE";            // Gyro Event Listener 리시버 register
    public static final String ACTION_MEDIA_STATUS = "kr.co.innochal.ACTION_MEDIA_STATUS";                      // 미디어 상태
    public static final String ACTION_FIND_FRIEND_UPDATE = "kr.co.innochal.ACTION_FIND_FRIEND_UPDATE";          // 친구찾기 상태 업데이트
    public static final String ACTION_SAFETY_ZONE_CHECK  = "kr.co.innochal.ACTION_SAFETY_ZONE_CHECK";           // 안심영역 체크

    public static final String MSG_SERVICE_ACTION_EMERGENCY = "kr.co.innochal.MSG_SERVICE_ACTION_EMERGENCY";    // 안심귀가 메시지
    public static final String MSG_SERVICE_ACTION_MEDIA = "kr.co.innochal.MSG_SERVICE_ACTION_MEDIA";            // 영상메시지 메시지
    public static final String MSG_SERVICE_ACTION_LOCATION = "kr.co.innochal.MSG_SERVICE_ACTION_LOCATION";      // 위치정보 메시지

    public static final String DEVELOPER_EMAIL = "daniel@innochal.co.kr";                                       // 개발자 이메일

    public static final String AUTH_TOKEN = "innochal001";                                                      // 서버 인증키

    public static final String AES_ENCRYPT_KEY = "Innochal20180219";                                            // 암호화키
    public static final String CUSTOMER_SERVICE_CENTER = "0269510277";                                          // 고객지원 센터 전화번호

    // URL
    public static final String SERVER_URL = "http://touchsori.com/";                                           // 서버 URL
//    public static final String SERVER_URL = "http://innochal.co.kr/";                                           // 서버 URL

    // TODO 테스트 URL
//    public static final String SERVER_URL = "http://192.168.123.11/";                                            // 서버 URL
//    public static final String SERVER_URL = "http://115.91.149.21/";                                            // 서버 URL
    public static final String URL_PRIVACY_POLICY = SERVER_URL + "?mode=privacy";                               // 개인정보 취급방침
    public static final String URL_SERIAL = "serial/serialManage";                                              // 시리얼 정보 URL
    public static final String URL_FCM_TOKEN_SEND = "fcm/regIdSave";                                            // FCM Token 전송 URL

    // 서용자 설정 키
    public final static String PREFER_APP_REG_INFO = "reg_info";                                                // 버튼 등록 여부
    public final static String PREFER_APP_REG_KEY = "reg_key";                                                  // 버튼 등록 키
    public final static String PREFER_EMERGENCY_SOUND = "emergency_sound";                                      // 사이렌
    public final static String PREFER_GCM_REGISTRATION_ID = "gcm_registration_id";                              // GCM Registration ID
    public final static String PREFER_HEADSET_PLUG = "headset_plug";                                            // 헤드셋 연결
    public final static String PREFER_POWER_DISCONNECTED = "power_disconnected";                                // 충전기 연결
    public static final String PREFER_IS_REGISTRATION = "is_registration";                                      // 시리얼 번호
    public static final String PREFER_TIME_START = "time_start";                                                // 안심귀가 시작 시간
    public static final String PREFER_TIME_END = "time_end";                                                    // 안심귀가 종료 시간
    public static final String PREFER_EMERGENCY_RECORDING_TIME = "emergency_recording_time";                    // 긴급상황 현장 녹음 시간
    public static final String PREFER_EMERGENCY_LOCATION_COUNT = "emergency_location_count";                    // 긴급상황 위치정보 발송 횟수
    public static final String PREFER_MESSAGE_RECORDING_TIME = "message_recording_time";                        // 영상메시지 음성 녹음 시간
    public static final String PREFER_INFORMATION_LIST = "information_list";                                    // 콜플러스 정보 리스트
    public static final String PREFER_EMERGENCY_TIME_SELETED_POPUP = "emergency_selected_popup";                // 안심귀가 설정체크 팝업 체크여부
    public static final String PREFER_REGIST_ACCESSIBILITY_PERMISSIONS = "regist_accessibility_permissions";    // 접근성 퍼미션 셋팅 여부
    public static final String PREFER_TUTORIAL_VIEW = "tutorial_view";                                          // 튜토리얼 보여져야 하는지 여부
    public static final String PREFER_FCM_TOKEN = "fcm_token";                                                  // FCM Toekn
    public static final String PREFER_EMERGENCY_SMS_COUNTRY = "emergency_sms_country";                          // 긴급 SMS 국가 설정
    public static final String PREFER_PHONE_NUMBER = "phone_number";                                            // 전화번호 가져올 수 없는 곳에서 사용자 설정 위함
    public static final String PREFER_PRIVACY_POLICY_AGREE = "privacy_policy_agree";                            // 개인정보보호정책 동의 여부
    public static final String PREFER_SAFETYZONE_LIST = "safety_zone_list";                                     // 안심영역 리스트


    // 서버 요청 키
    public final static String SEND_SERVER_REQUEST_EMERGENCY = "send_server_request_emergency";                 // 안심귀가
    public final static String SEND_SERVER_REQUEST_MEDIA = "send_server_request_media";                         // 영상메시지
    public final static String SEND_SERVER_REQUEST_ALARM_SOUND = "send_server_request_alarm_sound";             // 알람
    public final static String SEND_SERVER_REQUEST_FCM_TOKEN_SAVE = "send_server_request_fcm_token_save";       // FCM 저장

    // 터치소리 사운드 분석
    public static final String ACTION_SOUND_PARSER_RUNNING = "kr.co.innochal.ACTION_SOUND_PARSER_RUNNING";      // 사운드 파서 계속

    // 데이터 베이스
    public static String DB_PATH = "";                                                                          // DB 경로
    public static final String DB_NAME = "touchsori.sqlite";                                                    // DB 명칭

    // 터치소리 사운드 분석 결과
    public static final int SOUND_PARSE_RESULT_NONE = 0;                                                        // 소리분석 결과 없음
    public static final int SOUND_PARSE_RESULT_CANCEL = 1;                                                      // 소리분석 취소
    public static final int SOUND_PARSE_RESULT_BUTTON = 2;                                                      // 소리분석 결과 버튼 인식
    public static final int SOUND_PARSE_RESULT_NOISE = 3;                                                       // 소리분석 결과 잡음, 오류
    public static final int SOUND_PARSE_RESULT_BUTTON_READY = 4;                                                // 소리분석 결과 버튼 일부 인식

    // 터치소리 사운드 분석 타입
    public static final int PARSE_TYPE_NONE = 0;                                                                // 타입 없음
    public static final int PARSE_TYPE_EMERGENCY = 1;                                                           // 타입 안심귀가
    public static final int PARSE_TYPE_MEDIA = 2;                                                               // 타입 영상메시지
    public static final int PARSE_TYPE_CALL = 3;                                                                // 타입 SMS전송 (통화종료 이후)

    // 알람
    public static final int ALARM_ID_SOUNDER_PARSER = 1009;                                                     // 사운드 파서 알람 id
    public static final int ALARM_ID_WAKE_LOCK = 1001;                                                          // WakeLock 알람 id
    public static final int ALARM_ID_EMERGENCY_START = 1003;                                                    // 안심귀가 시작
    public static final int ALARM_ID_EMERGENCY_END = 1004;                                                      // 안심귀가 종료
    public static final int ALARM_ID_SEND_LOCATION = 1005;                                                      // 위치전송 알람 id
    public static final int ALARM_ID_STOP_LOCATION = 1006;                                                      // 위치전송 중지 알람 id
    public static final int ALARM_ID_GYRO_EMERGENCY_STOP = 1007;                                                // Gyroscope 값 변동이 없을 시 동작하는 알람 id
    public static final int ALARM_ID_GYRO_EVENT = 1008;                                                         // Gyroscope Listener register/unregister 알람 id
    public static final int ALARM_ID_MEDIA_STATUS = 1009;                                                       // 미디어 상태 알람 id
    public static final int ALARM_ID_SAFETY_ZONE_CHECK = 1010;

    // TODO 딜레이 타임
    public static final int DELAY_TIME_SERVICE_2000 = 2000;                                                     // 터치소리 서비스 딜레이 타임
    public static final int DELAY_TIME_SERVICE_2500 = 2500;                                                     // 터치소리 서비스 딜레이 타임
    public static final int DELAY_TIME_SERVICE_3500 = 3500;                                                     // 터치소리 서비스 딜레이 타임

    public static final int SOUND_PARSING_COUNT_5     = 5;                                                      // SoundParserTask Count
    public static final int SOUND_PARSING_COUNT_7     = 7;                                                      // SoundParserTask Count
    public static final int SOUND_PARSING_COUNT_10    = 10;                                                     // SoundParserTask Count

    public static final int LOCATION_UPATE_TIME = 15 * 1000;                                                    //위치정보 업데이트 타임
    public static final int LOCATION_SEND_TIME  = 5 * 60 *1000;                                                 // 위치정보 전송(반복시) 타임(5분)

    public static final int GYRO_STOP_SERVICE_TIME = 1 * 60 * 1000;                                             //Gyroscope값의 변동이 없는 경우 Service 종료시간
    public static final int GYRO_CHECK_TIME_1_SEC = 1 *  1000;                                                  //Gyroscope register/unregister 주기
    public static final int GYRO_CHECK_TIME_3_SEC = 3 * 1000;                                                   //Gyroscope register/unregister 주기

    public static final int AUDIO_CHECK_TIME        = 2 * 1000;                                                 // 이어폰 꼽혀 있을 때 audio 사용 중인지 체크
    public static final int MEDIA_BUTTON_CHECK_TIME = 1 * 1000;                                                 // 이어폰 꼽혀 있을 때 버튼 체크시간(2번연속 버튼, 3번연속 버튼 판단)

    public static final int SAFETY_ZONE_CHECK_TIME  = 5 * 60 * 1000;                                          // 안심영역 체크 시간
//    public static final int SAFETY_ZONE_CHECK_TIME  = 2 * 60 * 1000;                                            // 안심영역 체크 시간

    // 터치소리 액션
    public static final String TOUCH_ACTION_START = "kr.co.innochal.ACTION_START";                              // 터치소리 서비스 시작
    public static final String TOUCH_ACTION_EMERGNECY = "kr.co.innochal.ACTION_EMERGENCY";                      // 안심귀가
    public static final String TOUCH_ACTION_AFTER_CAMERA = "kr.co.innochal.ACTION_AFTER_CAMERA";                // 영상메시지 (카메라 촬영 이후)     // 부팅
    public static final String TOUCH_ACTION_AFTER_CALL = "kr.co.innochal.ACTION_AFTER_CALL";                    // SMS 전송 (통화 종료 이후)

    public static final String MONITOR_SERVICE_START = "kr.co.innochal.ACTION_MONOTOR_START";                    // 모니터 서비스 시작
    public static final String MONITOR_SERVICE_STOP = "kr.co.innochal.ACTION_MONOTOR_STOP";                      // 모니터 서비스 종료

    // 키
    public static final String KEY_TYPE = "type";                                                               // 타입
    public static final String KEY_URL = "url";                                                                 // URL
    public static final String KEY_AES = "aes";                                                                 // aes 사용여부

    // 다이얼 로그
    public static final String KEY_DIALOG_TYPE = "dialog_type";                                                 // 다이얼로그 타입
    public static final String KEY_DIALOG_TITLE = "dialog_title";                                               // 다이얼로그 타이틀
    public static final String KEY_DIALOG_MESSAGE = "dialog_message";                                           // 다이얼로그 메시지
    public static final String KEY_DIALOG_LIST_KEY = "dialog_list_key";                                         // 다이얼로그 리스트 키

    // 주소록 타입
    public static final int TYPE_CONTACTS_EMERGENCY = 0;                                                        // 주소록 타입 안심귀가
    public static final int TYPE_CONTACTS_IMAGEVIEW = 1;                                                        // 주소록 타입 이미지뷰
    public static final int TYPE_CONTACTS_FINDFRIENDS = 2;                                                      // 주소록 타입 친구찾기

    // 시간설정 타입
    public static final int TYPE_EMERGENCY_TIME_INSERT = 0;                                                     // 안심귀가 시간 추가
    public static final int TYPE_EMERGENCY_TIME_UPDATE = 1;                                                     // 안심귀가 시간 수정

    // 시리얼 번호
    public static final String KEY_AUTH_TOKEN = "auth_token";                                                   // 서버 인증키
    public static final String KEY_SERIAL_NUMBER = "serial_number";                                             // 시리얼 번호
    public static final String KEY_SAVED_SERIAL_NUMBER = "saved_serial_number";                                 // 저장된 시리얼 번호
    public static final String KEY_MODE = "mode";                                                               // 서버 요청 모드
    public static final String KEY_COUNTRY_CODE = "contry_code";                                                // 전화번호 국가코드
    public static final String KEY_UPDATE_DATE = "update_date";                                                 // 업데이트 날짜
    public static final String KEY_MODE_INSERT = "insert";                                                      // 입력 요청
    public static final String KEY_MODE_UPDATE = "update";                                                      // 업데이트 요청
    public static final String KEY_COUNTRY_ISO_KR = "kr";                                                       // 국가코드
    public static final String KEY_RESULT = "result";                                                           // 서버 통신 결과
    public static final String KEY_SAVED_HP = "saved_hp";                                                       // 서버에 저장된 휴대 전화번호
    public static final String KEY_NEW_HP = "new_hp";                                                           // 새로 등록할 전화번호

    // 버전
    public static final String KEY_VERSION_NAME = "version_name";                                               // 버전명
    public static final String KEY_VERSION_CODE = "version_code";                                               // 버전 코드

    public static final String KEY_TIME_START = "time_start";                                                   // 시작시간
    public static final String KEY_TIME_END = "time_end";                                                       // 종료시간

    // 주소록
    public static final String KEY_ID = "_id";                                                                  // 아이디
    public static final String KEY_NAME = "name";                                                               // 이름
    public static final String KEY_HP = "hp";                                                                   // 휴대전화번호
    public static final String KEY_EMAIL = "email";                                                             // 이메일
    public static final String KEY_SELECTED = "selected";                                                       // 선택여부
    public static final String KEY_NEW = "new";                                                                 // 새로 추가여부

    // 영상메시지
    public static final String KEY_DATE = "date";                                                               // 날짜
    public static final String KEY_FILE_PATH = "file_path";                                                     // 파일 경로 (이미지, 동영상)
    public static final String KEY_VOICE_PATH = "voice_path";                                                   // 음성파일 경로
    public static final String KEY_ADDRESS = "address";                                                         // 주소
    public static final String KEY_LATITUDE = "latitude";                                                       // 위도
    public static final String KEY_LONGITUDE = "longitude";                                                     // 경도
    public static final String KEY_CLICKED = "clicked";                                                         // 클릭여부
    public static final String KEY_AUDIO_URL = "audioUrl";                                                      // 음성녹음 파일 URL

    public static final String GOOGLE_AIP_KEY = "AIzaSyDRqNzUJxG0JPTS34P6_SaddfPxF1IpbK8";                      // Google API Key

    // GCM 상태 플래그
    public static final String SENDER_ID = "699829707104";                                                      // GCM Sender ID
    public static final String REGISTRATION_READY = "registration_ready";                                       // 준비
    public static final String REGISTRATION_GENERATING = "registration_generating";                             // 생성
    public static final String REGISTRATION_COMPLETE = "registration_complete";                                 // 완료

    // 통화 종료 후 메시지 전송
    public static final int AFTER_CALL_LOCATION = 1;                                                            // 위치
    public static final int AFTER_CALL_IMAGE = 2;                                                               // 이미지
    public static final int AFTER_CALL_MOVIE = 3;                                                               // 동영상
    public static final int AFTER_CALL_SETUP = 4;                                                               // 설정정보
    public static final int AFTER_CALL_ETC = 5;                                                                 // 기타

    public static final String PACKAGE_NAME_KAKAOTALK = "com.kakao.talk";                                       // 카카오톡 패키지명
    public static final String PACKAGE_NAME_BAND = "com.nhn.android.band";                                      // 밴드 패키지명
    public static final String PACKAGE_NAME_FACEBOOK = "com.facebook.katana";                                   // 페이스북 패키지명

    // 콜플러스
    public static final String KEY_CALL_PLUS_NAME = "1";                                                        // 이름
    public static final String KEY_CALL_PLUS_COMPANY = "2";                                                     // 회사명/학교명
    public static final String KEY_CALL_PLUS_LEVEL = "3";                                                       // 직함
    public static final String KEY_CALL_PLUS_ADDRESS = "4";                                                     // 주소
    public static final String KEY_CALL_PLUS_EMAIL = "5";                                                       // 이메일
    public static final String KEY_CALL_PLUS_FAX = "6";                                                         // 팩스
    public static final String KEY_CALL_PLUS_PHONE = "7";                                                       // 유선 전화번호
    public static final String KEY_CALL_PLUS_ETC = "8";                                                         // 기타
    public static final String KEY_CALL_PLUS_DATA = "data";                                                     // 콜백플러스 데이터

    public static final String NOTIFICATION_ID_FOREGROUND_GROUP_KEY = "9999";                                   // Foreground Service Notification group key
    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE = 9999;                                          // Foreground Service Notification id
    public static final int NOTIFICATION_ID_REQUEST_FRIEND = 5000;                                              // 친구 요청 Notification id
    public static final int NOTIFICATION_ID_REQUEST_APPROVAL = 5001;                                            // 친구 요청 수락 거절 Notification id
    public static final int NOTIFICATION_ID_ALERT_SAFTY_ZONE = 5002;                                            // 안심영역 벗어나거나 들어왔을 때 Notification id
    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE_MONOTOR = 9998;                                  // 모니터 서비스 Notification id
    public static final int NOTIFICATION_ID_FOREGROUND_SERVICE_LOCATION = 9997;                                 // 위치정보 전송 (5회) Notification id



    //FCM
    public static final String KEY_TEL = "tel";                                                                 // FCM param key : 전화번호
    public static final String KEY_REG_ID = "reg_id";                                                           // FCM param key : fcm token

    public static final int TOUCH_SERVICE_TYPE_END = 0;                                                         //
    public static final int TOUCH_SERVICE_TYPE_START = 1;
    public static final int TOUCH_SERVICE_TYPE_STOP = 2;
    public static final int TOUCH_SERVICE_TYPE_LOCATION = 3;
   // 친구찾기
    public static final String KEY_SENDER_HP = "sender_hp";                                                     // 발신자 휴대 전화번호 (2017-11-03)
    public static final String KEY_RECEIVER_HP = "receiver_hp";                                                 // 수신자 휴대 전화번호 (2017-11-03)
    public static final String KEY_STATUS = "status";                                                           // 친구 요청 승인 여부 0:요청,대기 1:승인 2:거절 3:해제 (2017-11-03)
    public static final String KEY_SENDER = "sender";                                                           // 발신자 이름 (2017-11-03)
    public static final String KEY_SENDER_NAME = "sender_name";                                                 // 발신자 이름 (2017-11-03)
    public static final String KEY_RECEIVER_NAME = "receiver_name";                                             // 수신자 이름 (2017-11-03)
    public static final String KEY_SEQ = "seq";                                                                 // 시퀀스 넘버 (2017-11-03)

    // 국가 코드
    public static final String KEY_COUNTRY_NAME = "country_name";                                               //국가 명
    public static final String KEY_COUNTRY_ISO_CODE = "country_iso_code";                                       //국가 코드

    // 안심 영역
    public static final int SAFETY_ZONE_UNIT  = 500;                                                            // 안심영역 단위 500m
    public static final String KEY_RADIUS = "radius";                                                           // 안심영역 반경
    public static final String KEY_IS_FIRST = "is_first";                                                       // 안심영역 설정 처음 호출인지..
}