package com.wave.cart.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wave.cart.config.CartProperties;
import com.wave.cart.domain.dto.CartFormDTO;
import com.wave.cart.domain.po.Cart;
import com.wave.cart.domain.vo.CartVO;
import com.wave.cart.mapper.CartMapper;
import com.wave.cart.service.ICartService;
import com.wave.client.api.ItmeClient;
import com.wave.client.dto.ItemDTO;
import com.wave.common.exception.BizIllegalException;
import com.wave.common.utils.BeanUtils;
import com.wave.common.utils.CollUtils;
import com.wave.common.utils.UserContext;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单详情表 服务实现类
 * </p>
 *
 * @author 虎哥
 * @since 2023-05-05
 */
@Service
@RequiredArgsConstructor // lombok注解 将带final修饰的对象创建构造方法，注入容器中
public class CartServiceImpl extends ServiceImpl<CartMapper, Cart> implements ICartService {
    private final RestTemplate restTemplate;

//    private final IItemService itemService;
    private final DiscoveryClient discoveryClient; // 注册中心工具对象

    private final ItmeClient itemClient;

    private  final CartProperties cartProperties; // 购物车配置属性类

    @Override
    public void addItem2Cart(CartFormDTO cartFormDTO) {
        // 1.获取登录用户
        Long userId = UserContext.getUser();

        // 2.判断是否已经存在
        if(checkItemExists(cartFormDTO.getItemId(), userId)){
            // 2.1.存在，则更新数量
            baseMapper.updateNum(cartFormDTO.getItemId(), userId);
            return;
        }
        // 2.2.不存在，判断是否超过购物车数量
        checkCartsFull(userId);

        // 3.新增购物车条目
        // 3.1.转换PO
        Cart cart = BeanUtils.copyBean(cartFormDTO, Cart.class);
        // 3.2.保存当前用户
        cart.setUserId(userId);
        // 3.3.保存到数据库
        save(cart);
    }

    @Override
    public List<CartVO> queryMyCarts() {
        // 1.查询我的购物车列表
        List<Cart> carts = lambdaQuery().eq(Cart::getUserId, UserContext.getUser()).list();
//        List<Cart> carts = lambdaQuery().eq(Cart::getUserId,1L).list();
        if (CollUtils.isEmpty(carts)) {
            return CollUtils.emptyList();
        }

        // 2.转换VO
        List<CartVO> vos = BeanUtils.copyList(carts, CartVO.class);

        // 3.处理VO中的商品信息
        handleCartItems(vos);

        // 4.返回
        return vos;
    }

    private void handleCartItems(List<CartVO> vos) {
        // 1.获取商品id
        Set<Long> itemIds = vos.stream().map(CartVO::getItemId).collect(Collectors.toSet());
        // 2.查询商品 但服务
//        List<ItemDTO> items = itemService.queryItemByIds(itemIds);
        //2.1 发送请求，查询商品 使用HTTP请求获取商品信息
//        ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
//                "http://localhost:8081/items?ids={ids}", // 请求路径
//                HttpMethod.GET, // 请求方式
//                null, // 请求实体
//                new ParameterizedTypeReference<List<ItemDTO>>() {}, //返回值类型
//                Map.of("ids",CollUtils.join(itemIds, ",")) // 请求参数
//        );
//        //2.1.1 使用注册中心获取商品信息，发现需要访问的服务实例
//        List<ServiceInstance> instances = discoveryClient.getInstances("item-service");
//        //2.1.2 负载均衡挑选一个实例，这里使用随机挑选
//        ServiceInstance instance = instances.get(RandomUtil.randomInt(instances.size()));
//        //2.1.3 发出请求查询数据
//        ResponseEntity<List<ItemDTO>> response = restTemplate.exchange(
//                instance.getUri()+"/items?ids={ids}", // 请求路径
//                HttpMethod.GET, // 请求方式
//                null, // 请求实体
//                new ParameterizedTypeReference<List<ItemDTO>>() {}, //返回值类型
//                Map.of("ids",CollUtils.join(itemIds, ",")) // 请求参数
//        );
        //2.2.1 使用feign调用商品服务 获取商品列表
        List<ItemDTO> items = itemClient.queryItemByIds(itemIds);
//        //2.2 处理请求结果
//        List<ItemDTO> items = null;
//        if (response.getStatusCode().is2xxSuccessful()){
//            items = response.getBody();
//        }
//        //2.3查询失败，抛出异常
//        if (CollUtils.isEmpty(items)) {
//            throw new BizIllegalException("查询商品失败");
//        }
        // 3.转为 id 到 item的map
        Map<Long, ItemDTO> itemMap = items.stream().collect(Collectors.toMap(ItemDTO::getId, Function.identity()));
        // 4.写入vo
        for (CartVO v : vos) {
            ItemDTO item = itemMap.get(v.getItemId());
            if (item == null) {
                continue;
            }
            v.setNewPrice(item.getPrice());
            v.setStatus(item.getStatus());
            v.setStock(item.getStock());
        }
    }

    @Override
    public void removeByItemIds(Collection<Long> itemIds) {
        // 1.构建删除条件，userId和itemId
        QueryWrapper<Cart> queryWrapper = new QueryWrapper<Cart>();
        queryWrapper.lambda()
                .eq(Cart::getUserId, UserContext.getUser())
                .in(Cart::getItemId, itemIds);
        // 2.删除
        remove(queryWrapper);
    }

    private void checkCartsFull(Long userId) {
        int count = lambdaQuery().eq(Cart::getUserId, userId).count().intValue();
        int maxAmount = cartProperties.getMaxAmount();
        if (count >= maxAmount) {
            throw new BizIllegalException(StrUtil.format("用户购物车课程不能超过{}", maxAmount));
        }
    }

    private boolean checkItemExists(Long itemId, Long userId) {
        int count = lambdaQuery()
                .eq(Cart::getUserId, userId)
                .eq(Cart::getItemId, itemId)
                .count().intValue();
        return count > 0;
    }
}
