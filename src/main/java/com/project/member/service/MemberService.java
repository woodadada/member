package com.project.member.service;

import com.project.member.constant.ErrorCode;
import com.project.member.exception.SiteException;
import com.project.member.model.auth.MemberDetails;
import com.project.member.model.dto.MemberDto;
import com.project.member.model.entity.Member;
import com.project.member.model.request.MemberJoinRequest;
import com.project.member.model.request.MemberLoginRequest;
import com.project.member.model.request.MemberPasswordRequest;
import com.project.member.model.response.MemberInfoResponse;
import com.project.member.model.response.MemberJoinResponse;
import com.project.member.model.response.MemberPasswordResponse;
import com.project.member.repository.MemberRepository;
import com.project.member.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    private final SmsAuthenticationService smsAuthenticationService;

    private final JwtUtil jwtUtil;


    @Transactional
    public MemberJoinResponse createMember(MemberJoinRequest memberJoinRequest) {
        Optional<Member> byEmail = memberRepository.findByEmail(memberJoinRequest.getEmail());
        if(byEmail.isPresent()) {
            throw new SiteException(ErrorCode.DUPLICATE_MEMBER);
        }

        // 회원 가입 SMS 인증
        smsAuthenticationService.smsAuthenticationByPhoneNumber(memberJoinRequest.getPhoneNumber());

        String rawPassword = memberJoinRequest.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        Member saveMember = new Member(memberJoinRequest, encPassword);

        Member member = memberRepository.save(saveMember);

        smsAuthenticationService.deleteSmsAuthenticationByPhoneNumber(member.getPhoneNumber());

        MemberDto memberDto = MemberDto.builder()
                .id(member.getId())
                .name(member.getName())
                .nickName(member.getNickName())
                .email(member.getEmail())
                .phoneNumber(member.getPhoneNumber())
                .build();

        MemberJoinResponse memberInfoResponse = new MemberJoinResponse();
        memberInfoResponse.setMemberDto(memberDto);

        return memberInfoResponse;
    }

    public String loginMember(MemberLoginRequest memberLoginRequest) {
        // 비밀번호 검증
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(memberLoginRequest.getEmail(), memberLoginRequest.getPassword());
        Authentication authenticate = authenticationManagerBuilder.getObject().authenticate(authenticationToken);

        return jwtUtil.createJwt(authenticate.getName());
    }

    @Transactional
    public MemberPasswordResponse updatePassword(MemberPasswordRequest memberPasswordRequest) {

        // 인증 후 비밀번호 변경
        Optional<Member> byEmail = memberRepository.findByEmail(memberPasswordRequest.getEmail());
        if(!byEmail.isPresent()) {
            throw new SiteException(ErrorCode.MEMBER_NOT_FOUNT);
        }
        Member member = byEmail.get();
        // SMS 번호 인증
        smsAuthenticationService.smsAuthenticationByPhoneNumber(member.getPhoneNumber());

        String rawPassword = memberPasswordRequest.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword);
        member.updatePassword(encPassword);
        Member saveMember = memberRepository.save(member);

        smsAuthenticationService.deleteSmsAuthenticationByPhoneNumber(member.getPhoneNumber());

        MemberDto memberDto = MemberDto.builder()
                .id(saveMember.getId())
                .name(saveMember.getName())
                .nickName(saveMember.getNickName())
                .email(saveMember.getEmail())
                .phoneNumber(saveMember.getPhoneNumber())
                .build();

        MemberPasswordResponse memberPasswordResponse = new MemberPasswordResponse();
        memberPasswordResponse.setMemberDto(memberDto);

        return memberPasswordResponse;
    }

    public MemberInfoResponse getMemberInfo() {
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

        MemberInfoResponse memberInfoResponse = new MemberInfoResponse();
        memberInfoResponse.setMemberDto(memberDto);

        return memberInfoResponse;
    }

    public Optional<Member> getMemberByPhoneNumber(String phoneNumber) {
        return memberRepository.findByPhoneNumber(phoneNumber);
    }
}


