package com.campus.market.goods;

import com.campus.market.common.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class GoodsCategoryController {

    private final GoodsService goodsService;

    public GoodsCategoryController(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @GetMapping
    public ApiResponse<List<CategoryResponse>> list() {
        return ApiResponse.ok(goodsService.listEnabledCategories());
    }
}
