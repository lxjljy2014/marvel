package com.marvel.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.marvel.module.system.entity.SysRoleMenu;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/** 角色-菜单关联 Mapper。 */
public interface SysRoleMenuMapper extends BaseMapper<SysRoleMenu> {
}
