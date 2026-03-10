-- 注意：该页面对应的前台目录为views/mdm文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026030310459790040', NULL, '内外包装映射表', '/mdm/packageMappingList', 'mdm/PackageMappingList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-03-03 22:45:04', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030310459790041', '2026030310459790040', '添加内外包装映射表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_package_mapping:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-03 22:45:04', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030310459790042', '2026030310459790040', '编辑内外包装映射表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_package_mapping:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-03 22:45:04', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030310459790043', '2026030310459790040', '删除内外包装映射表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_package_mapping:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-03 22:45:04', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030310459790044', '2026030310459790040', '批量删除内外包装映射表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_package_mapping:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-03 22:45:04', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030310459790045', '2026030310459790040', '导出excel_内外包装映射表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_package_mapping:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-03 22:45:04', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026030310459790046', '2026030310459790040', '导入excel_内外包装映射表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_package_mapping:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-03-03 22:45:04', NULL, NULL, 0, 0, '1', 0);