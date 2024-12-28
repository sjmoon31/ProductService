package com.shop.controller;

import com.shop.domain.enums.ProductType;
import com.shop.dto.ProductDTO;
import com.shop.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
@RequiredArgsConstructor
public class MainController {
    private final ProductService productService;

    /**
     * 메인페이지 호출
     * @return
     */
    @GetMapping("/main")
    public ResponseEntity<Map<String, Object>> main() {
        Page<ProductDTO> productList = productService.selectProductList(1,8, "sjmoon752@gmail.com", null, null);
        // 하나의 Map으로 모든 값을 묶어서 반환
        Map<String, Object> response = new HashMap<>();
        response.put("productList", productList);
        response.put("productCount", productService.selectProductCount());
        response.put("productType", Arrays.asList(ProductType.values()));
        return ResponseEntity.ok(response);
    }
}
