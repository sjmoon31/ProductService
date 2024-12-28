package com.shop.repository.impl;

import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.*;
import com.shop.dto.HeartDTO;
import com.shop.dto.QHeartDTO;
import com.shop.repository.custom.HeartConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class HeartRepositoryImpl implements HeartConfig {
    private final JPAQueryFactory queryFactory;
    QHeart qHeart = QHeart.heart;
    QProduct qProduct = QProduct.product;
    QProductFile qProductFile = QProductFile.productFile;
    QFile qFile = QFile.file;
    /**
     * 좋아요 정보 조회
     * @param heart
     * @return
     */
    public Heart selectHeartInfo(Heart heart){
        return queryFactory.selectFrom(qHeart)
                .where(qHeart.member.eq(heart.getMember())
                .and(qHeart.product.eq(heart.getProduct())))
                .fetchOne();
    }

    /**
     * 좋아요 목록 조회
     * @param pageable
     * @param memberSeq
     * @return
     */
    @EntityGraph(attributePaths = {"product"})
    public Page<HeartDTO> selectHeartList(Pageable pageable, Long memberSeq){
        QHeart subHeart = new QHeart("subHeart");
        QueryResults<HeartDTO> heartList = queryFactory
                .select(new QHeartDTO(qHeart.heartSeq,
                        qProduct.productSeq,
                        qProduct.productName,
                        qProduct.price,
                        qProductFile.file.fileSeq,
                        JPAExpressions
                                .select(subHeart.count())
                                .from(subHeart)
                                .where(subHeart.product.productSeq.eq(qProduct.productSeq))
                        ))
                .from(qProduct)
                .join(qProduct.heartList,qHeart).on(qHeart.member.memberSeq.eq(memberSeq))
                .join(qProduct.productFileList,qProductFile).on(qProductFile.repYn.eq("Y"))
                .join(qProductFile.file,qFile)
                .orderBy(qHeart.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<HeartDTO> content = heartList.getResults();
        long total = heartList.getTotal();
        return new PageImpl<>(content, pageable, total);
    }
}

