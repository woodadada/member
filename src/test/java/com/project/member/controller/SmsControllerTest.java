package com.project.member.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.member.constant.DictionaryKey;
import com.project.member.model.entity.Member;
import com.project.member.model.entity.SmsAuthentication;
import com.project.member.model.request.MemberJoinRequest;
import com.project.member.model.request.MemberSmsRequest;
import com.project.member.repository.MemberRepository;
import com.project.member.repository.SmsAuthenticationRepository;
import com.project.member.utils.SmsUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * packageName   : com.project.member.controller
 * fileName      : SmsControllerTest
 * author        : kang_jungwoo
 * date          : 2023/03/29
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/29       kang_jungwoo         최초 생성
 */
@SpringBootTest
@AutoConfigureMockMvc
class SmsControllerTest {

    private final Long MINUTE_1 = 60000L;
    private final Long MINUTE_3 = MINUTE_1 * 3;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SmsAuthenticationRepository smsAuthenticationRepository;

    @BeforeEach
    public void beforeEach() {
        memberRepository.deleteAll();
    }

    public Member settingUserTest() {
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest();
        memberJoinRequest.setName("강정우");
        memberJoinRequest.setPassword("testtest111!");
        memberJoinRequest.setNickName("낑깡");
        memberJoinRequest.setEmail("woodada@gmail.com");
        memberJoinRequest.setPhoneNumber("010-8888-3950");

        String rawPassword = memberJoinRequest.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        Member member = new Member(memberJoinRequest, encPassword);
        return memberRepository.save(member);
    }


    @Test
    @DisplayName("회원가입 SMS 인증번호 발송")
    void sendJoinSms() throws Exception {
        // given
        MemberSmsRequest memberSmsRequest = new MemberSmsRequest();
        memberSmsRequest.setPhoneNumber("010-8888-3950");
        String content = objectMapper.writeValueAsString(memberSmsRequest);
        // when
        // then
        mockMvc.perform(post("/api/sms/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("비밀번호 변경 SMS 인증번호 발송")
    void sendPasswordSms() throws Exception {
        // given
        settingUserTest();

        MemberSmsRequest memberSmsRequest = new MemberSmsRequest();
        memberSmsRequest.setPhoneNumber("010-8888-3950");
        String content = objectMapper.writeValueAsString(memberSmsRequest);
        // when
        // then
        mockMvc.perform(post("/api/sms/password")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("회원가입 SMS 인증번호 인증")
    void authenticationJoinSms() throws Exception {
        // given
        Long currentTime = System.currentTimeMillis();
        Long expiredTime = currentTime + MINUTE_3;
        String phoneNumber = "010-7570-3951";
        String email = "woodada3@gmail.com";
        String numberCode = SmsUtil.createNumberCode();
        SmsAuthentication smsAuthentication = SmsAuthentication.builder().phoneNumber(phoneNumber)
                .email(email).numberCode(numberCode)
                .authenticationYn(DictionaryKey.N.getKey()).expiredTime(expiredTime).build();
        SmsAuthentication save = smsAuthenticationRepository.save(smsAuthentication);

        MemberSmsRequest memberSmsRequest = new MemberSmsRequest();
        memberSmsRequest.setNumberCode(numberCode);
        memberSmsRequest.setPhoneNumber(phoneNumber);
        String content = objectMapper.writeValueAsString(memberSmsRequest);
        // when
        // then
        mockMvc.perform(post("/api/sms/authentication/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("비밀번호 변경 SMS 인증번호 인증")
    void authenticationPasswordSms() throws Exception {
        Member member = settingUserTest();
        // given
        Long currentTime = System.currentTimeMillis();
        Long expiredTime = currentTime + MINUTE_3;
        String phoneNumber = member.getPhoneNumber();
        String email = member.getEmail();
        String numberCode = SmsUtil.createNumberCode();
        SmsAuthentication smsAuthentication = SmsAuthentication.builder().phoneNumber(phoneNumber)
                .email(email).numberCode(numberCode)
                .authenticationYn(DictionaryKey.N.getKey()).expiredTime(expiredTime).build();
        SmsAuthentication save = smsAuthenticationRepository.save(smsAuthentication);

        MemberSmsRequest memberSmsRequest = new MemberSmsRequest();
        memberSmsRequest.setNumberCode(numberCode);
        memberSmsRequest.setPhoneNumber(phoneNumber);
        String content = objectMapper.writeValueAsString(memberSmsRequest);
        // when
        // then
        mockMvc.perform(post("/api/sms/authentication/password")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}