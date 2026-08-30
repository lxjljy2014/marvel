package com.marvel.module.system.dto;

/**
 * 角色下拉选项 VO：仅暴露展示所需字段，避免泄露 dataScope 等管理字段。
 */
public record SysRoleVO(Long roleId, String roleName) {
}
