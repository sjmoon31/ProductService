package com.shop.service;

import com.shop.common.ModelMapperUtil;
import com.shop.domain.Heart;
import com.shop.domain.Member;
import com.shop.domain.Product;
import com.shop.domain.enums.ProductType;
import com.shop.dto.HeartDTO;
import com.shop.dto.ProductDTO;
import com.shop.repository.HeartRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {
    private final ProductRepository productRepository;
    private final MemberRepository memberRepository;
    private final HeartRepository heartRepository;

    /**
     * 상품목록조회
     * @param start
     * @param limit
     * @return
     */
    public Page<ProductDTO> selectProductList(int start, int limit, String email, ProductType productType, String searchStr){
        Member member = memberRepository.findByEmail(email).get();
        PageRequest pageRequest = PageRequest.of(start-1, limit);
        Page<Product> result = productRepository.selectProductList(pageRequest, 0L, member.getMemberSeq(), productType,searchStr);
        int total = result.getTotalPages();
        pageRequest = PageRequest.of((total-1), limit);
        List<ProductDTO> list = ModelMapperUtil.mapAll(result.getContent(), ProductDTO.class);
        return new PageImpl<>(list, pageRequest, total);
    }
    /**
     * 상품별 개수 확인
     * @return
     */
    public Map<String,Long> selectProductCount(){
        List<ProductDTO> countList =  productRepository.selectProductCount();
        Map<String, Long> result = countList.stream()
                .collect(Collectors.groupingBy(dto -> dto.getProductType().getParentCategory().get().getCode()
                        , Collectors.summingLong(ProductDTO::getProductTypeCount)));
        return result;
    }
    /**
     * 상품정보조회
     * @param productSeq
     * @param email
     * @return
     */
    public ProductDTO selectProductInfo(Long productSeq, String email){
        Member member = memberRepository.findByEmail(email).get();
        Product productInfo = productRepository.selectProduct(productSeq, member.getMemberSeq());
        Heart heart = member.getHeartList().stream()
                .filter(h -> h.getProduct().getProductSeq().equals(productSeq))
                .findFirst()
                .orElse(null);

        ProductDTO product = ModelMapperUtil.map(productInfo, ProductDTO.class);
        if(heart != null) {
            HeartDTO heartDTO = ModelMapperUtil.map(heart, HeartDTO.class);
            product.setHeart(heartDTO);
        }

        return product;
    }
}
