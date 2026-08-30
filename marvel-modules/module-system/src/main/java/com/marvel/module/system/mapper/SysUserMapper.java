package com.marvel.module.system.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.marvel.module.system.entity.SysUser;
import org.apache.ibatis.annotations.Mapper;

@Mapper
/** 用户 Mapper。 */
public interface SysUserMapper extends BaseMapper<SysUser> {
}
