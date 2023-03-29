package com.project.member.service;

import com.project.member.model.entity.Member;
import com.project.member.model.request.MemberJoinRequest;
import com.project.member.model.request.MemberSmsRequest;
import com.project.member.model.response.SmsResponse;
import com.project.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName   : com.project.member.service
 * fileName      : SmsServiceTest
 * author        : kang_jungwoo
 * date          : 2023/03/29
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/29       kang_jungwoo         최초 생성
 */
@SpringBootTest
class SmsServiceTest {

    @Autowired
    private SmsService smsService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;



    @Test
    @DisplayName("회원가입 인증 번호 발송 테스트")
    void sendSmsNumberCodeByMemberJoin() throws Exception {
        // given
        MemberSmsRequest memberSmsRequest = new MemberSmsRequest();
        memberSmsRequest.setPhoneNumber("010-7570-3950");

        // when
        SmsResponse smsResponse = smsService.sendSmsNumberCodeByMemberJoin(memberSmsRequest);

        //then
        assertEquals(smsResponse.getStatusCode(), String.valueOf(HttpStatus.ACCEPTED.value()));
    }

    public void settingUserTest() {
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest();
        memberJoinRequest.setName("강정우");
        memberJoinRequest.setPassword("testtest111!");
        memberJoinRequest.setNickName("낑깡");
        memberJoinRequest.setEmail("woodada@gmail.com");
        memberJoinRequest.setPhoneNumber("010-7570-3950");

        String rawPassword = memberJoinRequest.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        Member member = new Member(memberJoinRequest, encPassword);
        Member save = memberRepository.save(member);
    }

    @Test
    @DisplayName("비밀번호 변경 인증 번호 발송 테스트")
    void sendSmsNumberCodeByUpdatePassword() throws Exception {
        // given
        settingUserTest();
        MemberSmsRequest memberSmsRequest = new MemberSmsRequest();
        memberSmsRequest.setPhoneNumber("010-7570-3950");

        // when
        SmsResponse smsResponse = smsService.sendSmsNumberCodeByUpdatePassword(memberSmsRequest);

        //then
        assertEquals(smsResponse.getStatusCode(), String.valueOf(HttpStatus.ACCEPTED.value()));
    }
}