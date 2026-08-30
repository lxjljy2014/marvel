package com.marvel.module.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.spring.service.impl.ServiceImpl;
import com.marvel.api.system.dto.MenuDTO;
import com.marvel.common.constant.Constants;
import com.marvel.common.exception.BusinessException;
import com.marvel.module.system.entity.SysMenu;
import com.marvel.module.system.mapper.SysMenuMapper;
import com.marvel.module.system.service.SysMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 菜单管理业务实现，负责菜单树构建与用户可见路由计算。
 */
@Service
public class SysMenuServiceImpl extends ServiceImpl<SysMenuMapper, SysMenu> implements SysMenuService {

    @Override
    public List<SysMenu> listMenus(String menuName, String status) {
        return list(new LambdaQueryWrapper<SysMenu>()
                .like(StringUtils.hasText(menuName), SysMenu::getMenuName, menuName)
                .eq(StringUtils.hasText(status), SysMenu::getStatus, status)
                .orderByAsc(SysMenu::getOrderNum));
    }

    /**
     * 构建用户可见菜单树（仅 M/C 类型，按钮不下发）。
     *
     * @param userId 用户 ID
     * @param admin  是否超级管理员：是则返回全部启用菜单，否则按角色关联过滤
     */
    @Override
    public List<MenuDTO> getMenuTree(Long userId, boolean admin) {
        List<SysMenu> menus = admin
                ? list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getStatus, Constants.STATUS_NORMAL)
                        .orderByAsc(SysMenu::getOrderNum))
                : baseMapper.selectMenusByUserId(userId);
        List<SysMenu> filtered = menus.stream()
                .filter(m -> !Constants.MENU_TYPE_BUTTON.equals(m.getMenuType()))
                .toList();
        return buildTree(filtered, 0L);
    }

    @Override
    public List<SysMenu> listMenuTreeAll() {
        return list(new LambdaQueryWrapper<SysMenu>().orderByAsc(SysMenu::getOrderNum));
    }

    @Override
    public List<Long> getChildMenuIds(Long menuId) {
        List<Long> ids = new ArrayList<>();
        collectChildren(menuId, ids);
        return ids;
    }

    private void collectChildren(Long parentId, List<Long> ids) {
        list(new LambdaQueryWrapper<SysMenu>().eq(SysMenu::getParentId, parentId))
                .forEach(child -> {
                    ids.add(child.getMenuId());
                    collectChildren(child.getMenuId(), ids);
                });
    }

    /** 递归组装菜单树；叶子节点 children 置 null，便于前端区分展开态 */
    private List<MenuDTO> buildTree(List<SysMenu> menus, Long parentId) {
        List<MenuDTO> tree = new ArrayList<>();
        menus.stream()
                .filter(m -> parentId.equals(m.getParentId()))
                .sorted(Comparator.comparing(SysMenu::getOrderNum, Comparator.nullsLast(Comparator.naturalOrder())))
                .forEach(m -> {
                    MenuDTO dto = new MenuDTO();
                    BeanUtils.copyProperties(m, dto);
                    dto.setId(m.getMenuId());
                    dto.setParentId(m.getParentId());
                    dto.setMenuName(m.getMenuName());
                    dto.setChildren(buildTree(menus, m.getMenuId()));
                    tree.add(dto);
                });
        if (tree.isEmpty()) {
            return null;
        }
        return tree;
    }

    public void checkMenuInUse(Long menuId) {
        if (baseMapper.countByParentId(menuId) > 0) {
            throw new BusinessException("存在子菜单，不允许删除");
        }
    }
}
