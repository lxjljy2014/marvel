package com.marvel.module.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.marvel.api.system.dto.MenuDTO;
import com.marvel.module.system.entity.SysMenu;

import java.util.List;

/** 菜单管理服务接口。 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> listMenus(String menuName, String status);

    /**
     * 树形菜单列表（搜索场景）：按名称搜索时保留命中节点的全部祖先链，
     * 保证树形结构完整可展开。
     */
    List<SysMenu> listTree(String menuName, String status);

    /**
     * 懒加载单层节点：parentId 为空返回根节点，否则返回其直接子节点；
     * 每个节点带 hasChild 标记，供前端决定是否展示展开箭头并按需加载。
     */
    List<SysMenu> listLevel(Long parentId, String status);

    List<MenuDTO> getMenuTree(Long userId, boolean admin);

    List<SysMenu> listMenuTreeAll();

    List<Long> getChildMenuIds(Long menuId);
}
