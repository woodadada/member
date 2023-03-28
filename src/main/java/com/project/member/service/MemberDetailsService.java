package com.project.member.service;

import com.project.member.constant.ErrorCode;
import com.project.member.exception.SiteException;
import com.project.member.model.auth.MemberDetails;
import com.project.member.model.entity.Member;
import com.project.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * packageName   : com.project.member.service
 * fileName      : MemberDetailService
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
public class MemberDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new SiteException(ErrorCode.MEMBER_NOT_FOUNT));

        if(member != null) {
            return new MemberDetails(member);
        }
        return null;
    }
}
