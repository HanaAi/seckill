package com.cq.seckill.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cq.seckill.pojo.Goods;
import com.cq.seckill.vo.GoodsVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author chenqi
 * @since 2022-06-26
 */
public interface IGoodsService extends IService<Goods> {
    /**
     * 获取商品列表
     * @return
     */
    List<GoodsVo> findGoodsVo();

    GoodsVo findGoodsVoByGoodsId(Long goodsId);
}
