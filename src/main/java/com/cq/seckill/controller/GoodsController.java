package com.cq.seckill.controller;

import com.cq.seckill.pojo.User;
import com.cq.seckill.service.IGoodsService;
import com.cq.seckill.service.IUserService;
import com.cq.seckill.vo.DetailVo;
import com.cq.seckill.vo.GoodsVo;
import com.cq.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private IUserService userService;
    @Autowired
    private IGoodsService goodsService;
    @Autowired
    private RedisTemplate redisTemplate;
    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 商品列表页
     * windows未优化前 QPS：1260
     * Linux 未优化前  QPS：226，中途OOM报错
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/toList",produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(HttpServletRequest request, HttpServletResponse response,
                         Model model, User user){
//        if(StringUtils.isEmpty(ticket)){
//            return "login";
//        }
        //在商品页面根据cookie验证用户权限，防止直接url访问
        //User user = (User) session.getAttribute(ticket);
        /**
         * 我们使用mvc进行统一的用户验证，不需要再业务代码中了
         */
//        User user = userService.getUserByCookie(ticket, request, response);
//        if(user == null){
//            return "login";
//        }
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //Redis中获取页面，如果不为空，直接返回页面
        String html = (String) valueOperations.get("goodsList");
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        //设置属性传到前端
        model.addAttribute("user",user);
        model.addAttribute("goodsList",goodsService.findGoodsVo());
        //如果为空，手动渲染，存入Redis并返回
        WebContext context = new WebContext(request, response,
                request.getServletContext(), request.getLocale(),model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList",
                context);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * 跳转商品详情页面
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/toDetail2/{goodsId}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(HttpServletRequest request, HttpServletResponse response,
                           Model model,User user,@PathVariable Long goodsId){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        //Redis中获取页面，如果不为空，直接返回页面
        String html = (String) valueOperations.get("goodsDetail:" + goodsId);
        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        model.addAttribute("user", user);
        GoodsVo goodsVo = goodsService.findGoodsVoByGoodsId(goodsId);
        model.addAttribute("goods", goodsVo);

        Date startDate = goodsVo.getStartDate();
        Date endDate = goodsVo.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //秒杀倒计时
        int remainSeconds = 0;
        if(nowDate.before(startDate)){
            //秒杀未开始
            remainSeconds = ((int) ((startDate.getTime() - nowDate.getTime()) / 1000));
        }else if(nowDate.after(endDate)){
            secKillStatus = 2;
            remainSeconds = -1;
        }else{
            secKillStatus = 1;
            remainSeconds = 0;
        }
        model.addAttribute("secKillStatus",secKillStatus);
        model.addAttribute("remainSeconds",remainSeconds);
//        return "goodsDetail";
        //如果为空，手动渲染，存入Redis并返回
        WebContext context = new WebContext(request, response,
                request.getServletContext(), request.getLocale(),
                model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail",
                context);
        if (!StringUtils.isEmpty(html)) {
            valueOperations.set("goodsDetail:" + goodsId, html, 60,
                    TimeUnit.SECONDS);
        }
        return html;
    }

    /**
     * 跳转商品详情页
     *
     * @param model
     * @param user
     * @param goodsId
     * @return
     */
    @RequestMapping(value = "/detail/{goodsId}")
    @ResponseBody
    public RespBean toDetail1(HttpServletRequest request, HttpServletResponse
            response, Model model, User user,
                             @PathVariable Long goodsId) {
        GoodsVo goods = goodsService.findGoodsVoByGoodsId(goodsId);
        Date startDate = goods.getStartDate();
        Date endDate = goods.getEndDate();
        Date nowDate = new Date();
        //秒杀状态
        int secKillStatus = 0;
        //剩余开始时间
        int remainSeconds = 0;
        //秒杀还未开始
        if (nowDate.before(startDate)) {
            remainSeconds = (int) ((startDate.getTime() - nowDate.getTime()) / 1000);
            // 秒杀已结束
        } else if (nowDate.after(endDate)) {
            secKillStatus = 2;
            remainSeconds = -1;
            // 秒杀中
        } else {
            secKillStatus = 1;
            remainSeconds = 0;
        }
        DetailVo detailVo = new DetailVo();
        detailVo.setGoodsVo(goods);
        detailVo.setUser(user);
        detailVo.setRemainSeconds(remainSeconds);
        detailVo.setSecKillStatus(secKillStatus);
        return RespBean.success(detailVo);
    }
}
