package com.productservice.services.products;

import com.productservice.dto.PutObjectDto;
import com.productservice.services.S3Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.CompletableFuture;
import static com.productservice.utils.Utils.getUUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class ProductImageService {
    private final S3Service s3Service;
    private static final String PRODUCT_IMAGE_BUCKET = "shopic-product-image";
    private final ProductQueryService productQueryService;


    public CompletableFuture<String> updateProductImage(long productId, MultipartFile productImage) {
        String imageUrl = productQueryService.getProductImageUrl(productId);

        return uploadProductImage(productImage)
                .thenApply(newImageUrl -> {
                    deleteImage(imageUrl);
                    return newImageUrl;
                });
    }

    public CompletableFuture<String> uploadProductImage(MultipartFile productImage) {
        return s3Service.uploadFile(new PutObjectDto(
                PRODUCT_IMAGE_BUCKET,
                getUUID().toString(),
                productImage
        ));
    }

    public void deleteImage(String imageUrl) {
        try {
            URI uri = new URI(imageUrl);
            String host = uri.getHost();
            String bucket = host.split("\\.")[0];
            String key = uri.getPath().substring(1);

            s3Service.delete(bucket, key);
        } catch (URISyntaxException e) {
            log.warn(e.getMessage());
        }
    }
}
