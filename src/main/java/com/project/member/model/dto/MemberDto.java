package com.project.member.model.dto;

import com.project.member.model.request.MemberJoinRequest;
import lombok.*;

import javax.persistence.*;

/**
 * packageName   : com.project.member.model.entity
 * fileName      : Member
 * author        : kang_jungwoo
 * date          : 2023/03/26
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/26       kang_jungwoo         최초 생성
 */
@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDto {

    private Long id;

    private String name;

    private String nickName;

    private String email;

    private String phoneNumber;
}
