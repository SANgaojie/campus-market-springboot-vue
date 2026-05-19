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
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class AdminDatabaseTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void bootstrapAdminCanAccessAdminApis() throws Exception {
        var adminToken = loginAndGetToken("admin", "admin123456");

        mockMvc.perform(get("/api/auth/me").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.roles", hasItem("ROLE_ADMIN")));

        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/admin/categories").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/admin/goods").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/orders").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/comments").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/admin/dashboard").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userCount").isNumber())
                .andExpect(jsonPath("$.data.goodsCount").isNumber())
                .andExpect(jsonPath("$.data.orderCount").isNumber())
                .andExpect(jsonPath("$.data.commentCount").isNumber());
    }

    @Test
    void normalUserCannotAccessAdminApisAndAdminCanModerateGoods() throws Exception {
        var adminToken = loginAndGetToken("admin", "admin123456");
        var userToken = registerAndGetToken("adminApiUser");
        var goodsId = createGoods(userToken);

        mockMvc.perform(get("/api/admin/users").header("Authorization", "Bearer " + userToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/admin/goods/{goodsId}/off-shelf", goodsId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("OFF_SHELF"));

        mockMvc.perform(get("/api/goods/{goodsId}", goodsId))
                .andExpect(status().isNotFound());
    }

    @Test
    void adminCanManageCategories() throws Exception {
        var adminToken = loginAndGetToken("admin", "admin123456");
        var userToken = registerAndGetToken("adminCategoryUser");
        var categoryName = "测试分类" + System.nanoTime();

        mockMvc.perform(post("/api/admin/categories")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "%s", "sortOrder": 90 }
                                """.formatted(categoryName)))
                .andExpect(status().isForbidden());

        var createResult = mockMvc.perform(post("/api/admin/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "%s", "sortOrder": 90 }
                                """.formatted(categoryName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(categoryName))
                .andExpect(jsonPath("$.data.enabled").value(1))
                .andReturn();
        Integer categoryId = JsonPath.read(createResult.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(patch("/api/admin/categories/{categoryId}", categoryId)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "name": "%s更新", "sortOrder": 95 }
                                """.formatted(categoryName)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.name").value(categoryName + "更新"))
                .andExpect(jsonPath("$.data.sortOrder").value(95));

        mockMvc.perform(patch("/api/admin/categories/{categoryId}/disable", categoryId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(0));

        mockMvc.perform(get("/api/categories"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[*].id", not(hasItem(categoryId))));

        mockMvc.perform(patch("/api/admin/categories/{categoryId}/enable", categoryId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.enabled").value(1));
    }

    @Test
    void adminCanFilterAndCancelOrders() throws Exception {
        var adminToken = loginAndGetToken("admin", "admin123456");
        var sellerToken = registerAndGetToken("adminOrderSeller");
        var buyerToken = registerAndGetToken("adminOrderBuyer");
        var goodsId = createGoods(sellerToken);
        var orderResult = mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "goodsId": %d }
                                """.formatted(goodsId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PENDING_PAYMENT"))
                .andReturn();
        Integer orderId = JsonPath.read(orderResult.getResponse().getContentAsString(), "$.data.id");

        mockMvc.perform(get("/api/admin/orders")
                        .param("status", "PENDING_PAYMENT")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].status").value("PENDING_PAYMENT"));

        mockMvc.perform(patch("/api/admin/orders/{orderId}/cancel", orderId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/admin/orders/{orderId}/cancel", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELED"));

        mockMvc.perform(get("/api/goods/{goodsId}", goodsId))
                .andExpect(status().isOk());
    }

    @Test
    void adminCanModerateComments() throws Exception {
        var adminToken = loginAndGetToken("admin", "admin123456");
        var sellerToken = registerAndGetToken("adminCommentSeller");
        var buyerToken = registerAndGetToken("adminCommentBuyer");
        var goodsId = createGoods(sellerToken);
        var commentId = createComment(buyerToken, goodsId);

        mockMvc.perform(get("/api/admin/comments").header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].id").exists());

        mockMvc.perform(delete("/api/admin/comments/{commentId}", commentId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/admin/comments/{commentId}", commentId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/goods/{goodsId}/comments", goodsId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()").value(0));
    }

    private Long createComment(String token, Long goodsId) throws Exception {
        var result = mockMvc.perform(post("/api/goods/{goodsId}/comments", goodsId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                { "content": "管理员需要审核的评论" }
                                """))
                .andExpect(status().isOk())
                .andReturn();
        Integer commentId = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
        return commentId.longValue();
    }

    private Long createGoods(String token) throws Exception {
        var result = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "管理员测试商品",
                                  "description": "用于后台强制下架测试",
                                  "price": 66.00,
                                  "conditionLevel": 4,
                                  "imageUrls": []
                                }
                                """))
                .andExpect(status().isOk())
                .andReturn();
        Integer goodsId = JsonPath.read(result.getResponse().getContentAsString(), "$.data.id");
        return goodsId.longValue();
    }

    private String registerAndGetToken(String prefix) throws Exception {
        var username = prefix + System.nanoTime();
        var result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "123456",
                                  "nickname": "普通用户"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())))
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token").toString();
    }

    private String loginAndGetToken(String username, String password) throws Exception {
        var result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "%s"
                                }
                                """.formatted(username, password)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())))
                .andReturn();
        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token").toString();
    }
}
