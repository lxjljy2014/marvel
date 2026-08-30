package com.marvel.framework.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Paths;

/**
 * 上传文件静态资源映射。
 *
 * <p>单体部署时由本服务直接对外提供 /uploads/**；拆分微服务或上云后，
 * 该职责移交 Nginx/OSS，删除本配置即可。
 */
@Configuration
public class UploadResourceConfig implements WebMvcConfigurer {

    @Value("${marvel.storage.local.path:./uploads}")
    private String basePath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:" + Paths.get(basePath).toAbsolutePath().normalize() + "/");
    }
}
