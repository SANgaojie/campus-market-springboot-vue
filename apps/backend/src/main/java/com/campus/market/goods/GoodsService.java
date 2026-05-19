package com.campus.market.goods;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.campus.market.common.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * GoodsService 业务组件。
 *
 * @author 阿德
 * @date 2026/05/07
 */
@Service
public class GoodsService {

    private final GoodsMapper goodsMapper;
    private final GoodsCategoryMapper goodsCategoryMapper;
    private final GoodsImageMapper goodsImageMapper;
    private final GoodsFavoriteMapper goodsFavoriteMapper;
    private final GoodsCommentMapper goodsCommentMapper;

    public GoodsService(GoodsMapper goodsMapper,
                        GoodsCategoryMapper goodsCategoryMapper,
                        GoodsImageMapper goodsImageMapper,
                        GoodsFavoriteMapper goodsFavoriteMapper,
                        GoodsCommentMapper goodsCommentMapper) {
        this.goodsMapper = goodsMapper;
        this.goodsCategoryMapper = goodsCategoryMapper;
        this.goodsImageMapper = goodsImageMapper;
        this.goodsFavoriteMapper = goodsFavoriteMapper;
        this.goodsCommentMapper = goodsCommentMapper;
    }

    public List<CategoryResponse> listEnabledCategories() {
        return goodsCategoryMapper.selectList(new LambdaQueryWrapper<GoodsCategory>()
                        .eq(GoodsCategory::getEnabled, 1)
                        .orderByAsc(GoodsCategory::getSortOrder)
                        .orderByAsc(GoodsCategory::getId))
                .stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    public List<CategoryResponse> listAllCategoriesForAdmin() {
        return goodsCategoryMapper.selectList(new LambdaQueryWrapper<GoodsCategory>()
                        .orderByAsc(GoodsCategory::getSortOrder)
                        .orderByAsc(GoodsCategory::getId))
                .stream()
                .map(this::toCategoryResponse)
                .toList();
    }

    @Transactional
    public CategoryResponse createCategory(CategoryManageRequest request) {
        ensureCategoryNameAvailable(null, request.name().trim());
        var category = new GoodsCategory();
        category.setName(request.name().trim());
        category.setSortOrder(request.sortOrder());
        category.setEnabled(1);
        goodsCategoryMapper.insert(category);
        return toCategoryResponse(category);
    }

    @Transactional
    public CategoryResponse updateCategory(Long categoryId, CategoryManageRequest request) {
        var category = getCategory(categoryId);
        var name = request.name().trim();
        ensureCategoryNameAvailable(categoryId, name);
        category.setName(name);
        category.setSortOrder(request.sortOrder());
        goodsCategoryMapper.updateById(category);
        return toCategoryResponse(goodsCategoryMapper.selectById(categoryId));
    }

    @Transactional
    public CategoryResponse setCategoryEnabled(Long categoryId, boolean enabled) {
        var category = getCategory(categoryId);
        category.setEnabled(enabled ? 1 : 0);
        goodsCategoryMapper.updateById(category);
        return toCategoryResponse(goodsCategoryMapper.selectById(categoryId));
    }

    @Transactional
    public GoodsResponse create(Long sellerId, CreateGoodsRequest request) {
        ensureCategoryEnabled(request.categoryId());

        var goods = new Goods();
        goods.setSellerId(sellerId);
        applyEditableFields(goods, request.categoryId(), request.title(), request.description(), request.price(), request.conditionLevel());
        goods.setStatus(GoodsStatus.ON_SALE.name());
        goods.setVersion(0);
        goodsMapper.insert(goods);
        replaceImages(goods.getId(), request.imageUrls());

        return toResponse(goods);
    }

    public List<GoodsResponse> listOnSale(Long categoryId) {
        var query = new LambdaQueryWrapper<Goods>()
                .eq(Goods::getStatus, GoodsStatus.ON_SALE.name())
                .orderByDesc(Goods::getCreatedAt);
        if (categoryId != null) {
            query.eq(Goods::getCategoryId, categoryId);
        }
        return goodsMapper.selectList(query).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<GoodsResponse> listAllForAdmin() {
        return goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                        .orderByDesc(Goods::getCreatedAt))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public List<GoodsResponse> listMine(Long sellerId) {
        return goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                        .eq(Goods::getSellerId, sellerId)
                        .orderByDesc(Goods::getCreatedAt))
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public GoodsResponse detail(Long goodsId) {
        var goods = goodsMapper.selectById(goodsId);
        if (goods == null || !GoodsStatus.ON_SALE.name().equals(goods.getStatus())) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
        return toResponse(goods);
    }

    public List<GoodsResponse> listFavorites(Long userId) {
        var favorites = goodsFavoriteMapper.selectList(new LambdaQueryWrapper<GoodsFavorite>()
                .eq(GoodsFavorite::getUserId, userId)
                .orderByDesc(GoodsFavorite::getCreatedAt));
        if (favorites.isEmpty()) {
            return List.of();
        }
        var goodsIds = favorites.stream().map(GoodsFavorite::getGoodsId).toList();
        var goodsById = goodsMapper.selectList(new LambdaQueryWrapper<Goods>()
                        .in(Goods::getId, goodsIds))
                .stream()
                .collect(java.util.stream.Collectors.toMap(Goods::getId, goods -> goods));
        return favorites.stream()
                .map(favorite -> goodsById.get(favorite.getGoodsId()))
                .filter(goods -> goods != null && GoodsStatus.ON_SALE.name().equals(goods.getStatus()))
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    public void favorite(Long userId, Long goodsId) {
        var goods = goodsMapper.selectById(goodsId);
        if (goods == null || !GoodsStatus.ON_SALE.name().equals(goods.getStatus())) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
        if (userId.equals(goods.getSellerId())) {
            throw new BusinessException(400, "不能收藏自己的商品");
        }
        var existing = goodsFavoriteMapper.selectOne(new LambdaQueryWrapper<GoodsFavorite>()
                .eq(GoodsFavorite::getUserId, userId)
                .eq(GoodsFavorite::getGoodsId, goodsId));
        if (existing != null) {
            return;
        }
        var favorite = new GoodsFavorite();
        favorite.setUserId(userId);
        favorite.setGoodsId(goodsId);
        goodsFavoriteMapper.insert(favorite);
    }

    @Transactional
    public void unfavorite(Long userId, Long goodsId) {
        goodsFavoriteMapper.delete(new LambdaQueryWrapper<GoodsFavorite>()
                .eq(GoodsFavorite::getUserId, userId)
                .eq(GoodsFavorite::getGoodsId, goodsId));
    }

    public GoodsResponse myDetail(Long sellerId, Long goodsId) {
        return toResponse(getOwnedGoods(sellerId, goodsId));
    }

    public List<CommentResponse> listComments(Long goodsId) {
        ensureVisibleGoods(goodsId);
        return goodsCommentMapper.selectList(new LambdaQueryWrapper<GoodsComment>()
                        .eq(GoodsComment::getGoodsId, goodsId)
                        .eq(GoodsComment::getDeleted, 0)
                        .orderByAsc(GoodsComment::getCreatedAt)
                        .orderByAsc(GoodsComment::getId))
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    public List<CommentResponse> listAllCommentsForAdmin() {
        return goodsCommentMapper.selectList(new LambdaQueryWrapper<GoodsComment>()
                        .orderByDesc(GoodsComment::getCreatedAt)
                        .orderByDesc(GoodsComment::getId))
                .stream()
                .map(this::toCommentResponse)
                .toList();
    }

    @Transactional
    public void adminDeleteComment(Long commentId) {
        var comment = goodsCommentMapper.selectById(commentId);
        if (comment == null) {
            throw new BusinessException(404, "评论不存在");
        }
        if (comment.getDeleted() == null || comment.getDeleted() == 0) {
            comment.setDeleted(1);
            goodsCommentMapper.updateById(comment);
        }
    }

    @Transactional
    public CommentResponse comment(Long userId, Long goodsId, CreateCommentRequest request) {
        ensureVisibleGoods(goodsId);
        var content = request.content() == null ? "" : request.content().trim();
        if (content.isBlank()) {
            throw new BusinessException(400, "评论内容不能为空");
        }
        var comment = new GoodsComment();
        comment.setUserId(userId);
        comment.setGoodsId(goodsId);
        comment.setContent(content);
        comment.setDeleted(0);
        goodsCommentMapper.insert(comment);
        return toCommentResponse(comment);
    }

    @Transactional
    public void deleteComment(Long userId, Long commentId) {
        var comment = goodsCommentMapper.selectById(commentId);
        if (comment == null || comment.getDeleted() == 1) {
            throw new BusinessException(404, "评论不存在");
        }
        var goods = goodsMapper.selectById(comment.getGoodsId());
        if (!userId.equals(comment.getUserId()) && (goods == null || !userId.equals(goods.getSellerId()))) {
            throw new BusinessException(404, "评论不存在");
        }
        comment.setDeleted(1);
        goodsCommentMapper.updateById(comment);
    }

    @Transactional
    public GoodsResponse update(Long sellerId, Long goodsId, UpdateGoodsRequest request) {
        ensureCategoryEnabled(request.categoryId());
        var goods = getOwnedGoods(sellerId, goodsId);
        ensureEditable(goods);

        applyEditableFields(goods, request.categoryId(), request.title(), request.description(), request.price(), request.conditionLevel());
        goodsMapper.updateById(goods);
        replaceImages(goodsId, request.imageUrls());

        return toResponse(goodsMapper.selectById(goodsId));
    }

    @Transactional
    public GoodsResponse offShelf(Long sellerId, Long goodsId) {
        var goods = getOwnedGoods(sellerId, goodsId);
        if (GoodsStatus.SOLD.name().equals(goods.getStatus()) || GoodsStatus.LOCKED.name().equals(goods.getStatus())) {
            throw new BusinessException(400, "交易中或已售出的商品不能下架");
        }
        goods.setStatus(GoodsStatus.OFF_SHELF.name());
        goodsMapper.updateById(goods);
        return toResponse(goodsMapper.selectById(goodsId));
    }

    @Transactional
    public GoodsResponse adminOffShelf(Long goodsId) {
        var goods = goodsMapper.selectById(goodsId);
        if (goods == null) {
            throw new BusinessException(404, "商品不存在");
        }
        if (GoodsStatus.SOLD.name().equals(goods.getStatus()) || GoodsStatus.LOCKED.name().equals(goods.getStatus())) {
            throw new BusinessException(400, "交易中或已售出的商品不能下架");
        }
        goods.setStatus(GoodsStatus.OFF_SHELF.name());
        goodsMapper.updateById(goods);
        return toResponse(goodsMapper.selectById(goodsId));
    }

    @Transactional
    public GoodsResponse relist(Long sellerId, Long goodsId) {
        var goods = getOwnedGoods(sellerId, goodsId);
        if (!GoodsStatus.OFF_SHELF.name().equals(goods.getStatus())) {
            throw new BusinessException(400, "只有已下架商品可以重新上架");
        }
        goods.setStatus(GoodsStatus.ON_SALE.name());
        goodsMapper.updateById(goods);
        return toResponse(goodsMapper.selectById(goodsId));
    }

    private void applyEditableFields(Goods goods, Long categoryId, String title, String description,
                                     java.math.BigDecimal price, Integer conditionLevel) {
        goods.setCategoryId(categoryId);
        goods.setTitle(title);
        goods.setDescription(description);
        goods.setPrice(price);
        goods.setConditionLevel(conditionLevel);
    }

    private void replaceImages(Long goodsId, List<String> imageUrls) {
        goodsImageMapper.delete(new LambdaUpdateWrapper<GoodsImage>().eq(GoodsImage::getGoodsId, goodsId));
        if (imageUrls == null || imageUrls.isEmpty()) {
            return;
        }
        for (int i = 0; i < imageUrls.size(); i++) {
            var image = new GoodsImage();
            image.setGoodsId(goodsId);
            image.setImageUrl(imageUrls.get(i));
            image.setSortOrder(i);
            goodsImageMapper.insert(image);
        }
    }

    private void ensureVisibleGoods(Long goodsId) {
        var goods = goodsMapper.selectById(goodsId);
        if (goods == null || !GoodsStatus.ON_SALE.name().equals(goods.getStatus())) {
            throw new BusinessException(404, "商品不存在或已下架");
        }
    }

    private Goods getOwnedGoods(Long sellerId, Long goodsId) {
        var goods = goodsMapper.selectById(goodsId);
        if (goods == null || !sellerId.equals(goods.getSellerId())) {
            throw new BusinessException(404, "商品不存在");
        }
        return goods;
    }

    private void ensureEditable(Goods goods) {
        if (GoodsStatus.SOLD.name().equals(goods.getStatus()) || GoodsStatus.LOCKED.name().equals(goods.getStatus())) {
            throw new BusinessException(400, "交易中或已售出的商品不能编辑");
        }
    }

    private void ensureCategoryEnabled(Long categoryId) {
        var category = goodsCategoryMapper.selectOne(new LambdaQueryWrapper<GoodsCategory>()
                .eq(GoodsCategory::getId, categoryId)
                .eq(GoodsCategory::getEnabled, 1));
        if (category == null) {
            throw new BusinessException(404, "商品分类不存在或已禁用");
        }
    }

    private GoodsCategory getCategory(Long categoryId) {
        var category = goodsCategoryMapper.selectById(categoryId);
        if (category == null) {
            throw new BusinessException(404, "分类不存在");
        }
        return category;
    }

    private void ensureCategoryNameAvailable(Long currentCategoryId, String name) {
        var existing = goodsCategoryMapper.selectOne(new LambdaQueryWrapper<GoodsCategory>()
                .eq(GoodsCategory::getName, name));
        if (existing != null && !existing.getId().equals(currentCategoryId)) {
            throw new BusinessException(400, "分类名称已存在");
        }
    }

    private List<String> listImages(Long goodsId) {
        return goodsImageMapper.selectList(new LambdaQueryWrapper<GoodsImage>()
                        .eq(GoodsImage::getGoodsId, goodsId)
                        .orderByAsc(GoodsImage::getSortOrder)
                        .orderByAsc(GoodsImage::getId))
                .stream()
                .map(GoodsImage::getImageUrl)
                .toList();
    }

    private CategoryResponse toCategoryResponse(GoodsCategory category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getSortOrder(),
                category.getEnabled()
        );
    }

    private CommentResponse toCommentResponse(GoodsComment comment) {
        return new CommentResponse(
                comment.getId(),
                comment.getGoodsId(),
                comment.getUserId(),
                comment.getContent(),
                comment.getDeleted(),
                comment.getCreatedAt()
        );
    }

    private GoodsResponse toResponse(Goods goods) {
        return new GoodsResponse(
                goods.getId(),
                goods.getSellerId(),
                goods.getCategoryId(),
                goods.getTitle(),
                goods.getDescription(),
                goods.getPrice(),
                goods.getConditionLevel(),
                GoodsStatus.valueOf(goods.getStatus()),
                goods.getCreatedAt(),
                listImages(goods.getId())
        );
    }
}
