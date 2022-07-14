package com.cq.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.seckill.pojo.Order;
import com.cq.seckill.pojo.User;
import com.cq.seckill.vo.GoodsVo;
import com.cq.seckill.vo.OrderDetailVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chenqi
 * @since 2022-06-26
 */
public interface IOrderService extends IService<Order> {

    Order seckill(User user, GoodsVo goods);

    OrderDetailVo detail(Long orderId);

    String createPath(User user, Long goodsId);

    boolean checkPath(User user, Long goodsId, String path);

    boolean checkCaptcha(User user, Long goodsId, String captcha);
}
