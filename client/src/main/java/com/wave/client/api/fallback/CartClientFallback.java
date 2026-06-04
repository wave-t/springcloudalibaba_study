package com.wave.client.api.fallback;


import com.wave.client.api.CartClient;
import com.wave.common.exception.BizIllegalException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;

import java.util.Set;

@Slf4j
public class CartClientFallback implements FallbackFactory<CartClient> {
    @Override
    public CartClient create(Throwable cause) {
        return new CartClient() {
            @Override
            public void deleteCartItemByIds(Set<Long> itemIds) {
                throw new BizIllegalException(cause); //请求异常降级处理。
            }
        };
    }
}
