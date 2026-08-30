package com.marvel.api.system.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 菜单树节点，用于前端动态路由与树形展示。
 */
@Data
public class MenuDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;
    private Long parentId;
    private String menuName;
    /** M=目录 C=菜单 F=按钮 */
    private String menuType;
    private String path;
    private String component;
    private String perms;
    private String icon;
    private Integer orderNum;
    private String visible;
    private String status;
    private List<MenuDTO> children;
}
