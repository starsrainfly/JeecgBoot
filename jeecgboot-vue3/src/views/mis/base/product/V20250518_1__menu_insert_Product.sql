-- 注意：该页面对应的前台目录为views/product文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2025051801556320090', NULL, '产品信息', '/product/productList', 'product/ProductList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2025-05-18 13:55:09', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051801556320091', '2025051801556320090', '添加产品信息', NULL, NULL, 0, NULL, NULL, 2, 'product:mis_product:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 13:55:09', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051801556320092', '2025051801556320090', '编辑产品信息', NULL, NULL, 0, NULL, NULL, 2, 'product:mis_product:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 13:55:09', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051801556320093', '2025051801556320090', '删除产品信息', NULL, NULL, 0, NULL, NULL, 2, 'product:mis_product:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 13:55:09', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051801556330094', '2025051801556320090', '批量删除产品信息', NULL, NULL, 0, NULL, NULL, 2, 'product:mis_product:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 13:55:09', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051801556330095', '2025051801556320090', '导出excel_产品信息', NULL, NULL, 0, NULL, NULL, 2, 'product:mis_product:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 13:55:09', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051801556330096', '2025051801556320090', '导入excel_产品信息', NULL, NULL, 0, NULL, NULL, 2, 'product:mis_product:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 13:55:09', NULL, NULL, 0, 0, '1', 0);