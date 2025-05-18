-- 注意：该页面对应的前台目录为views/Recipe文件夹下
-- 如果你想更改到其他目录，请修改sql中component字段对应的值


INSERT INTO sys_permission(id, parent_id, name, url, component, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_route, is_leaf, keep_alive, hidden, hide_tab, description, status, del_flag, rule_flag, create_by, create_time, update_by, update_time, internal_or_external) 
VALUES ('2025051803105870100', NULL, '配方表', '/Recipe/recipeList', 'Recipe/RecipeList', NULL, NULL, 0, NULL, '1', 0.00, 0, NULL, 1, 0, 0, 0, 0, NULL, '1', 0, 0, 'admin', '2025-05-18 15:10:10', NULL, NULL, 0);

-- 权限控制sql
-- 新增
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051803105870101', '2025051803105870100', '添加配方表', NULL, NULL, 0, NULL, NULL, 2, 'Recipe:mis_recipe:add', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 15:10:10', NULL, NULL, 0, 0, '1', 0);
-- 编辑
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051803105870102', '2025051803105870100', '编辑配方表', NULL, NULL, 0, NULL, NULL, 2, 'Recipe:mis_recipe:edit', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 15:10:10', NULL, NULL, 0, 0, '1', 0);
-- 删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051803105870103', '2025051803105870100', '删除配方表', NULL, NULL, 0, NULL, NULL, 2, 'Recipe:mis_recipe:delete', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 15:10:10', NULL, NULL, 0, 0, '1', 0);
-- 批量删除
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051803105880104', '2025051803105870100', '批量删除配方表', NULL, NULL, 0, NULL, NULL, 2, 'Recipe:mis_recipe:deleteBatch', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 15:10:10', NULL, NULL, 0, 0, '1', 0);
-- 导出excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051803105880105', '2025051803105870100', '导出excel_配方表', NULL, NULL, 0, NULL, NULL, 2, 'Recipe:mis_recipe:exportXls', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 15:10:10', NULL, NULL, 0, 0, '1', 0);
-- 导入excel
INSERT INTO sys_permission(id, parent_id, name, url, component, is_route, component_name, redirect, menu_type, perms, perms_type, sort_no, always_show, icon, is_leaf, keep_alive, hidden, hide_tab, description, create_by, create_time, update_by, update_time, del_flag, rule_flag, status, internal_or_external)
VALUES ('2025051803105880106', '2025051803105870100', '导入excel_配方表', NULL, NULL, 0, NULL, NULL, 2, 'Recipe:mis_recipe:importExcel', '1', NULL, 0, NULL, 1, 0, 0, 0, NULL, 'admin', '2025-05-18 15:10:10', NULL, NULL, 0, 0, '1', 0);