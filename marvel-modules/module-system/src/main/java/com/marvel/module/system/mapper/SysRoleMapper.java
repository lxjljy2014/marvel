package com.marvel.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.marvel.module.system.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
/** 角色 Mapper：提供按用户/菜单聚合查询角色的 SQL。 */
public interface SysRoleMapper extends BaseMapper<SysRole> {

    @Select("""
            SELECT r.* FROM sys_role r
            JOIN sys_user_role ur ON ur.role_id = r.role_id
            WHERE ur.user_id = #{userId}
            ORDER BY r.role_sort
            """)
    List<SysRole> selectRolesByUserId(@Param("userId") Long userId);

    @Select("""
            SELECT r.* FROM sys_role r
            JOIN sys_role_menu rm ON rm.role_id = r.role_id
            WHERE rm.menu_id = #{menuId}
            """)
    List<SysRole> selectRolesByMenuId(@Param("menuId") Long menuId);
}
