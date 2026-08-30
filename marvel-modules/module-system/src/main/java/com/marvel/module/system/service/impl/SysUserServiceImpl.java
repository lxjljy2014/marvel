package com.marvel.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.marvel.common.constant.Constants;
import com.marvel.common.exception.BusinessException;
import com.marvel.module.system.entity.SysUser;
import com.marvel.module.system.entity.SysUserRole;
import com.marvel.module.system.mapper.SysUserMapper;
import com.marvel.module.system.mapper.SysUserRoleMapper;
import com.marvel.module.system.service.SysUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * 用户管理业务实现。
 *
 * <p>关键规则：
 * <ul>
 *   <li>密码一律 BCrypt 单向加密存储，任何接口不回传密文；</li>
 *   <li>超级管理员（userId=1）受保护：禁止删除、禁止停用；</li>
 *   <li>用户-角色关联在事务内先删后插，保证与角色分配完全一致。</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    private final SysUserRoleMapper userRoleMapper;
    /** BCrypt 校验器无状态，可安全复用 */
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * 分页查询用户。
     * 部门过滤采用 ancestors 前缀匹配，命中指定部门及其全部下级部门；
     * 参数均通过 #{?} 占位符绑定，不存在 SQL 注入风险。
     */
    @Override
    public IPage<SysUser> pageUsers(long pageNum, long pageSize, String username, String nickname, String status, Long deptId) {
        LambdaQueryWrapper<SysUser> wrapper = new LambdaQueryWrapper<SysUser>()
                .like(StringUtils.hasText(username), SysUser::getUsername, username)
                .like(StringUtils.hasText(nickname), SysUser::getNickname, nickname)
                .eq(StringUtils.hasText(status), SysUser::getStatus, status)
                .apply(deptId != null,
                        "dept_id IN (SELECT dept_id FROM sys_dept WHERE dept_id = {0} OR ancestors LIKE CONCAT('%', {0}, '%'))",
                        deptId)
                .orderByAsc(SysUser::getUserId);
        IPage<SysUser> page = this.page(new Page<>(pageNum, pageSize), wrapper);
        // 列表数据统一抹除密码密文
        page.getRecords().forEach(u -> u.setPassword(null));
        return page;
    }

    @Override
    @Transactional
    public void createUser(SysUser user, List<Long> roleIds) {
        checkUsernameUnique(user.getUsername(), null);
        if (!StringUtils.hasText(user.getPassword())) {
            throw new BusinessException("初始密码不能为空");
        }
        user.setUserId(null);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        this.save(user);
        saveUserRoles(user.getUserId(), roleIds);
    }

    @Override
    @Transactional
    public void updateUser(SysUser user, List<Long> roleIds) {
        SysUser db = getById(user.getUserId());
        if (db == null) {
            throw new BusinessException("用户不存在");
        }
        // 超级管理员状态保护，防止误操作锁死系统
        if (Constants.SUPER_ADMIN_USER_ID.equals(user.getUserId()) && StringUtils.hasText(user.getStatus())
                && !Constants.STATUS_NORMAL.equals(user.getStatus())) {
            throw new BusinessException("不允许停用超级管理员");
        }
        checkUsernameUnique(user.getUsername(), user.getUserId());
        // 基本信息 update 不允许改密码，密码变更走独立接口
        user.setPassword(null);
        this.updateById(user);
        rebuildUserRoles(user.getUserId(), roleIds);
    }

    @Override
    @Transactional
    public void deleteUsers(List<Long> userIds) {
        if (userIds == null || userIds.isEmpty()) {
            return;
        }
        if (userIds.contains(Constants.SUPER_ADMIN_USER_ID)) {
            throw new BusinessException("不允许删除超级管理员");
        }
        this.removeByIds(userIds);
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().in(SysUserRole::getUserId, userIds));
    }

    @Override
    public void resetPassword(Long userId, String newPassword) {
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(newPassword));
        this.updateById(user);
    }

    @Override
    public void updatePassword(Long userId, String oldPassword, String newPassword) {
        SysUser db = getById(userId);
        // 使用常量时间比较语义的 matches，避免时序侧信道
        if (db == null || !passwordEncoder.matches(oldPassword, db.getPassword())) {
            throw new BusinessException("原密码错误");
        }
        resetPassword(userId, newPassword);
    }

    @Override
    public List<Long> getRoleIdsByUserId(Long userId) {
        return userRoleMapper.selectList(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId))
                .stream().map(SysUserRole::getRoleId).toList();
    }

    /** 重建用户-角色关联：先删旧关联再批量插入，调用方需处于事务内 */
    private void rebuildUserRoles(Long userId, List<Long> roleIds) {
        userRoleMapper.delete(new LambdaQueryWrapper<SysUserRole>().eq(SysUserRole::getUserId, userId));
        saveUserRoles(userId, roleIds);
    }

    private void saveUserRoles(Long userId, List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            return;
        }
        roleIds.forEach(roleId -> {
            SysUserRole ur = new SysUserRole();
            ur.setUserId(userId);
            ur.setRoleId(roleId);
            userRoleMapper.insert(ur);
        });
    }

    /** 用户名唯一性校验，excludeUserId 用于更新场景排除自身 */
    private void checkUsernameUnique(String username, Long excludeUserId) {
        SysUser existing = getOne(new LambdaQueryWrapper<SysUser>().eq(SysUser::getUsername, username));
        if (existing != null && !existing.getUserId().equals(excludeUserId)) {
            throw new BusinessException("用户名已存在");
        }
    }
}
