package com.cq.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.seckill.pojo.SecKillOrder;
import com.cq.seckill.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chenqi
 * @since 2022-06-26
 */
public interface ISeckillOrderService extends IService<SecKillOrder> {

    Long getResult(User user, Long goodsId);
}
