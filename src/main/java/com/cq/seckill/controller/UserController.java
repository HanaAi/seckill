package com.cq.seckill.controller;


import com.cq.seckill.pojo.User;
import com.cq.seckill.rabbitmq.MQSender;
import com.cq.seckill.vo.RespBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author chenqi
 * @since 2022-06-22
 */
@Controller
@RequestMapping("/user")
public class UserController {
    @Autowired
    private MQSender mqSender;

    @RequestMapping("/info")
    @ResponseBody
    public RespBean info(User user){
        return RespBean.success(user);
    }

//    @RequestMapping("/mq/topic01")
//    @ResponseBody
//    public void mq01(){
//        mqSender.send1("hello 01");
//    }
//
//    @RequestMapping("/mq/topic02")
//    @ResponseBody
//    public void mq02(){
//        mqSender.send2("hello");
//    }
}
