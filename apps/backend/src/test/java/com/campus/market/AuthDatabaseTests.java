package com.campus.market;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.blankOrNullString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AuthDatabaseTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userCanUpdateProfileAndChangePassword() throws Exception {
        var username = "profile" + System.nanoTime();
        var token = registerAndGetToken(username, "123456", "原昵称");

        mockMvc.perform(patch("/api/auth/me")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "nickname": "新昵称" }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.nickname").value("新昵称"))
                .andExpect(jsonPath("$.data.status").value(1));

        mockMvc.perform(patch("/api/auth/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "oldPassword": "wrong", "newPassword": "654321" }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("原密码不正确"));

        mockMvc.perform(patch("/api/auth/password")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "oldPassword": "123456", "newPassword": "654321" }
                                """))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "username": "%s", "password": "123456" }
                                """.formatted(username)))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "username": "%s", "password": "654321" }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())));
    }

    @Test
    void registerLoginAndReadCurrentUser() throws Exception {
        var username = "user" + System.nanoTime();
        var registerBody = """
                {
                  "username": "%s",
                  "password": "123456",
                  "nickname": "测试用户"
                }
                """.formatted(username);

        var registerResult = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(registerBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())))
                .andReturn();

        var token = com.jayway.jsonpath.JsonPath.read(
                registerResult.getResponse().getContentAsString(),
                "$.data.token"
        ).toString();

        var loginBody = """
                {
                  "username": "%s",
                  "password": "123456"
                }
                """.formatted(username);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(loginBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())));

        mockMvc.perform(get("/api/auth/me")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.username").value(username));
    }

    private String registerAndGetToken(String username, String password, String nickname) throws Exception {
        var result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s",
                                  "nickname": "%s"
                                }
                                """.formatted(username, password, nickname)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())))
                .andReturn();

        return com.jayway.jsonpath.JsonPath.read(
                result.getResponse().getContentAsString(),
                "$.data.token"
        ).toString();
    }
}
