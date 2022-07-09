package com.cq.seckill.vo;

import com.cq.seckill.validator.isMobile;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;

@Data
public class LoginVo {
    @NotNull
    @isMobile//自定义注解验证手机号
    private String mobile;

    @NotNull
    @Length(min=32)
    private String password;
}
