package com.cq.seckill.controller;

import com.cq.seckill.pojo.Order;
import com.cq.seckill.pojo.SecKillMessage;
import com.cq.seckill.pojo.SecKillOrder;
import com.cq.seckill.pojo.User;
import com.cq.seckill.rabbitmq.MQSender;
import com.cq.seckill.service.IGoodsService;
import com.cq.seckill.service.IOrderService;
import com.cq.seckill.service.ISeckillOrderService;
import com.cq.seckill.utils.JsonUtil;
import com.cq.seckill.vo.GoodsVo;
import com.cq.seckill.vo.RespBean;
import com.cq.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/seckill")
public class SecKillController implements InitializingBean {
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private ISeckillOrderService seckillOrderService;
    @Autowired
    private IOrderService orderService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private MQSender mqSender;

    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();

    /**
     * 秒杀
     * Windows优化前 QPS ： 257.8
     * 优化后: 636.3
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "doSeckill" , method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(Model model, User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
//        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
//        if(goods.getStockCount() < 1){
//            model.addAttribute("errmsg", RespBeanEnum.EMPTY_STOCK.getMessage());
//            return "secKillFail";
//        }
        /** 旧方法
        //判断是否重复抢购
        SeckillOrder seckillOrder = seckillOrderService.getOne(new QueryWrapper<SeckillOrder>().eq("user_id", user.getId()).eq(
                "goods_id", goodsId));
        if(seckillOrder != null){
            model.addAttribute("errmsg", RespBeanEnum.REPEATE_ERROR.getMessage());
            return "secKillFail";
        }
        Order order = orderService.seckill(user,goods);
        model.addAttribute("order",order);
        model.addAttribute("goods",goods);
        return "orderDetail";
         */
        //redis优化
        ValueOperations valueOperations = redisTemplate.opsForValue();
        SecKillOrder seckillOrder = (SecKillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+
                ":" + goodsId);
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //内存标记以减少redis请求
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //预减库存操作
        Long stock = valueOperations.decrement("seckillGoods:"+goodsId);
        if(stock < 0){
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:"+goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        SecKillMessage seckillMessage = new SecKillMessage(user,goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
    }

    //初始化时将库存数预载到redis中
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> goodsVo = goodsService.findGoodsVo();
        if(CollectionUtils.isEmpty(goodsVo)){
            return;
        }
        goodsVo.forEach(goods -> {
            redisTemplate.opsForValue().set("seckillGoods:" + goods.getId(), goods.getStockCount());
            EmptyStockMap.put(goods.getId(),false);
                }
        );
    }

    /**
     * 获取秒杀结果
     * @param user
     * @param goodsId
     * @return 0 排队中 ；orderId：成功；-1：失败
     */
    @RequestMapping(value = "/result",method = RequestMethod.GET)
    @ResponseBody
    public RespBean getResult(User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        Long orderId = seckillOrderService.getResult(user,goodsId);
        return RespBean.success(orderId);
    }
}
