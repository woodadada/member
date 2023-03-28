package com.project.member.service;

import com.project.member.constant.DictionaryKey;
import com.project.member.constant.ErrorCode;
import com.project.member.exception.SiteException;
import com.project.member.model.entity.SmsAuthentication;
import com.project.member.repository.SmsAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * packageName   : com.project.member.service
 * fileName      : SmsAuthenticationService
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/28       kang_jungwoo         최초 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmsAuthenticationService {

    private final Long MINUTE_1 = 60000L;
    private final Long MINUTE_3 = MINUTE_1 * 3;

    private final Long MINUTE_5 = MINUTE_1 * 5;

    private final SmsAuthenticationRepository smsAuthenticationRepository;

    @Transactional
    public SmsAuthentication createSmsAuthentication(String phoneNumber, String email, String numberCode) {
        Long currentTime = System.currentTimeMillis();
        Long expiredTime = currentTime + MINUTE_3;

        SmsAuthentication smsAuthentication = SmsAuthentication.builder().phoneNumber(phoneNumber).email(email).numberCode(numberCode).authenticationYn(DictionaryKey.N.getKey()).expiredTime(expiredTime).build();
        return smsAuthenticationRepository.save(smsAuthentication);
    }

    // 인증 처리
    @Transactional
    public SmsAuthentication updateSmsAuthentication(String phoneNumber, String email, String numberCode, String type) {
        Optional<SmsAuthentication> byNumberCode = null;
        if(StringUtils.equals(type, DictionaryKey.PASSWORD.getKey())) {
            byNumberCode = smsAuthenticationRepository.findByEmailAndNumberCode(email, numberCode);
        }else {
            byNumberCode = smsAuthenticationRepository.findByPhoneNumberAndNumberCode(phoneNumber, numberCode);
        }

        if(!byNumberCode.isPresent()) {
            throw new SiteException(ErrorCode.INVALID_NUMBER_CODE);
        }

        SmsAuthentication smsAuthentication = byNumberCode.get();
        if(smsAuthentication.getExpiredTime() < System.currentTimeMillis()) {
            throw new SiteException(ErrorCode.OVER_EXPIRED_TIME);
        }

        smsAuthentication.updateAuthenticationYn(DictionaryKey.Y.getKey());
        return smsAuthenticationRepository.save(smsAuthentication);
    }

    public void smsAuthentication(String phoneNumber) {
        Optional<SmsAuthentication> byPhoneNumberAndAuthenticationYn = smsAuthenticationRepository.findByPhoneNumberAndAuthenticationYn(phoneNumber, DictionaryKey.Y.getKey());
        if(!byPhoneNumberAndAuthenticationYn.isPresent()) {
            throw new SiteException(ErrorCode.NOT_AUTHENTICATION_NUMBER_CODE);
        }

        SmsAuthentication smsAuthentication = byPhoneNumberAndAuthenticationYn.get();

        if(smsAuthentication.getExpiredTime() < System.currentTimeMillis()) {
            throw new SiteException(ErrorCode.OVER_EXPIRED_TIME);
        }
    }
}
