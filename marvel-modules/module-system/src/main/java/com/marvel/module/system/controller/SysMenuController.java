package com.marvel.module.system.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.marvel.common.result.R;
import com.marvel.module.system.entity.SysMenu;
import com.marvel.module.system.service.SysMenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单管理接口，路径前缀 /system/**（与未来网关路由一致）。
 * 菜单变更仅影响后续登录会话的路由与权限（Sa-Token 注解实时查库，无需重启）。
 */
@RestController
@RequestMapping("/system/menu")
@RequiredArgsConstructor
public class SysMenuController {

    private final SysMenuService menuService;

    /** 全量菜单列表（平铺，角色菜单树等需要自行组树的场景使用） */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/list")
    public R<List<SysMenu>> list(@RequestParam(required = false) String menuName,
                                 @RequestParam(required = false) String status) {
        return R.ok(menuService.listMenus(menuName, status));
    }

    /**
     * 树形菜单列表（菜单管理页表格使用）。
     * 按名称搜索时保留命中节点的祖先链，树形结构完整可展开。
     */
    @SaCheckPermission("system:menu:list")
    @GetMapping("/tree")
    public R<List<SysMenu>> tree(@RequestParam(required = false) String menuName,
                                 @RequestParam(required = false) String status) {
        return R.ok(menuService.listTree(menuName, status));
    }

    @SaCheckPermission("system:menu:query")
    @GetMapping("/{menuId}")
    public R<SysMenu> detail(@PathVariable Long menuId) {
        return R.ok(menuService.getById(menuId));
    }

    @SaCheckPermission("system:menu:add")
    @PostMapping
    public R<Void> add(@RequestBody SysMenu menu) {
        menu.setMenuId(null);
        menuService.save(menu);
        return R.ok();
    }

    @SaCheckPermission("system:menu:edit")
    @PutMapping
    public R<Void> update(@RequestBody SysMenu menu) {
        menuService.updateById(menu);
        return R.ok();
    }

    @SaCheckPermission("system:menu:remove")
    @DeleteMapping("/{menuId}")
    public R<Void> remove(@PathVariable Long menuId) {
        List<Long> childIds = menuService.getChildMenuIds(menuId);
        if (!childIds.isEmpty()) {
            return R.fail("存在子菜单，不允许删除");
        }
        menuService.removeById(menuId);
        return R.ok();
    }
}
