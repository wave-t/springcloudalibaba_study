package com.wave.item.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wave.client.dto.OrderDetailDTO;
import com.wave.item.domain.po.Item;
import org.apache.ibatis.annotations.Update;

/**
 * <p>
 * 商品表 Mapper 接口
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
public interface ItemMapper extends BaseMapper<Item> {

    //扣减库存
    @Update("UPDATE item SET stock = stock - #{num} WHERE id = #{itemId}")
    void updateStock(OrderDetailDTO orderDetail);

    //恢复库存
    @Update("UPDATE item SET stock = stock + #{num} WHERE id = #{itemId}")
    void restoreStock(OrderDetailDTO orderDetail);
}
