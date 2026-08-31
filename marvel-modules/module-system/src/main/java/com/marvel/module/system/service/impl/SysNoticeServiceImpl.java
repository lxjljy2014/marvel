package com.marvel.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.marvel.module.system.entity.SysNotice;
import com.marvel.module.system.mapper.SysNoticeMapper;
import com.marvel.module.system.service.SysNoticeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 通知公告业务实现：纯内容管理，无额外业务规则。
 */
@Service
public class SysNoticeServiceImpl extends ServiceImpl<SysNoticeMapper, SysNotice> implements SysNoticeService {

    @Override
    public List<SysNotice> listNotices(String title, String type) {
        return list(new LambdaQueryWrapper<SysNotice>()
                .like(StringUtils.hasText(title), SysNotice::getTitle, title)
                .eq(StringUtils.hasText(type), SysNotice::getType, type)
                .orderByDesc(SysNotice::getNoticeId));
    }

    @Override
    public void createNotice(SysNotice notice) {
        notice.setNoticeId(null);
        this.save(notice);
    }

    @Override
    public void updateNotice(SysNotice notice) {
        this.updateById(notice);
    }

    @Override
    public void deleteNotices(List<Long> noticeIds) {
        if (noticeIds != null && !noticeIds.isEmpty()) {
            this.removeByIds(noticeIds);
        }
    }
}
