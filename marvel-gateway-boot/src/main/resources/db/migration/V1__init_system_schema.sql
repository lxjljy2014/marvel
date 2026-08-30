-- =============================================================
-- Marvel 系统管理域表结构（system 域）
-- =============================================================

CREATE TABLE sys_dept (
    dept_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '部门ID',
    parent_id   BIGINT       DEFAULT 0               COMMENT '父部门ID',
    ancestors   VARCHAR(255) DEFAULT ''              COMMENT '祖级列表',
    dept_name   VARCHAR(50)  NOT NULL                COMMENT '部门名称',
    order_num   INT          DEFAULT 0               COMMENT '显示顺序',
    leader      VARCHAR(50)  DEFAULT NULL            COMMENT '负责人',
    phone       VARCHAR(20)  DEFAULT NULL            COMMENT '联系电话',
    email       VARCHAR(50)  DEFAULT NULL            COMMENT '邮箱',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (dept_id)
) ENGINE=InnoDB COMMENT='部门表';

CREATE TABLE sys_user (
    user_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    dept_id     BIGINT       DEFAULT NULL            COMMENT '部门ID',
    username    VARCHAR(30)  NOT NULL                COMMENT '用户名',
    password    VARCHAR(100) DEFAULT ''              COMMENT '密码（BCrypt）',
    nickname    VARCHAR(50)  DEFAULT ''              COMMENT '昵称',
    email       VARCHAR(50)  DEFAULT ''              COMMENT '邮箱',
    phone       VARCHAR(20)  DEFAULT ''              COMMENT '手机号',
    sex         CHAR(1)      DEFAULT '0'             COMMENT '性别（0男 1女 2未知）',
    avatar      VARCHAR(255) DEFAULT ''              COMMENT '头像',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    remark      VARCHAR(255) DEFAULT NULL            COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id),
    UNIQUE KEY uk_username (username)
) ENGINE=InnoDB COMMENT='用户表';

CREATE TABLE sys_role (
    role_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '角色ID',
    role_name   VARCHAR(50)  NOT NULL                COMMENT '角色名称',
    role_key    VARCHAR(50)  NOT NULL                COMMENT '角色权限字符',
    role_sort   INT          DEFAULT 0               COMMENT '显示顺序',
    data_scope  CHAR(1)      DEFAULT '1'             COMMENT '数据范围（1全部 2自定义 3本部门 4本部门及以下）',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    remark      VARCHAR(255) DEFAULT NULL            COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (role_id),
    UNIQUE KEY uk_role_key (role_key)
) ENGINE=InnoDB COMMENT='角色表';

CREATE TABLE sys_menu (
    menu_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '菜单ID',
    parent_id   BIGINT       DEFAULT 0               COMMENT '父菜单ID',
    menu_name   VARCHAR(50)  NOT NULL                COMMENT '菜单名称',
    menu_type   CHAR(1)      DEFAULT ''              COMMENT '类型（M目录 C菜单 F按钮）',
    order_num   INT          DEFAULT 0               COMMENT '显示顺序',
    path        VARCHAR(200) DEFAULT ''              COMMENT '路由地址',
    component   VARCHAR(255) DEFAULT NULL            COMMENT '组件路径',
    perms       VARCHAR(100) DEFAULT NULL            COMMENT '权限标识',
    icon        VARCHAR(100) DEFAULT '#'             COMMENT '图标',
    visible     CHAR(1)      DEFAULT '0'             COMMENT '显示（0显示 1隐藏）',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (menu_id)
) ENGINE=InnoDB COMMENT='菜单权限表';

CREATE TABLE sys_user_role (
    user_id BIGINT NOT NULL COMMENT '用户ID',
    role_id BIGINT NOT NULL COMMENT '角色ID',
    PRIMARY KEY (user_id, role_id)
) ENGINE=InnoDB COMMENT='用户角色关联表';

CREATE TABLE sys_role_menu (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    menu_id BIGINT NOT NULL COMMENT '菜单ID',
    PRIMARY KEY (role_id, menu_id)
) ENGINE=InnoDB COMMENT='角色菜单关联表';

CREATE TABLE sys_role_dept (
    role_id BIGINT NOT NULL COMMENT '角色ID',
    dept_id BIGINT NOT NULL COMMENT '部门ID',
    PRIMARY KEY (role_id, dept_id)
) ENGINE=InnoDB COMMENT='角色部门关联表（数据权限自定义范围）';

CREATE TABLE sys_dict_type (
    dict_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典主键',
    dict_name   VARCHAR(100) DEFAULT ''              COMMENT '字典名称',
    dict_type   VARCHAR(100) DEFAULT ''              COMMENT '字典类型',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    remark      VARCHAR(255) DEFAULT NULL            COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (dict_id),
    UNIQUE KEY uk_dict_type (dict_type)
) ENGINE=InnoDB COMMENT='字典类型表';

