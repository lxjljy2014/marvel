package com.marvel.module.infra.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.marvel.common.result.R;
import com.marvel.module.infra.service.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * infra 域文件接口，路径前缀 /infra/**（与未来网关路由一致）。
 */
@RestController
@RequestMapping("/infra/file")
@RequiredArgsConstructor
public class InfraFileController {

    private final StorageService storageService;

    /** 上传文件，返回可访问的 URL（类型/扩展名白名单校验见存储实现） */
    @SaCheckLogin
    @PostMapping("/upload")
    public R<Map<String, String>> upload(@RequestParam("file") MultipartFile file) throws Exception {
        return R.ok(Map.of("url", storageService.upload(file)));
    }
}
