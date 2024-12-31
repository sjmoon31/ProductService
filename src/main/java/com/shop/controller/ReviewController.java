package com.shop.controller;

import com.shop.dto.ProductDTO;
import com.shop.dto.ReviewDTO;
import com.shop.service.ProductService;
import com.shop.service.ReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ProductService productService;
    private final ReviewService reviewService;

    /**
     * 리뷰목록 조회
     * @param email
     * @param productSeq
     * @param page
     * @return
     */
    @GetMapping("/getReviewList")
    public Map<String, Object> getReviewList(@RequestHeader(value="email",required = false, defaultValue="") String email
            , @RequestParam(value="productSeq",required = false, defaultValue="0L") long productSeq
            , @RequestParam(value="page",required = false, defaultValue="1") int page){
        ProductDTO product = productService.selectProductInfo(productSeq, email);
        Page<ReviewDTO> reviewList = reviewService.selectReviewList(page,3,productSeq);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("product", product);
        resultMap.put("reviewList", reviewList);
        resultMap.put("type", product.getProductType().getParentCategory().get().getCode());
        return resultMap;
    }

    /**
     * 내 리뷰 정보 조회
     * @param orderInfoSeq
     * @param productSeq
     * @param email
     * @return
     */
    @GetMapping("/getMyReviewInfo")
    public Map<String, Object> getMyReviewInfo(@RequestHeader(value="email",required = false, defaultValue="") String email
            , @RequestParam(value="orderInfoSeq",required = false, defaultValue="0L") long orderInfoSeq
            , @RequestParam(value = "productSeq", required = false, defaultValue = "0L") long productSeq) {
        ProductDTO product =  productService.selectProductInfo(productSeq, email);
        ReviewDTO reviewInfo = reviewService.findReviewInfo(orderInfoSeq);

        // 하나의 Map으로 모든 값을 묶어서 반환
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("product", product);
        resultMap.put("reviewInfo", reviewInfo);
        return resultMap;
    }

    /**
     * 리뷰 저장
     * @param reviewSeq
     * @param orderInfoSeq
     * @param content
     * @param score
     * @param imgFiles
     * @param email
     * @return
     * @throws IOException
     */
    @PostMapping(value="/saveMyReview", consumes = "multipart/form-data")
    public ResponseEntity<Void> saveMyReview(@RequestHeader(value="email",required = false, defaultValue="") String email
            , @RequestParam(value="reviewSeq",required = false, defaultValue="0L") Long reviewSeq
            , @RequestParam(value="orderInfoSeq",required = false, defaultValue="0L") Long orderInfoSeq
            , @RequestParam(value="content",required = false, defaultValue="") String content
            , @RequestParam(value="score",required = false, defaultValue="0") int score
            , @RequestPart(value="imgFile", required = false) List<MultipartFile> imgFiles) throws IOException {
        ReviewDTO review = new ReviewDTO();
        if(reviewSeq != 0L){
            review.setReviewSeq(reviewSeq);
        }
        review.setOrderSeq(orderInfoSeq);
        review.setScore(score);
        review.setContent(content);
        if(imgFiles != null && imgFiles.size() > 0) {
            review.setImgFile(imgFiles.get(0));
        }
        reviewService.saveReviewInfo(review);
        return ResponseEntity.ok().build();
    }
}
