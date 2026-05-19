package com.campus.market;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.storage.upload-dir=target/test-uploads")
@AutoConfigureMockMvc
class ImageUploadTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void authenticatedUserCanUploadGoodsImageAndReadPublicFile() throws Exception {
        var token = registerAndGetToken();
        var file = new MockMultipartFile(
                "file",
                "goods.png",
                MediaType.IMAGE_PNG_VALUE,
                new byte[]{(byte) 0x89, 'P', 'N', 'G'}
        );

        var result = mockMvc.perform(multipart("/api/images/goods")
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.url", endsWith(".png")))
                .andReturn();

        String url = JsonPath.read(result.getResponse().getContentAsString(), "$.data.url");
        mockMvc.perform(get(url))
                .andExpect(status().isOk())
                .andExpect(content().bytes(new byte[]{(byte) 0x89, 'P', 'N', 'G'}));
    }

    @Test
    void uploadRequiresLoginAndRejectsNonImage() throws Exception {
        var file = new MockMultipartFile("file", "note.txt", MediaType.TEXT_PLAIN_VALUE, "hello".getBytes());

        mockMvc.perform(multipart("/api/images/goods").file(file))
                .andExpect(status().isForbidden());

        var token = registerAndGetToken();
        mockMvc.perform(multipart("/api/images/goods")
                        .file(file)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("仅支持 jpg、png、webp、gif 图片"));
    }

    private String registerAndGetToken() throws Exception {
        var username = "imageUser" + System.nanoTime();
        var result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "123456",
                                  "nickname": "图片用户"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())))
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token").toString();
    }
}
