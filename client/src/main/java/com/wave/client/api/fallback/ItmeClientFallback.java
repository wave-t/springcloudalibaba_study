package com.wave.client.api.fallback;


import com.wave.client.api.ItmeClient;
import com.wave.client.dto.ItemDTO;
import com.wave.client.dto.OrderDetailDTO;
import com.wave.common.exception.BizIllegalException;
import com.wave.common.utils.CollUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Collection;
import java.util.List;

//ItmeClient的降级逻辑处理类
//该类需要在DefaultFeignConfig 配置类中注册Bean
//该类还需要在ItmeClient接口上添加注解属性 fallbackFactory
@Slf4j // 日志注解
public class ItmeClientFallback implements FallbackFactory<ItmeClient> {
    //说明：该类的主要作用
    // 处理ItmeClient接口中方法异常的情况，直接返回，避免浪费服务器资源
    @Override
    public ItmeClient create(Throwable cause) {
        return new ItmeClient() {
            @Override
            public void restoreStock(List<OrderDetailDTO> detailDTOS) {
                throw new BizIllegalException(cause);
            }

            @Override
            public List<ItemDTO> queryItemByIds(Collection<Long> ids) {
                return CollUtils.emptyList(); //查询失败，返回空集合
            }

            @Override
            public ItemDTO queryItemById(Long id) {
                return null;
            }

            @Override
            public void deductStock(List<OrderDetailDTO> detailDTOS) {
                throw new BizIllegalException(cause); // 库存扣减业务需要触发事务回滚，查询失败，抛出异常
            }
        };
    }
}
