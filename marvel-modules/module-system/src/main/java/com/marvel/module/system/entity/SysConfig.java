package com.marvel.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统参数（sys_config）：运行时可调整的键值配置（如默认密码、验证码开关）。
 */
@Data
@TableName("sys_config")
public class SysConfig {

    @TableId(type = IdType.AUTO)
    private Long configId;

    private String configName;

    /** 参数键名（唯一，如 sys.user.initPassword） */
    private String configKey;

    private String configValue;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
