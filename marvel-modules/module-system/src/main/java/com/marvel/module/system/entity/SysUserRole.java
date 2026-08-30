package com.marvel.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_user_role")
/** 用户-角色关联实体（sys_user_role）。 */
public class SysUserRole {

    private Long userId;
    private Long roleId;
}
