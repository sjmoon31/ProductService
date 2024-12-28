package com.shop.controller;

import com.shop.domain.enums.ProductType;
import com.shop.dto.ProductDTO;
import com.shop.dto.ReviewDTO;
import com.shop.service.FileService;
import com.shop.service.ProductService;
import com.shop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 상품 관련 Controller
 */
@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class ProductController {
    @Value("${root.filePath}")
    private String filePth;
    private final FileService fileService;
    private final ProductService productService;
    private final ReviewService reviewService;

    /**
     * 상품 목록 조회
     * @param page
     * @param type
     * @param searchStr
     * @return
     */
    @GetMapping("/getProductList")
    public ResponseEntity<Map<String, Object>> getProductList(@RequestHeader(value="email",required = false, defaultValue="") String email
            , @RequestParam(value="page",required = false, defaultValue="1") int page
            , @RequestParam(value="type",required = false, defaultValue="") String type
            , @RequestParam(value="searchStr",required = false, defaultValue="") String searchStr) {

        ProductType productType = null;
        if (!type.isEmpty()) {
            productType = ProductType.of(type);
        }
        Page<ProductDTO> productList = productService.selectProductList(page, 6, email, productType, searchStr);

        // 하나의 Map으로 모든 값을 묶어서 반환
        Map<String, Object> map = new HashMap<>();
        map.put("productList", productList);
        map.put("productCount", productService.selectProductCount());
        map.put("type", type);
        return ResponseEntity.ok(map);
    }

    /**
     * 상품 상세정보 조회
     * @param email
     * @param productSeq
     * @param page
     * @return
     */
    @GetMapping("/getProductInfo")
    public ResponseEntity<Map<String, Object>> getProductInfo(@RequestHeader(value="email",required = false, defaultValue="") String email
            , @RequestParam(value = "productSeq", required = false, defaultValue = "0L") long productSeq
            , @RequestParam(value="page",required = false, defaultValue="1") int page) {
        ProductDTO product = productService.selectProductInfo(productSeq, email);
        Page<ReviewDTO> reviewList = reviewService.selectReviewList(page,3,productSeq);
        // 하나의 Map으로 모든 값을 묶어서 반환
        Map<String, Object> map = new HashMap<>();
        map.put("product",product);
        map.put("reviewList",reviewList);
        map.put("reviewInfo", reviewService.selectReviewNumInfo(product.getProductSeq()));
        map.put("type", product.getProductType().getParentCategory().get().getCode());
        return ResponseEntity.ok(map);
    }
}
