package com.marvel.module.infra.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.IService;
import com.marvel.module.infra.entity.SysJob;
import com.marvel.module.infra.entity.SysJobLog;

import java.util.List;

/**
 * 定时任务管理服务：任务 CRUD、启停、立即执行、执行日志查询。
 */
public interface SysJobService extends IService<SysJob> {

    List<SysJob> listJobs(String jobName, String status);

    void createJob(SysJob job);

    void updateJob(SysJob job);

    void deleteJobs(List<Long> jobIds);

    /** 启用/暂停任务并即时刷新调度 */
    void changeStatus(Long jobId, String status);

    /** 立即执行一次并返回耗时 ms */
    long runOnce(Long jobId);

    /** 最近执行日志（供任务管理页日志对话框使用） */
    List<SysJobLog> listLogs(Long jobId, int limit);
}
