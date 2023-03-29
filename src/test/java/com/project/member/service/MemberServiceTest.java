package com.project.member.service;

import com.project.member.constant.ErrorCode;
import com.project.member.exception.SiteException;
import com.project.member.model.auth.MemberDetails;
import com.project.member.model.entity.Member;
import com.project.member.model.request.MemberJoinRequest;
import com.project.member.model.request.MemberLoginRequest;
import com.project.member.model.request.MemberPasswordRequest;
import com.project.member.repository.MemberRepository;
import com.project.member.utils.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.test.context.support.WithUserDetails;

import javax.annotation.PostConstruct;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * packageName   : com.project.member.service
 * fileName      : MemberServiceTest
 * author        : kang_jungwoo
 * date          : 2023/03/29
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/29       kang_jungwoo         최초 생성
 */
@SpringBootTest
class MemberServiceTest {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManagerBuilder authenticationManagerBuilder;

    @Autowired
    private JwtUtil jwtUtil;

    @BeforeEach
    public void beforeEach() {
        memberRepository.deleteAll();
        settingUserTest();
    }

    @Test
    @DisplayName("회원 생성")
    void createMember() {
        // given
        MemberJoinRequest memberJoinRequest = new MemberJoinRequest();
        memberJoinRequest.setName("강정우2");
        memberJoinRequest.setPassword("testtest111!");
        memberJoinRequest.setNickName("낑깡");
        memberJoinRequest.setEmail("woodada12@gmail.com");
        memberJoinRequest.setPhoneNumber("010-7570-3955");
        Optional<Member> byEmail = memberRepository.findByEmail(memberJoinRequest.getEmail());
        if(byEmail.isPresent()) {
            throw new SiteException(ErrorCode.DUPLICATE_MEMBER);
        }
        String rawPassword = memberJoinRequest.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        Member member = new Member(memberJoinRequest, encPassword);
        // when
        Member save = memberRepository.save(member);

        // then
        assertEquals(member.getEmail(), memberRepository.findById(save.getId()).get().getEmail());
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
    @DisplayName("로그인")
    void loginMember() {
        // given
        MemberLoginRequest memberLoginRequest = new MemberLoginRequest();
        memberLoginRequest.setEmail("woodada@gmail.com");
        memberLoginRequest.setPassword("testtest111!");
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginRequest.getEmail(), memberLoginRequest.getPassword());
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        // when
        String jwt = jwtUtil.createJwt(authenticate.getName());
        Authentication authentication = jwtUtil.getAuthentication(jwt);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // then
        assertNotNull(jwt);
    }

    @Test
    @DisplayName("비밀번호 변경")
    void updatePassword() {
        // given
        MemberPasswordRequest memberPasswordRequest = new MemberPasswordRequest();
        String email = "woodada@gmail.com";
        memberPasswordRequest.setEmail(email);
        memberPasswordRequest.setPassword("changeTest1!");
        Optional<Member> byEmail = memberRepository.findByEmail(memberPasswordRequest.getEmail());
        if(!byEmail.isPresent()) {
            throw new SiteException(ErrorCode.MEMBER_NOT_FOUNT);
        }
        Member member = byEmail.get();

        // when
        String rawPassword = memberPasswordRequest.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        member.updatePassword(encPassword);
        Member saveMember = memberRepository.save(member);

        // then
        assertEquals(encPassword, memberRepository.findById(saveMember.getId()).get().getPassword());
    }

    @Test
    @DisplayName("내 정보 보기")
    void getMemberInfo() {
        // given
        loginMember();

        // when
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MemberDetails memberDetails = (MemberDetails) principal;
        Member member = memberDetails.getMember();

        // then
        String email = "woodada@gmail.com";
        assertEquals(email, member.getEmail());
    }
}