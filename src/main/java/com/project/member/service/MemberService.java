package com.project.member.service;

import com.project.member.constant.ErrorCode;
import com.project.member.exception.SiteException;
import com.project.member.model.auth.MemberDetails;
import com.project.member.model.dto.MemberDto;
import com.project.member.model.entity.Member;
import com.project.member.model.request.MemberInfoRequest;
import com.project.member.model.request.MemberJoinRequest;
import com.project.member.model.request.MemberLoginRequest;
import com.project.member.model.request.MemberPasswordRequest;
import com.project.member.model.response.MemberInfoResponse;
import com.project.member.repository.MemberRepository;
import com.project.member.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * packageName   : com.project.member.service
 * fileName      : MemberService
 * author        : kang_jungwoo
 * date          : 2023/03/26
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/26       kang_jungwoo         최초 생성
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    private final MemberDetailsService memberDetailsService;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final JwtUtil jwtUtil;


    @Transactional
    public Member createMember(MemberJoinRequest memberJoinRequest) {
        Optional<Member> byEmail = memberRepository.findByEmail(memberJoinRequest.getEmail());
        if(byEmail.isPresent()) {
            throw new SiteException(ErrorCode.DUPLICATE_MEMBER);
        }

        // TODO SMS 인증 과정 필요

        String rawPassword = memberJoinRequest.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        Member member = new Member(memberJoinRequest, encPassword);

        return memberRepository.save(member);
    }

    public String loginMember(MemberLoginRequest memberLoginRequest) {
        // email 검증
        UserDetails userDetails = memberDetailsService.loadUserByUsername(memberLoginRequest.getEmail());

//        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginRequest.getEmail(), memberLoginRequest.getEmail());
//        // authenticate 매서드가 실행될 때 CustomUserDetailsService 에서 만든 loadUserByUsername 메서드가 실행
//        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        // 비밀번호 검증
        if(!bCryptPasswordEncoder.matches(memberLoginRequest.getPassword(), userDetails.getPassword())) {
            throw new SiteException(ErrorCode.INVALID_PASSWORD);
        }

        return jwtUtil.createJwt(userDetails.getUsername());
    }

    @Transactional
    public Member updatePassword(MemberPasswordRequest memberPasswordRequest) {
        // SMS 번호 인증

        // 인증 후 비밀번호 변경
        Optional<Member> byEmail = memberRepository.findByEmail(memberPasswordRequest.getEmail());
        if(byEmail.isPresent()) {
            throw new SiteException(ErrorCode.MEMBER_NOT_FOUNT);
        }
        Member member = byEmail.get();
        String rawPassword = memberPasswordRequest.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);

        member.updatePassword(encPassword);
        return memberRepository.save(member);
    }

    public MemberDto getMemberInfo() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        MemberDetails memberDetails = (MemberDetails) principal;
        Member member = memberDetails.getMember();

        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .nickName(member.getNickName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .build();

        return memberDto;
    }

    public Optional<Member> getMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }
}


