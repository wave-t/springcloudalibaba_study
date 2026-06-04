package com.wave.client.api;


import com.wave.client.api.fallback.TradeClientFallback;
import com.wave.client.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;

@FeignClient(value = "trade-service",
        configuration = DefaultFeignConfig.class, // 配置类
        fallbackFactory = TradeClientFallback.class // 降级处理类
)
public interface TradeClient {

   /**
     * 修改订单状态
     * @param
     */
    @PutMapping("/orders/{orderId}")
    void markOrderPaySuccess(@PathVariable("orderId") Long orderId);
}
