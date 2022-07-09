package com.cq.seckill.rabbitmq;

import com.cq.seckill.pojo.SecKillMessage;
import com.cq.seckill.pojo.SecKillOrder;
import com.cq.seckill.pojo.User;
import com.cq.seckill.service.IGoodsService;
import com.cq.seckill.service.IOrderService;
import com.cq.seckill.utils.JsonUtil;
import com.cq.seckill.vo.GoodsVo;
import com.cq.seckill.vo.RespBean;
import com.cq.seckill.vo.RespBeanEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class MQReciver {


//    @RabbitListener(queues = "queue_topic01")
//    public void recieve01(Object msg){
//        log.info("01接收消息"+msg);
//    }
//
//    @RabbitListener(queues = "queue_topic02")
//    public void recieve02(Object msg){
//        log.info("02接收消息"+msg);
//    }
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "seckillQueue")
    public void recieve(String message){
        log.info("接收消息:" + message);
        SecKillMessage secKillMessage = JsonUtil.jsonStr2Object(message, SecKillMessage.class);
        Long goodsId = secKillMessage.getGoodsId();
        User user = secKillMessage.getUser();
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        if(goodsVo.getStockCount() < 1){
            return;
        }

        //判断是否重复抢购
        SecKillOrder seckillOrder = (SecKillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+
                ":" + goodsId);
        if(seckillOrder != null){
            return;
        }

        //下单
        orderService.seckill(user,goodsVo);
    }
}
