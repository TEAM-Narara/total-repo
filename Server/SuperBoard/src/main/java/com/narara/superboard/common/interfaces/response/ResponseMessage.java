package com.narara.superboard.common.interfaces.response;

public class ResponseMessage {
    public static final String CREATED_USER = "회원 가입 성공";
    public static final String BAD_REQUEST_TERMS = "회원약관 요청 에러";
    public static final String DUPLICATE_EMAIL = "이메일 중복 에러";
    public static final String VALIDATION_ERROR = "이메일 유효성 에러";
    public static final String SEND_EMAIL_AUTHENTICATION_CODE = "이메일 인증코드 전송 성공";
    public static final String EMAIL_AUTHENTICATION_SUCCESS = "이메일 인증 성공";
    public static final String LOGIN_SUCCESS = "로그인 성공";
    public static final String LOGOUT_SUCCESS = "로그아웃 성공";
    public static final String NAVER_LOGIN_SUCCESS = "네이버 로그인 성공";
    public static final String LOGIN_FAIL = "로그인 실패";
    public static final String FIND_PASSWORD = "비밀번호 찾기 성공";

    public static final String CHANGE_PASSWORD = "비밀번호 변경 성공";
    public static final String READ_USER = "회원 정보 조회 성공";
    public static final String SEARCH_USERS = "회원 검색 성공";
    public static final String REISSUE_ACCESSTOKEN = "토큰 재발급 성공";
    public static final String UNAUTHORIZED = "회원 인증 실패";
    public static final String FORBIDDEN = "회원 권한 실패";
    public static final String NOT_FOUND_USER = "회원을 찾을 수 없습니다.";

    public static final String UPDATE_USER = "회원 정보 수정 성공";
    public static final String WITHDRAWAL_USER = "회원 탈퇴 성공";
    public static final String INTERNAL_SERVER_ERROR = "서버 내부 에러";
    public static final String DB_ERROR = "데이터베이스 에러";
}
