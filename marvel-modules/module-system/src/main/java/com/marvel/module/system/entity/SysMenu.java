package com.marvel.module.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@TableName("sys_menu")
public class SysMenu {

    @TableId(type = IdType.AUTO)
    private Long menuId;

    private Long parentId;

    private String menuName;

    /** M=目录 C=菜单 F=按钮 */
    private String menuType;

    private Integer orderNum;

    /** 路由地址 */
    private String path;

    /** 组件路径 */
    private String component;

    /** 权限标识，如 system:user:list */
    private String perms;

    private String icon;

    /** 0=显示 1=隐藏 */
    private String visible;

    /** 0=正常 1=停用 */
    private String status;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    /** 非表字段：子菜单（树形接口返回用） */
    @TableField(exist = false)
    private List<SysMenu> children;

    /** 非表字段：是否存在子菜单（懒加载展开箭头标记） */
    @TableField(exist = false)
    private Boolean hasChild;
}
