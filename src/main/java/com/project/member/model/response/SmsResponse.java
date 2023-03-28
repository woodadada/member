package com.project.member.model.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * packageName   : com.project.member.model.response
 * fileName      : sendSmsResponse
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/28       kang_jungwoo         최초 생성
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class SmsResponse {
    private String statusCode;
    private String statusName;
    private String requestId;
    private LocalDateTime requestTime;
}
