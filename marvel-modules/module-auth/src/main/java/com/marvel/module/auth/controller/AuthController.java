package com.marvel.module.auth.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.marvel.api.system.SystemApi;
import com.marvel.api.system.dto.SysUserDTO;
import com.marvel.common.constant.Constants;
import com.marvel.common.exception.BusinessException;
import com.marvel.common.result.R;
import com.marvel.module.auth.dto.LoginBody;
import com.marvel.module.auth.service.CaptchaService;
import com.marvel.module.auth.service.LoginProtectService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 认证域接口：验证码、登录、登出、当前用户信息与动态路由。
 *
 * <p>拆分微服务时本控制器整体迁移为独立的 auth 服务，前端无感知（路径 /auth/** 保持不变）。
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final SystemApi systemApi;
    private final CaptchaService captchaService;
    private final LoginProtectService loginProtectService;
    /** BCrypt 校验器无状态，可安全复用 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /** 获取图形验证码（数学运算 SVG，答案存 Redis，2 分钟有效、一次性使用） */
    @GetMapping("/captcha")
    public R<Map<String, String>> captcha() {
        return R.ok(captchaService.generateCaptcha());
    }

    /**
     * 账号密码登录。安全控制链：防爆破锁定 → 验证码校验 → 密码校验（BCrypt）→ 账号状态 → 签发 Sa-Token。
     */
    @PostMapping("/login")
    public R<Map<String, Object>> login(@Valid @RequestBody LoginBody body, HttpServletRequest request) {
        String ip = resolveClientIp(request);
        loginProtectService.checkLocked(body.getUsername(), ip);

        // 验证码一次性使用：无论对错先消费，防止重放
        if (!captchaService.verify(body.getUuid(), body.getCode())) {
            throw new BusinessException("验证码错误或已过期");
        }

        SysUserDTO user = systemApi.getUserByUsername(body.getUsername());
        if (user == null || !passwordEncoder.matches(body.getPassword(), user.getPassword())) {
            loginProtectService.recordFailure(body.getUsername(), ip);
            // 不区分「用户不存在」与「密码错误」，避免账号枚举
            throw new BusinessException("用户名或密码错误");
        }
        if (Constants.STATUS_DISABLED.equals(user.getStatus())) {
            throw new BusinessException("账号已停用，请联系管理员");
        }

        loginProtectService.clearFailure(body.getUsername(), ip);
        StpUtil.login(user.getId());
        return R.ok(Map.of("token", StpUtil.getTokenValue()));
    }

    /** 退出登录，销毁当前会话（未登录时为幂等操作） */
    @PostMapping("/logout")
    public R<Void> logout() {
        StpUtil.logout();
        return R.ok();
    }

    /** 当前登录用户的基本信息、角色与按钮权限集合 */
    @GetMapping("/getInfo")
    public R<Map<String, Object>> getInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        SysUserDTO user = systemApi.getUserById(userId);
        // 密码密文绝不外发
        user.setPassword(null);
        Set<String> roles = systemApi.getRoleKeysByUserId(userId);
        Set<String> permissions = systemApi.getPermissionsByUserId(userId);
        return R.ok(Map.of(
                "user", user,
                "roles", roles,
                "permissions", permissions
        ));
    }

    /** 当前用户可见的前端动态路由菜单树（仅目录/菜单，不含按钮） */
    @GetMapping("/getRouters")
    public R<List<?>> getRouters() {
        long userId = StpUtil.getLoginIdAsLong();
        return R.ok(List.copyOf(systemApi.getMenusByUserId(userId)));
    }

    /**
     * 解析客户端真实 IP：优先取反向代理传递的 X-Forwarded-For 首段。
     * 取到的值仅用于防爆破计数，不作为业务信任来源。
     */
    private String resolveClientIp(HttpServletRequest request) {
        String xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            return xff.split(",")[0].trim();
        }
        String realIp = request.getHeader("X-Real-IP");
        return realIp != null && !realIp.isBlank() ? realIp : request.getRemoteAddr();
    }
}
