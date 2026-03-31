-- 注意：该页面对应的前台目录为views/warehouse文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026011501594960500', NULL, '仓库信息', '/warehouse/warehouseList', 'warehouse/WarehouseList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-01-15 13:59:50', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011501594960501', '2026011501594960500', '添加仓库信息', NULL, NULL, 0, NULL, NULL, 2, 'warehouse:mis_warehouse:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 13:59:50', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011501594960502', '2026011501594960500', '编辑仓库信息', NULL, NULL, 0, NULL, NULL, 2, 'warehouse:mis_warehouse:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 13:59:50', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011501594960503', '2026011501594960500', '删除仓库信息', NULL, NULL, 0, NULL, NULL, 2, 'warehouse:mis_warehouse:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 13:59:50', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011501594960504', '2026011501594960500', '批量删除仓库信息', NULL, NULL, 0, NULL, NULL, 2, 'warehouse:mis_warehouse:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 13:59:50', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011501594960505', '2026011501594960500', '导出excel_仓库信息', NULL, NULL, 0, NULL, NULL, 2, 'warehouse:mis_warehouse:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 13:59:50', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026011501594960506', '2026011501594960500', '导入excel_仓库信息', NULL, NULL, 0, NULL, NULL, 2, 'warehouse:mis_warehouse:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-01-15 13:59:50', NULL, NULL, 0, 0, '1', 0);