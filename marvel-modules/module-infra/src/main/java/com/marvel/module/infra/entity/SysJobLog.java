package com.marvel.module.infra.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 任务执行日志（sys_job_log）：每次触发记录成败与耗时。
 */
@Data
@TableName("sys_job_log")
public class SysJobLog {

    @TableId(type = IdType.AUTO)
    private Long jobLogId;

    private Long jobId;

    private String jobName;

    /** 状态（0=成功 1=失败） */
    private String status;

    /** 失败时的异常信息 */
    private String errorMsg;

    private LocalDateTime startTime;

    private LocalDateTime endTime;
}
