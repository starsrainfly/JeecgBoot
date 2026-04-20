-- 注意：该页面对应的前台目录为views/scm文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026042001297450130', NULL, '销售订单主表', '/scm/salesOrderList', 'scm/SalesOrderList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-04-20 13:29:13', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026042001297450131', '2026042001297450130', '添加销售订单主表', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_sales_order:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-04-20 13:29:13', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026042001297450132', '2026042001297450130', '编辑销售订单主表', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_sales_order:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-04-20 13:29:13', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026042001297450133', '2026042001297450130', '删除销售订单主表', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_sales_order:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-04-20 13:29:13', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026042001297450134', '2026042001297450130', '批量删除销售订单主表', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_sales_order:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-04-20 13:29:13', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026042001297450135', '2026042001297450130', '导出excel_销售订单主表', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_sales_order:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-04-20 13:29:13', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026042001297450136', '2026042001297450130', '导入excel_销售订单主表', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_sales_order:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-04-20 13:29:13', NULL, NULL, 0, 0, '1', 0);