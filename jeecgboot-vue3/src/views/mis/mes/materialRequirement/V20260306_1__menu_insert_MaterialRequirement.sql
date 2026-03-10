-- 注意：该页面对应的前台目录为views/mes文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026030609032950590', NULL, '物料需求表', '/mes/materialRequirementList', 'mes/MaterialRequirementList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-03-06 21:03:59', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030609032960591', '2026030609032950590', '添加物料需求表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_material_requirement:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-06 21:03:59', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030609032960592', '2026030609032950590', '编辑物料需求表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_material_requirement:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-06 21:03:59', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030609032960593', '2026030609032950590', '删除物料需求表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_material_requirement:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-06 21:03:59', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030609032960594', '2026030609032950590', '批量删除物料需求表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_material_requirement:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-06 21:03:59', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030609032960595', '2026030609032950590', '导出excel_物料需求表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_material_requirement:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-06 21:03:59', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030609032960596', '2026030609032950590', '导入excel_物料需求表', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_material_requirement:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-06 21:03:59', NULL, NULL, 0, 0, '1', 0);