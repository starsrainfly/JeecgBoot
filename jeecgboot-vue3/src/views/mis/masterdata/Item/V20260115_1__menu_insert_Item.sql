-- 注意：该页面对应的前台目录为views/masterdata文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026011511109770430', NULL, '统一库存项表', '/masterdata/itemList', 'masterdata/ItemList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-01-15 23:10:43', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011511109770431', '2026011511109770430', '添加统一库存项表', NULL, NULL, 0, NULL, NULL, 2, 'masterdata:mis_item:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 23:10:43', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011511109770432', '2026011511109770430', '编辑统一库存项表', NULL, NULL, 0, NULL, NULL, 2, 'masterdata:mis_item:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 23:10:43', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011511109770433', '2026011511109770430', '删除统一库存项表', NULL, NULL, 0, NULL, NULL, 2, 'masterdata:mis_item:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 23:10:43', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011511109770434', '2026011511109770430', '批量删除统一库存项表', NULL, NULL, 0, NULL, NULL, 2, 'masterdata:mis_item:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 23:10:43', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011511109770435', '2026011511109770430', '导出excel_统一库存项表', NULL, NULL, 0, NULL, NULL, 2, 'masterdata:mis_item:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 23:10:43', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011511109770436', '2026011511109770430', '导入excel_统一库存项表', NULL, NULL, 0, NULL, NULL, 2, 'masterdata:mis_item:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 23:10:43', NULL, NULL, 0, 0, '1', 0);