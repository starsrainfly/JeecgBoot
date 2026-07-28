/*
工单表添加派工时间及派工人字段
*/
-- ===== 1. 工单表加字段 =====
ALTER TABLE `mis_production_task`
    ADD COLUMN `assign_time` datetime DEFAULT NULL COMMENT '派工时间' AFTER `assigned_operator_name`,
    ADD COLUMN `assign_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '派工人' AFTER `assign_time`;


-- ===== 2. 质检记录主表 =====
CREATE TABLE `mis_qc_record` (
                                 `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
                                 `qc_task_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质检工单id',
                                 `source_task_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '来源工单id',
                                 `batch_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '批次id',
                                 `batch_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '批次号',
                                 `order_no` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '生产订单号',
                                 `product_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品id',
                                 `product_code` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品编号',
                                 `product_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '产品名称',
                                 `qc_result` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质检结果(pass合格/fail不合格/rework返工)',
                                 `qc_conclusion` varchar(500) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '质检结论',
                                 `inspector_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检验员id',
                                 `inspector_name` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检验员',
                                 `inspect_time` datetime DEFAULT NULL COMMENT '检验时间',
                                 `remark` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                 `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                 `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                 `update_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '更新人',
                                 `update_time` datetime DEFAULT NULL COMMENT '更新日期',
                                 `sys_org_code` varchar(64) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '所属部门',
                                 PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='质检记录表';

-- ===== 3. 质检记录明细表（含检测设备三字段）=====
CREATE TABLE `mis_qc_record_detail` (
                                        `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
                                        `record_id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '质检记录id',
                                        `item_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检验项目',
                                        `standard` varchar(200) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '标准要求',
                                        `actual_value` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '实测值',
                                        `item_result` varchar(32) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单项结果(pass合格/fail不合格)',
                                        `equipment_id` varchar(36) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检测设备id',
                                        `equipment_code` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检测设备编码',
                                        `equipment_name` varchar(100) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '检测设备名称',
                                        `sort_no` int(10) DEFAULT NULL COMMENT '排序号',
                                        `remark` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                        `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                        `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                        PRIMARY KEY (`id`),
                                        KEY `idx_record_id` (`record_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='质检记录明细表';

-- ===== 4. 检验项配置表 =====
CREATE TABLE `mis_qc_item_config` (
                                      `id` varchar(36) COLLATE utf8mb4_unicode_ci NOT NULL,
                                      `field_name` varchar(50) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT 'recipe_spec字段名(驼峰)',
                                      `item_name` varchar(100) COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '检验项目名称',
                                      `unit` varchar(30) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '单位',
                                      `enabled` varchar(1) COLLATE utf8mb4_unicode_ci DEFAULT '1' COMMENT '是否默认带出(1是/0否)',
                                      `sort_no` int(10) DEFAULT NULL COMMENT '排序号',
                                      `remark` varchar(300) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '备注',
                                      `create_by` varchar(50) COLLATE utf8mb4_unicode_ci DEFAULT NULL COMMENT '创建人',
                                      `create_time` datetime DEFAULT NULL COMMENT '创建日期',
                                      PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='质检检验项配置表';
