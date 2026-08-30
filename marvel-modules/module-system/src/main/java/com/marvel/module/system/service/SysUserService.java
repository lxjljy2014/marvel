package com.marvel.module.system.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.spring.service.IService;
import com.marvel.module.system.entity.SysUser;

import java.util.List;

/** 用户管理服务接口。 */
public interface SysUserService extends IService<SysUser> {

    IPage<SysUser> pageUsers(long pageNum, long pageSize, String username, String nickname, String status, Long deptId);

    void createUser(SysUser user, List<Long> roleIds);

    void updateUser(SysUser user, List<Long> roleIds);

    void deleteUsers(List<Long> userIds);

    void resetPassword(Long userId, String newPassword);

    void updatePassword(Long userId, String oldPassword, String newPassword);

    List<Long> getRoleIdsByUserId(Long userId);
}
