package com.example.emotiondiary.integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * 인증(Auth) 엔드포인트에 대한 통합(Integration) 테스트
 *
 * <p>단위 테스트와 달리 Controller → Service → Repository → DB 까지
 * 실제 빈(Bean)을 모두 끌어올려 "요청 한 건이 실제 흐름대로 동작하는가" 를 검증한다.
 * Mockito 로 가짜 객체를 주입하지 않고, Spring 컨텍스트 안의 진짜 구현체가 쓰인다.</p>
 *
 * <h3>어노테이션 설명</h3>
 * <ul>
 *   <li>{@code @SpringBootTest} — 전체 Spring 컨텍스트를 띄운다. 즉 @Configuration,
 *       @Component, @Service, @Repository 등 애플리케이션의 모든 빈이 로드된다.</li>
 *   <li>{@code @ActiveProfiles("test")} — {@code application-test.yaml} 을 활성화해
 *       H2 인메모리 DB 로 접속한다. 운영 DB(MariaDB) 는 건드리지 않는다.</li>
 *   <li>{@code @Transactional} — 각 테스트 메서드 종료 시 DB 변경 내역을 자동 롤백해
 *       테스트 간 상태가 서로 오염되지 않도록 격리한다.</li>
 * </ul>
 *
 * <h3>MockMvc 구성</h3>
 * <p>HTTP 요청을 실제 포트로 보내는 대신, {@link MockMvc} 로 Dispatcher 레벨에서
 * 가짜 요청을 흘려보내 결과를 검증한다. {@code springSecurity()} 를 적용해
 * Security 필터 체인까지 포함한 상태로 테스트한다.</p>
 *
 * <h3>테스트 시나리오</h3>
 * <ol>
 *   <li>정상 회원가입 → 같은 계정으로 로그인 시 accessToken / refreshToken 발급 여부</li>
 *   <li>비밀번호 유효성 검증(영문+숫자 패턴) 위반 시 400 응답과
 *       {@code VALIDATION_ERROR} 코드 반환 여부</li>
 * </ol>
 */
@DisplayName("회원가입 → 로그인 E2E 통합 테스트")
@SpringBootTest
@ActiveProfiles("test")
@Transactional   // 테스트 종료 시 자동 롤백
class AuthControllerIntegrationTest {

    @Autowired WebApplicationContext ctx;
    @Autowired
    ObjectMapper om;

    private MockMvc mvc;

    void setup() {
        this.mvc = MockMvcBuilders.webAppContextSetup(ctx)
                .apply(springSecurity())   // Security 필터 체인 포함
                .build();
    }

    @Test
    @DisplayName("회원가입 후 같은 계정으로 로그인하면 accessToken 을 발급받는다")
    void signup_then_login_issuesToken() throws Exception {
        setup();

        ObjectNode signup = om.createObjectNode();
        signup.put("email", "test1@example.com");
        signup.put("password", "pass1234");
        signup.put("nickname", "길동이");

        mvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(signup)))
                .andDo(print())
                .andExpect(status().isCreated());

        ObjectNode login = om.createObjectNode();
        login.put("email", "test1@example.com");
        login.put("password", "pass1234");

        mvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(login)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").isNotEmpty())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"));
    }

    @Test
    @DisplayName("비밀번호가 영문+숫자 패턴을 위반하면 400 VALIDATION_ERROR")
    void signup_invalidPassword_returns400() throws Exception {
        setup();

        ObjectNode req = om.createObjectNode();
        req.put("email", "bad@example.com");
        req.put("password", "onlyletters");   // 숫자 없음 → 패턴 위반
        req.put("nickname", "홍길동");

        mvc.perform(post("/api/auth/signup")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value("VALIDATION_ERROR"));
    }
}