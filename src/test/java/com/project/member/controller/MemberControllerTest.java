package com.project.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.project.member.constant.DictionaryKey;
import com.project.member.constant.ErrorCode;
import com.project.member.exception.SiteException;
import com.project.member.model.entity.Member;
import com.project.member.model.entity.SmsAuthentication;
import com.project.member.model.request.MemberJoinRequest;
import com.project.member.model.request.MemberLoginRequest;
import com.project.member.model.request.MemberPasswordRequest;
import com.project.member.repository.MemberRepository;
import com.project.member.service.SmsAuthenticationService;
import com.project.member.utils.JwtUtil;
import com.project.member.utils.SmsUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * packageName   : com.project.member.controller
 * fileName      : MemberControllerTest
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
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private SmsAuthenticationService smsAuthenticationService;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    public void beforeEach() {
        memberRepository.deleteAll();
    }

    public Member settingUserTest() {
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest();
        memberJoinRequest.setName("강정우");
        memberJoinRequest.setPassword("testtest111!");
        memberJoinRequest.setNickName("낑깡");
        memberJoinRequest.setEmail("woodada8@gmail.com");
        memberJoinRequest.setPhoneNumber("010-7570-2222");

        String rawPassword = memberJoinRequest.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        Member member = new Member(memberJoinRequest, encPassword);
        return memberRepository.save(member); // 회원 가입
    }

    @Test
    @DisplayName("회원 가입")
    void joinMember() throws Exception {
        // given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest();
        memberJoinRequest.setName("강정우");
        memberJoinRequest.setEmail("woodada15@gmail.com");
        memberJoinRequest.setNickName("낑깡");
        memberJoinRequest.setPassword("testtest123!");
        memberJoinRequest.setPhoneNumber("010-7575-5555");

        String numberCode = SmsUtil.createNumberCode();
        // 인증번호 인증 처리
        smsAuthenticationService.createSmsAuthentication(memberJoinRequest.getPhoneNumber(), null, numberCode);
        smsAuthenticationService.updateSmsAuthentication(memberJoinRequest.getPhoneNumber(), null, numberCode, DictionaryKey.JOIN.getKey());
        // when
        // then
        String content = objectMapper.writeValueAsString(memberJoinRequest);
        mockMvc.perform(post("/api/members/join")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("비밀번호 변경")
    void updatePassword() throws Exception {
        // given
        Member save = settingUserTest();

        String numberCode = SmsUtil.createNumberCode();
        // 인증번호 인증 처리
        smsAuthenticationService.createSmsAuthentication(save.getPhoneNumber(), save.getEmail(), numberCode);
        smsAuthenticationService.updateSmsAuthentication(save.getPhoneNumber(), save.getEmail(), numberCode, DictionaryKey.PASSWORD.getKey());

        MemberPasswordRequest memberPasswordRequest = new MemberPasswordRequest();
        memberPasswordRequest.setEmail("woodada8@gmail.com");
        memberPasswordRequest.setPassword("qwerqwer11!");
        // when
        // then
        String content = objectMapper.writeValueAsString(memberPasswordRequest);
        mockMvc.perform(post("/api/members/update-password")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("로그인")
    void loginMember() throws Exception {
        // given
        Member save = settingUserTest();

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest();
        memberLoginRequest.setEmail("woodada8@gmail.com");
        memberLoginRequest.setPassword("testtest111!");
        // when
        // then
        String content = objectMapper.writeValueAsString(memberLoginRequest);
        mockMvc.perform(post("/api/members/login")
                        .content(content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("내 정보 보기")
    void getMemberInfo() throws Exception {
        // given
        Member save = settingUserTest();

        MemberLoginRequest memberLoginRequest = new MemberLoginRequest();
        memberLoginRequest.setEmail("woodada8@gmail.com");
        memberLoginRequest.setPassword("testtest111!");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginRequest.getEmail(), memberLoginRequest.getPassword());
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        String jwt = jwtUtil.createJwt(authenticate.getName());
        Authentication authentication = jwtUtil.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // then
        mockMvc.perform(get("/api/members/info")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .header("Authorization", "Bearer " + jwt))
                .andExpect(MockMvcResultMatchers.status().isOk()).andDo(print());

    }
}