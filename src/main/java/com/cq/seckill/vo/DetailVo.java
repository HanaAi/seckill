package com.cq.seckill.vo;

import com.cq.seckill.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetailVo {

    private User user;
    private GoodsVo goodsVo;
    private int secKillStatus;
    private int remainSeconds;

}
