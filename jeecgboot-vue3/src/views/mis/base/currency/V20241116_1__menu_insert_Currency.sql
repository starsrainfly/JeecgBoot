-- 注意：该页面对应的前台目录为views/currency文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2024111608127350200', NULL, '币种表', '/currency/currencyList', 'currency/CurrencyList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2024-11-16 20:12:20', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111608127350201', '2024111608127350200', '添加币种表', NULL, NULL, 0, NULL, NULL, 2, 'currency:mis_currency:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 20:12:20', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111608127350202', '2024111608127350200', '编辑币种表', NULL, NULL, 0, NULL, NULL, 2, 'currency:mis_currency:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 20:12:20', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111608127350203', '2024111608127350200', '删除币种表', NULL, NULL, 0, NULL, NULL, 2, 'currency:mis_currency:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 20:12:20', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111608127350204', '2024111608127350200', '批量删除币种表', NULL, NULL, 0, NULL, NULL, 2, 'currency:mis_currency:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 20:12:20', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111608127350205', '2024111608127350200', '导出excel_币种表', NULL, NULL, 0, NULL, NULL, 2, 'currency:mis_currency:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 20:12:20', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2024111608127350206', '2024111608127350200', '导入excel_币种表', NULL, NULL, 0, NULL, NULL, 2, 'currency:mis_currency:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2024-11-16 20:12:20', NULL, NULL, 0, 0, '1', 0);