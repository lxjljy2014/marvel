package com.marvel.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.marvel.module.system.entity.SysUserRole;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/** 用户-角色关联 Mapper。 */
public interface SysUserRoleMapper extends BaseMapper<SysUserRole> {
}
