package com.marvel.module.infra.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * 文件存储抽象：本地实现见 LocalStorageService，未来可替换为 OSS/COS 实现。
 */
public interface StorageService {

    /** 返回文件访问 URL */
    String upload(MultipartFile file) throws Exception;
}
