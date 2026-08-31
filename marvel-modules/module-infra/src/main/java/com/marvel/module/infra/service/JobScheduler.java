package com.marvel.module.infra.service;

import com.marvel.module.infra.entity.SysJob;

/**
 * 任务调度核心：数据库任务 ↔ Spring TaskScheduler 的注册与刷新。
 * 拆分微服务时随 infra 模块整体迁移。
 */
public interface JobScheduler {

    /** 按 sys_job 表当前数据全量重建调度（先清空再注册启用状态的任务），增删改后调用 */
    void refresh();

    /** 立即同步执行一次指定任务（不走 cron 触发），返回执行耗时 ms */
    long runOnce(SysJob job);
}
