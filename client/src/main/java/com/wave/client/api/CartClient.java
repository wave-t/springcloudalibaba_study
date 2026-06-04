package com.wave.client.api;


import com.wave.client.api.fallback.CartClientFallback;
import com.wave.client.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Set;

@FeignClient(value = "cart-service",
        configuration = DefaultFeignConfig.class, // 配置类
        fallbackFactory = CartClientFallback.class // 降级处理类
)
public interface CartClient {

    /**
     * 删除购物车商品
     * @param itemIds
     */
    @DeleteMapping("/carts")
    void deleteCartItemByIds(@RequestParam("ids")Set<Long> itemIds);
}
