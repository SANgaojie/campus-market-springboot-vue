package com.campus.market.order;

import com.campus.market.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public ApiResponse<OrderResponse> create(Authentication authentication, @Valid @RequestBody CreateOrderRequest request) {
        return ApiResponse.ok(orderService.create(currentUserId(authentication), request));
    }

    @GetMapping("/bought")
    public ApiResponse<List<OrderResponse>> bought(Authentication authentication) {
        return ApiResponse.ok(orderService.listBought(currentUserId(authentication)));
    }

    @GetMapping("/sold")
    public ApiResponse<List<OrderResponse>> sold(Authentication authentication) {
        return ApiResponse.ok(orderService.listSold(currentUserId(authentication)));
    }

    @GetMapping("/{orderId}")
    public ApiResponse<OrderResponse> detail(Authentication authentication, @PathVariable Long orderId) {
        return ApiResponse.ok(orderService.detail(currentUserId(authentication), orderId));
    }

    @PatchMapping("/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancel(Authentication authentication, @PathVariable Long orderId) {
        return ApiResponse.ok(orderService.cancel(currentUserId(authentication), orderId));
    }

    @PatchMapping("/{orderId}/pay")
    public ApiResponse<OrderResponse> pay(Authentication authentication, @PathVariable Long orderId) {
        return ApiResponse.ok(orderService.pay(currentUserId(authentication), orderId));
    }

    @PatchMapping("/{orderId}/refund")
    public ApiResponse<OrderResponse> requestRefund(Authentication authentication, @PathVariable Long orderId) {
        return ApiResponse.ok(orderService.requestRefund(currentUserId(authentication), orderId));
    }

    @PatchMapping("/{orderId}/complete")
    public ApiResponse<OrderResponse> complete(Authentication authentication, @PathVariable Long orderId) {
        return ApiResponse.ok(orderService.complete(currentUserId(authentication), orderId));
    }

    private Long currentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
