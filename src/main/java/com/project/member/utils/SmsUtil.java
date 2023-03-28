package com.project.member.utils;

import java.util.Random;

/**
 * packageName   : com.project.member.utils
 * fileName      : SmsUtil
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/28       kang_jungwoo         최초 생성
 */
public class SmsUtil {

    // 기본 생성자가 만들어지는 것 방지
    private SmsUtil() {}

    private static final String SMS_STRING = "인증번호[%s]를 입력해주세요.\n유효 시간은 3분입니다.";

    // 인증코드 만들기
    public static String createNumberCode() {
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0; i < 6; i++) { // 인증코드 6자리
            key.append((rnd.nextInt(10)));
        }
        return key.toString();
    }

    public static String getSmsStringFormat(String numberCode) {
        return String.format(SMS_STRING, numberCode);
    }
}
