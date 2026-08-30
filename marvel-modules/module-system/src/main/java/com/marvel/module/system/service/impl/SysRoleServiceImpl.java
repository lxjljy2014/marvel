package com.marvel.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.marvel.common.constant.Constants;
import com.marvel.common.exception.BusinessException;
import com.marvel.module.system.entity.SysRole;
import com.marvel.module.system.entity.SysRoleMenu;
import com.marvel.module.system.mapper.SysMenuMapper;
import com.marvel.module.system.mapper.SysRoleMapper;
import com.marvel.module.system.mapper.SysRoleMenuMapper;
import com.marvel.module.system.service.SysRoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色管理业务实现。
 *
 * <p>关键规则：
 * <ul>
 *   <li>超级管理员角色（roleKey=admin）受保护：禁止修改与删除；</li>
 *   <li>权限判定：admin 角色直接持有 {@code *:*:*} 全量权限，普通角色按角色-菜单关联取按钮权限；</li>
 *   <li>角色-菜单关联在事务内先删后插，保证菜单分配原子生效。</li>
 * </ul>
 */
@Service
@RequiredArgsConstructor
public class SysRoleServiceImpl extends ServiceImpl<SysRoleMapper, SysRole> implements SysRoleService {

    private final SysRoleMenuMapper roleMenuMapper;
    private final SysMenuMapper menuMapper;

    @Override
    public IPage<SysRole> pageRoles(long pageNum, long pageSize, String roleName, String roleKey, String status) {
        LambdaQueryWrapper<SysRole> wrapper = new LambdaQueryWrapper<SysRole>()
                .like(StringUtils.hasText(roleName), SysRole::getRoleName, roleName)
                .like(StringUtils.hasText(roleKey), SysRole::getRoleKey, roleKey)
                .eq(StringUtils.hasText(status), SysRole::getStatus, status)
                .orderByAsc(SysRole::getRoleSort);
        return this.page(new Page<>(pageNum, pageSize), wrapper);
    }

    @Override
    public List<SysRole> listRolesByUserId(Long userId) {
        return baseMapper.selectRolesByUserId(userId);
    }

    @Override
    @Transactional
    public void createRole(SysRole role) {
        checkRoleKeyUnique(role.getRoleKey(), null);
        role.setRoleId(null);
        this.save(role);
        saveRoleMenus(role.getRoleId(), role.getMenuIds());
    }

    @Override
    @Transactional
    public void updateRole(SysRole role) {
        if (Constants.SUPER_ADMIN_ROLE.equals(getById(role.getRoleId()).getRoleKey())) {
            throw new BusinessException("不允许修改超级管理员角色");
        }
        checkRoleKeyUnique(role.getRoleKey(), role.getRoleId());
        this.updateById(role);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, role.getRoleId()));
        saveRoleMenus(role.getRoleId(), role.getMenuIds());
    }

    @Override
    @Transactional
    public void deleteRoles(List<Long> roleIds) {
        for (Long roleId : roleIds) {
            SysRole role = getById(roleId);
            if (role != null && Constants.SUPER_ADMIN_ROLE.equals(role.getRoleKey())) {
                throw new BusinessException("不允许删除超级管理员角色");
            }
        }
        this.removeByIds(roleIds);
        roleMenuMapper.delete(new LambdaQueryWrapper<SysRoleMenu>().in(SysRoleMenu::getRoleId, roleIds));
    }

    @Override
    public Set<String> getRoleKeysByUserId(Long userId) {
        return listRolesByUserId(userId).stream().map(SysRole::getRoleKey).collect(java.util.stream.Collectors.toSet());
    }

    /**
     * 查询用户权限标识集合（Sa-Token 数据源）。
     * 超级管理员返回通配权限 {@code *:*:*}，由注解校验时匹配任意权限串。
     */
    @Override
    public Set<String> getPermissionsByUserId(Long userId) {
        Set<String> roleKeys = getRoleKeysByUserId(userId);
        if (roleKeys.contains(Constants.SUPER_ADMIN_ROLE)) {
            return Set.of("*:*:*");
        }
        return new HashSet<>(menuMapper.selectPermsByUserId(userId));
    }

    @Override
    public List<Long> getMenuIdsByRoleId(Long roleId) {
        return roleMenuMapper.selectList(new LambdaQueryWrapper<SysRoleMenu>().eq(SysRoleMenu::getRoleId, roleId))
                .stream().map(SysRoleMenu::getMenuId).toList();
    }

    private void saveRoleMenus(Long roleId, List<Long> menuIds) {
        if (menuIds == null) {
            return;
        }
        menuIds.forEach(menuId -> {
            SysRoleMenu rm = new SysRoleMenu();
            rm.setRoleId(roleId);
            rm.setMenuId(menuId);
            roleMenuMapper.insert(rm);
        });
    }

    private void checkRoleKeyUnique(String roleKey, Long excludeRoleId) {
        SysRole existing = getOne(new LambdaQueryWrapper<SysRole>().eq(SysRole::getRoleKey, roleKey));
        if (existing != null && !existing.getRoleId().equals(excludeRoleId)) {
            throw new BusinessException("角色权限字符已存在");
        }
    }
}
