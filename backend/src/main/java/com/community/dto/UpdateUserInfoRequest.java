package com.community.dto;

import lombok.Data;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Email;

@Data
public class UpdateUserInfoRequest {
    
    @NotBlank(message = "用户名不能为空")
    @Size(min = 3, max = 50, message = "用户名长度必须为3-50位")
    private String newUsername;
    
    private String nickname;
    
    @Size(max = 20, message = "手机号码长度不能超过20位")
    @Pattern(regexp = "^1[3-9]\\d{9}$|^$", message = "请输入正确的手机号码")
    private String phone;
    
    @Size(max = 100, message = "邮箱长度不能超过100位")
    @Email(message = "请输入正确的邮箱格式")
    private String email;
}
