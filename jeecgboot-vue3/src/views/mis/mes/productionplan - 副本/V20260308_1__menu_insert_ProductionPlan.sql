-- 注意：该页面对应的前台目录为views/mes文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026030805223410040', NULL, '生产计划', '/mes/productionPlanList', 'mes/ProductionPlanList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-03-08 17:22:04', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030805223410041', '2026030805223410040', '添加生产计划', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_plan:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-08 17:22:04', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030805223410042', '2026030805223410040', '编辑生产计划', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_plan:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-08 17:22:04', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030805223410043', '2026030805223410040', '删除生产计划', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_plan:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-08 17:22:04', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030805223410044', '2026030805223410040', '批量删除生产计划', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_plan:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-08 17:22:04', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030805223410045', '2026030805223410040', '导出excel_生产计划', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_plan:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-08 17:22:04', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030805223410046', '2026030805223410040', '导入excel_生产计划', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_production_plan:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-08 17:22:04', NULL, NULL, 0, 0, '1', 0);