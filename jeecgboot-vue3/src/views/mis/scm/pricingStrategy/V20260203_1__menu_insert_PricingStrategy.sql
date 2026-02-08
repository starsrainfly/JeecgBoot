-- 注意：该页面对应的前台目录为views/scm文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026020308006910250', NULL, '价格策略', '/scm/pricingStrategyList', 'scm/PricingStrategyList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-02-03 20:00:25', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026020308006910251', '2026020308006910250', '添加价格策略', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_pricing_strategy:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 20:00:25', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026020308006910252', '2026020308006910250', '编辑价格策略', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_pricing_strategy:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 20:00:25', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026020308006910253', '2026020308006910250', '删除价格策略', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_pricing_strategy:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 20:00:25', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026020308006910254', '2026020308006910250', '批量删除价格策略', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_pricing_strategy:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 20:00:25', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026020308006910255', '2026020308006910250', '导出excel_价格策略', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_pricing_strategy:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 20:00:25', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026020308006910256', '2026020308006910250', '导入excel_价格策略', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_pricing_strategy:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-02-03 20:00:25', NULL, NULL, 0, 0, '1', 0);