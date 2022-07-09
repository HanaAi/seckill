package com.cq.seckill.exception;

import com.cq.seckill.vo.RespBean;
import com.cq.seckill.vo.RespBeanEnum;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


/**
 * 全局异常处理类
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public RespBean ExceptionHandler(Exception e){
        if(e instanceof GlobalException){
            GlobalException ex = (GlobalException) e;
            return RespBean.error(ex.getRespBeanEnum());
        }else if(e instanceof BindException){
            BindException ex = (BindException) e;
            RespBean respBean = RespBean.error(RespBeanEnum.BIND_ERROR);
            //绑定异常信息不够准确，这里我们获取对应的错误信息
            respBean.setMessage("参数校验异常："+ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return respBean;
        }

        return RespBean.error(RespBeanEnum.ERROR);
    }
}
