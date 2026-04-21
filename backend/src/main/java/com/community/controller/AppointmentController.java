package com.community.controller;

import com.community.common.PageResult;
import com.community.common.Result;
import com.community.context.LoginUserContext;
import com.community.dto.AppointmentCreateRequest;
import com.community.dto.AppointmentQueryRequest;
import com.community.entity.ServiceAppointment;
import com.community.entity.User;
import com.community.service.ServiceAppointmentService;
import com.community.service.UserService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

@RestController
@RequestMapping("/api/appointments")
@CrossOrigin(origins = "*")
public class AppointmentController {
    
    @Resource
    private ServiceAppointmentService serviceAppointmentService;
    
    @Resource
    private UserService userService;
    
    @PostMapping("/create")
    public Result<ServiceAppointment> createAppointment(@Validated @RequestBody AppointmentCreateRequest request) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceAppointment appointment = serviceAppointmentService.createAppointment(user.getId(), request);
            return Result.success("预约创建成功", appointment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/confirm/{appointmentId}")
    public Result<ServiceAppointment> confirmAppointment(@PathVariable("appointmentId") Long appointmentId) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceAppointment appointment = serviceAppointmentService.confirmAppointment(user.getId(), appointmentId);
            return Result.success("预约确认成功", appointment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/start/{appointmentId}")
    public Result<ServiceAppointment> startAppointment(@PathVariable("appointmentId") Long appointmentId) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceAppointment appointment = serviceAppointmentService.startAppointment(user.getId(), appointmentId);
            return Result.success("服务开始", appointment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/complete/{appointmentId}")
    public Result<ServiceAppointment> completeAppointment(@PathVariable("appointmentId") Long appointmentId) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceAppointment appointment = serviceAppointmentService.completeAppointment(user.getId(), appointmentId);
            return Result.success("服务完成", appointment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @PostMapping("/cancel")
    public Result<ServiceAppointment> cancelAppointment(
            @RequestParam("appointmentId") Long appointmentId,
            @RequestParam("cancelReason") String cancelReason) {
        
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        try {
            ServiceAppointment appointment = serviceAppointmentService.cancelAppointment(user.getId(), appointmentId, cancelReason);
            return Result.success("预约取消成功", appointment);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    @GetMapping("/{appointmentId}")
    public Result<ServiceAppointment> getAppointmentById(@PathVariable("appointmentId") Long appointmentId) {
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        ServiceAppointment appointment = serviceAppointmentService.getAppointmentById(user.getId(), appointmentId);
        if (appointment == null) {
            return Result.error("预约记录不存在");
        }
        return Result.success(appointment);
    }
    
    @GetMapping("/list")
    public Result<PageResult<ServiceAppointment>> getAppointmentList(
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "appointmentDate", required = false) String appointmentDate,
            @RequestParam(value = "current", required = false, defaultValue = "1") long current,
            @RequestParam(value = "size", required = false, defaultValue = "10") long size) {
        
        String currentUsername = LoginUserContext.getCurrentUser();
        User user = userService.getUserByUsername(currentUsername);
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        AppointmentQueryRequest request = new AppointmentQueryRequest();
        request.setStatus(status);
        request.setAppointmentDate(appointmentDate);
        request.setCurrent(current);
        request.setSize(size);
        
        PageResult<ServiceAppointment> result = serviceAppointmentService.getAppointmentPage(user.getId(), request);
        return Result.success(result);
    }
}
