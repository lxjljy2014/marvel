package com.marvel.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 通知公告（sys_notice）：面向全体用户的通知/公告内容。
 */
@Data
@TableName("sys_notice")
public class SysNotice {

    @TableId(type = IdType.AUTO)
    private Long noticeId;

    private String title;

    /** 公告内容（纯文本/受限 HTML，由前端渲染为文本展示） */
    private String content;

    /** 类型（1=通知 2=公告） */
    private String type;

    /** 状态（0=正常 1=关闭） */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
