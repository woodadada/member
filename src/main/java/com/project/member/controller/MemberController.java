package com.project.member.controller;

import com.project.member.model.request.MemberJoinRequest;
import com.project.member.model.request.MemberLoginRequest;
import com.project.member.model.request.MemberPasswordRequest;
import com.project.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

/**
 * packageName   : com.project.member.controller
 * fileName      : MemberController
 * author        : kang_jungwoo
 * date          : 2023/03/26
 * description   :
 * ===========================================================
 * DATE              AUTHOR               NOTE
 * -----------------------------------------------------------
 * 2023/03/26       kang_jungwoo         최초 생성
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/members")
public class MemberController {

    private final MemberService memberService;

    // 회원 가입
    @PostMapping("/join")
    public ResponseEntity<?> joinMember(@Valid @RequestBody MemberJoinRequest memberJoinRequest){
        return ResponseEntity.ok(memberService.createMember(memberJoinRequest));
    }

    // 비밀번호 변경
    @PostMapping("/update-password")
    public ResponseEntity<?> updatePassword(@Valid @RequestBody MemberPasswordRequest memberJoinRequest){
        return ResponseEntity.ok(memberService.updatePassword(memberJoinRequest));
    }

    // 로그인
    @PostMapping("/login")
    public ResponseEntity<?> loginMember(@Valid @RequestBody MemberLoginRequest memberLoginRequest){
        return ResponseEntity.ok(memberService.loginMember(memberLoginRequest));
    }

    // 정보
    @GetMapping("/info")
    public ResponseEntity<?> getMemberInfo(HttpServletRequest httpRequest){
        return ResponseEntity.ok(memberService.getMemberInfo());
    }
}
