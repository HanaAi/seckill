package com.cq.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cq.seckill.pojo.Goods;
import com.cq.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author chenqi
 * @since 2022-06-26
 */
public interface GoodsMapper extends BaseMapper<Goods> {

    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
