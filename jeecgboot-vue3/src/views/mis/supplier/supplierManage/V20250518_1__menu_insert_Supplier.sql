-- 注意：该页面对应的前台目录为views/supplier文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2025051804481350270', NULL, '供应商表', '/supplier/supplierList', 'supplier/SupplierList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2025-05-18 16:48:27', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051804481350271', '2025051804481350270', '添加供应商表', NULL, NULL, 0, NULL, NULL, 2, 'supplier:mis_supplier:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 16:48:27', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051804481350272', '2025051804481350270', '编辑供应商表', NULL, NULL, 0, NULL, NULL, 2, 'supplier:mis_supplier:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 16:48:27', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051804481350273', '2025051804481350270', '删除供应商表', NULL, NULL, 0, NULL, NULL, 2, 'supplier:mis_supplier:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 16:48:27', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051804481350274', '2025051804481350270', '批量删除供应商表', NULL, NULL, 0, NULL, NULL, 2, 'supplier:mis_supplier:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 16:48:27', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051804481350275', '2025051804481350270', '导出excel_供应商表', NULL, NULL, 0, NULL, NULL, 2, 'supplier:mis_supplier:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 16:48:27', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051804481350276', '2025051804481350270', '导入excel_供应商表', NULL, NULL, 0, NULL, NULL, 2, 'supplier:mis_supplier:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 16:48:27', NULL, NULL, 0, 0, '1', 0);