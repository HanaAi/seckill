package com.cq.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.seckill.pojo.User;
import com.cq.seckill.vo.LoginVo;
import com.cq.seckill.vo.RespBean;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chenqi
 * @since 2022-06-22
 */
public interface IUserService extends IService<User> {

    RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    /**
     * 根据cookie获取用户
     * @param userTicket
     * @return
     */
    User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response);

    RespBean updatePassword(String userTicket,Long id,String password);

    User getByUserTicket(String ticket, HttpServletRequest request, HttpServletResponse response);
}
