package com.marvel.module.infra.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务（sys_job）：数据库可配置的 cron 调度任务。
 * invoke_target 格式为 Spring Bean 名 + 方法名（如 sampleJob.run），由调度器反射调用。
 */
@Data
@TableName("sys_job")
public class SysJob {

    @TableId(type = IdType.AUTO)
    private Long jobId;

    private String jobName;

    /** 任务分组（预留，默认 DEFAULT） */
    private String jobGroup;

    /** 调用目标：beanName.method（当前支持无参方法） */
    private String invokeTarget;

    /** cron 表达式（6 位，Quartz 语法） */
    private String cronExpression;

    /** 状态（0=正常 1=暂停） */
    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
