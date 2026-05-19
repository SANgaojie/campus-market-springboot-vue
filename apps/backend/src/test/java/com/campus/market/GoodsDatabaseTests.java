package com.campus.market;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GoodsDatabaseTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void listCategoriesAndPublishGoods() throws Exception {
        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(post("/api/goods")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "未登录发布",
                                  "price": 10,
                                  "conditionLevel": 4
                                }
                                """))
                .andExpect(status().isForbidden());

        var token = registerAndGetToken("seller");
        var createResult = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "Java 17 实战书",
                                  "description": "几乎全新，适合后端学习",
                                  "price": 39.90,
                                  "conditionLevel": 5,
                                  "imageUrls": [
                                    "https://example.com/book-1.jpg",
                                    "https://example.com/book-2.jpg"
                                  ]
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.imageUrls", hasSize(2)))
                .andReturn();

        Integer goodsId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/goods/{goodsId}", goodsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("Java 17 实战书"))
                .andExpect(jsonPath("$.data.imageUrls[0]").value("https://example.com/book-1.jpg"));
    }

    @Test
    void sellerCanManageOwnGoods() throws Exception {
        var sellerToken = registerAndGetToken("manager");
        var otherToken = registerAndGetToken("other");
        var goodsId = createGoods(sellerToken);

        mockMvc.perform(get("/api/goods/mine")
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(put("/api/goods/{goodsId}", goodsId)
                        .header("Authorization", "Bearer " + otherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody("别人不能改")))
                .andExpect(status().isNotFound());

        mockMvc.perform(put("/api/goods/{goodsId}", goodsId)
                        .header("Authorization", "Bearer " + sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateBody("更新后的标题")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("更新后的标题"))
                .andExpect(jsonPath("$.data.imageUrls", hasSize(1)))
                .andExpect(jsonPath("$.data.imageUrls[0]").value("https://example.com/updated.jpg"));

        mockMvc.perform(patch("/api/goods/{goodsId}/off-shelf", goodsId)
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OFF_SHELF"));

        mockMvc.perform(get("/api/goods/{goodsId}", goodsId))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/goods/{goodsId}/relist", goodsId)
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("ON_SALE"));
    }

    private Long createGoods(String token) throws Exception {
        var result = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "测试商品",
                                  "description": "测试描述",
                                  "price": 19.90,
                                  "conditionLevel": 4,
                                  "imageUrls": ["https://example.com/original.jpg"]
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();
        Integer goodsId = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
        return goodsId.longValue();
    }

    private String updateBody(String title) {
        return """
                {
                  "categoryId": 1,
                  "title": "%s",
                  "description": "更新后的描述",
                  "price": 29.90,
                  "conditionLevel": 3,
                  "imageUrls": ["https://example.com/updated.jpg"]
                }
                """.formatted(title);
    }

    private String registerAndGetToken(String prefix) throws Exception {
        var username = prefix + System.nanoTime();
        var result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "123456",
                                  "nickname": "卖家"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())))
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token").toString();
    }
}
