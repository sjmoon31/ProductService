package com.shop.controller;

import com.shop.dto.HeartDTO;
import com.shop.dto.ProductDTO;
import com.shop.service.HeartService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 좋아요 관련 Controller
 */
@RestController
@RequiredArgsConstructor
public class HeartController {
    private final HeartService heartService;

    /**
     * 내가 좋아요한 상품 목록 조회
     * @param email
     * @param page
     * @return
     */
    @GetMapping("/getMyHeartList")
    public Map<String, Object>  getMyHeartList(@RequestHeader(value="email",required = false, defaultValue="") String email
            , @RequestParam(value = "page", defaultValue = "1") int page){
        Page<HeartDTO> myHeartList = heartService.selectHeartList(page, 10,email);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("myHeartList", myHeartList);
        return resultMap;
    }

    /**
     * 좋아요 등록/삭제
     * @param product
     * @return
     */
    @PostMapping("/updateHeartInfo")
    public ResponseEntity<Void> updateHeartInfo(@RequestHeader(value="email",required = false, defaultValue="") String email
            , @RequestBody ProductDTO product){
        product.setUserEmail(email);
        heartService.updateHeartInfo(product);
        return ResponseEntity.ok().build();
    }
}
