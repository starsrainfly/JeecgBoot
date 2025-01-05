-- 注意：该页面对应的前台目录为views/material文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2024111410109380400', NULL, '物料表', '/material/materialList', 'material/MaterialList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2024-11-14 22:10:40', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111410109390401', '2024111410109380400', '添加物料表', NULL, NULL, 0, NULL, NULL, 2, 'material:mis_material:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-14 22:10:40', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111410109390402', '2024111410109380400', '编辑物料表', NULL, NULL, 0, NULL, NULL, 2, 'material:mis_material:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-14 22:10:40', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111410109390403', '2024111410109380400', '删除物料表', NULL, NULL, 0, NULL, NULL, 2, 'material:mis_material:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-14 22:10:40', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111410109390404', '2024111410109380400', '批量删除物料表', NULL, NULL, 0, NULL, NULL, 2, 'material:mis_material:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-14 22:10:40', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111410109390405', '2024111410109380400', '导出excel_物料表', NULL, NULL, 0, NULL, NULL, 2, 'material:mis_material:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-14 22:10:40', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111410109390406', '2024111410109380400', '导入excel_物料表', NULL, NULL, 0, NULL, NULL, 2, 'material:mis_material:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-14 22:10:40', NULL, NULL, 0, 0, '1', 0);