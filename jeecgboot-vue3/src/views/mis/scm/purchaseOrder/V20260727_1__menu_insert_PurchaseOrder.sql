-- 注意：该页面对应的前台目录为views/scm文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('202607270733550200', NULL, '采购订单', '/scm/purchaseOrderList', 'scm/PurchaseOrderList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-07-27 19:33:20', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202607270733550201', '202607270733550200', '添加采购订单', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_purchase_order:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-27 19:33:20', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202607270733550202', '202607270733550200', '编辑采购订单', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_purchase_order:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-27 19:33:20', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202607270733550203', '202607270733550200', '删除采购订单', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_purchase_order:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-27 19:33:20', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202607270733550204', '202607270733550200', '批量删除采购订单', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_purchase_order:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-27 19:33:20', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202607270733550205', '202607270733550200', '导出excel_采购订单', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_purchase_order:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-27 19:33:20', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('202607270733550206', '202607270733550200', '导入excel_采购订单', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_purchase_order:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-27 19:33:20', NULL, NULL, 0, 0, '1', 0);