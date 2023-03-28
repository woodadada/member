package com.project.member.model.request;

import com.project.member.service.SmsService;
import com.project.member.utils.SmsUtil;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

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
@AllArgsConstructor
@NoArgsConstructor
public class MessagesRequest {
    private String to;
    private String content;

    public MessagesRequest(String to, String numberCode) {
        this.to = to;
        this.content = SmsUtil.getSmsStringFormat(numberCode);
    }
}
