package com.campus.market;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import static org.hamcrest.Matchers.blankOrNullString;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class OrderDatabaseTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void buyerCanCreateCancelAndRecreateOrder() throws Exception {
        var sellerToken = registerAndGetToken("orderSeller");
        var buyerToken = registerAndGetToken("orderBuyer");
        var goodsId = createGoods(sellerToken, "可取消商品");

        var orderResult = createOrder(buyerToken, goodsId)
                .andExpect(jsonPath("$.data.status").value("PENDING_PAYMENT"))
                .andReturn();
        var orderId = readId(orderResult.getResponse().getContentAsString());

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderBody(goodsId)))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/orders/{orderId}/cancel", orderId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("CANCELED"));

        createOrder(buyerToken, goodsId)
                .andExpect(jsonPath("$.data.status").value("PENDING_PAYMENT"));
    }

    @Test
    void orderPaymentAndCompletionMarksGoodsSold() throws Exception {
        var sellerToken = registerAndGetToken("completeSeller");
        var buyerToken = registerAndGetToken("completeBuyer");
        var strangerToken = registerAndGetToken("stranger");
        var goodsId = createGoods(sellerToken, "可完成商品");

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + sellerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderBody(goodsId)))
                .andExpect(status().isBadRequest());

        var orderResult = createOrder(buyerToken, goodsId).andReturn();
        var orderId = readId(orderResult.getResponse().getContentAsString());

        mockMvc.perform(get("/api/orders/bought")
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/orders/sold")
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.length()", greaterThanOrEqualTo(1)));

        mockMvc.perform(get("/api/orders/{orderId}", orderId)
                        .header("Authorization", "Bearer " + strangerToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/orders/{orderId}/complete", orderId)
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/api/orders/{orderId}/pay", orderId)
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/orders/{orderId}/pay", orderId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));

        mockMvc.perform(patch("/api/orders/{orderId}/cancel", orderId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/api/orders/{orderId}/complete", orderId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/orders/{orderId}/complete", orderId)
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("COMPLETED"));

        mockMvc.perform(get("/api/goods/{goodsId}", goodsId))
                .andExpect(status().isNotFound());

        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderBody(goodsId)))
                .andExpect(status().isNotFound());
    }

    @Test
    void buyerCanRequestRefundAndAdminCanApprove() throws Exception {
        var sellerToken = registerAndGetToken("refundSeller");
        var buyerToken = registerAndGetToken("refundBuyer");
        var goodsId = createGoods(sellerToken, "退款商品");
        var orderResult = createOrder(buyerToken, goodsId).andReturn();
        var orderId = readId(orderResult.getResponse().getContentAsString());

        mockMvc.perform(patch("/api/orders/{orderId}/refund", orderId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isBadRequest());

        mockMvc.perform(patch("/api/orders/{orderId}/pay", orderId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("PAID"));

        mockMvc.perform(patch("/api/orders/{orderId}/refund", orderId)
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isNotFound());

        mockMvc.perform(patch("/api/orders/{orderId}/refund", orderId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REFUNDING"));

        mockMvc.perform(patch("/api/orders/{orderId}/complete", orderId)
                        .header("Authorization", "Bearer " + sellerToken))
                .andExpect(status().isBadRequest());

        var adminToken = loginAndGetToken("admin", "admin123456");
        mockMvc.perform(patch("/api/admin/orders/{orderId}/refund", orderId)
                        .header("Authorization", "Bearer " + buyerToken))
                .andExpect(status().isForbidden());

        mockMvc.perform(patch("/api/admin/orders/{orderId}/refund", orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.status").value("REFUNDED"));

        mockMvc.perform(get("/api/goods/{goodsId}", goodsId))
                .andExpect(status().isOk());
    }

    private ResultActions createOrder(String buyerToken, Long goodsId) throws Exception {
        return mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + buyerToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(createOrderBody(goodsId)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.id").exists())
                .andExpect(jsonPath("$.data.orderNo", not(blankOrNullString())));
    }

    private Long createGoods(String token, String title) throws Exception {
        var result = mockMvc.perform(post("/api/goods")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "categoryId": 1,
                                  "title": "%s",
                                  "description": "订单测试商品",
                                  "price": 88.00,
                                  "conditionLevel": 4,
                                  "imageUrls": ["https://example.com/order.jpg"]
                                }
                                """.formatted(title)))
                .andExpect(status().isOk())
                .andReturn();
        return readId(result.getResponse().getContentAsString());
    }

    private Long readId(String responseBody) {
        Integer id = JsonPath.read(responseBody, "$.data.id");
        return id.longValue();
    }

    private String createOrderBody(Long goodsId) {
        return """
                {
                  "goodsId": %d
                }
                """.formatted(goodsId);
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

    private String registerAndGetToken(String prefix) throws Exception {
        var username = prefix + System.nanoTime();
        var result = mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "username": "%s",
                                  "password": "123456",
                                  "nickname": "订单用户"
                                }
                                """.formatted(username)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.token", not(blankOrNullString())))
                .andReturn();

        return JsonPath.read(result.getResponse().getContentAsString(), "$.data.token").toString();
    }
}
