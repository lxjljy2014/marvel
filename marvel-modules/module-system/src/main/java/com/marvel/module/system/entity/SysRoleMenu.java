package com.marvel.module.system.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("sys_role_menu")
/** 角色-菜单关联实体（sys_role_menu）。 */
public class SysRoleMenu {

    private Long roleId;
    private Long menuId;
}
