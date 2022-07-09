package com.cq.seckill.exception;

import com.cq.seckill.vo.RespBeanEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
/**
 * 自定义全局异常
 */
public class GlobalException extends RuntimeException{
    private RespBeanEnum respBeanEnum;
}
