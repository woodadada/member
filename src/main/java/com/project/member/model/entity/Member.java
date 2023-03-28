package com.project.member.model.entity;

import com.project.member.model.request.MemberJoinRequest;
import lombok.Getter;
import lombok.NoArgsConstructor;

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
@Entity
@Table(name = "members")
@Getter
@NoArgsConstructor
public class Member {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String password;

    private String nickName;

    private String email;

    private String phoneNumber;

//    private String role;

    public Member(MemberJoinRequest memberJoinRequest, String encPassword) {
        this.name = memberJoinRequest.getName();
        this.password = encPassword;
        this.nickName = memberJoinRequest.getNickName();
        this.email = memberJoinRequest.getEmail();
        this.phoneNumber = memberJoinRequest.getPhoneNumber();
//        this.role = "ROLE_USER";
    }

    public void updatePassword(String password) {
        this.password = password;
    }
}
