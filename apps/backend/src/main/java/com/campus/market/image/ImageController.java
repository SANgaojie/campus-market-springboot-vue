package com.campus.market.image;

import com.campus.market.common.ApiResponse;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/images")
public class ImageController {

    private final FileStorageService fileStorageService;

    public ImageController(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @PostMapping("/goods")
    public ApiResponse<UploadResponse> uploadGoodsImage(@RequestParam("file") MultipartFile file) {
        return ApiResponse.ok(fileStorageService.storeGoodsImage(file));
    }
}
