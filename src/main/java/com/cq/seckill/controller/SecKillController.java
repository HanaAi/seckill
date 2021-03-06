package com.cq.seckill.controller;

import com.cq.seckill.config.AccessLimit;
import com.cq.seckill.exception.GlobalException;
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
import com.pig4cloud.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
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
    @Autowired
    private RedisScript<Long> script;

    private Map<Long,Boolean> EmptyStockMap = new HashMap<>();

    /**
     * ??????????????????
     * @param user
     * @param goodsId
     * @return
     */
    @AccessLimit(second = 5,maxCount = 5,needLogin = true)
    @RequestMapping(value = "/path", method = RequestMethod.GET)
    @ResponseBody
    public RespBean getPath(User user, Long goodsId, String captcha) {
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        System.out.println(captcha);
        boolean check = orderService.checkCaptcha(user,goodsId,captcha);
        if(!check){
            return RespBean.error(RespBeanEnum.CAPTCHA_ERROR);
        }
        String url = orderService.createPath(user, goodsId);
        return RespBean.success(url);
    }

    /**
     * ??????
     * Windows????????? QPS ??? 257.8
     * ?????????: 636.3
     * @param path
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/{path}/doSeckill" , method = RequestMethod.POST)
    @ResponseBody
    public RespBean doSecKill(@PathVariable String path, User user, Long goodsId){
        if(user == null){
            return RespBean.error(RespBeanEnum.SESSION_ERROR);
        }
        //redis??????
        ValueOperations valueOperations = redisTemplate.opsForValue();
        boolean check = orderService.checkPath(user, goodsId,path);
        if(!check){
            return RespBean.error(RespBeanEnum.REQUEST_ILLEGAL);
        }

        SecKillOrder seckillOrder = (SecKillOrder) redisTemplate.opsForValue().get("order:"+user.getId()+
                ":" + goodsId);
        if(seckillOrder != null){
            return RespBean.error(RespBeanEnum.REPEATE_ERROR);
        }
        //?????????????????????redis??????
        if(EmptyStockMap.get(goodsId)){
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //??????????????????
        Long stock = (Long) redisTemplate.execute(script,
                Collections.singletonList("seckillGoods:" + goodsId), Collections.EMPTY_LIST);
        if (stock < 0) {
            EmptyStockMap.put(goodsId,true);
            valueOperations.increment("seckillGoods:" + goodsId);
            return RespBean.error(RespBeanEnum.EMPTY_STOCK);
        }
        //????????????
        SecKillMessage seckillMessage = new SecKillMessage(user,goodsId);
        mqSender.sendSecKillMessage(JsonUtil.object2JsonStr(seckillMessage));
        return RespBean.success(0);
    }

    //?????????????????????????????????redis???
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
     * ??????????????????
     * @param user
     * @param goodsId
     * @return 0 ????????? ???orderId????????????-1?????????
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

    @RequestMapping(value = "/captcha",method = RequestMethod.GET)
    public void verifyCode(User user, Long goodsId, HttpServletResponse response) {
        if(user == null || goodsId < 0){
            throw new GlobalException(RespBeanEnum.REQUEST_ILLEGAL);
        }
        //???????????????????????????????????????
        response.setContentType("image/jpg");
        response.setHeader("Pargam","No-cache");
        response.setHeader("Cache-Control","No-cache");
        response.setDateHeader("Expires",0);
        //????????????????????????Redis
        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130,32,3);
        redisTemplate.opsForValue().set("captcha:"+user.getId()+":"+goodsId,captcha.text(),300, TimeUnit.SECONDS);
        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            log.error("?????????????????????",e.getMessage());
        }
    }
}
