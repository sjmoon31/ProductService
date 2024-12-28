package com.shop.service;

import com.shop.domain.Heart;
import com.shop.domain.Member;
import com.shop.domain.Product;
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

@Service
@RequiredArgsConstructor
public class HeartService {
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final HeartRepository heartRepository;

    /**
     * 좋아요 목록 조회
     * @param start
     * @param limit
     * @param email
     * @return
     */
    public Page<HeartDTO> selectHeartList(int start, int limit, String email) {
        Member member = memberRepository.findByEmail(email).get();
        PageRequest pageRequest = PageRequest.of(start-1, limit);
        Page<HeartDTO> result = heartRepository.selectHeartList(pageRequest,member.getMemberSeq());
        int total = result.getTotalPages();
        if (total > 0) {
            pageRequest = PageRequest.of((total-1), limit);
        }
        return new PageImpl<>(result.getContent(), pageRequest, total);
    }

    /**
     * 좋아요 등록/삭제
     * @param productDTO
     */
    @Transactional
    public void updateHeartInfo(ProductDTO productDTO) {
        LocalDateTime nowDate = LocalDateTime.now();
        Heart param = new Heart();
        Member member = memberRepository.findByEmail(productDTO.getUserEmail()).get();
        Product product = productRepository.findById(productDTO.getProductSeq()).get();
        param.createHeart(member, product, nowDate);
        // 좋아요 취소시 삭제
        if("Y".equals(productDTO.getUpdateYn())) {
            heartRepository.save(param);
        }else {
            Heart heart = heartRepository.selectHeartInfo(param);
            heartRepository.delete(heart);
        }
    }
}
