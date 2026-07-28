-- 注意：该页面对应的前台目录为views/mes文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2026072405357630050', NULL, '质检记录', '/mes/qcRecordList', 'mes/QcRecordList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2026-07-24 17:35:05', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072405357630051', '2026072405357630050', '添加质检记录', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_qc_record:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 17:35:05', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072405357630052', '2026072405357630050', '编辑质检记录', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_qc_record:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 17:35:05', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072405357630053', '2026072405357630050', '删除质检记录', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_qc_record:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 17:35:05', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072405357630054', '2026072405357630050', '批量删除质检记录', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_qc_record:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 17:35:05', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072405357630055', '2026072405357630050', '导出excel_质检记录', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_qc_record:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 17:35:05', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2026072405357630056', '2026072405357630050', '导入excel_质检记录', NULL, NULL, 0, NULL, NULL, 2, 'mes:mis_qc_record:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2026-07-24 17:35:05', NULL, NULL, 0, 0, '1', 0);