CREATE TABLE sys_dict_data (
    dict_code   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '字典编码',
    dict_type   VARCHAR(100) DEFAULT ''              COMMENT '字典类型',
    dict_label  VARCHAR(100) DEFAULT ''              COMMENT '字典标签',
    dict_value  VARCHAR(100) DEFAULT ''              COMMENT '字典键值',
    order_num   INT          DEFAULT 0               COMMENT '显示顺序',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1停用）',
    remark      VARCHAR(255) DEFAULT NULL            COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (dict_code)
) ENGINE=InnoDB COMMENT='字典数据表';

CREATE TABLE sys_config (
    config_id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '参数主键',
    config_name VARCHAR(100) DEFAULT ''              COMMENT '参数名称',
    config_key  VARCHAR(100) DEFAULT ''              COMMENT '参数键名',
    config_value VARCHAR(500) DEFAULT ''             COMMENT '参数键值',
    remark      VARCHAR(255) DEFAULT NULL            COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (config_id),
    UNIQUE KEY uk_config_key (config_key)
) ENGINE=InnoDB COMMENT='系统参数配置表';

CREATE TABLE sys_notice (
    notice_id   BIGINT       NOT NULL AUTO_INCREMENT COMMENT '公告ID',
    title       VARCHAR(100) NOT NULL                COMMENT '公告标题',
    content     TEXT                                 COMMENT '公告内容',
    type        CHAR(1)      DEFAULT '0'             COMMENT '类型（1通知 2公告）',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1关闭）',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (notice_id)
) ENGINE=InnoDB COMMENT='通知公告表';

CREATE TABLE sys_oper_log (
    oper_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '日志主键',
    title       VARCHAR(50)  DEFAULT ''              COMMENT '模块标题',
    method      VARCHAR(200) DEFAULT ''              COMMENT '方法名称',
    request_method VARCHAR(10) DEFAULT ''             COMMENT '请求方式',
    oper_url    VARCHAR(255) DEFAULT ''              COMMENT '请求URL',
    oper_param  TEXT                                 COMMENT '请求参数',
    oper_user   VARCHAR(50)  DEFAULT ''              COMMENT '操作人员',
    oper_ip     VARCHAR(50)  DEFAULT ''              COMMENT '主机地址',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1失败）',
    error_msg   TEXT                                 COMMENT '错误消息',
    oper_time   DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
    PRIMARY KEY (oper_id),
    KEY idx_oper_time (oper_time)
) ENGINE=InnoDB COMMENT='操作日志表';

CREATE TABLE sys_logininfor (
    info_id     BIGINT       NOT NULL AUTO_INCREMENT COMMENT '访问ID',
    username    VARCHAR(50)  DEFAULT ''              COMMENT '用户账号',
    ipaddr      VARCHAR(50)  DEFAULT ''              COMMENT '登录IP',
    browser     VARCHAR(50)  DEFAULT ''              COMMENT '浏览器',
    os          VARCHAR(50)  DEFAULT ''              COMMENT '操作系统',
    msg         VARCHAR(255) DEFAULT ''              COMMENT '提示消息',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0成功 1失败）',
    login_time  DATETIME     DEFAULT CURRENT_TIMESTAMP COMMENT '登录时间',
    PRIMARY KEY (info_id),
    KEY idx_login_time (login_time)
) ENGINE=InnoDB COMMENT='登录日志表';

-- =============================================================
-- infra 域表结构
-- =============================================================

CREATE TABLE sys_file (
    file_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '文件ID',
    file_name    VARCHAR(255) NOT NULL                COMMENT '原始文件名',
    file_path    VARCHAR(500) NOT NULL                COMMENT '存储路径/URL',
    file_size    BIGINT       DEFAULT 0               COMMENT '文件大小（字节）',
    content_type VARCHAR(100) DEFAULT ''              COMMENT '文件类型',
    create_time  DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (file_id)
) ENGINE=InnoDB COMMENT='文件记录表';

CREATE TABLE sys_job (
    job_id      BIGINT       NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    job_name    VARCHAR(64)  NOT NULL                COMMENT '任务名称',
    job_group   VARCHAR(64)  DEFAULT 'DEFAULT'       COMMENT '任务组名',
    invoke_target VARCHAR(255) NOT NULL              COMMENT '调用目标（beanName.method）',
    cron_expression VARCHAR(255) DEFAULT ''          COMMENT 'cron表达式',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0正常 1暂停）',
    remark      VARCHAR(255) DEFAULT NULL            COMMENT '备注',
    create_time DATETIME     DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME     DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (job_id)
) ENGINE=InnoDB COMMENT='定时任务调度表';

CREATE TABLE sys_job_log (
    job_log_id  BIGINT       NOT NULL AUTO_INCREMENT COMMENT '任务日志ID',
    job_id      BIGINT       NOT NULL                COMMENT '任务ID',
    job_name    VARCHAR(64)                          COMMENT '任务名称',
    status      CHAR(1)      DEFAULT '0'             COMMENT '状态（0成功 1失败）',
    error_msg   TEXT                                 COMMENT '错误信息',
    start_time  DATETIME                             COMMENT '开始时间',
    end_time    DATETIME                             COMMENT '结束时间',
    PRIMARY KEY (job_log_id)
) ENGINE=InnoDB COMMENT='定时任务日志表';
