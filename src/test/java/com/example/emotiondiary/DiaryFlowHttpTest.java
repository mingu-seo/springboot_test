package com.example.emotiondiary;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
@ActiveProfiles("test")
class DiaryFlowHttpTest {

    @Autowired
    TestRestTemplate rest;

    @Test
    @DisplayName("회원가입 → 로그인 → 일기 생성 → 목록 조회")
    void fullFlow() {
        // 1) 회원가입
        ResponseEntity<Void> signup = rest.postForEntity(
                "/api/auth/signup",
                Map.of("email", "e2e@example.com",
                        "password", "pass1234",
                        "nickname", "e2e유저"),
                Void.class);
        assertThat(signup.getStatusCode()).isEqualTo(HttpStatus.CREATED);

        // 2) 로그인 → 토큰 확보
        ResponseEntity<Map> login = rest.postForEntity(
                "/api/auth/login",
                Map.of("email", "e2e@example.com", "password", "pass1234"),
                Map.class);
        String accessToken = (String) login.getBody().get("accessToken");

        // 3) 일기 생성 (Authorization 헤더 동반)
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(accessToken);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(
                Map.of("date", 100L, "content", "HTTP 통합", "emotionId", 5),
                headers);

        ResponseEntity<Map> create = rest.postForEntity("/api/diaries", entity, Map.class);
        assertThat(create.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }
}