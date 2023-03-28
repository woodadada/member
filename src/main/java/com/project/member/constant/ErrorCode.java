package com.project.member.constant;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@AllArgsConstructor
public enum ErrorCode {
    /* 400 BAD_REQUEST : 잘못된 요청 */
    VALIDATION_EXCEPTION(BAD_REQUEST, "입력 값이 잘못되었습니다."),
    INVALID_NUMBER_CODE(BAD_REQUEST, "인증번호가 잘못되었습니다."),
    OVER_EXPIRED_TIME(BAD_REQUEST, "인증번호 유효 시간이 지났습니다."),
    NOT_AUTHENTICATION_NUMBER_CODE(BAD_REQUEST, "전화번호 인증을 진행해주세요."),

    MEMBER_NOT_FOUNT(NOT_FOUND, "계정이 존재하지 않습니다."),
    INVALID_PASSWORD(UNAUTHORIZED, "비밀번호를 확인해주세요."),
    DUPLICATE_MEMBER(BAD_REQUEST, "회원이 이미 존재합니다"),

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
    DUPLICATE_RESOURCE(CONFLICT, "데이터가 이미 존재합니다"),
    DUPLICATE_PHONE_NUMBER(CONFLICT, "전화번호가 이미 존재합니다"),

    /* 500 INTERNAL_SERVER_ERROR : 서버에서 오류 발생 */
    ;

    private final HttpStatus httpStatus;
    private final String detail;
}