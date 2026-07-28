-- 注意：该页面对应的前台目录为views/scm文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026072805325860060', NULL, '成本核算快照', '/scm/costCalcList', 'scm/CostCalcList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-07-28 17:32:06', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072805325860061', '2026072805325860060', '添加成本核算快照', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_cost_calc:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-28 17:32:06', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072805325860062', '2026072805325860060', '编辑成本核算快照', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_cost_calc:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-28 17:32:06', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072805325860063', '2026072805325860060', '删除成本核算快照', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_cost_calc:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-28 17:32:06', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072805325860064', '2026072805325860060', '批量删除成本核算快照', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_cost_calc:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-28 17:32:06', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072805325860065', '2026072805325860060', '导出excel_成本核算快照', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_cost_calc:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-28 17:32:06', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072805325860066', '2026072805325860060', '导入excel_成本核算快照', NULL, NULL, 0, NULL, NULL, 2, 'scm:mis_cost_calc:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-28 17:32:06', NULL, NULL, 0, 0, '1', 0);