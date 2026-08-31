package com.marvel.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 字典数据（sys_dict_data）：某个字典类型下的键值对（label/value），
 * 前端下拉、状态标签等场景按 dictType 拉取。
 */
@Data
@TableName("sys_dict_data")
public class SysDictData {

    @TableId(type = IdType.AUTO)
    private Long dictCode;

    /** 所属字典类型键（冗余存储，查询免联表） */
    private String dictType;

    /** 展示标签（如「正常」） */
    private String dictLabel;

    /** 键值（如 0） */
    private String dictValue;

    private Integer orderNum;

    /** 0=正常 1=停用 */
    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
