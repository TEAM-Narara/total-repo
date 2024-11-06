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


    // 워크스페이스 관련 상수
    public static final String WORKSPACE_CREATE_SUCCESS = "워크스페이스 생성 성공";
    public static final String WORKSPACE_UPDATE_SUCCESS = "워크스페이스 수정 성공";
    public static final String WORKSPACE_DELETE_SUCCESS = "워크스페이스 삭제 성공";

    // 워크스페이스 멤버 관련 상수
    public static final String WORKSPACE_MEMBER_FETCH_SUCCESS = "워크스페이스 멤버 조회 성공";
    public static final String MEMBER_WORKSPACE_LIST_FETCH_SUCCESS = "멤버의 워크스페이스 리스트 조회 성공";

    // 보드 관련 상수
    public static final String BOARD_CREATE_SUCCESS = "보드 생성 성공";
    public static final String BOARD_ADMIN_UPDATE_SUCCESS = "어드민의 보드 수정 성공";
    public static final String BOARD_DELETE_SUCCESS = "보드 삭제 성공";
    public static final String BOARD_FETCH_SUCCESS = "보드 조회 성공";
    public static final String BOARD_MEMBER_UPDATE_SUCCESS = "사용자의 보드 설정 업데이트 성공";
    public static final String BOARD_ARCHIVED_FETCH_SUCCESS = "아카이브된 보드 목록 조회 성공";
    public static final String BOARD_ARCHIVE_STATUS_CHANGED = "보드 아카이브 상태 변경 성공";

    // 보드 멤버 관련 상수
    public static final String BOARD_MEMBER_CREATE_SUCCESS = "보드 멤버 생성 성공";
    public static final String BOARD_MEMBER_FETCH_SUCCESS = "보드 멤버 조회 성공";
    public static final String BOARD_MEMBER_WATCH_STATUS_SUCCESS = "보드 멤버 알림 상태 조회 성공";
    public static final String BOARD_MEMBER_WATCH_STATUS_UPDATE_SUCCESS = "보드 멤버 알림 상태 업데이트 성공";
    public static final String BOARD_MEMBER_AUTHORITY_UPDATE_SUCCESS = "보드 멤버 권한 수정 성공";

    // 리스트 관련 상수
    public static final String LIST_CREATE_SUCCESS = "리스트 생성 성공";
    public static final String LIST_UPDATE_SUCCESS = "리스트 수정 성공";
    public static final String LIST_DELETE_SUCCESS = "리스트 삭제 성공";
    public static final String LIST_MOVE_SUCCESS = "리스트 이동 성공";
    public static final String LIST_ARCHIVE_CHANGE_SUCCESS = "아카이브된 리스트 목록 조회 성공";
    public static final String LIST_GET_ARCHIVED_SUCCESS = "리스트 아카이브 상태 변경 성공";

    // 카드 관련 상수
    public static final String CARD_CREATE_SUCCESS = "카드 생성 성공";
    public static final String CARD_UPDATE_SUCCESS = "카드 수정 성공";
    public static final String CARD_DELETE_SUCCESS = "카드 삭제 성공";
    public static final String CARD_MOVE_SUCCESS = "카드 이동 성공";
    public static final String ARCHIVED_CARD_LIST_SUCCESS = "아카이브된 카드 목록 조회 성공";
    public static final String CARD_ARCHIVE_STATUS_CHANGE_SUCCESS = "카드 아카이브 상태 변경 성공";
    // 댓글 관련 상수
    public static final String REPLY_CREATE_SUCCESS = "댓글 생성 성공";
    public static final String REPLY_UPDATE_SUCCESS = "댓글 수정 성공";
    public static final String REPLY_DELETE_SUCCESS = "댓글 삭제 성공";

}
