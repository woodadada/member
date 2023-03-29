package com.project.member.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

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

    @Test
    @DisplayName("회원 가입")
    void joinMember() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("비밀번호 변경")
    void updatePassword() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("로그인")
    void loginMember() {
        // given

        // when

        // then

    }

    @Test
    @DisplayName("내 정보 보기")
    void getMemberInfo() {
        // given

        // when

        // then

    }
}