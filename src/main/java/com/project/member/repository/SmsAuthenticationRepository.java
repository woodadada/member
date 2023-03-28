package com.project.member.repository;

import com.project.member.model.entity.Member;
import com.project.member.model.entity.SmsAuthentication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * packageName   : com.project.member.repository
 * fileName      : SmsAuthenticationRepository
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/28       kang_jungwoo         최초 생성
 */
@Repository
public interface SmsAuthenticationRepository extends JpaRepository<SmsAuthentication, Long> {
    Optional<SmsAuthentication> findByEmail(String email);

    Optional<SmsAuthentication> findByEmailAndNumberCode(String email, String numberCode);
    Optional<SmsAuthentication> findByPhoneNumberAndNumberCode(String phoneNumber, String numberCode);

    Optional<SmsAuthentication> findByPhoneNumberAndAuthenticationYn(String phoneNumber, String authenticationYn);
    List<SmsAuthentication> findAllByAuthenticationYn(String authenticationYn);

}
