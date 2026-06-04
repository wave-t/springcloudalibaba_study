package com.wave.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.wave.user.domain.dto.LoginFormDTO;
import com.wave.user.domain.vo.UserLoginVO;
import com.wave.user.domain.po.User;

/**
 * <p>
 * 用户表 服务类
 * </p>
 */
public interface IUserService extends IService<User> {

    UserLoginVO login(LoginFormDTO loginFormDTO);

    void deductMoney(String pw, Integer totalFee);
}
