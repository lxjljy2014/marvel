-- =============================================================
-- V3：二期菜单与按钮权限（字典/参数/公告/定时任务）
-- =============================================================

-- 基础设施目录
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status) VALUES
(2, 0, '基础设施', 'M', 2, 'infra', NULL, NULL, 'mdi-server-outline', '0', '0'),
(210, 2, '定时任务', 'C', 1, 'job', 'infra/job/index', 'infra:job:list', 'mdi-clock-time-eight-outline', '0', '0');

-- 系统管理下的二期菜单
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status) VALUES
(200, 1, '字典管理', 'C', 5, 'dict', 'system/dict/index', 'system:dict:list', 'mdi-book-open-variant', '0', '0'),
(201, 1, '参数配置', 'C', 6, 'config', 'system/config/index', 'system:config:list', 'mdi-tune-variant', '0', '0'),
(202, 1, '通知公告', 'C', 7, 'notice', 'system/notice/index', 'system:notice:list', 'mdi-bullhorn-variant-outline', '0', '0');

-- 定时任务按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status) VALUES
(2110, 210, '任务查询', 'F', 1, NULL, NULL, 'infra:job:query',   '#', '0', '0'),
(2111, 210, '任务新增', 'F', 2, NULL, NULL, 'infra:job:add',     '#', '0', '0'),
(2112, 210, '任务修改', 'F', 3, NULL, NULL, 'infra:job:edit',    '#', '0', '0'),
(2113, 210, '任务删除', 'F', 4, NULL, NULL, 'infra:job:remove',  '#', '0', '0'),
(2114, 210, '立即执行', 'F', 5, NULL, NULL, 'infra:job:run',     '#', '0', '0');

-- 字典管理按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status) VALUES
(2000, 200, '字典查询', 'F', 1, NULL, NULL, 'system:dict:query',  '#', '0', '0'),
(2001, 200, '字典新增', 'F', 2, NULL, NULL, 'system:dict:add',    '#', '0', '0'),
(2002, 200, '字典修改', 'F', 3, NULL, NULL, 'system:dict:edit',   '#', '0', '0'),
(2003, 200, '字典删除', 'F', 4, NULL, NULL, 'system:dict:remove', '#', '0', '0');

-- 参数配置按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status) VALUES
(2010, 201, '参数查询', 'F', 1, NULL, NULL, 'system:config:query',  '#', '0', '0'),
(2011, 201, '参数新增', 'F', 2, NULL, NULL, 'system:config:add',    '#', '0', '0'),
(2012, 201, '参数修改', 'F', 3, NULL, NULL, 'system:config:edit',   '#', '0', '0'),
(2013, 201, '参数删除', 'F', 4, NULL, NULL, 'system:config:remove', '#', '0', '0');

-- 通知公告按钮
INSERT INTO sys_menu (menu_id, parent_id, menu_name, menu_type, order_num, path, component, perms, icon, visible, status) VALUES
(2020, 202, '公告查询', 'F', 1, NULL, NULL, 'system:notice:query',  '#', '0', '0'),
(2021, 202, '公告新增', 'F', 2, NULL, NULL, 'system:notice:add',    '#', '0', '0'),
(2022, 202, '公告修改', 'F', 3, NULL, NULL, 'system:notice:edit',   '#', '0', '0'),
(2023, 202, '公告删除', 'F', 4, NULL, NULL, 'system:notice:remove', '#', '0', '0');

-- 示例任务（默认暂停，供演示调度链路）
INSERT INTO sys_job (job_id, job_name, job_group, invoke_target, cron_expression, status, remark) VALUES
(1, '演示任务', 'DEFAULT', 'sampleJob.run', '0 * * * * ?', '1', '每分钟触发一次的演示任务，默认暂停');
