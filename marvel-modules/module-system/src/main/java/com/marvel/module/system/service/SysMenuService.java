package com.marvel.module.system.service;

import com.baomidou.mybatisplus.spring.service.IService;
import com.marvel.api.system.dto.MenuDTO;
import com.marvel.module.system.entity.SysMenu;

import java.util.List;

/** 菜单管理服务接口。 */
public interface SysMenuService extends IService<SysMenu> {

    List<SysMenu> listMenus(String menuName, String status);

    /**
     * 树形菜单列表（菜单管理页表格使用）。
     * 按名称搜索时保留命中节点的全部祖先链，保证树形结构完整可展开。
     */
    List<SysMenu> listTree(String menuName, String status);

    List<MenuDTO> getMenuTree(Long userId, boolean admin);

    List<SysMenu> listMenuTreeAll();

    List<Long> getChildMenuIds(Long menuId);
}
