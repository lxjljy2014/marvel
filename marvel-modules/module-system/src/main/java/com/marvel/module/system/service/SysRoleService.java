package com.marvel.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.marvel.module.system.entity.SysRole;

import java.util.List;
import java.util.Set;

/** 角色管理服务接口。 */
public interface SysRoleService extends IService<SysRole> {

    IPage<SysRole> pageRoles(long pageNum, long pageSize, String roleName, String roleKey, String status);

    List<SysRole> listRolesByUserId(Long userId);

    void createRole(SysRole role);

    void updateRole(SysRole role);

    void deleteRoles(List<Long> roleIds);

    Set<String> getRoleKeysByUserId(Long userId);

    Set<String> getPermissionsByUserId(Long userId);

    List<Long> getMenuIdsByRoleId(Long roleId);
}
