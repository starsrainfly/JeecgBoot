-- 注意：该页面对应的前台目录为views/mdm文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('202602031146460370', NULL, '物料表', '/mdm/materialList', 'mdm/MaterialList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-02-03 11:46:37', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202602031146460371', '202602031146460370', '添加物料表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_material:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 11:46:37', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202602031146460372', '202602031146460370', '编辑物料表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_material:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 11:46:37', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202602031146460373', '202602031146460370', '删除物料表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_material:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 11:46:37', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202602031146460374', '202602031146460370', '批量删除物料表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_material:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 11:46:37', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202602031146460375', '202602031146460370', '导出excel_物料表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_material:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 11:46:37', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202602031146460376', '202602031146460370', '导入excel_物料表', NULL, NULL, 0, NULL, NULL, 2, 'mdm:mis_material:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 11:46:37', NULL, NULL, 0, 0, '1', 0);