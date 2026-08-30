package com.marvel.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.marvel.module.system.entity.SysMenu;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
/** 菜单 Mapper：提供按用户聚合查询菜单树与权限标识的 SQL。 */
public interface SysMenuMapper extends BaseMapper<SysMenu> {

    @Select("""
            SELECT DISTINCT m.* FROM sys_menu m
            JOIN sys_role_menu rm ON rm.menu_id = m.menu_id
            JOIN sys_user_role ur ON ur.role_id = rm.role_id
            WHERE ur.user_id = #{userId} AND m.status = '0'
            ORDER BY m.parent_id, m.order_num
            """)
    List<SysMenu> selectMenusByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT DISTINCT m.perms FROM sys_menu m
            JOIN sys_role_menu rm ON rm.menu_id = m.menu_id
            JOIN sys_user_role ur ON ur.role_id = rm.role_id
            WHERE ur.user_id = #{userId} AND m.status = '0' AND m.perms IS NOT NULL AND m.perms != ''
            """)
    List<String> selectPermsByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT COUNT(1) FROM sys_menu WHERE parent_id = #{menuId}
            """)
    int countByParentId(@Param("menuId") Long menuId);
}
