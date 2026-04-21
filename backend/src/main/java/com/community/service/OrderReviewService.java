package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.dto.ReviewCreateRequest;
import com.community.entity.OrderReview;
import com.community.entity.ServiceOrder;
import com.community.mapper.OrderReviewMapper;
import com.community.mapper.ServiceOrderMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.annotation.Resource;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OrderReviewService {
    
    @Resource
    private OrderReviewMapper orderReviewMapper;
    
    @Resource
    private ServiceOrderMapper serviceOrderMapper;
    
    @Transactional
    public OrderReview createReview(Long userId, ReviewCreateRequest request) {
        ServiceOrder order = serviceOrderMapper.selectById(request.getOrderId());
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权评价此订单");
        }
        
        if (!"completed".equals(order.getStatus())) {
            throw new RuntimeException("订单未完成，无法评价");
        }
        
        if (order.getIsReviewed() == 1) {
            throw new RuntimeException("订单已评价");
        }
        
        OrderReview existingReview = orderReviewMapper.selectOne(
            new LambdaQueryWrapper<OrderReview>()
                .eq(OrderReview::getOrderId, request.getOrderId())
                .eq(OrderReview::getDeleted, 0)
        );
        
        if (existingReview != null) {
            throw new RuntimeException("订单已评价");
        }
        
        OrderReview review = new OrderReview();
        review.setOrderId(order.getId());
        review.setOrderNo(order.getOrderNo());
        review.setUserId(userId);
        review.setServiceId(order.getServiceId());
        review.setRating(request.getRating());
        review.setContent(request.getContent());
        review.setImages(request.getImages());
        review.setIsAnonymous(request.getIsAnonymous() != null ? request.getIsAnonymous() : 0);
        review.setIsVisible(1);
        review.setStatus(1);
        review.setDeleted(0);
        review.setCreateTime(LocalDateTime.now());
        review.setUpdateTime(LocalDateTime.now());
        
        orderReviewMapper.insert(review);
        
        order.setIsReviewed(1);
        order.setReviewId(review.getId());
        order.setUpdateTime(LocalDateTime.now());
        serviceOrderMapper.updateById(order);
        
        return review;
    }
    
    public OrderReview getReviewById(Long userId, Long reviewId) {
        OrderReview review = orderReviewMapper.selectById(reviewId);
        if (review == null || review.getDeleted() == 1) {
            return null;
        }
        if (!review.getUserId().equals(userId)) {
            return null;
        }
        return review;
    }
    
    public OrderReview getReviewByOrderId(Long userId, Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            return null;
        }
        if (!order.getUserId().equals(userId)) {
            return null;
        }
        
        return orderReviewMapper.selectOne(
            new LambdaQueryWrapper<OrderReview>()
                .eq(OrderReview::getOrderId, orderId)
                .eq(OrderReview::getDeleted, 0)
        );
    }
    
    public PageResult<OrderReview> getMyReviews(Long userId, long current, long size) {
        Page<OrderReview> page = new Page<>(current, size);
        LambdaQueryWrapper<OrderReview> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(OrderReview::getUserId, userId)
                   .eq(OrderReview::getDeleted, 0)
                   .orderByDesc(OrderReview::getCreateTime);
        
        Page<OrderReview> resultPage = orderReviewMapper.selectPage(page, queryWrapper);
        
        return PageResult.of(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize()
        );
    }
    
    public PageResult<OrderReview> getServiceReviews(Long serviceId, long current, long size) {
        Page<OrderReview> page = new Page<>(current, size);
        LambdaQueryWrapper<OrderReview> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(OrderReview::getServiceId, serviceId)
                   .eq(OrderReview::getIsVisible, 1)
                   .eq(OrderReview::getDeleted, 0)
                   .orderByDesc(OrderReview::getCreateTime);
        
        Page<OrderReview> resultPage = orderReviewMapper.selectPage(page, queryWrapper);
        
        return PageResult.of(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize()
        );
    }
}
