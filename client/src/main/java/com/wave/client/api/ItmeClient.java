package com.wave.client.api;


import com.wave.client.api.fallback.ItmeClientFallback;
import com.wave.client.config.DefaultFeignConfig;
import com.wave.client.dto.ItemDTO;
import com.wave.client.dto.OrderDetailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Collection;
import java.util.List;

@FeignClient(value = "item-service",
        configuration = DefaultFeignConfig.class,  // 配置类
        fallbackFactory = ItmeClientFallback.class // 降级处理类
)
public interface ItmeClient {

    /**
     * 根据ids查询商品信息
     * @param ids 商品Id
     * @return 商品信息
     */
    @GetMapping("/items")
    List<ItemDTO> queryItemByIds(@RequestParam("ids") Collection<Long> ids);
    /**
     * 根据id查询商品信息
     * @param id 商品Id
     * @return 商品信息
     */
    @GetMapping("/items/{id}")
    ItemDTO queryItemById(@RequestParam("id") Long id);

    /**
     * 批量扣减库存
     * @param detailDTOS 订单详情
     */
    @PutMapping("/items/stock/deduct")
    void deductStock(@RequestBody List<OrderDetailDTO> detailDTOS);

    /**
     * 批量恢复库存
     * @param detailDTOS 订单详情
     */
    @PutMapping("/items/stock/restore")
    void restoreStock(@RequestBody List<OrderDetailDTO> detailDTOS);
}
