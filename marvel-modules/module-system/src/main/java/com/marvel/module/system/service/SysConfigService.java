package com.marvel.module.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.marvel.module.system.entity.SysConfig;

/**
 * 系统参数服务：参数 CRUD 与按键名读取。
 */
public interface SysConfigService extends IService<SysConfig> {

    java.util.List<SysConfig> listConfigs(String configName, String configKey, String enabled);

    SysConfig getByKey(String configKey);

    void createConfig(SysConfig config);

    void updateConfig(SysConfig config);

    void deleteConfigs(java.util.List<Long> configIds);
}
