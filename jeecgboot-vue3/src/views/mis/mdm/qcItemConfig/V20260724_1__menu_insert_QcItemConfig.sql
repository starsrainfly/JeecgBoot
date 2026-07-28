-- 注意：该页面对应的前台目录为views/mdm文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026072404493060110', NULL, '质检项目配置', '/mdm/qcItemConfigList', 'mdm/QcItemConfigList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-07-24 16:49:11', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072404493070111', '2026072404493060110', '添加质检项目配置', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_qc_item_config:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 16:49:11', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072404493070112', '2026072404493060110', '编辑质检项目配置', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_qc_item_config:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 16:49:11', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072404493070113', '2026072404493060110', '删除质检项目配置', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_qc_item_config:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 16:49:11', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072404493070114', '2026072404493060110', '批量删除质检项目配置', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_qc_item_config:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 16:49:11', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072404493070115', '2026072404493060110', '导出excel_质检项目配置', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_qc_item_config:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 16:49:11', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072404493070116', '2026072404493060110', '导入excel_质检项目配置', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_qc_item_config:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 16:49:11', NULL, NULL, 0, 0, '1', 0);