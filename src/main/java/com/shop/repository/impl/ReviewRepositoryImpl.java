package com.shop.repository.impl;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.domain.*;
import com.shop.dto.ReviewDTO;
import com.shop.repository.custom.ReviewConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RequiredArgsConstructor
public class ReviewRepositoryImpl implements ReviewConfig {
    private final JPAQueryFactory queryFactory;
    QReview qReview = QReview.review;
    QOrderInfo qOrderInfo = QOrderInfo.orderInfo;
    QProductStock qProductStock = QProductStock.productStock;
    QProduct qProduct = QProduct.product;
    QFile qFile = QFile.file;
    QMember qMember = QMember.member;

    /**
     * 내 리뷰 정보 조회
     * @param orderInfo
     * @return
     */
    public Review selectReviewInfo(OrderInfo orderInfo){
        return queryFactory.selectFrom(qReview)
                .join(qReview.file,qFile)
                .where(qReview.orderInfo.eq(orderInfo))
                .fetchOne();
    }

    /**
     * 리뷰 리스트 조회
     * @param pageable
     * @param productSeq
     * @return
     */
    public Page<Review> selectReviewList(Pageable pageable, Long productSeq){
        QueryResults<Review> reviewList =  queryFactory.selectFrom(qReview)
                .join(qReview.file,qFile).fetchJoin()
                .join(qReview.orderInfo,qOrderInfo).fetchJoin()
                .join(qOrderInfo.member,qMember).fetchJoin()
                .join(qOrderInfo.productStock,qProductStock)
                .join(qProductStock.product,qProduct)
                .where(qProduct.productSeq.eq(productSeq))
                .orderBy(qReview.modDate.desc().nullsLast(), qReview.regDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        List<Review> content = reviewList.getResults();
        long total = reviewList.getTotal();
        return new PageImpl<>(content, pageable, total);
    }

    /**
     * 상품 리뷰 정보 조회
     * @param productSeq
     * @return
     */
    public ReviewDTO ProductReviewInfo(long productSeq){
        return queryFactory.select(Projections.fields(ReviewDTO.class,
                        qReview.count().as("reviewCount"),
                        qReview.score.avg().as("scoreAvg")))
                .from(qReview)
                .leftJoin(qReview.orderInfo,qOrderInfo)
                .leftJoin(qOrderInfo.productStock,qProductStock)
                .leftJoin(qProductStock.product,qProduct)
                .where(qProduct.productSeq.eq(productSeq))
                .fetchOne();
    }

    /**
     * 리뷰 정보 수정
     * @param reviewSeq
     * @param review
     * @return
     */
    public long updateReviewInfo(long reviewSeq, Review review) {
        var updateQuery = queryFactory.update(qReview)
                .set(qReview.content, review.getContent())
                .set(qReview.score, review.getScore())
                .set(qReview.modDate, review.getRegDate())
                .where(qReview.reviewSeq.eq(reviewSeq));

        // 파일이 있을 경우에만 file을 업데이트
        if (review.getFile().getFileSeq() != null) {
            updateQuery.set(qReview.file, review.getFile());
        }

        // 쿼리 실행
        return updateQuery.execute();
    }
}
