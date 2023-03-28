package com.project.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.project.member.model.request.MemberJoinRequest;
import com.project.member.model.request.MemberLoginRequest;
import com.project.member.model.request.MemberPasswordRequest;
import com.project.member.model.request.MemberSmsRequest;
import com.project.member.service.MemberService;
import com.project.member.service.SmsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * packageName   : com.project.member.controller
 * fileName      : SmsController
 * author        : kang_jungwoo
 * date          : 2023/03/28
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/26       kang_jungwoo         최초 생성
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sms")
public class SmsController {

    private final SmsService smsService;

    // 인증 문자
    @PostMapping("/join")
    public ResponseEntity<?> sendJoinSms(@Valid @RequestBody MemberSmsRequest memberSmsRequest) throws Exception {
        return ResponseEntity.ok(smsService.sendSmsNumberCodeByMemberJoin(memberSmsRequest));
    }

    @PostMapping("/password")
    public ResponseEntity<?> sendPasswordSms(@Valid @RequestBody MemberSmsRequest memberSmsRequest) throws Exception {
        return ResponseEntity.ok(smsService.sendSmsNumberCodeByUpdatePassword(memberSmsRequest));
    }

    @PostMapping("/authentication/join")
    public ResponseEntity<?> authenticationJoinSms(@Valid @RequestBody MemberSmsRequest memberSmsRequest) {
        return ResponseEntity.ok(smsService.updateJoinSmsAuthenticationYn(memberSmsRequest));
    }

    @PostMapping("/authentication/password")
    public ResponseEntity<?> authenticationPasswordSms(@Valid @RequestBody MemberSmsRequest memberSmsRequest) {
        return ResponseEntity.ok(smsService.updatePasswordSmsAuthenticationYn(memberSmsRequest));
    }
}
