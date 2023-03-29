package com.project.member.model.response;

import com.project.member.model.dto.MemberDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * packageName   : com.project.member.model.response
 * fileName      : MemberInfoResponse
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/28       kang_jungwoo         최초 생성
 */
@Getter
@Setter
@ToString
@NoArgsConstructor
public class MemberPasswordResponse {
    MemberDto memberDto;
}
