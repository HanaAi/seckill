package com.cq.seckill.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cq.seckill.exception.GlobalException;
import com.cq.seckill.mapper.UserMapper;
import com.cq.seckill.pojo.User;
import com.cq.seckill.service.IUserService;
import com.cq.seckill.utils.MD5Util;
import com.cq.seckill.utils.ValidatorUtil;
import com.cq.seckill.utils.UUIDUtil;
import com.cq.seckill.utils.CookieUtil;
import com.cq.seckill.vo.LoginVo;
import com.cq.seckill.vo.RespBean;
import com.cq.seckill.vo.RespBeanEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author chenqi
 * @since 2022-06-22
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    /**
     * 登录逻辑
     * @param loginVo
     * @return
     */
    @Override
    public RespBean doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {
        String id = loginVo.getMobile();
        String password = loginVo.getPassword();

        //参数校验，没做注解校验之前耦合性高，要自己在业务代码中写
        //校验传进来的id,密码
//        if(StringUtils.isEmpty(id) || StringUtils.isEmpty(password)){
//            return RespBean.error(RespBeanEnum.LOGIN_ERROR);
//        }
//        //校验手机号码
//        if(!ValidatorUtil.isMobile(id)){
//            return RespBean.error(RespBeanEnum.MOBILE_ERROR);
//        }
        //查询用户是否存在
        User user = userMapper.selectById(id);
        if(user == null){
            //由于我们自定义了全局异常处理
            //return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        if(!MD5Util.fromPassToDBPass(password,user.getSalt()).equals(user.getPassword())){
            //return RespBean.error(RespBeanEnum.LOGIN_ERROR);
            throw new GlobalException(RespBeanEnum.LOGIN_ERROR);
        }
        //生成cookie
        String ticket = UUIDUtil.uuid();
//        request.getSession().setAttribute(ticket,user);
        //存到redis中
        redisTemplate.opsForValue().set("user:"+ticket,user);
        CookieUtil.setCookie(request,response,"userTicket",ticket);

        return RespBean.success(ticket);
    }

    @Override
    public User getUserByCookie(String userTicket,HttpServletRequest request,HttpServletResponse response) {
        if(StringUtils.isEmpty(userTicket)){
            return null;
        }
        User user = (User) redisTemplate.opsForValue().get("user:" + userTicket);
        if(user != null){
            CookieUtil.setCookie(request,response,"userTicket",userTicket);
        }
        return user;
    }

    /**
     * 更新密码
     *
     * @param userTicket
     * @param id
     * @param password
     * @return
     */
    @Override
    public RespBean updatePassword(String userTicket, Long id, String password) {
        User user = userMapper.selectById(id);
        if (user == null) {
            throw new GlobalException(RespBeanEnum.MOBILE_NOT_EXIST);
        }
        user.setPassword(MD5Util.inputPassToDBPass(password, user.getSalt()));
        int result = userMapper.updateById(user);
        if (1 == result) {
            //删除Redis
            redisTemplate.delete("user:" + userTicket);
            return RespBean.success();
        }
        return RespBean.error(RespBeanEnum.PASSWORD_UPDATE_FAIL);
    }
}
