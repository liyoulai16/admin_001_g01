package com.community.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.community.common.Result;
import com.community.entity.ContactMessage;
import com.community.service.ContactMessageService;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ContactMessageController {
    
    @Resource
    private ContactMessageService contactMessageService;
    
    @PostMapping("/api/contact/submit")
    public Result<Map<String, Object>> submitMessage(@RequestBody ContactMessage message) {
        boolean success = contactMessageService.createMessage(message);
        Map<String, Object> result = new HashMap<>();
        if (success) {
            result.put("id", message.getId());
            return Result.success("留言提交成功", result);
        } else {
            return Result.error("留言提交失败，请检查必填字段");
        }
    }
    
    @GetMapping("/api/admin/messages")
    public Result<Page<ContactMessage>> getMessageList(
            @RequestParam(defaultValue = "1") int pageNum,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Integer status,
            @RequestParam(required = false) String keyword) {
        Page<ContactMessage> page = contactMessageService.getMessagePage(pageNum, pageSize, type, status, keyword);
        return Result.success(page);
    }
    
    @GetMapping("/api/admin/messages/{id}")
    public Result<ContactMessage> getMessageDetail(@PathVariable Long id) {
        ContactMessage message = contactMessageService.getMessageById(id);
        if (message == null || message.getDeleted() == 1) {
            return Result.error("留言不存在");
        }
        return Result.success(message);
    }
    
    @PutMapping("/api/admin/messages/{id}/status")
    public Result<String> updateMessageStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        Integer status = body.get("status");
        if (status == null) {
            return Result.error("状态不能为空");
        }
        boolean success = contactMessageService.updateStatus(id, status);
        if (success) {
            return Result.success("状态更新成功");
        } else {
            return Result.error("状态更新失败");
        }
    }
    
    @PutMapping("/api/admin/messages/{id}/reply")
    public Result<String> replyMessage(@PathVariable Long id, @RequestBody Map<String, String> body) {
        String reply = body.get("reply");
        if (reply == null || reply.trim().isEmpty()) {
            return Result.error("回复内容不能为空");
        }
        boolean success = contactMessageService.replyMessage(id, reply, null);
        if (success) {
            return Result.success("回复成功");
        } else {
            return Result.error("回复失败");
        }
    }
    
    @DeleteMapping("/api/admin/messages/{id}")
    public Result<String> deleteMessage(@PathVariable Long id) {
        boolean success = contactMessageService.deleteMessage(id);
        if (success) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
}
