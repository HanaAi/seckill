package com.cq.seckill.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.seckill.exception.GlobalException;
import com.cq.seckill.mapper.OrderMapper;
import com.cq.seckill.pojo.Order;
import com.cq.seckill.pojo.SecKillGoods;
import com.cq.seckill.pojo.SecKillOrder;
import com.cq.seckill.pojo.User;
import com.cq.seckill.service.IGoodsService;
import com.cq.seckill.service.IOrderService;
import com.cq.seckill.service.ISeckillGoodsService;
import com.cq.seckill.service.ISeckillOrderService;
import com.cq.seckill.vo.GoodsVo;
import com.cq.seckill.vo.OrderDetailVo;
import com.cq.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.beans.Transient;
import java.util.Date;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chenqi
 * @since 2022-06-26
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    private ISeckillGoodsService seckillGoodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private OrderMapper orderMapper;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Transactional
    @Override
    public Order seckill(User user, GoodsVo goods) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //秒杀商品表减库存
        SecKillGoods seckillGoods = seckillGoodsService.getOne(new QueryWrapper<SecKillGoods>().eq(
                "goods_id",goods.getId()));
        seckillGoods.setStockCount(seckillGoods.getStockCount()-1);
        Boolean result = seckillGoodsService.update(new UpdateWrapper<SecKillGoods>().setSql("stock_count = " +
                "stock_count - 1").eq(
                        "goods_id",goods.getId()).gt("stock_count",0));
//        if(!result){
//            return null;
//        }
        if(seckillGoods.getStockCount() < 1){
            //判断是否库存为空
            valueOperations.set("isStockEmpty:"+goods.getId(),"0");
            return null;
        }
        //生成商品订单
        Order order = new Order();
        //order.setId(user.getId()); mybatis-plus已经获取了
        order.setUserId(user.getId());
        order.setGoodsId(goods.getId());
        order.setDeliveryAddrId(0L);
        order.setGoodsName(goods.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(seckillGoods.getSeckillPrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);
        //生成秒杀订单
        SecKillOrder seckillOrder = new SecKillOrder();
//        seckillOrder.setId(user.getId());
        seckillOrder.setUserId(user.getId());
        seckillOrder.setOrderId(order.getId());
        seckillOrder.setGoodsId(goods.getId());
        seckillOrderService.save(seckillOrder);
        redisTemplate.opsForValue().set("order:"+user.getId()+":"+goods.getId(),seckillOrder);
        return order;
    }

    @Override
    public OrderDetailVo detail(Long orderId) {
        if (null==orderId){
            throw new GlobalException(RespBeanEnum.ORDER_NOT_EXIST);
        }
        Order order = orderMapper.selectById(orderId);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(order.getGoodsId());
        OrderDetailVo detail = new OrderDetailVo();
        detail.setGoodsVo(goodsVo);
        detail.setOrder(order);
        return detail;
    }
}