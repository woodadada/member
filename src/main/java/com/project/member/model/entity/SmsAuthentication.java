package com.project.member.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * packageName   : com.project.member.model.entity
 * fileName      : SmsAuthentication
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/28       kang_jungwoo         최초 생성
 */
@Entity
@Table(name = "smsAuthentications")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SmsAuthentication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    String email;

    String numberCode;

    String authenticationYn;

    Long expiredTime;

    public void updateAuthenticationYn(String flag) {
        this.authenticationYn = flag;
    }
}
