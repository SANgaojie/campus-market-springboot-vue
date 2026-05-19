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
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class GoodsCommentTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userCanCommentAndAuthorOrSellerCanDelete() throws Exception {
        var sellerToken = registerAndGetToken("commentSeller");
        var buyerToken = registerAndGetToken("commentBuyer");
        var strangerToken = registerAndGetToken("commentStranger");
        var goodsId = createGoods(sellerToken);

        mockMvc.perform(get("/api/goods/{goodsId}/comments", goodsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(0)));

        mockMvc.perform(post("/api/goods/{goodsId}/comments", goodsId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "content": "未登录评论" }
                                """))
                .andExpect(status().isForbidden());

        var commentResult = mockMvc.perform(post("/api/goods/{goodsId}/comments", goodsId)
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "content": "  这个还在吗？  " }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").value("这个还在吗？"))
                .andReturn();
        Integer commentId = JsonPath.read(commentResult.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/goods/{goodsId}/comments", goodsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data[0].content").value("这个还在吗？"));

        mockMvc.perform(delete("/api/goods/comments/{commentId}", commentId)
                        .header("Authorization", "Bearer " + strangerToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(delete("/api/goods/comments/{commentId}", commentId)
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/goods/{goodsId}/comments", goodsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    @Test
    void blankCommentIsRejected() throws Exception {
        var sellerToken = registerAndGetToken("blankCommentSeller");
        var buyerToken = registerAndGetToken("blankCommentBuyer");
        var goodsId = createGoods(sellerToken);

        mockMvc.perform(post("/api/goods/{goodsId}/comments", goodsId)
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "content": "   " }
                                """))
                .andExpect(status().isBadRequest());
    }

    private Long createGoods(String token) throws Exception {
        var result = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "可评论商品",
                                  "description": "评论测试商品",
                                  "price": 77.00,
                                  "conditionLevel": 4,
                                  "imageUrls": []
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();
        Integer id = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
        return id.longValue();
    }

    private String registerAndGetToken(String prefix) throws Exception {
        var username = prefix + System.nanoTime();
        var result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "123456",
                                  "nickname": "评论用户"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())))
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token").toString();
    }
}
