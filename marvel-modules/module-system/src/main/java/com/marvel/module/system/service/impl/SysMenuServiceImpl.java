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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
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
     * 树形菜单：按 parentId 组装 children。
     * 搜索时只保留命中节点与其祖先链，避免父节点被过滤后子节点悬空丢失。
     */
    @Override
    public List<SysMenu> listTree(String menuName, String status) {
        List<SysMenu> all = list(new LambdaQueryWrapper<SysMenu>()
                .eq(StringUtils.hasText(status), SysMenu::getStatus, status)
                .orderByAsc(SysMenu::getOrderNum));
        List<SysMenu> kept = all;
        if (StringUtils.hasText(menuName)) {
            Map<Long, SysMenu> byId = all.stream()
                    .collect(Collectors.toMap(SysMenu::getMenuId, m -> m));
            Set<Long> keepIds = new HashSet<>();
            for (SysMenu m : all) {
                if (m.getMenuName().contains(menuName)) {
                    SysMenu cur = m;
                    // 沿 parentId 向上收齐祖先链
                    while (cur != null && keepIds.add(cur.getMenuId())) {
                        cur = byId.get(cur.getParentId());
                    }
                }
            }
            kept = all.stream().filter(m -> keepIds.contains(m.getMenuId())).toList();
        }
        return buildMenuTree(kept);
    }

    /**
     * 懒加载单层节点：parentId 为空视为根（0）。
     * hasChild 通过一次 IN 查询批量标记，避免逐行 N+1。
     */
    @Override
    public List<SysMenu> listLevel(Long parentId, String status) {
        List<SysMenu> nodes = list(new LambdaQueryWrapper<SysMenu>()
                .eq(SysMenu::getParentId, parentId == null ? 0L : parentId)
                .eq(StringUtils.hasText(status), SysMenu::getStatus, status)
                .orderByAsc(SysMenu::getOrderNum));
        fillHasChild(nodes);
        return nodes;
    }

    /** 批量标记 hasChild：一次查出这些节点的子节点 parent_id 集合 */
    private void fillHasChild(List<SysMenu> nodes) {
        if (nodes.isEmpty()) {
            return;
        }
        List<Long> ids = nodes.stream().map(SysMenu::getMenuId).toList();
        Set<Long> parentsWithChild = list(new LambdaQueryWrapper<SysMenu>()
                        .select(SysMenu::getParentId)
                        .in(SysMenu::getParentId, ids))
                .stream().map(SysMenu::getParentId).collect(Collectors.toSet());
        nodes.forEach(n -> n.setHasChild(parentsWithChild.contains(n.getMenuId())));
    }

    /** 组装实体树：父节点不在集合内的节点视为根，防止脏数据丢失 */
    private List<SysMenu> buildMenuTree(List<SysMenu> menus) {
        Set<Long> ids = menus.stream().map(SysMenu::getMenuId).collect(Collectors.toSet());
        Map<Long, List<SysMenu>> byParent = menus.stream()
                .filter(m -> m.getParentId() != null && ids.contains(m.getParentId()))
                .collect(Collectors.groupingBy(SysMenu::getParentId));
        List<SysMenu> roots = menus.stream()
                .filter(m -> m.getParentId() == null || !ids.contains(m.getParentId()))
                .toList();
        roots.forEach(root -> attachChildren(root, byParent));
        return roots;
    }

    private void attachChildren(SysMenu node, Map<Long, List<SysMenu>> byParent) {
        List<SysMenu> children = byParent.get(node.getMenuId());
        node.setChildren(children);
        if (children != null) {
            children.forEach(child -> attachChildren(child, byParent));
        }
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
