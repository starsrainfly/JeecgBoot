-- 注意：该页面对应的前台目录为views/unit文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2024111607432000290', NULL, '计量单位', '/unit/unitList', 'unit/UnitList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2024-11-16 19:43:29', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111607432010291', '2024111607432000290', '添加计量单位', NULL, NULL, 0, NULL, NULL, 2, 'unit:mis_unit:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 19:43:29', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111607432010292', '2024111607432000290', '编辑计量单位', NULL, NULL, 0, NULL, NULL, 2, 'unit:mis_unit:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 19:43:29', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111607432010293', '2024111607432000290', '删除计量单位', NULL, NULL, 0, NULL, NULL, 2, 'unit:mis_unit:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 19:43:29', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111607432010294', '2024111607432000290', '批量删除计量单位', NULL, NULL, 0, NULL, NULL, 2, 'unit:mis_unit:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 19:43:29', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111607432010295', '2024111607432000290', '导出excel_计量单位', NULL, NULL, 0, NULL, NULL, 2, 'unit:mis_unit:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 19:43:29', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111607432010296', '2024111607432000290', '导入excel_计量单位', NULL, NULL, 0, NULL, NULL, 2, 'unit:mis_unit:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 19:43:29', NULL, NULL, 0, 0, '1', 0);