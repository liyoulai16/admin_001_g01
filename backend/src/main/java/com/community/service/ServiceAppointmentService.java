package com.community.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.PageResult;
import com.community.dto.AppointmentCreateRequest;
import com.community.dto.AppointmentQueryRequest;
import com.community.dto.OrderCreateRequest;
import com.community.entity.ServiceAppointment;
import com.community.entity.ServiceItem;
import com.community.entity.ServiceOrder;
import com.community.mapper.ServiceAppointmentMapper;
import com.community.mapper.ServiceItemMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import jakarta.annotation.Resource;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class ServiceAppointmentService {
    
    @Resource
    private ServiceAppointmentMapper serviceAppointmentMapper;
    
    @Resource
    private ServiceItemMapper serviceItemMapper;
    
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
    
    @Transactional
    public ServiceAppointment createAppointment(Long userId, AppointmentCreateRequest request) {
        ServiceItem serviceItem = serviceItemMapper.selectById(request.getServiceId());
        if (serviceItem == null || serviceItem.getStatus() != 1 || serviceItem.getDeleted() == 1) {
            throw new RuntimeException("服务不存在或已下架");
        }
        
        ServiceAppointment appointment = new ServiceAppointment();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setUserId(userId);
        appointment.setOrderId(request.getOrderId());
        appointment.setServiceId(serviceItem.getId());
        appointment.setServiceName(serviceItem.getName());
        appointment.setAppointmentDate(LocalDate.parse(request.getAppointmentDate(), dateFormatter));
        appointment.setAppointmentStartTime(LocalTime.parse(request.getAppointmentStartTime(), timeFormatter));
        appointment.setAppointmentEndTime(LocalTime.parse(request.getAppointmentEndTime(), timeFormatter));
        appointment.setAppointmentTimeStr(buildAppointmentTimeStr(
            request.getAppointmentDate(), 
            request.getAppointmentStartTime(), 
            request.getAppointmentEndTime()
        ));
        appointment.setServiceAddress(request.getServiceAddress());
        appointment.setContactName(request.getContactName());
        appointment.setContactPhone(request.getContactPhone());
        appointment.setStatus("pending");
        appointment.setRemark(request.getRemark());
        appointment.setRecordStatus(1);
        appointment.setDeleted(0);
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        serviceAppointmentMapper.insert(appointment);
        
        return appointment;
    }
    
    @Transactional
    public ServiceAppointment createAppointmentFromOrder(Long userId, ServiceOrder order, OrderCreateRequest request) {
        ServiceAppointment appointment = new ServiceAppointment();
        appointment.setAppointmentNo(generateAppointmentNo());
        appointment.setUserId(userId);
        appointment.setOrderId(order.getId());
        appointment.setServiceId(order.getServiceId());
        appointment.setServiceName(order.getServiceName());
        
        if (StringUtils.hasText(request.getAppointmentDate())) {
            appointment.setAppointmentDate(LocalDate.parse(request.getAppointmentDate(), dateFormatter));
        }
        if (StringUtils.hasText(request.getAppointmentStartTime())) {
            appointment.setAppointmentStartTime(LocalTime.parse(request.getAppointmentStartTime(), timeFormatter));
        }
        if (StringUtils.hasText(request.getAppointmentEndTime())) {
            appointment.setAppointmentEndTime(LocalTime.parse(request.getAppointmentEndTime(), timeFormatter));
        }
        
        if (StringUtils.hasText(request.getAppointmentTime())) {
            appointment.setAppointmentTimeStr(request.getAppointmentTime());
        } else if (StringUtils.hasText(request.getAppointmentDate())) {
            appointment.setAppointmentTimeStr(buildAppointmentTimeStr(
                request.getAppointmentDate(),
                request.getAppointmentStartTime(),
                request.getAppointmentEndTime()
            ));
        }
        
        appointment.setServiceAddress(request.getServiceAddress());
        appointment.setContactName(request.getContactName());
        appointment.setContactPhone(request.getContactPhone());
        appointment.setStatus("pending");
        appointment.setRemark(request.getRemark());
        appointment.setRecordStatus(1);
        appointment.setDeleted(0);
        appointment.setCreateTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        serviceAppointmentMapper.insert(appointment);
        
        return appointment;
    }
    
    @Transactional
    public ServiceAppointment confirmAppointment(Long userId, Long appointmentId) {
        ServiceAppointment appointment = serviceAppointmentMapper.selectById(appointmentId);
        if (appointment == null || appointment.getDeleted() == 1) {
            throw new RuntimeException("预约记录不存在");
        }
        
        if (!appointment.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约");
        }
        
        if (!"pending".equals(appointment.getStatus())) {
            throw new RuntimeException("预约状态不允许确认");
        }
        
        appointment.setStatus("confirmed");
        appointment.setConfirmTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        serviceAppointmentMapper.updateById(appointment);
        
        return appointment;
    }
    
    @Transactional
    public void confirmAppointmentByOrderId(Long orderId) {
        LambdaUpdateWrapper<ServiceAppointment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ServiceAppointment::getOrderId, orderId)
                    .eq(ServiceAppointment::getStatus, "pending")
                    .set(ServiceAppointment::getStatus, "confirmed")
                    .set(ServiceAppointment::getConfirmTime, LocalDateTime.now())
                    .set(ServiceAppointment::getUpdateTime, LocalDateTime.now());
        
        serviceAppointmentMapper.update(null, updateWrapper);
    }
    
    @Transactional
    public ServiceAppointment startAppointment(Long userId, Long appointmentId) {
        ServiceAppointment appointment = serviceAppointmentMapper.selectById(appointmentId);
        if (appointment == null || appointment.getDeleted() == 1) {
            throw new RuntimeException("预约记录不存在");
        }
        
        if (!appointment.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约");
        }
        
        if (!"confirmed".equals(appointment.getStatus())) {
            throw new RuntimeException("预约状态不允许开始");
        }
        
        appointment.setStatus("inProgress");
        appointment.setStartTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        serviceAppointmentMapper.updateById(appointment);
        
        return appointment;
    }
    
    @Transactional
    public ServiceAppointment completeAppointment(Long userId, Long appointmentId) {
        ServiceAppointment appointment = serviceAppointmentMapper.selectById(appointmentId);
        if (appointment == null || appointment.getDeleted() == 1) {
            throw new RuntimeException("预约记录不存在");
        }
        
        if (!appointment.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约");
        }
        
        if (!"inProgress".equals(appointment.getStatus())) {
            throw new RuntimeException("预约状态不允许完成");
        }
        
        appointment.setStatus("completed");
        appointment.setCompleteTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        serviceAppointmentMapper.updateById(appointment);
        
        return appointment;
    }
    
    @Transactional
    public ServiceAppointment cancelAppointment(Long userId, Long appointmentId, String cancelReason) {
        ServiceAppointment appointment = serviceAppointmentMapper.selectById(appointmentId);
        if (appointment == null || appointment.getDeleted() == 1) {
            throw new RuntimeException("预约记录不存在");
        }
        
        if (!appointment.getUserId().equals(userId)) {
            throw new RuntimeException("无权操作此预约");
        }
        
        if ("completed".equals(appointment.getStatus())) {
            throw new RuntimeException("预约已完成，无法取消");
        }
        
        if ("cancelled".equals(appointment.getStatus())) {
            throw new RuntimeException("预约已取消");
        }
        
        appointment.setStatus("cancelled");
        appointment.setCancelReason(cancelReason);
        appointment.setCancelTime(LocalDateTime.now());
        appointment.setUpdateTime(LocalDateTime.now());
        
        serviceAppointmentMapper.updateById(appointment);
        
        return appointment;
    }
    
    @Transactional
    public void cancelAppointmentByOrderId(Long orderId, String cancelReason) {
        LambdaUpdateWrapper<ServiceAppointment> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.eq(ServiceAppointment::getOrderId, orderId)
                    .ne(ServiceAppointment::getStatus, "completed")
                    .ne(ServiceAppointment::getStatus, "cancelled")
                    .set(ServiceAppointment::getStatus, "cancelled")
                    .set(ServiceAppointment::getCancelReason, cancelReason)
                    .set(ServiceAppointment::getCancelTime, LocalDateTime.now())
                    .set(ServiceAppointment::getUpdateTime, LocalDateTime.now());
        
        serviceAppointmentMapper.update(null, updateWrapper);
    }
    
    public ServiceAppointment getAppointmentById(Long userId, Long appointmentId) {
        ServiceAppointment appointment = serviceAppointmentMapper.selectById(appointmentId);
        if (appointment == null || appointment.getDeleted() == 1) {
            return null;
        }
        if (!appointment.getUserId().equals(userId)) {
            return null;
        }
        return appointment;
    }
    
    public PageResult<ServiceAppointment> getAppointmentPage(Long userId, AppointmentQueryRequest request) {
        Page<ServiceAppointment> page = new Page<>(request.getCurrent(), request.getSize());
        LambdaQueryWrapper<ServiceAppointment> queryWrapper = new LambdaQueryWrapper<>();
        
        queryWrapper.eq(ServiceAppointment::getUserId, userId)
                   .eq(ServiceAppointment::getDeleted, 0);
        
        if (StringUtils.hasText(request.getStatus()) && !"all".equals(request.getStatus())) {
            queryWrapper.eq(ServiceAppointment::getStatus, request.getStatus());
        }
        
        if (StringUtils.hasText(request.getAppointmentDate())) {
            queryWrapper.eq(ServiceAppointment::getAppointmentDate, LocalDate.parse(request.getAppointmentDate(), dateFormatter));
        }
        
        queryWrapper.orderByDesc(ServiceAppointment::getCreateTime);
        
        Page<ServiceAppointment> resultPage = serviceAppointmentMapper.selectPage(page, queryWrapper);
        
        return PageResult.of(
            resultPage.getRecords(),
            resultPage.getTotal(),
            resultPage.getCurrent(),
            resultPage.getSize()
        );
    }
    
    private String buildAppointmentTimeStr(String date, String startTime, String endTime) {
        if (date == null) return null;
        StringBuilder sb = new StringBuilder(date);
        if (startTime != null) {
            sb.append(" ").append(startTime);
            if (endTime != null) {
                sb.append("-").append(endTime);
            }
        }
        return sb.toString();
    }
    
    private String generateAppointmentNo() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
        return "APT" + timestamp + random;
    }
}
