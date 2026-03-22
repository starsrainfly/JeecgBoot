-- 注意：该页面对应的前台目录为views/mes文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026031706205890340', NULL, '工序表', '/mes/productionTaskList', 'mes/ProductionTaskList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-03-17 18:20:34', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026031706205920341', '2026031706205890340', '添加工序表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_task:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-17 18:20:34', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026031706205920342', '2026031706205890340', '编辑工序表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_task:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-17 18:20:34', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026031706205920343', '2026031706205890340', '删除工序表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_task:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-17 18:20:34', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026031706205920344', '2026031706205890340', '批量删除工序表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_task:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-17 18:20:34', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026031706205930345', '2026031706205890340', '导出excel_工序表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_task:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-17 18:20:34', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026031706205930346', '2026031706205890340', '导入excel_工序表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_task:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-17 18:20:34', NULL, NULL, 0, 0, '1', 0);