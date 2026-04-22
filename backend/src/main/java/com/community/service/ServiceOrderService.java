package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.dto.OrderCreateRequest;
import com.community.dto.OrderQueryRequest;
import com.community.entity.*;
import com.community.mapper.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ServiceOrderService {
    
    @Resource
    private ServiceOrderMapper serviceOrderMapper;
    
    @Resource
    private ServiceItemMapper serviceItemMapper;
    
    @Resource
    private ServiceCategoryMapper serviceCategoryMapper;
    
    @Resource
    private UserBalanceService userBalanceService;
    
    @Resource
    private BalanceTransactionMapper balanceTransactionMapper;
    
    @Resource
    private ServiceAppointmentService serviceAppointmentService;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    @Transactional
    public ServiceOrder createOrder(Long userId, OrderCreateRequest request) {
        ServiceItem serviceItem = serviceItemMapper.selectById(request.getServiceId());
        if (serviceItem == null || serviceItem.getStatus() != 1 || serviceItem.getDeleted() == 1) {
            throw new RuntimeException("服务不存在或已下架");
        }
        
        validateAppointmentTime(request);
        
        ServiceCategory category = serviceCategoryMapper.selectById(serviceItem.getCategoryId());
        
        ServiceOrder order = new ServiceOrder();
        order.setOrderNo(generateOrderNo());
        order.setUserId(userId);
        order.setServiceId(serviceItem.getId());
        order.setCategoryId(serviceItem.getCategoryId());
        order.setServiceName(serviceItem.getName());
        order.setCategoryName(category != null ? category.getName() : null);
        order.setServiceIcon(serviceItem.getIcon());
        order.setServiceDescription(serviceItem.getDescription());
        order.setUnitPrice(serviceItem.getPriceValue() != null ? serviceItem.getPriceValue() : BigDecimal.ZERO);
        order.setQuantity(request.getQuantity() != null ? request.getQuantity() : 1);
        
        BigDecimal totalAmount = order.getUnitPrice().multiply(new BigDecimal(order.getQuantity()));
        if (request.getExtraFee() != null) {
            totalAmount = totalAmount.add(request.getExtraFee());
        }
        if (request.getDiscountAmount() != null) {
            totalAmount = totalAmount.subtract(request.getDiscountAmount());
            if (totalAmount.compareTo(BigDecimal.ZERO) < 0) {
                totalAmount = BigDecimal.ZERO;
            }
        }
        
        order.setTotalAmount(totalAmount);
        order.setExtraFee(request.getExtraFee() != null ? request.getExtraFee() : BigDecimal.ZERO);
        order.setDiscountAmount(request.getDiscountAmount() != null ? request.getDiscountAmount() : BigDecimal.ZERO);
        order.setPayAmount(BigDecimal.ZERO);
        order.setStatus("pending");
        order.setPayMethod(request.getPayMethod() != null ? request.getPayMethod() : 0);
        order.setPayStatus(0);
        order.setAppointmentTime(request.getAppointmentTime());
        
        if (StringUtils.hasText(request.getAppointmentDate())) {
            order.setAppointmentDate(LocalDate.parse(request.getAppointmentDate(), dateFormatter));
        }
        if (StringUtils.hasText(request.getAppointmentStartTime())) {
            order.setAppointmentStartTime(LocalTime.parse(request.getAppointmentStartTime(), timeFormatter));
        }
        if (StringUtils.hasText(request.getAppointmentEndTime())) {
            order.setAppointmentEndTime(LocalTime.parse(request.getAppointmentEndTime(), timeFormatter));
        }
        
        order.setServiceAddress(request.getServiceAddress());
        order.setContactName(request.getContactName());
        order.setContactPhone(request.getContactPhone());
        order.setIsReviewed(0);
        order.setRemark(request.getRemark());
        order.setRecordStatus(1);
        order.setDeleted(0);
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        serviceOrderMapper.insert(order);
        
        if (StringUtils.hasText(request.getAppointmentDate()) && 
            StringUtils.hasText(request.getAppointmentStartTime()) &&
            StringUtils.hasText(request.getAppointmentEndTime())) {
            try {
                serviceAppointmentService.createAppointmentFromOrder(userId, order, request);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        return order;
    }
    
    @Transactional
    public ServiceOrder payOrder(Long userId, Long orderId, Integer payMethod, String transactionId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        if (!"pending".equals(order.getStatus())) {
            throw new RuntimeException("订单状态不允许支付");
        }
        
        if (order.getPayStatus() == 1) {
            throw new RuntimeException("订单已支付");
        }
        
        if (payMethod == null) {
            payMethod = order.getPayMethod() != null ? order.getPayMethod() : 0;
        }
        
        if (payMethod == 0) {
            UserBalance balance = userBalanceService.getOrCreateBalance(userId);
            if (balance.getBalance().compareTo(order.getTotalAmount()) < 0) {
                throw new RuntimeException("余额不足");
            }
            
            userBalanceService.deductBalance(userId, order.getTotalAmount(), orderId, "order", "订单支付：" + order.getServiceName());
        }
        
        order.setPayMethod(payMethod);
        order.setPayStatus(1);
        order.setPayAmount(order.getTotalAmount());
        order.setStatus("confirmed");
        order.setPayTime(LocalDateTime.now());
        order.setTransactionId(transactionId);
        order.setUpdateTime(LocalDateTime.now());
        
        serviceOrderMapper.updateById(order);
        
        try {
            serviceAppointmentService.confirmAppointmentByOrderId(orderId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return order;
    }
    
    @Transactional
    public ServiceOrder cancelOrder(Long userId, Long orderId, String cancelReason) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        if ("cancelled".equals(order.getStatus())) {
            throw new RuntimeException("订单已取消");
        }
        
        if ("completed".equals(order.getStatus())) {
            throw new RuntimeException("订单已完成，无法取消");
        }
        
        if (order.getPayStatus() == 1 && order.getPayAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (order.getPayMethod() == 0) {
                userBalanceService.addBalance(userId, order.getPayAmount(), orderId, "refund", "订单取消退款：" + order.getServiceName());
            }
        }
        
        order.setStatus("cancelled");
        order.setCancelReason(cancelReason);
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        serviceOrderMapper.updateById(order);
        
        try {
            serviceAppointmentService.cancelAppointmentByOrderId(orderId, cancelReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return order;
    }
    
    public ServiceOrder getOrderById(Long userId, Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            return null;
        }
        if (!order.getUserId().equals(userId)) {
            return null;
        }
        return order;
    }
    
    public PageResult<ServiceOrder> getOrderPage(Long userId, OrderQueryRequest request) {
        Page<ServiceOrder> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ServiceOrder> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(ServiceOrder::getUserId, userId)
                   .eq(ServiceOrder::getDeleted, 0);
        
        if (StringUtils.hasText(request.getStatus()) && !"all".equals(request.getStatus())) {
            queryWrapper.eq(ServiceOrder::getStatus, request.getStatus());
        }
        
        if (StringUtils.hasText(request.getKeyword())) {
            queryWrapper.and(wrapper -> wrapper
                .like(ServiceOrder::getServiceName, request.getKeyword())
                .or()
                .like(ServiceOrder::getOrderNo, request.getKeyword())
            );
        }
        
        if (StringUtils.hasText(request.getSortBy())) {
            switch (request.getSortBy()) {
                case "createTimeAsc":
                    queryWrapper.orderByAsc(ServiceOrder::getCreateTime);
                    break;
                case "amountDesc":
                    queryWrapper.orderByDesc(ServiceOrder::getTotalAmount);
                    break;
                case "amountAsc":
                    queryWrapper.orderByAsc(ServiceOrder::getTotalAmount);
                    break;
                case "createTimeDesc":
                default:
                    queryWrapper.orderByDesc(ServiceOrder::getCreateTime);
                    break;
            }
        } else {
            queryWrapper.orderByDesc(ServiceOrder::getCreateTime);
        }
        
        Page<ServiceOrder> resultPage = serviceOrderMapper.selectPage(page, queryWrapper);
        
        return PageResult.of(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize()
        );
    }
    
    public long countByStatus(Long userId, String status) {
        LambdaQueryWrapper<ServiceOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceOrder::getUserId, userId)
                   .eq(ServiceOrder::getDeleted, 0);
        
        if (StringUtils.hasText(status) && !"all".equals(status)) {
            queryWrapper.eq(ServiceOrder::getStatus, status);
        }
        
        return serviceOrderMapper.selectCount(queryWrapper);
    }
    
    private String generateOrderNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "ORD" + timestamp + random;
    }
    
    private void validateAppointmentTime(OrderCreateRequest request) {
        if (StringUtils.hasText(request.getAppointmentDate()) && 
            StringUtils.hasText(request.getAppointmentStartTime())) {
            
            LocalDate appointmentDate = LocalDate.parse(request.getAppointmentDate(), dateFormatter);
            LocalTime appointmentStartTime = LocalTime.parse(request.getAppointmentStartTime(), timeFormatter);
            
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            
            if (appointmentDate.isBefore(today)) {
                throw new RuntimeException("预约日期不能是过去的日期");
            }
            
            if (appointmentDate.isEqual(today) && appointmentStartTime.isBefore(now)) {
                throw new RuntimeException("预约时间不能是过去的时间");
            }
        }
    }
    
    @Transactional
    public ServiceOrder requestRefund(Long userId, Long orderId, String refundReason) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        if (!"confirmed".equals(order.getStatus()) && !"inProgress".equals(order.getStatus())) {
            throw new RuntimeException("只有待服务和进行中的订单可以申请退款");
        }
        
        if (order.getPayStatus() != 1) {
            throw new RuntimeException("未支付的订单无法申请退款");
        }
        
        if ("refunding".equals(order.getRefundStatus())) {
            throw new RuntimeException("该订单已在退款中");
        }
        
        order.setRefundAmount(order.getPayAmount());
        order.setRefundReason(refundReason);
        order.setRefundTime(LocalDateTime.now());
        order.setRefundStatus("refunding");
        order.setStatus("refunding");
        order.setUpdateTime(LocalDateTime.now());
        
        serviceOrderMapper.updateById(order);
        
        return order;
    }
    
    @Transactional
    public ServiceOrder cancelRefund(Long userId, Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!order.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此订单");
        }
        
        if (!"refunding".equals(order.getStatus())) {
            throw new RuntimeException("该订单不在退款中状态");
        }
        
        if (order.getAppointmentDate() != null && order.getAppointmentStartTime() != null) {
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            LocalDate apptDate = order.getAppointmentDate();
            LocalTime apptTime = order.getAppointmentStartTime();
            
            if (apptDate.isBefore(today) || (apptDate.isEqual(today) && apptTime.isBefore(now))) {
                order.setStatus("inProgress");
            } else {
                order.setStatus("confirmed");
            }
        } else {
            order.setStatus("confirmed");
        }
        
        order.setRefundStatus(null);
        order.setUpdateTime(LocalDateTime.now());
        
        serviceOrderMapper.updateById(order);
        
        return order;
    }
    
    @Transactional
    public ServiceOrder processRefund(Long orderId, boolean approved, String rejectReason) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        
        if (!"refunding".equals(order.getStatus())) {
            throw new RuntimeException("该订单不在退款中状态");
        }
        
        if (approved) {
            if (order.getPayMethod() == 0 && order.getRefundAmount() != null && 
                order.getRefundAmount().compareTo(BigDecimal.ZERO) > 0) {
                userBalanceService.addBalance(order.getUserId(), order.getRefundAmount(), 
                    orderId, "refund", "订单退款：" + order.getServiceName());
            }
            
            order.setStatus("cancelled");
            order.setRefundStatus("refunded");
            order.setCancelReason(order.getRefundReason());
            order.setCancelTime(LocalDateTime.now());
            order.setUpdateTime(LocalDateTime.now());
            
            serviceOrderMapper.updateById(order);
            
            try {
                serviceAppointmentService.cancelAppointmentByOrderId(orderId, order.getRefundReason());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            if (order.getAppointmentDate() != null && order.getAppointmentStartTime() != null) {
                LocalDate today = LocalDate.now();
                LocalTime now = LocalTime.now();
                LocalDate apptDate = order.getAppointmentDate();
                LocalTime apptTime = order.getAppointmentStartTime();
                
                if (apptDate.isBefore(today) || (apptDate.isEqual(today) && apptTime.isBefore(now))) {
                    order.setStatus("inProgress");
                } else {
                    order.setStatus("confirmed");
                }
            } else {
                order.setStatus("confirmed");
            }
            
            order.setRefundStatus("rejected");
            order.setUpdateTime(LocalDateTime.now());
            
            serviceOrderMapper.updateById(order);
        }
        
        return order;
    }
    
    public void updateOrderStatusByAppointmentTime() {
        LambdaQueryWrapper<ServiceOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceOrder::getStatus, "confirmed")
                   .eq(ServiceOrder::getDeleted, 0)
                   .isNotNull(ServiceOrder::getAppointmentDate)
                   .isNotNull(ServiceOrder::getAppointmentStartTime);
        
        List<ServiceOrder> orders = serviceOrderMapper.selectList(queryWrapper);
        
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();
        
        for (ServiceOrder order : orders) {
            LocalDate apptDate = order.getAppointmentDate();
            LocalTime apptTime = order.getAppointmentStartTime();
            
            if (apptDate.isBefore(today) || (apptDate.isEqual(today) && apptTime.isBefore(now))) {
                order.setStatus("inProgress");
                order.setUpdateTime(LocalDateTime.now());
                serviceOrderMapper.updateById(order);
            }
        }
    }
    
    public ServiceOrder getOrderWithStatusUpdate(Long userId, Long orderId) {
        ServiceOrder order = getOrderById(userId, orderId);
        if (order == null) {
            return null;
        }
        
        if ("confirmed".equals(order.getStatus()) && order.getAppointmentDate() != null 
            && order.getAppointmentStartTime() != null) {
            LocalDate today = LocalDate.now();
            LocalTime now = LocalTime.now();
            LocalDate apptDate = order.getAppointmentDate();
            LocalTime apptTime = order.getAppointmentStartTime();
            
            if (apptDate.isBefore(today) || (apptDate.isEqual(today) && apptTime.isBefore(now))) {
                order.setStatus("inProgress");
                order.setUpdateTime(LocalDateTime.now());
                serviceOrderMapper.updateById(order);
            }
        }
        
        return order;
    }
    
    public Page<ServiceOrder> getOrderPageForAdmin(int pageNum, int pageSize, String keyword, Long categoryId, String status) {
        Page<ServiceOrder> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<ServiceOrder> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(ServiceOrder::getDeleted, 0);
        
        if (StringUtils.hasText(keyword)) {
            queryWrapper.and(wrapper -> wrapper
                .like(ServiceOrder::getServiceName, keyword)
                .or()
                .like(ServiceOrder::getOrderNo, keyword)
                .or()
                .like(ServiceOrder::getContactName, keyword)
                .or()
                .like(ServiceOrder::getContactPhone, keyword)
            );
        }
        
        if (categoryId != null) {
            queryWrapper.eq(ServiceOrder::getCategoryId, categoryId);
        }
        
        if (StringUtils.hasText(status) && !"all".equals(status)) {
            queryWrapper.eq(ServiceOrder::getStatus, status);
        }
        
        queryWrapper.orderByDesc(ServiceOrder::getCreateTime);
        
        return serviceOrderMapper.selectPage(page, queryWrapper);
    }
    
    public ServiceOrder getOrderByIdForAdmin(Long orderId) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            return null;
        }
        return order;
    }
    
    @Transactional
    public boolean updateOrderStatusByAdmin(Long orderId, String status, String reason) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            return false;
        }
        
        order.setStatus(status);
        order.setUpdateTime(LocalDateTime.now());
        
        if ("cancelled".equals(status) && StringUtils.hasText(reason)) {
            order.setCancelReason(reason);
            order.setCancelTime(LocalDateTime.now());
        }
        
        return serviceOrderMapper.updateById(order) > 0;
    }
    
    @Transactional
    public ServiceOrder adminCancelOrder(Long orderId, String cancelReason) {
        ServiceOrder order = serviceOrderMapper.selectById(orderId);
        if (order == null || order.getDeleted() == 1) {
            throw new RuntimeException("订单不存在");
        }
        
        if ("cancelled".equals(order.getStatus())) {
            throw new RuntimeException("订单已取消");
        }
        
        if ("completed".equals(order.getStatus())) {
            throw new RuntimeException("订单已完成，无法取消");
        }
        
        if (order.getPayStatus() == 1 && order.getPayAmount() != null && order.getPayAmount().compareTo(BigDecimal.ZERO) > 0) {
            if (order.getPayMethod() == 0) {
                userBalanceService.addBalance(order.getUserId(), order.getPayAmount(), orderId, "refund", "订单取消退款：" + order.getServiceName());
            }
        }
        
        order.setStatus("cancelled");
        order.setCancelReason(cancelReason);
        order.setCancelTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        
        serviceOrderMapper.updateById(order);
        
        try {
            serviceAppointmentService.cancelAppointmentByOrderId(orderId, cancelReason);
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return order;
    }
    
    @Transactional
    public boolean deleteOrderByAdmin(Long orderId) {
        return serviceOrderMapper.deleteById(orderId) > 0;
    }
    
    public long countByStatusForAdmin(String status) {
        LambdaQueryWrapper<ServiceOrder> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ServiceOrder::getDeleted, 0);
        
        if (StringUtils.hasText(status) && !"all".equals(status)) {
            queryWrapper.eq(ServiceOrder::getStatus, status);
        }
        
        return serviceOrderMapper.selectCount(queryWrapper);
    }
    
    public Map<String, Long> getOrderStatsForAdmin() {
        Map<String, Long> stats = new HashMap<>();
        stats.put("all", countByStatusForAdmin("all"));
        stats.put("pending", countByStatusForAdmin("pending"));
        stats.put("confirmed", countByStatusForAdmin("confirmed"));
        stats.put("inProgress", countByStatusForAdmin("inProgress"));
        stats.put("completed", countByStatusForAdmin("completed"));
        stats.put("cancelled", countByStatusForAdmin("cancelled"));
        stats.put("refunding", countByStatusForAdmin("refunding"));
        stats.put("refunded", countByStatusForAdmin("refunded"));
        stats.put("rejected", countByStatusForAdmin("rejected"));
        return stats;
    }
}
