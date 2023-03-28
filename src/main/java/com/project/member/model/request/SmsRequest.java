package com.project.member.model.request;

import lombok.*;

import java.util.List;

/**
 * packageName   : com.project.member.model.request
 * fileName      : SmsRequest
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/28       kang_jungwoo         최초 생성
 */
@Data
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SmsRequest {
    private String type;
    private String contentType;
    private String countryCode;
    private String from;
    private String content;
    private List<MessagesRequest> messages;
}
