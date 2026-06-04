package com.wave.item.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wave.client.dto.ItemDTO;
import com.wave.client.dto.OrderDetailDTO;
import com.wave.common.exception.BizIllegalException;
import com.wave.common.utils.BeanUtils;
import com.wave.item.domain.po.Item;
import com.wave.item.mapper.ItemMapper;
import com.wave.item.service.IItemService;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;

/**
 * <p>
 * 商品表 服务实现类
 * </p>
 *
 * @author 虎哥
 */
@Service
public class ItemServiceImpl extends ServiceImpl<ItemMapper, Item> implements IItemService {

    @Override
    public void deductStock(List<OrderDetailDTO> items) {
        String sqlStatement = "com.heima.item.mapper.ItemMapper.updateStock";
        boolean r = false;
        try {
            r = executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            log.error("更新库存异常", e);
            throw new BizIllegalException("库存更新异常！");
        }
        if (!r) {
            throw new BizIllegalException("库存不足！");
        }
    }

    @Override
    public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
        return BeanUtils.copyList(listByIds(ids), ItemDTO.class);
    }

    @Override
    public void restoreStock(List<OrderDetailDTO> items) {
        String sqlStatement = "com.heima.item.mapper.ItemMapper.restoreStock";
        try {
            executeBatch(items, (sqlSession, entity) -> sqlSession.update(sqlStatement, entity));
        } catch (Exception e) {
            log.error("更新库存异常", e);
            throw new BizIllegalException("库存更新异常！");
        }
    }
}
