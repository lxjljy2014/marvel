package com.marvel.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典类型（sys_dict_type）：一组业务枚举的容器（如 sys_normal_disable），
 * 具体键值对存 sys_dict_data。
 */
@Data
@TableName("sys_dict_type")
public class SysDictType {

    @TableId(type = IdType.AUTO)
    private Long dictId;

    /** 字典名称（展示用，如「系统状态」） */
    private String dictName;

    /** 字典类型键（唯一，如 sys_normal_disable） */
    private String dictType;

    /** 0=正常 1=停用 */
    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
