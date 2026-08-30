-- =============================================================
-- 初始数据：部门 / 用户 / 角色 / 菜单 / 参数 / 字典
-- 默认账号：admin / admin123
-- =============================================================

-- 部门
INSERT INTO sys_dept (dept_id, parent_id, ancestors, dept_name, order_num, status) VALUES
(100, 0, '0',   'Marvel 科技', 0, '0'),
(101, 100, '0,100', '研发部', 1, '0'),
(102, 100, '0,100', '市场部', 2, '0'),
(103, 101, '0,100,101', '后端组', 1, '0'),
(104, 101, '0,100,101', '前端组', 2, '0');

-- 超级管理员（密码 admin123）
INSERT INTO sys_user (user_id, dept_id, username, password, nickname, status, remark) VALUES
(1, 103, 'admin', '$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE8ByOhJIrdAu2', '超级管理员', '0', '内置用户');

-- 角色
INSERT INTO sys_role (role_id, role_name, role_key, role_sort, data_scope, status, remark) VALUES
(1, '超级管理员', 'admin', 1, '1', '0', '拥有全部权限'),
(2, '普通用户', 'common', 2, '4', '0', '默认角色');

INSERT INTO sys_user_role (user_id, role_id) VALUES (1, 1);

-- 菜单：目录 + 页面 + 按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status) VALUES
(1,  0, '系统管理', 'M', 1, 'system',  NULL, NULL, 'mdi-cog-outline', '0', '0'),
(100, 1, '用户管理', 'C', 1, 'user',   'system/user/index',  'system:user:list',  'mdi-account-multiple-outline', '0', '0'),
(101, 1, '角色管理', 'C', 2, 'role',   'system/role/index',  'system:role:list',  'mdi-account-key-outline', '0', '0'),
(102, 1, '菜单管理', 'C', 3, 'menu',   'system/menu/index',  'system:menu:list',  'mdi-menu', '0', '0'),
(103, 1, '部门管理', 'C', 4, 'dept',   'system/dept/index',  'system:dept:list',  'mdi-sitemap', '0', '0'),
(1000, 100, '用户查询', 'F', 1, NULL, NULL, 'system:user:query',   '#', '0', '0'),
(1001, 100, '用户新增', 'F', 2, NULL, NULL, 'system:user:add',     '#', '0', '0'),
(1002, 100, '用户修改', 'F', 3, NULL, NULL, 'system:user:edit',    '#', '0', '0'),
(1003, 100, '用户删除', 'F', 4, NULL, NULL, 'system:user:remove',  '#', '0', '0'),
(1004, 100, '重置密码', 'F', 5, NULL, NULL, 'system:user:resetPwd','#', '0', '0'),
(1010, 101, '角色查询', 'F', 1, NULL, NULL, 'system:role:query',   '#', '0', '0'),
(1011, 101, '角色新增', 'F', 2, NULL, NULL, 'system:role:add',     '#', '0', '0'),
(1012, 101, '角色修改', 'F', 3, NULL, NULL, 'system:role:edit',    '#', '0', '0'),
(1013, 101, '角色删除', 'F', 4, NULL, NULL, 'system:role:remove',  '#', '0', '0'),
(1020, 102, '菜单查询', 'F', 1, NULL, NULL, 'system:menu:query',   '#', '0', '0'),
(1021, 102, '菜单新增', 'F', 2, NULL, NULL, 'system:menu:add',     '#', '0', '0'),
(1022, 102, '菜单修改', 'F', 3, NULL, NULL, 'system:menu:edit',    '#', '0', '0'),
(1023, 102, '菜单删除', 'F', 4, NULL, NULL, 'system:menu:remove',  '#', '0', '0'),
(1030, 103, '部门查询', 'F', 1, NULL, NULL, 'system:dept:query',   '#', '0', '0'),
(1031, 103, '部门新增', 'F', 2, NULL, NULL, 'system:dept:add',     '#', '0', '0'),
(1032, 103, '部门修改', 'F', 3, NULL, NULL, 'system:dept:edit',    '#', '0', '0'),
(1033, 103, '部门删除', 'F', 4, NULL, NULL, 'system:dept:remove',  '#', '0', '0');

-- 普通角色授予系统管理目录与用户管理（示例）
INSERT INTO sys_role_menu (role_id, menu_id) VALUES
(2, 1), (2, 100), (2, 1000), (2, 1001);

-- 系统参数
INSERT INTO sys_config (config_id, config_name, config_key, config_value, remark) VALUES
(1, '系统名称', 'sys.name', 'Marvel 后台管理系统', '系统名称'),
(2, '验证码开关', 'sys.captcha.enabled', 'true', '是否启用登录验证码'),
(3, '默认密码', 'sys.user.initPassword', '123456', '新增用户默认密码');

-- 字典
INSERT INTO sys_dict_type (dict_id, dict_name, dict_type, status, remark) VALUES
(1, '系统状态', 'sys_normal_disable', '0', '通用状态'),
(2, '用户性别', 'sys_user_sex', '0', '用户性别');

INSERT INTO sys_dict_data (dict_code, dict_type, dict_label, dict_value, order_num, status) VALUES
(1, 'sys_normal_disable', '正常', '0', 1, '0'),
(2, 'sys_normal_disable', '停用', '1', 2, '0'),
(3, 'sys_user_sex', '男', '0', 1, '0'),
(4, 'sys_user_sex', '女', '1', 2, '0'),
(5, 'sys_user_sex', '未知', '2', 3, '0');
