package com.cq.seckill.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@AllArgsConstructor
public enum RespBeanEnum {
    SUCCESS(200,"SUCCESS"),
    ERROR(500,"服务端异常"),

    //登录模块
    LOGIN_ERROR(500200,"用户名或密码错误"),
    //手机号格式错误
    MOBILE_ERROR(500201,"手机号格式错误"),
    MOBILE_NOT_EXIST(500213, "手机号码不存在"),
    PASSWORD_UPDATE_FAIL(500214, "密码更新失败"),
    //绑定异常，参数校验失败
    BIND_ERROR(500202,"参数校验失败"),
    //秒杀模块5004xx
    EMPTY_STOCK(500400,"库存为空"),
    //重复抢购
    REPEATE_ERROR(500401,"您已购买过当前商品!"),
    //未登录会话错误
    SESSION_ERROR(500202,"当前未登录"),
    ORDER_NOT_EXIST(500404,"订单不存在")
    ;

    private final Integer code;
    private final String message;
}
