package com.marvel.module.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.marvel.module.system.entity.SysNotice;

import java.util.List;

/**
 * 通知公告服务。
 */
public interface SysNoticeService extends IService<SysNotice> {

    List<SysNotice> listNotices(String title, String type);

    void createNotice(SysNotice notice);

    void updateNotice(SysNotice notice);

    void deleteNotices(List<Long> noticeIds);
}
