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
class GoodsFavoriteTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void userCanFavoriteListAndUnfavoriteGoods() throws Exception {
        var sellerToken = registerAndGetToken("favoriteSeller");
        var buyerToken = registerAndGetToken("favoriteBuyer");
        var goodsId = createGoods(sellerToken);

        mockMvc.perform(post("/api/goods/{goodsId}/favorite", goodsId))
                .andExpect(status().isForbidden());

        mockMvc.perform(post("/api/goods/{goodsId}/favorite", goodsId)
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isBadRequest());

        mockMvc.perform(post("/api/goods/{goodsId}/favorite", goodsId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/goods/{goodsId}/favorite", goodsId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/goods/favorites")
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)))
                .andExpect(jsonPath("$.data[0].id").value(goodsId));

        mockMvc.perform(delete("/api/goods/{goodsId}/favorite", goodsId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/goods/favorites")
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    private Long createGoods(String token) throws Exception {
        var result = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "可收藏商品",
                                  "description": "收藏测试商品",
                                  "price": 66.00,
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
                                  "nickname": "收藏用户"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())))
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token").toString();
    }
}
