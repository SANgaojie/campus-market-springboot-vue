package com.campus.market.image;

import com.campus.market.common.BusinessException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * FileStorageService 业务组件。
 *
 * @author 阿德
 * @date 2026/05/10
 */
@Service
public class FileStorageService {

    private static final long MAX_IMAGE_BYTES = 5 * 1024 * 1024;
    private static final Map<String, String> EXTENSIONS_BY_CONTENT_TYPE = Map.of(
            "image/jpeg", ".jpg",
            "image/png", ".png",
            "image/webp", ".webp",
            "image/gif", ".gif"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp", ".gif");

    private final Path uploadRoot;
    private final String publicPath;

    public FileStorageService(FileStorageProperties properties) {
        this.uploadRoot = Path.of(properties.uploadDir()).toAbsolutePath().normalize();
        this.publicPath = normalizePublicPath(properties.publicPath());
    }

    public UploadResponse storeGoodsImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请选择要上传的图片");
        }
        if (file.getSize() > MAX_IMAGE_BYTES) {
            throw new BusinessException(400, "图片不能超过 5MB");
        }

        var extension = resolveExtension(file);
        var now = LocalDate.now();
        var relativeDir = Path.of("goods", String.valueOf(now.getYear()), "%02d".formatted(now.getMonthValue()));
        var filename = UUID.randomUUID() + extension;
        var targetDir = uploadRoot.resolve(relativeDir).normalize();
        var target = targetDir.resolve(filename).normalize();

        if (!target.startsWith(uploadRoot)) {
            throw new BusinessException(400, "非法文件路径");
        }

        try {
            Files.createDirectories(targetDir);
            file.transferTo(target);
        } catch (IOException exception) {
            throw new BusinessException(500, "图片保存失败");
        }

        var url = publicPath + "/" + relativeDir.resolve(filename).toString().replace('\\', '/');
        return new UploadResponse(url);
    }

    private String resolveExtension(MultipartFile file) {
        var contentType = file.getContentType();
        if (contentType != null) {
            var normalized = contentType.toLowerCase(Locale.ROOT);
            if (EXTENSIONS_BY_CONTENT_TYPE.containsKey(normalized)) {
                return EXTENSIONS_BY_CONTENT_TYPE.get(normalized);
            }
        }

        var originalFilename = StringUtils.cleanPath(file.getOriginalFilename() == null ? "" : file.getOriginalFilename());
        var dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            var extension = originalFilename.substring(dotIndex).toLowerCase(Locale.ROOT);
            if (ALLOWED_EXTENSIONS.contains(extension)) {
                return extension.equals(".jpeg") ? ".jpg" : extension;
            }
        }

        throw new BusinessException(400, "仅支持 jpg、png、webp、gif 图片");
    }

    private String normalizePublicPath(String value) {
        var normalized = value == null || value.isBlank() ? "/uploads" : value.trim();
        if (!normalized.startsWith("/")) {
            normalized = "/" + normalized;
        }
        return normalized.endsWith("/") ? normalized.substring(0, normalized.length() - 1) : normalized;
    }
}
