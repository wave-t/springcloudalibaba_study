package com.wave.client.api;


import com.wave.client.api.fallback.UserClientFallback;
import com.wave.client.config.DefaultFeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(value = "user-service",
        configuration = DefaultFeignConfig.class, // 配置类
        fallbackFactory = UserClientFallback.class // 降级处理类
)
public interface UserClient {

    /**
     * 扣减余额
     * @param pw
     * @param amount
     */
    @PutMapping("/users/money/deduct")
    void deductMoney(@RequestParam("pw") String pw, @RequestParam("amount") Integer amount);
}
