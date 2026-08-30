package com.marvel.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("sys_user")
public class SysUser {

    @TableId(type = IdType.AUTO)
    private Long userId;

    private Long deptId;

    private String username;

    /** BCrypt 密文 */
    private String password;

    private String nickname;

    private String email;

    private String phone;

    private String sex;

    private String avatar;

    /** 0=正常 1=停用 */
    private String status;

    private String remark;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 非表字段：部门名称、角色 ID 集合 */
    @TableField(exist = false)
    private String deptName;

    @TableField(exist = false)
    private java.util.List<Long> roleIds;
}
