package com.campus.market.goods;

import com.campus.market.common.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * GoodsController 业务组件。
 *
 * @author 阿德
 * @date 2026/05/12
 */
@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    private final GoodsService goodsService;

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @PostMapping
    public ApiResponse<GoodsResponse> create(Authentication authentication, @Valid @RequestBody CreateGoodsRequest request) {
        return ApiResponse.ok(goodsService.create(currentUserId(authentication), request));
    }

    @GetMapping
    public ApiResponse<List<GoodsResponse>> list(@RequestParam(required = false) Long categoryId) {
        return ApiResponse.ok(goodsService.listOnSale(categoryId));
    }

    @GetMapping("/{goodsId}")
    public ApiResponse<GoodsResponse> detail(@PathVariable Long goodsId) {
        return ApiResponse.ok(goodsService.detail(goodsId));
    }

    @GetMapping("/mine")
    public ApiResponse<List<GoodsResponse>> mine(Authentication authentication) {
        return ApiResponse.ok(goodsService.listMine(currentUserId(authentication)));
    }

    @GetMapping("/mine/{goodsId}")
    public ApiResponse<GoodsResponse> myDetail(Authentication authentication, @PathVariable Long goodsId) {
        return ApiResponse.ok(goodsService.myDetail(currentUserId(authentication), goodsId));
    }

    @GetMapping("/favorites")
    public ApiResponse<List<GoodsResponse>> favorites(Authentication authentication) {
        return ApiResponse.ok(goodsService.listFavorites(currentUserId(authentication)));
    }

    @PostMapping("/{goodsId}/favorite")
    public ApiResponse<Void> favorite(Authentication authentication, @PathVariable Long goodsId) {
        goodsService.favorite(currentUserId(authentication), goodsId);
        return ApiResponse.ok();
    }

    @DeleteMapping("/{goodsId}/favorite")
    public ApiResponse<Void> unfavorite(Authentication authentication, @PathVariable Long goodsId) {
        goodsService.unfavorite(currentUserId(authentication), goodsId);
        return ApiResponse.ok();
    }

    @GetMapping("/{goodsId}/comments")
    public ApiResponse<List<CommentResponse>> comments(@PathVariable Long goodsId) {
        return ApiResponse.ok(goodsService.listComments(goodsId));
    }

    @PostMapping("/{goodsId}/comments")
    public ApiResponse<CommentResponse> comment(Authentication authentication,
                                                @PathVariable Long goodsId,
                                                @Valid @RequestBody CreateCommentRequest request) {
        return ApiResponse.ok(goodsService.comment(currentUserId(authentication), goodsId, request));
    }

    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<Void> deleteComment(Authentication authentication, @PathVariable Long commentId) {
        goodsService.deleteComment(currentUserId(authentication), commentId);
        return ApiResponse.ok();
    }

    @PutMapping("/{goodsId}")
    public ApiResponse<GoodsResponse> update(Authentication authentication,
                                             @PathVariable Long goodsId,
                                             @Valid @RequestBody UpdateGoodsRequest request) {
        return ApiResponse.ok(goodsService.update(currentUserId(authentication), goodsId, request));
    }

    @PatchMapping("/{goodsId}/off-shelf")
    public ApiResponse<GoodsResponse> offShelf(Authentication authentication, @PathVariable Long goodsId) {
        return ApiResponse.ok(goodsService.offShelf(currentUserId(authentication), goodsId));
    }

    @PatchMapping("/{goodsId}/relist")
    public ApiResponse<GoodsResponse> relist(Authentication authentication, @PathVariable Long goodsId) {
        return ApiResponse.ok(goodsService.relist(currentUserId(authentication), goodsId));
    }

    private Long currentUserId(Authentication authentication) {
        return Long.parseLong(authentication.getName());
    }
}
