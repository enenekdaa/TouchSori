package com.sori.touchsori.utill;

public class ErrorCode {
	public static final int ERROR_NONE 					= 0;	// 에러 없음, 성공

	// 시리얼 번호
	public static final int ERROR_EMPTY_SERIAL			= 1000;	// 시리얼 번호 공백
	public static final int ERROR_LENGTH_SERIAL			= 1001;	// 시리얼 번호 길이 오류
	public static final int ERROR_INVALID_SERIAL		= 1002;	// 시리얼 번호 형식 오류

	// 안심 귀가 서비스 기능설정
	public static final int ERROR_EMPTY_TIME_START		= 2000;	// 시작 시간 공백
	public static final int ERROR_EMPTY_TIME_END		= 2001;	// 도착 시간 공백
	public static final int ERROR_INVALID_TIMES			= 2002;	// 시간 설정 오류 (시작시간이 도착시간보다 늦음, 도착시간이 시작시간보다 빠름)
	public static final int ERROR_SAME_TIMES			= 2003;	// 시간 설정 오류 (시간기간과 도착 시간이 같음)
	public static final int ERROR_EMPTY_RECEIVERS 		= 2004;	// 수신자 공백
	public static final int ERROR_LENGTH_SHORT_SERIAL	= 2005;	// 6자리 시리얼
}