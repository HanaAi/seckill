package com.cq.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.seckill.mapper.OrderMapper;
import com.cq.seckill.mapper.SeckillOrderMapper;
import com.cq.seckill.pojo.SecKillOrder;
import com.cq.seckill.pojo.User;
import com.cq.seckill.service.ISeckillOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chenqi
 * @since 2022-06-26
 */
@Service
public class SeckillOrderServiceImpl extends ServiceImpl<SeckillOrderMapper, SecKillOrder> implements ISeckillOrderService {
    @Autowired
    private SeckillOrderMapper seckillOrderMapper;
    @Autowired
    private RedisTemplate redisTemplate;
    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @Override
    public Long getResult(User user, Long goodsId) {
        SecKillOrder secKillOrder = seckillOrderMapper.selectOne(new QueryWrapper<SecKillOrder>().eq("user_id", user.getId()).
                eq("goods_id", goodsId));
        if(secKillOrder != null){
            return secKillOrder.getOrderId();
        }
        else if(redisTemplate.hasKey("isStockEmpty:"+goodsId)){
            return -1L;
        }
        else{
            return 0L;
        }
    }
}
