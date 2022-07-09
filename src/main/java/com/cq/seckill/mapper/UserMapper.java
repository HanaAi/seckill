package com.cq.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cq.seckill.pojo.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author chenqi
 * @since 2022-06-22
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
