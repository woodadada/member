package com.project.member.service;

import com.project.member.constant.DictionaryKey;
import com.project.member.constant.ErrorCode;
import com.project.member.exception.SiteException;
import com.project.member.model.entity.SmsAuthentication;
import com.project.member.repository.SmsAuthenticationRepository;
import com.project.member.utils.SmsUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName   : com.project.member.service
 * fileName      : SmsAuthenticationServiceTest
 * author        : kang_jungwoo
 * date          : 2023/03/29
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/29       kang_jungwoo         최초 생성
 */
@SpringBootTest
class SmsAuthenticationServiceTest {

    private final Long MINUTE_1 = 60000L;
    private final Long MINUTE_3 = MINUTE_1 * 3;

    private final Long MINUTE_5 = MINUTE_1 * 5;

    @Autowired
    private SmsAuthenticationRepository smsAuthenticationRepository;

    @Test
    @DisplayName("SMS 인증 정보 생성")
    void createSmsAuthentication() {
        // given
        Long currentTime = System.currentTimeMillis();
        Long expiredTime = currentTime + MINUTE_3;
        String phoneNumber = "010-7570-3951";
        String email = "woodada3@gmail.com";
        String numberCode = SmsUtil.createNumberCode();
        SmsAuthentication smsAuthentication = SmsAuthentication.builder().phoneNumber(phoneNumber)
                .email(email).numberCode(numberCode)
                .authenticationYn(DictionaryKey.N.getKey()).expiredTime(expiredTime).build();

        // when
        SmsAuthentication save = smsAuthenticationRepository.save(smsAuthentication);

        // then
        assertEquals(numberCode, smsAuthenticationRepository.findById(smsAuthentication.getId()).get().getNumberCode());
    }

    @Test
    @DisplayName("회원가입 SMS 인증 정보 수정")
    void updateJoinSmsAuthentication() {
        // given
        Long currentTime = System.currentTimeMillis();
        Long expiredTime = currentTime + MINUTE_3;
        String phoneNumber = "010-7570-3955";
        String email = "woodada11@gmail.com";
        String numberCode = SmsUtil.createNumberCode();
        SmsAuthentication saveSmsAuthentication = SmsAuthentication.builder().phoneNumber(phoneNumber)
                .email(email).numberCode(numberCode)
                .authenticationYn(DictionaryKey.N.getKey()).expiredTime(expiredTime).build();
        SmsAuthentication save = smsAuthenticationRepository.save(saveSmsAuthentication);

        Optional<SmsAuthentication> byNumberCode = smsAuthenticationRepository.findByPhoneNumberAndNumberCode(phoneNumber, numberCode);
        if(!byNumberCode.isPresent()) {
            throw new SiteException(ErrorCode.INVALID_NUMBER_CODE);
        }
        SmsAuthentication smsAuthentication = byNumberCode.get();
        if(smsAuthentication.getExpiredTime() < System.currentTimeMillis()) {
            throw new SiteException(ErrorCode.OVER_EXPIRED_TIME);
        }
        smsAuthentication.updateAuthenticationYn(DictionaryKey.Y.getKey());

        // when
        SmsAuthentication save1 = smsAuthenticationRepository.save(smsAuthentication);

        // then
        assertEquals(DictionaryKey.Y.getKey(), smsAuthenticationRepository.findById(save1.getId()).get().getAuthenticationYn());
    }

    @Test
    @DisplayName("비밀번호 변경 SMS 인증 정보 수정")
    void updatePasswordSmsAuthentication() {
        // given
        Long currentTime = System.currentTimeMillis();
        Long expiredTime = currentTime + MINUTE_3;
        String phoneNumber = "010-7570-3950";
        String email = "woodada@gmail.com";
        String numberCode = SmsUtil.createNumberCode();
        SmsAuthentication saveSmsAuthentication = SmsAuthentication.builder().phoneNumber(phoneNumber)
                .email(email).numberCode(numberCode)
                .authenticationYn(DictionaryKey.N.getKey()).expiredTime(expiredTime).build();
        SmsAuthentication save = smsAuthenticationRepository.save(saveSmsAuthentication);

        Optional<SmsAuthentication> byNumberCode = smsAuthenticationRepository.findByEmailAndNumberCode(email, numberCode);
        if(!byNumberCode.isPresent()) {
            throw new SiteException(ErrorCode.INVALID_NUMBER_CODE);
        }
        SmsAuthentication smsAuthentication = byNumberCode.get();
        if(smsAuthentication.getExpiredTime() < System.currentTimeMillis()) {
            throw new SiteException(ErrorCode.OVER_EXPIRED_TIME);
        }
        smsAuthentication.updateAuthenticationYn(DictionaryKey.Y.getKey());

        // when
        SmsAuthentication save1 = smsAuthenticationRepository.save(smsAuthentication);

        // then
        assertEquals(DictionaryKey.Y.getKey(), smsAuthenticationRepository.findById(save1.getId()).get().getAuthenticationYn());
    }

    @Test
    @DisplayName("SMS 인증 정보 검증")
    void smsAuthenticationByPhoneNumber() {
        // given
        String phoneNumber = "010-7570-3955";
        updateJoinSmsAuthentication();

        // when
        Optional<SmsAuthentication> byPhoneNumberAndAuthenticationYn = smsAuthenticationRepository.findByPhoneNumberAndAuthenticationYn(phoneNumber, DictionaryKey.Y.getKey());
        if(!byPhoneNumberAndAuthenticationYn.isPresent()) {
            throw new SiteException(ErrorCode.NOT_AUTHENTICATION_NUMBER_CODE);
        }

        SmsAuthentication smsAuthentication = byPhoneNumberAndAuthenticationYn.get();

        if(smsAuthentication.getExpiredTime() < System.currentTimeMillis()) {
            throw new SiteException(ErrorCode.OVER_EXPIRED_TIME);
        }

        // then
        assertEquals(DictionaryKey.Y.getKey(), smsAuthenticationRepository.findById(smsAuthentication.getId()).get().getAuthenticationYn());
        assertTrue(smsAuthentication.getExpiredTime() > System.currentTimeMillis());
    }
}