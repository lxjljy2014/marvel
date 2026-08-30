package com.marvel.module.system.security;

import cn.dev33.satoken.stp.StpInterface;
import com.marvel.api.system.SystemApi;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * Sa-Token 权限数据源：登录鉴权时由框架回调，从 system 域读取当前用户的角色与按钮权限。
 *
 * <p>依赖 marvel-api 契约接口 {@link SystemApi} 而非直接访问 system 模块内部，
 * 保证未来拆分微服务时只需把本地实现替换为 Feign 客户端。
 */
@Component
@RequiredArgsConstructor
public class StpInterfaceImpl implements StpInterface {

    private final SystemApi systemApi;

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        return new ArrayList<>(systemApi.getPermissionsByUserId(Long.valueOf(loginId.toString())));
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return new ArrayList<>(systemApi.getRoleKeysByUserId(Long.valueOf(loginId.toString())));
    }
}
