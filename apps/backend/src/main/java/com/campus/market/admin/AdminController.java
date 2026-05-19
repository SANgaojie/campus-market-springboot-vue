package com.campus.market.admin;

import com.campus.market.common.ApiResponse;
import com.campus.market.goods.CategoryManageRequest;
import com.campus.market.goods.CategoryResponse;
import com.campus.market.goods.CommentResponse;
import com.campus.market.goods.GoodsResponse;
import com.campus.market.goods.GoodsService;
import com.campus.market.order.OrderResponse;
import com.campus.market.order.OrderService;
import com.campus.market.order.OrderStatus;
import com.campus.market.user.UserProfileResponse;
import com.campus.market.user.UserService;
import com.campus.market.user.UserStatus;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;
    private final GoodsService goodsService;
    private final OrderService orderService;
    private final AdminDashboardService adminDashboardService;

    public AdminController(UserService userService,
                           GoodsService goodsService,
                           OrderService orderService,
                           AdminDashboardService adminDashboardService) {
        this.userService = userService;
        this.goodsService = goodsService;
        this.orderService = orderService;
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/dashboard")
    public ApiResponse<AdminDashboardResponse> dashboard() {
        return ApiResponse.ok(adminDashboardService.summary());
    }

    @GetMapping("/users")
    public ApiResponse<List<UserProfileResponse>> users() {
        return ApiResponse.ok(userService.listUsers());
    }

    @PatchMapping("/users/{userId}/enable")
    public ApiResponse<UserProfileResponse> enableUser(@PathVariable Long userId) {
        return ApiResponse.ok(userService.updateStatus(userId, UserStatus.ENABLED));
    }

    @PatchMapping("/users/{userId}/disable")
    public ApiResponse<UserProfileResponse> disableUser(@PathVariable Long userId) {
        return ApiResponse.ok(userService.updateStatus(userId, UserStatus.DISABLED));
    }

    @GetMapping("/goods")
    public ApiResponse<List<GoodsResponse>> goods() {
        return ApiResponse.ok(goodsService.listAllForAdmin());
    }

    @PatchMapping("/goods/{goodsId}/off-shelf")
    public ApiResponse<GoodsResponse> offShelfGoods(@PathVariable Long goodsId) {
        return ApiResponse.ok(goodsService.adminOffShelf(goodsId));
    }

    @GetMapping("/orders")
    public ApiResponse<List<OrderResponse>> orders(@RequestParam(required = false) OrderStatus status) {
        return ApiResponse.ok(orderService.listAllForAdmin(status));
    }

    @PatchMapping("/orders/{orderId}/cancel")
    public ApiResponse<OrderResponse> cancelOrder(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.adminCancel(orderId));
    }

    @PatchMapping("/orders/{orderId}/refund")
    public ApiResponse<OrderResponse> refundOrder(@PathVariable Long orderId) {
        return ApiResponse.ok(orderService.adminRefund(orderId));
    }

    @GetMapping("/categories")
    public ApiResponse<List<CategoryResponse>> categories() {
        return ApiResponse.ok(goodsService.listAllCategoriesForAdmin());
    }

    @PostMapping("/categories")
    public ApiResponse<CategoryResponse> createCategory(@Valid @RequestBody CategoryManageRequest request) {
        return ApiResponse.ok(goodsService.createCategory(request));
    }

    @PatchMapping("/categories/{categoryId}")
    public ApiResponse<CategoryResponse> updateCategory(@PathVariable Long categoryId,
                                                        @Valid @RequestBody CategoryManageRequest request) {
        return ApiResponse.ok(goodsService.updateCategory(categoryId, request));
    }

    @PatchMapping("/categories/{categoryId}/enable")
    public ApiResponse<CategoryResponse> enableCategory(@PathVariable Long categoryId) {
        return ApiResponse.ok(goodsService.setCategoryEnabled(categoryId, true));
    }

    @PatchMapping("/categories/{categoryId}/disable")
    public ApiResponse<CategoryResponse> disableCategory(@PathVariable Long categoryId) {
        return ApiResponse.ok(goodsService.setCategoryEnabled(categoryId, false));
    }

    @GetMapping("/comments")
    public ApiResponse<List<CommentResponse>> comments() {
        return ApiResponse.ok(goodsService.listAllCommentsForAdmin());
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(@PathVariable Long commentId) {
        goodsService.adminDeleteComment(commentId);
        return ApiResponse.ok();
    }
}
